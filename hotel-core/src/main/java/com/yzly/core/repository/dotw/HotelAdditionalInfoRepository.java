package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.HotelAdditionalInfo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2019/11/29
 * @desc
 **/
@Repository
public interface HotelAdditionalInfoRepository extends MongoRepository<HotelAdditionalInfo, ObjectId> {

    HotelAdditionalInfo findOneByHotelId(String hotelId);

}
