package org.umlproject.UI;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;

import java.util.Optional;

public class GuiSearch {

    public static void GuiSearch(){

        TextField input = new TextField();
        Alert popup = FXDialogueFactory.createAlertWindow(Alert.AlertType.INFORMATION, "Class Search",
                "Search for a Class", null, input);
        Optional<ButtonType> result = popup.showAndWait();
        UMLClass umlClass = UMLDocument.getInstance().getClass(input.getText());
        if(umlClass != null){
            if(umlClass.getListener() instanceof GuiClass guiClass && guiClass != null){
                GuiCamera.setCameraLocation(GuiController.getInstance().getWorld().sceneToLocal(umlClass.getLocation()));
                GuiSelect.getInstance().selectUiElement(guiClass);
            }
        }
        else{
            Alert error = FXDialogueFactory.createAlertWindow(Alert.AlertType.ERROR, "Error",
                    "Class does not exist!", "", null);
            error.showAndWait();
        }

    }

}
