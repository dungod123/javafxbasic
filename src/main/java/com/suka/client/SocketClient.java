package com.suka.client;

import com.suka.model.Packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.google.gson.Gson;


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

    private Gson gson = new Gson();

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


    public void sendPacket(Packet packet){
        String json = gson.toJson(packet);
        out.println(json); // client send Json to Server
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
        Packet packet = new Packet("LEAVE","SYSTEM",null,null);
        sendPacket(packet);
        close();
    }

    /**
     * Closes reader, writer, and socket resources. Multiple calls are tolerated.
     */
    public void close() {
        try {
            // Đóng socket trước để unblock reader.readLine() ở luồng khác
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {}

        try {
            if (in != null) in.close();
        } catch (IOException ignored) {}

        if (out != null) out.close();
    }
}
