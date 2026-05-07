package com.suka.repository;

import com.suka.model.User;
import com.suka.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserRepository {
    public User login(String username, String password){
        /**
         * WHERE user name = ?: prepare statement
         */
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (
                Connection conn = DatabaseConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("password")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void signUp(User user){
        String sql = "INSERT INTO users VALUES (?, ?,?,?)";

        try (
                Connection conn = DatabaseConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());

            int rs = stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
