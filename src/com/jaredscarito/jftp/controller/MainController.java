package com.jaredscarito.jftp.controller;

import com.jaredscarito.jftp.model.AppSettings;
import com.jaredscarito.jftp.model.PresetStore;
import com.jaredscarito.jftp.model.pages.MainPage;
import com.jaredscarito.jftp.model.pages.panels.FilesPanel;
import com.jaredscarito.jftp.model.pages.panels.LoginPanel;
import com.jaredscarito.jftp.view.UiDialogs;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainController extends Application {
    public Stage mainStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.mainStage = primaryStage;
        Scene mainScene = getMainScene();
        mainScene.getStylesheets().add("com/jaredscarito/jftp/resources/style.css");
        primaryStage.setTitle("JFTP");
        primaryStage.setMinWidth(980);
        primaryStage.setMinHeight(640);
        applyWindowIcon(primaryStage);
        primaryStage.setScene(mainScene);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        primaryStage.show();
    }

    private Menu presetsMenu;

    private void applyWindowIcon(Stage stage) {
        ClassLoader loader = getClass().getClassLoader();
        int[] sizes = {16, 32, 48, 64, 128, 256};
        for (int size : sizes) {
            java.net.URL url = loader.getResource("com/jaredscarito/jftp/resources/app-icon-" + size + ".png");
            if (url != null) {
                stage.getIcons().add(new Image(url.toExternalForm(), false));
            }
        }
        if (stage.getIcons().isEmpty()) {
            java.net.URL url = loader.getResource("com/jaredscarito/jftp/resources/app-icon.png");
            if (url != null) {
                stage.getIcons().add(new Image(url.toExternalForm(), false));
            }
        }
    }

    private HBox buildHeader() {
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(8, 14, 8, 14));
        header.getStyleClass().add("app-header");

        ClassLoader loader = getClass().getClassLoader();
        java.net.URL logoUrl = loader.getResource("com/jaredscarito/jftp/resources/app-icon-64.png");
        if (logoUrl == null) {
            logoUrl = loader.getResource("com/jaredscarito/jftp/resources/app-icon.png");
        }
        if (logoUrl != null) {
            ImageView logo = new ImageView(new Image(logoUrl.toExternalForm(), 64, 64, true, true, false));
            logo.setFitWidth(28);
            logo.setFitHeight(28);
            logo.setPreserveRatio(true);
            logo.setSmooth(true);
            header.getChildren().add(logo);
        }

        Label name = new Label("JFTP");
        name.getStyleClass().add("app-title");
        header.getChildren().add(name);
        return header;
    }

    public Scene getMainScene() {
        VBox root = new VBox();
        Scene mainScene = new Scene(root, 1180, 760);

        Label status = new Label("Not connected");
        status.getStyleClass().addAll("status-pill", "status-off");
        MainPage.setStatusLabel(status);

        HBox header = buildHeader();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(spacer, status, buildMenuBar());

        root.getChildren().add(header);

        MainPage mainPage = new MainPage();
        VBox.setVgrow(mainPage, Priority.ALWAYS);
        root.getChildren().add(mainPage);
        return mainScene;
    }

    private MenuBar buildMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.getStyleClass().add("header-menu");

        Menu jftpMenu = new Menu("JFTP");
        MenuItem preferences = new MenuItem("Preferences");
        preferences.setOnAction(event -> showPreferences());
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(event -> {
            mainStage.close();
            Platform.exit();
            System.exit(0);
        });
        jftpMenu.getItems().addAll(preferences, new SeparatorMenuItem(), exit);

        Menu tasksMenu = new Menu("Tasks");
        MenuItem create = new MenuItem("Create");
        create.setOnAction(event -> activePanel().promptCreate());
        MenuItem edit = new MenuItem("Rename");
        edit.setOnAction(event -> activePanel().promptRename());
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(event -> activePanel().deleteSelected());
        MenuItem upload = new MenuItem("Upload");
        upload.setOnAction(event -> MainPage.get().getMyFilesPanel().uploadSelected());
        MenuItem download = new MenuItem("Download");
        download.setOnAction(event -> MainPage.get().getFtpFilesPanel().downloadSelected());
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction(event -> activePanel().refresh());
        tasksMenu.getItems().addAll(create, edit, delete, new SeparatorMenuItem(), upload, download, new SeparatorMenuItem(), refresh);

        presetsMenu = new Menu("Presets");
        rebuildPresets();

        Menu helpMenu = new Menu("Help");
        MenuItem about = new MenuItem("About");
        about.setOnAction(event -> showAbout());
        MenuItem support = new MenuItem("Support");
        support.setOnAction(event -> showSupport());
        helpMenu.getItems().addAll(about, support);

        menuBar.getMenus().addAll(jftpMenu, tasksMenu, presetsMenu, helpMenu);
        return menuBar;
    }

    private FilesPanel activePanel() {
        return MainPage.get().getActiveFilesPanel();
    }

    private void rebuildPresets() {
        presetsMenu.getItems().clear();
        MenuItem save = new MenuItem("Save current...");
        save.setOnAction(event -> savePreset());
        MenuItem remove = new MenuItem("Delete preset...");
        remove.setOnAction(event -> deletePreset());
        presetsMenu.getItems().addAll(save, remove);

        List<PresetStore.Preset> presets = PresetStore.load();
        if (presets.isEmpty()) {
            MenuItem empty = new MenuItem("No saved presets");
            empty.setDisable(true);
            presetsMenu.getItems().add(new SeparatorMenuItem());
            presetsMenu.getItems().add(empty);
            return;
        }
        presetsMenu.getItems().add(new SeparatorMenuItem());
        for (PresetStore.Preset preset : presets) {
            MenuItem item = new MenuItem(preset.name);
            item.setOnAction(event -> {
                LoginPanel login = MainPage.get().getLoginPanel();
                login.applyPreset(preset.host, preset.port, preset.username);
                MainPage.get().getCommandPanel().addMessage("Loaded preset " + preset.name + ". Enter the password, then connect.", "#64748b", false);
            });
            presetsMenu.getItems().add(item);
        }
    }

    private void savePreset() {
        LoginPanel login = MainPage.get().getLoginPanel();
        if (login.getHost().isEmpty()) {
            MainPage.get().getCommandPanel().addMessage("ERROR: Enter a host before saving a preset.", "RED", true);
            return;
        }
        TextInputDialog dialog = new TextInputDialog(login.getHost());
        dialog.setTitle("Save preset");
        dialog.setHeaderText("Save this connection");
        dialog.setContentText("Name");
        UiDialogs.style(dialog);
        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent() || result.get().trim().isEmpty()) {
            return;
        }
        String port = login.getPortText().isEmpty() ? Integer.toString(AppSettings.defaultPort()) : login.getPortText();
        PresetStore.save(new PresetStore.Preset(result.get().trim(), login.getHost(), port, login.getUsername()));
        rebuildPresets();
        MainPage.get().getCommandPanel().addMessage("Saved preset " + result.get().trim(), "GREEN", false);
    }

    private void deletePreset() {
        List<PresetStore.Preset> presets = PresetStore.load();
        if (presets.isEmpty()) {
            MainPage.get().getCommandPanel().addMessage("There are no presets to delete.", "#64748b", false);
            return;
        }
        List<String> names = new ArrayList<String>();
        for (PresetStore.Preset preset : presets) {
            names.add(preset.name);
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<String>(names.get(0), names);
        dialog.setTitle("Delete preset");
        dialog.setHeaderText("Remove a saved connection");
        dialog.setContentText("Preset");
        UiDialogs.style(dialog);
        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) {
            return;
        }
        PresetStore.delete(result.get());
        rebuildPresets();
        MainPage.get().getCommandPanel().addMessage("Deleted preset " + result.get(), "GREEN", false);
    }

    private void showPreferences() {
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.setTitle("Preferences");
        dialog.setHeaderText("JFTP settings");
        TextField portField = new TextField(Integer.toString(AppSettings.defaultPort()));
        portField.setPromptText("21");
        CheckBox confirm = new CheckBox("Ask before deleting files and folders");
        confirm.setSelected(AppSettings.confirmDelete());
        Label portLabel = new Label("Default FTP port");
        portLabel.getStyleClass().add("login-label");
        VBox form = new VBox(8, portLabel, portField, confirm);
        form.setPadding(new Insets(4, 0, 4, 0));
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        UiDialogs.style(dialog);
        Optional<ButtonType> result = dialog.showAndWait();
        if (!result.isPresent() || result.get() != ButtonType.OK) {
            return;
        }
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException ex) {
            MainPage.get().getCommandPanel().addMessage("ERROR: Default port must be a number.", "RED", true);
            return;
        }
        if (port < 1 || port > 65535) {
            MainPage.get().getCommandPanel().addMessage("ERROR: Default port must be between 1 and 65535.", "RED", true);
            return;
        }
        AppSettings.update(port, confirm.isSelected());
        MainPage.get().getCommandPanel().addMessage("Preferences saved.", "GREEN", false);
    }

    private void showAbout() {
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.setTitle("About JFTP");
        dialog.setHeaderText("JFTP");
        VBox body = new VBox(10);
        body.setAlignment(Pos.CENTER_LEFT);
        ClassLoader loader = getClass().getClassLoader();
        java.net.URL logoUrl = loader.getResource("com/jaredscarito/jftp/resources/app-icon-64.png");
        if (logoUrl != null) {
            ImageView logo = new ImageView(new Image(logoUrl.toExternalForm(), 64, 64, true, true, false));
            logo.setFitWidth(48);
            logo.setFitHeight(48);
            body.getChildren().add(logo);
        }
        Label summary = new Label("A desktop client for moving files between this computer and an FTP server.");
        summary.setWrapText(true);
        summary.setMaxWidth(360);
        Label detail = new Label("Select a pane, then use the command bar or the Tasks menu to create, rename, delete, upload, and download.");
        detail.setWrapText(true);
        detail.setMaxWidth(360);
        detail.getStyleClass().add("command-hint");
        body.getChildren().addAll(summary, detail);
        dialog.getDialogPane().setContent(body);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        UiDialogs.style(dialog);
        dialog.showAndWait();
    }

    private void showSupport() {
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.setTitle("Support");
        dialog.setHeaderText("Get help");
        Label summary = new Label("Questions and issues for JFTP are tracked on GitHub.");
        summary.setWrapText(true);
        summary.setMaxWidth(360);
        Button open = new Button("Open project page");
        open.getStyleClass().add("command-button");
        open.getStyleClass().add("accent");
        open.setOnAction(event -> openLink("https://github.com/TheWolfBadger/JFTP"));
        VBox body = new VBox(12, summary, open);
        dialog.getDialogPane().setContent(body);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        UiDialogs.style(dialog);
        dialog.showAndWait();
    }

    private void openLink(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
                return;
            }
        } catch (Exception ignored) {
        }
        MainPage.get().getCommandPanel().addMessage("Open this page in a browser: " + url, "#64748b", false);
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
