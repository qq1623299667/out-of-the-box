package com.encrypt;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Scanner;

@Slf4j
public class Authenticator {
    public static String publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAi2JtDZ+aL9DWRc18KopuZ4osVVHc2qNZQQeEJbUevfgxAJkvSjZ/flSDGgXQhcsUrKNAs6lx3wVz9sDbgNDZyX7aUOUWbkrmw7nzkJwRNKMxGkVeZg1gY8iIXOTwwwdS6t2w21lH0tMBMJ5Y9G3ZV1jqg5QX8d9NTaAe6dFpOOkOCT0f3MJ36etP6S+SbmVL/0DOLnopVDMF3HoOTVLQTEQYXqAFAL4rUXTP+85JwdD2ptkaudQBe8fOc6r+ngnPvCayL52EPjaQZuxQjsQPCQYU716eVfnAgYGFdO39wD6FZkfyFKkH6dHLsndLwC/zOF/U5sQajTxiO52Vm65+7wIDAQAB";
    public static String splitCodeEnd = ";";
    public static String splitCode = ":";
    public static String END_TIME = "endTime";
    public static String MAC = "mac";
    // 获取注册码
    public static String getRegisterCode(){
        log.info("欢迎使用挂机软件");
        log.info("请输入注册码：");
        Scanner scan = new Scanner(System.in);
        String next = scan.next();
        scan.close();
        log.info("您输入的注册码是："+next);
        return next;
    }

    // 校验注册码
    // 校验逻辑：校验密串通过解密，校验数据截止时间（需联网）未到，校验mac合法：一机一号
    public static void validateRegisterCode(String registerCode,String salt) throws Exception {
        // 校验必须包含参数
        Assert.notEmpty(registerCode);
        String plainStr = Base64Decoder.decodeStr(registerCode);
        Assert.notEmpty(plainStr);
        Assert.state(plainStr.contains(splitCodeEnd));
        Assert.state(plainStr.contains(END_TIME));
        Assert.state(plainStr.contains(MAC));
        String[] split = plainStr.split(splitCodeEnd);
        //时间不能低于截止日期
        String endTimeStr = split[0];
        endTimeStr = endTimeStr.split(splitCode)[1];
        Date nowDate = TimeUtil.getNetworkTime();
        Assert.state(!DateUtil.parse(endTimeStr).before(DateUtil.offsetDay(nowDate,-1)));
        // mac地址必须相同
        String macStr = split[1];
        macStr = macStr.split(splitCode)[1];
        Assert.state(macStr.equals(SystemUtil.getLocalMac()));
        // 校验数据hash未被篡改
        String md5HashStr = split[2];
        Assert.state(RSAUtil.verify(RSAUtil.md5Hash(endTimeStr,macStr,salt),md5HashStr,publicKeyString));
        log.info("正式版软件验证通过");
    }
}
