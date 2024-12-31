package com.jiawa.train.member.controller;

import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.resp.CommonResp;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.member.domain.${Domain};
import com.jiawa.train.member.req.${Domain}QueryReq;
import com.jiawa.train.member.req.${Domain}SaveReq;
import com.jiawa.train.member.resp.${Domain}QueryResp;
import com.jiawa.train.member.service.${Domain}Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: ${Domain}Controller
 * Package: com.jiawa.train.member.controller
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/29 16:25
 * @Version 1.0
 */
@RestController
@RequestMapping("/${domain}")
public class ${Domain}Controller {

    @Autowired
    private ${Domain}Service ${domain}Service;
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody ${Domain}SaveReq ${domain}SaveReq) {
        ${domain}Service.save(${domain}SaveReq);
        return new CommonResp();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<${Domain}QueryResp>> queryList(@Valid  ${Domain}QueryReq ${domain}QueryReq) {
        ${domain}QueryReq.setMemberId(LoginMemberContext.getId());
        return new CommonResp<>(${domain}Service.queryList(${domain}QueryReq));
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        ${domain}Service.delete(id);
        return new CommonResp<>();
    }
}
