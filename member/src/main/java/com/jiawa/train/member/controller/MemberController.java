package com.jiawa.train.member.controller;

import com.jiawa.train.common.resp.CommonResp;
import com.jiawa.train.member.req.MemberRegisterReq;
import com.jiawa.train.member.req.MemberSendCodeReq;
import com.jiawa.train.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: MemberController
 * Package: com.jiawa.train.member.controller
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/23 5:16
 * @Version 1.0
 */
@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @GetMapping("/count")
    public CommonResp<Integer> count(){
        return new CommonResp<>(memberService.count());
    }


    @PostMapping("/register")
    public CommonResp<Long> register(@Validated MemberRegisterReq registerReq){
        return new CommonResp<>(memberService.register(registerReq));
    }

    @PostMapping("/send-code")
    public CommonResp sendCode(@Validated MemberSendCodeReq sendCodeReq){
        memberService.sendCode(sendCodeReq);
        return new CommonResp();
    }
}
