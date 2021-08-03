package com.database;

import java.io.*;

public class DataBase {
    public static void main(String[] args) {
//        String backupCommand = "D:\\program\\mysql-8.0.26-winx64\\bin\\mysqldump -uroot -proot -R -c --set-charset=utf8 pulmonary_function";
        String outPutPath = "D:\\pulmonary_function.sql";
//        backup(backupCommand,outPutPath);

        String loadCommand = "mysql -uroot -proot pulmonary_function1";
        loadAll(outPutPath,loadCommand);
    }

    // 备份文件到指定路径
    public static void backup(String backupCommand,String outPutPath) {
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
    public static void loadAll(String path, String command) {
        try {
            Runtime rt = Runtime.getRuntime();

            Process child = rt.exec(command);
            OutputStream out = child.getOutputStream();

            String inStr;
            OutputStreamWriter writer = new OutputStreamWriter(out, "utf8");

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf8"));

            StringBuilder sb = new StringBuilder();
            while ((inStr = br.readLine()) != null) {
                // 注释不处理，
                // 删除表不处理
                if(inStr.startsWith("--") || inStr.startsWith("DROP")){
                    clear(sb);
                    continue;
                }
                sb.append(inStr + "\r\n");

                // 数据入库
                // TODO 没有表格则执行建表语句

                // TODO 不相同数据直接入库
                // TODO 相同数据覆盖操作
                if(inStr.endsWith(";")){
                    String string = sb.toString();
                    printLn(string);
                    clear(sb);
                    writer.write(string);
                    writer.flush();
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

    private static void clear(StringBuilder sb) {
        if(sb.length()>0){
            sb.delete(0,sb.length());
        }
    }

    public static void printLn(String string){
        System.out.println(string);
    }
}
