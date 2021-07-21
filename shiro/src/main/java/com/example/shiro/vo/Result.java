package com.example.shiro.vo;

import com.example.shiro.enums.ErrorCode;
import lombok.Data;

@Data
public class Result {
    Integer code;
    String msg;
    Object data;

    public Result(){
        this.code = ErrorCode.SUCCESS.getCode();
        this.msg = ErrorCode.SUCCESS.getMsg();
        this.data = null;
    }

    public Result(Object data){
        this.code = ErrorCode.SUCCESS.getCode();
        this.msg = ErrorCode.SUCCESS.getMsg();
        this.data = data;
    }

    public Result(ErrorCode errorCode){
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
        this.data = null;
    }

    public Result(ErrorCode errorCode,Object data){
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
        this.data = data;
    }
}
