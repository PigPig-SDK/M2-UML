package org.umlproject.UI;

import javafx.scene.Group;
import org.umlproject.UIListener;
import org.umlproject.UMLClass;
import org.umlproject.UMLDiagramElement;

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
    
    
}
