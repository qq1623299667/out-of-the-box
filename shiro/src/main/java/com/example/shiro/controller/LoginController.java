package com.example.shiro.controller;

import com.example.shiro.enums.ErrorCode;
import com.example.shiro.vo.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class LoginController {
    @PostMapping("/login")
    public Result login(String username,String password){
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username,password);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(usernamePasswordToken);
        } catch (UnknownAccountException e) {
            e.printStackTrace();
            System.out.println("用户名错误");
            return new Result(ErrorCode.USERNAME_PASSWORD_NOT_MATCH);
        } catch(IncorrectCredentialsException e){
            e.printStackTrace();
            System.out.println("密码错误");
            return new Result(ErrorCode.USERNAME_PASSWORD_NOT_MATCH);
        }
        return new Result(ErrorCode.LOGIN_SUCCESS);
    }

    @PostMapping("/logout")
    public Object logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return new Result(ErrorCode.LOGOUT_SUCCESS);
    }
}
