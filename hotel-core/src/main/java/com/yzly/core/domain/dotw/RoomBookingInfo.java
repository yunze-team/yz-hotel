package com.yzly.core.domain.dotw;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 根据入住日期，费率选择而确定的房态详细数据集
 * @author lazyb
 * @create 2019/12/2
 * @desc
 **/
@Document(collection = "dotw_room_booking_info")
@Data
@NoArgsConstructor
@ToString
public class RoomBookingInfo {

    @Id
    private ObjectId id;

    private String hotelId;
    private String specials;
    private String name;
    private String roomTypeCode;
    private String twin;
    private String roomInfo;
    private String rateDescription;
    private String minStay;
    private String dateApplyMinStay;
    private String allowsSpecialRequests;
    private String allowsBeddingPreference;
    private String withinCancellationDeadline;
    private String dates;
    private String allowsExtraMeals;
    private String tariffNotes;
    private String rateType;
    private String rateBasisId;
    private String passengerNamesRequiredForBooking;
    private String total;
    private String isBookable;
    private String leftToSell;
    private String allocationDetails;
    private String status;
    private String fromDate;
    private String toDate;
    private String cancellationRules;

}
