package com.jiawa.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.business.domain.TrainStation;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.business.domain.DailyTrainStation;
import com.jiawa.train.business.domain.DailyTrainStationExample;
import com.jiawa.train.business.mapper.DailyTrainStationMapper;
import com.jiawa.train.business.req.DailyTrainStationQueryReq;
import com.jiawa.train.business.req.DailyTrainStationSaveReq;
import com.jiawa.train.business.resp.DailyTrainStationQueryResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * ClassName: DailyTrainStationService
 * Package: com.jiawa.train.business.service
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:27
 * @Version 1.0
 */
@Service
public class DailyTrainStationService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationService.class);

    @Autowired
    private DailyTrainStationMapper dailyTrainStationMapper;
    @Autowired
    private TrainStationService trainStationService;
    public void save(DailyTrainStationSaveReq dailyTrainStationSaveReq) {
        DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(dailyTrainStationSaveReq, DailyTrainStation.class);
        DateTime now = DateTime.now();
        if(ObjectUtil.isNull(dailyTrainStation.getId())) {
//设置一下memberId
            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.insert(dailyTrainStation);
        }else {
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.updateByPrimaryKey(dailyTrainStation);
        }
    }

    public PageResp<DailyTrainStationQueryResp> queryList(DailyTrainStationQueryReq dailyTrainStationQueryReq) {
        DailyTrainStationExample dailyTrainStationExample = new DailyTrainStationExample();
        dailyTrainStationExample.setOrderByClause("date desc, train_code asc, `index` asc");
        DailyTrainStationExample.Criteria criteria = dailyTrainStationExample.createCriteria();
    //这里构造一下查询条件
        if (ObjectUtil.isNotNull(dailyTrainStationQueryReq.getTrainCode())){
            criteria.andTrainCodeEqualTo(dailyTrainStationQueryReq.getTrainCode());
        }
        if (ObjectUtil.isNotNull(dailyTrainStationQueryReq.getDate())){
            criteria.andDateEqualTo(dailyTrainStationQueryReq.getDate());
        }
        LOG.info("查询页码：{}", dailyTrainStationQueryReq.getPage());
        LOG.info("每页条数：{}", dailyTrainStationQueryReq.getSize());
        PageHelper.startPage(dailyTrainStationQueryReq.getPage(), dailyTrainStationQueryReq.getSize());
        List<DailyTrainStation> dailyTrainStations = dailyTrainStationMapper.selectByExample(dailyTrainStationExample);
        PageInfo<DailyTrainStation> dailyTrainStationPageInfo = new PageInfo<>(dailyTrainStations);

        LOG.info("总行数：{}", dailyTrainStationPageInfo.getTotal());
        LOG.info("总页数：{}", dailyTrainStationPageInfo.getPages());
        List<DailyTrainStationQueryResp> list = BeanUtil.copyToList(dailyTrainStations, DailyTrainStationQueryResp.class);
        PageResp<DailyTrainStationQueryResp> dailyTrainStationQueryRespPageResp = new PageResp<>();
        dailyTrainStationQueryRespPageResp.setTotal(dailyTrainStationPageInfo.getTotal());
        dailyTrainStationQueryRespPageResp.setList(list);
        return dailyTrainStationQueryRespPageResp;
    }
    public void delete(Long id) {
        dailyTrainStationMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public void genDaily(Date date, String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的车站信息开始", DateUtil.formatDate(date), trainCode);

        // 删除某日某车次的车站信息
        DailyTrainStationExample dailyTrainStationExample = new DailyTrainStationExample();
        dailyTrainStationExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        dailyTrainStationMapper.deleteByExample(dailyTrainStationExample);

        // 查出某车次的所有的车站信息
        List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(stationList)) {
            LOG.info("该车次没有车站基础数据，生成该车次的车站信息结束");
            return;
        }

        for (TrainStation trainStation : stationList) {
            DateTime now = DateTime.now();
            DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(trainStation, DailyTrainStation.class);
            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStation.setDate(date);
            dailyTrainStationMapper.insert(dailyTrainStation);
        }
        LOG.info("生成日期【{}】车次【{}】的车站信息结束", DateUtil.formatDate(date), trainCode);
    }
}
