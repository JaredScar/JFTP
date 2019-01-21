package com.jaredscarito.jftp.model.pages.panels;

import javafx.scene.layout.GridPane;

import java.util.List;

public abstract class Panel extends GridPane {
    protected String name;
    public Panel() {}
    public Panel(String name, List<String> styleClasses) {
        this.name = name;
        this.getStyleClass().addAll(styleClasses);
        init();
    }
    public Panel(String name, String styleClass) {
        this.name = name;
        this.getStyleClass().add(styleClass);
        init();
    }

    public abstract void init();

    public abstract String getName();
}
