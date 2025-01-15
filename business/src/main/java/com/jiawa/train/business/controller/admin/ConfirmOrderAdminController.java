package com.jiawa.train.business.controller.admin;

import com.jiawa.train.common.resp.CommonResp;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.business.req.ConfirmOrderQueryReq;
import com.jiawa.train.business.req.ConfirmOrderDoReq;
import com.jiawa.train.business.resp.ConfirmOrderQueryResp;
import com.jiawa.train.business.service.ConfirmOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: ConfirmOrderController
 * Package: com.jiawa.train.business.controller
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:25
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/confirm-order")
public class ConfirmOrderAdminController {

    @Autowired
    private ConfirmOrderService confirmOrderService;
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody ConfirmOrderDoReq confirmOrderDoReq) {
        confirmOrderService.save(confirmOrderDoReq);
        return new CommonResp();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<ConfirmOrderQueryResp>> queryList(@Valid  ConfirmOrderQueryReq confirmOrderQueryReq) {
        //这里拿到localthread的id
        return new CommonResp<>(confirmOrderService.queryList(confirmOrderQueryReq));
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        confirmOrderService.delete(id);
        return new CommonResp<>();
    }
}
