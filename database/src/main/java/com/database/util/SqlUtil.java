package com.database.util;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class SqlUtil {
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    /**
     * sql拼接
     *
     * @author Will Shi
     * @since 2021/8/3
     */
    public String analysisSql(String sqlString, Map<String, Object> parameters) {
        MybatisConfiguration configuration = (MybatisConfiguration) sqlSessionFactory.getConfiguration();
        LanguageDriver xmlLanguageDriver = new XMLLanguageDriver();
        final StringBuilder sqlXml = new StringBuilder();
        String[] strings = new String[]{sqlString};
        for (String fragment : strings) {
            sqlXml.append(fragment);
            sqlXml.append(" ");
        }
        SqlSource sqlSource = xmlLanguageDriver.createSqlSource(configuration, sqlXml.toString().trim(), Map.class);
        BoundSql boundSql = sqlSource.getBoundSql(parameters);
        final StringBuilder sql = new StringBuilder(boundSql.getSql());
        if (parameters != null && !parameters.isEmpty()) {
            for (Object parameter : parameters.values()) {
                int start = sql.indexOf("?");
                sql.replace(start, start + 1, parameter.toString());
            }
        }
        return sql.toString();
    }

    /**
     * 直接返回sql执行结果
     * @author Will Shi
     * @since 2021/8/3
     */
    public List<Map<String, Object>> query(String sqlString) {
        return query(sqlString,null);
    }

    /**
     * 直接返回sql执行结果
     *
     * @author Will Shi
     * @since 2021/8/3
     */
    public List<Map<String, Object>> query(String sqlString, Map<String, Object> param) {
        MybatisConfiguration configuration = (MybatisConfiguration) sqlSessionFactory.getConfiguration();
//        if(!configuration.hasStatement(mastpId)){
//            String[] strings=new String[]{"<script>select\n" +
//                    "        role_id as id,\n" +
//                    "        name,\n" +
//                    "        description\n" +
//                    "        from sys_role\n" +
//                    "        where 1=1\n" +
//                    "        <if test=\"name != null and name != ''\">\n" +
//                    "            and name LIKE CONCAT('%', #{name},'%')\n" +
//                    "\n" +
//                    "        </if></script>"};

        LanguageDriver xmlLanguageDriver = new XMLLanguageDriver();
        final StringBuilder sql = new StringBuilder();
        String[] strings = new String[]{sqlString};
        for (String fragment : strings) {
            sql.append(fragment);
            sql.append(" ");
        }
        SqlSource sqlSource = xmlLanguageDriver.createSqlSource(configuration, sql.toString().trim(), Map.class);

        BoundSql boundSql = sqlSource.getBoundSql(param);
        String mastpId = UUID.randomUUID().toString();
        MappedStatement ms = new MappedStatement.Builder(configuration, mastpId, sqlSource, SqlCommandType.SELECT)
                .resultMaps(new ArrayList<ResultMap>() {
                    {
                        add(new ResultMap.Builder(configuration, "defaultResultMap", Map.class, new ArrayList<>(0))
                                .build());
                    }
                }).build();
        // 缓存
        configuration.addMappedStatement(ms);
//        }
        DefaultSqlSession defaultSqlSession = (DefaultSqlSession) sqlSessionFactory.openSession();
        List<Map<String, Object>> resutls = defaultSqlSession.selectList(mastpId, param);
        return resutls;
    }

}