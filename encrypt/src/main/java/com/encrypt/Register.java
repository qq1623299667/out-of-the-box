package com.encrypt;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Scanner;

@Slf4j
public class Register {
    public static String privateKeyString = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCLYm0Nn5ov0NZFzXwqim5niixVUdzao1lBB4QltR69+DEAmS9KNn9+VIMaBdCFyxSso0CzqXHfBXP2wNuA0NnJftpQ5RZuSubDufOQnBE0ozEaRV5mDWBjyIhc5PDDB1Lq3bDbWUfS0wEwnlj0bdlXWOqDlBfx301NoB7p0Wk46Q4JPR/cwnfp60/pL5JuZUv/QM4ueilUMwXceg5NUtBMRBheoAUAvitRdM/7zknB0Pam2Rq51AF7x85zqv6eCc+8JrIvnYQ+NpBm7FCOxA8JBhTvXp5V+cCBgYV07f3APoVmR/IUqQfp0cuyd0vAL/M4X9TmxBqNPGI7nZWbrn7vAgMBAAECggEAbKQ5MCc4vRBgE2RI5bekult+lroKFPjBduhu3h1Aav3q4/aCX6v+z+77iOsDT2WtJarRobfvI9BSRQCplswzFNUlQWxugWUsXV6xNAQRfR++BCYRoCXI/b2uXAmuh7VLsfk+ApqiWvdQtB6YY1zDzUtm70wdJc/RpzGpJsKs60fE6oYH7JYm5oCgMQ0X18OhobogPpJt427p9tRCzfiWbEdCBIt1znVWAsSvw9HeYcpnOsYIkpzE3iE9qlilATcBrEsG5yrkZN0W2dL1g4LOzDWiRtvpcbDM68K5DdB2IW707YaXMmLxLvgLSfpzVfmygBffs7lLtkAHg1kA2PHe0QKBgQDMAw+Znv0wERsKA6IQX3rv8rXWuA8FfYff51FevxBYYrQsymrTYEnv41HRIeJW3CeVIG98Bu91TEe/oWZzJZxTiWWO0hxmqPD81/iZfhiOOTBjTKfphs33mcRLVBYB3M3lNnm9M3crLv2a1x0tvv8h1GEoUtX6BNnGp3V/PtWlJQKBgQCu51JtXGF7tALYmz699kgEuI98uekjWR18G4Ql/Mwky+U5sKA6kVqAIMZhIA4d1nkDrtHDYU7a7rr8FxrXiVOZ3sHiRjYx8PSmvPBe5kpoaRiGhhY3AuYmt/iKKPmPxRCDLW6z7XZcvDnYBaF5n6knI6eYLs/pBvAqR95Bl7L5gwKBgFG3rCkBmTMGtwj02ZarWCgC1TlzsnilhqZ2Fo7YA2kT6E3EqrOOby9Ko7wcUXgvaR9/xYgtkv8uFdM5iDr5RrvjXi2uFbg9VYJB1A9PhfB6EdrEmsGcwKW8DveJb+NBGawFfQ//LG28vdKm9cYcKj/XvS8UtycViFHdQ+A7QAKNAoGAfbiBI2L4H3cxZImS+/D/q4UOftUTo/j2nLMatkOUag614lX3lqHMr7TfZAkqnIAB1Ifg99QGXpCjj3j8Llnj4gKg0wktA5D0CrZdAZn/ejWxD2FqrnhC/ynWWVlCqfk4+VG69VKHdflGiuThQQ9nA185pMZhZFs8Xufx9qwYVgkCgYBowgQBrtOgkmd0Cg5uA999tWEEpMDMDWaF2Cp8YsFNOIazfDKuqnnK5bf81eDqy4VougjSMf+Y+W0FHBQ6geo+m7siGtd0DHRFa3cLISg8ODdcFLQ45+eXmkWaoQE0g9uwGCT6UUOj25KVeyCDsUTfVXsrRmHIZHTGXXs039DpGA==";
    public static String splitCodeEnd = ";";
    public static String splitCode = ":";
    public static String END_TIME = "endTime";
    public static String MAC = "mac";

    public static void main(String[] args) throws Exception {
        log.info("请输入注册天数：");
        Scanner scan = new Scanner(System.in);
        String next = scan.next();
        Integer count = Integer.valueOf(next);
        log.info("请输入mac地址：");
        String mac = scan.next();
        log.info("请输入产品名称：");
        String salt = scan.next();
        scan.close();
        String registerCode = generateRegisterCode(count, mac,salt);
        log.info("注册码生成成功：{}",registerCode);
        Thread.sleep(1000_000);
    }

    // 生成注册码
    private static String generateRegisterCode(Integer count,String mac,String salt) throws Exception {
        Date date = new Date();
        DateTime dateTime = DateUtil.offsetDay(date, count);
        String endTime = DateUtil.format(dateTime, DatePattern.NORM_DATE_PATTERN);
        log.info("截止日期是：{}",endTime);
        return generateRegisterCode(endTime,mac,salt);
    }

    // 生成注册码
    public static String generateRegisterCode(String endTime,String mac,String salt) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(END_TIME+splitCode+endTime);
        stringBuilder.append(splitCodeEnd);
        stringBuilder.append(MAC+splitCode+mac);
        stringBuilder.append(splitCodeEnd);
//        String encrypt = RSAUtil.encrypt(RSAUtil.getPubKey(publicKeyString), endTime + mac);
        String encode = RSAUtil.md5Hash(endTime,mac,salt);
        String sign = RSAUtil.sign(encode,privateKeyString);
        stringBuilder.append(sign);
        return Base64Encoder.encode(stringBuilder.toString());
    }
}
