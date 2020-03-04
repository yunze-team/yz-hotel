package com.yzly.api.interceptor;

import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author lazyb
 * @create 2020/3/5
 * @desc
 **/
@Component
public class LogInterceptor extends HandlerInterceptorAdapter {

    /**
     * 日志跟踪标识
     */
    private static final String TRACE_ID = "TRACE_ID";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = UUID.randomUUID().toString();
        if (StringUtils.isEmpty(MDC.get(TRACE_ID))) {
            MDC.put(TRACE_ID, traceId);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        MDC.remove(TRACE_ID);
    }

}
