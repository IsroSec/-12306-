package com.jiawa.train.business.enums;

/**
 * ClassName: RocketMQTopicEnum
 * Package: com.jiawa.train.business.enums
 * Description:
 *
 * @Author GalSec
 * @Create 2025/1/25 20:26
 * @Version 1.0
 */
public enum RocketMQTopicEnum {
    CONFIRM_ORDER("CONFIRM_ORDER","确认订单排队");
    private String code;
    private String desc;
    RocketMQTopicEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RocketMQTopicEnum{");
        sb.append("code='").append(code).append('\'');
        sb.append(", desc='").append(desc).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
