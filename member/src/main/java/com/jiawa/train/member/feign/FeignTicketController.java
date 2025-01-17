package com.jiawa.train.member.feign;

import com.jiawa.train.common.req.MemberTicketReq;
import com.jiawa.train.common.resp.CommonResp;
import com.jiawa.train.member.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: FeignTicketController
 * Package: com.jiawa.train.member.feign
 * Description:
 *
 * @Author GalSec
 * @Create 2025/1/17 0:11
 * @Version 1.0
 */
@RestController
@RequestMapping("/feign/ticket")
public class FeignTicketController {
    @Autowired
    TicketService ticketService;
    @PostMapping("/save")
    public CommonResp<Object>save(@Valid @RequestBody MemberTicketReq req){
        ticketService.save(req);
        return new CommonResp();
    }
}
