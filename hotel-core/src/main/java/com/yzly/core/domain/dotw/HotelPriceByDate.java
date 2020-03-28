package com.yzly.core.domain.dotw;

import com.alibaba.fastjson.JSONObject;
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
 * @create 2020/3/28
 * @desc
 **/
@Document(collection = "dotw_hotel_price_date")
@Data
@NoArgsConstructor
@ToString
public class HotelPriceByDate {

    @Id
    private ObjectId id;

    private String hotelCode;// dotw酒店code

    private String fromDate;// 开始时间
    private String toDate;// 截止时间

    private JSONObject roomPriceJson;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

}
