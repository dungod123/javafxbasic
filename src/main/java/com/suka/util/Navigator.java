package com.suka.util;


import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class Navigator {
    private static Stage stage;
    // biến static để giữ reference đến cửa sổ chính (global)
    public static void setStage(Stage s){
        stage=s;
    }

    /**
     *
     * @param controllerConsumer
     * @param <T>: kieu controller: dashboardController, SettingController,...
     */
    public static <T> void switchScene(String fxml, Consumer<T> controllerConsumer) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Navigator.class.getResource("/" + fxml)
            );

            Scene scene = new Scene(loader.load());

            T controller = loader.getController();

            controllerConsumer.accept(controller);

            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void switchScene(String fxml) {
        switchScene(fxml, controller -> {});
    }
}
