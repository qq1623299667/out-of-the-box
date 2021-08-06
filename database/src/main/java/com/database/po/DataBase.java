package com.database.po;

import com.database.util.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.regex.Pattern;

@Component
public class DataBase {
    @Autowired
    private SqlUtil sqlUtil;
    @Autowired
    private Table table;

    public static void main(String[] args) {
//        String backupCommand = "D:\\program\\mysql-8.0.26-winx64\\bin\\mysqldump -uroot -proot -R -c --set-charset=utf8 pulmonary_function";
        String outPutPath = "D:\\pulmonary_function.sql";
//        backup(backupCommand,outPutPath);

        DataBase dataBase = new DataBase();
        dataBase.loadAll(outPutPath);
    }

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
                        String[] split = sql.split("`");
                        String tableName = split[1];
                        boolean tableExist = table.existTable(tableName);
                        if(!tableExist){
                            runCommand(writer,sql);
                        }
                    }else{// 数据插入
                        // sql如果存在自增id，删掉自增id
                        String sql1 = handlePrimaryKey(sql);
                        // TODO 不相同数据直接入库
                        // TODO 相同数据覆盖操作
                        boolean existData = table.existData(sql1);
                        if(existData){
                            String sql2 = changeInsetSqlToUpdateSql(sql1);
                            runCommand(writer,sql2);
                        }else{
                            runCommand(writer, sql1);
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
        String id = getFirstValue(sql);
        Pattern pattern = Pattern.compile("^[\\d]*$");

        // 如果是正整数，就去掉id
        if(pattern.matcher(id).matches()){
            return removeIncrementId(sql);
        }
        return sql;
    }

    /**
     * 获取第一个值
     * @author Will Shi
     * @since 2021/8/6
     */
    private String getFirstValue(String sql) {
        String[] split = sql.split("\\) VALUES \\(");
        return split[1].split(",")[0];
    }

    /**
     * 将插入的sql转换成修改sql
     * @author Will Shi
     * @since 2021/8/5
     */
    private String changeInsetSqlToUpdateSql(String sql1) {
        return sql1;
    }

    /**
     * 删掉自增id
     * @author Will Shi
     * @since 2021/8/5
     */
    private String removeIncrementId(String sql) {
         // 去掉第一个字段
        String firstColumn = getFirstColumn(sql);
        sql = sql.replace("`"+firstColumn+"`,", "");
        // TODO 去掉第一个值
        String pointer = ") VALUES (";
        int pointerInt = sql.indexOf(pointer);
        String left = sql.substring(0,pointerInt+10);
        String firstValue = getFirstValue(sql);
        String right = sql.substring(left.length()+firstValue.length()+1);
        sql = left+right;
        return sql;
    }

    /**
     * 获取第一个字段
     * @author Will Shi
     * @since 2021/8/6
     */
    private String getFirstColumn(String sql) {
        String[] split = sql.split("`");
        return split[3];
    }

    /**
     * 执行命令
     * @author Will Shi
     * @since 2021/8/5
     */
    private void runCommand(OutputStreamWriter writer, String sql) throws IOException {
        printLn(sql);
        writer.write(sql);
        writer.flush();
    }

    private static void clear(StringBuilder sb) {
        if(sb.length()>0){
            sb.delete(0,sb.length());
        }
    }

    public static void printLn(String string){
        System.out.println(string);
    }
}
