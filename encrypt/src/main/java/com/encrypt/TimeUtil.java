package com.encrypt;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class TimeUtil {
    public static void main(String[] args) {

        String webUrlt = "http://www.taobao.com";
        String webUrlb = "http://www.baidu.com";
        System.out.println(getNetworkTime(webUrlt) + " [淘宝]");
        System.out.println(getNetworkTime(webUrlb) + " [百度]");

    }
    public static Date getNetworkTime(){
        String webUrlt = "http://www.taobao.com";
        return getNetworkTime(webUrlt);
    }

    public static Date getNetworkTime(String webUrl) {
        try {
            URL url = new URL(webUrl);
            URLConnection conn = url.openConnection();
            conn.connect();
            long dateL = conn.getDate();
            Date date = new Date(dateL);
            return date;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
