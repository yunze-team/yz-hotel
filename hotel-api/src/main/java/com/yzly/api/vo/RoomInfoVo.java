package com.yzly.api.vo;

public class RoomInfoVo {
    private String belongPartnerId;

    private String activeCode;

    private String poiId;

    private String ruleStartDate;

    private String ruleEndDate;

    private String roomType;

    private int breakfastNum;

    private int earliestBookingDays;

    private int earliestBookingHours;

    private int latestBookingDays;

    private int latestBookingHours;

    private int serialCheckinMin;

    private int serialCheckinMax;

    private int roomCountMin;

    private int roomCountMax;

    private int allowCancel;


    private int moveupCancelDays;

    private int moveupCancelHour;

    public String getBelongPartnerId() {
        return belongPartnerId;
    }

    public void setBelongPartnerId(String belongPartnerId) {
        this.belongPartnerId = belongPartnerId;
    }

    public String getActiveCode() {
        return activeCode;
    }

    public void setActiveCode(String activeCode) {
        this.activeCode = activeCode;
    }

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public String getRuleStartDate() {
        return ruleStartDate;
    }

    public void setRuleStartDate(String ruleStartDate) {
        this.ruleStartDate = ruleStartDate;
    }

    public String getRuleEndDate() {
        return ruleEndDate;
    }

    public void setRuleEndDate(String ruleEndDate) {
        this.ruleEndDate = ruleEndDate;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getBreakfastNum() {
        return breakfastNum;
    }

    public void setBreakfastNum(int breakfastNum) {
        this.breakfastNum = breakfastNum;
    }

    public int getEarliestBookingDays() {
        return earliestBookingDays;
    }

    public void setEarliestBookingDays(int earliestBookingDays) {
        this.earliestBookingDays = earliestBookingDays;
    }

    public int getEarliestBookingHours() {
        return earliestBookingHours;
    }

    public void setEarliestBookingHours(int earliestBookingHours) {
        this.earliestBookingHours = earliestBookingHours;
    }

    public int getLatestBookingDays() {
        return latestBookingDays;
    }

    public void setLatestBookingDays(int latestBookingDays) {
        this.latestBookingDays = latestBookingDays;
    }

    public int getLatestBookingHours() {
        return latestBookingHours;
    }

    public void setLatestBookingHours(int latestBookingHours) {
        this.latestBookingHours = latestBookingHours;
    }

    public int getSerialCheckinMin() {
        return serialCheckinMin;
    }

    public void setSerialCheckinMin(int serialCheckinMin) {
        this.serialCheckinMin = serialCheckinMin;
    }

    public int getSerialCheckinMax() {
        return serialCheckinMax;
    }

    public void setSerialCheckinMax(int serialCheckinMax) {
        this.serialCheckinMax = serialCheckinMax;
    }

    public int getRoomCountMin() {
        return roomCountMin;
    }

    public void setRoomCountMin(int roomCountMin) {
        this.roomCountMin = roomCountMin;
    }

    public int getRoomCountMax() {
        return roomCountMax;
    }

    public void setRoomCountMax(int roomCountMax) {
        this.roomCountMax = roomCountMax;
    }

    public int getAllowCancel() {
        return allowCancel;
    }

    public void setAllowCancel(int allowCancel) {
        this.allowCancel = allowCancel;
    }

    public int getMoveupCancelDays() {
        return moveupCancelDays;
    }

    public void setMoveupCancelDays(int moveupCancelDays) {
        this.moveupCancelDays = moveupCancelDays;
    }

    public int getMoveupCancelHour() {
        return moveupCancelHour;
    }

    public void setMoveupCancelHour(int moveupCancelHour) {
        this.moveupCancelHour = moveupCancelHour;
    }
}
