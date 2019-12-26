package com.yzly.core.domain.meit.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * meit房型基础同步api，单个房型
 * @author lazyb
 * @create 2019/12/26
 * @desc
 **/
@Getter
@Setter
public class RoomTypeBasic {

    private String roomNameCn;
    private String roomNameEn;
    private String roomNameLocal;
    private String roomId;

}
