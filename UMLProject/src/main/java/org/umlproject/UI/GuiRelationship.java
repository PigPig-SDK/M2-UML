package org.umlproject.UI;

import javafx.scene.Group;
import org.umlproject.UIListener;
import org.umlproject.UMLDiagramElement;
import org.umlproject.UMLRelationship;

public class GuiRelationship implements UIListener{

    public GuiRelationship(Group world, UMLRelationship umlRelationship)
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
    
}
