package com.jiawa.train.common.exception;

/**
 * ClassName: BusinessExceptionEnum
 * Package: com.jiawa.train.common.exception
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/23 19:50
 * @Version 1.0
 */
//构造一个自定义异常类的枚举类
public enum BusinessExceptionEnum {
    MEMBER_MOBILE_EXIST("手机号已存在");
    private String desc;

    BusinessExceptionEnum(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "BusinessExceptionEnum{" +
                "desc='" + desc + '\'' +
                '}';
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
