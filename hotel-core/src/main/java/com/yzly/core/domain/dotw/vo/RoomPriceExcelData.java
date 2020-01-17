package com.yzly.core.domain.dotw.vo;

import com.yzly.core.domain.dotw.RoomType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author lazyb
 * @create 2020/1/17
 * @desc
 **/
@Setter
@Getter
public class RoomPriceExcelData {

    private String hotelCode;
    private String hotelName;
    private List<RoomType> roomTypeList;

}
