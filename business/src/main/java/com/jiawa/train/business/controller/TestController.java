package com.jiawa.train.business.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.jiawa.train.business.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: TestController
 * Package: com.jiawa.train.business.controller
 * Description:
 *
 * @Author GalSec
 * @Create 2025/1/2 23:49
 * @Version 1.0
 */
@RestController
public class TestController {
    @Autowired
    private TestService testService;
    @SentinelResource("hello")
    @GetMapping("/hello")
    public String hello() {
        return testService.hello2();
    }
    @SentinelResource("hello1")
    @GetMapping("/hello1")
    public String hello1() {
        return testService.hello2();
    }
}
