package com.test.demo.controller;

import com.test.demo.tool.RedisManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {
    @Autowired
    private RedisManager redisManager;

    @GetMapping("/testHashPutIfAbsent")
    public Object testHashPut(String key){
        log.info("testHashPut");
        Map<Object, Object> objectObjectMap = redisManager.hGetAll(key);
        return objectObjectMap;
    }
}
