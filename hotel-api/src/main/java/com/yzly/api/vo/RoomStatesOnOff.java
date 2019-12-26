package com.yzly.api.vo;

public class RoomStatesOnOff {

    private String belongPartnerId;

    private String activeCode;

    private String poiId;

    private String roomType;

    private  Integer breakfastNum;

    private  Integer status;

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

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Integer getBreakfastNum() {
        return breakfastNum;
    }

    public void setBreakfastNum(Integer breakfastNum) {
        this.breakfastNum = breakfastNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
