package com.example.authentif.controller;

import com.example.authentif.model.User;
import com.example.authentif.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable("id") Long id){
        Optional<User> user = userService.getUser(id);
        return user.orElse(null);
    }

    @PostMapping("/user")
    public User createUser(@RequestBody User user){
        return userService.saveUser(user);
    }
}
