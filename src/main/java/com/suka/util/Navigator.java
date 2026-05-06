package com.suka.util;


import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Navigator {
    private static Stage stage;
    // biến static để giữ reference đến cửa sổ chính (global)
    public static void setStage(Stage s){
        stage=s;
    }
    public static void switchScene(String fxml){
        try {
            // load file FXML giống hệt code cũ:
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource("/"+fxml));

            Scene scene = new Scene(loader.load());

            // dùng stage global (không cần lấy từ button nữa):
            stage.setScene(scene);
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }
}
