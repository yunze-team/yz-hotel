package com.yzly.core.domain.meit.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * meit房型基础api，单个酒店房型
 * @author lazyb
 * @create 2019/12/26
 * @desc
 **/
@Setter
@Getter
public class HotelRoomTypeBasic {

    private String hotelId;
    private List<RoomTypeBasic> roomTypeBasics;

}
