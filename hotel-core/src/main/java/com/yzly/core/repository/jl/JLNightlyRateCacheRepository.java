package com.yzly.core.repository.jl;


import com.yzly.core.domain.jl.JLNightlyRateCache;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lazyb
 * @create 2020/6/29
 * @desc
 **/
@Repository
public interface JLNightlyRateCacheRepository extends MongoRepository<JLNightlyRateCache, ObjectId> {

    List<JLNightlyRateCache> findAllByRatePlanKeyIdAndDate(String ratePlanKeyId, String date);

}
