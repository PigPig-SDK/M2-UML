package org.umlproject.UI;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.umlproject.Main;

public class GuiHelpWindow {

    public static void showHelp()
    {
        InputStream in = Main.class.getResourceAsStream("help.txt");
        Alert helpBox = FXDialogueFactory.createAlertWindow(Alert.AlertType.INFORMATION, "Help", null, null, FXDialogueFactory.parseTextToVBox(in));
        helpBox.show();
    }
}
