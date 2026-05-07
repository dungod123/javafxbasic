package com.suka.controller;

import com.suka.model.User;
import com.suka.repository.UserRepository;
import com.suka.session.Session;
import com.suka.util.Navigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ManageUsersController {
    private ObservableList<User> users;
    @FXML
    public TableColumn usernameColumn;
    @FXML
    public TableColumn idColumn;
    @FXML
    public TableColumn emailColumn;
    @FXML
    public TableColumn roleColumn;
    @FXML
    public TableView userTable;
    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField roleField;
    private UserRepository userRepository = new UserRepository();

    @FXML
    public void initialize(){

        User currentUser = Session.getCurrentUser();


        //route guard:
        if (currentUser == null) {
            Navigator.switchScene("login.fxml");
            return;
        }
        if (!currentUser.getRole().equals("ADMIN")){
            Navigator.switchScene("dashboard.fxml");
            return;
        }

        //mapping column = getter : id = user.getId()
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));


        users = FXCollections.observableArrayList(userRepository.getAllUsers());

        userTable.setItems(users);
    }

    @FXML
    private void handleAdd() {

        User user = new User(
                usernameField.getText(),
                emailField.getText(),
                roleField.getText(),
                ""
        );

        userRepository.addUser(user);//userRepository se modify database

        refreshTable();
    }

    @FXML
    private void handleUpdate() {
        /**
         * chi update object user
         * Khong chinh sua database (trong user repository se lam )
         */

        User selectedUser = (User) userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            return;
        }

        selectedUser.setUsername(usernameField.getText()
        );

        selectedUser.setEmail(emailField.getText());

        selectedUser.setRole(roleField.getText());

        userRepository.updateUser(selectedUser); //userRepository se modify database

        refreshTable();
    }
    @FXML
    private void handleDelete() {

        User selectedUser = (User) userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            return;
        }

        userRepository.deleteUser(selectedUser.getId());//userRepository se modify database

        refreshTable();
    }
    private void refreshTable() {

        users.setAll(userRepository.getAllUsers());
    }
}
