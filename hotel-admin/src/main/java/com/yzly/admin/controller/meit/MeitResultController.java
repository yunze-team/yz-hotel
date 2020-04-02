package com.yzly.admin.controller.meit;

import com.yzly.core.domain.meit.dto.MeitResult;
import com.yzly.core.domain.meit.query.MeitResultQuery;
import com.yzly.core.service.meit.MeitQueryService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lazyb
 * @create 2020/4/2
 * @desc
 **/
@RestController
@CommonsLog
public class MeitResultController {

    @Autowired
    private MeitQueryService meitQueryService;

    @GetMapping("/meit/result")
    public Map<String, Object> getPageResult(int page, int rows, MeitResultQuery meitResultQuery) {
        Map<String, Object> resMap = new HashMap<>();
        Page<MeitResult> mpage = meitQueryService.findAllByPageQuery(page, rows, meitResultQuery);
        resMap.put("total", mpage.getTotalElements());
        resMap.put("rows", mpage.getContent());
        return resMap;
    }

}
