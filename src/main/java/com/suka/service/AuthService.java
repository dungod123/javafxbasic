package com.suka.service;

import com.suka.model.User;
import com.suka.repository.UserRepository;
import com.suka.util.PasswordUtil;

public class AuthService {
    private static UserRepository userRepository = new UserRepository();



    public static User login(String username, String password){

        return userRepository.login(username,password);

    }

    public static void signUp(User user){
        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
        User hashedUser = new User(user.getUsername(), user.getEmail(), user.getRole(), hashedPassword);
        userRepository.signUp(hashedUser);
    }
}
