package com.jaredscarito.jftp.controller;

import com.jaredscarito.jftp.model.pages.MainPage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainController extends Application {
    public Stage mainStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("com/jaredscarito/jftp/view/MainPage.fxml"));
        Scene mainScene = getMainScene();
        mainScene.getStylesheets().add("com/jaredscarito/jftp/resources/style.css");
        primaryStage.setTitle("JFTP");
        primaryStage.setScene(mainScene);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        primaryStage.show();
        this.mainStage = primaryStage;
}

    private MenuItem[] jftpItems;
    private MenuItem[] tasksItems;
    private MenuItem[] presetItems;
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
                        // TODO Add event handler
                        break;
                    case "Exit":
                        // Exit the program
                        item.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                mainStage.close();
                                System.exit(0);
                            }
                        });
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
                        // TODO Add event handler
                        break;
                    case "Edit":
                        // Edit
                        icon = new ImageView(new Image("com/jaredscarito/jftp/resources/edit-icon.png"));
                        icon.setFitHeight(20);
                        icon.setFitWidth(20);
                        item.setGraphic(icon);
                        // TODO Add event handler
                        break;
                    case "Delete":
                        // Delete
                        icon = new ImageView(new Image("com/jaredscarito/jftp/resources/delete-icon.png"));
                        icon.setFitHeight(20);
                        icon.setFitWidth(20);
                        item.setGraphic(icon);
                        // TODO Add event handler
                        break;
                }
            }
        }
        // Presets Menu TODO
        if(presetItems !=null) {
            ImageView icon;
            for(MenuItem item : presetItems) {
                switch (item.getText()) {
                    case "Save":
                        icon = new ImageView(new Image("com/jaredscarito/jftp/resources/save-icon.png"));
                        icon.setFitHeight(20);
                        icon.setFitWidth(20);
                        item.setGraphic(icon);
                        break;
                    default:
                        // Give load icon
                        icon = new ImageView(new Image("com/jaredscarito/jftp/resources/load-icon.png"));
                        icon.setFitWidth(20);
                        icon.setFitHeight(20);
                        item.setGraphic(icon);
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
                        // TODO Add event handler
                        break;
                    case "Support":
                        // Support
                        icon = new ImageView(new Image("com/jaredscarito/jftp/resources/support-icon.png"));
                        icon.setFitHeight(18);
                        icon.setFitWidth(18);
                        item.setGraphic(icon);
                        // TODO Add event handler
                        break;
                }
            }
        }
    }

    public Scene getMainScene() {
        VBox root = new VBox();
        Scene mainScene = new Scene(root, 1400, 1000);
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
        Menu presetsMenu = new Menu("Presets");
        MenuItem[] presetItems = {new MenuItem("Save")};
        presetsMenu.getItems().addAll(presetItems);
        this.presetItems = presetItems;
        // TODO Get saved presets from data file and add them to presetsMenu with (delete button as well)
        Menu helpMenu = new Menu("Help");
        MenuItem[] helpItems = {new MenuItem("About"), new MenuItem("Support")};
        helpMenu.getItems().addAll(helpItems);
        this.helpItems = helpItems;
        menuBar.getMenus().addAll(jftpMenu, tasksMenu, presetsMenu, helpMenu);

        this.setMenuHandlers();
        root.getChildren().add(menuBar);

        MainPage mainPage = new MainPage();
        root.getChildren().add(mainPage);
        return mainScene;
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "KMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
