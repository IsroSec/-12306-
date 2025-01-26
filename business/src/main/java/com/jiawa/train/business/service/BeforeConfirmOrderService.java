package com.jiawa.train.business.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.jiawa.train.business.domain.ConfirmOrder;
import com.jiawa.train.business.dto.ConfirmOrderMQDto;
import com.jiawa.train.business.enums.ConfirmOrderStatusEnum;
import com.jiawa.train.business.enums.RedisKeyPreEnum;
import com.jiawa.train.business.enums.RocketMQTopicEnum;
import com.jiawa.train.business.mapper.ConfirmOrderMapper;
import com.jiawa.train.business.req.ConfirmOrderDoReq;
import com.jiawa.train.business.req.ConfirmOrderTicketReq;
import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import com.jiawa.train.common.util.SnowUtil;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
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
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private ConfirmOrderMapper confirmOrderMapper;
    @SentinelResource(value = "doConfirm", blockHandler = "doConfirmBlock")
    public Long doConfirm(ConfirmOrderDoReq confirmOrderDoReq) {
        confirmOrderDoReq.setMemberId(LoginMemberContext.getId());
        //校验令牌余量
        boolean validSkToken=skTokenService.validSkToken(confirmOrderDoReq.getTrainCode(),confirmOrderDoReq.getDate(),confirmOrderDoReq.getMemberId());
        if (validSkToken){
            LOG.info("令牌校验通过");
        }else {
            LOG.info("令牌校验不通过");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_FAIL);
        }
//        // 省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过
//        String localKey= RedisKeyPreEnum.CONFIRM_ORDER +"-"+ DateUtil.formatDate(confirmOrderDoReq.getDate())+"-"+confirmOrderDoReq.getTrainCode();
//        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(localKey, localKey, 5, TimeUnit.SECONDS);
//        if (aBoolean){
//            LOG.info("加锁成功:{}",localKey);
//        }else {
//            LOG.info("加锁失败:{}",localKey);
//            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION_FAIL);
//        }
        Date date = confirmOrderDoReq.getDate();
        String trainCode = confirmOrderDoReq.getTrainCode();
        String start = confirmOrderDoReq.getStart();
        String end = confirmOrderDoReq.getEnd();
        List<ConfirmOrderTicketReq> tickets = confirmOrderDoReq.getTickets();
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrder.setMemberId(LoginMemberContext.getId());
        confirmOrder.setDate(date);
        confirmOrder.setTrainCode(trainCode);
        confirmOrder.setStart(start);
        confirmOrder.setEnd(end);
        confirmOrder.setDailyTrainTicketId(confirmOrderDoReq.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setTickets(JSON.toJSONString(tickets));
        confirmOrderMapper.insert(confirmOrder);
        //通过rocketmq发送异步请求
        ConfirmOrderMQDto confirmOrderMQDto = new ConfirmOrderMQDto();
        confirmOrderMQDto.setMDC(MDC.get("LOG_ID"));
        confirmOrderMQDto.setDate(date);
        confirmOrderMQDto.setTrainCode(trainCode);
        String reqJson = JSON.toJSONString(confirmOrderMQDto);
        LOG.info("排队购票，发送mq开始，消息：{}", reqJson);
        rocketMQTemplate.convertAndSend(RocketMQTopicEnum.CONFIRM_ORDER.getCode(),reqJson);
        LOG.info("排队购票，发送mq结束");
        return confirmOrder.getId();
    }

    private void doConfirmBlock(ConfirmOrderDoReq confirmOrderDoReq, BlockException e){
        LOG.info("限流了");
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }
}
