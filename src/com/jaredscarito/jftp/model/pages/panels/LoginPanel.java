package com.jaredscarito.jftp.model.pages.panels;

import com.jaredscarito.jftp.model.AppSettings;
import com.jaredscarito.jftp.model.FTPConnect;
import com.jaredscarito.jftp.model.PaneFile;
import com.jaredscarito.jftp.model.pages.MainPage;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.apache.commons.net.ftp.FTPFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class LoginPanel extends Panel {
    private String name;
    private TextField hostField;
    private TextField portField;
    private TextField userField;
    private TextField passField;
    private FTPConnect connection;
    private Button connectButton;
    public LoginPanel() {
        init();
    }
    public LoginPanel(String name, List<String> styleClasses) {
        super(name, styleClasses);
    }
    public LoginPanel(String name, String styleClass) {
        super(name, styleClass);
    }

    public FTPConnect getConnection() {
        return this.connection;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void init() {
        setPadding(new Insets(10, 12, 10, 12));
        setHgap(8);
        setMaxWidth(Double.MAX_VALUE);
        setAlignment(Pos.CENTER_LEFT);

        ColumnConstraints hostCol = new ColumnConstraints();
        hostCol.setHgrow(Priority.ALWAYS);
        hostCol.setFillWidth(true);
        hostCol.setMinWidth(160);
        ColumnConstraints portCol = new ColumnConstraints();
        portCol.setMinWidth(76);
        portCol.setPrefWidth(84);
        portCol.setMaxWidth(100);
        ColumnConstraints userCol = new ColumnConstraints();
        userCol.setHgrow(Priority.SOMETIMES);
        userCol.setFillWidth(true);
        userCol.setMinWidth(120);
        userCol.setPrefWidth(160);
        ColumnConstraints passCol = new ColumnConstraints();
        passCol.setHgrow(Priority.SOMETIMES);
        passCol.setFillWidth(true);
        passCol.setMinWidth(120);
        passCol.setPrefWidth(160);
        ColumnConstraints buttonCol = new ColumnConstraints();
        buttonCol.setMinWidth(118);
        buttonCol.setPrefWidth(128);
        getColumnConstraints().addAll(hostCol, portCol, userCol, passCol, buttonCol);

        TextField hostField = new TextField();
        hostField.setPromptText("Host");
        hostField.setMaxWidth(Double.MAX_VALUE);
        hostField.getStyleClass().add("login-textfield");
        add(hostField, 0, 0);
        TextField portField = new TextField(Integer.toString(AppSettings.defaultPort()));
        portField.setPromptText("Port");
        portField.setMaxWidth(Double.MAX_VALUE);
        portField.getStyleClass().add("login-textfield");
        add(portField, 1, 0);
        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setMaxWidth(Double.MAX_VALUE);
        userField.getStyleClass().add("login-textfield");
        add(userField, 2, 0);
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setMaxWidth(Double.MAX_VALUE);
        passField.getStyleClass().add("login-textfield");
        add(passField, 3, 0);
        Button connectBtn = new Button("Connect");
        connectBtn.setMaxWidth(Double.MAX_VALUE);
        connectBtn.setPrefHeight(32);
        connectBtn.getStyleClass().add("login-button");
        add(connectBtn, 4, 0);
        this.connectButton = connectBtn;
        this.hostField = hostField;
        this.portField = portField;
        this.userField = userField;
        this.passField = passField;
        this.connectButton.getStyleClass().add("connect-btn");
        this.connectButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY) {
                    if (connection == null) {
                        String host = hostField.getText().trim();
                        if (host.isEmpty()) {
                            MainPage.get().getCommandPanel().addMessage("ERROR: Enter a host before connecting.", "RED", true);
                            return;
                        }
                        int port = AppSettings.defaultPort();
                        String portText = portField.getText().trim();
                        if (!portText.isEmpty()) {
                            try {
                                port = Integer.parseInt(portText);
                            } catch (NumberFormatException ex) {
                                MainPage.get().getCommandPanel().addMessage("ERROR: Port must be a number.", "RED", true);
                                return;
                            }
                        }
                        if (port < 1 || port > 65535) {
                            MainPage.get().getCommandPanel().addMessage("ERROR: Port must be between 1 and 65535.", "RED", true);
                            return;
                        }
                        String username = userField.getText();
                        String pass = passField.getText();
                        MainPage.get().getCommandPanel().addMessage("Connection: Attempting to connect to " + host + ":" + port, "GRAY", true); // CommandMessage
                        connection = new FTPConnect(host, port, username, pass);
                        if (connection.connect()) {
                            FTPFile[] remoteFiles = connection.getFiles();
                            if (remoteFiles == null) {
                                remoteFiles = new FTPFile[0];
                            }
                            for (FTPFile file : remoteFiles) {
                                if (file == null || file.getName() == null || file.getTimestamp() == null) {
                                    continue;
                                }
                                String name = file.getName();
                                String size = humanReadableByteCount(file.getSize(), true);
                                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy | hh:mm");
                                String lastModified = dateFormat.format(file.getTimestamp().getTimeInMillis());
                                ImageView icon;
                                if(file.isDirectory()) {
                                    icon = new ImageView(new Image("com/jaredscarito/jftp/resources/ftp-folder-icon.png"));
                                    icon.setFitWidth(15);
                                    icon.setFitHeight(15);
                                } else {
                                    // It's a file
                                    icon = new ImageView(new Image("com/jaredscarito/jftp/resources/ftp-file-icon.png"));
                                    icon.setFitWidth(15);
                                    icon.setFitHeight(15);
                                }
                                MainPage.get().getFtpFilesPanel().getTableView().getItems().add(new PaneFile(icon, name, size, lastModified));
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
                            MainPage.get().getCommandPanel().addMessage("Connection: Attempting to connect to " + host + ":" + port +
                                    " = SUCCESS", "GREEN", true); // CommandMessage
                            connectButton.setText("Disconnect");
                            if (!connectButton.getStyleClass().contains("is-connected")) {
                                connectButton.getStyleClass().add("is-connected");
                            }
                            MainPage.setStatus("Connected to " + host, true);
                        } else {
                            MainPage.get().getCommandPanel().addMessage("ERROR: Failed to connect to " + host + ":" + port, "RED", true); // CommandMessage
                            connection = null;
                            MainPage.setStatus("Not connected", false);
                        }
                    } else {
                        // It is the disconnect button
                        MainPage.get().getCommandPanel().addMessage("Connection: Disconnected from FTP Server", "GRAY", false); // CommandMessage
                        MainPage.get().getFtpFilesPanel().clearRemote();
                        connection.disconnect();
                        connection = null;
                        connectButton.setText("Connect");
                        connectButton.getStyleClass().remove("is-connected");
                        MainPage.setStatus("Not connected", false);
                    }
                }
            }
        });
    }
    public String getHost() {
        return hostField.getText().trim();
    }

    public String getPortText() {
        return portField.getText().trim();
    }

    public String getUsername() {
        return userField.getText().trim();
    }

    public void applyPreset(String host, String port, String username) {
        hostField.setText(host);
        portField.setText(port);
        userField.setText(username);
        passField.clear();
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "KMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
