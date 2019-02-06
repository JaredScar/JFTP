package com.jaredscarito.jftp.model.pages.panels;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
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
        text.setStyle("-fx-fill: " + color + "; -fx-font-weight: " + bold + ";");
        commandsField.getChildren().add(text);
    }

    @Override
    public void init() {
        TextFlow commandsField = new TextFlow();
        commandsField.setMinWidth(1080);
        commandsField.setMinHeight(200);
        commandsField.setStyle("-fx-background-color: WHITE;");
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setMinWidth(1096);
        scrollPane.setMinHeight(204);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(commandsField);
        this.commandsField = commandsField;

        setPadding(new Insets(50, 0, 0, 0));
        add(scrollPane, 0, 0);
    }
}
