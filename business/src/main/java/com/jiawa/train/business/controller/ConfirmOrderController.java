package com.jiawa.train.business.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.jiawa.train.business.req.ConfirmOrderDoReq;
import com.jiawa.train.business.service.AfterConfirmOrderService;
import com.jiawa.train.business.service.ConfirmOrderService;
import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import com.jiawa.train.common.resp.CommonResp;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;

/**
 * ClassName: ConfirmOrderController
 * Package: com.jiawa.train.business.controller
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:25
 * @Version 1.0
 */
@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {
    private static final Logger LOG = LoggerFactory.getLogger(AfterConfirmOrderService.class);

    @Autowired
    private ConfirmOrderService confirmOrderService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    //接口资源名不要和接口路径一致，会导致限流后走不到降级方法中
    @SentinelResource(value = "confirmOrderDo", blockHandler = "confirmOrderDoBlock")
    @PostMapping("/do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq confirmOrderDoReq) throws Exception {
        //图片验证码校验
        String imageCode = confirmOrderDoReq.getImageCode();
        String imageCodeToken = confirmOrderDoReq.getImageCodeToken();
        String imageCodeRedis = redisTemplate.opsForValue().get(imageCodeToken);
        LOG.info("从redis中获取到验证码：{}",imageCodeRedis);
        if (ObjectUtil.isEmpty(imageCodeRedis)){
            return new CommonResp<>(false,"验证码已过期",null);
        }
        //验证码校验，大小写忽略，提升体验，比如Oo Vv Ww容易混淆
        if (!imageCode.equalsIgnoreCase(imageCodeRedis)){
            return new CommonResp<>(false,"验证码错误",null);
        }else {
            //验证通过，移除验证码
            redisTemplate.delete(imageCodeToken);
        }
        confirmOrderDoReq.setMemberId(LoginMemberContext.getId());
        confirmOrderService.doConfirm(confirmOrderDoReq);
        return new CommonResp();
    }


    private CommonResp<Object> confirmOrderDoBlock(ConfirmOrderDoReq confirmOrderDoReq, BlockException e){
      LOG.info("controller限流了");
      throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }
}
