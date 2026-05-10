package com.suka.controller;

import com.suka.client.SocketClient;
import com.suka.model.Message;
import com.suka.model.Packet;
import com.suka.repository.MessageRepository;
import com.suka.session.Session;
import com.suka.util.Navigator;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import com.google.gson.Gson;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;


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
    public ListView<String> roomListView;

    @FXML
    public Label currentRoomLabel;

    @FXML
    private TextField recipientField;

    @FXML
    private ListView<String> onlineUsersListView;

    private SocketClient socketClient;

    private Packet packet;

    private Gson gson = new Gson();

    private volatile boolean listening;

    private MessageRepository messageRepository = new MessageRepository();

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");




    @FXML
    private TextField searchField;

    private ObservableList<String> allMessages = FXCollections.observableArrayList();
    private FilteredList<String> filteredMessages;

    @FXML
    private Label typingLabel;
    private Set<String> typingUsers = new HashSet<>();





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

        // 2. Thiết lập cơ chế lọc Real-time
        // Tạo FilteredList bọc quanh danh sách gốc, mặc định cho hiện tất cả (s -> true)

        filteredMessages = new FilteredList<>(allMessages);

        searchField.textProperty().addListener(
                (observable,oldValue,newValue)->{
                    filteredMessages.setPredicate(message -> {
                        if (newValue == null || newValue.isBlank()) {
                            return true;
                        }
                        String lowerCaseFilter = newValue.toLowerCase();

                        return message.toLowerCase().contains(lowerCaseFilter);
                            }

                    );

                }
        );

        // 4. Kết nối danh sách đã lọc với ListView trên giao diện
        // Từ nay, bạn chỉ cần thêm tin nhắn vào 'allMessages', giao diện sẽ tự cập nhật

        messageListView.setItems(filteredMessages);


        //dungod123 is typing ...
        //property java la reactive UI -> realtime
        messageField.textProperty().addListener(
                (observable,oldValue,newValue)->{
                    sendTypingEvent();
                }
        );


        roomListView.getItems().addAll("General" , "Anime" ,"Gaming");
        roomListView.getSelectionModel().select("General");

        roomListView.getSelectionModel().selectedItemProperty().addListener(
                (obs,oldRoom,newRoom) ->{
                    if (newRoom != null){
                        joinRoom(newRoom);
                    }
                }
        );

        startMessageListener();

    }


    private void joinRoom(String room) {
        currentRoomLabel.setText("ROOM: " + room);
        loadRecentRoomMessages();

        Packet packet = new Packet();
        packet.setType("JOIN_ROOM");
        packet.setRoom(room);
        packet.setSender(Session.getCurrentUser().getUsername());
        socketClient.sendPacket(packet);
    }

    private void sendTypingEvent() {
        Packet packet = new Packet();
        packet.setType("TYPING");
        packet.setSender(Session.getCurrentUser().getUsername());

        socketClient.sendPacket(packet);
    }

//    private void loadRecentMessages() {
//        allMessages.clear();
//        List<Message> messages = messageRepository.getRecentMessages();
//        Collections.reverse(messages);
//
//        for (Message message : messages){
//            String display = formatMessage(message);
//            if (!display.isEmpty()){
//                allMessages.add(display);
//            }
//        }
//    }
    private void loadRecentRoomMessages() {
        String room = currentRoomLabel.getText().replace("ROOM: ","");
        allMessages.clear();
        List<Message> messages = messageRepository.getRecentRoomMessages(room);
        Collections.reverse(messages);

        for (Message message : messages){
            String display = formatMessage(message);
            if (!display.isEmpty()){
                allMessages.add(display);
            }
        }
    }

    public String formatMessage(Message message){
        if (message.getCreatedAt() == null) return "";
        String time = message.getCreatedAt().toLocalDateTime().format(formatter);
        String display = "["+time+"]";
        if ("DM".equals(message.getType())){
            display +="[DM FROM "+message.getSender()+"] "+message.getContent();
        }
        else if ("CHAT".equals(message.getType())){
            display +="[" + message.getSender() +"] "+message.getContent();
        }

        return display;
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
        String display = "";
        switch (packet.getType()){
            case "CHAT":
                display="["+packet.getTimestamp()+"]"+"["+packet.getSender()+"] "+packet.getMessage();
                break;
            case "DM":
                display="["+packet.getTimestamp()+"]"+"[DM FROM "+packet.getSender()+"] "+packet.getMessage();
                break;
            case "SYSTEM", "LEAVE":
                display="["+packet.getTimestamp()+"]"+"[SYSTEM] "+packet.getMessage();
                break;
            case "USERS":
                updateOnlineUsers(packet.getMessage());
                break;
            case "TYPING":
                showTypingIndicator(packet.getSender());
        }
        if (!display.isEmpty()){
            allMessages.add(display);
        }
    }

    private void showTypingIndicator(String username) {
        typingUsers.add(username);
        updateTypingLabel();
//        typingLabel.setText(username+" is typing...");   OLD
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e->{
            typingUsers.remove(username);
//            typingLabel.setText(""); ->OLD
            updateTypingLabel();
        });
        pause.play();
    }

    private void updateTypingLabel() {
        if (typingUsers.isEmpty()){
            typingLabel.setText("");
            return;
        }
        else{
            String users = String.join(",", typingUsers);
            if (typingUsers.size() == 1) {
                typingLabel.setText(users+ " is typing");
            }
            else{
                typingLabel.setText(users+ "are typing");
            }
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
            packet.setRoom(currentRoomLabel.getText().replace("ROOM: ",""));
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

    @FXML
    public void handleSearch(){
        String keyword = searchField.getText();
        if (keyword.isBlank()){
            loadRecentRoomMessages();
            return;
        }
        else {
            messageListView.getItems().clear();
            List<Message> messages=messageRepository.searchMessages(keyword);
            Collections.reverse(messages);
            for (Message message:messages){
                String time = message.getCreatedAt().toLocalDateTime().format(formatter);

                String display = "["+time+"]";
                if ("DM".equals(message.getType())){
                    display +="[DM FROM "+message.getSender()+"] "+message.getContent();
                }
                else if ("CHAT".equals(message.getType())){
                    display +="[" + message.getSender() +"] "+message.getContent();
                }
                messageListView.getItems().add(display);
            }
        }
    }
}
