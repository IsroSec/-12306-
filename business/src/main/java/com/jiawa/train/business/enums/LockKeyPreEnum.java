package com.jiawa.train.business.enums;

/**
 * ClassName: LockKeyPreEnum
 * Package: com.jiawa.train.business.enums
 * Description:
 *
 * @Author GalSec
 * @Create 2025/1/24 21:20
 * @Version 1.0
 */
public enum LockKeyPreEnum {
    CONFIRM_ORDER("LOCK_CONFIRM_ORDER","一等座"),
    SK_TOKEN("LOCK_SK_TOKEN","二等座");
    private final String code;
    private final String desc;
    LockKeyPreEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public String getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
}
