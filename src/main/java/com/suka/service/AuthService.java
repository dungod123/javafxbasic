package com.suka.service;

public class AuthService {
    public static boolean login(String username, String password){
        return username.equals("admin") && password.equals("123");
    }
}
