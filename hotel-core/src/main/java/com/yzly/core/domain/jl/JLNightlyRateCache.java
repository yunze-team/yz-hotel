package com.yzly.core.domain.jl;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

/**
 * @author lazyb
 * @create 2020/6/29
 * @desc
 **/
@Document(collection = "jl_nightly_rate")
@Data
@NoArgsConstructor
@ToString
public class JLNightlyRateCache {

    @Id
    private ObjectId id;

    private Integer hotelId;// 酒店编号

    private String roomTypeId;// 房型编号

    private String ratePlanKeyId;// 唯一产品编号

    private String formulaTypen;// 配额类型

    private String date;// 日期

    private Double cose;// 价格

    private Integer status;// 房态，1保留房，2待查，3满房，4限时确认

    private Integer currentAlloment;// 库存数量

    private Integer breakfast;// 早餐

    private String bookingRuleId;// 预定条款编号

    private String refundRuleId;// 取消条款编号

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

}
