package com.yzly.core.service;

import com.yzly.core.domain.event.EventAttr;
import com.yzly.core.repository.event.EventAttrRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyb
 * @create 2020/3/8
 * @desc
 **/
@Service
@CommonsLog
public class EventAttrService {

    @Autowired
    private EventAttrRepository eventAttrRepository;

    /**
     * 分页查找所有的配置项
     * @param page
     * @param size
     * @return
     */
    public Page<EventAttr> findAllByPage(int page, int size) {
        Pageable pageable = new PageRequest(page - 1, size);
        return eventAttrRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);
    }

    /**
     * 按照数值修改配置项
     * @param id
     * @param value
     * @return
     */
    public EventAttr editByValue(Long id, String value) {
        EventAttr attr = eventAttrRepository.findOne(id);
        attr.setEventValue(value);
        return eventAttrRepository.save(attr);
    }

}
