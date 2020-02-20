package com.yzly.core.domain.dotw;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * dotw房型价格xml数据实体
 * @author lazyb
 * @create 2020/2/20
 * @desc
 **/
@Document(collection = "dotw_room_price_xml")
@Data
@NoArgsConstructor
@ToString
public class HotelRoomPriceXml {

    @Id
    private ObjectId id;

    private String hotelId;
    private String fromDate;
    private String toDate;
    private String currency;
    private String roomNumber;
    private String numberOfAdults;
    private String numberOfChildren;
    private String childrenAges;
    private String ratePlanCode;
    private String xmlResp;

}
