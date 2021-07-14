package com.test.demo.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class StringHandler {
    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate stringRedisTemplate;

    private ValueOperations getStringOperations() {
        return stringRedisTemplate.opsForValue();
    }

    public boolean putIfAbsent(String key, Object value) {
        return getStringOperations().setIfAbsent(key, value);
    }

    public void set(String key, Object value,long expirationTime) {
        getStringOperations().set(key, value,expirationTime);
    }

    public long increment(String key, long value) {
        return getStringOperations().increment(key,value);
    }

    public boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    public Long delete(String key,String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptText(script);
        return (Long)stringRedisTemplate.execute(redisScript, Arrays.asList(key),value);
    }

    public Long delete(List<String> keys) {
        return stringRedisTemplate.delete(keys);
    }

    public boolean expire(String key, Long time) {
        return stringRedisTemplate.expire(key,time, TimeUnit.SECONDS);
    }

    public Object get(String key) {
        return getStringOperations().get(key);
    }

    public boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key);
    }


}
