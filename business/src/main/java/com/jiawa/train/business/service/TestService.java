package com.jiawa.train.business.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.stereotype.Service;

/**
 * ClassName: TestService
 * Package: com.jiawa.train.business.service
 * Description:
 *
 * @Author GalSec
 * @Create 2025/1/23 17:57
 * @Version 1.0
 */
@Service
public class TestService {
    @SentinelResource("hello2")
    public String hello2() throws InterruptedException {
        Thread.sleep(5000);
        return "hello222222";
    }
}
