package com.yzly.core.repository.meit;

import com.yzly.core.domain.meit.dto.MeitResult;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author lazyb
 * @create 2020/2/19
 * @desc
 **/
@Repository
public interface MeitResultRepository extends MongoRepository<MeitResult, ObjectId> {

    List<MeitResult> findAllByCreatedAtBefore(Date day);

}
