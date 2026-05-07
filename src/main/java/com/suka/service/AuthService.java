package com.suka.service;

import com.suka.model.User;
import com.suka.repository.UserRepository;

public class AuthService {
    private static UserRepository userRepository = new UserRepository();

    public static User login(String username, String password){

        return userRepository.login(username,password);

    }
}
