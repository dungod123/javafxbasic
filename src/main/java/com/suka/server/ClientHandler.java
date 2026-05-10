package com.suka.server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.suka.model.Message;
import com.suka.model.Packet;
import com.suka.repository.MessageRepository;

import static com.suka.server.chatServer.clients;

/**
 * Handles a single connected chat client on the server side.
 * The handler reads inbound messages from one socket and broadcasts
 * updates to all connected clients.
 */
public class ClientHandler implements Runnable{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private boolean left;
    private Gson gson = new Gson();
    private MessageRepository messageRepository = new MessageRepository();
    private String currentRoom ="General";


    /**
     * Creates a handler for one accepted client socket.
     *
     * @param socket connected client socket
     */
    public ClientHandler(Socket socket){
        this.socket =socket;

        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    /**
     * Reads the username first, then continuously reads chat messages until the
     * client disconnects or sends the leave command.
     */
    public void run() {

        try {

            username = in.readLine();
            broadcast(new Packet("SYSTEM","SYSTEM",null,username+" JOINED THE CHAT"));
            broadcastUsers();


            //String message;
            String json;
            while ((json = in.readLine())!= null){
                /**
                 * fromJson : String json-> javaObject json
                 */
                Packet packet = gson.fromJson(json, Packet.class);
                handlePacket(packet);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            try{

                socket.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }

            leaveChatRoom();
        }
    }

    /**
     * Server handlePacket
     * @param packet
     */
    private void handlePacket(Packet packet) {
        switch (packet.getType()){
            case "CHAT":
                chatSaveMessage(packet);
                broadcastChatRoom(packet);
                break;
            case "DM":
                DMSaveMessage(packet);
                sendPrivateMessage(packet);
                break;
            case "LEAVE":
                leaveChatRoom();
                break;
            case "TYPING":
                broadcastTyping(packet);
                break;
            case "JOIN_ROOM":
                handleJoinRoom(packet);
                break;
        }
    }

    private void handleJoinRoom(Packet packet) {
        currentRoom = packet.getRoom();
        System.out.println(username +" joined "+currentRoom);
    }

    private void broadcastTyping(Packet packet) {
        for (ClientHandler client:clients){
            //khong gui lai cho sender (dont show sender : YOU are typing...)
            if (!client.username.equals(packet.getSender())) {
                client.out.println(gson.toJson(packet));
            }
        }
    }

    private void broadcastChatRoom(Packet packet){
        packet.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        for (ClientHandler client : clients){
            if (client.currentRoom != null && client.currentRoom.equals(packet.getRoom())){
                client.out.println(gson.toJson(packet));
            }
        }
    }

    private void chatSaveMessage(Packet packet) {
        Message message = new Message(packet.getSender(),null, "CHAT", packet.getMessage() );
        message.setRoom(currentRoom);
        messageRepository.saveMessage(message);
    }

    private void DMSaveMessage(Packet packet){
        Message message = new Message(packet.getSender(),packet.getRecipient(),"DM", packet.getMessage());
        message.setRoom(currentRoom);
        messageRepository.saveMessage(message);
    }

    private void broadcastChat(Packet packet) {
        packet.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        packet = new Packet("CHAT",packet.getSender(), null,packet.getMessage());
        broadcast(packet);
    }

    private void sendPrivateMessage(Packet packet) {
        packet = new Packet("DM",packet.getSender(), packet.getRecipient(), packet.getMessage());
        packet.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        String json = gson.toJson(packet);
        for (ClientHandler client:clients){
            if (client.username.equals(packet.getRecipient())){
                client.out.println(json);
                break;
            }
        }
    }

    public void leaveChatRoom(){
        if (left) {return;}
        left = true;

        chatServer.clients.remove(this);
        Packet packet = new Packet("LEAVE","SYSTEM", null,username+" LEFT THE CHAT");
        //packet.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        String json = gson.toJson(packet);
        broadcast(packet);
        broadcastUsers();
    }


    /**
     * Sends a chat message to every currently connected client.
     *
     *
     */
    public void broadcast(Packet packet){
        packet.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        String json = gson.toJson(packet);
        for (ClientHandler client : chatServer.clients){
            client.out.println(json);
        }
    }


    public void broadcastUsers(){

        /**
         * create a message "alice,bob,charles"
         */
        StringBuilder users =new StringBuilder();
        for (ClientHandler client : chatServer.clients){
            users.append(client.username).append(",");
        }
        /**
         * send Object json to client
         */
        Packet packet = new Packet("USERS", "SYSTEM", null, users.toString());
        String json = gson.toJson(packet);
        for (ClientHandler client : chatServer.clients){
            client.out.println(json);
        }

    }
}
