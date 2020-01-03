package com.yzly.core.domain.meit.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * meit订单创建视图中的订单信息视图
 * @author lazyb
 * @create 2020/1/2
 * @desc
 **/
@Getter
@Setter
public class OrderInfo {

    private Long orderId;// 美团酒店id
    private String mobile;// 联系电话
    private String personName;// 联系人姓名

}
