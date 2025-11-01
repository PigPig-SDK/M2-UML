package org.umlproject.UI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.umlproject.Main;

public class GuiHelp {

    public static void showHelp()
    {
        Alert helpBox = new Alert(Alert.AlertType.INFORMATION);
        helpBox.setTitle("Help");
        helpBox.setHeaderText(null);
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER_LEFT);
        
        Label createdBy = new Label("UMLEditor by Microsoft2\n\nTeam Members: Adam, Aidan, Keetyn, Michelle, Sean, Ty Greene");
        createdBy.setFont(new Font(16));
        
        Separator line = new Separator();

        //Read from help text file...
        InputStream in = Main.class.getResourceAsStream("help.txt");
        
        String text = "Error!";
        try 
        {
            text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (IOException e) {  }
        
        Label helpText = new Label(text);

        content.getChildren().addAll(createdBy, line, helpText);

        helpBox.getDialogPane().setContent(content);
        helpBox.showAndWait();
    }
}
