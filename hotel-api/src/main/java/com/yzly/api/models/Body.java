package com.yzly.api.models;/**
 * @Description:TODO
 * @Auther frank
 * version V1.0
 * @createtime 2019-11-14 17:26
 **/


import com.yzly.api.annotations.Array;

import java.util.List;

/**
 *@ClassName Body
 *@Description TODO
 *@Auth frank
 *@Date 2019-11-14 17:26
 *@Version 1.0
 **/
@Array
public class Body {

    private List<Result> bodyList;

    public List<Result> getBodyList() {
        return bodyList;
    }

    public void setBodyList(List<Result> bodyList) {
        this.bodyList = bodyList;
    }

    @Override
    public String toString() {
        String back = null;
        back = "Body{"+"bodyList=";
        if(null != bodyList && !bodyList.isEmpty()) {
            for (Result result : bodyList) {
                back += result.toString();
            }
        }
        return back += '}';
    }
}
