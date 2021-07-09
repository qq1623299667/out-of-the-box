package com.example.toolsaspect.service;

import com.example.toolsaspect.po.User;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    public User testLogAspect(String userId) {
        User user = new User();
        user.setUserId(userId);
        user.setName("name"+userId);
        user.setAge(userId.hashCode()%100);
        return user;
    }
}
