package com.suka.controller;


import com.suka.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


/**
@FXML = cho phép Java “nhìn thấy” component trong FXML
fx:id="userNameField" ↔ private TextField userNameField;
*/

public class LoginController {
    @FXML
    private TextField userNameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button enterButton;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        messageLabel.setVisible(false);
    }

    @FXML
    private void handleLogin(){
        String username = userNameField.getText();
        String password = passwordField.getText();
        if (username.equals("admin") && password.equals("123")) {
            /** DOAN CODE NAY LA KHI CHUA TACH RIENG NAVIGATOR:
            try{
             //TIM FILE dashboard.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));

            //tao object UI
                Parent root = loader.load();

            //Lấy cửa sổ hiện tại theo: Button → Scene → Stage
                Stage stage = (Stage) enterButton.getScene().getWindow();
            //Thay UI
                stage.setScene(new Scene(root));
                stage.setTitle("DASHBOARD");

            }
            catch (Exception e){
                e.printStackTrace();
            }
             */

            Navigator.switchScene("dashboard.fxml");
        }
        else {
            messageLabel.setText("Wrong username or password");
            messageLabel.setVisible(true);
        }
    }


}
