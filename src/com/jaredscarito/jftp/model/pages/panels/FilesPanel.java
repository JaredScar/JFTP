package com.jaredscarito.jftp.model.pages.panels;

import com.jaredscarito.jftp.api.SoundUtils;
import com.jaredscarito.jftp.controller.MainController;
import com.jaredscarito.jftp.model.AppSettings;
import com.jaredscarito.jftp.model.FTPConnect;
import com.jaredscarito.jftp.model.PaneFile;
import com.jaredscarito.jftp.model.PresetStore;
import com.jaredscarito.jftp.model.pages.MainPage;
import com.jaredscarito.jftp.view.UiDialogs;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

import java.util.function.Predicate;
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
        allFiles.clear();
        addParentRow();
        File[] listed = new File(MainPage.get().getMyCurrentDirectory()).listFiles();
        if (listed != null) {
            for (File file : listed) {
                String fileSize = file.isDirectory() ? "—" : MainController.humanReadableByteCount(file.length(), true);
                String when = "—";
                try {
                    FileTime lastModified = Files.getLastModifiedTime(Paths.get(file.getAbsolutePath()));
                    if (lastModified != null) {
                        when = stamp(lastModified.toMillis());
                    }
                } catch (IOException ex) {
                }
                allFiles.add(new PaneFile(getIconImageView(file), file.getName(), fileSize, when));
            }
        }
        paintLocation();
    }
    private void setupMyRootDirectory() {
        allFiles.clear();
        for (File file : File.listRoots()) {
            String fileSize = MainController.humanReadableByteCount((file.getTotalSpace() - file.getFreeSpace()), true);
            String when = "—";
            try {
                FileTime lastModified = Files.getLastModifiedTime(Paths.get(file.getAbsolutePath()));
                if (lastModified != null) {
                    when = stamp(lastModified.toMillis());
                }
            } catch (IOException ex) {
            }
            allFiles.add(new PaneFile(getIconImageView(file), file.getAbsolutePath(), fileSize, when));
        }
        MainPage.get().setMyCurrentDirectory("ROOT1337");
        paintLocation();
    }
    private void setupFTPRootDirectory() {
        allFiles.clear();
        FTPConnect connection = MainPage.get().getLoginPanel().getConnection();
        if (connection != null) {
            FTPFile[] files = connection.getFiles();
            if (files != null) {
                for (FTPFile file : files) {
                    ftpFileSetup(file);
                }
            }
        }
        MainPage.get().setFtpCurrentDirectory("ROOT1337");
        paintLocation();
    }
    private void ftpFileSetup(FTPFile file) {
        if (file == null || file.getName() == null || ".".equals(file.getName()) || "..".equals(file.getName())) {
            return;
        }
        String name = file.getName();
        String size = file.isDirectory() ? "—" : MainController.humanReadableByteCount(file.getSize(), true);
        String lastModified = "—";
        if (file.getTimestamp() != null) {
            lastModified = stamp(file.getTimestamp().getTimeInMillis());
        }
        ImageView icon = new ImageView(new Image(file.isDirectory()
                ? "com/jaredscarito/jftp/resources/ftp-folder-icon.png"
                : "com/jaredscarito/jftp/resources/ftp-file-icon.png"));
        icon.setFitWidth(16);
        icon.setFitHeight(16);
        allFiles.add(new PaneFile(icon, name, size, lastModified));
    }
    private void setupFTPCurrentDirectory() {
        allFiles.clear();
        addParentRow();
        FTPConnect connection = MainPage.get().getLoginPanel().getConnection();
        try {
            if (connection != null && connection.getClient() != null) {
                FTPFile[] files = connection.getClient().listFiles(MainPage.get().getFtpCurrentDirectory());
                if (files != null) {
                    for (FTPFile file : files) {
                        ftpFileSetup(file);
                    }
                }
            }
        } catch (IOException e) {
        }
        paintLocation();
    }

    private void addParentRow() {
        ImageView icon = new ImageView(new Image("com/jaredscarito/jftp/resources/ftp-folder-icon.png"));
        icon.setFitWidth(16);
        icon.setFitHeight(16);
        allFiles.add(new PaneFile(icon, "..", "—", ""));
    }

    private String stamp(long millis) {
        Date when = new Date(millis);
        return new SimpleDateFormat("MMM d, yyyy").format(when) + "\n" + new SimpleDateFormat("HH:mm").format(when);
    }

    private TableView tableView;
    private ObservableList<PaneFile> allFiles;
    private FilteredList<PaneFile> filteredFiles;
    private TextField filterField;
    private HBox breadcrumb;
    private Label pathLabel;
    private Label countLabel;
    private Label hintLabel;
    private Label offlineBadge;
    private Region paneDot;
    private VBox emptyState;
    private VBox presetCards;
    private CheckBox selectAllBox;
    private boolean refreshingChecks;

    private enum FileType {
        CLIENT,
        FTP;
    }
    private FileType currentClipFilesType;
    private HashMap<String, String> clipboardFilePaths = new HashMap<>();

    @Override
    public void init() {
        allFiles = FXCollections.observableArrayList();
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
        RowConstraints footRow = new RowConstraints();
        footRow.setVgrow(Priority.NEVER);
        getRowConstraints().addAll(headerRow, pathRow, tableRow, footRow);

        Label myFilesLabel = new Label(isLocal() ? "My computer" : "Server");
        myFilesLabel.getStyleClass().add("pane-title");
        TableView tableView = new TableView();
        this.tableView = tableView;
        filteredFiles = new FilteredList<PaneFile>(allFiles, new Predicate<PaneFile>() {
            @Override
            public boolean test(PaneFile file) {
                return matchesFilter(file);
            }
        });
        tableView.setItems(filteredFiles);

        selectAllBox = new CheckBox();
        selectAllBox.setFocusTraversable(false);
        selectAllBox.setOnAction(event -> {
            if (selectAllBox.isSelected()) {
                tableView.getSelectionModel().selectAll();
            } else {
                tableView.getSelectionModel().clearSelection();
            }
            tableView.refresh();
        });
        TableColumn checkCol = new TableColumn("");
        checkCol.setGraphic(selectAllBox);
        checkCol.setSortable(false);
        checkCol.setResizable(false);
        checkCol.setPrefWidth(36);
        checkCol.setMinWidth(36);
        checkCol.setMaxWidth(42);
        checkCol.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn column) {
                return new TableCell() {
                    private final CheckBox box = new CheckBox();
                    {
                        box.setFocusTraversable(false);
                        box.setOnAction(event -> {
                            if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                                return;
                            }
                            if (box.isSelected()) {
                                getTableView().getSelectionModel().select(getIndex());
                            } else {
                                getTableView().getSelectionModel().clearSelection(getIndex());
                            }
                        });
                    }
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            box.setSelected(getTableView().getSelectionModel().isSelected(getIndex()));
                            setGraphic(box);
                        }
                    }
                };
            }
        });

        TableColumn fileNamesCol = new TableColumn("NAME");
        fileNamesCol.setCellValueFactory(new PropertyValueFactory<>("obj"));
        fileNamesCol.setMinWidth(160);
        fileNamesCol.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn column) {
                return new TableCell() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(null);
                        if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                            setGraphic(null);
                            return;
                        }
                        Object row = getTableView().getItems().get(getIndex());
                        if (!(row instanceof PaneFile)) {
                            setGraphic(null);
                            return;
                        }
                        PaneFile file = (PaneFile) row;
                        if (file.getObj() instanceof Node && !(file.getObj() instanceof String)) {
                            setGraphic((Node) file.getObj());
                            return;
                        }
                        ImageView icon = file.getIcon();
                        if (icon != null && icon.getParent() instanceof javafx.scene.layout.Pane) {
                            ((javafx.scene.layout.Pane) icon.getParent()).getChildren().remove(icon);
                        }
                        Label name = new Label(file.getFilename() == null ? "" : file.getFilename());
                        name.getStyleClass().add("file-name");
                        HBox line = new HBox(8);
                        line.setAlignment(Pos.CENTER_LEFT);
                        if (icon != null) {
                            line.getChildren().add(icon);
                        }
                        line.getChildren().add(name);
                        setGraphic(line);
                    }
                };
            }
        });
        TableColumn fileSizesCol = new TableColumn("SIZE");
        fileSizesCol.setCellValueFactory(new PropertyValueFactory<>("filesize"));
        fileSizesCol.setPrefWidth(88);
        fileSizesCol.setMinWidth(72);
        TableColumn fileModified = new TableColumn("MODIFIED");
        fileModified.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
        fileModified.setPrefWidth(140);
        fileModified.setMinWidth(120);
        fileModified.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn column) {
                return new TableCell() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null || item.toString().isEmpty()) {
                            setText(null);
                            setGraphic(null);
                            return;
                        }
                        String text = item.toString();
                        int split = text.indexOf('\n');
                        if (split < 0) {
                            setGraphic(null);
                            setText(text);
                            return;
                        }
                        Label date = new Label(text.substring(0, split));
                        date.getStyleClass().add("file-date");
                        Label time = new Label(text.substring(split + 1));
                        time.getStyleClass().add("file-time");
                        VBox lines = new VBox(0, date, time);
                        lines.setAlignment(Pos.CENTER_LEFT);
                        setText(null);
                        setGraphic(lines);
                    }
                };
            }
        });
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getColumns().addAll(checkCol, fileNamesCol, fileSizesCol, fileModified);
        tableView.getStyleClass().add("files-table");
        tableView.setMinHeight(120);
        tableView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        Label empty = new Label(isLocal() ? "This folder is empty" : "This folder is empty");
        empty.getStyleClass().add("empty-state");
        tableView.setPlaceholder(empty);
        tableView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Object>() {
            @Override
            public void onChanged(Change<? extends Object> change) {
                if (refreshingChecks) {
                    return;
                }
                refreshingChecks = true;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        tableView.refresh();
                        refreshingChecks = false;
                    }
                });
            }
        });
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> markActive());

        boolean mdl2 = Font.getFamilies().contains("Segoe MDL2 Assets");
        String[] glyphs = mdl2
                ? new String[]{"\uE74A", "\uE80F", "\uE72C", "\uE8F4", "\uE74D"}
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
        Label transferGlyph = new Label(mdl2 ? (isLocal() ? "\uE898" : "\uE896") : (isLocal() ? "\u2191" : "\u2193"));
        transferGlyph.getStyleClass().add(mdl2 ? "mdl2-glyph" : "tool-glyph");
        transferButton.setGraphic(transferGlyph);
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
                        if ("..".equals(pf.getFilename())) {
                            goUp();
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
                            } else {
                                uploadSelected();
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
                                if (files == null || files.length <= 1) {
                                    downloadSelected();
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
        buildChrome(myFilesLabel, iconsBox, mdl2);
    }

    private void buildChrome(Label title, HBox tools, boolean mdl2) {
        paneDot = new Region();
        paneDot.getStyleClass().add("pane-dot");
        if (!isLocal()) {
            paneDot.getStyleClass().add("pane-dot-off");
        }
        pathLabel = new Label(isLocal() ? "This PC" : "");
        pathLabel.getStyleClass().add("pane-path");
        pathLabel.setMaxWidth(280);
        offlineBadge = new Label("offline");
        offlineBadge.getStyleClass().add("offline-badge");
        boolean remote = !isLocal();
        offlineBadge.setVisible(remote);
        offlineBadge.setManaged(remote);
        pathLabel.setVisible(!remote);
        pathLabel.setManaged(!remote);

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(paneDot, title, pathLabel, offlineBadge, spacer, tools);

        breadcrumb = new HBox(2);
        breadcrumb.setAlignment(Pos.CENTER_LEFT);

        filterField = new TextField();
        filterField.setPromptText("Filter");
        filterField.getStyleClass().add("shell-field");
        filterField.setPrefWidth(150);
        Label search = new Label(mdl2 ? "\uE721" : "\u2315");
        search.getStyleClass().add(mdl2 ? "mdl2-glyph" : "tool-glyph");
        HBox filterShell = new HBox(6, search, filterField);
        filterShell.setAlignment(Pos.CENTER_LEFT);
        filterShell.getStyleClass().add("input-shell");
        filterShell.setMinWidth(150);
        filterShell.setPrefWidth(180);
        filterShell.setMaxWidth(210);
        filterField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (filteredFiles != null) {
                filteredFiles.setPredicate(new Predicate<PaneFile>() {
                    @Override
                    public boolean test(PaneFile file) {
                        return matchesFilter(file);
                    }
                });
            }
            updateFooter();
        });

        Region navSpacer = new Region();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);
        HBox navRow = new HBox(8, breadcrumb, navSpacer, filterShell);
        navRow.setAlignment(Pos.CENTER_LEFT);

        emptyState = buildEmptyState(mdl2);
        StackPane body = new StackPane();
        body.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        body.getChildren().add(tableView);
        if (remote) {
            body.getChildren().add(emptyState);
            tableView.setVisible(false);
            tableView.setManaged(false);
        } else {
            emptyState.setVisible(false);
            emptyState.setManaged(false);
        }
        setVgrow(body, Priority.ALWAYS);
        setHgrow(body, Priority.ALWAYS);

        countLabel = new Label("—");
        countLabel.getStyleClass().add("pane-foot");
        hintLabel = new Label("");
        hintLabel.getStyleClass().add("pane-hint");
        Region footSpacer = new Region();
        HBox.setHgrow(footSpacer, Priority.ALWAYS);
        HBox footer = new HBox(12, countLabel, footSpacer, hintLabel);
        footer.setAlignment(Pos.CENTER_LEFT);

        add(header, 0, 0);
        add(navRow, 0, 1);
        add(body, 0, 2);
        add(footer, 0, 3);

        allFiles.addListener(new ListChangeListener<PaneFile>() {
            @Override
            public void onChanged(Change<? extends PaneFile> change) {
                updateFooter();
            }
        });

        if (isLocal()) {
            setupMyRootDirectory();
        } else {
            reloadPresetCards();
            paintLocation();
        }
    }

    private VBox buildEmptyState(boolean mdl2) {
        StackPane iconWrap = new StackPane();
        iconWrap.getStyleClass().add("empty-icon");
        Label icon = new Label(mdl2 ? "\uE968" : "\u25A1");
        icon.getStyleClass().add(mdl2 ? "mdl2-glyph" : "tool-glyph");
        iconWrap.getChildren().add(icon);

        Label heading = new Label("No server connected");
        heading.getStyleClass().add("empty-title");
        Label copy = new Label("Enter your server details above, or jump straight into a saved server.");
        copy.setWrapText(true);
        copy.setTextAlignment(TextAlignment.CENTER);
        copy.setAlignment(Pos.CENTER);
        copy.setMaxWidth(380);
        copy.getStyleClass().add("empty-copy");

        presetCards = new VBox(8);
        presetCards.setAlignment(Pos.CENTER);
        presetCards.setMaxWidth(460);
        presetCards.setFillWidth(true);

        Button manual = new Button("Enter details manually");
        manual.setFocusTraversable(false);
        manual.getStyleClass().add("text-link");
        manual.setOnAction(event -> {
            if (MainPage.get() != null) {
                MainPage.get().getLoginPanel().focusHost();
            }
        });

        VBox empty = new VBox(12, iconWrap, heading, copy, presetCards, manual);
        empty.setAlignment(Pos.CENTER);
        empty.setMaxWidth(520);
        empty.setPadding(new Insets(16, 20, 16, 20));
        return empty;
    }

    public void reloadPresetCards() {
        if (presetCards == null) {
            return;
        }
        presetCards.getChildren().clear();
        for (final PresetStore.Preset preset : PresetStore.load()) {
            Label badge = new Label("FTP");
            badge.getStyleClass().add("proto-badge");
            Label name = new Label(preset.name);
            name.getStyleClass().add("preset-name");
            Label host = new Label(preset.host);
            host.getStyleClass().add("preset-host");
            VBox text = new VBox(2, name, host);
            HBox.setHgrow(text, Priority.ALWAYS);
            Button connect = new Button("Connect");
            connect.setFocusTraversable(false);
            connect.getStyleClass().add("preset-connect");
            connect.setOnAction(event -> MainPage.get().getLoginPanel().usePreset(preset.host, preset.port, preset.username));
            HBox card = new HBox(10, badge, text, connect);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setMaxWidth(Double.MAX_VALUE);
            card.getStyleClass().add("preset-card");
            presetCards.getChildren().add(card);
        }
    }

    public void setRemoteConnected(boolean connected, String host) {
        if (isLocal() || offlineBadge == null) {
            return;
        }
        if (connected) {
            offlineBadge.setVisible(false);
            offlineBadge.setManaged(false);
            pathLabel.setText(host == null ? "" : host);
            pathLabel.setVisible(true);
            pathLabel.setManaged(true);
            paneDot.getStyleClass().remove("pane-dot-off");
        } else {
            offlineBadge.setText("offline");
            offlineBadge.setVisible(true);
            offlineBadge.setManaged(true);
            pathLabel.setVisible(false);
            pathLabel.setManaged(false);
            if (!paneDot.getStyleClass().contains("pane-dot-off")) {
                paneDot.getStyleClass().add("pane-dot-off");
            }
        }
        syncEmpty();
    }

    private void syncEmpty() {
        if (isLocal() || emptyState == null) {
            return;
        }
        boolean disconnected = connection() == null;
        emptyState.setVisible(disconnected);
        emptyState.setManaged(disconnected);
        tableView.setVisible(!disconnected);
        tableView.setManaged(!disconnected);
    }

    private boolean matchesFilter(PaneFile file) {
        if (filterField == null || filterField.getText() == null || filterField.getText().trim().isEmpty()) {
            return true;
        }
        String name = file == null ? null : file.getFilename();
        return name != null && name.toLowerCase().contains(filterField.getText().trim().toLowerCase());
    }

    private void updateFooter() {
        if (countLabel == null || hintLabel == null) {
            return;
        }
        if (!isLocal() && connection() == null) {
            countLabel.setText("—");
            hintLabel.setText("");
            return;
        }
        int count = 0;
        if (filteredFiles != null) {
            for (PaneFile file : filteredFiles) {
                if (file.getFilename() != null && !"..".equals(file.getFilename())) {
                    count++;
                }
            }
        }
        countLabel.setText(count + (count == 1 ? " item" : " items"));
        hintLabel.setText(isLocal() ? "Double-click a file to upload" : "Double-click a file to download");
    }

    private void paintLocation() {
        if (breadcrumb == null) {
            return;
        }
        breadcrumb.getChildren().clear();
        if (isLocal()) {
            if (isRoot()) {
                addCrumb("This PC", true, null);
                pathLabel.setText("This PC");
            } else {
                addCrumb("This PC", false, new Runnable() {
                    @Override
                    public void run() {
                        setupMyRootDirectory();
                    }
                });
                File current = new File(MainPage.get().getMyCurrentDirectory());
                java.util.List<File> chain = new ArrayList<File>();
                File cursor = current;
                while (cursor != null) {
                    chain.add(0, cursor);
                    cursor = cursor.getParentFile();
                }
                for (int i = 0; i < chain.size(); i++) {
                    final File target = chain.get(i);
                    boolean last = i == chain.size() - 1;
                    addCrumbSeparator();
                    String label = target.getName();
                    if (label == null || label.isEmpty()) {
                        label = target.getPath();
                    }
                    addCrumb(label, last, last ? null : new Runnable() {
                        @Override
                        public void run() {
                            MainPage.get().setMyCurrentDirectory(target.getAbsolutePath());
                            setupMyCurrentDirectory();
                        }
                    });
                }
                pathLabel.setText(current.getAbsolutePath());
            }
        } else if (connection() == null || isRoot()) {
            addCrumb("/", true, null);
        } else {
            final String full = MainPage.get().getFtpCurrentDirectory();
            addCrumb("/", false, new Runnable() {
                @Override
                public void run() {
                    setupFTPRootDirectory();
                }
            });
            java.util.List<String> parts = new ArrayList<String>();
            if (full != null) {
                for (String part : full.split("/")) {
                    if (!part.isEmpty()) {
                        parts.add(part);
                    }
                }
            }
            String built = "";
            for (int i = 0; i < parts.size(); i++) {
                built = built.isEmpty() ? parts.get(i) : built + "/" + parts.get(i);
                final String target = built;
                boolean last = i == parts.size() - 1;
                addCrumbSeparator();
                addCrumb(parts.get(i), last, last ? null : new Runnable() {
                    @Override
                    public void run() {
                        MainPage.get().setFtpCurrentDirectory(target);
                        setupFTPCurrentDirectory();
                    }
                });
            }
        }
        updateFooter();
        syncEmpty();
    }

    private void addCrumb(String text, boolean current, final Runnable action) {
        Button crumb = new Button(text);
        crumb.setMnemonicParsing(false);
        crumb.setFocusTraversable(false);
        crumb.getStyleClass().add("crumb");
        if (current) {
            crumb.getStyleClass().add("crumb-current");
        }
        if (action != null) {
            crumb.setOnAction(event -> {
                markActive();
                action.run();
            });
        }
        breadcrumb.getChildren().add(crumb);
    }

    private void addCrumbSeparator() {
        Label separator = new Label("\u203a");
        separator.getStyleClass().add("crumb-sep");
        breadcrumb.getChildren().add(separator);
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
        allFiles.clear();
        MainPage.get().setFtpCurrentDirectory("ROOT1337");
        setRemoteConnected(false, null);
        paintLocation();
    }

    public void refresh() {
        if (!isLocal() && connection() == null) {
            syncEmpty();
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
        if (MainPage.get() == null || MainPage.get().getLoginPanel() == null) {
            return null;
        }
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
                if (file.getFilename() != null && !file.getFilename().trim().isEmpty() && !"..".equals(file.getFilename())) {
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
