package com.suka.controller;

import com.suka.model.User;
import com.suka.repository.UserRepository;
import com.suka.service.AuthService;
import com.suka.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.Random;

public class signUpController {


    @FXML
    public PasswordField passwordField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;



    public void handleSignUp(){

        String fullname = nameField.getText();
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
//        Random ran = new Random();
//        int id = ran.nextInt(100);
//        String Sid = String.valueOf(id);
        User user = new User(username, email, "User", password);

        AuthService.signUp(user);


        Navigator.switchScene("login.fxml");
    }
}
