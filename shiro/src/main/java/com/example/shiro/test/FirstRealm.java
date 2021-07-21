package com.example.shiro.test;

import org.apache.shiro.authc.*;
import org.apache.shiro.realm.AuthenticatingRealm;

public class FirstRealm extends AuthenticatingRealm {
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken)authenticationToken;
        String userName = upToken.getUsername();
        //假设这就是从数据库获取的用户信息
        SimpleAccount simpleAccount = getAccountInfo();
        if(simpleAccount.getPrincipals().asList().contains(userName)) {
            System.out.println("用户名验证通过");
            return simpleAccount;
        }else{
            return null;
//            throw new UnknownAccountException();
        }
    }

    private SimpleAccount getAccountInfo(){
        return new SimpleAccount("zhaoyun","1111",this.getName());
//        return  new SimpleAuthenticationInfo("zhaoyun","1111",this.getName());
    }
}
