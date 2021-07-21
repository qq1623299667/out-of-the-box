package com.example.demo;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpTest {
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = "https://www.zhihu.com/";
        HttpGet httpGet = new HttpGet(url);
        System.out.println(httpGet);

        HttpPost httpPost = new HttpPost(url);
        System.out.println(httpPost);

        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            System.out.println(httpResponse);

            //获取响应码
            int status = httpResponse.getStatusLine().getStatusCode();
            System.out.println(status);

            if(status == 200){
                String entity = EntityUtils.toString(httpResponse.getEntity());
                System.out.println(entity);
                EntityUtils.consume(httpResponse.getEntity());
            }else{
                EntityUtils.consume(httpResponse.getEntity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(httpResponse!=null){
                httpResponse.close();
            }
        }
    }
}