package com.jaredscarito.jftp.model;

import com.jaredscarito.jftp.controller.MainController;

import java.io.File;

public class CopyFile {
    public enum FtpFileType {
        CLIENT,
        SERVER;
    }
    private MainController main;
    private File file;
    private FtpFileType copyFileType;
    public CopyFile(MainController main, File file, FtpFileType copyFileType) {
        this.file = file;
        this.main = main;
        this.copyFileType = copyFileType;
    }

    public boolean pasteFile(String location) {
        return false; // TODO
    }
}
