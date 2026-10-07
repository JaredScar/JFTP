package com.jaredscarito.jftp.model.pages;

import com.jaredscarito.jftp.model.pages.panels.CommandPanel;
import com.jaredscarito.jftp.model.pages.panels.FilesPanel;
import com.jaredscarito.jftp.model.pages.panels.LoginPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class MainPage extends GridPane {
    private LoginPanel loginPanel;
    private FilesPanel myFilesPanel;
    private FilesPanel ftpFilesPanel;
    private CommandPanel commandPanel;

    private static MainPage mainPage;
    private static Label statusLabel;
    private FilesPanel activePanel;
    private Label activePaneLabel;

    public static MainPage get() {
        return mainPage;
    }

    public static void setStatusLabel(Label label) {
        statusLabel = label;
    }

    public static void setStatus(String text, boolean connected) {
        if (statusLabel == null) {
            return;
        }
        statusLabel.setText(text);
        statusLabel.getStyleClass().removeAll("status-on", "status-off");
        statusLabel.getStyleClass().add(connected ? "status-on" : "status-off");
    }

    public void bindActivePaneLabel(Label label) {
        this.activePaneLabel = label;
        updateActiveLabel();
    }

    public FilesPanel getActiveFilesPanel() {
        if (activePanel == null) {
            return myFilesPanel;
        }
        return activePanel;
    }

    public void setActiveFilesPanel(FilesPanel panel) {
        if (myFilesPanel != null) {
            myFilesPanel.getStyleClass().remove("panel-active");
        }
        if (ftpFilesPanel != null) {
            ftpFilesPanel.getStyleClass().remove("panel-active");
        }
        this.activePanel = panel;
        if (panel != null) {
            panel.getStyleClass().add("panel-active");
        }
        updateActiveLabel();
    }

    private void updateActiveLabel() {
        if (activePaneLabel == null || activePanel == null) {
            return;
        }
        activePaneLabel.setText(activePanel.isLocal() ? "My Files" : "FTP Files");
    }

    private String myCurrentDirectory = "ROOT1337";
    private String ftpCurrentDirectory = "ROOT1337";
    public String getMyCurrentDirectory() {
        return myCurrentDirectory;
    }
    public String getFtpCurrentDirectory() {
        return ftpCurrentDirectory;
    }
    public void setMyCurrentDirectory(String myCurrentDirectory) {
        this.myCurrentDirectory = myCurrentDirectory;
    }
    public void setFtpCurrentDirectory(String ftpCurrentDirectory) {
        this.ftpCurrentDirectory = ftpCurrentDirectory;
    }

    public MainPage() {
        mainPage = this;
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(12));
        setHgap(12);
        setVgap(12);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.getStyleClass().add("main-pane");

        ColumnConstraints left = new ColumnConstraints();
        left.setPercentWidth(50);
        left.setHgrow(Priority.ALWAYS);
        left.setFillWidth(true);
        ColumnConstraints right = new ColumnConstraints();
        right.setPercentWidth(50);
        right.setHgrow(Priority.ALWAYS);
        right.setFillWidth(true);
        getColumnConstraints().addAll(left, right);

        RowConstraints loginRow = new RowConstraints();
        loginRow.setVgrow(Priority.NEVER);
        RowConstraints filesRow = new RowConstraints();
        filesRow.setVgrow(Priority.ALWAYS);
        filesRow.setFillHeight(true);
        RowConstraints logRow = new RowConstraints();
        logRow.setVgrow(Priority.NEVER);
        logRow.setPrefHeight(112);
        logRow.setMinHeight(88);
        logRow.setMaxHeight(140);
        getRowConstraints().addAll(loginRow, filesRow, logRow);

        LoginPanel loginPanel = new LoginPanel("Login-Panel", "login-pane");
        FilesPanel myFilesPanel = new FilesPanel("1", "myFiles-pane");
        FilesPanel ftpFilesPanel = new FilesPanel("2", "ftpFilesPane");
        CommandPanel commandPanel = new CommandPanel("", "");
        this.loginPanel = loginPanel;
        this.myFilesPanel = myFilesPanel;
        this.ftpFilesPanel = ftpFilesPanel;
        this.commandPanel = commandPanel;

        GridPane.setHgrow(loginPanel, Priority.ALWAYS);
        loginPanel.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(myFilesPanel, Priority.ALWAYS);
        GridPane.setVgrow(myFilesPanel, Priority.ALWAYS);
        myFilesPanel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setHgrow(ftpFilesPanel, Priority.ALWAYS);
        GridPane.setVgrow(ftpFilesPanel, Priority.ALWAYS);
        ftpFilesPanel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setHgrow(commandPanel, Priority.ALWAYS);
        commandPanel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        add(loginPanel, 0, 0, 2, 1);
        add(myFilesPanel, 0, 1);
        add(ftpFilesPanel, 1, 1);
        add(commandPanel, 0, 2, 2, 1);
        setActiveFilesPanel(myFilesPanel);
    }
    /**
     * Getters
     */
    public LoginPanel getLoginPanel() {
        return this.loginPanel;
    }
    public FilesPanel getMyFilesPanel() {
        return this.myFilesPanel;
    }
    public FilesPanel getFtpFilesPanel() {
        return this.ftpFilesPanel;
    }
    public CommandPanel getCommandPanel() {
        return this.commandPanel;
    }
}
