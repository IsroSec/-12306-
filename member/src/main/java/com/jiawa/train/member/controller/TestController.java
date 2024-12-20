package com.jiawa.train.member.controller;

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
public class TestController {
    @GetMapping("/hello")
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
