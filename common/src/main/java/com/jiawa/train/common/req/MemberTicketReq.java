package com.jiawa.train.common.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class MemberTicketReq {


    /**
     * 会员id
     */
       //这里针对date和time
    @NotNull(message = "【会员id】不能为空")
    private Long memberId;

    /**
     * 乘客id
     */
       //这里针对date和time
    @NotNull(message = "【乘客id】不能为空")
    private Long passengerId;

    /**
     * 乘客姓名
     */
       //这里针对date和time
    @NotBlank(message = "【乘客姓名】不能为空")
    private String passengerName;

    /**
     * 日期
     */
       //这里针对date和time
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "【日期】不能为空")
    private Date trainDate;

    /**
     * 车次编号
     */
       //这里针对date和time
    @NotBlank(message = "【车次编号】不能为空")
    private String trainCode;

    /**
     * 箱序
     */
       //这里针对date和time
    @NotNull(message = "【箱序】不能为空")
    private Integer carriageIndex;

    /**
     * 排号|01, 02
     */
       //这里针对date和time
    @NotBlank(message = "【排号】不能为空")
    private String seatRow;

    /**
     * 列号|枚举[SeatColEnum]
     */
       //这里针对date和time
    @NotBlank(message = "【列号】不能为空")
    private String seatCol;

    /**
     * 出发站
     */
       //这里针对date和time
    @NotBlank(message = "【出发站】不能为空")
    private String startStation;

    /**
     * 出发时间
     */
       //这里针对date和time
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "【出发时间】不能为空")
    private Date startTime;

    /**
     * 到达站
     */
       //这里针对date和time
    @NotBlank(message = "【到达站】不能为空")
    private String endStation;

    /**
     * 到站时间
     */
       //这里针对date和time
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "【到站时间】不能为空")
    private Date endTime;

    /**
     * 座位类型|枚举[SeatTypeEnum]
     */
       //这里针对date和time
    @NotBlank(message = "【座位类型】不能为空")
    private String seatType;

    /**
     * 新增时间
     */
       //这里针对date和time
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 修改时间
     */
       //这里针对date和time
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MemberTicketReq{");
        sb.append("memberId=").append(memberId);
        sb.append(", passengerId=").append(passengerId);
        sb.append(", passengerName='").append(passengerName).append('\'');
        sb.append(", trainDate=").append(trainDate);
        sb.append(", trainCode='").append(trainCode).append('\'');
        sb.append(", carriageIndex=").append(carriageIndex);
        sb.append(", seatRow='").append(seatRow).append('\'');
        sb.append(", seatCol='").append(seatCol).append('\'');
        sb.append(", startStation='").append(startStation).append('\'');
        sb.append(", startTime=").append(startTime);
        sb.append(", endStation='").append(endStation).append('\'');
        sb.append(", endTime=").append(endTime);
        sb.append(", seatType='").append(seatType).append('\'');
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append('}');
        return sb.toString();
    }

    public Date getTrainDate() {
        return trainDate;
    }

    public void setTrainDate(Date trainDate) {
        this.trainDate = trainDate;
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    public Integer getCarriageIndex() {
        return carriageIndex;
    }

    public void setCarriageIndex(Integer carriageIndex) {
        this.carriageIndex = carriageIndex;
    }


    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(String seatRow) {
        this.seatRow = seatRow;
    }

    public String getSeatCol() {
        return seatCol;
    }

    public void setSeatCol(String seatCol) {
        this.seatCol = seatCol;
    }

    public String getStartStation() {
        return startStation;
    }

    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public void setEndStation(String endStation) {
        this.endStation = endStation;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
