package com.yzly.core.repository.event;

import com.yzly.core.domain.event.EventAttr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2019/12/11
 * @desc
 **/
@Repository
public interface EventAttrRepository extends JpaRepository<EventAttr, Long> {

    EventAttr findByEventType(String eventType);

}
