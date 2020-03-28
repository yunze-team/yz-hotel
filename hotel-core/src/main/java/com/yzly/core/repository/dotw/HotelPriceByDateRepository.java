package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.HotelPriceByDate;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2020/3/28
 * @desc
 **/
@Repository
public interface HotelPriceByDateRepository extends MongoRepository<HotelPriceByDate, ObjectId> {

    HotelPriceByDate findByHotelCodeAndFromDateAndToDate(String hotelCode, String fromDate, String toDate);

}
