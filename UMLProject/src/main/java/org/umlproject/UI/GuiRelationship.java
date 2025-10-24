package org.umlproject.UI;

import javafx.scene.Group;
import org.umlproject.UIListener;
import org.umlproject.UMLDiagramElement;
import org.umlproject.UMLRelationship;

public class GuiRelationship implements UIListener<UMLRelationship>{

    public GuiRelationship(Group world, UMLRelationship umlRelationship)
    {
        //Bind our UI elements to 'world'
    }
    
    @Override
    public void update(UMLRelationship desiredElement) {
    }

    @Override
    public void updateSelected(UMLRelationship desiredElement) {
    }

    @Override
    public void updateLocation(UMLRelationship desiredElement) {
    }

    @Override
    public void cleanUp() {
    }
    
}
