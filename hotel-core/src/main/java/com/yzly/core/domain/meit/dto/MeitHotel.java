package com.yzly.core.domain.meit.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 美团酒店信息基础视图类
 * @author lazyb
 * @create 2019/12/23
 * @desc
 **/
@Getter
@Setter
public class MeitHotel {

    private String  hotelId;// 酒店ID
    private Integer cityId;// 城市ID，参照美团城市列表
    private String nameCn;// 酒店中文名称，非必输
    private String nameEn;// 酒店英文名称
    private String nameLocal;// 本地语言酒店名称，非必输
    private String addressCn;// 酒店中文地址，非必输
    private String addressEn;// 酒店英文地址
    private String addressLocal;// 本地语言的地址
    private String tel;// 联系电话
    private String longitude;// 经度
    private String latitude;// 纬度
    private Integer regionType;// 区域，1：内地，2：境外

}
