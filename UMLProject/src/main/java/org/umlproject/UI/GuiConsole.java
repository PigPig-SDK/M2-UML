
package org.umlproject.UI;

import java.io.OutputStream;
import java.io.PrintStream;
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
            Platform.runLater(() -> console.setText(console.getText() + "\n" + line));
        } else {
            buffer.append(c);
        }
    }   
    public static void setupConsole() {
        
        PrintStream ps = new PrintStream(new GuiConsole( GuiController.singleton.consoleOut));
        System.setOut(ps);
        System.setErr(ps); 
        
        AnchorPane consoleAnchorPane = GuiController.singleton.consoleAnchorPane;
        CheckMenuItem viewTerminalMenuItem = GuiController.singleton.viewTerminalMenuItem;
        TextArea consoleOut = GuiController.singleton.consoleOut;
        
        consoleOut.setDisable(true);
        consoleOut.setMouseTransparent(true);
        consoleOut.visibleProperty().bind(viewTerminalMenuItem.selectedProperty());
        consoleOut.managedProperty().bind(consoleOut.visibleProperty());
        consoleAnchorPane.visibleProperty().bind(viewTerminalMenuItem.selectedProperty());
        consoleAnchorPane.managedProperty().bind(consoleAnchorPane.visibleProperty());
    }
    
    public static void updateSize() {
        TextArea consoleOut = GuiController.singleton.consoleOut;
        consoleOut.setPrefHeight(Main.mainStage.getHeight() - 80);//Allow space for console at bottom...
        consoleOut.setPrefWidth(Main.mainStage.getWidth());
    }

}
