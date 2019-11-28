package com.yzly.core.redis;

import com.yzly.core.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by lazyb on 2017/6/27.
 */
@Service("redisService")
public class RedisServiceImpl implements IRedisService {

    @Override
    public <T> List<T> getListFromJson(String key, Class<T> clz) {
        String json = get(key);
        if(json!=null){
            List<T> list = JsonUtil.parseList(json, clz);
            return list;
        }
        return null;
    }

    @Autowired
    private RedisTemplate<String, ?> redisTemplate;

    @Override
    public boolean set(final String key, final String value) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.set(serializer.serialize(key), serializer.serialize(value));
                return true;
            }
        });
        return result;
    }

    public String get(final String key){
        String result = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] value =  connection.get(serializer.serialize(key));
                return serializer.deserialize(value);
            }
        });
        return result;
    }

    @Override
    public boolean expire(final String key, long expire) {
        return redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    @Override
    public <T> boolean setList(String key, List<T> list) {
        String value = JsonUtil.toJson(list);
        return set(key,value);
    }

    @Override
    public <T> List<T> getList(String key,Class<T> clz) {
        String json = get(key);
        if(json!=null){
            List<T> list = JsonUtil.toList(json, clz);
            return list;
        }
        return null;
    }

    @Override
    public long lpush(final String key, Object obj) {
        final String value = JsonUtil.toJson(obj);
        long result = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                long count = connection.lPush(serializer.serialize(key), serializer.serialize(value));
                return count;
            }
        });
        return result;
    }

    @Override
    public long rpush(final String key, Object obj) {
        final String value = JsonUtil.toJson(obj);
        long result = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                long count = connection.rPush(serializer.serialize(key), serializer.serialize(value));
                return count;
            }
        });
        return result;
    }

    @Override
    public String lpop(final String key) {
        String result = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] res =  connection.lPop(serializer.serialize(key));
                return serializer.deserialize(res);
            }
        });
        return result;
    }

    @Override
    public void del(String... key) {
        if(key != null && key.length > 0){
            if(key.length == 1){
                redisTemplate.delete(key[0]);
            }else{
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

}
