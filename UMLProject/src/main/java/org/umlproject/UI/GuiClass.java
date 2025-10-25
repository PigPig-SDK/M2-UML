package org.umlproject.UI;

import javafx.scene.Group;
import javafx.scene.control.Alert;
import org.umlproject.UIListener;
import org.umlproject.UMLClass;
import org.umlproject.UMLDiagramElement;
import org.umlproject.UMLDocument;

public class GuiClass implements UIListener {

    
    public GuiClass(Group world, UMLClass parentClass)
    {
        //Bind our UI elements to 'world'
    }
    
    @Override
    public void update(UMLDiagramElement desiredElement) {
    }

    @Override
    public void updateSelected(UMLDiagramElement desiredElement) {
    }

    @Override
    public void updateLocation(UMLDiagramElement desiredElement) {
    }

    @Override
    public void cleanUp() {
    }

    /**
     * Runs after user input, adding a dummy class of format NewClass x
     */
    public void updateAdd(){
        UMLDocument doc = UMLDocument.getInstance();
        UMLClass checkClass = doc.addClass(doc.findValidDummyName());
        if(checkClass == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Dummy Creation Error");
            alert.showAndWait();
        }
    }

    /**
     * Runs after user input, removing the chosen class from the model
     *
     * @param input - Input class to be removed from model
     */
    public void updateRemove(UMLClass input){
        UMLClass checkClass = UMLDocument.getInstance().removeClass(input.getClassName());
        if(checkClass == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unable To Remove Class Error");
            alert.showAndWait();
        }
    }

    /**
     * Runs after user input, renaming the chosen class from the model
     *
     * @param input - Input class to be renamed from model
     */
    public void updateRename(UMLClass input, String newName){
        boolean checkClass = UMLDocument.getInstance().renameClass(input.getClassName(), newName);
        if(!checkClass){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unable To Rename Class Error");
            alert.showAndWait();
        }
    }


    
    
}
