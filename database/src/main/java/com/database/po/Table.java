package com.database.po;

import com.database.util.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Table {
    @Autowired
    private SqlUtil sqlUtil;

    public boolean existTable(String tableName){

        return false;
    }
}
