package com.yzly.api.service;





import com.yzly.api.constants.xiecheng.AppHeader;
import com.yzly.api.constants.xiecheng.CommonRequest;
import com.yzly.api.constants.xiecheng.CommonResponse;
import com.yzly.api.constants.xiecheng.XmlResponse;
import com.yzly.api.exception.YzException;

import java.util.Map;

/**
 * @Description:@RestHttpService提供http服务
 * @Auther frank
 * version V1.0
 * @createtime 2019-10-30 10:06
 **/
public interface RestHttpService {

    CommonResponse doGet(String path, Map map) throws YzException;

    CommonResponse doGet(String path) throws YzException;

    CommonResponse doPost(String path, Map map) throws YzException;

    CommonResponse doPost(String path) throws YzException;

    CommonResponse doPost(String path, CommonRequest commonRequest) throws YzException;

    CommonResponse doPost(String path, CommonRequest commonRequest, AppHeader appHeader) throws YzException;

    XmlResponse doPagePost(String path, CommonRequest commonRequest, AppHeader appHeader) throws YzException;

    CommonResponse doNewPost(CommonRequest commonRequest, AppHeader appHeader)
            throws YzException;
}
