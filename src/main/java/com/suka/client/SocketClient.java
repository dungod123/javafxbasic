package com.suka.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Lightweight TCP chat client used by the JavaFX chat screen.
 * It connects to the local chat server, sends plain text messages,
 * and exposes the socket reader for the UI listener thread.
 */
public class SocketClient {
    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    private Socket socket;

    private BufferedReader in;

    private PrintWriter out;

    /**
     * Opens a socket connection to the chat server and immediately sends the username
     * as the first line so the server can register the client.
     *
     * @param username username announced to the chat server
     */
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

    /**
     * Sends one chat message to the server.
     *
     * @param message raw message body entered by the user
     */
    public void sendMessage(String message){
        if (out == null) {
            throw new IllegalStateException("Socket output stream is not initialized.");
        }
        out.println(message);
    }

    /**
     * Returns the buffered reader used by the UI thread to receive server messages.
     *
     * @return socket reader for inbound messages
     */
    public BufferedReader getReader(){
        if (in == null) {
            throw new IllegalStateException("Socket input stream is not initialized.");
        }
        return in;
    }

    /**
     * Requests to leave the chat room and then closes local socket resources.
     */
    public void leaveChatRoom() {
        if (out != null) {
            out.println("/leave");
        }
        close();
    }

    /**
     * Closes reader, writer, and socket resources. Multiple calls are tolerated.
     */
    public void close() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException ignored) {
        }

        if (out != null) {
            out.close();
        }

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }
}
