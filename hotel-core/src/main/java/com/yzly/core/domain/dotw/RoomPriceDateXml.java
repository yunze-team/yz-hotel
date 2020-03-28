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
 * @create 2020/3/27
 * @desc
 **/
@Document(collection = "dotw_room_price_date_xml")
@Data
@NoArgsConstructor
@ToString
public class RoomPriceDateXml {

    @Id
    private ObjectId id;

    private String hotelCode;// dotw酒店code
    private String roomTypeCode;// 房型code
    private String roomName;// 房型名称
    private String total;// 总价
    private String rateBasis;// 适用价格
    private String rateBasisId;
    private String fromDate;// 开始时间
    private String toDate;// 截止时间

    private JSONObject rateJson;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

}
