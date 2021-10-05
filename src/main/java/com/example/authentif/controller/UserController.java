package com.example.authentif.controller;

import com.example.authentif.model.User;
import com.example.authentif.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public User createUser(@RequestBody User user){
        return userService.saveUser(user);
    }
}
