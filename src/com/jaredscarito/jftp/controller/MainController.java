package com.jaredscarito.jftp.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainController extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("com/jaredscarito/jftp/view/MainPage.fxml"));
        primaryStage.setTitle("JFTP");
        primaryStage.setScene(getMainScene());
        primaryStage.show();
    }

    public Scene getMainScene() {
        GridPane mainPane = new GridPane();
        // Menu Bar
        //
        // Header
        //
        // Login
        //
        // Files View
        //
        return new Scene(mainPane, 1400, 900);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
