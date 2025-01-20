package com.jiawa.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.business.domain.DailyTrain;
import com.jiawa.train.business.domain.TrainStation;
import com.jiawa.train.business.enums.SeatTypeEnum;
import com.jiawa.train.business.enums.TrainTypeEnum;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.business.domain.DailyTrainTicket;
import com.jiawa.train.business.domain.DailyTrainTicketExample;
import com.jiawa.train.business.mapper.DailyTrainTicketMapper;
import com.jiawa.train.business.req.DailyTrainTicketQueryReq;
import com.jiawa.train.business.req.DailyTrainTicketSaveReq;
import com.jiawa.train.business.resp.DailyTrainTicketQueryResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * ClassName: DailyTrainTicketService
 * Package: com.jiawa.train.business.service
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:27
 * @Version 1.0
 */
@Service
public class DailyTrainTicketService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainTicketService.class);

    @Autowired
    private DailyTrainTicketMapper dailyTrainTicketMapper;
    @Autowired
    private TrainStationService trainStationService;
    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;
    public void save(DailyTrainTicketSaveReq dailyTrainTicketSaveReq) {
        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(dailyTrainTicketSaveReq, DailyTrainTicket.class);
        DateTime now = DateTime.now();
        if(ObjectUtil.isNull(dailyTrainTicket.getId())) {
//设置一下memberId
            dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.insert(dailyTrainTicket);
        }else {
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.updateByPrimaryKey(dailyTrainTicket);
        }
    }

    @CachePut(value = "DailyTrainTicketService.queryList")
    public PageResp<DailyTrainTicketQueryResp> queryList3(DailyTrainTicketQueryReq dailyTrainTicketQueryReq){
        LOG.info("测试缓存穿透");
        return null;
    }
    @CachePut(value = "DailyTrainTicketService.queryList")
    public PageResp<DailyTrainTicketQueryResp> queryList2(DailyTrainTicketQueryReq dailyTrainTicketQueryReq){
        return queryList(dailyTrainTicketQueryReq);
    }
    @Cacheable(value = "DailyTrainTicketService.queryList")
    public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq dailyTrainTicketQueryReq) {
        //分布式锁防止缓存穿透，防止高并发的缓存击穿
//        if (有数据){
//            null[]
//            return
//        }else {
//            去数据库查数据
//        }
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        dailyTrainTicketExample.setOrderByClause("id desc");
        DailyTrainTicketExample.Criteria criteria = dailyTrainTicketExample.createCriteria();
    //这里构造一下查询条件
        if (ObjUtil.isNotNull(dailyTrainTicketQueryReq.getDate())) {
            criteria.andDateEqualTo(dailyTrainTicketQueryReq.getDate());
        }
        if (ObjUtil.isNotEmpty(dailyTrainTicketQueryReq.getTrainCode())) {
            criteria.andTrainCodeEqualTo(dailyTrainTicketQueryReq.getTrainCode());
        }
        if (ObjUtil.isNotEmpty(dailyTrainTicketQueryReq.getStart())) {
            criteria.andStartEqualTo(dailyTrainTicketQueryReq.getStart());
        }
        if (ObjUtil.isNotEmpty(dailyTrainTicketQueryReq.getEnd())) {
            criteria.andEndEqualTo(dailyTrainTicketQueryReq.getEnd());
        }
        LOG.info("查询页码：{}", dailyTrainTicketQueryReq.getPage());
        LOG.info("每页条数：{}", dailyTrainTicketQueryReq.getSize());
        PageHelper.startPage(dailyTrainTicketQueryReq.getPage(), dailyTrainTicketQueryReq.getSize());
        List<DailyTrainTicket> dailyTrainTickets = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);
        PageInfo<DailyTrainTicket> dailyTrainTicketPageInfo = new PageInfo<>(dailyTrainTickets);

        LOG.info("总行数：{}", dailyTrainTicketPageInfo.getTotal());
        LOG.info("总页数：{}", dailyTrainTicketPageInfo.getPages());
        List<DailyTrainTicketQueryResp> list = BeanUtil.copyToList(dailyTrainTickets, DailyTrainTicketQueryResp.class);
        PageResp<DailyTrainTicketQueryResp> dailyTrainTicketQueryRespPageResp = new PageResp<>();
        dailyTrainTicketQueryRespPageResp.setTotal(dailyTrainTicketPageInfo.getTotal());
        dailyTrainTicketQueryRespPageResp.setList(list);
        return dailyTrainTicketQueryRespPageResp;
    }
    public void delete(Long id) {
        dailyTrainTicketMapper.deleteByPrimaryKey(id);
    }

    public void genDaily(DailyTrain dailyTrain,Date date, String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的余票信息开始", DateUtil.formatDate(date), trainCode);

        // 删除某日某车次的余票信息
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        dailyTrainTicketExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        dailyTrainTicketMapper.deleteByExample(dailyTrainTicketExample);

        // 查出某车次的所有的车站信息
        List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(stationList)) {
            LOG.info("该车次没有车站基础数据，生成该车次的余票信息结束");
            return;
        }

        DateTime now = DateTime.now();
        // 得到出发站，从出发站循环终点站 A->B A->C A->D
        for (int i = 0; i < stationList.size(); i++) {
            TrainStation trainStationStart = stationList.get(i);
            BigDecimal sumKM = BigDecimal.ZERO;
            for (int j = (i + 1); j < stationList.size(); j++) {
                TrainStation trainStationEnd = stationList.get(j);
                sumKM = sumKM.add(trainStationEnd.getKm());

                DailyTrainTicket dailyTrainTicket = new DailyTrainTicket();
                dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
                dailyTrainTicket.setDate(date);
                dailyTrainTicket.setTrainCode(trainCode);
                dailyTrainTicket.setStart(trainStationStart.getName());
                dailyTrainTicket.setStartPinyin(trainStationStart.getNamePinyin());
                dailyTrainTicket.setStartTime(trainStationStart.getOutTime());
                dailyTrainTicket.setStartIndex(trainStationStart.getIndex());
                dailyTrainTicket.setEnd(trainStationEnd.getName());
                dailyTrainTicket.setEndPinyin(trainStationEnd.getNamePinyin());
                dailyTrainTicket.setEndTime(trainStationEnd.getInTime());
                dailyTrainTicket.setEndIndex(trainStationEnd.getIndex());
                int ydz= dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YDZ.getCode());
                int edz= dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.EDZ.getCode());
                int yw= dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YW.getCode());
                int rw= dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.RW.getCode());
                dailyTrainTicket.setYdz(ydz);
                //计算票价需要 距离*火车系数*座位系数
                String trainType = dailyTrain.getType();
                BigDecimal priceRate = EnumUtil.getFieldBy(TrainTypeEnum::getPriceRate, TrainTypeEnum::getCode, trainType);
                BigDecimal ydzPrice = sumKM.multiply(SeatTypeEnum.YDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal edzPrice = sumKM.multiply(SeatTypeEnum.EDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal ywPrice = sumKM.multiply(SeatTypeEnum.YW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal rwPrice = sumKM.multiply(SeatTypeEnum.RW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                dailyTrainTicket.setYdzPrice(ydzPrice);
                dailyTrainTicket.setEdz(edz);
                dailyTrainTicket.setEdzPrice(edzPrice);
                dailyTrainTicket.setRw(rw);
                dailyTrainTicket.setRwPrice(rwPrice);
                dailyTrainTicket.setYw(yw);
                dailyTrainTicket.setYwPrice(ywPrice);
                dailyTrainTicket.setCreateTime(now);
                dailyTrainTicket.setUpdateTime(now);
                dailyTrainTicketMapper.insert(dailyTrainTicket);
            }
        }
        LOG.info("生成日期【{}】车次【{}】的余票信息结束", DateUtil.formatDate(date), trainCode);

    }
    public DailyTrainTicket selectByUnique(Date date, String trainCode,String start,String end) {
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        dailyTrainTicketExample.createCriteria()
                .andTrainCodeEqualTo(trainCode)
                .andDateEqualTo(date)
                .andStartEqualTo(start)
                .andEndEqualTo(end);
        List<DailyTrainTicket> list = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
