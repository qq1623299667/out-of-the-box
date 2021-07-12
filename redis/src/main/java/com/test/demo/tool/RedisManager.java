package com.test.demo.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Redis操作，抽象封装，具体用redistemplate还是jedis可以直接修改，不影响业务使用
 * @author 石佳
 * @since 2021/7/12
 */
@Component
public class RedisManager {
    @Autowired
    private HashHandler hashHandler;

    // 新增：如果数据不存在则插入数据
    public boolean putIfAbsent(String key, String hashKey, Object value){
        return hashHandler.putIfAbsent(key,hashKey,value);
    }

    // 新增/修改：插入/修改数据
    public void put(String key, String hashKey, Object value){
        hashHandler.put(key,hashKey,value);
    }

    // 新增：批量插入数据
    public void putAll(String key, Map<String,Object> map){
        hashHandler.putAll(key,map);
    }

    // 删除
    public long delete(String key, Object... hashKeys){
        return hashHandler.delete(key,hashKeys);
    }

    // 查询：某个hashKey是否存在
    public boolean hasKey(String key, String hashKey){
        return hashHandler.hasKey(key,hashKey);
    }

    // 查询
    public Object get(String key, String hashKey){
        return hashHandler.get(key,hashKey);
    }

    // 查询
    public Object multiGet(String key, List<Object> hashKeys){
        return hashHandler.multiGet(key,hashKeys);
    }
}
