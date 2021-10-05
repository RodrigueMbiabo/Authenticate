package com.example.authentif.security;

import com.example.authentif.model.User;

import java.util.Optional;

public interface UserService {

    public Optional<User> getUser(final Long id);
    public void deleteUser(final Long id);
    public User saveUser(User user);
}
