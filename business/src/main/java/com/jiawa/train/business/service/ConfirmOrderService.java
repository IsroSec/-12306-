package com.jiawa.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.business.domain.*;
import com.jiawa.train.business.enums.ConfirmOrderStatusEnum;
import com.jiawa.train.business.enums.RedisKeyPreEnum;
import com.jiawa.train.business.enums.SeatColEnum;
import com.jiawa.train.business.enums.SeatTypeEnum;
import com.jiawa.train.business.req.ConfirmOrderTicketReq;
import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.business.mapper.ConfirmOrderMapper;
import com.jiawa.train.business.req.ConfirmOrderQueryReq;
import com.jiawa.train.business.req.ConfirmOrderDoReq;
import com.jiawa.train.business.resp.ConfirmOrderQueryResp;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: ConfirmOrderService
 * Package: com.jiawa.train.business.service
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:27
 * @Version 1.0
 */
@Service
public class ConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Autowired
    private ConfirmOrderMapper confirmOrderMapper;
    @Autowired
    private DailyTrainTicketService dailyTrainTicketService;
    @Autowired
    private DailyTrainCarriageService dailyTrainCarriageService;
    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;
    @Autowired
    private AfterConfirmOrderService afterConfirmOrderService;
    @Autowired
//    private RedissonClient redissonClient;
    private RedisTemplate redisTemplate;
    @Autowired
    private SkTokenService skTokenService;
    public void save(ConfirmOrderDoReq confirmOrderDoReq) {
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(confirmOrderDoReq, ConfirmOrder.class);
        DateTime now = DateTime.now();
        if(ObjectUtil.isNull(confirmOrder.getId())) {
//设置一下memberId
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        }else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);
        }
    }

    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq confirmOrderQueryReq) {
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        confirmOrderExample.setOrderByClause("id desc");
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();
    //这里构造一下查询条件

        LOG.info("查询页码：{}", confirmOrderQueryReq.getPage());
        LOG.info("每页条数：{}", confirmOrderQueryReq.getSize());
        PageHelper.startPage(confirmOrderQueryReq.getPage(), confirmOrderQueryReq.getSize());
        List<ConfirmOrder> confirmOrders = confirmOrderMapper.selectByExample(confirmOrderExample);
        PageInfo<ConfirmOrder> confirmOrderPageInfo = new PageInfo<>(confirmOrders);

        LOG.info("总行数：{}", confirmOrderPageInfo.getTotal());
        LOG.info("总页数：{}", confirmOrderPageInfo.getPages());
        List<ConfirmOrderQueryResp> list = BeanUtil.copyToList(confirmOrders, ConfirmOrderQueryResp.class);
        PageResp<ConfirmOrderQueryResp> confirmOrderQueryRespPageResp = new PageResp<>();
        confirmOrderQueryRespPageResp.setTotal(confirmOrderPageInfo.getTotal());
        confirmOrderQueryRespPageResp.setList(list);
        return confirmOrderQueryRespPageResp;
    }
    public void delete(Long id) {
        confirmOrderMapper.deleteByPrimaryKey(id);
    }

    @SentinelResource(value = "doConfirm", blockHandler = "doConfirmBlock")
    public void doConfirm(ConfirmOrderDoReq confirmOrderDoReq)  {
        //校验令牌余量
        boolean validSkToken=skTokenService.validSkToken(confirmOrderDoReq.getTrainCode(),confirmOrderDoReq.getDate(),confirmOrderDoReq.getMemberId());
        if (validSkToken){
            LOG.info("令牌校验通过");
        }else {
            LOG.info("令牌校验不通过");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_FAIL);
        }
        // 省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过
        String localKey= RedisKeyPreEnum.CONFIRM_ORDER +"-"+DateUtil.formatDate(confirmOrderDoReq.getDate())+"-"+confirmOrderDoReq.getTrainCode();
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(localKey, localKey, 5, TimeUnit.SECONDS);
        if (aBoolean){
            LOG.info("加锁成功:{}",localKey);
        }else {
            LOG.info("加锁失败:{}",localKey);
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION_FAIL);
        }
//        RLock lock=null;
        try {
//            lock = redissonClient.getLock(localKey);
//            /**
//             * waitTime-the maximum time to acquire the lock 等待获取锁时间(最大尝试获得锁的时间)，超时返回false
//             * LeaseTime-leasetime锁时长，即n秒后自动释放锁
//             * time unit-time unit 时间单位
//             */
//            boolean tryLock = lock.tryLock(0, TimeUnit.SECONDS);//看门狗
////        boolean tryLock = lock.tryLock(30,10, TimeUnit.SECONDS);看门狗
//            if (tryLock){
//                LOG.info("加锁成功:{}",localKey);
//            }else {
//                LOG.info("加锁失败:{}",localKey);
//                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION_FAIL);
//            }
            // 保存确认订单表，状态初始
            Date date = confirmOrderDoReq.getDate();
            String trainCode = confirmOrderDoReq.getTrainCode();
            String start = confirmOrderDoReq.getStart();
            String end = confirmOrderDoReq.getEnd();
            List<ConfirmOrderTicketReq> tickets = confirmOrderDoReq.getTickets();
            DateTime now = DateTime.now();
            ConfirmOrder confirmOrder = new ConfirmOrder();
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrder.setMemberId(LoginMemberContext.getId());
            confirmOrder.setDate(date);
            confirmOrder.setTrainCode(trainCode);
            confirmOrder.setStart(start);
            confirmOrder.setEnd(end);
            confirmOrder.setDailyTrainTicketId(confirmOrderDoReq.getDailyTrainTicketId());
            confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
            confirmOrder.setTickets(JSON.toJSONString(tickets));
            confirmOrderMapper.insert(confirmOrder);
            // 查出余票记录，需要得到真实的库存
            DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
            LOG.info("余票信息：{}", dailyTrainTicket);
            // 扣减余票数量，并判断余票是否足够
            reduceTickets(tickets, dailyTrainTicket);

            // 存放最终选座结果
            List<DailyTrainSeat>finalSeatList=new ArrayList<>();
            //计算相对第一个位置的偏移值
            //比如选择的C1，C2,则偏移值是：【0，5】
            //比如选择的A1B1C1，则偏移值是：[0,1,2]
            ConfirmOrderTicketReq ticketReq0 = tickets.get(0);
            if (StrUtil.isNotBlank(ticketReq0.getSeat())){
                LOG.info("本次购票选座");
                // 获取座位类型，判断所选座位的偏移值 A C D F或A B C D F
                List<SeatColEnum> colsByType = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());
                //组建一个偏移值数组
                //{
                //       A1: false, C1: true，D1: false, F1: false，
                //       A2: false, C2: false，D2: true, F2: false
                //}
                ArrayList<String> referSeatList = new ArrayList<>();
                for (int i=1;i<=2;i++){
                    for (SeatColEnum seatColEnum: colsByType){
                        referSeatList.add(seatColEnum.getCode()+i);
                    }
                }
                LOG.info("座位：{}", referSeatList);
                //找偏移值referSeatList={A1,C1,D1,F1,A2,C2,D2,F2}绝对偏移值减去第一位绝对偏移值等于相对偏移值
                //获取绝对偏移值
                ArrayList<Integer> absoluteOffsetList = new ArrayList<>();
                for (ConfirmOrderTicketReq ticket : tickets) {
                    int index = referSeatList.indexOf(ticket.getSeat());
                    absoluteOffsetList.add(index);
                }
                LOG.info("绝对偏移值：{}", absoluteOffsetList);
                ArrayList<Integer> offsetList = new ArrayList<>();
                for (Integer index : absoluteOffsetList) {
                    //相对第一个座位偏移值
                    int offset = index - absoluteOffsetList.get(0);
                    offsetList.add(offset);
                }
                LOG.info("相对偏移值：{}", offsetList);
                // 选座
                getSeat(finalSeatList,
                        trainCode,
                        date,
                        ticketReq0.getSeatTypeCode(),
                        ticketReq0.getSeat().split("")[0],
                        offsetList,
                        dailyTrainTicket.getStartIndex(),
                        dailyTrainTicket.getEndIndex());
            }else {
                LOG.info("本次购票不选座");
                // 选座
                for (ConfirmOrderTicketReq ticket : tickets){
                    getSeat(finalSeatList,trainCode, date, ticket.getSeatTypeCode(), null, null,
                            dailyTrainTicket.getStartIndex(),
                            dailyTrainTicket.getEndIndex());
                }
            }
            LOG.info("最终选座结果：{}", finalSeatList);
            try {
                afterConfirmOrderService.AfterDoConfirm(dailyTrainTicket,finalSeatList,tickets,confirmOrder);
            } catch (Exception e) {
                LOG.error("保存购票信息失败",e);
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION);
            }
//        redisTemplate.delete(localKey);
        }  finally {
            LOG.info("释放分布式锁：{}", localKey);
            redisTemplate.delete(localKey);
//            if (null != lock && lock.isHeldByCurrentThread()){
//                lock.unlock();
//            }
        }


        // 挑选符合条件的座位，如果这个车箱不满足，则进入下个车箱（多个选座应该在同一个车厢）

        // 选中座位后事务处理：

        // 座位表修改售卖情况sell；
        // 余票详情表修改余票；
        // 为会员增加购票记录
        // 更新确认订单为成功
    }

    /**
     * 降级方法，需包含限流方法的所有参数和blockException参数
     * @param confirmOrderDoReq
     * @param e
     */
    private void doConfirmBlock(ConfirmOrderDoReq confirmOrderDoReq, BlockException e){
        LOG.info("限流了");
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }

    /**
     * 计算座位在区间内是否可卖
     * 例子：一共三个站，012，那么本次的购买区间就是sell=00
     * 假设本次要买第1-2站就是sell=11
     *
     * 选中后，要计算购票后的sell，比如原来是00，本次购买是11
     * 原来的00和现在的11进行按位或计算，得到新的sell=11
     */
    private boolean callSell(DailyTrainSeat dailyTrainSeats,Integer startIndex,Integer endIndex){
        //刚开始什么都没卖00，假设卖区间1-2站的票，那么就是01
        String sell = dailyTrainSeats.getSell();
        //取出售卖区间的sellpart截出了第二个0
        String sellPart = sell.substring(startIndex, endIndex);
        if (Integer.parseInt(sellPart)>0){
            LOG.info("座位{}在本次车站区间{}~{}已售过票", dailyTrainSeats.getCarriageSeatIndex(),startIndex,endIndex);
            return false;
        }else {
            LOG.info("座位{}在本次车站区间{}~{}可以卖票", dailyTrainSeats.getCarriageSeatIndex(),startIndex,endIndex);
            String curSell = sellPart.replace('0', '1');
            //curlsell是1，应该放在01，前面有多少0主要是看endIndex最终站的长度
            //比如四个站 0   0   0   0   0
            //        0-1 1-2 2-3 3-4 4-5
            //只买2-3站那么endIndex就是2，cursell也只有一个1，那么补零就需要往前面补endIndex就是两个
            curSell= StrUtil.fillBefore(curSell, '0', endIndex);
            //往后补
            curSell=StrUtil.fillAfter(curSell,'0',sell.length());
            //做按位或计算
            //00000 | 00100=00100=4
            int newSellInt = NumberUtil.binaryToInt(curSell) | NumberUtil.binaryToInt(sell);
            //4=0100
            String newSell = NumberUtil.getBinaryStr(newSellInt);
            newSell = StrUtil.fillBefore(newSell, '0', sell.length());
            LOG.info("座位{}被选中，原首票信息：{}，车站区间：{}~{}，即{}，最终售票信息为：{}",dailyTrainSeats.getCarriageSeatIndex(),sell, startIndex, endIndex, curSell, newSell);
            dailyTrainSeats.setSell(newSell);
            return true;
        }
    }
    private void getSeat(List<DailyTrainSeat>finalSeatList,String trainCode, Date date,String seatType,String column,List<Integer> offsetList,Integer startIndex,Integer endIndex){
        List<DailyTrainSeat> getSeatList=new ArrayList<>();
        // 一个车箱一个车箱的获取座位数据
        List<DailyTrainCarriage> dailyTrainCarriages = dailyTrainCarriageService.selectBySeatType(trainCode, date, seatType);
        LOG.info("车箱一共：{}", dailyTrainCarriages.size());
        for (DailyTrainCarriage dailyTrainCarriage : dailyTrainCarriages) {
            LOG.info("开始选车箱：{}", dailyTrainCarriage);
            List<DailyTrainSeat> dailyTrainSeats = dailyTrainSeatService.selectByCarriage(trainCode, dailyTrainCarriage.getIndex(),date);
            LOG.info("车厢{}座位一共：{}", dailyTrainCarriage.getIndex(), dailyTrainSeats.size());
            for (DailyTrainSeat dailyTrainSeat : dailyTrainSeats) {
                //如果有选座的话判断是否和选择的列名相等
                String col = dailyTrainSeat.getCol();
                getSeatList=new ArrayList<>();
                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();
                boolean alreadyChooseFlag=false;
                for (DailyTrainSeat trainSeat : finalSeatList) {
                    if (trainSeat.getId().equals(dailyTrainSeat.getId())){
                        alreadyChooseFlag=true;
                        break;
                    }
                }
                if (alreadyChooseFlag){
                    LOG.info("座位{}已被选中，继续判断下一个座位", seatIndex);
                    continue;
                }
                if (StrUtil.isBlank(column)){
                    LOG.info("无选座");
                }else {
                    if(!column.equals(col)){
                        LOG.info("座位{}列值不对，继续判断下一个座位，当前列值为{},目标列值为{}", seatIndex, col, column);
                        continue;
                    }
                }

                boolean isChooseSell = callSell(dailyTrainSeat, startIndex, endIndex);
                if (isChooseSell){
                    LOG.info("座位被选中");
                    getSeatList.add(dailyTrainSeat);
                }else {
                    LOG.info("座位没被选中");
                    continue;
                }
                //根据offset选剩下的座位
                boolean isGetAllOffsetSeat=true;
                if (CollUtil.isNotEmpty(offsetList)){
                    LOG.info("座位偏移列表：{}", offsetList);
                    for (int i= 1;i<offsetList.size();i++) {
                        Integer offset = offsetList.get(i);
                        int nextIndex = seatIndex + offset -1;
                        //选座要选同一车厢
                        if (nextIndex>dailyTrainSeats.size()){
                            LOG.info("座位{}不可选，偏移后的索引值超出了这一个车厢的座位",nextIndex);
                            isGetAllOffsetSeat=false;
                            break;
                        }
                        DailyTrainSeat nextDailyTrainSeat = dailyTrainSeats.get(nextIndex);
                        boolean isChooseNext = callSell(nextDailyTrainSeat, startIndex, endIndex);
                        if (isChooseNext){
                            LOG.info("座位{}被选中", nextDailyTrainSeat.getCarriageSeatIndex());
                            getSeatList.add(nextDailyTrainSeat);
                        }else {
                            LOG.info("座位{}不可选", nextDailyTrainSeat.getCarriageSeatIndex());
                            isGetAllOffsetSeat=false;
                            break;
                        }
                    }
                }
                if (!isGetAllOffsetSeat){
                    getSeatList=new ArrayList<>();
                    continue;
                }
                //保存选好的座位
                finalSeatList.addAll(getSeatList);
                return;
            }
        }
    }
    private static void reduceTickets(List<ConfirmOrderTicketReq> tickets, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticketReq: tickets) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            //用反射写写看?
//            reduceTicketsReflect(seatTypeEnum, dailyTrainTicket);
            //switch方法
            switch (seatTypeEnum){
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft < 0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft < 0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countLeft);

                }case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft < 0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countLeft);

                }case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft < 0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countLeft);
                }
            }

        }
    }

    public  void reduceTicketsReflect(SeatTypeEnum seatTypeEnum, DailyTrainTicket dailyTrainTicket) throws Exception {

            // 获取 DailyTrainTicket 类的 Class 对象
            Class<?> dailyTrainTicketClass = dailyTrainTicket.getClass();

            // 构建方法名
            String methodName = "get" + seatTypeEnum.name().replace("Z","z").replace("D","d");
            Method getMethod = dailyTrainTicketClass.getMethod(methodName);

            // 调用获取票数的方法
            Integer countLeft = (Integer) getMethod.invoke(dailyTrainTicket) - 1;

            // 检查票数是否小于0
            if (countLeft < 0) {
                LOG.info("余票不足");
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
            }

            // 构建设置票数的方法名
            String setMethodName = "set" + seatTypeEnum.name().replace("Z","z").replace("D","d");
            Method setMethod = dailyTrainTicketClass.getMethod(setMethodName, Integer.class);

            // 调用设置票数的方法
            setMethod.invoke(dailyTrainTicket, countLeft);

    }

}
