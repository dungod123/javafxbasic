package com.suka.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    private Socket socket;

    private BufferedReader in;

    private PrintWriter out;

    public SocketClient(String username){
        try{
            socket = new Socket(HOST , PORT);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream() , true);
        }
        catch (Exception e){
            throw new IllegalStateException(
                    "Cannot connect to chat server at " + HOST + ":" + PORT,
                    e
            );
        }
        out.println(username);
    }

    public void sendMessage(String message){
        if (out == null) {
            throw new IllegalStateException("Socket output stream is not initialized.");
        }
        out.println(message);
    }

    public BufferedReader getReader(){
        if (in == null) {
            throw new IllegalStateException("Socket input stream is not initialized.");
        }
        return in;
    }
}
