package com.suka.service;

import com.suka.model.User;
import com.suka.repository.UserRepository;
import com.suka.util.PasswordUtil;
import com.suka.util.ValidationUtil;
import com.suka.validation.validationResult;


/**
 * LOGIC NAM TRONG AuthService va UserRepository chu khong nam trong Controller!!!
 */

/**
 * nhien vu cua AuthService: logic login, sign up, check password...
 */
public class AuthService {
    private static UserRepository userRepository = new UserRepository();



    public static User login(String username, String password){

        return userRepository.login(username,password);

    }

//    public static void signUp(User user){
//        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
//        User hashedUser = new User(user.getUsername(), user.getEmail(), user.getRole(), hashedPassword);
//        userRepository.signUp(hashedUser);
//    }

    public static boolean resetPassword(String email,String newPassword){
        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        return userRepository.updatePassword(email,hashedPassword);

    }

    public validationResult signUp(String username, String password, String email){
        if (username.isBlank()||password.isBlank()||email.isBlank()){
            return new validationResult(false, "ALL FIELDS REQUIRED");
        }

        if (!ValidationUtil.isEmailValid(email)){
            return new validationResult(false, "EMAIL IS INVALID");
        }

        if (userRepository.existsByUsername(username)){
            return new validationResult(false, "USERNAME EXISTS");
        }

        if (password.length() <6){
            return new validationResult(false,"PASSWORD IS TOO SHORT");
        }
        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = new User(username, email, "USER", hashedPassword);
        userRepository.signUp(user);

        return new validationResult(true, "SIGN UP SUCCESSFULLY");
    }
}
