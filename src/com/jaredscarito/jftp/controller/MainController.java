package com.jaredscarito.jftp.controller;

import com.jaredscarito.jftp.model.FTPConnect;
import com.jaredscarito.jftp.model.PaneFile;
import com.jaredscarito.jftp.model.pages.MainPage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

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
        this.main = this;
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
        this.connectButton.getStyleClass().add("connect-btn");
        this.connectButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY) {
                    String host = hostField.getText();
                    int port = 0;
                    try {
                        port = Integer.parseInt(portField.getText());
                    } catch (NumberFormatException ex) {}
                    String username = userField.getText();
                    String pass = passField.getText();
                    connection = new FTPConnect(host, port, username, pass);
                    if(connection.connect()) {
                        for(FTPFile file : connection.getFiles()) {
                            String name = file.getName();
                            String size = humanReadableByteCount(file.getSize(), true);
                            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy | hh:mm");
                            String lastModified = dateFormat.format(file.getTimestamp().getTimeInMillis());
                            ftpFilesView.getItems().add(new PaneFile(name, size, lastModified));
                        }
                        /** /
                        for(FTPFile file : connection.getDirectories()) {
                            String name = file.getName();
                            String size = humanReadableByteCount(file.getSize(), true);
                            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy | hh:mm");
                            String lastModified = dateFormat.format(file.getTimestamp().getTimeInMillis());
                            ftpFilesView.getItems().add(new PaneFile(name, size, lastModified));
                        }
                        /**/
                        connectButton.setText("Disconnect");
                    } else {
                        JOptionPane.showMessageDialog(null, "FAILED TO CONNECT TO FTP SERVER", "ERROR",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    public MainController main;

    public FTPConnect connection;

    public Button connectButton;
    private TextField hostField;
    private TextField portField;
    private TextField userField;
    private TextField passField;

    public TextField myURLField;
    public TextField ftpURLField;
    public TableView myFilesView;
    public TableView ftpFilesView;

    public String myCurrentDirectory = "ROOT1337";
    public String ftpCurrentDirectory = "ROOT1337";

    public Scene getMainScene() {
        VBox root = new VBox();
        Scene mainScene = new Scene(root, 1400, 1000);
        MainPage mainPage = new MainPage();
        root.getChildren().add(mainPage);
        return mainScene;
    }

    public void setupMyCurrentDirectory() {
        myFilesView.getItems().clear();
        for (File file : new File(myCurrentDirectory).listFiles()) {
            String fileSize = "";
            if (!file.isDirectory()) {
                fileSize = humanReadableByteCount((file.length()), true);
            }
            FileTime lastModified = null;
            try {
                lastModified = Files.getLastModifiedTime(Paths.get(file.getAbsolutePath()));
            } catch (IOException ex) {
            }
            if (lastModified != null) {
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy | hh:mm");
                myFilesView.getItems().add(new PaneFile(file.getName(), fileSize, dateFormat.format(lastModified.toMillis())));
            } else {
                myFilesView.getItems().add(new PaneFile(file.getName(), fileSize, "N/A"));
            }
        }
        this.myURLField.setText(myCurrentDirectory);
    }
    public void setupMyRootDirectory() {
        myFilesView.getItems().clear();
        for(File file : File.listRoots()) {
            String fileSize = humanReadableByteCount((file.getTotalSpace() - file.getFreeSpace()), true);
            FileTime lastModified = null;
            try {
                lastModified = Files.getLastModifiedTime(Paths.get(file.getAbsolutePath()));
            } catch (IOException ex) {}
            if(lastModified !=null) {
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy | hh:mm");
                myFilesView.getItems().add(new PaneFile(file.getAbsolutePath(), fileSize, dateFormat.format(lastModified.toMillis())));
            } else {
                myFilesView.getItems().add(new PaneFile(file.getAbsolutePath(), fileSize, "N/A"));
            }
        }
        myCurrentDirectory = "ROOT1337";
        this.myURLField.setText("/");
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
