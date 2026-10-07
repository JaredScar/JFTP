package com.jaredscarito.jftp.model.pages.panels;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;

public class CommandPanel extends Panel {
    public CommandPanel() {}
    public CommandPanel(String name, List<String> styleClasses) {
        super(name, styleClasses);
    }
    public CommandPanel(String name, String styleClass) {
        super(name, styleClass);
    }

    @Override
    public String getName() {
        return this.name;
    }

    private TextFlow commandsField;

    public TextFlow getCommandsField() {
        return this.commandsField;
    }

    public void addMessage(String msg, String color, boolean boldBool) {
        Text text = new Text("   " + msg + "\n");
        String bold = boldBool ? "bold" : "normal";
        text.setStyle("-fx-fill: " + readableColor(color) + "; -fx-font-weight: " + bold + ";");
        commandsField.getChildren().add(text);
    }

    private static String readableColor(String color) {
        if (color == null) {
            return "#ededed";
        }
        if ("GREEN".equalsIgnoreCase(color)) {
            return "#3ECF8E";
        }
        if ("RED".equalsIgnoreCase(color)) {
            return "#f87171";
        }
        if ("GRAY".equalsIgnoreCase(color) || "GREY".equalsIgnoreCase(color) || "#64748b".equalsIgnoreCase(color)) {
            return "#a3a3a3";
        }
        if ("BLUE".equalsIgnoreCase(color)) {
            return "#7dd3fc";
        }
        return color;
    }

    @Override
    public void init() {
        getStyleClass().add("command-card");
        setPadding(new Insets(10, 12, 10, 12));
        setVgap(6);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        ColumnConstraints stretch = new ColumnConstraints();
        stretch.setHgrow(Priority.ALWAYS);
        stretch.setPercentWidth(100);
        getColumnConstraints().add(stretch);

        Label title = new Label("Activity");
        title.getStyleClass().add("section-title");

        TextFlow commandsField = new TextFlow();
        commandsField.getStyleClass().add("log-flow");
        this.commandsField = commandsField;

        ScrollPane scrollPane = new ScrollPane(commandsField);
        scrollPane.getStyleClass().add("log-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(72);
        scrollPane.setMinHeight(56);
        scrollPane.setMaxWidth(Double.MAX_VALUE);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setHgrow(scrollPane, Priority.ALWAYS);

        add(title, 0, 0);
        add(scrollPane, 0, 1);
        addMessage("Ready.", "#64748b", false);
    }
}
