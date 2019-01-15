package com.jaredscarito.jftp.model;

public class PaneFile {
    private String filename;
    private String filesize;
    private String lastModified;
    public PaneFile(String filename, String filesize, String lastModified) {
        this.filename = filename;
        this.filesize = filesize;
        this.lastModified = lastModified;
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
