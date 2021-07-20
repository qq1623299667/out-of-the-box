package com.example.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;

public class TestAuthenticator2 {
    public static void main(String[] args) throws Exception{
        //定义安全管理器
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        //定义一个支持MD5+盐+hash散列的密码匹配器
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        //告诉密码匹配器密文是哪种加密方式
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        //告诉密码匹配器，密码被散列的次数，这个一般定义为1024的倍数，默认一次，需要和校验部分保持一直
        hashedCredentialsMatcher.setHashIterations(1024);
        //定义自己的realm
        SecondRealm secondRealm = new SecondRealm();
        //为realm设置密码匹配器
        secondRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        //为安全管理器设置realm
        defaultSecurityManager.setRealm(secondRealm);
        //模拟用户登录场景,不需要传入盐，校验部分自动获取
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken("zhaoyun","123");
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(usernamePasswordToken);
            System.out.println("登录成功");

            if(subject.isAuthenticated()){
                //认证通过
                if(subject.hasRole("admin")){
                    System.out.println("该用户拥有admin的权限");
                }else{
                    System.out.println("该用户没有admin的权限");
                }

                if(subject.isPermitted("system:customer:view")){
                    System.out.println("该用户拥有system:customer:view的权限");
                }else{
                    System.out.println("该用户没有system:customer:view的权限");
                }
                if(subject.isPermitted("system:view:customer")){
                    System.out.println("该用户拥有system:view:customer的权限");
                }else{
                    System.out.println("该用户没有system:view:customer的权限");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
