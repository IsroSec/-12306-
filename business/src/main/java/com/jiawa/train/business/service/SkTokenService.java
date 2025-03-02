package com.jiawa.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.business.enums.RedisKeyPreEnum;
import com.jiawa.train.business.mapper.cust.SkTokenMapperCust;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private SkTokenMapperCust skTokenMapperCust;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Value("${spring.profiles.active}")
    private String env;
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

    public boolean validSkToken(String trainCode,Date date,Long memberId) {
        LOG.info("会员【{}】获取日期【{}】车次【{}】的令牌开始",memberId,date,trainCode);
        if (!env.equals("dev")){
            //获取令牌锁，再进行校验令牌余量，防止刷票
            String lockKey = RedisKeyPreEnum.SK_TOKEN +"-"+DateUtil.formatDate(date) + "-" + trainCode;
            Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
            if (setIfAbsent){
                LOG.info("获取令牌锁成功");
            }else {
                LOG.info("获取令牌锁失败");
                return false;
            }
        }

        //我们改成使用redis
        String skTokenCountKey = RedisKeyPreEnum.SK_TOKEN_COUNT + "-" + DateUtil.formatDate(date) + trainCode;
        Object skTokenCount = redisTemplate.opsForValue().get(skTokenCountKey);
        if (skTokenCount!=null){
            LOG.info("令牌余量为【{}】",skTokenCount);
            Long count = redisTemplate.opsForValue().decrement(skTokenCountKey, 1);
            if (count<0L){
                LOG.error("获取令牌失败：{}",count);
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_FAIL);
            }else {
                LOG.info("获取令牌成功：{}",count);
                redisTemplate.expire(skTokenCountKey,60,TimeUnit.SECONDS);
                if (count%5==0){
                    skTokenMapperCust.decrease(date,trainCode,5);
                }
                return true;
            }
        }else {
            //第一次没有缓存的话
            LOG.info("缓存中没有该车次令牌大闸的key:{}",skTokenCountKey);
            //先做检查数据库中的令牌是否有余量
            SkTokenExample skTokenExample = new SkTokenExample();
            skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
            List<SkToken> tokenCountList = skTokenMapper.selectByExample(skTokenExample);
            if (CollUtil.isEmpty(tokenCountList)){
                LOG.info("找不到日期【{}】车次【{}】的令牌记录",date,trainCode);
                return false;
            }
            SkToken skToken = tokenCountList.get(0);
            if (skToken.getCount()<=0){
                LOG.error("获取令牌失败：{}",skToken.getCount());
                return false;
            }
            //如果令牌还有余量，则减一放入redis中
            Integer count = skToken.getCount() - 1;
            LOG.info("将改车次的令牌大闸放入缓存中,key:{},count:{}",skTokenCountKey,count);
            redisTemplate.opsForValue().set(skTokenCountKey,String.valueOf(count),60,TimeUnit.SECONDS);
            return true;
        }
        //频繁访问数据库
//        int updateCount=skTokenMapperCust.decrease(date,trainCode);
//        if (updateCount>0){
//            return true;
//        }else {
//            return false;
//        }
    }
}
