package com.suka.controller;


import com.suka.model.User;
import com.suka.service.AuthService;
import com.suka.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import com.suka.session.Session;
/**
@FXML = cho phép Java “nhìn thấy” component trong FXML
fx:id="userNameField" ↔ private TextField userNameField;
*/

public class LoginController {
    @FXML
    private TextField userNameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button enterButton;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        messageLabel.setVisible(false);
    }

    @FXML
    private void handleLogin(){
        String username = userNameField.getText();
        String password = passwordField.getText();

        User user = AuthService.login(username,password);


        if (user != null) {

//            Navigator.switchScene("dashboard.fxml", controller -> {
//                ((dashboardController) controller).setUsername(username);
//            });

            Session.setCurrentUser(user);
            Navigator.switchScene("dashboard.fxml");
        }
        else {
            messageLabel.setText("Wrong username or password");
            messageLabel.setVisible(true);
        }
    }


}
