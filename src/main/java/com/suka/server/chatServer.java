package com.suka.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class chatServer {

    //All client
    public static Set<ClientHandler> clients = new HashSet<>();

    public static void main(String[] args) {
        try{
            ServerSocket serverSocket = new ServerSocket(9999);

            System.out.println("Chat server started ....");

            while (true){
                Socket socket = serverSocket.accept();

                System.out.println("Client connected");

                ClientHandler clientHandler = new ClientHandler(socket);

                clients.add(clientHandler);   //for broadcast

                Thread thread = new Thread((Runnable) clientHandler);

                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
