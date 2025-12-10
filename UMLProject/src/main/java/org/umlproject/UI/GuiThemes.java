
package org.umlproject.UI;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;


public class GuiThemes {
   
    private static GuiThemes instance = new GuiThemes();
    
        public static synchronized GuiThemes getInstance()
    {
        if(instance == null)
        {
            instance = new GuiThemes();
        }
        return instance;
    }

    String curTheme = "Dark Mode";
    
    public void onThemeMenuItemPressed() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> ThemeBox = new ComboBox<>();
        ThemeBox.getItems().add("Dark Mode");
	ThemeBox.getItems().add("Light Mode");
	ThemeBox.setValue(curTheme);
        grid.add(new Label("Theme"), 0, 0);
	grid.add(ThemeBox, 1, 0);
	
        Alert alert = FXDialogueFactory.createAlertWindow(Alert.AlertType.INFORMATION, "Change Theme", "Select a theme", null, grid);
	Optional<ButtonType> result = alert.showAndWait();
	
        if (result.isPresent() && result.get() == ButtonType.OK) //User Acceptance
        {
            String oldTheme = curTheme;
            curTheme = ThemeBox.getValue();
            GuiController.getInstance().rootVBox.getStylesheets().add(getClass().getResource("/org/umlproject/" + curTheme + ".css").toExternalForm());    
            GuiController.getInstance().rootVBox.getStylesheets().remove(getClass().getResource("/org/umlproject/" + oldTheme + ".css").toExternalForm());
            GuiConsole.updateTransparency();
        }
    }
    
    public void applyAlertTheme(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        String cssPath = GuiController.getInstance().getClass().getResource("/org/umlproject/" + curTheme + ".css").toExternalForm();
        dialogPane.getStylesheets().add(cssPath);
    }
}
