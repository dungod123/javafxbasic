package com.suka.controller;

import com.suka.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class dashboardController {

    @FXML
    private Label welcomeLabel;

    public void setUsername(String username) {
        welcomeLabel.setText("Welcome, " + username);
    }

    @FXML
    private void goToSetting(){
        Navigator.switchScene("setting.fxml");
    }
}
