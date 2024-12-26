package com.jiawa.train.member.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * ClassName: MemberLoginReq
 * Package: com.jiawa.train.member.req
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/26 18:50
 * @Version 1.0
 */
public class MemberLoginReq {

    @NotBlank(message = "【手机号】不能为空")
    private String mobile;
    @NotBlank(message = "【短信验证码】不能为空")
    private String code;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MemberLoginReq{");
        sb.append("mobile='").append(mobile).append('\'');
        sb.append(", code='").append(code).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
