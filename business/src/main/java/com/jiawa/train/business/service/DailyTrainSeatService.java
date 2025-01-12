package com.jiawa.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
        dailyTrainSeatExample.setOrderByClause("id desc");
        DailyTrainSeatExample.Criteria criteria = dailyTrainSeatExample.createCriteria();
    //这里构造一下查询条件

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
}
