package com.jaredscarito.jftp.model;

import java.io.File;

public class FTPConnect {
    private String username;
    private String password;
    private String host;
    private int port;
    public FTPConnect(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
    public FTPConnect(String host, String port, String username, String password) {
        this.host = host;
        this.port = Integer.parseInt(port);
        this.username = username;
        this.password = password;
    }
    public boolean connect() {
        return false; // TODO
    }
    public boolean disconnect() {
        return false; // TODO
    }

    public File[] getFiles() {
        return null; // TODO
    }
    public boolean fileExists(String fileName) {
        return false; // TODO
    }
    public boolean deleteFile(String fileName) {
        return false; // TODO
    }
    public boolean copyFile(String fileName) {
        return false; // TODO
    }
    public boolean createFile(String fileName) {
        return false; // TODO
    }
    public boolean renameFile(String oldName, String newName) {
        return false; // TODO
    }
}
