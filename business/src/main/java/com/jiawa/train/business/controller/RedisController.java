package com.jiawa.train.business.controller;

import com.jiawa.train.business.service.AfterConfirmOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: RedisController
 * Package: com.jiawa.train.business.controller
 * Description:
 *
 * @Author GalSec
 * @Create 2025/1/19 18:48
 * @Version 1.0
 */
@RestController
public class RedisController {
    private static final Logger LOG = LoggerFactory.getLogger(AfterConfirmOrderService.class);

    @Autowired
    private RedisTemplate redisTemplate;
    @RequestMapping("/redis/set/{key}/{value}")
    public String set(@PathVariable String key, @PathVariable String value){
        redisTemplate.opsForValue().set(key, value);
        LOG.info("key:"+key+" value:"+value);
        return "success";
    }
    @RequestMapping("/redis/get/{key}")
    public String get(@PathVariable String key){
        String value = (String) redisTemplate.opsForValue().get(key);
        LOG.info("key:"+key+" value:"+value);
        return value;
    }
}
