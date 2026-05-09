package com.suka.server;

import java.io.*;
import java.net.Socket;

import static com.suka.server.chatServer.clients;

public class ClientHandler implements Runnable{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

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
            String message;

            while ((message = in.readLine())!= null){

                System.out.println(message);

                broadcast(message);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void broadcast(String message){
        for (ClientHandler client : chatServer.clients){
            client.out.println(message);
        }
    }
}
