package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.HotelRoomPriceXml;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * @author lazyb
 * @create 2020/2/20
 * @desc
 **/
@Repository
public interface HotelRoomPriceXmlRepository extends MongoRepository<HotelRoomPriceXml, ObjectId> {

}
