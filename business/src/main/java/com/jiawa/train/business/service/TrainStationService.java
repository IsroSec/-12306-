package com.jiawa.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.business.domain.TrainStation;
import com.jiawa.train.business.domain.TrainStationExample;
import com.jiawa.train.business.mapper.TrainStationMapper;
import com.jiawa.train.business.req.TrainStationQueryReq;
import com.jiawa.train.business.req.TrainStationSaveReq;
import com.jiawa.train.business.resp.TrainStationQueryResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: TrainStationService
 * Package: com.jiawa.train.business.service
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:27
 * @Version 1.0
 */
@Service
public class TrainStationService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainStationService.class);

    @Autowired
    private TrainStationMapper trainStationMapper;
    public void save(TrainStationSaveReq trainStationSaveReq) {
        TrainStation trainStation = BeanUtil.copyProperties(trainStationSaveReq, TrainStation.class);
        DateTime now = DateTime.now();
        if(ObjectUtil.isNull(trainStation.getId())) {
            // 保存之前，先校验唯一键是否存在
            TrainStation trainStationDB = selectByUnique(trainStationSaveReq.getTrainCode(), trainStationSaveReq.getIndex());
            if (ObjectUtil.isNotEmpty(trainStationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_INDEX_UNIQUE_ERROR);
            }
            // 保存之前，先校验唯一键是否存在
            trainStationDB = selectByUnique(trainStationSaveReq.getTrainCode(), trainStationSaveReq.getName());
            if (ObjectUtil.isNotEmpty(trainStationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR);
            }
            trainStation.setId(SnowUtil.getSnowflakeNextId());
            trainStation.setCreateTime(now);
            trainStation.setUpdateTime(now);
            trainStationMapper.insert(trainStation);
        }else {
            trainStation.setUpdateTime(now);
            trainStationMapper.updateByPrimaryKey(trainStation);
        }
    }
    private TrainStation selectByUnique(String trainCode, Integer index) {
        TrainStationExample trainStationExample = new TrainStationExample();
        trainStationExample.createCriteria()
                .andTrainCodeEqualTo(trainCode)
                .andIndexEqualTo(index);
        List<TrainStation> list = trainStationMapper.selectByExample(trainStationExample);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    private TrainStation selectByUnique(String trainCode, String name) {
        TrainStationExample trainStationExample = new TrainStationExample();
        trainStationExample.createCriteria()
                .andTrainCodeEqualTo(trainCode)
                .andNameEqualTo(name);
        List<TrainStation> list = trainStationMapper.selectByExample(trainStationExample);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }
    public PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq trainStationQueryReq) {
        TrainStationExample trainStationExample = new TrainStationExample();
        trainStationExample.setOrderByClause("train_code asc, `index` asc");
        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();
    //这里构造一下查询条件

        LOG.info("查询页码：{}", trainStationQueryReq.getPage());
        LOG.info("每页条数：{}", trainStationQueryReq.getSize());
        PageHelper.startPage(trainStationQueryReq.getPage(), trainStationQueryReq.getSize());
        List<TrainStation> trainStations = trainStationMapper.selectByExample(trainStationExample);
        PageInfo<TrainStation> trainStationPageInfo = new PageInfo<>(trainStations);

        LOG.info("总行数：{}", trainStationPageInfo.getTotal());
        LOG.info("总页数：{}", trainStationPageInfo.getPages());
        List<TrainStationQueryResp> list = BeanUtil.copyToList(trainStations, TrainStationQueryResp.class);
        PageResp<TrainStationQueryResp> trainStationQueryRespPageResp = new PageResp<>();
        trainStationQueryRespPageResp.setTotal(trainStationPageInfo.getTotal());
        trainStationQueryRespPageResp.setList(list);
        return trainStationQueryRespPageResp;
    }
    public void delete(Long id) {
        trainStationMapper.deleteByPrimaryKey(id);
    }

    public List<TrainStation> selectByTrainCode(String trainCode) {
        TrainStationExample trainStationExample = new TrainStationExample();
        trainStationExample.setOrderByClause("`index` asc");
        trainStationExample.createCriteria().andTrainCodeEqualTo(trainCode);
        return trainStationMapper.selectByExample(trainStationExample);
    }
}
