package com.example.shiro.enums;

import lombok.Getter;

@Getter
public enum  ErrorCode {
    // 统一显示
    SUCCESS(0,"操作成功"),
    FAIL(1,"操作失败"),

    // 账户权限相关
    LOGIN_SUCCESS(10000,"登录成功"),
    LOGIN_ERROR(10001,"登录失败"),
    USERNAME_PASSWORD_NOT_MATCH(10002,"账户名或密码错误！"),
    LOGOUT_SUCCESS(10003,"退出成功！"),
    ;

    Integer code;
    String msg;

    ErrorCode(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }
}
