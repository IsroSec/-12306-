package com.jiawa.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.business.domain.SkToken;
import com.jiawa.train.business.domain.SkTokenExample;
import com.jiawa.train.business.mapper.SkTokenMapper;
import com.jiawa.train.business.req.SkTokenQueryReq;
import com.jiawa.train.business.req.SkTokenSaveReq;
import com.jiawa.train.business.resp.SkTokenQueryResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * ClassName: SkTokenService
 * Package: com.jiawa.train.business.service
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:27
 * @Version 1.0
 */
@Service
public class SkTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(SkTokenService.class);

    @Autowired
    private SkTokenMapper skTokenMapper;
    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;
    @Autowired
    private DailyTrainStationService dailyTrainStationService;
    public void save(SkTokenSaveReq skTokenSaveReq) {
        SkToken skToken = BeanUtil.copyProperties(skTokenSaveReq, SkToken.class);
        DateTime now = DateTime.now();
        if(ObjectUtil.isNull(skToken.getId())) {
//设置一下memberId
            skToken.setId(SnowUtil.getSnowflakeNextId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skTokenMapper.insert(skToken);
        }else {
            skToken.setUpdateTime(now);
            skTokenMapper.updateByPrimaryKey(skToken);
        }
    }

    public PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq skTokenQueryReq) {
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.setOrderByClause("id desc");
        SkTokenExample.Criteria criteria = skTokenExample.createCriteria();
    //这里构造一下查询条件

        LOG.info("查询页码：{}", skTokenQueryReq.getPage());
        LOG.info("每页条数：{}", skTokenQueryReq.getSize());
        PageHelper.startPage(skTokenQueryReq.getPage(), skTokenQueryReq.getSize());
        List<SkToken> skTokens = skTokenMapper.selectByExample(skTokenExample);
        PageInfo<SkToken> skTokenPageInfo = new PageInfo<>(skTokens);

        LOG.info("总行数：{}", skTokenPageInfo.getTotal());
        LOG.info("总页数：{}", skTokenPageInfo.getPages());
        List<SkTokenQueryResp> list = BeanUtil.copyToList(skTokens, SkTokenQueryResp.class);
        PageResp<SkTokenQueryResp> skTokenQueryRespPageResp = new PageResp<>();
        skTokenQueryRespPageResp.setTotal(skTokenPageInfo.getTotal());
        skTokenQueryRespPageResp.setList(list);
        return skTokenQueryRespPageResp;
    }
    public void delete(Long id) {
        skTokenMapper.deleteByPrimaryKey(id);
    }

    public void genDaily(Date date,String trainCode){
        LOG.info("删除日期【{}】车次【{}】的令牌记录",date,trainCode);
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        skTokenMapper.deleteByExample(skTokenExample);

        //删了之后重新生成
        DateTime now = DateTime.now();
        SkToken skToken = new SkToken();
        skToken.setId(SnowUtil.getSnowflakeNextId());
        skToken.setDate(date);
        skToken.setTrainCode(trainCode);
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);

        //统计一下每辆车的座位数
        int seatCount=dailyTrainSeatService.countSeat(date,trainCode);
        LOG.info("车次【{}】座位数【{}】",trainCode,seatCount);
        //统计一下每一站的座位数
        int stationCount=dailyTrainStationService.countByTrainCode(date,trainCode);
        LOG.info("车次【{}】站点数【{}】",trainCode,stationCount);

        //需要根据实际卖票比例来定，一趟火车最多可以卖（seatCount*stationCount）
        int count =(seatCount*stationCount*3/4);
        LOG.info("车次【{}】令牌数【{}】",trainCode,count);
        skToken.setCount(count);

        skTokenMapper.insert(skToken);
    }
}
