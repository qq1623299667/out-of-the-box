package com.encrypt;

import lombok.extern.slf4j.Slf4j;

import java.io.*;


/**
 * 加密文件，逐字加密，只加密前10个字节，非常慢，1M耗时接近10秒
 * @author Will Shi
 * @since 2021/8/4
 */
@Slf4j
public class FileEncrypt2 {
    private static final int numOfEncAndDec = 0x99; //加密解密秘钥
    private static int dataOfFile = 0; //文件字节内容

    public static void main(String[] args) {
        String path = "D:\\1";
        String encFilePath = "D:\\2.secret";
        String decFilePath = "D:\\2";
        File srcFile = new File(path); //初始文件
        File encFile = new File(encFilePath); //加密文件
        File decFile = new File(decFilePath); //解密文件

        try {
            long start = System.currentTimeMillis();
            encFile(srcFile, encFile); //加密操作
            long encEnd = System.currentTimeMillis();
            log.info("加密耗时：{} ms",(encEnd-start));
            decFile(encFile, decFile); //解密操作
            long decEnd = System.currentTimeMillis();
            log.info("解密耗时：{} ms",(decEnd-encEnd));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void encFile(File srcFile, File encFile) throws Exception {
        if (!srcFile.exists()) {
            System.out.println("source file not exixt");
            return;
        }

        if (!encFile.exists()) {
            System.out.println("encrypt file created");
            encFile.createNewFile();
        }
        InputStream fis = new FileInputStream(srcFile);
        OutputStream fos = new FileOutputStream(encFile);

        int count = 0;
        while ((dataOfFile = fis.read()) > -1) {
            count++;
            if(count<10){
                fos.write(dataOfFile ^ numOfEncAndDec);
            }else {
                fos.write(dataOfFile);
            }
        }
        fis.close();
        fos.flush();
        fos.close();
    }

    private static void decFile(File encFile, File decFile) throws Exception {
        if (!encFile.exists()) {
            System.out.println("encrypt file not exixt");
            return;
        }

        if (!decFile.exists()) {
            System.out.println("decrypt file created");
            decFile.createNewFile();
        }

        InputStream fis = new FileInputStream(encFile);
        OutputStream fos = new FileOutputStream(decFile);

        int count = 0;
        while ((dataOfFile = fis.read()) > -1) {
            count++;
            if(count<10){
                fos.write(dataOfFile ^ numOfEncAndDec);
            }else {
                fos.write(dataOfFile);
            }
        }
        fis.close();
        fos.flush();
        fos.close();
    }

}
