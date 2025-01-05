package com.jiawa.train.business.req;

import com.jiawa.train.common.req.PageReq;

public class TrainCarriageQueryReq extends PageReq {

    private String trainCode;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TrainCarriageQueryReq{");
        sb.append("trainCode='").append(trainCode).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

}
