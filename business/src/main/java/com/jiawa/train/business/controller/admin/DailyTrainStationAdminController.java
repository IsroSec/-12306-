package com.jiawa.train.business.controller.admin;

import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.resp.CommonResp;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.business.domain.DailyTrainStation;
import com.jiawa.train.business.req.DailyTrainStationQueryReq;
import com.jiawa.train.business.req.DailyTrainStationSaveReq;
import com.jiawa.train.business.resp.DailyTrainStationQueryResp;
import com.jiawa.train.business.service.DailyTrainStationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: DailyTrainStationController
 * Package: com.jiawa.train.business.controller
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:25
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/daily-train-station")
public class DailyTrainStationAdminController {

    @Autowired
    private DailyTrainStationService dailyTrainStationService;
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainStationSaveReq dailyTrainStationSaveReq) {
        dailyTrainStationService.save(dailyTrainStationSaveReq);
        return new CommonResp();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainStationQueryResp>> queryList(@Valid  DailyTrainStationQueryReq dailyTrainStationQueryReq) {
        //这里拿到localthread的id
        return new CommonResp<>(dailyTrainStationService.queryList(dailyTrainStationQueryReq));
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainStationService.delete(id);
        return new CommonResp<>();
    }
}
