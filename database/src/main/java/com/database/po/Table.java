package com.database.po;

import com.database.util.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class Table {
    @Autowired
    private SqlUtil sqlUtil;

    /** 
     * 通过查询数据库查询表是否存在
     * @author Will Shi
     * @since 2021/8/5
     */
    public boolean existTable(String tableName){
        List<Map<String, Object>> query = sqlUtil.query("show tables like '" + tableName + "'");
        return query.size()>0;
    }

    /**
     * 指定sql是否在数据库中存在
     * @author Will Shi
     * @since 2021/8/5
     */
    public boolean existData(String tableName,Map<String,String> map){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, String> entry:map.entrySet()){
            String column = entry.getKey();
            String columnValue = entry.getValue();
            sb.append(" and "+column+"="+columnValue);
        }
        String sql = "select count(1) from "+tableName+" where 1=1 "+sb.toString();
        log.debug(sql);
        List<Map<String, Object>> query = sqlUtil.query(sql);
        Map<String, Object> stringObjectMap = query.get(0);
        Object o = stringObjectMap.get("count(1)");
        Long count = (Long)o;
        return count>0;
    }
}
