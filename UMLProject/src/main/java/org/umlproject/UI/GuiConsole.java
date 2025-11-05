
package org.umlproject.UI;

import java.io.OutputStream;
import javafx.application.Platform;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import org.umlproject.Main;

public class GuiConsole extends OutputStream {
    private TextArea console;
    

    public GuiConsole(TextArea console) {
        this.console = console;
    }

    private StringBuilder buffer = new StringBuilder();

    @Override
    public void write(int b) {
        char c = (char) b;
        if (c == '\n') {
            String line = buffer.toString();
            buffer.setLength(0); // clear buffer
            Platform.runLater(() -> console.setText(line + "\n" + console.getText()));
        } else {
            buffer.append(c);
        }
    }   
    public static void setupConsole() {
        
        AnchorPane consoleAnchorPane = GuiController.singleton.consoleAnchorPane;
        CheckMenuItem viewTerminalMenuItem = GuiController.singleton.viewTerminalMenuItem;
        TextArea consoleOut = GuiController.singleton.consoleOut;
        
        consoleOut.setDisable(true);
        consoleOut.setMouseTransparent(true);
        consoleOut.visibleProperty().bind(viewTerminalMenuItem.selectedProperty());
        consoleOut.managedProperty().bind(consoleOut.visibleProperty());
        consoleAnchorPane.visibleProperty().bind(viewTerminalMenuItem.selectedProperty());
        consoleAnchorPane.managedProperty().bind(consoleAnchorPane.visibleProperty());
        //consoleOutAnchorPane.visibleProperty().bind(viewTerminalMenuItem.selectedProperty());
        //consoleOutAnchorPane.managedProperty().bind(consoleOutAnchorPane.visibleProperty());
       

       
    }
    
    public static void updateSize() {
        TextArea consoleOut = GuiController.singleton.consoleOut;
        consoleOut.setPrefHeight(Main.mainStage.getHeight()-60);
        consoleOut.setPrefWidth(Main.mainStage.getWidth());
    }

}
