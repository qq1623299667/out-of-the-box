package com.database.po;

import com.database.util.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
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
     * TODO 指定sql是否在数据库中存在
     * @author Will Shi
     * @since 2021/8/5
     */
    public boolean existData(String sql){
        return true;
    }
}
