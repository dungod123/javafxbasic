package com.suka.repository;

import com.suka.model.Message;
import com.suka.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository {
    public void saveMessage(Message message){
        String sql = "INSERT INTO messages (sender,recipient,type,content) VALUES (?,?,?,?)";
        try(
                Connection conn = DatabaseConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
        )
        {
            stmt.setString(1,message.getSender());
            stmt.setString(2,message.getRecipient());
            stmt.setString(3,message.getType());
            stmt.setString(4,message.getContent());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Message> getRecentMessages(){
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages ORDER BY created_at DESC LIMIT 50";

        try(
                Connection conn = DatabaseConnection.connect();

                PreparedStatement stmt = conn.prepareStatement(sql);

                ResultSet rs = stmt.executeQuery();
                ){
            while (rs.next()){
                Message message = new Message();
                message.setId(rs.getInt("id"));
                message.setSender(rs.getString("sender"));
                message.setRecipient(rs.getString("recipient"));
                message.setRecipient(rs.getString("recipient"));
                message.setType(rs.getString("type"));
                message.setContent(rs.getString("content"));
                message.setCreateAt(rs.getTimestamp("created_at"));
                messages.add(message);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return messages;
    }

    public List<Message> searchMessages(String keyword){
        String sql = "SELECT * FROM messages WHERE content LIKE ? ORDER BY created_at DESC";
        List<Message> messages = new ArrayList<>();
        try(
                Connection conn = DatabaseConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                )
        {
            stmt.setString(1,"%"+keyword+"%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                Message message = new Message();
                message.setCreateAt(rs.getTimestamp("created_at"));
                message.setId(rs.getInt("id"));
                message.setSender(rs.getString("sender"));
                message.setRecipient(rs.getString("recipient"));
                message.setType(rs.getString("type"));
                message.setContent(rs.getString("content"));
                messages.add(message);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return messages;
    }
}
