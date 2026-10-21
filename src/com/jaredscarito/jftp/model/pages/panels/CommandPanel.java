package com.jaredscarito.jftp.model.pages.panels;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    private TextFlow transferField;
    private ScrollPane logScroll;
    private ScrollPane transferScroll;
    private Label transferEmpty;
    private Button transfersTab;
    private Button logTab;

    public TextFlow getCommandsField() {
        return this.commandsField;
    }

    public void showTransfers() {
        showTab(true);
    }

    public void showLog() {
        showTab(false);
    }

    public void addMessage(String msg, String color, boolean boldBool) {
        String level = levelFor(color);
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        commandsField.getChildren().addAll(line(time, level, msg, color, boldBool));
        if (isTransfer(msg)) {
            transferField.getChildren().addAll(line(time, level, msg, color, boldBool));
            transferEmpty.setVisible(false);
            transferEmpty.setManaged(false);
        }
        if (logScroll != null) {
            logScroll.setVvalue(1);
        }
        if (transferScroll != null) {
            transferScroll.setVvalue(1);
        }
    }

    private boolean isTransfer(String msg) {
        if (msg == null) {
            return false;
        }
        String lower = msg.toLowerCase();
        return lower.contains("upload") || lower.contains("download") || lower.contains("transfer") || lower.contains("pasted");
    }

    private String levelFor(String color) {
        if (color != null && "RED".equalsIgnoreCase(color)) {
            return "ERROR";
        }
        if (color != null && "GREEN".equalsIgnoreCase(color)) {
            return "OK";
        }
        return "INFO";
    }

    private Text[] line(String time, String level, String msg, String color, boolean boldBool) {
        Text timeText = new Text(time + "   ");
        timeText.setStyle("-fx-fill: #6f6f6f;");
        Text levelText = new Text(String.format("%-5s", level) + "   ");
        levelText.setStyle("-fx-fill: " + readableColor(color) + "; -fx-font-weight: bold;");
        Text msgText = new Text(msg + "\n");
        msgText.setStyle("-fx-fill: #e6e6e6; -fx-font-weight: " + (boldBool ? "bold" : "normal") + ";");
        return new Text[]{timeText, levelText, msgText};
    }

    private static String readableColor(String color) {
        if (color == null) {
            return "#8d8d8d";
        }
        if ("GREEN".equalsIgnoreCase(color)) {
            return "#3ECF8E";
        }
        if ("RED".equalsIgnoreCase(color)) {
            return "#f87171";
        }
        if ("BLUE".equalsIgnoreCase(color)) {
            return "#7dd3fc";
        }
        return "#8d8d8d";
    }

    private void showTab(boolean transfers) {
        transferScroll.setVisible(transfers);
        transferScroll.setManaged(transfers);
        logScroll.setVisible(!transfers);
        logScroll.setManaged(!transfers);
        if (transfers) {
            if (!transfersTab.getStyleClass().contains("selected")) {
                transfersTab.getStyleClass().add("selected");
            }
            logTab.getStyleClass().remove("selected");
        } else {
            if (!logTab.getStyleClass().contains("selected")) {
                logTab.getStyleClass().add("selected");
            }
            transfersTab.getStyleClass().remove("selected");
        }
    }

    private void clearLog() {
        commandsField.getChildren().clear();
        transferField.getChildren().clear();
        transferEmpty.setVisible(true);
        transferEmpty.setManaged(true);
    }

    @Override
    public void init() {
        getStyleClass().add("command-card");
        setPadding(new Insets(8, 12, 10, 12));
        setVgap(6);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        ColumnConstraints stretch = new ColumnConstraints();
        stretch.setHgrow(Priority.ALWAYS);
        stretch.setPercentWidth(100);
        getColumnConstraints().add(stretch);

        transfersTab = new Button("Transfers");
        transfersTab.setFocusTraversable(false);
        transfersTab.getStyleClass().add("log-tab");
        transfersTab.setOnAction(event -> showTab(true));

        logTab = new Button("Log");
        logTab.setFocusTraversable(false);
        logTab.getStyleClass().addAll("log-tab", "selected");
        logTab.setOnAction(event -> showTab(false));

        Button clear = new Button("Clear");
        clear.setFocusTraversable(false);
        clear.getStyleClass().add("log-clear");
        clear.setOnAction(event -> clearLog());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox tabs = new HBox(4, transfersTab, logTab, spacer, clear);
        tabs.setAlignment(Pos.CENTER_LEFT);

        commandsField = new TextFlow();
        commandsField.getStyleClass().add("log-flow");
        transferField = new TextFlow();
        transferField.getStyleClass().add("log-flow");

        transferEmpty = new Label("No transfers yet.");
        transferEmpty.getStyleClass().add("empty-state");
        StackPane transferBody = new StackPane(transferField, transferEmpty);
        StackPane.setAlignment(transferEmpty, Pos.TOP_LEFT);
        transferEmpty.setPadding(new Insets(8, 8, 8, 12));

        logScroll = scroll(commandsField);
        transferScroll = scroll(transferBody);
        transferScroll.setVisible(false);
        transferScroll.setManaged(false);

        StackPane body = new StackPane(logScroll, transferScroll);
        setHgrow(body, Priority.ALWAYS);
        setVgrow(body, Priority.ALWAYS);

        add(tabs, 0, 0);
        add(body, 0, 1);
        addMessage("Ready. Enter a host or pick a saved server to begin.", "#64748b", false);
    }

    private ScrollPane scroll(javafx.scene.Node content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.getStyleClass().add("log-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setMinHeight(72);
        scrollPane.setMaxWidth(Double.MAX_VALUE);
        scrollPane.setMaxHeight(Double.MAX_VALUE);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }
}
