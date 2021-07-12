package com.test.demo.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class HashHandler{
    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate hashRedisTemplate;

    private HashOperations<String, Object, Object> getHashOperations() {
        return hashRedisTemplate.opsForHash();
    }

    public boolean putIfAbsent(String key, String hashKey, Object value){
        return getHashOperations().putIfAbsent(key, hashKey, value);
    }

    public void put(String key, String hashKey, Object value){
        getHashOperations().put(key, hashKey, value);
    }

    public void putAll(String key, Map<String, Object> map) {
        getHashOperations().putAll(key,map);
    }

    public long delete(String key, Object... hashKeys) {
        Long delete = getHashOperations().delete(key, hashKeys);
        return delete;
    }

    public boolean hasKey(String key, String hashKey) {
        return getHashOperations().hasKey(key,hashKey);
    }

    public Object get(String key, String hashKey) {
        return getHashOperations().get(key,hashKey);
    }

    public Object multiGet(String key, List<Object> hashKeys) {
        return getHashOperations().multiGet(key,hashKeys);
    }
}
