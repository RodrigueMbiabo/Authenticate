package com.example.authentif.security.services.impl;

import com.example.authentif.model.User;
import com.example.authentif.repository.UserRepository;
import com.example.authentif.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public Optional<User> getUser(Long id) {
        return Optional.empty();
    }

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public User saveUser(User user) {
        User userSave = userRepository.save(user);
        return userSave;
    }
}
