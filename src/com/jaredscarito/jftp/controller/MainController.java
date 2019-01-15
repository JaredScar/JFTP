package com.jaredscarito.jftp.controller;

import com.jaredscarito.jftp.model.PaneFile;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class MainController extends Application {
    public Stage mainStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("com/jaredscarito/jftp/view/MainPage.fxml"));
        Scene mainScene = getMainScene();
        mainScene.getStylesheets().add("com/jaredscarito/jftp/resources/style.css");
        primaryStage.setTitle("JFTP");
        primaryStage.setScene(mainScene);
        primaryStage.show();
        this.mainStage = primaryStage;
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
                        // TODO Add event handler
                        break;
                    case "Exit":
                        // Exit the program
                        item.setOnAction(event -> mainStage.close());
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
    public void setupConnectButton() {
        // TODO
    }

    public Button connectButton;
    public TableView myFilesView;
    public Button[] leftActionButtons;

    public Scene getMainScene() {
        VBox root = new VBox();
        Scene mainScene = new Scene(root, 1400, 900);
        GridPane mainPane = new GridPane();
        mainPane.getStyleClass().add("main-pane");
        mainPane.setAlignment(Pos.CENTER);
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
        GridPane loginPane = new GridPane();
        loginPane.getStyleClass().add("login-pane");
        loginPane.setHgap(30);
        Label hostLab = new Label("Host:");
        hostLab.getStyleClass().add("login-label");
        loginPane.add(hostLab, 0, 0);
        TextField hostField = new TextField();
        hostField.getStyleClass().add("login-textfield");
        loginPane.add(hostField, 0, 1);
        Label portLab = new Label("Port:");
        portLab.getStyleClass().add("login-label");
        loginPane.add(portLab, 1, 0);
        TextField portField = new TextField();
        portField.getStyleClass().add("login-textfield");
        loginPane.add(portField, 1, 1);
        Label userLab = new Label("Username:");
        userLab.getStyleClass().add("login-label");
        loginPane.add(userLab, 2, 0);
        TextField userField = new TextField();
        userField.getStyleClass().add("login-textfield");
        loginPane.add(userField, 2, 1);
        Label passLab = new Label("Password:");
        passLab.getStyleClass().add("login-label");
        loginPane.add(passLab, 3, 0);
        PasswordField passField = new PasswordField();
        passField.getStyleClass().add("login-textfield");
        loginPane.add(passField, 3, 1);
        Button connectBtn = new Button("Connect");
        connectBtn.getStyleClass().add("login-button");
        loginPane.add(connectBtn, 4, 1);
        this.connectButton = connectBtn;
        setupConnectButton();

        mainPane.add(loginPane, 0, 1);
        // Files View
        GridPane filesPane = new GridPane();
        filesPane.getStyleClass().add("files-pane");
        Label myFilesLabel = new Label("My Files");
        myFilesLabel.getStyleClass().add("my-files-label");
        mainPane.add(myFilesLabel, 0, 2);
        TableView myFilesView = new TableView();
        this.myFilesView = myFilesView;
        TableColumn myFileNamesCol = new TableColumn("Filename");
        myFileNamesCol.setCellValueFactory(new PropertyValueFactory<>("filename"));
        TableColumn myFileSizesCol = new TableColumn("Size");
        myFileSizesCol.setCellValueFactory(new PropertyValueFactory<>("filesize"));
        TableColumn myFileModified = new TableColumn("Last Modified");
        myFileModified.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
        myFilesView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //myFilesView.getItems().add(new PaneFile("FileName", 100, "1/14/19")); // TODO How to add files to tableView
        /** /
        myFileNamesCol.setResizable(false);
        myFileSizesCol.setResizable(false);
        myFileModified.setResizable(false);
        myFileSizesCol.setMaxWidth(20);
        myFileSizesCol.setMaxWidth(10);
        myFileModified.setMaxWidth(20);
         /**/
        myFilesView.getColumns().addAll(myFileNamesCol, myFileSizesCol, myFileModified);
        ScrollPane myFilePane = new ScrollPane();
        myFilePane.setContent(myFilesView);
        myFilesView.setPrefSize(400, 600);
        filesPane.add(myFilePane, 0, 0);

        mainPane.add(filesPane, 0, 3);

        // My Files Buttons
        ImageView[] images = {new ImageView(new Image("com/jaredscarito/jftp/resources/new-folder-icon.png")), new ImageView(new Image("com/jaredscarito/jftp/resources/folder-up-icon.png")),
                new ImageView(new Image("com/jaredscarito/jftp/resources/delete-folder-icon.png")), new ImageView(new Image("com/jaredscarito/jftp/resources/reload-icon.png")),
                new ImageView(new Image("com/jaredscarito/jftp/resources/home-icon.png"))};
        VBox iconsBox = new VBox();
        iconsBox.getStyleClass().add("icons-box");
        Button iconButton;
        Button[] leftActionButtons = new Button[images.length];
        int count = 0;
        for(ImageView imageView : images) {
            imageView.setFitWidth(30);
            imageView.setFitHeight(30);
            iconButton = new Button();
            iconButton.getStyleClass().add("icon-button");
            switch (count) {
                case 0:
                    // newFolder
                    iconButton.setId("new-folder-1");
                    break;
                case 1:
                    //folder-up
                    iconButton.setId("folder-up-1");
                    break;
                case 2:
                    //delete-folder
                    iconButton.setId("delete-folder-1");
                    break;
                case 3:
                    //reload
                    iconButton.setId("reload-1");
                    break;
                case 4:
                    //home
                    iconButton.setId("home-1");
                    break;
            }
            iconButton.setGraphic(imageView);
            iconsBox.getChildren().add(iconButton);
            leftActionButtons[count] = iconButton;
            count++;
        }
        this.leftActionButtons = leftActionButtons;
        filesPane.add(iconsBox, 1, 0);
        // Setup FileSystemView for user client
        File[] files;
        files = File.listRoots();
        /**/
        for(File file : files) {
            System.out.println(file.getName());
            String fileSize = getSizeFromBytes(file.getTotalSpace());
            FileTime lastModified = null;
            try {
                lastModified = Files.getLastModifiedTime(Paths.get(file.getAbsolutePath()));
            } catch (IOException ex) {}
            if(lastModified !=null) {
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy | hh:mm");
                this.myFilesView.getItems().add(new PaneFile(file.getAbsolutePath(), fileSize, dateFormat.format(lastModified.toMillis())));
            } else {
                this.myFilesView.getItems().add(new PaneFile(file.getAbsolutePath(), fileSize, "N/A"));
            }
        }
        // TODO Add Context-Menus for items
        /**/

        root.getChildren().add(mainPane);
        return mainScene;
    }

    public static String getSizeFromBytes(long bytesInp) {
        long bytes = bytesInp;
        long kilobytes = (bytes / 1024);
        long megabytes = (kilobytes / 1024);
        long gigabytes = (megabytes / 1024);
        long terabytes = (gigabytes / 1024);
        long petabytes = (terabytes / 1024);
        long exabytes = (petabytes / 1024);
        long zettabytes = (exabytes / 1024);
        long yottabytes = (zettabytes / 1024);
        String[] type = new String[] {"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        int track = 0;
        for(int i = 0; i < 8; i++) {
            if((bytes / 1024) > 0) {
                bytes /= 1024;
                track += 1;
            }
        }
        return bytes + " " + type[track];
    }


    public static void main(String[] args) {
        launch(args);
    }
}
