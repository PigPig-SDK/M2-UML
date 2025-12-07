package org.umlproject.UI;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;

import java.util.Optional;

public class GuiSearch {

    public static void GuiSearchSetup(){

        if(UMLDocument.getInstance().getClassSet().isEmpty()){
            FXDialogueFactory.createAlertWindow(Alert.AlertType.ERROR, "Class Search",
                    "Your project requires at least 1 class", null, null).show();
            return;
        }

        TextField inputText = new TextField();
        Platform.runLater(() -> {
            inputText.requestFocus();
            inputText.selectAll();
        });
        TitledPane inputTextWrap = new TitledPane("Class Name:", inputText);

        ComboBox<String> inputDropdown = new ComboBox<>();
        inputDropdown.getItems().addAll(UMLDocument.getInstance().getClassSet().keySet());
        inputDropdown.setPromptText("...");

        VBox inputContainer = new VBox(10);
        inputContainer.getChildren().addAll(inputTextWrap, new Label("or"), inputDropdown);

        Alert popup = FXDialogueFactory.createAlertWindow(Alert.AlertType.INFORMATION, "Class Search",
                "Enter class name or select from dropdown", null, inputContainer);

        Optional<ButtonType> result = popup.showAndWait();
        if(!result.isPresent()){
            return;
        }

        UMLClass umlClass = UMLDocument.getInstance().getClass(inputText.getText());
        if(umlClass != null){
            GuiSearchAct(umlClass);
        }
        else{

            umlClass = UMLDocument.getInstance().getClass(inputDropdown.getValue());
            if(umlClass != null){
                GuiSearchAct(umlClass);
            }
            else{
                Alert error = FXDialogueFactory.createAlertWindow(Alert.AlertType.ERROR, "Error",
                        "Class does not exist!", "", null);
                error.showAndWait();
            }
        }
    }

    public static void GuiSearchAct(UMLClass inputClass){
            if(inputClass.getListener() instanceof GuiClass guiClass && guiClass != null){

                GuiCamera.resetCameraLocation();
                GuiCamera.setCameraLocation(GuiCamera.getScreenCenter().subtract(guiClass.getLocation()));
                GuiSelect.getInstance().selectUiElement(guiClass);

            }
        }
}
