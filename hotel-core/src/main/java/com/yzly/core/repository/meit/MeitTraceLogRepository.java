package com.yzly.core.repository.meit;

import com.yzly.core.domain.meit.MeitTraceLog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author lazyb
 * @create 2019/12/20
 * @desc
 **/
@Repository
public interface MeitTraceLogRepository extends MongoRepository<MeitTraceLog, ObjectId> {

    List<MeitTraceLog> findAllByCreatedAtBefore(Date day);

    void deleteAllByCreatedAtBefore(Date day);

}
