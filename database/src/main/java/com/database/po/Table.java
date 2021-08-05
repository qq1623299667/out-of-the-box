package com.database.po;

import com.database.util.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

        return false;
    }
}
