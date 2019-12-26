package com.yzly.api.vo;

public class HotelInfoVo {

    private String belongPartnerIds;
    private String poiId;
    private String pointName;
    private String phone;
    private String address;
    private String longitude;
    private String latitude;
    private Integer cityId;
    private Integer regionType;

    public HotelInfoVo(String poiId, String pointName, String phone, String address, String longitude, String latitude, Integer cityId) {
        this.poiId = poiId;
        this.pointName = pointName;
        this.phone = phone;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.cityId = cityId;
    }

    public String getBelongPartnerIds() {
        return belongPartnerIds;
    }

    public void setBelongPartnerIds(String belongPartnerIds) {
        this.belongPartnerIds = belongPartnerIds;
    }

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getRegionType() {
        return regionType;
    }

    public void setRegionType(Integer regionType) {
        this.regionType = regionType;
    }
}
