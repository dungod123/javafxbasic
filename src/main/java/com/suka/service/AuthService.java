package com.suka.service;

import com.suka.model.User;

public class AuthService {
    public static User login(String username, String password){
        if (username.equals("admin") && password.equals("123")){
            return new User(
                    1,
                    "admin",
                    "admin@gmail.com",
                    "ADMIN"
            );
        }
        return null;
    }
}
