package com.yzly.core.domain.meit.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * meit订单创建参数视图中的客人信息视图
 * @author lazyb
 * @create 2020/1/2
 * @desc
 **/
@Setter
@Getter
public class GuestInfo {

    private Integer seq;// 客人序号
    private Integer roomSeq;// 房间序号
    private String firstName;// 名
    private String lastName;// 姓
    private String gender;// 性别

}
