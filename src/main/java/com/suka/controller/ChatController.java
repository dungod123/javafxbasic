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

/**
 * JavaFX controller for the chat room screen.
 * It connects the UI to the socket-based chat client and keeps the
 * message list plus online-user list in sync with server updates.
 */
public class ChatController {
    @FXML
    public Label usernameLabel;

    @FXML
    public ListView<String> messageListView;

    @FXML
    public TextField messageField;

    @FXML
    private TextField recipientField;

    @FXML
    private ListView<String> onlineUsersListView;

    private SocketClient socketClient;



    @FXML
    /**
     * Initializes the chat view, displays the current username, connects to the
     * chat server, and starts listening for server updates.
     */
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

    /**
     * Starts a background listener that reads server messages and marshals UI
     * updates back onto the JavaFX application thread.
     */
    private void startMessageListener() {
        Task<Void> task = new Task<>() {

            @Override
            protected Void call() throws Exception {

                String message;

                while ((message = socketClient.getReader().readLine())!= null){
                    String finalMessage = message;

                    Platform.runLater(() -> {

                        if (finalMessage.startsWith("USERS:")){
                            String users = finalMessage.substring(6);

                            String[] usernames = users.split(",");

                            onlineUsersListView.getItems().clear();

                            for (String username : usernames){
                                if (!username.isBlank()){
                                    onlineUsersListView.getItems().add(username);
                                }
                            }
                            return;
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
    /**
     * Sends the current text field content to the chat server.
     *
     * @param actionEvent button action event from JavaFX
     */
    public void handleSend(ActionEvent actionEvent) {
        if (socketClient == null) {
            messageListView.getItems().add("Chat server is unavailable.");
            return;
        }

        String message = messageField.getText();

        if (message.isBlank()) return;

        String recipient = recipientField.getText();

        if (recipient.isBlank()){
            socketClient.sendMessage("CHAT:"+message);
        }
        else{
            socketClient.sendMessage("DM:"+recipient+":"+message);
        }
        messageField.clear();

    }

    @FXML
    /**
     * Leaves the chat room and returns the user to the dashboard screen.
     *
     * @param actionEvent button action event from JavaFX
     */
    public void handleBackDashboard(ActionEvent actionEvent) {
        if (socketClient != null) {
            socketClient.leaveChatRoom();
            socketClient = null;
        }

        Navigator.switchScene("dashboard.fxml");
    }


}
