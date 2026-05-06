package com.suka.controller;

import com.suka.util.Navigator;
import javafx.fxml.FXML;

public class settingController {
    @FXML
    private void goBack() {
        Navigator.switchScene("dashboard.fxml");
    }
}
