package com.test.demo.controller;

import com.test.demo.tool.RedisManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {
    @Autowired
    private RedisManager redisManager;

    @GetMapping("/testHashPutIfAbsent")
    public String testHashPut(String key,String hashKey,String value){
        log.info("testHashPut");
        boolean result = redisManager.putIfAbsent(key, hashKey, value);
        return result?"success":"failure";
    }
}
