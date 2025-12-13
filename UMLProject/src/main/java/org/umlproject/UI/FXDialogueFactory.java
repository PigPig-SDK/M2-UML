package org.umlproject.UI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.umlproject.RelationshipType;


public class FXDialogueFactory {
    /**
     * Create alert window
     * @param allertType The type of alert to be expecting
     * @param titleText The window title
     * @param headerText The header text, can be null
     * @param contentText The content text to be used inplace of a content node
     * @param content The content to display
     */
    public static Alert createAlertWindow(Alert.AlertType allertType, String titleText, String headerText, String contentText, Node content)
    {
        Alert alert = new Alert(allertType);
        GuiThemes.getInstance().applyAlertTheme(alert);
        alert.setTitle(titleText);
        alert.setContentText(contentText);
        alert.setHeaderText(headerText);
        alert.getDialogPane().setContent(content);
        


        
        FadeTransition fade = new FadeTransition(Duration.millis(250), alert.getDialogPane());
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
        
        return alert;
    }
    /**
     * Generate a VBOX with specific formatting
     * 
     * Note to teacher: This code was AI generated! I didn't want to spend valuable time writing something so boring!
     * 
     * @param in a textfile inputstream.
     * @return A VBOX parsed from your textfile/inputstream.
     */
    public static VBox parseTextToVBox(InputStream in) {
        VBox content = new VBox(10);

        StringBuilder currentLabelText = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

            String line;
            while ((line = br.readLine()) != null) {

                if (line.equals("<LINEBREAK>")) {

                    // Add the current text block if it has content
                    if (currentLabelText.length() > 0) {
                        Label tempLabel = new Label(currentLabelText.toString());
                        tempLabel.setFont(Font.font(13));
                        tempLabel.setWrapText(true);
                        content.getChildren().add(tempLabel);
                        
                        currentLabelText.setLength(0); // reset
                    }

                    // Add a separator
                    content.getChildren().add(new Separator());

                } else {
                    // Append this line
                    currentLabelText.append(line).append("\n");
                }
            }

            // Add any final text block
            if (currentLabelText.length() > 0) {
                Label tempLabel = new Label(currentLabelText.toString());
                tempLabel.setFont(Font.font(13));
                tempLabel.setWrapText(true);
                content.getChildren().add(tempLabel);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

}
