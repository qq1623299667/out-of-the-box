package com.example.demo;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class HttpRequest {
    public static Map<String, String> cookies;

    public static void main(String[] args) throws IOException {
        // 登录
        String url = "http://localhost:8009/login";
        String requestBody = "{\"userName\":\"admin\",\"password\":\"123456\"}";
        String login = postLogin(url, requestBody);
        System.out.println(login);

        // 获取用户列表
        String url1="http://localhost:8009/user/userList";
        String text1 = get(url1);
        System.out.println(text1);

        // 获取病人历史
        String url2="http://localhost:8009/queryPatientTestHistory";
        String requestBody2 = "{\"currentPage\":1,\"sizePage\":10,\"personId\":\"b1b0e8150a2a4cbc8a64\"}";
        String text2 = post(url2,requestBody2);
        System.out.println(text2);
    }


    // get请求数据
    public static String get(String url1) throws IOException {
        Connection connection = Jsoup.connect(url1).ignoreContentType(true);
        Set<Map.Entry<String, String>> entries = cookies.entrySet();
        for(Map.Entry<String, String> entry:entries){
            connection.cookie(entry.getKey(), entry.getValue());
        }
        Document objectDoc = connection.get();
        return objectDoc.text();
    }

    // post登录
    public static String postLogin(String url,String requestBody) throws IOException {
        Connection.Response execute = Jsoup.connect(url)
                .requestBody(requestBody)
                .header("Content-Type", "application/json")
                .ignoreContentType(true)
                .method(Connection.Method.POST)
                .execute();
        cookies = execute.cookies();
        return execute.parse().text();
    }

    // post请求数据
    public static String post(String url,String requestBody) throws IOException {
        Connection connection = Jsoup.connect(url)
                .requestBody(requestBody)
                .header("Content-Type", "application/json")
                .ignoreContentType(true)
                .method(Connection.Method.POST);
        Set<Map.Entry<String, String>> entries = cookies.entrySet();
        for(Map.Entry<String, String> entry:entries){
            connection.cookie(entry.getKey(), entry.getValue());
        }
        Connection.Response execute = connection.execute();
        return execute.parse().text();
    }
}
