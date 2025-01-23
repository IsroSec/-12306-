package com.jiawa.train.batch.feign;

import com.jiawa.train.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;

/**
 * ClassName: BusinessFeign
 * Package: com.jiawa.train.batch.feign
 * Description:
 *
 * @Author GalSec
 * @Create 2025/1/12 15:12
 * @Version 1.0
 */
@FeignClient("business")
//@FeignClient(name = "business",url = "http://127.0.0.1:8002/business")
public interface BusinessFeign {
    @GetMapping("/business/hello")
    String hello();

    @GetMapping("/business/admin/daily-train/gen-daily/{date}")
    public CommonResp<Object> genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);
}
