package com.suka.controller;

import com.suka.model.User;
import com.suka.repository.UserRepository;
import com.suka.service.AuthService;
import com.suka.util.Navigator;
import com.suka.util.PasswordUtil;
import com.suka.validation.validationResult;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

    @FXML
    private Label messageField;

    AuthService authService=new AuthService();

    public void handleSignUp(){


        String fullname = nameField.getText();
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        //User user = new User(username, email, "USER", password);

        validationResult result = authService.signUp(username,password,email);

        messageField.setVisible(true);
        messageField.setText(result.getMessage());

        if (result.isValid()) {Navigator.switchScene("login.fxml");}
    }
}
