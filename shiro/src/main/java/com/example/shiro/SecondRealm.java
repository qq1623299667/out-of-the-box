package com.example.shiro;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.Arrays;

public class SecondRealm extends AuthorizingRealm {
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String primaryPricncipal = (String)principalCollection.getPrimaryPrincipal();
        System.out.println("用户主体："+primaryPricncipal);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addRole("admin");
        simpleAuthorizationInfo.addRoles(Arrays.asList("system","operator"));
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken)authenticationToken;
        String userName = usernamePasswordToken.getUsername();
        //获取数据库中的用户信息
//        SimpleAccount simpleAccount = new SimpleAccount("zhaoyun","202cb962ac59075b964b07152d234b70","df",this.getName());
        SimpleAuthenticationInfo simpleAuthenticationInfo = getSimpleInfo();
        //验证用户名与token用户名是否相同
        if(simpleAuthenticationInfo.getPrincipals().asList().contains(userName)){
            return simpleAuthenticationInfo;
        }else{
            return  null;
        }
    }

    private SimpleAuthenticationInfo getSimpleInfo(){
        String salt = "12345&8";
        Md5Hash md5Hash = new Md5Hash("123");
        Md5Hash md5Hash1 = new Md5Hash("123",salt);
        Md5Hash md5Hash2 = new Md5Hash("123",salt,1024);

        return new SimpleAuthenticationInfo("zhaoyun",md5Hash2, ByteSource.Util.bytes(salt),this.getName());
    }

}
