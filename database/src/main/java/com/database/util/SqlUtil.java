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
        defaultSqlSession.close();
        return resutls;
    }

    /**
     * 获取insert语句的第一个字段
     * @author Will Shi
     * @since 2021/8/6
     */
    private String getInsertFirstColumn(String sql) {
        String[] split = sql.split("`");
        return split[3];
    }

    /**
     * 获取sql语句的第一个栏
     * @author Will Shi
     * @since 2021/8/6
     */
    public String getFirstColumn(String sql) {
        if(sql.startsWith("INSERT INTO")){
            return getInsertFirstColumn(sql);
        }
        return null;
    }

    /**
     * 获取新增sql语句的第一个值
     * @author Will Shi
     * @since 2021/8/6
     */
    private String getInsertFirstValue(String sql) {
        List<String> strings = HighStringUtil.extractParenthesisContent(sql);
        String values = strings.get(1);
        return values.split(",")[0];
    }


    /**
     * 获取sql语句的第一个值
     * @author Will Shi
     * @since 2021/8/6
     */
    public String getFirstValue(String sql) {
        if(sql.startsWith("INSERT INTO")){
            return getInsertFirstValue(sql);
        }
        return null;
    }

    /**
     * 删掉自增id
     * @author Will Shi
     * @since 2021/8/5
     */
    public String removeInsertSqlIncrementId(String sql) {
        // 去掉第一个字段
        String firstColumn = getFirstColumn(sql);
        sql = sql.replace("`"+firstColumn+"`,", "");
        // 去掉第一个值
        String pointer = ") VALUES (";
        int pointerInt = sql.indexOf(pointer);
        String left = sql.substring(0,pointerInt+10);
        String firstValue = getFirstValue(sql);
        String right = sql.substring(left.length()+firstValue.length()+1);
        sql = left+right;
        return sql;
    }

    /**
     * 获取插入sql语句的表名
     * @author Will Shi
     * @since 2021/8/6
     */
    private String getInsertSqlTableName(String sql){
        String[] split = sql.split("`");
        String tableName = split[1];
        return tableName;
    }

    /**
     * 获取sql语句的表名
     * @author Will Shi
     * @since 2021/8/6
     */
    public String getTableName(String sql){
        if(sql.startsWith("CREATE TABLE") || sql.startsWith("INSERT INTO")){
            return getInsertSqlTableName(sql);
        }
        return null;
    }

    /**
     * 将批量入库的sql拆成单独入库的sql
     * @author Will Shi
     * @since 2021/8/6
     */
    public List<String> parseBatchInsertSqlToSimple(String batchInsertSql) {
        // 拿到固定的插入column语句部分
        List<String> strings = HighStringUtil.extractParenthesisContent(batchInsertSql);
        String insertColumnSql = batchInsertSql.substring(0,batchInsertSql.indexOf("("))+"("+strings.get(0)+") values (";
        // 拿到value的语句部分
        List<String> insertSqls = new ArrayList<>();
        for(int i=1;i<strings.size();i++){
            String sql = insertColumnSql+strings.get(i)+")";
            insertSqls.add(sql);
        }
        // 循环组装
        return insertSqls;
    }
}