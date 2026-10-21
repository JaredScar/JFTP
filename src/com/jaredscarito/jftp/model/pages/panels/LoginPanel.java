package com.jaredscarito.jftp.model.pages.panels;

import com.jaredscarito.jftp.model.AppSettings;
import com.jaredscarito.jftp.model.FTPConnect;
import com.jaredscarito.jftp.model.pages.MainPage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.List;

public class LoginPanel extends Panel {
    private String name;
    private ComboBox<String> protocolBox;
    private TextField hostField;
    private TextField portField;
    private TextField userField;
    private PasswordField passField;
    private TextField visiblePass;
    private FTPConnect connection;
    private Button connectButton;
    private Label connectGlyph;

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
        setPadding(new Insets(2, 2, 0, 2));
        setHgap(10);
        setMaxWidth(Double.MAX_VALUE);
        setAlignment(Pos.BOTTOM_LEFT);

        boolean mdl2 = Font.getFamilies().contains("Segoe MDL2 Assets");

        ColumnConstraints protocolCol = new ColumnConstraints();
        protocolCol.setMinWidth(108);
        protocolCol.setPrefWidth(118);
        protocolCol.setMaxWidth(140);
        ColumnConstraints hostCol = new ColumnConstraints();
        hostCol.setHgrow(Priority.ALWAYS);
        hostCol.setFillWidth(true);
        hostCol.setMinWidth(180);
        ColumnConstraints portCol = new ColumnConstraints();
        portCol.setMinWidth(72);
        portCol.setPrefWidth(84);
        portCol.setMaxWidth(100);
        ColumnConstraints userCol = new ColumnConstraints();
        userCol.setHgrow(Priority.SOMETIMES);
        userCol.setFillWidth(true);
        userCol.setMinWidth(130);
        userCol.setPrefWidth(168);
        ColumnConstraints passCol = new ColumnConstraints();
        passCol.setHgrow(Priority.SOMETIMES);
        passCol.setFillWidth(true);
        passCol.setMinWidth(140);
        passCol.setPrefWidth(180);
        ColumnConstraints buttonCol = new ColumnConstraints();
        buttonCol.setMinWidth(124);
        buttonCol.setPrefWidth(132);
        getColumnConstraints().addAll(protocolCol, hostCol, portCol, userCol, passCol, buttonCol);

        protocolBox = new ComboBox<String>();
        protocolBox.getItems().addAll("FTP", "SFTP", "FTPS");
        protocolBox.setValue("FTP");
        protocolBox.setMaxWidth(Double.MAX_VALUE);
        protocolBox.getStyleClass().add("protocol-box");

        hostField = new TextField();
        hostField.setPromptText("ftp.example.com");
        hostField.getStyleClass().add("shell-field");
        HBox hostShell = fieldShell(mdl2 ? "\uE753" : "@", hostField, mdl2);

        portField = new TextField(Integer.toString(AppSettings.defaultPort()));
        portField.setPromptText("21");
        portField.getStyleClass().add("shell-field");
        HBox portShell = fieldShell(null, portField, mdl2);

        userField = new TextField();
        userField.setPromptText("anonymous");
        userField.getStyleClass().add("shell-field");
        HBox userShell = fieldShell(mdl2 ? "\uE77B" : "@", userField, mdl2);

        passField = new PasswordField();
        passField.setPromptText("Password");
        passField.getStyleClass().add("shell-field");
        visiblePass = new TextField();
        visiblePass.setPromptText("Password");
        visiblePass.getStyleClass().add("shell-field");
        visiblePass.setVisible(false);
        visiblePass.setManaged(false);
        passField.textProperty().bindBidirectional(visiblePass.textProperty());
        StackPane passStack = new StackPane(passField, visiblePass);
        HBox.setHgrow(passStack, Priority.ALWAYS);

        Button eye = new Button();
        eye.setFocusTraversable(false);
        eye.getStyleClass().add("eye-button");
        final Label eyeGlyph = glyph(mdl2 ? "\uE890" : "\u25CE", mdl2);
        eye.setGraphic(eyeGlyph);
        eye.setOnAction(event -> {
            boolean reveal = !visiblePass.isVisible();
            visiblePass.setVisible(reveal);
            visiblePass.setManaged(reveal);
            passField.setVisible(!reveal);
            passField.setManaged(!reveal);
            eyeGlyph.setText(mdl2 ? (reveal ? "\uE7B3" : "\uE890") : (reveal ? "\u25CF" : "\u25CE"));
            if (reveal) {
                visiblePass.requestFocus();
            } else {
                passField.requestFocus();
            }
        });
        HBox passShell = new HBox(4, passStack, eye);
        passShell.setAlignment(Pos.CENTER_LEFT);
        passShell.getStyleClass().add("input-shell");
        watchFocus(passShell, passField, visiblePass, eye);

        connectGlyph = glyph(mdl2 ? "\uE703" : "\u2192", mdl2);
        connectButton = new Button("Connect");
        connectButton.setGraphic(connectGlyph);
        connectButton.setContentDisplay(ContentDisplay.LEFT);
        connectButton.setMaxWidth(Double.MAX_VALUE);
        connectButton.setPrefHeight(36);
        connectButton.getStyleClass().add("connect-btn");
        connectButton.setOnAction(event -> toggleConnection());

        add(labeled("PROTOCOL", protocolBox), 0, 0);
        add(labeled("HOST", hostShell), 1, 0);
        add(labeled("PORT", portShell), 2, 0);
        add(labeled("USERNAME", userShell), 3, 0);
        add(labeled("PASSWORD", passShell), 4, 0);
        add(labeled(" ", connectButton), 5, 0);
    }

    public void toggleConnection() {
        if (connection == null) {
            String protocol = protocolBox.getValue();
            if (protocol != null && !"FTP".equals(protocol)) {
                MainPage.get().getCommandPanel().addMessage("ERROR: This build connects with FTP. " + protocol + " is not available yet.", "RED", true);
                protocolBox.setValue("FTP");
                return;
            }
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
            MainPage.get().getCommandPanel().addMessage("Connecting to " + host + ":" + port, "GRAY", true);
            connection = new FTPConnect(host, port, username, pass);
            if (connection.connect()) {
                MainPage.get().getFtpFilesPanel().setRemoteConnected(true, host);
                MainPage.get().getFtpFilesPanel().refresh();
                MainPage.get().getCommandPanel().addMessage("Connected to " + host + ":" + port, "GREEN", true);
                connectButton.setText("Disconnect");
                if (!connectButton.getStyleClass().contains("is-connected")) {
                    connectButton.getStyleClass().add("is-connected");
                }
                MainPage.setStatus("Connected to " + host, true);
            } else {
                MainPage.get().getCommandPanel().addMessage("ERROR: Failed to connect to " + host + ":" + port, "RED", true);
                connection = null;
                MainPage.setStatus("Not connected", false);
            }
        } else {
            MainPage.get().getCommandPanel().addMessage("Disconnected from the server", "GRAY", false);
            MainPage.get().getFtpFilesPanel().clearRemote();
            connection.disconnect();
            connection = null;
            connectButton.setText("Connect");
            connectButton.getStyleClass().remove("is-connected");
            MainPage.setStatus("Not connected", false);
        }
    }

    public void focusHost() {
        hostField.requestFocus();
    }

    public void usePreset(String host, String port, String username) {
        if (connection != null) {
            MainPage.get().getCommandPanel().addMessage("Disconnect before loading another server.", "RED", true);
            return;
        }
        applyPreset(host, port, username);
        protocolBox.setValue("FTP");
        if (passField.getText() == null || passField.getText().isEmpty()) {
            passField.requestFocus();
            MainPage.get().getCommandPanel().addMessage("Loaded " + host + ". Enter the password, then connect.", "#64748b", false);
            return;
        }
        toggleConnection();
    }

    private VBox labeled(String caption, Node field) {
        Label label = new Label(caption);
        label.getStyleClass().add("field-caption");
        VBox box = new VBox(4, label, field);
        box.setAlignment(Pos.BOTTOM_LEFT);
        return box;
    }

    private HBox fieldShell(String icon, TextField field, boolean mdl2) {
        field.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(field, Priority.ALWAYS);
        HBox shell = new HBox(6);
        shell.setAlignment(Pos.CENTER_LEFT);
        shell.getStyleClass().add("input-shell");
        if (icon != null) {
            shell.getChildren().add(glyph(icon, mdl2));
        }
        shell.getChildren().add(field);
        watchFocus(shell, field);
        return shell;
    }

    private Label glyph(String text, boolean mdl2) {
        Label label = new Label(text);
        label.getStyleClass().add(mdl2 ? "mdl2-glyph" : "tool-glyph");
        return label;
    }

    private void watchFocus(final HBox shell, Node... nodes) {
        for (Node node : nodes) {
            node.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean focused) {
                    if (focused) {
                        if (!shell.getStyleClass().contains("shell-focused")) {
                            shell.getStyleClass().add("shell-focused");
                        }
                    } else {
                        shell.getStyleClass().remove("shell-focused");
                    }
                }
            });
        }
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
}
