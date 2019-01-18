package com.jaredscarito.jftp.model;

import javafx.scene.image.ImageView;

public class PaneFile {
    private String filename;
    private String filesize;
    private String lastModified;
    private ImageView icon;
    public PaneFile(ImageView icon, String filename, String filesize, String lastModified) {
        this.icon = icon;
        this.filename = filename;
        this.filesize = filesize;
        this.lastModified = lastModified;
    }
    public ImageView getIcon() {
        return this.icon;
    }
    public String getFilename() {
        return this.filename;
    }
    public String getFilesize() {
        return this.filesize;
    }
    public String getLastModified() {
        return this.lastModified;
    }
}
