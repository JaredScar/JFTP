package com.jaredscarito.jftp.model.pages.panels;

import com.jaredscarito.jftp.api.SoundUtils;
import com.jaredscarito.jftp.controller.MainController;
import com.jaredscarito.jftp.model.FTPConnect;
import com.jaredscarito.jftp.model.PaneFile;
import com.jaredscarito.jftp.model.pages.MainPage;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
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
        getStyleClass().add("myFiles-pane");
        Label myFilesLabel = new Label("My Files");
        if(!getName().equals("1")) {
            myFilesLabel.setText("FTP Files");
        }
        myFilesLabel.getStyleClass().add("filesLabel-" + getName());
        TableView tableView = new TableView();
        this.tableView = tableView;
        TableColumn iconCol = new TableColumn("");
        iconCol.setCellValueFactory(new PropertyValueFactory<>("icon"));
        iconCol.setMaxWidth(500);
        TableColumn fileNamesCol = new TableColumn("Filename");
        fileNamesCol.setCellValueFactory(new PropertyValueFactory<>("obj"));
        TableColumn fileSizesCol = new TableColumn("Size");
        fileSizesCol.setCellValueFactory(new PropertyValueFactory<>("filesize"));
        TableColumn fileModified = new TableColumn("Last Modified");
        fileModified.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getColumns().addAll(iconCol, fileNamesCol, fileSizesCol, fileModified);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("scrollPane-" + this.getName());
        tableView.getStyleClass().add("filesTable-" + this.getName());
        scrollPane.setContent(tableView);
        tableView.setPrefSize(400, 600);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // My Files Buttons
        ImageView[] images = {new ImageView(new Image("com/jaredscarito/jftp/resources/new-folder-icon.png")), new ImageView(new Image("com/jaredscarito/jftp/resources/folder-up-icon.png")),
                new ImageView(new Image("com/jaredscarito/jftp/resources/delete-folder-icon.png")), new ImageView(new Image("com/jaredscarito/jftp/resources/reload-icon.png")),
                new ImageView(new Image("com/jaredscarito/jftp/resources/home-icon.png"))};
        VBox iconsBox = new VBox();
        iconsBox.getStyleClass().add("icons-box");
        Button iconButton;
        int count = 0;
        for(ImageView imageView : images) {
            imageView.setFitWidth(30);
            imageView.setFitHeight(30);
            iconButton = new Button();
            iconButton.getStyleClass().add("icon-button");
            switch (count) {
                case 0:
                    // newFolder or new file
                    iconButton.setId("new-folder-" + getName());
                    iconButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if(event.getButton() == MouseButton.PRIMARY) {
                                if(getName().equals("1")) {
                                    // create table row with TextFields which have action set up on 'enter' key to implement
                                    TextField fileCreateName = new TextField("");
                                    fileCreateName.setOnKeyPressed(new EventHandler<KeyEvent>() {
                                        @Override
                                        public void handle(KeyEvent event) {
                                            if(event.getCode().getName().equalsIgnoreCase("Enter")) {
                                                String fileName = fileCreateName.getText();
                                                File file = new File(MainPage.get().getMyCurrentDirectory()
                                                        + "/" + fileName);
                                                if(!file.exists()) {
                                                    // Doesn't exist, try creating
                                                    try {
                                                        file.createNewFile();
                                                        MainPage.get().getCommandPanel().addMessage("SUCCESS: File " + file.getName() + " created",
                                                                "GREEN", false); // CommandMessage
                                                    } catch (IOException e) {
                                                        MainPage.get().getCommandPanel().addMessage("ERROR: " + e.getCause().getMessage(),
                                                                "RED", true); // CommandMessage
                                                    }
                                                    setupMyCurrentDirectory();
                                                } else {
                                                    MainPage.get().getCommandPanel().addMessage("ERROR: File already exists", "RED", true); // CommandMessage
                                                    SoundUtils.getInstance().playErrorSound(); // Error Sound
                                                }
                                            }
                                        }
                                    });
                                    tableView.getItems().add(new PaneFile(null, fileCreateName, "", ""));
                                } else {
                                    // FTP Files cannot be created through FTP
                                }
                            }
                        }
                    });
                    if(getName().equals("2")) {
                        iconButton = null;
                    }
                    break;
                case 1:
                    //folder-up
                    iconButton.setId("folder-up-" + getName());
                    iconButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if(event.getButton() == MouseButton.PRIMARY) {
                                if(getName().equals("1")) {
                                    if (!MainPage.get().getMyCurrentDirectory().equals("ROOT1337")) {
                                        String[] splitSlashes = MainPage.get().getMyCurrentDirectory().split("/");
                                        if (MainPage.get().getMyCurrentDirectory().split("\\\\").length > 1) {
                                            String lastSlashString = "/" + splitSlashes[splitSlashes.length - 1];
                                            MainPage.get().setMyCurrentDirectory(MainPage.get().getMyCurrentDirectory().replace(lastSlashString, ""));
                                            setupMyCurrentDirectory();
                                        } else {
                                            setupMyRootDirectory();
                                        }
                                    }
                                } else {
                                    if(!MainPage.get().getFtpCurrentDirectory().equals("ROOT1337")) {
                                        if(MainPage.get().getFtpCurrentDirectory().contains("/")) {
                                            String[] split = MainPage.get().getFtpCurrentDirectory().split("/");
                                            String lastSlashString = "/" + split[split.length - 1];
                                            MainPage.get().setFtpCurrentDirectory(MainPage.get().getFtpCurrentDirectory().replace(lastSlashString, ""));
                                            setupFTPCurrentDirectory();
                                        } else {
                                            setupFTPRootDirectory();
                                        }
                                    }
                                }
                            }
                        }
                    });
                    break;
                case 2:
                    //delete-folder or file
                    iconButton.setId("delete-folder-" + getName());
                    iconButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if(event.getButton() == MouseButton.PRIMARY) {
                                if(getName().equals("1")) {
                                    for (Object obj : tableView.getSelectionModel().getSelectedItems()) {
                                        PaneFile selected = (PaneFile) obj;
                                        File file = new File(MainPage.get().getMyCurrentDirectory() + "/" + selected.getFilename());
                                        if (file.isDirectory()) {
                                            try {
                                                FileUtils.deleteDirectory(file);
                                                MainPage.get().getCommandPanel().addMessage("SUCCESS: File " + file.getName() + " deleted",
                                                        "GREEN", false); // CommandMessage
                                            } catch (IOException e) {
                                                // Failed deleting
                                                MainPage.get().getCommandPanel().addMessage("ERROR: Unable to delete directory " + selected.getFilename(),
                                                        "RED", true); // CommandMessage
                                                SoundUtils.getInstance().playErrorSound(); // Error Sound
                                            }
                                        } else {
                                            if (!file.delete()) {
                                                MainPage.get().getCommandPanel().addMessage("ERROR: Unable to delete file " + selected.getFilename(),
                                                        "RED", true); // CommandMessage
                                                SoundUtils.getInstance().playErrorSound(); // Error Sound
                                            }
                                        }
                                        setupMyCurrentDirectory();
                                    }
                                } else {
                                    FTPConnect connection = MainPage.get().getLoginPanel().getConnection();
                                    for(Object obj : tableView.getSelectionModel().getSelectedItems()) {
                                        PaneFile selected = (PaneFile) obj;
                                        if(!MainPage.get().getFtpCurrentDirectory().equals("ROOT1337")) {
                                            try {
                                                connection.getClient().deleteFile(MainPage.get().getFtpCurrentDirectory() + "/" + selected.getFilename());
                                                MainPage.get().getCommandPanel().addMessage("SUCCESS: File " + selected.getFilename() + " deleted",
                                                        "GREEN", false); // CommandMessage
                                            } catch (IOException e) {
                                                MainPage.get().getCommandPanel().addMessage("ERROR: Unable to delete " + selected.getFilename(),
                                                        "RED", true); // CommandMessage
                                                SoundUtils.getInstance().playErrorSound(); // Error Sound
                                            }
                                            setupFTPCurrentDirectory();
                                        } else {
                                            // Is just file name
                                            try {
                                                connection.getClient().deleteFile(selected.getFilename());
                                                MainPage.get().getCommandPanel().addMessage("SUCCESS: File " + selected.getFilename() + " deleted",
                                                        "GREEN", false); // CommandMessage
                                            } catch (IOException e) {
                                                MainPage.get().getCommandPanel().addMessage("ERROR: Unable to delete " + selected.getFilename(),
                                                        "RED", true); // CommandMessage
                                                SoundUtils.getInstance().playErrorSound(); // Error Sound
                                            }
                                            setupFTPRootDirectory();
                                        }
                                    }
                                }
                            }
                        }
                    });
                    break;
                case 3:
                    //reload
                    iconButton.setId("reload-" + getName());
                    iconButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if(event.getButton() == MouseButton.PRIMARY) {
                                if(getName().equals("1")) {
                                    tableView.getItems().clear();
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            if (!MainPage.get().getMyCurrentDirectory().equals("ROOT1337")) {
                                                setupMyCurrentDirectory();
                                            } else {
                                                setupMyRootDirectory();
                                            }
                                        }
                                    }, 100L);
                                } else {
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            if(!MainPage.get().getFtpCurrentDirectory().equals("ROOT1337")) {
                                                setupFTPCurrentDirectory();
                                            } else {
                                                setupFTPRootDirectory();
                                            }
                                        }
                                    }, 100L);
                                }
                            }
                        }
                    });
                    break;
                case 4:
                    //home
                    iconButton.setId("home-" + getName());
                    iconButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if(event.getButton() == MouseButton.PRIMARY) {
                                if(getName().equals("1")) {
                                    setupMyRootDirectory();
                                } else {
                                    setupFTPRootDirectory();
                                }
                            }
                        }
                    });
                    break;
            }
            if(iconButton !=null) {
                iconButton.setGraphic(imageView);
                iconsBox.getChildren().add(iconButton);
            } else {
                Button invisButton = new Button();
                invisButton.setGraphic(imageView);
                invisButton.setVisible(false);
                iconsBox.getChildren().add(invisButton);
            }
            count++;
        }
        /**
         * URL TEXTFIELD (USER)
         */
        TextField currentDirectoryField = new TextField("");
        this.currentDirectoryField = currentDirectoryField;
        currentDirectoryField.getStyleClass().add("URLField-" + getName());
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
        uploadItem.setOnAction(event -> {
            // Upload menu item
            MainPage.get().getCommandPanel().addMessage("Attempting to upload files... ", "GRAY", false); // CommandMessage
            for(Object row : tableView.getSelectionModel().getSelectedItems()) {
                PaneFile pf = (PaneFile) row;
                String path = MainPage.get().getMyCurrentDirectory() + "/" + pf.getFilename();
                if(MainPage.get().getLoginPanel().getConnection().uploadFile(path, MainPage.get().getFtpCurrentDirectory())) {
                    MainPage.get().getCommandPanel().addMessage("SUCCESS: Uploaded file - " + pf.getFilename(), "GREEN", true); // CommandMessage
                } else {
                    // Error
                    MainPage.get().getCommandPanel().addMessage("ERROR: Unable to upload file - " + pf.getFilename(), "RED", true); // CommandMessage
                    SoundUtils.getInstance().playErrorSound(); // Error Sound
                }
            }
        });
        downloadItem.setOnAction(event -> {
            // Download menu item
            MainPage.get().getCommandPanel().addMessage("Attempting to download files... ", "GRAY", false); // CommandMessage
            for(Object row : tableView.getSelectionModel().getSelectedItems()) {
                PaneFile pf = (PaneFile) row;
                String path = MainPage.get().getFtpCurrentDirectory() + "/" + pf.getFilename();
                if(MainPage.get().getLoginPanel().getConnection().downloadFile(path, MainPage.get().getMyCurrentDirectory())) {
                    MainPage.get().getCommandPanel().addMessage("SUCCESS: Downloaded file - " + pf.getFilename(), "GREEN", true); // CommandMessage
                } else {
                    // Error
                    MainPage.get().getCommandPanel().addMessage("ERROR: Unable to download file - " + pf.getFilename(), "RED", true); // CommandMessage
                    SoundUtils.getInstance().playErrorSound(); // Error Sound
                }
            }
        });
        renameItem.setOnAction(event -> {
            // TODO Rename menu item
            if(getName().equals("1")) {
                // Client files
                PaneFile selectedFile = (PaneFile) this.tableView.getSelectionModel().getSelectedItem();
                //
            } else {
                // FTP Files
            }
        });
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
                        if (getName().equals("1")) {
                            String temp;
                            if (!MainPage.get().getMyCurrentDirectory().equals("ROOT1337")) {
                                temp = MainPage.get().getMyCurrentDirectory() + "/" + pf.getFilename();
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
        if(getName().equals("1")) {
            // Buttons on right
            add(myFilesLabel, 0, 0);
            add(scrollPane, 0, 2);
            add(currentDirectoryField, 0, 1);
            add(iconsBox, 1, 2);
        } else {
            // Buttons on left
            add(myFilesLabel, 1, 0);
            add(scrollPane, 1, 2);
            add(currentDirectoryField, 1, 1);
            add(iconsBox, 0, 2);
        }
    }

    public TableView getTableView() {
        return this.tableView;
    }
}
