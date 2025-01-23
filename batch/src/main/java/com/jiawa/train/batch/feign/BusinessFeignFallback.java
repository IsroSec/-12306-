package com.jiawa.train.batch.feign;

import com.jiawa.train.common.resp.CommonResp;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * ClassName: BusinessFeignFallback
 * Package: com.jiawa.train.batch.feign
 * Description:
 *
 * @Author GalSec
 * @Create 2025/1/23 18:48
 * @Version 1.0
 */
@Component
public class BusinessFeignFallback implements BusinessFeign{
    @Override
    public String hello() {
        return "fallback";
    }

    @Override
    public CommonResp<Object> genDaily(Date date) {
        return null;
    }
}
