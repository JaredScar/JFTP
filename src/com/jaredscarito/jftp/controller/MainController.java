package com.jaredscarito.jftp.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("com/jaredscarito/jftp/view/MainPage.fxml"));
        primaryStage.setTitle("JFTP");
        primaryStage.setScene(getMainScene());
        primaryStage.show();
    }

    private MenuItem[] jftpItems;
    private MenuItem[] tasksItems;
    private MenuItem[] helpItems;

    public void setMenuHandlers() {
        if(jftpItems != null) {
            // It is set, we can go through it
            for(MenuItem item : jftpItems) {
                switch (item.getText()) {
                    case "Preferences":
                        // Preferences
                        ImageView icon = new ImageView(new Image("com/jaredscarito/jftp/resources/settings.png"));
                        icon.setFitHeight(20);
                        icon.setFitWidth(20);
                        item.setGraphic(icon);
                        break;
                    case "Exit":
                        // Exit the program
                        break;
                }
            }
        }
        // Tasks Menu
        if(tasksItems !=null) {
            ImageView icon;
            for(MenuItem item : tasksItems) {
                switch (item.getText()) {
                    case "Create":
                        // Create
                        icon = new ImageView(new Image("com/jaredscarito/jftp/resources/create-icon.png"));
                        icon.setFitHeight(20);
                        icon.setFitWidth(20);
                        item.setGraphic(icon);
                        break;
                    case "Edit":
                        // Edit
                        icon = new ImageView(new Image("com/jaredscarito/jftp/resources/edit-icon.png"));
                        icon.setFitHeight(20);
                        icon.setFitWidth(20);
                        item.setGraphic(icon);
                        break;
                    case "Delete":
                        // Delete
                        icon = new ImageView(new Image("com/jaredscarito/jftp/resources/delete-icon.png"));
                        icon.setFitHeight(20);
                        icon.setFitWidth(20);
                        item.setGraphic(icon);
                        break;
                }
            }
        }
        // Help Menu
        if(helpItems !=null) {
            ImageView icon;
            for(MenuItem item : helpItems) {
                switch (item.getText()) {
                    case "About":
                        // About
                        icon = new ImageView(new Image("com/jaredscarito/jftp/resources/about.png"));
                        icon.setFitHeight(20);
                        icon.setFitWidth(20);
                        item.setGraphic(icon);
                        break;
                    case "Support":
                        // Support
                        icon = new ImageView(new Image("com/jaredscarito/jftp/resources/support-icon.png"));
                        icon.setFitHeight(18);
                        icon.setFitWidth(18);
                        item.setGraphic(icon);
                        break;
                }
            }
        }
    }

    public Scene getMainScene() {
        VBox root = new VBox();
        Scene mainScene = new Scene(root, 1400, 900);
        GridPane mainPane = new GridPane();
        // Menu Bar
        MenuBar menuBar = new MenuBar();
        Menu jftpMenu = new Menu("JFTP");
        MenuItem[] jftpItems = {new MenuItem("Preferences"), new MenuItem("Exit")};
        jftpMenu.getItems().addAll(jftpItems);
        this.jftpItems = jftpItems;
        Menu tasksMenu = new Menu("Tasks");
        MenuItem[] tasksItems = {new MenuItem("Create"), new MenuItem("Edit"), new MenuItem("Delete")};
        tasksMenu.getItems().addAll(tasksItems);
        this.tasksItems = tasksItems;
        Menu helpMenu = new Menu("Help");
        MenuItem[] helpItems = {new MenuItem("About"), new MenuItem("Support")};
        helpMenu.getItems().addAll(helpItems);
        this.helpItems = helpItems;
        menuBar.getMenus().addAll(jftpMenu, tasksMenu, helpMenu);

        this.setMenuHandlers();
        root.getChildren().add(menuBar);
        //
        // Header
        //
        // Login
        //
        // Files View
        //
        root.getChildren().add(mainPane);
        return mainScene;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
