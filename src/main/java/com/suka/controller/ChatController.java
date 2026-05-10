package com.suka.controller;

import com.suka.client.SocketClient;
import com.suka.model.Packet;
import com.suka.session.Session;
import com.suka.util.Navigator;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;

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

    private Packet packet;

    private Gson gson = new Gson();

    private volatile boolean listening;



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
        listening = true;
        BufferedReader reader = socketClient.getReader();
        Task<Void> task = new Task<>() {

            @Override
            protected Void call() {

                String json;
                try {
                    while (listening && (json = reader.readLine()) != null) {
                        Packet packet = gson.fromJson(json, Packet.class);
                        Platform.runLater(() -> handlePacket(packet));
                    }
                } catch (IOException e) {
                    if (listening) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * CLient handle packet from server
     * @param packet
     */
    private void handlePacket(Packet packet) {
        switch (packet.getType()){
            case "CHAT":
                messageListView.getItems().add("["+packet.getSender()+"] "+packet.getMessage());
                break;
            case "DM":
                messageListView.getItems().add("[DM FROM "+packet.getSender()+"] "+packet.getMessage());
                break;
            case "SYSTEM", "LEAVE":
                messageListView.getItems().add("[SYSTEM] "+packet.getMessage());
                break;
            case "USERS":
                updateOnlineUsers(packet.getMessage());
                break;
        }
    }

    public void updateOnlineUsers(String message){
        String[] usernames = message.split(",");
        onlineUsersListView.getItems().clear();

        for (String username : usernames){
            if (!username.isBlank()){
                onlineUsersListView.getItems().add(username);
            }
        }
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
        String recipient = recipientField.getText();
        if (message.isBlank()) return;
        if (recipient.isBlank()){
            packet = new Packet("CHAT", Session.getCurrentUser().getUsername(), null, message); //GUI DEN TOAN SERVER
        }
        else{
            packet = new Packet("DM", Session.getCurrentUser().getUsername(),recipient,message); //GUI TIN RIENG
        }
        socketClient.sendPacket(packet);
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
            listening = false;

            SocketClient clientToClose = socketClient;
            socketClient = null;

            /**
             * Tao luong moi de close socket thay vi
             * truc tiep tren UI thread -> tranh deadlock
             */
            new Thread(()->{
               try {
                   clientToClose.leaveChatRoom();
               }
               catch (Exception e){
                   e.printStackTrace();
               }
            }).start();
        }

        Navigator.switchScene("dashboard.fxml");
    }


}
