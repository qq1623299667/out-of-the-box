package com.database.po;

import com.database.util.HighStringUtil;
import com.database.util.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Component
public class DataBase {
    @Autowired
    private SqlUtil sqlUtil;
    @Autowired
    private Table table;

    // 备份文件到指定路径
    public void backup(String backupCommand,String outPutPath) {
        try {
            Runtime rt = Runtime.getRuntime();

            Process child = rt.exec(backupCommand);
            InputStream in = child.getInputStream();
            InputStreamReader xx = new InputStreamReader(in, "utf8");
            String inStr;
            StringBuffer sb = new StringBuffer("");
            String outStr;
            BufferedReader br = new BufferedReader(xx);
            while ((inStr = br.readLine()) != null) {
                sb.append(inStr + "\r\n");
            }
            outStr = sb.toString();

            FileOutputStream fout = new FileOutputStream(outPutPath);
            OutputStreamWriter writer = new OutputStreamWriter(fout, "utf8");
            writer.write(outStr);
            writer.flush();

            in.close();
            xx.close();
            br.close();
            writer.close();
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 读取加载文件到其他服务器：全量复制,适合数据量较小
    public void loadAll(String path) {
        try {
            Runtime rt = Runtime.getRuntime();
            String command = "mysql -uroot -proot pulmonary_function1";
            Process child = rt.exec(command);
            OutputStream out = child.getOutputStream();

            String inStr;
            OutputStreamWriter writer = new OutputStreamWriter(out, "utf8");

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf8"));

            StringBuilder sb = new StringBuilder();
            while ((inStr = br.readLine()) != null) {
                if(inStr.startsWith("--")
                        || inStr.equals("")
                        ||inStr.startsWith("UNLOCK")
                        ||inStr.startsWith("LOCK")){
                    continue;
                }
                sb.append(inStr + "\r\n");
                // sql数据必须结尾
                if(inStr.endsWith(";")){
                    // sql 解析到的数据一共7种：注释(单行注释末尾不用加分号，提前判断，多行注释放行到分号)，
                    // 空串，加锁，解锁，删除表，新增表，新增数据
                    String sql = sb.toString();
                    clear(sb);

                    // 注释不处理
                    // 删除表不处理
                    if(sql.startsWith("/*") || sql.startsWith("DROP")){
                        continue;
                    }


                    // 数据入库
                    if(sql.startsWith("CREATE")){
                        // 没有表格则执行建表语句
                        boolean tableExist = table.existTable(sqlUtil.getTableName(sql));
                        if(!tableExist){
                            runCommand(writer,sql);
                        }
                    }else{// 数据插入
                        // 批量入库的sql需要拆成单独入库的sql
                        List<String> list = sqlUtil.parseBatchInsertSqlToSimple(sql);
                        for(int i=0;i<list.size();i++){
                            //  否则去掉自增id，校验数据是否存在等信息不好处理
                            // sql如果存在自增id，删掉自增id
                            String sql1 = list.get(i);
                            sql1 = handlePrimaryKey(sql1);
                            // 不相同数据直接入库
                            // 相同数据覆盖操作
                            String tableName = sqlUtil.getTableName(sql1);
                            boolean checkDataRepeat = true;
                            Map<String,String> map = new HashMap<>();
                            if(tableName.equals("p_test_data")){
                                checkDataRepeat = false;
                            }else {
                                List<String> list1 = HighStringUtil.extractParenthesisContent(sql1);
                                String columnValue = list1.get(1);
                                String[] split = columnValue.split(",");
                                if(tableName.equals("person_message")){
                                    map.put("id_type",split[1]);
                                    map.put("id_number",split[2]);
                                }else if(tableName.equals("person_message_history")){
                                    map.put("test_num",split[2]);
                                }
                            }

                            if(!checkDataRepeat){
                                runCommand(writer, sql1);
                            }else{
                                boolean existData = table.existData(tableName,map);
                                if(existData){
                                    // 将插入的sql转换成修改sql
                                    String sql2 = changeInsetSqlToUpdateSql(sql1,map);
                                    runCommand(writer,sql2);
                                }else{
                                    runCommand(writer, sql1);
                                }
                            }
                        }
                    }
                }



            }
//            outStr = sb.toString();
//            writer.write(outStr);
//            writer.flush();


            out.close();
            br.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 主键处理
     * @author Will Shi
     * @since 2021/8/6
     */
    private String handlePrimaryKey(String sql) {
        String id = sqlUtil.getFirstValue(sql);
        Pattern pattern = Pattern.compile("^[\\d]*$");

        // 如果是正整数，就去掉id
        if(pattern.matcher(id).matches()){
            return sqlUtil.removeInsertSqlIncrementId(sql);
        }
        return sql;
    }

    /**
     * 将插入的sql转换成修改sql
     * @author Will Shi
     * @since 2021/8/5
     */
    private String changeInsetSqlToUpdateSql(String sql1,Map<String,String> map) {
//        if(sql1.contains("0f0cc4decfc74207b5d6")){
//            log.info(sql1);
//        }
        List<String> strings = HighStringUtil.extractParenthesisContent(sql1);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UPDATE "+sqlUtil.getTableName(sql1)+" set ");
        String[] columns = strings.get(0).split(",");
        String value = strings.get(1);
        String[] values = null;
        if(!value.contains("{")){
            values = value.split(",");
        }else {
            // TODO 复杂数据处理起来非常困难
            return "-- ";
        }
        for(int i=0;i<columns.length;i++){
            stringBuilder.append(" "+columns[i]+"="+values[i]);
            if(i!=columns.length-1){
                stringBuilder.append(",");
            }
        }
        Set<Map.Entry<String, String>> entries = map.entrySet();
        stringBuilder.append(" where 1=1 ");
        for(Map.Entry<String, String> entry:entries){
            stringBuilder.append(" and "+entry.getKey()+"="+entry.getValue());

        }
        return stringBuilder.toString();
    }



    /**
     * 执行命令
     * @author Will Shi
     * @since 2021/8/5
     */
    private void runCommand(OutputStreamWriter writer, String sql) throws IOException {
        log.info(sql);
        writer.write(sql);
        writer.flush();
    }

    private static void clear(StringBuilder sb) {
        if(sb.length()>0){
            sb.delete(0,sb.length());
        }
    }
}
