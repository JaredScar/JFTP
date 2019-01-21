package com.jaredscarito.jftp.model.pages.panels;

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

    @Override
    public void init() {}
}
