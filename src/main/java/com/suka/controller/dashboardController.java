package com.suka.controller;

import com.suka.model.User;
import com.suka.session.Session;

import com.suka.util.Navigator;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class dashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {

        User user = Session.getCurrentUser();

        if (user == null) {
            Navigator.switchScene("login.fxml");
            return;
        }

        welcomeLabel.setText("Welcome, " + user.getUsername()+", "+user.getRole());
    }

    @FXML
    private void goToSetting(){
        Navigator.switchScene("setting.fxml");
    }
}
