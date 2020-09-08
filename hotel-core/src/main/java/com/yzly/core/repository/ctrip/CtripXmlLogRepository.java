package com.yzly.core.repository.ctrip;

import com.yzly.core.domain.ctrip.CtripXmlLog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2020/9/8
 * @desc
 **/
@Repository
public interface CtripXmlLogRepository extends MongoRepository<CtripXmlLog, ObjectId> {
}
