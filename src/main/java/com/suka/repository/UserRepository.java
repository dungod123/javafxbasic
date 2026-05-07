package com.suka.repository;

import com.suka.model.User;
import com.suka.util.DatabaseConnection;
import com.suka.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    public User login(String username, String password){
        /**
         * WHERE user name = ?: prepare statement
         */
        String sql = "SELECT * FROM users WHERE username = ?";
        try (
                Connection conn = DatabaseConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, username);


            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                String hashedPassword = rs.getString("password");
                boolean correctedPassword = PasswordUtil.verifyPassword(password , hashedPassword);

                if (correctedPassword){
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("role"),
                            rs.getString("password")
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void signUp(User user){

        String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?,?,?)";

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

    public List<User> getAllUsers(){
        List<User> users = new ArrayList<>();

        String sql = "SELECT * FROM users";

        try(
                Connection conn = DatabaseConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
        ){
            while (rs.next()){
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("password")

                );
                users.add(user);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return users;
    }
    public void addUser(User user) {

        String sql =
                "INSERT INTO users(username, password, email, role) VALUES (?, ?, ?, ?)";

        try (
                Connection conn = DatabaseConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, user.getUsername());

            stmt.setString(2, user.getPassword());

            stmt.setString(3, user.getEmail());

            stmt.setString(4, user.getRole());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateUser(User user) {

        String sql = "UPDATE users SET username = ?, email = ?, role = ? WHERE id = ?";

        try (
                Connection conn = DatabaseConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole());
            stmt.setInt(4, user.getId());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void deleteUser(int id) {

        String sql = "DELETE FROM users WHERE id = ?";

        try (
                Connection conn = DatabaseConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
