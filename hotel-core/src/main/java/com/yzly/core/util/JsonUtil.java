package com.yzly.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lazyb on 2017/7/7.
 */
public class JsonUtil {

    private static Gson gson = null;

    static{
        gson  = new Gson();//todo yyyy-MM-dd HH:mm:ss
    }

    public static synchronized Gson newInstance(){
        if(gson == null){
            gson =  new Gson();
        }
        return gson;
    }

    public static String toJson(Object obj){
        return gson.toJson(obj);
    }

    public static String toJsonFast(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static <T> T toBean(String json,Class<T> clz){

        return gson.fromJson(json, clz);
    }

    public static <T> T toBeanFast(String json, Class<T> clz) {
        return JSON.parseObject(json, clz);
    }

    public static <T> Map<String, T> toMap(String json, Class<T> clz){
        Map<String, JsonObject> map = gson.fromJson(json, new TypeToken<Map<String,JsonObject>>(){}.getType());
        Map<String, T> result = new HashMap<>();
        for(String key:map.keySet()){
            result.put(key,gson.fromJson(map.get(key),clz) );
        }
        return result;
    }

    public static Map toFastMap(String json) {
        return JSONObject.parseObject(json, Map.class);
    }

    public static Map<String, Object> toMap(String json){
        Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String,Object>>(){}.getType());
        return map;
    }

    public static <T> List<T> toList(String json, Class<T> clz){
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        List<T> list  = new ArrayList<>();
        for(final JsonElement elem : array){
            list.add(gson.fromJson(elem, clz));
        }
        return list;
    }

    public static <T> List<T> parseList(String json, Class<T> clz) {
        List<T> list = JSONObject.parseArray(json, clz);
        return list;
    }

}
