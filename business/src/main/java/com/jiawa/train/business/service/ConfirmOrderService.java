package com.jiawa.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.business.domain.DailyTrainTicket;
import com.jiawa.train.business.enums.ConfirmOrderStatusEnum;
import com.jiawa.train.business.enums.SeatTypeEnum;
import com.jiawa.train.business.req.ConfirmOrderTicketReq;
import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.business.domain.ConfirmOrder;
import com.jiawa.train.business.domain.ConfirmOrderExample;
import com.jiawa.train.business.mapper.ConfirmOrderMapper;
import com.jiawa.train.business.req.ConfirmOrderQueryReq;
import com.jiawa.train.business.req.ConfirmOrderDoReq;
import com.jiawa.train.business.resp.ConfirmOrderQueryResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    public void doConfirm(ConfirmOrderDoReq confirmOrderDoReq) throws Exception {
        // 省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过

        // 保存确认订单表，状态初始
        Date date = confirmOrderDoReq.getDate();
        String trainCode = confirmOrderDoReq.getTrainCode();
        String start = confirmOrderDoReq.getStart();
        String end = confirmOrderDoReq.getEnd();
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
        confirmOrder.setTickets(JSON.toJSONString(confirmOrderDoReq.getTickets()));
        confirmOrderMapper.insert(confirmOrder);
        // 查出余票记录，需要得到真实的库存
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        LOG.info("余票信息：{}", dailyTrainTicket);
        // 扣减余票数量，并判断余票是否足够
        for (ConfirmOrderTicketReq ticketReq: confirmOrderDoReq.getTickets()) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            //用反射写写看?
            reduceTicketsReflect(seatTypeEnum, dailyTrainTicket);
            //switch方法
            //reduceTickets(seatTypeEnum, dailyTrainTicket);
        }
        // 选座

        // 一个车箱一个车箱的获取座位数据

        // 挑选符合条件的座位，如果这个车箱不满足，则进入下个车箱（多个选座应该在同一个车厢）

        // 选中座位后事务处理：

        // 座位表修改售卖情况sell；
        // 余票详情表修改余票；
        // 为会员增加购票记录
        // 更新确认订单为成功
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
    private static void reduceTickets(SeatTypeEnum seatTypeEnum, DailyTrainTicket dailyTrainTicket) {
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
