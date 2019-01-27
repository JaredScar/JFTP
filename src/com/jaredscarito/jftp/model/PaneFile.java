package com.jaredscarito.jftp.model;

import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class PaneFile {
    private String filename;
    private TextField createFileField;
    private String filesize;
    private String lastModified;
    private ImageView icon;
    private Object obj;
    public PaneFile(ImageView icon, Object obj, String filesize, String lastModified) {
        this.icon = icon;
        if(obj instanceof String)
            this.filename = (String) obj;
        if(obj instanceof TextField)
            this.createFileField = (TextField) obj;
        this.filesize = filesize;
        this.lastModified = lastModified;
        this.obj = obj;
    }
    public ImageView getIcon() {
        return this.icon;
    }
    public Object getObj() {
        if(this.createFileField !=null)
            return this.createFileField;
        return this.filename;
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
