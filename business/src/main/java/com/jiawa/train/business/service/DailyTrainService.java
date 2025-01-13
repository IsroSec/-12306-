package com.jiawa.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.business.domain.Train;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.business.domain.DailyTrain;
import com.jiawa.train.business.domain.DailyTrainExample;
import com.jiawa.train.business.mapper.DailyTrainMapper;
import com.jiawa.train.business.req.DailyTrainQueryReq;
import com.jiawa.train.business.req.DailyTrainSaveReq;
import com.jiawa.train.business.resp.DailyTrainQueryResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * ClassName: DailyTrainService
 * Package: com.jiawa.train.business.service
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:27
 * @Version 1.0
 */
@Service
public class DailyTrainService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainService.class);

    @Autowired
    private DailyTrainMapper dailyTrainMapper;

    @Autowired
    private TrainService trainService;

    @Autowired
    private DailyTrainStationService dailyTrainStationService;

    @Autowired
    private  DailyTrainCarriageService dailyTrainCarriageService;

    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;

    @Autowired
    private DailyTrainTicketService dailyTrainTicketService;
    public void save(DailyTrainSaveReq dailyTrainSaveReq) {
        DailyTrain dailyTrain = BeanUtil.copyProperties(dailyTrainSaveReq, DailyTrain.class);
        DateTime now = DateTime.now();
        if(ObjectUtil.isNull(dailyTrain.getId())) {
//设置一下memberId
            dailyTrain.setId(SnowUtil.getSnowflakeNextId());
            dailyTrain.setCreateTime(now);
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.insert(dailyTrain);
        }else {
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.updateByPrimaryKey(dailyTrain);
        }
    }

    public PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq dailyTrainQueryReq) {
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        dailyTrainExample.setOrderByClause("date desc, code asc");
        DailyTrainExample.Criteria criteria = dailyTrainExample.createCriteria();
    //这里构造一下查询条件
        if (ObjectUtil.isNotNull(dailyTrainQueryReq.getCode())){
            criteria.andCodeEqualTo(dailyTrainQueryReq.getCode());
        }
        if (ObjectUtil.isNotNull(dailyTrainQueryReq.getDate())){
            criteria.andDateEqualTo(dailyTrainQueryReq.getDate());
        }
        LOG.info("查询页码：{}", dailyTrainQueryReq.getPage());
        LOG.info("每页条数：{}", dailyTrainQueryReq.getSize());
        PageHelper.startPage(dailyTrainQueryReq.getPage(), dailyTrainQueryReq.getSize());
        List<DailyTrain> dailyTrains = dailyTrainMapper.selectByExample(dailyTrainExample);
        PageInfo<DailyTrain> dailyTrainPageInfo = new PageInfo<>(dailyTrains);

        LOG.info("总行数：{}", dailyTrainPageInfo.getTotal());
        LOG.info("总页数：{}", dailyTrainPageInfo.getPages());
        List<DailyTrainQueryResp> list = BeanUtil.copyToList(dailyTrains, DailyTrainQueryResp.class);
        PageResp<DailyTrainQueryResp> dailyTrainQueryRespPageResp = new PageResp<>();
        dailyTrainQueryRespPageResp.setTotal(dailyTrainPageInfo.getTotal());
        dailyTrainQueryRespPageResp.setList(list);
        return dailyTrainQueryRespPageResp;
    }
    public void delete(Long id) {
        dailyTrainMapper.deleteByPrimaryKey(id);
    }

    public void genDaily(Date date) {
        /**
         * 先查询所有的车次
         * 然后先把每日车次表的数据删除
         * 重新插入今天的车次表
         */
        List<Train> trainList = trainService.selectAll();
        if (CollUtil.isEmpty(trainList)) {
            LOG.info("没有车次基础数据，任务结束");
            return;
        }

        for (Train train : trainList) {
            genDailyTrain(date, train);
        }
    }

    @Transactional
    public void genDailyTrain(Date date, Train train) {
        LOG.info("生成日期【{}】车次【{}】的信息开始", DateUtil.formatDate(date), train.getCode());
        // 删除该车次已有的数据
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        dailyTrainExample.createCriteria()
                .andDateEqualTo(date)
                .andCodeEqualTo(train.getCode());
        dailyTrainMapper.deleteByExample(dailyTrainExample);

        // 生成该车次的数据
        DateTime now = DateTime.now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(train, DailyTrain.class);
        dailyTrain.setId(SnowUtil.getSnowflakeNextId());
        dailyTrain.setCreateTime(now);
        dailyTrain.setUpdateTime(now);
        dailyTrain.setDate(date);
        dailyTrainMapper.insert(dailyTrain);

        // 生成该车次的车站数据
        dailyTrainStationService.genDaily(date, train.getCode());
        // 生成该车次的车厢数据
        dailyTrainCarriageService.genDaily(date, train.getCode());
        // 生成该车次的座位数据
        dailyTrainSeatService.genDaily(date, train.getCode());
        // 生成该车次的票数据
        dailyTrainTicketService.genDaily(dailyTrain,date, train.getCode());
    }
}
