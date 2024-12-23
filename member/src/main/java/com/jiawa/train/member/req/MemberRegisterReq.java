package com.jiawa.train.member.req;

/**
 * ClassName: MemberRegisterReq
 * Package: com.jiawa.train.member.req
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/23 16:58
 * @Version 1.0
 */
public class MemberRegisterReq {
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "MemberRegisterReq{" +
                "mobile='" + mobile + '\'' +
                '}';
    }
}
