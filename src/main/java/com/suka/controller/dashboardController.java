package com.suka.controller;

import com.suka.model.User;
import com.suka.session.Session;

import com.suka.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class dashboardController {
    User user = Session.getCurrentUser();
    @FXML
    private Label welcomeLabel;

    @FXML
    private Button manager;

    @FXML
    public void initialize() {

        if (user == null) {
            Navigator.switchScene("login.fxml");
            return;
        }

        //UI guard
        if (!user.getRole().equals("ADMIN")){
            manager.setVisible(false);
        }

        welcomeLabel.setText("Welcome, " + user.getUsername()+", "+user.getRole());
    }

    @FXML
    private void goToSetting(){
        Navigator.switchScene("setting.fxml");
    }

    @FXML
    private void goToManageUser(){
        Navigator.switchScene("manage-users.fxml");

    }

    @FXML
    public void goToChat(){
        Navigator.switchScene("chat.fxml");
    }
}
