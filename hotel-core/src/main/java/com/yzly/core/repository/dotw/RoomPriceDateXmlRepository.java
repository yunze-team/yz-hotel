package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.RoomPriceDateXml;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lazyb
 * @create 2020/3/27
 * @desc
 **/
@Repository
public interface RoomPriceDateXmlRepository extends MongoRepository<RoomPriceDateXml, ObjectId> {

    RoomPriceDateXml findByRoomTypeCodeAndRateBasisAndFromDateAndToDate(String roomTypeCode, String rateBasis, String fromDate, String toDate);

    List<RoomPriceDateXml> findAllByHotelCodeAndFromDateAndToDate(String hotelCode, String fromDate, String toDate);

    List<RoomPriceDateXml> findAllByRoomTypeCodeAndFromDateIsGreaterThanEqualAndToDateIsLessThanEqualOrderByFromDateAsc(String roomTypeCode, String fromDate, String toDate);

    RoomPriceDateXml findByRoomTypeCodeAndRateBasisIdAndFromDateAndToDate(String roomTypeCode, String rateBasisId, String fromDate, String toDate);

}
