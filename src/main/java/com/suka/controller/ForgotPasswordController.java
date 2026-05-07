package com.suka.controller;

import com.suka.repository.UserRepository;
import com.suka.service.AuthService;
import com.suka.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ForgotPasswordController {
    @FXML
    public TextField newPasswordField;
    @FXML
    public TextField emailField;
    @FXML
    public Label messageLabel;
    @FXML
    public void handleResetPassword() {
        String newPassword = newPasswordField.getText();
        String email = emailField.getText();

        boolean success = AuthService.resetPassword(email,newPassword);

        if (success){
            Navigator.switchScene("login.fxml");
        }
        else{
            messageLabel.setVisible(true);
            messageLabel.setText("RESET NOT SUCCESSFUL!!!");
        }
    }
}
