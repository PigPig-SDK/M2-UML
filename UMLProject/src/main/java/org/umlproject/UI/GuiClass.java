package org.umlproject.UI;

import javafx.scene.Group;
import org.umlproject.UIListener;
import org.umlproject.UMLClass;

public class GuiClass implements UIListener<UMLClass> {
    
    public GuiClass(Group world, UMLClass parentClass)
    {
        //Bind our UI elements to 'world'
    }
    
    @Override
    public void update(UMLClass desiredElement) {
    }

    @Override
    public void updateSelected(UMLClass desiredElement) {
    }

    @Override
    public void updateLocation(UMLClass desiredElement) {
    }

    @Override
    public void cleanUp() {
    }
    
    
}
