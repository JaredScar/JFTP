package com.jaredscarito.jftp.controller;

import com.jaredscarito.jftp.model.FTPConnect;
import com.jaredscarito.jftp.model.FileWalker;
import com.jaredscarito.jftp.model.PaneFile;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
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
        this.connectButton.getStyleClass().add("connect-btn");
        this.connectButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY) {
                    String host = hostField.getText();
                    int port = Integer.parseInt(portField.getText());
                    String username = userField.getText();
                    String pass = passField.getText();
                    connection = new FTPConnect(main, host, port, username, pass);
                    if(connection.connect()) {
                        for(FTPFile file : connection.getFiles()) {
                            String name = file.getName();
                            String size = humanReadableByteCount(file.getSize(), true);
                            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy | hh:mm");
                            String lastModified = dateFormat.format(file.getTimestamp().getTimeInMillis());
                            ftpFilesView.getItems().add(new PaneFile(name, size, lastModified));
                        }
                        connectButton.setText("Disconnect");
                    }
                }
            }
        });
    }
    public void setupActionButtons() {
        // TODO
    }

    public MainController main;

    public FTPConnect connection;

    public Button connectButton;
    private TextField hostField;
    private TextField portField;
    private TextField userField;
    private TextField passField;

    public TableView myFilesView;
    public TableView ftpFilesView;
    public Button[] leftActionButtons;
    public Button[] rightActionButtons;

    public String myCurrentDirectory = "Root";
    public String ftpCurrentDirectory = "Root";

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
        this.hostField = hostField;
        this.portField = portField;
        this.userField = userField;
        this.passField = passField;
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
        //myFilesView.getItems().add(new PaneFile("FileName", 100, "1/14/19")); // TODO NOTE: How to add files to tableView
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
                    iconButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            myFilesView.getItems().clear();
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if(!myCurrentDirectory.equals("Root")) {
                                        for (File file : new File(myCurrentDirectory).listFiles()) {
                                            String fileSize = humanReadableByteCount(file.length(), true);
                                            FileTime lastModified = null;
                                            try {
                                                lastModified = Files.getLastModifiedTime(Paths.get(file.getAbsolutePath()));
                                            } catch (IOException ex) {
                                            }
                                            if (lastModified != null) {
                                                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy | hh:mm");
                                                myFilesView.getItems().add(new PaneFile(file.getAbsolutePath(), fileSize, dateFormat.format(lastModified.toMillis())));
                                            } else {
                                                myFilesView.getItems().add(new PaneFile(file.getAbsolutePath(), fileSize, "N/A"));
                                            }
                                        }
                                    } else {
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
                                    }
                                }
                            }, 100L);
                        }
                    });
                    break;
                case 4:
                    //home
                    iconButton.setId("home-1");
                    iconButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
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
                        }
                    });
                    break;
            }
            iconButton.setGraphic(imageView);
            iconsBox.getChildren().add(iconButton);
            leftActionButtons[count] = iconButton;
            count++;
        }
        this.leftActionButtons = leftActionButtons;
        filesPane.add(iconsBox, 1, 0);
        /**
         * USER CLIENT FTP VIEW FILES:
         */
        // Setup FileSystemView for user client
        File[] files;
        files = File.listRoots();
        /**/
        for(File file : files) {
            String fileSize = humanReadableByteCount((file.getTotalSpace() - file.getFreeSpace()), true);
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
        // Add ContextMenu for table
        ContextMenu cm = new ContextMenu();
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");
        cm.getItems().addAll(copyItem, pasteItem);
        this.myFilesView.setContextMenu(cm);
        // Add actions for ContextMenu
        copyItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // TODO Copy the directory/file
            }
        });
        pasteItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // TODO Paste the copied directory/file in selected directory
            }
        });
        // Set up double click action on table rows
        this.myFilesView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            private int clickCount = 0;
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY) {
                    clickCount++;
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            clickCount = 0;
                        }
                    }, 500L);
                    if(clickCount == 2) {
                        clickCount = 0;
                        // TODO Open sub-directories if is directory
                        PaneFile pf = (PaneFile) myFilesView.getSelectionModel().getSelectedItem();
                        File file = new File(pf.getFilename());
                        myCurrentDirectory = file.getAbsolutePath();
                        if(file.isDirectory()) {
                            myFilesView.getItems().clear();
                            for (File subFile : file.listFiles()) {
                                String fileSize = humanReadableByteCount((subFile.length()), true);
                                // TODO Figure out File Size
                                FileTime lastModified = null;
                                try {
                                    lastModified = Files.getLastModifiedTime(Paths.get(subFile.getAbsolutePath()));
                                } catch (IOException ex) {}
                                if(lastModified !=null) {
                                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy | hh:mm");
                                    myFilesView.getItems().add(new PaneFile(subFile.getAbsolutePath(), fileSize, dateFormat.format(lastModified.toMillis())));
                                } else {
                                    myFilesView.getItems().add(new PaneFile(subFile.getAbsolutePath(), fileSize, "N/A"));
                                }
                            }
                        }
                    }
                }
            }
        });
        /**/
        /**
         * FTP SERVER FILES VIEW:
         */

        root.getChildren().add(mainPane);
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
