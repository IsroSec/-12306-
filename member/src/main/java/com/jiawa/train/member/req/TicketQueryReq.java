package com.jiawa.train.member.req;

import com.jiawa.train.common.req.PageReq;

public class TicketQueryReq extends PageReq {
    public Long memberId;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TicketQueryReq{");
        sb.append("memberId='").append(memberId).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
