package com.suka.controller;

import com.suka.session.Session;
import com.suka.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ChatController {
    @FXML
    public Label usernameLabel;

    @FXML
    public ListView<String> messageListView;

    @FXML
    public TextField messageField;



    @FXML
    public void initialize(){
        usernameLabel.setText(Session.getCurrentUser().getUsername());

    }

    @FXML
    public void handleSend(ActionEvent actionEvent) {
        String message = messageField.getText();

        if (message.isBlank()) return;

        messageListView.getItems().add("[" + Session.getCurrentUser().getUsername()+"] "+ message);

        messageField.clear();

    }

    @FXML
    public void handleBackDashboard(ActionEvent actionEvent) {
        Navigator.switchScene("dashboard.fxml");
    }


}
