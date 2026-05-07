package com.suka.controller;

import com.suka.model.User;
import com.suka.repository.UserRepository;
import com.suka.session.Session;
import com.suka.util.Navigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ManageUsersController {
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
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("usernameColumn"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));


        ObservableList<User> users = FXCollections.observableArrayList(userRepository.getAllUsers());

        userTable.setItems(users);
    }
}
