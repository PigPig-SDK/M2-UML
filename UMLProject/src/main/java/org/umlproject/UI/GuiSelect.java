package org.umlproject.UI;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import org.umlproject.Main;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;
import org.umlproject.UMLRelationship;

import java.util.ArrayList;
import java.util.Optional;
import javafx.animation.AnimationTimer;
import org.umlproject.App;
import org.umlproject.DiagramElementListener;
import org.umlproject.DocumentState;
import org.umlproject.Memento;
import org.umlproject.MementoListener;
import org.umlproject.MementoUpdateType;

public class GuiSelect implements MementoListener<UMLDocument>
{

    private static GuiSelect instance;

    private static ArrayList<UISelectable> selectedObjects;
    private static UISelectable latestSelection = null;

    /**
     * Singleton GuiSelect constructor. Creates three relevant ArrayLists and sets a condition on clicking the scene.
     */
    private GuiSelect(){
        selectedObjects = new ArrayList<>();
        AnimationTimer selectionAnimationTimer = new AnimationTimer() {
            @Override
            public void handle(long now){
                for(UISelectable selectable : selectedObjects)
                {
                    selectable.selectionAnimationUpdate(now);
                }
            }
        };
        selectionAnimationTimer.start();//Runs forever...
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
     * @param selectable - Class being selected/deselected
     */
    public void clickUiElement(MouseEvent e, UISelectable selectable, boolean isDragging) {
        
        //Mouse UP  
        if(e.getEventType() == MouseEvent.MOUSE_CLICKED)
        {
            if(!isDragging)
            {
                if(e.isControlDown())
                {
                    if(latestSelection != selectable)
                    {
                        deselectUiElement(selectable);
                        selectedObjects.remove(selectable);
                    }
                }
                else
                {
                    resetSelect();
                    selectUiElement(selectable);
                }
            }
            latestSelection = null;
        }
        else//Mouse down
        {
            if(!selectedObjects.contains(selectable))
            {
                if(e.isControlDown())
                {
                    latestSelection = selectable;
                    selectUiElement(selectable);
                }
                else
                {
                    resetSelect();
                    selectUiElement(selectable);
                }
            } 
        }
    }
    private void deselectUiElement(UISelectable selectable)
    {
        selectedObjects.remove(selectable);
        selectable.setSelected(false);
    }
    public void selectUiElement(UISelectable selectable)
    {
        selectable.setSelected(true);
        selectedObjects.add(selectable);
    }
    /**
     * resetSelect method. Checks if ctrl is being held when the scene is clicked. If so, clears the selections of
     * classes and relationships, and sets colours back to default.
     *
     * @param e - Event to check for ctrl.
     */
    private void checkResetSelect(MouseEvent e){
        if(!e.isControlDown()){
            resetSelect();
        }
    }
    /**
     * Deselects all elements
     */
    public void resetSelect()
    {
        for(UISelectable selectable : selectedObjects){
                selectable.setSelected(false);
        }
        selectedObjects.clear();
    }
    /**
     * deleteAllSelected method. Checks if classes are selected. If so, deletes all selected classes and relationships.
     */
    public void deleteAllSelected(){

        UMLDocument.executeActionUnderState(DocumentState.MASS_OPERATION,()->{
            //Checks if any items are selected
            if(selectedObjects.isEmpty()){
                return;
            }
            //Displays confirmation box for deletion
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText(String.format("Are you sure you want to delete %d items?", selectedObjects.size()));
            Optional choice = alert.showAndWait();

            if(choice.get() == ButtonType.CANCEL){
                return;
            }
            //Deletes all classes.
            UMLClass errorClass;
            for(UISelectable selectable : selectedObjects){

                if(selectable instanceof GuiClass guiClass)//Manage deletion of classes
                {
                    String className = guiClass.getParentClass().getClassName();
                    errorClass = UMLDocument.getInstance().removeClass(className);
                    if(errorClass == null)
                        System.out.println("An error occurred removing class ");
                }
                else if (selectable instanceof GuiRelationship guiRelationship)//Handle deletion of relationships
                {
                    UMLRelationship relationship = guiRelationship.getRelationship();
                    if(relationship == null)//This is a 100% valid possibility. Classes removes relationship before we do.
                        continue;
                    UMLDocument.getInstance().removeRelationship(relationship.getSourceName(), relationship.getDestinationName());
                }
            }
            selectedObjects.clear();
        });
        UMLDocument.saveMementoState();
    }
    /**
     * Selects all elements
     */
    public void selectAll()
    {
        resetSelect();
        for(DiagramElementListener listner : UMLDocument.getInstance().getUIListeners())
        {
            if(listner instanceof UISelectable selectable)
            {
                selectable.setSelected(true);
                selectedObjects.add(selectable);
            }
        }
    }

    public ArrayList<UISelectable> getSelectedObjects(){
        return selectedObjects;
    }

    @Override
    public void update(Memento<UMLDocument> memento,MementoUpdateType type) {
        if(type == MementoUpdateType.SAVESTATE) return;
        resetSelect();
    }
}
