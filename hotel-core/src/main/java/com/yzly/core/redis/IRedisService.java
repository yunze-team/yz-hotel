package com.yzly.core.redis;

import java.util.List;

/**
 * Created by lazyb on 2017/6/27.
 */
public interface IRedisService {

    public boolean set(String key, String value);

    public String get(String key);

    public boolean expire(String key, long expire);

    public <T> boolean setList(String key, List<T> list);

    public <T> List<T> getList(String key, Class<T> clz);

    public <T> List<T> getListFromJson(String key, Class<T> clz);

    public long lpush(String key, Object obj);

    public long rpush(String key, Object obj);

    public String lpop(String key);

    public void del(String... key);

}
