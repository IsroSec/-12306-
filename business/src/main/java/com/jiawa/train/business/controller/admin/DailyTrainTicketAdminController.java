package com.jiawa.train.business.controller.admin;

import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.resp.CommonResp;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.business.domain.DailyTrainTicket;
import com.jiawa.train.business.req.DailyTrainTicketQueryReq;
import com.jiawa.train.business.req.DailyTrainTicketSaveReq;
import com.jiawa.train.business.resp.DailyTrainTicketQueryResp;
import com.jiawa.train.business.service.DailyTrainTicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: DailyTrainTicketController
 * Package: com.jiawa.train.business.controller
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:25
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/daily-train-ticket")
public class DailyTrainTicketAdminController {

    @Autowired
    private DailyTrainTicketService dailyTrainTicketService;
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainTicketSaveReq dailyTrainTicketSaveReq) {
        dailyTrainTicketService.save(dailyTrainTicketSaveReq);
        return new CommonResp();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid  DailyTrainTicketQueryReq dailyTrainTicketQueryReq) {
        //这里拿到localthread的id
        return new CommonResp<>(dailyTrainTicketService.queryList(dailyTrainTicketQueryReq));
    }

    @GetMapping("/query-list2")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList2(@Valid  DailyTrainTicketQueryReq dailyTrainTicketQueryReq) {
        //这里拿到localthread的id
        return new CommonResp<>(dailyTrainTicketService.queryList2(dailyTrainTicketQueryReq));
    }

    @GetMapping("/query-list3")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList3(@Valid  DailyTrainTicketQueryReq dailyTrainTicketQueryReq) {
        //这里拿到localthread的id
        return new CommonResp<>(dailyTrainTicketService.queryList3(dailyTrainTicketQueryReq));
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainTicketService.delete(id);
        return new CommonResp<>();
    }
}
