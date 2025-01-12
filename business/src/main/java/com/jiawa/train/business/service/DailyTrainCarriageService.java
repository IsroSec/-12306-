package com.jiawa.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.business.domain.TrainCarriage;
import com.jiawa.train.business.enums.SeatColEnum;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.business.domain.DailyTrainCarriage;
import com.jiawa.train.business.domain.DailyTrainCarriageExample;
import com.jiawa.train.business.mapper.DailyTrainCarriageMapper;
import com.jiawa.train.business.req.DailyTrainCarriageQueryReq;
import com.jiawa.train.business.req.DailyTrainCarriageSaveReq;
import com.jiawa.train.business.resp.DailyTrainCarriageQueryResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * ClassName: DailyTrainCarriageService
 * Package: com.jiawa.train.business.service
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:27
 * @Version 1.0
 */
@Service
public class DailyTrainCarriageService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainCarriageService.class);

    @Autowired
    private DailyTrainCarriageMapper dailyTrainCarriageMapper;

    @Autowired
    private TrainCarriageService trainCarriageService;
    public void save(DailyTrainCarriageSaveReq dailyTrainCarriageSaveReq) {
        DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(dailyTrainCarriageSaveReq, DailyTrainCarriage.class);
        // 自动计算出列数和总座位数
        List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(dailyTrainCarriage.getSeatType());
        dailyTrainCarriage.setColCount(seatColEnums.size());
        dailyTrainCarriage.setSeatCount(dailyTrainCarriage.getColCount() * dailyTrainCarriage.getRowCount());
        DateTime now = DateTime.now();
        if(ObjectUtil.isNull(dailyTrainCarriage.getId())) {
//设置一下memberId
            dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.insert(dailyTrainCarriage);
        }else {
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.updateByPrimaryKey(dailyTrainCarriage);
        }
    }

    public PageResp<DailyTrainCarriageQueryResp> queryList(DailyTrainCarriageQueryReq dailyTrainCarriageQueryReq) {
        DailyTrainCarriageExample dailyTrainCarriageExample = new DailyTrainCarriageExample();
        dailyTrainCarriageExample.setOrderByClause("date desc, train_code asc, `index` asc");
        DailyTrainCarriageExample.Criteria criteria = dailyTrainCarriageExample.createCriteria();
    //这里构造一下查询条件
        if (ObjectUtil.isNotNull(dailyTrainCarriageQueryReq.getTrainCode())){
            criteria.andTrainCodeEqualTo(dailyTrainCarriageQueryReq.getTrainCode());
        }
        if (ObjectUtil.isNotNull(dailyTrainCarriageQueryReq.getDate())){
            criteria.andDateEqualTo(dailyTrainCarriageQueryReq.getDate());
        }
        LOG.info("查询页码：{}", dailyTrainCarriageQueryReq.getPage());
        LOG.info("每页条数：{}", dailyTrainCarriageQueryReq.getSize());
        PageHelper.startPage(dailyTrainCarriageQueryReq.getPage(), dailyTrainCarriageQueryReq.getSize());
        List<DailyTrainCarriage> dailyTrainCarriages = dailyTrainCarriageMapper.selectByExample(dailyTrainCarriageExample);
        PageInfo<DailyTrainCarriage> dailyTrainCarriagePageInfo = new PageInfo<>(dailyTrainCarriages);

        LOG.info("总行数：{}", dailyTrainCarriagePageInfo.getTotal());
        LOG.info("总页数：{}", dailyTrainCarriagePageInfo.getPages());
        List<DailyTrainCarriageQueryResp> list = BeanUtil.copyToList(dailyTrainCarriages, DailyTrainCarriageQueryResp.class);
        PageResp<DailyTrainCarriageQueryResp> dailyTrainCarriageQueryRespPageResp = new PageResp<>();
        dailyTrainCarriageQueryRespPageResp.setTotal(dailyTrainCarriagePageInfo.getTotal());
        dailyTrainCarriageQueryRespPageResp.setList(list);
        return dailyTrainCarriageQueryRespPageResp;
    }
    public void delete(Long id) {
        dailyTrainCarriageMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public void genDaily(Date date, String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的车厢信息开始", DateUtil.formatDate(date), trainCode);

        // 删除某日某车次的车厢信息
        DailyTrainCarriageExample dailyTrainCarriageExample = new DailyTrainCarriageExample();
        dailyTrainCarriageExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        dailyTrainCarriageMapper.deleteByExample(dailyTrainCarriageExample);

        // 查出某车次的所有的车厢信息
        List<TrainCarriage> carriageList = trainCarriageService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(carriageList)) {
            LOG.info("该车次没有车厢基础数据，生成该车次的车厢信息结束");
            return;
        }

        for (TrainCarriage trainCarriage : carriageList) {
            DateTime now = DateTime.now();
            DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(trainCarriage, DailyTrainCarriage.class);
            dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriage.setDate(date);
            dailyTrainCarriageMapper.insert(dailyTrainCarriage);
        }
        LOG.info("生成日期【{}】车次【{}】的车厢信息结束", DateUtil.formatDate(date), trainCode);
    }
}
