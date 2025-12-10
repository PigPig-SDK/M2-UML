package org.umlproject.UI;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.umlproject.Main;

public class GuiHelpWindow {

    public static void showHelp()
    {
        InputStream in = Main.class.getResourceAsStream("help.txt");
        
        VBox contentVBox = FXDialogueFactory.parseTextToVBox(in);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(contentVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(600);
        FXDialogueFactory.createAlertWindow(Alert.AlertType.INFORMATION, "Help", null, null, scrollPane).show();
    
        //FXDialogueFactory.createAlertWindow(Alert.AlertType.INFORMATION, "Help", null, null, FXDialogueFactory.parseTextToVBox(in)).show();
    }
}
