package com.example.toolsaspect.controller;

import com.example.toolsaspect.po.User;
import com.example.toolsaspect.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping("/testLogAspect")
    public User testLogAspect(String userId){
        User user = testService.testLogAspect(userId);
        return user;
    }
}
