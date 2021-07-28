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
    @Autowired
    private StringHandler stringHandler;

    //String操作
    // 判断没有才新增
    public boolean putIfAbsent(String key, Object value){
        return stringHandler.putIfAbsent(key,value);
    }

    // 新增/修改：带缓存时间
    public void set(String key, Object value,long expirationTime){
        stringHandler.set(key,value,expirationTime);
    }

    // 原子自增
    public long increment(String key, long value){
        return stringHandler.increment(key,value);
    }

    // 删除key
    public boolean delete(String key){
        return stringHandler.delete(key);
    }

    // 匹配value删除key
    public Long delete(String key,String value){
        return stringHandler.delete(key,value);
    }

    // 批量删除key
    public Long delete(List<String> keys){
        return stringHandler.delete(keys);
    }

    // 手动过期key
    public boolean expire(String key,Long time){
        return stringHandler.expire(key,time);
    }

    // 查看key的值
    public Object get(String key){
        return stringHandler.get(key);
    }

    // 查看是否存在
    public boolean hasKey(String key){
        return stringHandler.hasKey(key);
    }

    // 查询剩余时间
    public Long getExpire(String key){
        return stringHandler.getExpire(key);
    }

    // hash操作
    // 新增：如果数据不存在则插入数据
    public boolean hPutIfAbsent(String key, String hashKey, Object value){
        return hashHandler.putIfAbsent(key,hashKey,value);
    }

    // 新增/修改：插入/修改数据
    public void hPut(String key, String hashKey, Object value){
        hashHandler.put(key,hashKey,value);
    }

    // 新增：批量插入数据
    public void hPutAll(String key, Map<String,Object> map){
        hashHandler.putAll(key,map);
    }

    // 删除
    public long hDelete(String key, Object... hashKeys){
        return hashHandler.delete(key,hashKeys);
    }

    // 查询：某个hashKey是否存在
    public boolean hHasKey(String key, String hashKey){
        return hashHandler.hasKey(key,hashKey);
    }

    // 查询
    public Object hGetAll(String key, String hashKey){
        return hashHandler.getAll(key,hashKey);
    }

    // 查询
    public Object hMultiGet(String key, List<Object> hashKeys){
        return hashHandler.multiGet(key,hashKeys);
    }

    // 查询
    public Map<Object, Object> hGetAll(String key){
        return hashHandler.getAll(key);
    }
}
