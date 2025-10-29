package org.umlproject.UI;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import org.umlproject.Main;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;
import org.umlproject.UMLRelationship;

import java.util.ArrayList;
import java.util.Optional;

public class GuiSelect {

    private static GuiSelect instance;

    private static ArrayList<UMLRelationship> selectedRelationships;

    private static ArrayList<GuiClass> selectedGUIClasses;


    /**
     * Singleton GuiSelect constructor. Creates three relevant ArrayLists and sets a condition on clicking the scene.
     */
    private GuiSelect(){
        selectedRelationships = new ArrayList<>();
        selectedGUIClasses = new ArrayList<>();
        Main.currentScene.setOnMouseClicked(e -> {
            GuiSelect.getInstance().resetSelect(e);
            e.consume();
        });
    }

    /**
     * getInstance method. Used for singleton
     *
     * @return GuiSelect - Instance of GuiSelect
     */
    public static synchronized GuiSelect getInstance()
    {
        if(instance == null)
        {
            instance = new GuiSelect();
        }
        return instance;
    }

    /**
     * testClassSelect method. Selects or deselects a passed in class.
     *
     * @param e - Event checked for ctrl being held
     * @param GUIClassIn - Class being selected/deselected
     */
    public void classSelect(MouseEvent e, GuiClass GUIClassIn) {
        //Checks if control is being held. If not, returns.
        if (!e.isControlDown()) {
            return;
        }
        //Checks if class is already selected. If so, deselects it.
        if(selectedGUIClasses.contains(GUIClassIn)){
            GUIClassIn.parentVBox.setBackground(new Background(new BackgroundFill(Color.MINTCREAM, null, null)));
            selectedGUIClasses.remove(GUIClassIn);
            return;
        }

        //Selects class if not already selected.
        selectedGUIClasses.add(GUIClassIn);
        //Sets to selected color.
        GUIClassIn.parentVBox.setBackground(new Background(new BackgroundFill(GuiColor.SELECTION_COLOR, null, null)));

        //Testing
        System.out.println(selectedGUIClasses);
        System.out.println(selectedRelationships);
        System.out.println("Test Passed");

    }

    /**
     * Unimplemented selection method for relationships.
     *
     * @param e
     * @param relationshipIn
     */
    public void relationshipSelect(MouseEvent e, UMLRelationship relationshipIn) {
        if (!e.isControlDown()) {
            return;
        }
        selectedRelationships.add(relationshipIn);
        System.out.println(selectedGUIClasses);
        System.out.println(selectedRelationships);
        System.out.println("Test Passed");

    }

    /**
     * resetSelect method. Checks if ctrl is being held when the scene is clicked. If so, clears the selections of
     * classes and relationships, and sets colours back to default.
     *
     * @param e - Event to check for ctrl.
     */
    public void resetSelect(MouseEvent e){
        if(!e.isControlDown()){
            for(GuiClass gClass : selectedGUIClasses){
                gClass.parentVBox.setBackground(new Background(new BackgroundFill(Color.MINTCREAM, null, null)));
            }
            selectedRelationships.clear();
            selectedGUIClasses.clear();
        }

    }

    /**
     * deleteAllSelected method. Checks if classes are selected. If so, deletes all selected classes and relationships.
     */
    public void deleteAllSelected(){

        //Checks if any items are selected
        if(selectedGUIClasses.isEmpty() && selectedRelationships.isEmpty()){
            System.out.println("No items selected");
            return;
        }

        //Displays confirmation box for deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to delete " + (selectedGUIClasses.size()
                + selectedRelationships.size()) + " items?");
        Optional choice = alert.showAndWait();

        if(choice.get() == ButtonType.CANCEL){
            return;
        }

        //Deletes all classes.
        UMLClass errorClass;
        for(GuiClass GUIClassIn : selectedGUIClasses){
            errorClass = UMLDocument.getInstance().removeClass(GUIClassIn.getParentClass().getClassName());
            if(errorClass == null){
                System.out.println("An error occurred removing class " + GUIClassIn.getParentClass().getClassName());
            }
        }

        //Deletes all relationships
        boolean errorRelationship;
        for(UMLRelationship URelationship : selectedRelationships){
            errorRelationship = UMLDocument.getInstance().removeRelationship(URelationship.getSourceName(), URelationship.getDestinationName());
            if(!errorRelationship){
                System.out.println("An error occurred removing relationship " + URelationship);
            }
        }

        selectedRelationships.clear();
        selectedGUIClasses.clear();

    }


}
