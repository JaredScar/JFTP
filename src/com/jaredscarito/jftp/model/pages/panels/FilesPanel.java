package com.jaredscarito.jftp.model.pages.panels;

import com.jaredscarito.jftp.model.PaneFile;
import com.jaredscarito.jftp.model.pages.MainPage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
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

    public String getName() {
        return this.name;
    }

    private TableView tableView;

    @Override
    public void init() {
        getStyleClass().add("myFiles-pane");
        Label myFilesLabel = new Label("My Files"); // Add to Pane TODO
        myFilesLabel.getStyleClass().add("filesLabel-" + getName());
        TableView tableView = new TableView();
        this.tableView = tableView;
        TableColumn fileNamesCol = new TableColumn("Filename");
        fileNamesCol.setCellValueFactory(new PropertyValueFactory<>("filename"));
        TableColumn fileSizesCol = new TableColumn("Size");
        fileSizesCol.setCellValueFactory(new PropertyValueFactory<>("filesize"));
        TableColumn fileModified = new TableColumn("Last Modified");
        fileModified.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getColumns().addAll(fileNamesCol, fileSizesCol, fileModified);
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
                                    if (!myCurrentDirectory.equals("ROOT1337")) {
                                        String[] splitSlashes = myCurrentDirectory.split("/");
                                        if (myCurrentDirectory.split("\\\\").length > 1) {
                                            String lastSlashString = "/" + splitSlashes[splitSlashes.length - 1];
                                            myCurrentDirectory = myCurrentDirectory.replace(lastSlashString, "");
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
                                        File file = new File(myCurrentDirectory + "/" + selected.getFilename());
                                        if (file.isDirectory()) {
                                            try {
                                                FileUtils.deleteDirectory(file);
                                            } catch (IOException e) {
                                                // Failed deleting
                                                JOptionPane.showMessageDialog(null, "FAILED: UNABLE TO DELETE DIRECTORY '" + selected.getFilename() + "'",
                                                        "ERROR", JOptionPane.ERROR_MESSAGE);
                                            }
                                        } else {
                                            System.out.println(file.exists() + " " + myCurrentDirectory + selected.getFilename());
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
                                            if (!myCurrentDirectory.equals("ROOT1337")) {
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
        TextField myURLField = new TextField("");
        this.myURLField = myURLField;
        myURLField.getStyleClass().add("URLField-" + getName());
        this.myURLField.setEditable(false);
        // TODO Gotta add myURLField
        /**
         * USER CLIENT FTP VIEW FILES:
         */
        // Setup FileSystemView for user client
        /**/
        setupMyRootDirectory();
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
                            if (!myCurrentDirectory.equals("ROOT1337")) {
                                myCurrentDirectory = myCurrentDirectory + "/" + pf.getFilename();
                            } else {
                                myCurrentDirectory = pf.getFilename();
                            }
                            File file = new File(myCurrentDirectory);
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
    }

    public TableView getTableView() {
        return this.tableView;
    }
}
