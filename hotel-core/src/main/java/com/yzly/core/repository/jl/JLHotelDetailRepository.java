package com.yzly.core.repository.jl;

import com.yzly.core.domain.jl.JLHotelDetail;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author lazyb
 * @create 2020/5/18
 * @desc
 **/
public interface JLHotelDetailRepository extends MongoRepository<JLHotelDetail, ObjectId> {

    JLHotelDetail findByHotelId(Integer hotelId);

}
