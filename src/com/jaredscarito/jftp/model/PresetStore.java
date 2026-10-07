package com.jaredscarito.jftp.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class PresetStore {
    private static final File FILE = new File(System.getProperty("user.home"), ".jftp/presets.txt");

    private PresetStore() {}

    public static class Preset {
        public final String name;
        public final String host;
        public final String port;
        public final String username;

        public Preset(String name, String host, String port, String username) {
            this.name = name;
            this.host = host;
            this.port = port;
            this.username = username;
        }
    }

    public static List<Preset> load() {
        List<Preset> presets = new ArrayList<Preset>();
        if (!FILE.isFile()) {
            return presets;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t", -1);
                if (parts.length < 4 || parts[0].trim().isEmpty()) {
                    continue;
                }
                presets.add(new Preset(parts[0], parts[1], parts[2], parts[3]));
            }
        } catch (IOException ignored) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return presets;
    }

    public static void save(Preset preset) {
        List<Preset> presets = load();
        boolean replaced = false;
        for (int i = 0; i < presets.size(); i++) {
            if (presets.get(i).name.equalsIgnoreCase(preset.name)) {
                presets.set(i, preset);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            presets.add(preset);
        }
        write(presets);
    }

    public static void delete(String name) {
        List<Preset> presets = load();
        List<Preset> kept = new ArrayList<Preset>();
        for (Preset preset : presets) {
            if (!preset.name.equalsIgnoreCase(name)) {
                kept.add(preset);
            }
        }
        write(kept);
    }

    private static void write(List<Preset> presets) {
        File parent = FILE.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE), "UTF-8"));
            for (Preset preset : presets) {
                writer.write(safe(preset.name) + "\t" + safe(preset.host) + "\t" + safe(preset.port) + "\t" + safe(preset.username));
                writer.newLine();
            }
        } catch (IOException ignored) {
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static String safe(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\t", " ").replace("\n", " ").replace("\r", " ");
    }
}
