package com.jiawa.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.business.domain.Train;
import com.jiawa.train.business.domain.TrainExample;
import com.jiawa.train.business.mapper.TrainMapper;
import com.jiawa.train.business.req.TrainQueryReq;
import com.jiawa.train.business.req.TrainSaveReq;
import com.jiawa.train.business.resp.TrainQueryResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: TrainService
 * Package: com.jiawa.train.business.service
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:27
 * @Version 1.0
 */
@Service
public class TrainService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainService.class);

    @Autowired
    private TrainMapper trainMapper;
    public void save(TrainSaveReq trainSaveReq) {
        Train train = BeanUtil.copyProperties(trainSaveReq, Train.class);
        DateTime now = DateTime.now();
        if(ObjectUtil.isNull(train.getId())) {
//设置一下memberId
            train.setId(SnowUtil.getSnowflakeNextId());
            train.setCreateTime(now);
            train.setUpdateTime(now);
            trainMapper.insert(train);
        }else {
            train.setUpdateTime(now);
            trainMapper.updateByPrimaryKey(train);
        }
    }

    public PageResp<TrainQueryResp> queryList(TrainQueryReq trainQueryReq) {
        TrainExample trainExample = new TrainExample();
        trainExample.setOrderByClause("id desc");
        TrainExample.Criteria criteria = trainExample.createCriteria();
    //这里构造一下查询条件

        LOG.info("查询页码：{}", trainQueryReq.getPage());
        LOG.info("每页条数：{}", trainQueryReq.getSize());
        PageHelper.startPage(trainQueryReq.getPage(), trainQueryReq.getSize());
        List<Train> trains = trainMapper.selectByExample(trainExample);
        PageInfo<Train> trainPageInfo = new PageInfo<>(trains);

        LOG.info("总行数：{}", trainPageInfo.getTotal());
        LOG.info("总页数：{}", trainPageInfo.getPages());
        List<TrainQueryResp> list = BeanUtil.copyToList(trains, TrainQueryResp.class);
        PageResp<TrainQueryResp> trainQueryRespPageResp = new PageResp<>();
        trainQueryRespPageResp.setTotal(trainPageInfo.getTotal());
        trainQueryRespPageResp.setList(list);
        return trainQueryRespPageResp;
    }
    public void delete(Long id) {
        trainMapper.deleteByPrimaryKey(id);
    }
    public List<TrainQueryResp> queryAll() {
        TrainExample trainExample = new TrainExample();
        trainExample.setOrderByClause("id desc");
        TrainExample.Criteria criteria = trainExample.createCriteria();
        //这里构造一下查询条件
        List<Train> trains = trainMapper.selectByExample(trainExample);
        List<TrainQueryResp> list = BeanUtil.copyToList(trains, TrainQueryResp.class);
        return list;
    }
}
