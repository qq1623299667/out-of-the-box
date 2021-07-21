package com.example.shiro.controller;

import com.example.shiro.vo.Result;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/test")
public class TestController {
    @GetMapping(value = "/test1",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object test1(){
        return new Result();
    }
}
