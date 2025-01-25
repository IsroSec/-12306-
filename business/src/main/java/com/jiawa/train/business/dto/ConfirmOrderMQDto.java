package com.jiawa.train.business.dto;

import java.util.Date;

/**
 * ClassName: ConfirmOrderMQDto
 * Package: com.jiawa.train.business.dto
 * Description:
 *
 * @Author GalSec
 * @Create 2025/1/25 21:16
 * @Version 1.0
 */
public class ConfirmOrderMQDto {
    private String trainCode;
    private Date date;
    private String MDC;

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMDC() {
        return MDC;
    }

    public void setMDC(String MDC) {
        this.MDC = MDC;
    }
}
