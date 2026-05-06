package com.suka.controller;


import com.suka.service.AuthService;
import com.suka.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


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


        if (AuthService.login(username, password)) {

            Navigator.switchScene("dashboard.fxml", controller -> {
                ((dashboardController) controller).setUsername(username);
            });
        }
        else {
            messageLabel.setText("Wrong username or password");
            messageLabel.setVisible(true);
        }
    }


}
