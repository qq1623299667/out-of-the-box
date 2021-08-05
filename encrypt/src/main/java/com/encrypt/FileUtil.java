package com.encrypt;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class FileUtil {
    // 写文件
    public static boolean writeFile(String path,String content){
        File f=new File(path);
        FileWriter fw=null;
        BufferedWriter bw=null;
        try{
            if(f.exists()){
                log.info("文件 {} 已存在，删除文件",path);
                f.delete();
            }
//            log.info("生成文件 {}",path);
            f.createNewFile();
            fw=new FileWriter(f.getAbsoluteFile(),true);  //true表示可以追加新内容
            //fw=new FileWriter(f.getAbsoluteFile()); //表示不追加
            bw=new BufferedWriter(fw);
            bw.write(content);
            bw.close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    // 读文件
    public static String readFile(String path){
        String s="";
        File f=new File(path);
        BufferedReader br=null;
        try{
//            System.out.println("按照行读取文件内容");
            br=new BufferedReader(new FileReader(f));
            String temp;
            while((temp=br.readLine())!=null){
                s+=temp;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        System.out.println("file content:"+s);
        return s;
    }

    // 文件是否存在
    public static boolean fileExist(String path){
        File file = new File(path);
        return file.exists();
    }

    // 删除文件
    public static boolean deleteFile(String path){
        File file = new File(path);
        return file.delete();
    }

    public static void main(String[] args) {
        String path = "D:\\licence";
        String content="要写入文件的新内容";
        boolean b = writeFile(path, content);
        log.info("{}",b);
        String s = readFile(path);
        log.info("读取到的文件内容是：{}",s);
    }


}
