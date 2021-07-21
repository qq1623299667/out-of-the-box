package com.example.shiro.test;

import org.apache.shiro.crypto.hash.Md5Hash;

public class TestMd5 {
    public static void main(String[] args) {
        Md5Hash md5Hash = new Md5Hash("123");
        System.out.println(md5Hash.toHex());
        Md5Hash md5Hash1 = new Md5Hash("123","12345&8");
        System.out.println(md5Hash1.toHex());
        Md5Hash md5Hash2 = new Md5Hash("123","12345&8",1024);
        System.out.println(md5Hash2.toHex());

    }
}
