package com.jiawa.train.business.controller;

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
@RequestMapping("/business")
public class TestController {
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
