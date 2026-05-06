package com.suka.controller;

import com.suka.util.Navigator;
import javafx.fxml.FXML;

public class dashboardController {

    @FXML
    private void goToSetting(){
        Navigator.switchScene("setting.fxml");
    }
}
