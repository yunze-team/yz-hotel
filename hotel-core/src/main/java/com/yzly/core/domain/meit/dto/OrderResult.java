package com.yzly.core.domain.meit.dto;

import com.yzly.core.enums.meit.PlatformOrderStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * meit创建订单结果数据返回集
 * @author lazyb
 * @create 2020/1/3
 * @desc
 **/
@Setter
@Getter
public class OrderResult {

    private String partnerOrderId;// 供应商订单id
    private String agentOrderId;// 渠道商订单id
    private List<String> confirmationNumbers;// 房间确认号
    private Long orderId;// 美团订单id
    private PlatformOrderStatusEnum orderStatus;// 订单状态
    private String orderMessage;// 订单说明
    private Integer totalPrice;// 订单总金额，单位分
    private Integer penalty;// 罚金，取消状态的订单使用

}
