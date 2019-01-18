package com.jaredscarito.jftp.model.pages.panels;

import com.jaredscarito.jftp.controller.MainController;
import com.jaredscarito.jftp.model.PaneFile;
import com.jaredscarito.jftp.model.pages.MainPage;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.apache.commons.io.FileUtils;

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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FilesPanel extends Panel {
    private boolean buttonsLeft;
    private String name;
    public FilesPanel() {}
    public FilesPanel(String name, List<String> styleClasses) {
        this.name = name;
        getStyleClass().addAll(styleClasses);
        init();
    }
    public FilesPanel(String name, String styleClass) {
        this.name = name;
        getStyleClass().add(styleClass);
        init();
    }
    public FilesPanel(String name, String styleClass, boolean buttonsLeft) {
        this.name = name;
        getStyleClass().add(styleClass);
        this.buttonsLeft = buttonsLeft;
        init();
    }
    public FilesPanel(String name, List<String> styleClasses, boolean buttonsLeft) {
        this.name = name;
        getStyleClass().addAll(styleClasses);
        this.buttonsLeft = buttonsLeft;
        init();
    }

    private String getName() {
        return this.name;
    }

    public ImageView getIconImageView(File file) {
        ImageIcon imgIcon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file);
        BufferedImage bufferedImage = (BufferedImage) imgIcon.getImage();
        Image img = SwingFXUtils.toFXImage(bufferedImage, null);
        ImageView iconImg = new ImageView(img);
        iconImg.setFitHeight(15);
        iconImg.setFitWidth(15);
        return iconImg;
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

    private TableView tableView;
    private TextField currentDirectoryField;

    @Override
    public void init() {
        getStyleClass().add("myFiles-pane");
        Label myFilesLabel = new Label("My Files"); // Add to Pane TODO
        if(!getName().equals("1")) {
            myFilesLabel.setText("FTP Files");
        }
        myFilesLabel.getStyleClass().add("filesLabel-" + getName());
        TableView tableView = new TableView();
        this.tableView = tableView;
        tableView.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // On Table Row Drag TODO
            }
        });
        tableView.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                // On Table Row Drag dropped TODO
            }
        });
        TableColumn iconCol = new TableColumn("");
        iconCol.setCellValueFactory(new PropertyValueFactory<>("icon"));
        TableColumn fileNamesCol = new TableColumn("Filename");
        fileNamesCol.setCellValueFactory(new PropertyValueFactory<>("filename"));
        TableColumn fileSizesCol = new TableColumn("Size");
        fileSizesCol.setCellValueFactory(new PropertyValueFactory<>("filesize"));
        TableColumn fileModified = new TableColumn("Last Modified");
        fileModified.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getColumns().addAll(iconCol, fileNamesCol, fileSizesCol, fileModified);
        ScrollPane scrollPane = new ScrollPane(); // Add to Pane TODO
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
                                    // TODO create table row with TextFields which have action set up on 'enter' key to implement
                                } else {
                                    // TODO FTP files
                                }
                            }
                        }
                    });
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
                                    // TODO FTP Files
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
                                    // TODO (possible bugs)
                                    for (Object obj : tableView.getSelectionModel().getSelectedItems()) {
                                        PaneFile selected = (PaneFile) obj;
                                        File file = new File(MainPage.get().getMyCurrentDirectory() + "/" + selected.getFilename());
                                        if (file.isDirectory()) {
                                            try {
                                                FileUtils.deleteDirectory(file);
                                            } catch (IOException e) {
                                                // Failed deleting
                                                JOptionPane.showMessageDialog(null, "FAILED: UNABLE TO DELETE DIRECTORY '" + selected.getFilename() + "'",
                                                        "ERROR", JOptionPane.ERROR_MESSAGE);
                                            }
                                        } else {
                                            if (!file.delete()) {
                                                JOptionPane.showMessageDialog(null, "FAILED: UNABLE TO DELETE FILE '" + selected.getFilename() + "'",
                                                        "ERROR", JOptionPane.ERROR_MESSAGE);
                                            }
                                        }
                                        setupMyCurrentDirectory();
                                    }
                                } else {
                                    // TODO FTP Files
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
                                    new java.util.Timer().schedule(new TimerTask() {
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
                                    // TODO FTP Files
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
                                    // TODO FTP Files
                                }
                            }
                        }
                    });
                    break;
            }
            iconButton.setGraphic(imageView);
            iconsBox.getChildren().add(iconButton);
            count++;
        }
        // TODO Gotta add iconsBox
        /**
         * URL TEXTFIELD (USER)
         */
        TextField currentDirectoryField = new TextField("");
        this.currentDirectoryField = currentDirectoryField;
        currentDirectoryField.getStyleClass().add("URLField-" + getName());
        this.currentDirectoryField.setEditable(false);
        // TODO Gotta add currentDirectoryField
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
        copyItem.getStyleClass().add("context-item");
        pasteItem.getStyleClass().add("context-item");
        uploadItem.getStyleClass().add("context-item");
        downloadItem.getStyleClass().add("context-item");
        cm.getItems().addAll(copyItem, pasteItem);
        if(getName().equals("1")) {
            cm.getItems().add(uploadItem);
        } else {
            cm.getItems().add(downloadItem);
        }
        this.tableView.setContextMenu(cm);
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
        uploadItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // TODO Upload menu item
            }
        });
        downloadItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // TODO Download menu item
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
                            if (!MainPage.get().getMyCurrentDirectory().equals("ROOT1337")) {
                                MainPage.get().setMyCurrentDirectory(MainPage.get().getMyCurrentDirectory() + "/" + pf.getFilename());
                            } else {
                                MainPage.get().setMyCurrentDirectory(pf.getFilename());
                            }
                            File file = new File(MainPage.get().getMyCurrentDirectory());
                            if (file.isDirectory()) {
                                setupMyCurrentDirectory();
                            }
                        } else {
                            // TODO FTP Files
                        }
                    }
                }
            }
        });
        // TODO Add all of the items to this GridPane extension:
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
