package com.database.controller;

import com.alibaba.fastjson.JSON;
import com.database.po.DataBase;
import com.database.po.Table;
import com.database.util.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sql")
public class SqlController {
    @Autowired
    private SqlUtil sqlUtil;
    @Autowired
    private Table table;

    @Autowired
    private DataBase dataBase;

    private static final String script="<script>";
    private static final String back_script="</script>";

    /**
     * mybatis动态sql测试
     * @author Will Shi
     * @since 2021/8/3
     */
    @GetMapping("/test")
    public String test(String sql){
        //按照Mybatis xml的方式写sql
        StringBuffer sb=new StringBuffer();
        sb.append(script);
//        sb.append("select ");
//        sb.append(" role_id as id, ");
//        sb.append("name, ");
//        sb.append("description ");
//        sb.append("from sys_role ");
//        sb.append("where 1=1 ");
//        sb.append("<if test=\"name != null and name != ''\"> ");
//        sb.append("and name LIKE CONCAT('%', #{name},'%') ");
//        sb.append("</if></script>");
        sb.append(sql);
        sb.append(back_script);

        Map<String,Object> parameters=new HashMap<>();
//        parameters.put("name","test");

        //拼接sql
        String s = sqlUtil.analysisSql(sb.toString(), parameters);
        System.out.println(s);

        //执行sql，并缓存sql，直接返回执行结果

        List<Map<String, Object>> query = sqlUtil.query(sb.toString(), parameters);

        System.out.println(JSON.toJSONString(query));
        return JSON.toJSONString(query);
    }

    @GetMapping("/test2")
    public void test2(){
        dataBase.loadAll("D:\\1.sql");
//        String backupCommand = "D:\\program\\mysql-8.0.26-winx64\\bin\\mysqldump -uroot -proot -R -c --set-charset=utf8 pulmonary_function";
//        String outPutPath = "D:\\pulmonary_function.sql";
//        dataBase.backup(backupCommand,outPutPath);
    }
}
