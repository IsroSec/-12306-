package com.jiawa.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.business.domain.TrainCarriage;
import com.jiawa.train.business.domain.TrainCarriageExample;
import com.jiawa.train.business.mapper.TrainCarriageMapper;
import com.jiawa.train.business.req.TrainCarriageQueryReq;
import com.jiawa.train.business.req.TrainCarriageSaveReq;
import com.jiawa.train.business.resp.TrainCarriageQueryResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: TrainCarriageService
 * Package: com.jiawa.train.business.service
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:27
 * @Version 1.0
 */
@Service
public class TrainCarriageService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainCarriageService.class);

    @Autowired
    private TrainCarriageMapper trainCarriageMapper;
    public void save(TrainCarriageSaveReq trainCarriageSaveReq) {
        TrainCarriage trainCarriage = BeanUtil.copyProperties(trainCarriageSaveReq, TrainCarriage.class);
        DateTime now = DateTime.now();
        if(ObjectUtil.isNull(trainCarriage.getId())) {
//设置一下memberId
            trainCarriage.setId(SnowUtil.getSnowflakeNextId());
            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.insert(trainCarriage);
        }else {
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.updateByPrimaryKey(trainCarriage);
        }
    }

    public PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq trainCarriageQueryReq) {
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        trainCarriageExample.setOrderByClause("id desc");
        TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
    //这里构造一下查询条件
        if (ObjectUtil.isNotEmpty(trainCarriageQueryReq.getTrainCode())){
            criteria.andTrainCodeEqualTo(trainCarriageQueryReq.getTrainCode());
        }
        LOG.info("查询页码：{}", trainCarriageQueryReq.getPage());
        LOG.info("每页条数：{}", trainCarriageQueryReq.getSize());
        PageHelper.startPage(trainCarriageQueryReq.getPage(), trainCarriageQueryReq.getSize());
        List<TrainCarriage> trainCarriages = trainCarriageMapper.selectByExample(trainCarriageExample);
        PageInfo<TrainCarriage> trainCarriagePageInfo = new PageInfo<>(trainCarriages);

        LOG.info("总行数：{}", trainCarriagePageInfo.getTotal());
        LOG.info("总页数：{}", trainCarriagePageInfo.getPages());
        List<TrainCarriageQueryResp> list = BeanUtil.copyToList(trainCarriages, TrainCarriageQueryResp.class);
        PageResp<TrainCarriageQueryResp> trainCarriageQueryRespPageResp = new PageResp<>();
        trainCarriageQueryRespPageResp.setTotal(trainCarriagePageInfo.getTotal());
        trainCarriageQueryRespPageResp.setList(list);
        return trainCarriageQueryRespPageResp;
    }
    public void delete(Long id) {
        trainCarriageMapper.deleteByPrimaryKey(id);
    }
}
