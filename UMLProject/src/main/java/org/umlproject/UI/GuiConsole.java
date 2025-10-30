
package org.umlproject.UI;

import java.io.OutputStream;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class GuiConsole extends OutputStream {
    private TextArea console;
    

    public GuiConsole(TextArea console) {
        this.console = console;
    }

    @Override
    public void write(int b) {
        // Append the character to the TextArea
        console.appendText(String.valueOf((char) b));
    }
    public static void setupConsole() {
        TextArea consoleOut = GuiController.singleton.consoleOut;
        AnchorPane consolePane = GuiController.singleton.consolePane;
        AnchorPane consoleOutPane = GuiController.singleton.consoleOutPane;
        CheckMenuItem viewTerminalMenuItem = GuiController.singleton.viewTerminalMenuItem;
        
        consoleOutPane.visibleProperty().bind(viewTerminalMenuItem.selectedProperty());
        consoleOutPane.managedProperty().bind(consoleOutPane.visibleProperty());
        consolePane.visibleProperty().bind(viewTerminalMenuItem.selectedProperty());
        consolePane.managedProperty().bind(consolePane.visibleProperty());
       
    }

}
