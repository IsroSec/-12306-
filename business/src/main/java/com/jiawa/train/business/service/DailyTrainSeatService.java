package com.jiawa.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.business.domain.TrainSeat;
import com.jiawa.train.business.domain.TrainStation;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.business.domain.DailyTrainSeat;
import com.jiawa.train.business.domain.DailyTrainSeatExample;
import com.jiawa.train.business.mapper.DailyTrainSeatMapper;
import com.jiawa.train.business.req.DailyTrainSeatQueryReq;
import com.jiawa.train.business.req.DailyTrainSeatSaveReq;
import com.jiawa.train.business.resp.DailyTrainSeatQueryResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * ClassName: DailyTrainSeatService
 * Package: com.jiawa.train.business.service
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:27
 * @Version 1.0
 */
@Service
public class DailyTrainSeatService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainSeatService.class);

    @Autowired
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Autowired
    private TrainSeatService trainSeatService;
    @Autowired
    private TrainStationService trainStationService;
    public void save(DailyTrainSeatSaveReq dailyTrainSeatSaveReq) {
        DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(dailyTrainSeatSaveReq, DailyTrainSeat.class);
        DateTime now = DateTime.now();
        if(ObjectUtil.isNull(dailyTrainSeat.getId())) {
//设置一下memberId
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        }else {
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.updateByPrimaryKey(dailyTrainSeat);
        }
    }

    public PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq dailyTrainSeatQueryReq) {
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.setOrderByClause("date desc, train_code asc, carriage_index asc, carriage_seat_index asc");
        DailyTrainSeatExample.Criteria criteria = dailyTrainSeatExample.createCriteria();
    //这里构造一下查询条件
        if (ObjectUtil.isNotEmpty(dailyTrainSeatQueryReq.getTrainCode())) {
            criteria.andTrainCodeEqualTo(dailyTrainSeatQueryReq.getTrainCode());
        }
        LOG.info("查询页码：{}", dailyTrainSeatQueryReq.getPage());
        LOG.info("每页条数：{}", dailyTrainSeatQueryReq.getSize());
        PageHelper.startPage(dailyTrainSeatQueryReq.getPage(), dailyTrainSeatQueryReq.getSize());
        List<DailyTrainSeat> dailyTrainSeats = dailyTrainSeatMapper.selectByExample(dailyTrainSeatExample);
        PageInfo<DailyTrainSeat> dailyTrainSeatPageInfo = new PageInfo<>(dailyTrainSeats);

        LOG.info("总行数：{}", dailyTrainSeatPageInfo.getTotal());
        LOG.info("总页数：{}", dailyTrainSeatPageInfo.getPages());
        List<DailyTrainSeatQueryResp> list = BeanUtil.copyToList(dailyTrainSeats, DailyTrainSeatQueryResp.class);
        PageResp<DailyTrainSeatQueryResp> dailyTrainSeatQueryRespPageResp = new PageResp<>();
        dailyTrainSeatQueryRespPageResp.setTotal(dailyTrainSeatPageInfo.getTotal());
        dailyTrainSeatQueryRespPageResp.setList(list);
        return dailyTrainSeatQueryRespPageResp;
    }
    public void delete(Long id) {
        dailyTrainSeatMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public void genDaily(Date date, String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的座位信息开始", DateUtil.formatDate(date), trainCode);

        // 删除某日某车次的座位信息
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        dailyTrainSeatMapper.deleteByExample(dailyTrainSeatExample);

        List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
        //初始售卖情况为0，一开始有5个车站，所以sell的初始值是0000
        String sell = StrUtil.fillBefore("", '0', stationList.size() - 1);

        // 查出某车次的所有的座位信息
        List<TrainSeat> seatList = trainSeatService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(seatList)) {
            LOG.info("该车次没有座位基础数据，生成该车次的座位信息结束");
            return;
        }

        for (TrainSeat trainSeat : seatList) {
            DateTime now = DateTime.now();
            DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(trainSeat, DailyTrainSeat.class);
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeat.setDate(date);
            dailyTrainSeat.setSell(sell);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        }
        LOG.info("生成日期【{}】车次【{}】的座位信息结束", DateUtil.formatDate(date), trainCode);
    }

    public int countSeat(Date date, String trainCode, String seatType){
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode)
                .andSeatTypeEqualTo(seatType);
        long l = dailyTrainSeatMapper.countByExample(dailyTrainSeatExample);
        if (l == 0L) {
            return -1;
        }
        return (int) l;
    }
}
