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
    MEMBER_MOBILE_EXIST("手机号已存在"),
    MEMBER_MOBILE_NOT_EXIST("手机号不存在"),
    MEMBER_MOBILE_CODE_ERROR("手机号验证码错误"),
    BUSINESS_STATION_NAME_UNIQUE_ERROR("车站已存在"),
    BUSINESS_TRAIN_CODE_UNIQUE_ERROR("车次编号已存在"),
    BUSINESS_TRAIN_STATION_INDEX_UNIQUE_ERROR("同车次站序已存在"),
    BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR("同车次站名已存在"),
    BUSINESS_TRAIN_CARRIAGE_INDEX_UNIQUE_ERROR("同车次厢号已存在"),
    CONFIRM_ORDER_TICKET_COUNT_ERROR("余票不足"),
    CONFIRM_ORDER_EXCEPTION("服务器忙，请稍后重试"),
    CONFIRM_ORDER_EXCEPTION_FAIL("抢票人数过多，请稍后重试"),
    CONFIRM_ORDER_FLOW_EXCEPTION("抢票人数过多，请稍后重试"),
    CONFIRM_ORDER_SK_FAIL("票已卖完");
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
