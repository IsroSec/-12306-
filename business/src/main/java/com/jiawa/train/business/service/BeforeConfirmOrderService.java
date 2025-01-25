package com.jiawa.train.business.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.jiawa.train.business.enums.RedisKeyPreEnum;
import com.jiawa.train.business.req.ConfirmOrderDoReq;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * ClassName: beforeDoConfirm
 * Package: com.jiawa.train.business.service
 * Description:
 *
 * @Author GalSec
 * @Create 2025/1/25 19:51
 * @Version 1.0
 */
@Service
public class BeforeConfirmOrderService {
    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SkTokenService skTokenService;
    @SentinelResource(value = "doConfirm", blockHandler = "doConfirmBlock")
    public void doConfirm(ConfirmOrderDoReq confirmOrderDoReq) {
        //校验令牌余量
        boolean validSkToken=skTokenService.validSkToken(confirmOrderDoReq.getTrainCode(),confirmOrderDoReq.getDate(),confirmOrderDoReq.getMemberId());
        if (validSkToken){
            LOG.info("令牌校验通过");
        }else {
            LOG.info("令牌校验不通过");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_FAIL);
        }
        // 省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过
        String localKey= RedisKeyPreEnum.CONFIRM_ORDER +"-"+ DateUtil.formatDate(confirmOrderDoReq.getDate())+"-"+confirmOrderDoReq.getTrainCode();
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(localKey, localKey, 5, TimeUnit.SECONDS);
        if (aBoolean){
            LOG.info("加锁成功:{}",localKey);
        }else {
            LOG.info("加锁失败:{}",localKey);
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION_FAIL);
        }
        //通过rocketmq发送异步请求
        String reqJson = JSON.toJSONString(confirmOrderDoReq);
        LOG.info("排队购票，发送mq开始，消息：{}", reqJson);

    }

    private void doConfirmBlock(ConfirmOrderDoReq confirmOrderDoReq, BlockException e){
        LOG.info("限流了");
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }
}
