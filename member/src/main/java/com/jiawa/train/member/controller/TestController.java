package com.jiawa.train.member.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: TestController
 * Package: com.jiawa.train.member.controller
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/20 15:35
 * @Version 1.0
 */
@RestController
@RefreshScope
public class TestController {
    @GetMapping("/hello")
    public String main(String args[]) {
        return "hello  nacos";
    }
    @Value("${test.nacos}")
    private String name;
    @GetMapping("/test")
    public String test() {
        return name;
    }
}
