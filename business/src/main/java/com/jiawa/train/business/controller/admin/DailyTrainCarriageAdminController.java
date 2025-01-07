package com.jiawa.train.business.controller.admin;

import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.resp.CommonResp;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.business.domain.DailyTrainCarriage;
import com.jiawa.train.business.req.DailyTrainCarriageQueryReq;
import com.jiawa.train.business.req.DailyTrainCarriageSaveReq;
import com.jiawa.train.business.resp.DailyTrainCarriageQueryResp;
import com.jiawa.train.business.service.DailyTrainCarriageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: DailyTrainCarriageController
 * Package: com.jiawa.train.business.controller
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:25
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/daily-train-carriage")
public class DailyTrainCarriageAdminController {

    @Autowired
    private DailyTrainCarriageService dailyTrainCarriageService;
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainCarriageSaveReq dailyTrainCarriageSaveReq) {
        dailyTrainCarriageService.save(dailyTrainCarriageSaveReq);
        return new CommonResp();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainCarriageQueryResp>> queryList(@Valid  DailyTrainCarriageQueryReq dailyTrainCarriageQueryReq) {
        //这里拿到localthread的id
        return new CommonResp<>(dailyTrainCarriageService.queryList(dailyTrainCarriageQueryReq));
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainCarriageService.delete(id);
        return new CommonResp<>();
    }
}
