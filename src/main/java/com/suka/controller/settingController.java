package com.suka.controller;

import com.suka.session.Session;
import com.suka.util.Navigator;
import javafx.fxml.FXML;

public class settingController {
    @FXML
    private void goBack() {
        Navigator.switchScene("dashboard.fxml");
    }
    @FXML
    private void logout(){
        Session.clear();
        Navigator.switchScene("login.fxml");
    }
}
