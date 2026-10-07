package com.jaredscarito.jftp.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AppSettings {
    private static final File FILE = new File(System.getProperty("user.home"), ".jftp/settings.properties");
    private static final Properties PROPS = new Properties();

    static {
        load();
    }

    private AppSettings() {}

    public static int defaultPort() {
        try {
            int port = Integer.parseInt(PROPS.getProperty("defaultPort", "21"));
            if (port < 1 || port > 65535) {
                return 21;
            }
            return port;
        } catch (NumberFormatException ex) {
            return 21;
        }
    }

    public static boolean confirmDelete() {
        return Boolean.parseBoolean(PROPS.getProperty("confirmDelete", "true"));
    }

    public static void update(int port, boolean confirmDelete) {
        PROPS.setProperty("defaultPort", Integer.toString(port));
        PROPS.setProperty("confirmDelete", Boolean.toString(confirmDelete));
        store();
    }

    private static void load() {
        if (!FILE.isFile()) {
            return;
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream(FILE);
            PROPS.load(in);
        } catch (IOException ignored) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static void store() {
        File parent = FILE.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(FILE);
            PROPS.store(out, "JFTP settings");
        } catch (IOException ignored) {
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
