package com.jiawa.train.batch.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ClassName: BusinessFeign
 * Package: com.jiawa.train.batch.feign
 * Description:
 *
 * @Author GalSec
 * @Create 2025/1/12 15:12
 * @Version 1.0
 */
//@FeignClient("business")
@FeignClient(name = "business",url = "http://127.0.0.1:8002/business/business")
public interface BusinessFeign {
    @GetMapping("/hello")
    String hello();
}
