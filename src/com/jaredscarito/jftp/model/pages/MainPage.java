package com.jaredscarito.jftp.model.pages;

import com.jaredscarito.jftp.model.pages.panels.CommandPanel;
import com.jaredscarito.jftp.model.pages.panels.FilesPanel;
import com.jaredscarito.jftp.model.pages.panels.LoginPanel;
import com.jaredscarito.jftp.model.pages.panels.Panel;
import javafx.scene.layout.GridPane;

public class MainPage extends GridPane {
    private LoginPanel loginPanel;
    private FilesPanel myFilesPanel;
    private FilesPanel ftpFilesPanel;
    private CommandPanel commandPanel;

    private static MainPage mainPage;;;

    public static MainPage get() {
        return mainPage;
    }

    private String myCurrentDirectory;
    private Strng ftpCurrentDirectory;

    public MainPage() {
        // Create Main Page
        mainPage = this;
        this.getStyleClass().add("main-pane");
        LoginPanel loginPanel = new LoginPanel("Login-Panel", "login-pane");
        FilesPanel myFilesPanel = new FilesPanel("1", "myFiles-pane", false);
        FilesPanel ftpFilesPanel = new FilesPanel("2", "ftpFilesPane");
        CommandPanel commandPanel = new CommandPanel("", "");
        this.loginPanel = loginPanel;
        this.myFilesPanel = myFilesPanel;
        this.ftpFilesPanel = ftpFilesPanel;
        this.commandPanel = commandPanel;
        add(loginPanel, 0, 0);
        add(myFilesPanel, 0, 1);
        add(ftpFilesPanel, 1, 1);
        add(commandPanel, 0, 2, 2, 1);
    }
    /**
     * Getters
     */
    public Panel getLoginPanel() {
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
