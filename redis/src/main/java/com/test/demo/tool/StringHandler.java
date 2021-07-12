package com.test.demo.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

public class StringHandler {
    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate stringRedisTemplate;
}
