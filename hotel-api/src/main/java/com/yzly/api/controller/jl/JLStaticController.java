package com.yzly.api.controller.jl;

import com.yzly.api.common.JLHandler;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**捷旅api-静态信息接口
 * @author lazyb
 * @create 2020/4/20
 * @desc
 **/
@RequestMapping("/api/jl")
@RestController
@CommonsLog
public class JLStaticController {

    @Autowired
    private JLHandler jlHandler;

}
