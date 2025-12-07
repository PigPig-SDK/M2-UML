package org.umlproject.UI;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


public class FXUtility {
    
    private static FXUtility instance = null;
    
    public static FXUtility getInstance()
    {
        if(instance == null)
        {
            instance = new FXUtility();
        }
        return instance;
    }
    
    public void applyIconsToButtons(String toolTipString, Button button, String iconDirectory, double width, double height)
    {
        Image icon = new Image(getClass().getResource(iconDirectory).toExternalForm());
        button.setText("");//Clear text...
        ImageView iconView = new ImageView(icon);
        iconView.setFitWidth(width);
        iconView.setFitHeight(height);
        iconView.setPreserveRatio(true);
        //Remove background...
        button.setStyle(
            "-fx-background-color: transparent;" + "-fx-border-color: transparent;"
        );
        //Make the icon dim when mousing over.
        iconView.setOpacity(0.7);
        button.setOnMouseEntered(e -> iconView.setOpacity(1.0));
        button.setOnMouseExited(e -> iconView.setOpacity(0.7));
        
        Tooltip tip = new Tooltip(toolTipString);
        tip.setShowDelay(Duration.seconds(0.15));
        button.setTooltip(tip);
        //Set graphic
        button.setGraphic(iconView);
    }
    public static void selectTextFieldCarrotAtEnd(TextField field)
    {
        Platform.runLater(() -> {
            
            if (field == null) return;
            
            field.requestFocus();
            field.selectAll();
            field.positionCaret(field.getText().length());
        });
    }
}
