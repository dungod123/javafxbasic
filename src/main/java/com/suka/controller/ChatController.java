package com.suka.controller;

import com.suka.client.SocketClient;
import com.suka.session.Session;
import com.suka.util.Navigator;
import javafx.application.Platform;
import javafx.concurrent.Task;
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

    private SocketClient socketClient;



    @FXML
    public void initialize(){
        usernameLabel.setText(Session.getCurrentUser().getUsername());

        try {
            socketClient = new SocketClient(Session.getCurrentUser().getUsername());
        } catch (IllegalStateException e) {
            messageListView.getItems().add("Cannot connect to chat server. Start server on port 9999.");
            return;
        }

        startMessageListener();

    }

    private void startMessageListener() {
        Task<Void> task = new Task<>() {

            @Override
            protected Void call() throws Exception {

                String message;

                while ((message = socketClient.getReader().readLine())!= null){
                    String finalMessage = message;

                    Platform.runLater(() -> {

                        messageListView.getItems().add(finalMessage); //add vao bang chat (listview)

                    });
                }
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void handleSend(ActionEvent actionEvent) {
        if (socketClient == null) {
            messageListView.getItems().add("Chat server is unavailable.");
            return;
        }

        String message = messageField.getText();

        if (message.isBlank()) return;

//        messageListView.getItems().add("[" + Session.getCurrentUser().getUsername()+"] "+ message);->FAKE MESSAGE

        socketClient.sendMessage(message);

        messageField.clear();

    }

    @FXML
    public void handleBackDashboard(ActionEvent actionEvent) {
        Navigator.switchScene("dashboard.fxml");
    }


}
