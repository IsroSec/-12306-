package com.jiawa.train.member.controller.admin;

import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.req.MemberTicketReq;
import com.jiawa.train.common.resp.CommonResp;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.member.domain.Ticket;
import com.jiawa.train.member.req.TicketQueryReq;
import com.jiawa.train.member.req.TicketSaveReq;
import com.jiawa.train.member.resp.TicketQueryResp;
import com.jiawa.train.member.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: TicketController
 * Package: com.jiawa.train.member.controller
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:25
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/ticket")
public class TicketAdminController {

    @Autowired
    private TicketService ticketService;
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody MemberTicketReq req) {
        ticketService.save(req);
        return new CommonResp();
    }


}
