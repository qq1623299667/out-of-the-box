package com.encrypt;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

@Slf4j
public class SystemUtil {
    public static void main(String[] args) throws SocketException, UnknownHostException, InterruptedException {
        // TODO Auto-generated method stub

        //得到IP，输出PC-201309011313/122.206.73.83

        String localMac = getLocalMac();
        log.info(localMac);
        Thread.sleep(1000_000);
    }

    // 获取mac地址
    public static String getLocalMac() throws SocketException, UnknownHostException {
        // TODO Auto-generated method stub
        //获取网卡，获取地址
        InetAddress ia = InetAddress.getLocalHost();
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        StringBuffer sb = new StringBuffer("");
        for(int i=0; i<mac.length; i++) {
            if(i!=0) {
                sb.append("-");
            }
            //字节转换为整数
            int temp = mac[i]&0xff;
            String str = Integer.toHexString(temp);
            if(str.length()==1) {
                sb.append("0"+str);
            }else {
                sb.append(str);
            }
        }
        String macString = sb.toString().toUpperCase();
        return macString;
    }
}
