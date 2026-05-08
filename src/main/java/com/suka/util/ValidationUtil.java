package com.suka.util;


import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public static boolean isEmailValid(String email){
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
