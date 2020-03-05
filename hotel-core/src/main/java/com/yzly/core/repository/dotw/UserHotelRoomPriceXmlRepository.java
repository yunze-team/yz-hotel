package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.UserHotelRoomPriceXml;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2020/3/5
 * @desc
 **/
@Repository
public interface UserHotelRoomPriceXmlRepository extends MongoRepository<UserHotelRoomPriceXml, ObjectId> {
}
