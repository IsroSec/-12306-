package com.jiawa.train.batch.controller;

import com.jiawa.train.batch.config.BatchApplication;
import com.jiawa.train.batch.feign.BusinessFeign;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/batch")
public class TestController {
    private static final Logger LOG = LoggerFactory.getLogger(BatchApplication.class);
    @Resource
    BusinessFeign businessFeign;
    @GetMapping("/hello")
    public String hello() {
        String hello = businessFeign.hello();
        LOG.info(hello);
        return "hello batch "+hello;
    }
}
