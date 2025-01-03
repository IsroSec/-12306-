package com.jiawa.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.business.domain.Station;
import com.jiawa.train.business.domain.StationExample;
import com.jiawa.train.business.mapper.StationMapper;
import com.jiawa.train.business.req.StationQueryReq;
import com.jiawa.train.business.req.StationSaveReq;
import com.jiawa.train.business.resp.StationQueryResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: StationService
 * Package: com.jiawa.train.business.service
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:27
 * @Version 1.0
 */
@Service
public class StationService {

    private static final Logger LOG = LoggerFactory.getLogger(StationService.class);

    @Autowired
    private StationMapper stationMapper;
    public void save(StationSaveReq stationSaveReq) {
        Station station = BeanUtil.copyProperties(stationSaveReq, Station.class);
        DateTime now = DateTime.now();
        if(ObjectUtil.isNull(station.getId())) {
//设置一下memberId
            station.setId(SnowUtil.getSnowflakeNextId());
            station.setCreateTime(now);
            station.setUpdateTime(now);
            stationMapper.insert(station);
        }else {
            station.setUpdateTime(now);
            stationMapper.updateByPrimaryKey(station);
        }
    }

    public PageResp<StationQueryResp> queryList(StationQueryReq stationQueryReq) {
        StationExample stationExample = new StationExample();
        stationExample.setOrderByClause("id desc");
        StationExample.Criteria criteria = stationExample.createCriteria();
    //这里构造一下查询条件

        LOG.info("查询页码：{}", stationQueryReq.getPage());
        LOG.info("每页条数：{}", stationQueryReq.getSize());
        PageHelper.startPage(stationQueryReq.getPage(), stationQueryReq.getSize());
        List<Station> stations = stationMapper.selectByExample(stationExample);
        PageInfo<Station> stationPageInfo = new PageInfo<>(stations);

        LOG.info("总行数：{}", stationPageInfo.getTotal());
        LOG.info("总页数：{}", stationPageInfo.getPages());
        List<StationQueryResp> list = BeanUtil.copyToList(stations, StationQueryResp.class);
        PageResp<StationQueryResp> stationQueryRespPageResp = new PageResp<>();
        stationQueryRespPageResp.setTotal(stationPageInfo.getTotal());
        stationQueryRespPageResp.setList(list);
        return stationQueryRespPageResp;
    }
    public void delete(Long id) {
        stationMapper.deleteByPrimaryKey(id);
    }
}
