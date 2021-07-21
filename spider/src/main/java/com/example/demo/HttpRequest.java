package com.example.demo;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class HttpRequest {
    public static void main(String[] args) throws IOException {
        // 登录
        String url = "http://localhost:8009/login";
        String requestBody = "{\"userName\":\"admin\",\"password\":\"123456\"}";
        Connection.Response res = postLogin(url,requestBody);

        Document doc = res.parse();


        System.out.println(doc.text());
        //这儿的SESSIONID需要根据要登录的目标网站设置的session Cookie名字而定
        Map<String, String> map = res.cookies();
        Set<Map.Entry<String, String>> entries = map.entrySet();
        Connection connection = Jsoup.connect("http://localhost:8009/user/userList").ignoreContentType(true);
        for(Map.Entry<String, String> entry:entries){
            connection.cookie(entry.getKey(), entry.getValue());
        }
        Document objectDoc = connection.get();
        System.out.println(objectDoc.text());
    }

    private static Connection.Response postLogin(String url,String requestBody) throws IOException {
        return Jsoup.connect(url)
                .requestBody(requestBody)
                .header("Content-Type","application/json")
                .ignoreContentType(true)
                .method(Connection.Method.POST)
                .execute();
    }
}
