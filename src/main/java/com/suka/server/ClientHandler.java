package com.suka.server;

import java.io.*;
import java.net.Socket;

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
            broadcast("[SYSTEM] "+ username +" joined the chat");
            broadcastUsers();


            String message;
            while ((message = in.readLine())!= null){
                if ("/leave".equals(message)) {
                    break;
                }

                if (message.startsWith("CHAT:")){
                    String content = message.substring(5);
                    broadcast("["+username+"] "+content);
                }

                else if (message.startsWith("DM:")){
                    String[] parts = message.split(":",3);

                    String recipient = parts[1];
                    String content = parts[2];

                    sendPrivateMessage(recipient, content);
                }

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

    private void sendPrivateMessage(String recipient, String content) {
        for (ClientHandler client:clients){
            if (client.username.equals(recipient)){
                client.out.println("[DM FROM "+ username +"] "+content);
            }
        }
    }

    /**
     * Removes this client from the active client set and notifies the remaining
     * users that the client has left.
     */
    public void leaveChatRoom(){
        if (left) {
            return;
        }
        left = true;

        chatServer.clients.remove(this);
        broadcast("[SYSTEM] "+ username +" left the chat");
        broadcastUsers();
    }


    /**
     * Sends a chat message to every currently connected client.
     *
     * @param message formatted message to broadcast
     */
    public void broadcast(String message){
        for (ClientHandler client : chatServer.clients){
            client.out.println(message);
        }
    }

    /**
     * Broadcasts the current online user list using the {@code USERS:} protocol
     * understood by the chat client UI.
     */
    public void broadcastUsers(){
        StringBuilder users = new StringBuilder("USERS:");

        for (ClientHandler client : chatServer.clients){
            users.append(client.username).append(",");
        }

        String userList = users.toString();

        for (ClientHandler client : chatServer.clients){
            client.out.println(userList);
        }

    }
}
