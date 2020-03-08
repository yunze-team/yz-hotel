package com.yzly.admin.controller;

import com.yzly.admin.domain.ReturnT;
import com.yzly.core.domain.event.EventAttr;
import com.yzly.core.service.EventAttrService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lazyb
 * @create 2020/3/8
 * @desc
 **/
@RestController
@CommonsLog
public class EventAttrController {

    @Autowired
    private EventAttrService eventAttrService;

    /**
     * 分页展示attr
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/event/all")
    public Map<String, Object> getPageAttr(int page, int rows) {
        Map<String, Object> resMap = new HashMap<>();
        Page<EventAttr> epage = eventAttrService.findAllByPage(page, rows);
        resMap.put("total", epage.getTotalElements());
        resMap.put("rows", epage.getContent());
        return resMap;
    }

    /**
     * 修改attr
     * @param id
     * @param value
     * @return
     */
    @PostMapping("/event/edit")
    public ReturnT editAttr(@RequestParam(name = "id") Long id,
                            @RequestParam(name = "eventValue") String value) {
        EventAttr attr = eventAttrService.editByValue(id, value);
        return new ReturnT(attr);
    }

}
