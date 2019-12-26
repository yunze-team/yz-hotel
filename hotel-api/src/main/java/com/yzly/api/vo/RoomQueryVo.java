package com.yzly.api.vo;

public class RoomQueryVo {
    private String belongPartnerId;

    private String activeCode;

    private int breakfastNum;

    private String poiId;

    private String roomType;

    private String startDate;

    private String endDate;

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

    public int getBreakfastNum() {
        return breakfastNum;
    }

    public void setBreakfastNum(int breakfastNum) {
        this.breakfastNum = breakfastNum;
    }

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
