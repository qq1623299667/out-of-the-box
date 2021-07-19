package com.example.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;

public class TestAuthenticator {
    public static void main(String[] args) {
        //第一步，获取Security Manager
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        //第二步，使用Security Manager加载配置文件，注意前面已经说过Realm是身份认证与权限的数据获取的地方，所以调用setRealm
//        defaultSecurityManager.setRealm(new IniRealm("classpath:shiro.ini"));
        defaultSecurityManager.setRealm(new FirstRealm());
        //第三步，使用SecurityUtils获取Subject
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();
        //第四步，获取用户登录Token
        UsernamePasswordToken authenticationToken = new UsernamePasswordToken("zhaoyun","daye");
//        UsernamePasswordToken authenticationToken = new UsernamePasswordToken("zhaoyun","1111");
        try {
            System.out.println(subject.isAuthenticated());
            //第五步，登录
            subject.login(authenticationToken);
            System.out.println(subject.isAuthenticated());
        } catch (UnknownAccountException e) {
            e.printStackTrace();
        }catch(IncorrectCredentialsException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}

