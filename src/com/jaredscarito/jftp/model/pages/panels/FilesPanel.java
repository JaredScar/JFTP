package com.jaredscarito.jftp.model.pages.panels;

import com.jaredscarito.jftp.api.SoundUtils;
import com.jaredscarito.jftp.controller.MainController;
import com.jaredscarito.jftp.model.AppSettings;
import com.jaredscarito.jftp.model.FTPConnect;
import com.jaredscarito.jftp.model.PaneFile;
import com.jaredscarito.jftp.model.pages.MainPage;
import com.jaredscarito.jftp.view.UiDialogs;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;

public class FilesPanel extends Panel {
    public FilesPanel() {}
    public FilesPanel(String name, List<String> styleClasses) {
        super(name, styleClasses);
    }
    public FilesPanel(String name, String styleClass) {
        super(name, styleClass);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public ImageView getIconImageView(File file) {
        try {
            ImageIcon imgIcon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file);
            BufferedImage bufferedImage = (BufferedImage) imgIcon.getImage();
            Image img = SwingFXUtils.toFXImage(bufferedImage, null);
            ImageView iconImg = new ImageView(img);
            iconImg.setFitHeight(15);
            iconImg.setFitWidth(15);
            return iconImg;
        } catch (Exception ex) {
            // Use default images instead
            // Make it a folder or file dependent on condition
            if(file.isDirectory()) {
                Image img = new Image("com/jaredscarito/jftp/resources/ftp-folder-icon.png");
                ImageView icon = new ImageView(img);
                icon.setFitWidth(15);
                icon.setFitHeight(15);
                return icon;
            } else {
                // Is File
                Image img = new Image("com/jaredscarito/jftp/resources/ftp-file-icon.png");
                ImageView icon = new ImageView(img);
                icon.setFitWidth(15);
                icon.setFitHeight(15);
                return icon;
            }
        }
    }

    private void setupMyCurrentDirectory() {
        tableView.getItems().clear();
        for (File file : new File(MainPage.get().getMyCurrentDirectory()).listFiles()) {
            String fileSize = "";
            if (!file.isDirectory()) {
                fileSize = MainController.humanReadableByteCount((file.length()), true);
            }
            FileTime lastModified = null;
            try {
                lastModified = Files.getLastModifiedTime(Paths.get(file.getAbsolutePath()));
            } catch (IOException ex) {
            }
            if (lastModified != null) {
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy | hh:mm");
                tableView.getItems().add(new PaneFile(getIconImageView(file), file.getName(), fileSize, dateFormat.format(lastModified.toMillis())));
            } else {
                tableView.getItems().add(new PaneFile(getIconImageView(file), file.getName(), fileSize, "N/A"));
            }
        }
        this.currentDirectoryField.setText(MainPage.get().getMyCurrentDirectory());
    }
    private void setupMyRootDirectory() {
        tableView.getItems().clear();
        for(File file : File.listRoots()) {
            String fileSize = MainController.humanReadableByteCount((file.getTotalSpace() - file.getFreeSpace()), true);
            FileTime lastModified = null;
            try {
                lastModified = Files.getLastModifiedTime(Paths.get(file.getAbsolutePath()));
            } catch (IOException ex) {}
            if(lastModified !=null) {
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy | hh:mm");
                tableView.getItems().add(new PaneFile(getIconImageView(file), file.getAbsolutePath(), fileSize, dateFormat.format(lastModified.toMillis())));
            } else {
                tableView.getItems().add(new PaneFile(getIconImageView(file), file.getAbsolutePath(), fileSize, "N/A"));
            }
        }
        MainPage.get().setMyCurrentDirectory("ROOT1337");
        this.currentDirectoryField.setText("/");
    }
    private void setupFTPRootDirectory() {
        tableView.getItems().clear();
        FTPConnect connection = MainPage.get().getLoginPanel().getConnection();
        for (FTPFile file : connection.getFiles()) {
            ftpFileSetup(file);
        }
        MainPage.get().setFtpCurrentDirectory("ROOT1337");
        this.currentDirectoryField.setText("");
    }
    private void ftpFileSetup(FTPFile file) {
        String name = file.getName();
        String size = MainController.humanReadableByteCount(file.getSize(), true);
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
    private void setupFTPCurrentDirectory() {
        tableView.getItems().clear();
        FTPConnect connection = MainPage.get().getLoginPanel().getConnection();
        try {
            FTPFile[] files = connection.getClient().listFiles(MainPage.get().getFtpCurrentDirectory());
            for(FTPFile file : files) {
                ftpFileSetup(file);
            }
            this.currentDirectoryField.setText(MainPage.get().getFtpCurrentDirectory());
        } catch (IOException e) {}
    }

    private TableView tableView;
    private TextField currentDirectoryField;

    private enum FileType {
        CLIENT,
        FTP;
    }
    private FileType currentClipFilesType;
    private HashMap<String, String> clipboardFilePaths = new HashMap<>();

    @Override
    public void init() {
        getStyleClass().add("files-card");
        setPadding(new Insets(10, 12, 12, 12));
        setVgap(8);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        ColumnConstraints stretch = new ColumnConstraints();
        stretch.setHgrow(Priority.ALWAYS);
        stretch.setPercentWidth(100);
        getColumnConstraints().add(stretch);
        RowConstraints headerRow = new RowConstraints();
        headerRow.setVgrow(Priority.NEVER);
        RowConstraints pathRow = new RowConstraints();
        pathRow.setVgrow(Priority.NEVER);
        RowConstraints tableRow = new RowConstraints();
        tableRow.setVgrow(Priority.ALWAYS);
        tableRow.setFillHeight(true);
        getRowConstraints().addAll(headerRow, pathRow, tableRow);

        Label myFilesLabel = new Label("My Files");
        if(!getName().equals("1")) {
            myFilesLabel.setText("FTP Files");
        }
        myFilesLabel.getStyleClass().add("pane-title");
        TableView tableView = new TableView();
        this.tableView = tableView;
        TableColumn iconCol = new TableColumn("");
        iconCol.setCellValueFactory(new PropertyValueFactory<>("icon"));
        iconCol.setPrefWidth(44);
        iconCol.setMinWidth(44);
        iconCol.setMaxWidth(44);
        iconCol.setResizable(false);
        TableColumn fileNamesCol = new TableColumn("Filename");
        fileNamesCol.setCellValueFactory(new PropertyValueFactory<>("obj"));
        fileNamesCol.setMinWidth(140);
        TableColumn fileSizesCol = new TableColumn("Size");
        fileSizesCol.setCellValueFactory(new PropertyValueFactory<>("filesize"));
        fileSizesCol.setPrefWidth(90);
        fileSizesCol.setMinWidth(72);
        TableColumn fileModified = new TableColumn("Last Modified");
        fileModified.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
        fileModified.setPrefWidth(150);
        fileModified.setMinWidth(130);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getColumns().addAll(iconCol, fileNamesCol, fileSizesCol, fileModified);
        tableView.getStyleClass().add("files-table");
        tableView.setMinHeight(180);
        tableView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        Label empty = new Label(getName().equals("1") ? "This folder is empty" : "Connect to a server to browse remote files");
        empty.getStyleClass().add("empty-state");
        tableView.setPlaceholder(empty);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> markActive());

        boolean mdl2 = Font.getFamilies().contains("Segoe MDL2 Assets");
        String[] glyphs = mdl2
                ? new String[]{"\uE74A", "\uE80F", "\uE72C", "\uE710", "\uE74D"}
                : new String[]{"\u2191", "\u2302", "\u21BB", "+", "\u2715"};
        String[] tips = {"Up one level", "Home", "Refresh", "New file or folder", "Delete selected"};
        HBox iconsBox = new HBox(4);
        iconsBox.setAlignment(Pos.CENTER_RIGHT);
        iconsBox.getStyleClass().add("toolbar");
        for (int count = 0; count < tips.length; count++) {
            Button iconButton = new Button();
            iconButton.setMnemonicParsing(false);
            iconButton.setFocusTraversable(false);
            iconButton.setTooltip(new Tooltip(tips[count]));
            iconButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            iconButton.getStyleClass().add("pane-tool");
            if (count == 4) {
                iconButton.getStyleClass().add("danger");
            }
            Label glyph = new Label(glyphs[count]);
            glyph.getStyleClass().add(mdl2 ? "mdl2-glyph" : "tool-glyph");
            iconButton.setGraphic(glyph);
            final int action = count;
            iconButton.setOnAction(event -> {
                markActive();
                switch (action) {
                    case 0:
                        goUp();
                        break;
                    case 1:
                        goHome();
                        break;
                    case 2:
                        refresh();
                        break;
                    case 3:
                        promptCreate();
                        break;
                    case 4:
                        deleteSelected();
                        break;
                    default:
                        break;
                }
            });
            iconsBox.getChildren().add(iconButton);
        }
        Button transferButton = new Button(isLocal() ? "Upload" : "Download");
        transferButton.setFocusTraversable(false);
        transferButton.getStyleClass().addAll("pane-tool", "accent");
        transferButton.setTooltip(new Tooltip(isLocal() ? "Upload selected files" : "Download selected files"));
        transferButton.setOnAction(event -> {
            markActive();
            if (isLocal()) {
                uploadSelected();
            } else {
                downloadSelected();
            }
        });
        iconsBox.getChildren().add(transferButton);
        /**
         * URL TEXTFIELD (USER)
         */
        TextField currentDirectoryField = new TextField("");
        this.currentDirectoryField = currentDirectoryField;
        currentDirectoryField.getStyleClass().add("path-field");
        currentDirectoryField.setMaxWidth(Double.MAX_VALUE);
        this.currentDirectoryField.setEditable(false);
        /**
         * USER CLIENT FTP VIEW FILES:
         */
        // Setup FileSystemView for user client
        /**/
        if(getName().equals("1")) {
            setupMyRootDirectory();
        }
        // Add ContextMenu for table
        ContextMenu cm = new ContextMenu();
        cm.getStyleClass().add("context-menu");
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");
        MenuItem uploadItem = new MenuItem("Upload");
        MenuItem downloadItem = new MenuItem("Download");
        MenuItem renameItem = new MenuItem("Rename");
        renameItem.getStyleClass().add("context-item");
        copyItem.getStyleClass().add("context-item");
        pasteItem.getStyleClass().add("context-item");
        uploadItem.getStyleClass().add("context-item");
        downloadItem.getStyleClass().add("context-item");
        cm.getItems().addAll(copyItem, pasteItem);
        cm.getItems().add(renameItem);
        if(getName().equals("1")) {
            cm.getItems().add(uploadItem);
        } else {
            cm.getItems().add(downloadItem);
        }
        this.tableView.setContextMenu(cm);
        // Add actions for ContextMenu
        copyItem.setOnAction(event -> {
            if(getName().equals("1")) {
                MainPage.get().getCommandPanel().addMessage("Copied Files: ", "BLUE", true); // CommandMessage
                for (Object obj : tableView.getSelectionModel().getSelectedItems()) {
                    PaneFile paneFile = (PaneFile) obj;
                    clipboardFilePaths.put(MainPage.get().getMyCurrentDirectory() + "\\\\" +  paneFile.getFilename(), paneFile.getFilename());
                    MainPage.get().getCommandPanel().addMessage("Client File Copied - " + paneFile.getFilename(), "BLUE", false); // CommandMessage
                }
                currentClipFilesType = FileType.CLIENT;
            } else {
                // FTP Copy
                MainPage.get().getCommandPanel().addMessage("Copied Files: ", "BLUE", true); // CommandMessage
                for (Object obj : tableView.getSelectionModel().getSelectedItems()) {
                    PaneFile paneFile = (PaneFile) obj;
                    clipboardFilePaths.put(MainPage.get().getMyCurrentDirectory() + "\\\\" +  paneFile.getFilename(), paneFile.getFilename());
                    MainPage.get().getCommandPanel().addMessage("FTP File Copied - " + paneFile.getFilename(), "BLUE", false); // CommandMessage
                }
                currentClipFilesType = FileType.FTP;
            }
        });
        pasteItem.setOnAction(event -> {
            MainPage.get().getCommandPanel().addMessage("Attempting to paste files...", "GRAY", false); // CommandMessage
            if(getName().equals("1")) {
                // Client files
                if(currentClipFilesType == FileType.CLIENT) {
                    //Paste client files to client side
                    for(String key : clipboardFilePaths.keySet()) {
                        String filePath = key;
                        String fileName = clipboardFilePaths.get(key);
                        PaneFile pf = (PaneFile) tableView.getSelectionModel().getSelectedItem();
                        if(tableView.getSelectionModel().getSelectedCells().size() == 0 || pf == null
                                || new File(MainPage.get().getMyCurrentDirectory() + "\\\\" + pf.getFilename()).isFile()) {
                            // Paste it in current directory
                            try {
                                FileUtils.copyFileToDirectory(new File(filePath), new File(MainPage.get().getMyCurrentDirectory()));
                                MainPage.get().getCommandPanel().addMessage("File pasted - " + fileName, "GREEN", false); // CommandMessage
                            } catch (IOException e) {
                                MainPage.get().getCommandPanel().addMessage("ERROR: " + e.getMessage(), "RED", true); // CommandMessage
                                SoundUtils.getInstance().playErrorSound(); // Error Sound
                                e.printStackTrace();
                            }
                        } else {
                            // Paste it in selected directory
                            try {
                                FileUtils.copyFileToDirectory(new File(filePath), new File(MainPage.get().getMyCurrentDirectory() + "\\\\" + pf.getFilename()));
                                MainPage.get().getCommandPanel().addMessage("File pasted - " + fileName, "GREEN", false); // CommandMessage
                            } catch (IOException e) {
                                MainPage.get().getCommandPanel().addMessage("ERROR: " + e.getMessage(), "RED", true); // CommandMessage
                                SoundUtils.getInstance().playErrorSound(); // Error Sound
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    // Paste FTP files to client side
                    for(String filePath : clipboardFilePaths.keySet()) {
                        String fileFilename = clipboardFilePaths.get(filePath);
                        PaneFile pf = (PaneFile) tableView.getSelectionModel().getSelectedItem();
                        File f = new File(MainPage.get().getMyCurrentDirectory() + pf.getFilename());
                        if(tableView.getSelectionModel().getSelectedCells().size() == 0 || f.isFile()) {
                            // Paste it in current directory for clients
                            if(MainPage.get().getLoginPanel().getConnection().downloadFile(filePath, MainPage.get().getMyCurrentDirectory())) {
                                MainPage.get().getCommandPanel().addMessage("SUCCESS: Pasted file -" + fileFilename, "GREEN", true); // CommandMessage
                            } else {
                                MainPage.get().getCommandPanel().addMessage("ERROR: Unable to paste file - " + fileFilename, "RED", true); // CommandMessage
                                SoundUtils.getInstance().playErrorSound(); // Error Sound
                            }
                        } else {
                            // Paste it in selected directory if folder
                            if(MainPage.get().getLoginPanel().getConnection().downloadFile(filePath, MainPage.get().getMyCurrentDirectory() + pf.getFilename())) {
                                MainPage.get().getCommandPanel().addMessage("SUCCESS: Pasted file - " + fileFilename, "GREEN", true); // CommandMessage
                            } else {
                                MainPage.get().getCommandPanel().addMessage("ERROR: Unable to paste file - " + fileFilename, "RED", true); // CommandMessage
                                SoundUtils.getInstance().playErrorSound(); // Error Sound
                            }
                        }
                    }
                }
            } else {
                // FTP Files
                if(currentClipFilesType == FileType.CLIENT) {
                    // Paste client files to FTP side
                    for(String filePath : clipboardFilePaths.keySet()) {
                        String fileFilename = clipboardFilePaths.get(filePath);
                        PaneFile pf = (PaneFile) tableView.getSelectionModel().getSelectedItem();
                        boolean isFolder = MainPage.get().getLoginPanel().getConnection().isDirectory(MainPage.get().getFtpCurrentDirectory() + pf.getFilename());
                        if(isFolder) {
                            // We want to paste them in here
                            if(MainPage.get().getLoginPanel().getConnection().uploadFile(filePath, MainPage.get().getFtpCurrentDirectory() + pf.getFilename())) {
                                MainPage.get().getCommandPanel().addMessage("SUCCESS: Pasted file - " + fileFilename, "GREEN", true); // CommandMessage
                            } else {
                                MainPage.get().getCommandPanel().addMessage("ERROR: Unable to paste file - " + fileFilename, "RED", true); // CommandMessage
                                SoundUtils.getInstance().playErrorSound(); // Error Sound
                            }
                        } else {
                            // We paste them in current FTP directory
                            if(MainPage.get().getLoginPanel().getConnection().uploadFile(filePath, MainPage.get().getFtpCurrentDirectory())) {
                                MainPage.get().getCommandPanel().addMessage("SUCCESS: Pasted file - " + fileFilename, "GREEN", true); // CommandMessage
                            } else {
                                MainPage.get().getCommandPanel().addMessage("ERROR: Unable to paste file - " + fileFilename, "RED", true); // CommandMessage
                                SoundUtils.getInstance().playErrorSound(); // Error Sound
                            }
                        }
                    }
                } else {
                    // Paste FTP files to FTP side
                    // This can't be done as there is no method in FTPClient to do it
                    MainPage.get().getCommandPanel().addMessage("ERROR: Unable to paste FTP Files to FTP Server", "RED", true); // CommandMessage
                    SoundUtils.getInstance().playErrorSound(); // Error Sound
                }
            }
        });
        uploadItem.setOnAction(event -> uploadSelected());
        downloadItem.setOnAction(event -> downloadSelected());
        renameItem.setOnAction(event -> promptRename());
        // Set up double click action on table rows
        this.tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
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
                        PaneFile pf = (PaneFile) tableView.getSelectionModel().getSelectedItem();
                        if (pf == null || pf.getFilename() == null) {
                            return;
                        }
                        if (getName().equals("1")) {
                            String temp;
                            if (!MainPage.get().getMyCurrentDirectory().equals("ROOT1337")) {
                                temp = new File(MainPage.get().getMyCurrentDirectory(), pf.getFilename()).getAbsolutePath();
                            } else {
                               temp = pf.getFilename();
                            }
                            File file = new File(temp);
                            if (file.isDirectory()) {
                                MainPage.get().setMyCurrentDirectory(temp);
                                setupMyCurrentDirectory();
                            }
                        } else {
                            try {
                                if (MainPage.get().getLoginPanel().getConnection() == null) {
                                    return;
                                }
                                FTPFile[] files;
                                if(!MainPage.get().getFtpCurrentDirectory().equals("ROOT1337")) {
                                    files = MainPage.get().getLoginPanel().getConnection().getClient().listFiles(MainPage.get().getFtpCurrentDirectory() + "/" + pf.getFilename());
                                } else {
                                    files = MainPage.get().getLoginPanel().getConnection().getClient().listFiles(pf.getFilename());
                                }
                                if(files.length <= 1) {
                                    // Is a file
                                } else {
                                    // Is a directory
                                    if(!MainPage.get().getFtpCurrentDirectory().equals("ROOT1337")) {
                                        MainPage.get().setFtpCurrentDirectory(MainPage.get().getFtpCurrentDirectory() + "/" + pf.getFilename());
                                    } else {
                                        MainPage.get().setFtpCurrentDirectory(pf.getFilename());
                                    }
                                    setupFTPCurrentDirectory();
                                }
                            } catch (IOException ex) {}
                        }
                    }
                }
            }
        });
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("pane-header");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(myFilesLabel, spacer, iconsBox);
        setHgrow(header, Priority.ALWAYS);
        setHgrow(currentDirectoryField, Priority.ALWAYS);
        setVgrow(tableView, Priority.ALWAYS);
        setHgrow(tableView, Priority.ALWAYS);
        add(header, 0, 0);
        add(currentDirectoryField, 0, 1);
        add(tableView, 0, 2);
    }

    public TableView getTableView() {
        return this.tableView;
    }

    public boolean isLocal() {
        return "1".equals(getName());
    }

    public void markActive() {
        if (MainPage.get() != null) {
            MainPage.get().setActiveFilesPanel(this);
        }
    }

    public void clearRemote() {
        tableView.getItems().clear();
        MainPage.get().setFtpCurrentDirectory("ROOT1337");
        if (currentDirectoryField != null) {
            currentDirectoryField.setText("");
        }
    }

    public void refresh() {
        if (!isLocal() && connection() == null) {
            error("Connect to an FTP server first.");
            return;
        }
        if (isLocal()) {
            if (isRoot()) {
                setupMyRootDirectory();
            } else {
                setupMyCurrentDirectory();
            }
        } else if (isRoot()) {
            setupFTPRootDirectory();
        } else {
            setupFTPCurrentDirectory();
        }
    }

    public void goHome() {
        if (!isLocal() && connection() == null) {
            error("Connect to an FTP server first.");
            return;
        }
        if (isLocal()) {
            setupMyRootDirectory();
        } else {
            setupFTPRootDirectory();
        }
    }

    public void goUp() {
        if (isLocal()) {
            if (isRoot()) {
                info("Already at This PC.");
                return;
            }
            File current = new File(MainPage.get().getMyCurrentDirectory());
            File parent = current.getParentFile();
            if (parent == null) {
                setupMyRootDirectory();
            } else {
                MainPage.get().setMyCurrentDirectory(parent.getAbsolutePath());
                setupMyCurrentDirectory();
            }
            return;
        }
        if (connection() == null) {
            error("Connect to an FTP server first.");
            return;
        }
        if (isRoot()) {
            info("Already at the server root.");
            return;
        }
        String dir = MainPage.get().getFtpCurrentDirectory();
        int slash = dir.lastIndexOf('/');
        if (slash <= 0) {
            setupFTPRootDirectory();
        } else {
            MainPage.get().setFtpCurrentDirectory(dir.substring(0, slash));
            setupFTPCurrentDirectory();
        }
    }

    public void promptCreate() {
        markActive();
        if (isLocal() && isRoot()) {
            error("Open a folder before creating a file or folder.");
            return;
        }
        if (!isLocal() && connection() == null) {
            error("Connect to an FTP server first.");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.setTitle("Create");
        dialog.setHeaderText(isLocal() ? "Create in My Files" : "Create on the server");
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        RadioButton fileOption = new RadioButton("File");
        RadioButton folderOption = new RadioButton("Folder");
        ToggleGroup group = new ToggleGroup();
        fileOption.setToggleGroup(group);
        folderOption.setToggleGroup(group);
        folderOption.setSelected(true);
        VBox form = new VBox(10, nameField, new HBox(16, fileOption, folderOption));
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Node ok = dialog.getDialogPane().lookupButton(ButtonType.OK);
        ok.setDisable(true);
        nameField.textProperty().addListener((obs, oldValue, newValue) -> ok.setDisable(newValue == null || newValue.trim().isEmpty()));
        UiDialogs.style(dialog);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            createEntry(nameField.getText().trim(), folderOption.isSelected());
        }
    }

    public void promptRename() {
        markActive();
        PaneFile selected = selectedFile();
        if (selected == null) {
            error("Select a file or folder to rename.");
            return;
        }
        if (isLocal() && isRoot()) {
            error("Drives can't be renamed from here.");
            return;
        }
        if (!isLocal() && connection() == null) {
            error("Connect to an FTP server first.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog(selected.getFilename());
        dialog.setTitle("Rename");
        dialog.setHeaderText("Rename " + selected.getFilename());
        dialog.setContentText("New name");
        UiDialogs.style(dialog);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            renameTo(selected, result.get().trim());
        }
    }

    public void deleteSelected() {
        markActive();
        List<PaneFile> files = selectedFiles();
        if (files.isEmpty()) {
            error("Select something to delete.");
            return;
        }
        if (isLocal() && isRoot()) {
            error("Drives can't be deleted from here.");
            return;
        }
        if (!isLocal() && connection() == null) {
            error("Connect to an FTP server first.");
            return;
        }
        if (AppSettings.confirmDelete()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete");
            alert.setHeaderText("Delete " + files.size() + (files.size() == 1 ? " item?" : " items?"));
            alert.setContentText("This cannot be undone.");
            UiDialogs.style(alert);
            Optional<ButtonType> answer = alert.showAndWait();
            if (!answer.isPresent() || answer.get() != ButtonType.OK) {
                return;
            }
        }
        for (PaneFile selected : files) {
            if (isLocal()) {
                File file = new File(MainPage.get().getMyCurrentDirectory(), selected.getFilename());
                try {
                    if (file.isDirectory()) {
                        FileUtils.deleteDirectory(file);
                    } else if (!file.delete()) {
                        error("Unable to delete " + selected.getFilename());
                        continue;
                    }
                    success("Deleted " + selected.getFilename());
                } catch (IOException e) {
                    error("Unable to delete " + selected.getFilename());
                }
            } else if (connection().deletePath(remotePath(selected.getFilename()))) {
                success("Deleted " + selected.getFilename());
            } else {
                error("Unable to delete " + selected.getFilename());
            }
        }
        refresh();
    }

    public void uploadSelected() {
        if (!isLocal()) {
            error("Select files in My Files to upload.");
            return;
        }
        FTPConnect connection = connection();
        if (connection == null) {
            error("Connect to an FTP server before uploading.");
            return;
        }
        if (isRoot()) {
            error("Open a folder and select files to upload.");
            return;
        }
        List<PaneFile> files = selectedFiles();
        if (files.isEmpty()) {
            error("Select a file to upload.");
            return;
        }
        info("Uploading " + files.size() + (files.size() == 1 ? " file..." : " files..."));
        for (PaneFile file : files) {
            String localPath = new File(MainPage.get().getMyCurrentDirectory(), file.getFilename()).getAbsolutePath();
            if (connection.uploadFile(localPath, remotePath(file.getFilename()))) {
                success("Uploaded " + file.getFilename());
            } else {
                error("Unable to upload " + file.getFilename());
            }
        }
        MainPage.get().getFtpFilesPanel().refresh();
    }

    public void downloadSelected() {
        if (isLocal()) {
            error("Select files in FTP Files to download.");
            return;
        }
        FTPConnect connection = connection();
        if (connection == null) {
            error("Connect to an FTP server first.");
            return;
        }
        if ("ROOT1337".equals(MainPage.get().getMyCurrentDirectory())) {
            error("Open a local folder to download into.");
            return;
        }
        List<PaneFile> files = selectedFiles();
        if (files.isEmpty()) {
            error("Select a file to download.");
            return;
        }
        info("Downloading " + files.size() + (files.size() == 1 ? " file..." : " files..."));
        for (PaneFile file : files) {
            File dest = new File(MainPage.get().getMyCurrentDirectory(), file.getFilename());
            if (connection.downloadFile(remotePath(file.getFilename()), dest.getAbsolutePath())) {
                success("Downloaded " + file.getFilename());
            } else {
                error("Unable to download " + file.getFilename());
            }
        }
        MainPage.get().getMyFilesPanel().refresh();
    }

    private void createEntry(String name, boolean directory) {
        if (name.indexOf('/') >= 0 || name.indexOf('\\') >= 0 || ".".equals(name) || "..".equals(name)) {
            error("That name can't be used.");
            return;
        }
        if (isLocal()) {
            File file = new File(MainPage.get().getMyCurrentDirectory(), name);
            try {
                boolean created = directory ? file.mkdir() : file.createNewFile();
                if (created) {
                    success((directory ? "Folder created: " : "File created: ") + name);
                    setupMyCurrentDirectory();
                } else {
                    error("Couldn't create " + name + ". It may already exist.");
                }
            } catch (IOException e) {
                error("Couldn't create " + name + ".");
            }
            return;
        }
        String path = remotePath(name);
        try {
            boolean created;
            if (directory) {
                created = connection().makeDirectory(path);
            } else {
                created = connection().getClient().storeFile(path, new ByteArrayInputStream(new byte[0]));
            }
            if (created) {
                success((directory ? "Folder created: " : "File created: ") + name);
                refresh();
            } else {
                error("Couldn't create " + name + " on the server.");
            }
        } catch (IOException e) {
            error("Couldn't create " + name + " on the server.");
        }
    }

    private void renameTo(PaneFile selected, String newName) {
        if (newName.isEmpty() || newName.indexOf('/') >= 0 || newName.indexOf('\\') >= 0) {
            error("That name can't be used.");
            return;
        }
        if (isLocal()) {
            File file = new File(MainPage.get().getMyCurrentDirectory(), selected.getFilename());
            File dest = new File(MainPage.get().getMyCurrentDirectory(), newName);
            if (file.renameTo(dest)) {
                success("Renamed " + selected.getFilename() + " to " + newName);
                setupMyCurrentDirectory();
            } else {
                error("Unable to rename " + selected.getFilename());
            }
            return;
        }
        if (connection().renameFTPFile(remotePath(selected.getFilename()), remotePath(newName))) {
            success("Renamed " + selected.getFilename() + " to " + newName);
            refresh();
        } else {
            error("Unable to rename " + selected.getFilename());
        }
    }

    private boolean isRoot() {
        String dir = isLocal() ? MainPage.get().getMyCurrentDirectory() : MainPage.get().getFtpCurrentDirectory();
        return "ROOT1337".equals(dir);
    }

    private FTPConnect connection() {
        return MainPage.get().getLoginPanel().getConnection();
    }

    private String remotePath(String name) {
        String dir = MainPage.get().getFtpCurrentDirectory();
        if (dir == null || dir.isEmpty() || "ROOT1337".equals(dir)) {
            return name;
        }
        return dir + "/" + name;
    }

    private PaneFile selectedFile() {
        List<PaneFile> files = selectedFiles();
        if (files.isEmpty()) {
            return null;
        }
        return files.get(0);
    }

    private List<PaneFile> selectedFiles() {
        List<PaneFile> files = new ArrayList<PaneFile>();
        for (Object item : new ArrayList<Object>(tableView.getSelectionModel().getSelectedItems())) {
            if (item instanceof PaneFile) {
                PaneFile file = (PaneFile) item;
                if (file.getFilename() != null && !file.getFilename().trim().isEmpty()) {
                    files.add(file);
                }
            }
        }
        return files;
    }

    private void info(String message) {
        MainPage.get().getCommandPanel().addMessage(message, "#64748b", false);
    }

    private void success(String message) {
        MainPage.get().getCommandPanel().addMessage(message, "GREEN", false);
    }

    private void error(String message) {
        MainPage.get().getCommandPanel().addMessage(message, "RED", true);
        SoundUtils.getInstance().playErrorSound();
    }
}
