package com.jaredscarito.jftp.model.pages.panels;

import javafx.scene.layout.GridPane;

import java.util.List;

public abstract class Panel extends GridPane {
    public Panel() {}
    public Panel(String name, List<String> styleClasses) {}
    public Panel(String name, String styleClass) {}

    public abstract void init();
}
