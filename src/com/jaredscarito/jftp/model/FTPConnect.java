package com.jaredscarito.jftp.model;

import com.jaredscarito.jftp.controller.MainController;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;

public class FTPConnect {
    private String username;
    private String password;
    private String host;
    private int port;
    private MainController main;
    private FTPClient client;
    public FTPConnect(MainController main, String host, int port, String username, String password) {
        this.main = main;
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
        this.client = new FTPClient();
        try {
            this.client.connect(this.host, this.port) ;
            if(!this.client.login(this.username, this.password)) return false;
            this.client.enterLocalPassiveMode();
            this.client.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    public boolean disconnect() {
        try {
            this.client.disconnect();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public FTPClient getClient() {
        return this.client;
    }

    public FTPFile[] getFiles() {
        try {
            return this.client.listFiles();
        } catch (IOException e) {
            return null;
        }
    }
    public FTPFile[] getDirectories() {
        try {
            return this.client.listDirectories();
        } catch (IOException e) {
            return null;
        }
    }
    public boolean fileExists(String fileName) {
        try {
            for (FTPFile file : this.client.listFiles()) {
                if(file.getName().equals(fileName)) {
                    return true;
                }
            }
        } catch (IOException ex) {
            return false;
        }
        return false;
    }
    public boolean deleteFile(String fileName, String absPath) {
        if(this.fileExists(fileName)) {
            try {
                this.client.deleteFile(absPath);
                // TODO Do aestetics
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }
    public boolean createFile(String fileName, CopyFile.FtpFileType ftpFileType) {
        return false; // TODO
    }
    public boolean renameFile(String oldName, String newName, CopyFile.FtpFileType ftpFileType) {
        return false; // TODO
    }
}
