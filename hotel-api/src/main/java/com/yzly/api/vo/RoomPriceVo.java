package com.yzly.api.vo;

import java.util.List;

public class RoomPriceVo {

    private String belongPartnerId;

    private String activeCode;

    private String poiId;

    private Integer breakfastNum;

    private List<RoomSinglePriceVo> roomSinglePriceVos;

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

    public List<RoomSinglePriceVo> getRoomSinglePriceVos() {
        return roomSinglePriceVos;
    }

    public void setRoomSinglePriceVos(List<RoomSinglePriceVo> roomSinglePriceVos) {
        this.roomSinglePriceVos = roomSinglePriceVos;
    }

    public Integer getBreakfastNum() {
        return breakfastNum;
    }

    public void setBreakfastNum(Integer breakfastNum) {
        this.breakfastNum = breakfastNum;
    }
}
