package com.jaredscarito.jftp.view;

import com.jaredscarito.jftp.model.pages.MainPage;
import javafx.scene.control.Dialog;
import javafx.stage.Window;

public final class UiDialogs {
    private UiDialogs() {}

    public static void style(Dialog<?> dialog) {
        if (MainPage.get() != null && MainPage.get().getScene() != null) {
            Window owner = MainPage.get().getScene().getWindow();
            if (owner != null) {
                dialog.initOwner(owner);
            }
        }
        dialog.getDialogPane().getStylesheets().add("com/jaredscarito/jftp/resources/style.css");
        dialog.getDialogPane().getStyleClass().add("modern-dialog");
    }
}
