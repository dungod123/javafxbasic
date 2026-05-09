package com.suka.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * Starts the chat server and accepts client connections on port 9999.
 * Each accepted socket is wrapped in a {@link ClientHandler} and processed
 * on a dedicated thread.
 */
public class chatServer {

    /**
     * Connected clients that currently participate in the chat room.
     */
    public static Set<ClientHandler> clients = new HashSet<>();

    /**
     * Boots the chat server and keeps accepting new clients until the process stops.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        try{
            ServerSocket serverSocket = new ServerSocket(9999);

            System.out.println("Chat server started ....");

            while (true){
                Socket socket = serverSocket.accept();

                System.out.println("Client connected");

                ClientHandler clientHandler = new ClientHandler(socket);

                clients.add(clientHandler);

                Thread thread = new Thread((Runnable) clientHandler);

                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
