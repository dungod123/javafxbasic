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

    @FXML
    private ListView<String> onlineUsersListView;

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

                        if (finalMessage.startsWith("USERS:")){
                            String users = finalMessage.substring(6); //-> listuser (String)

                            String[] usernames = users.split(",");

                            onlineUsersListView.getItems().clear();

                            for (String username : usernames){
                                if (!username.isBlank()){
                                    onlineUsersListView.getItems().add(username);
                                }
                            }
                            return; //neu khong thi Chatroom se cho ca: USERS:dungod123,... vao messageListView
                        }
                        messageListView.getItems().add(finalMessage);

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
        if (socketClient != null) {
            socketClient.leaveChatRoom();
            socketClient = null;
        }

        Navigator.switchScene("dashboard.fxml");
    }


}
