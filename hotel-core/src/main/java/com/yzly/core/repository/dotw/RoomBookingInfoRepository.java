package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.RoomBookingInfo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2019/12/2
 * @desc
 **/
@Repository
public interface RoomBookingInfoRepository extends MongoRepository<RoomBookingInfo, ObjectId> {

    RoomBookingInfo findByAllocationDetails(String allocationDetails);

    RoomBookingInfo findByAllocationDetailsAndFromDateAndToDate(String allocationDetails, String fromDate, String toDate);

    RoomBookingInfo findByRoomTypeCodeAndFromDateAndToDate(String roomTypeCode, String fromDate, String toDate);

}
