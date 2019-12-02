package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.RoomBookingInfo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author lazyb
 * @create 2019/12/2
 * @desc
 **/
public interface RoomBookingInfoRepository extends MongoRepository<RoomBookingInfo, ObjectId> {

    RoomBookingInfo findByAllocationDetails(String allocationDetails);

}
