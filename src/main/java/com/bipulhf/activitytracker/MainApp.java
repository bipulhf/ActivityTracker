package com.bipulhf.activitytracker;

import com.bipulhf.activitytracker.classes.GetList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("application.css")).toExternalForm());
        stage.setTitle("Activity Tracker");

        Image icon = new Image(String.valueOf(getClass().getResource("icon.png")));
        stage.getIcons().add(icon);

        stage.setOnCloseRequest(event -> {
            if(MainController.time > 1)
                event.consume();
            else {
                while (true) {
                    if(MainController.pool == null || MainController.pool.isShutdown()) {
                        Platform.exit();
                        break;
                    }
                }
            }
        });
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        File dir = new File(GetList.getDirectoryName());
        if(!dir.exists()) dir.mkdir();
        GetList.makeFile();
        launch();
    }
}