package com.jiawa.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.business.domain.TrainCarriage;
import com.jiawa.train.business.enums.SeatColEnum;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.business.domain.TrainSeat;
import com.jiawa.train.business.domain.TrainSeatExample;
import com.jiawa.train.business.mapper.TrainSeatMapper;
import com.jiawa.train.business.req.TrainSeatQueryReq;
import com.jiawa.train.business.req.TrainSeatSaveReq;
import com.jiawa.train.business.resp.TrainSeatQueryResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: TrainSeatService
 * Package: com.jiawa.train.business.service
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:27
 * @Version 1.0
 */
@Service
public class TrainSeatService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainSeatService.class);

    @Autowired
    private TrainSeatMapper trainSeatMapper;

    @Autowired
    private TrainCarriageService trainCarriageService;
    public void save(TrainSeatSaveReq trainSeatSaveReq) {
        TrainSeat trainSeat = BeanUtil.copyProperties(trainSeatSaveReq, TrainSeat.class);
        DateTime now = DateTime.now();
        if(ObjectUtil.isNull(trainSeat.getId())) {
//设置一下memberId
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insert(trainSeat);
        }else {
            trainSeat.setUpdateTime(now);
            trainSeatMapper.updateByPrimaryKey(trainSeat);
        }
    }

    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq trainSeatQueryReq) {
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        trainSeatExample.setOrderByClause("id asc");
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
    //这里构造一下查询条件
        if (ObjectUtil.isNotEmpty(trainSeatQueryReq.getTrainCode())) {
            criteria.andTrainCodeEqualTo(trainSeatQueryReq.getTrainCode());
        }
        LOG.info("查询页码：{}", trainSeatQueryReq.getPage());
        LOG.info("每页条数：{}", trainSeatQueryReq.getSize());
        PageHelper.startPage(trainSeatQueryReq.getPage(), trainSeatQueryReq.getSize());
        List<TrainSeat> trainSeats = trainSeatMapper.selectByExample(trainSeatExample);
        PageInfo<TrainSeat> trainSeatPageInfo = new PageInfo<>(trainSeats);

        LOG.info("总行数：{}", trainSeatPageInfo.getTotal());
        LOG.info("总页数：{}", trainSeatPageInfo.getPages());
        List<TrainSeatQueryResp> list = BeanUtil.copyToList(trainSeats, TrainSeatQueryResp.class);
        PageResp<TrainSeatQueryResp> trainSeatQueryRespPageResp = new PageResp<>();
        trainSeatQueryRespPageResp.setTotal(trainSeatPageInfo.getTotal());
        trainSeatQueryRespPageResp.setList(list);
        return trainSeatQueryRespPageResp;
    }
    public void delete(Long id) {
        trainSeatMapper.deleteByPrimaryKey(id);
    }

    public void genTrainSeat(String trainCode) {
        /**
         * 生成火车座位的逻辑分析
         * 1.首先先删掉该车厢的所有座位
         * 2.找到该车次的所有车厢，遍历车厢，然后重新找到该车厢的排数
         * 3.车厢的列数在枚举SeatTypeEnum中
         * 4.循环遍历每一排
         * 5.循环遍历每一列
         * 6.生成每一个座位
         */
        DateTime now = DateTime.now();
        //1.先删除火车车厢原有的座位
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        trainSeatExample.createCriteria().andTrainCodeEqualTo(trainCode);
        trainSeatMapper.deleteByExample(trainSeatExample);
        //2.找到该车次的所有车厢，进行遍历
        List<TrainCarriage> trainCarriages = trainCarriageService.selectByTrainCode(trainCode);
        for (TrainCarriage trainCarriage: trainCarriages) {
            Integer rowCount = trainCarriage.getRowCount();
            String seatType = trainCarriage.getSeatType();
            //每次重新生成车厢时都重新从1开始
            int seatIndex = 1;
            List<SeatColEnum> colsByTypeList = SeatColEnum.getColsByType(seatType);
            LOG.info("座位列数：{}", colsByTypeList);
            //遍历排数
            for (int i=1;i<=rowCount;i++) {
                //遍历列数
                for (SeatColEnum colsByType:colsByTypeList) {
                    TrainSeat trainSeat = new TrainSeat();
                    trainSeat.setId(SnowUtil.getSnowflakeNextId());
                    trainSeat.setTrainCode(trainCode);
                    trainSeat.setCarriageIndex(trainCarriage.getIndex());
                    trainSeat.setRow(StrUtil.fillBefore(String.valueOf(i), '0', 2));
                    trainSeat.setCol(colsByType.getCode());
                    trainSeat.setSeatType(seatType);
                    trainSeat.setCarriageSeatIndex(seatIndex++);
                    trainSeat.setCreateTime(now);
                    trainSeat.setUpdateTime(now);
                    trainSeatMapper.insert(trainSeat);
                }
            }
        }
    }
}
