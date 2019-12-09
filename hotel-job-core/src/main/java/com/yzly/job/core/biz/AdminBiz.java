package com.yzly.job.core.biz;

import com.yzly.job.core.biz.model.HandleCallbackParam;
import com.yzly.job.core.biz.model.RegistryParam;
import com.yzly.job.core.biz.model.ReturnT;

import java.util.List;

/**
 * @author yunze tech team 2017-07-27 21:52:49
 */
public interface AdminBiz {

    public static final String MAPPING = "/api";


    // ---------------------- callback ----------------------

    /**
     * callback
     *
     * @param callbackParamList
     * @return
     */
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList);


    // ---------------------- registry ----------------------

    /**
     * registry
     *
     * @param registryParam
     * @return
     */
    public ReturnT<String> registry(RegistryParam registryParam);

    /**
     * registry remove
     *
     * @param registryParam
     * @return
     */
    public ReturnT<String> registryRemove(RegistryParam registryParam);

}
