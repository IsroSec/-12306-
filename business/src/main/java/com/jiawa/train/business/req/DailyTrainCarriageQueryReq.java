package com.jiawa.train.business.req;

import com.jiawa.train.common.req.PageReq;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DailyTrainCarriageQueryReq extends PageReq {

    private String trainCode;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DailyTrainCarriageQueryReq{");
        sb.append("trainCode='").append(trainCode).append('\'');
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }

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

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
}
