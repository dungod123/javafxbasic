package com.suka.server;

import java.io.*;
import java.net.Socket;

import static com.suka.server.chatServer.clients;

/**
 *  CLASS NAY: SERVER SENT PACKAGE (GOI DU LIEU) -> CLIENT UPDATE STATE + UI
 */
public class ClientHandler implements Runnable{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private String username;
    private boolean left;

    public ClientHandler(Socket socket){
        this.socket =socket;

        try{
            //luong doc
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //luong ghi
            out = new PrintWriter(socket.getOutputStream(), true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void run() {

        try {

            //khi tham gia phong chat:
            username = in.readLine();
            broadcast("[SYSTEM] "+ username +" joined the chat");
            broadcastUsers();


            String message;
            while ((message = in.readLine())!= null){
                if ("/leave".equals(message)) {
                    break;
                }

                broadcast("["+username+"] "+message);

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

    public void leaveChatRoom(){
        if (left) {
            return;
        }
        left = true;

        chatServer.clients.remove(this);
        broadcast("[SYSTEM] "+ username +" left the chat");
        broadcastUsers();
    }


    public void broadcast(String message){
        /**
         * Gui package tin nhan de cap nhap vao chatbox: messageListView
         */
        for (ClientHandler client : chatServer.clients){
            client.out.println(message);
        }
    }

    public void broadcastUsers(){
        /**
         * GUI PACKAGE cho client :"USERS:dungod123,arisuka,bobby789" ->list nhung nguoi online
         * de cap nhap onlineUsersListView
         */

        StringBuilder users = new StringBuilder("USERS:");

        for (ClientHandler client : chatServer.clients){
            users.append(client.username).append(","); //cap nhap nhung nguoi dang Online
        }

        String userList = users.toString();

        for (ClientHandler client : chatServer.clients){
            client.out.println(userList);
        }

    }
}
