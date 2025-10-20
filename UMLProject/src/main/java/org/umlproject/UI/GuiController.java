package org.umlproject.UI;

import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;
import org.umlproject.UMLGuiController;
import org.umlproject.UMLRelationship;

public class GuiController implements UMLGuiController {
    
    //----------------- UMLGuiController Interface -----------------
    @Override
    public void addClass(UMLClass umlClass) {
        GuiClass guiClass = new GuiClass(UMLClass umlClass);
        umlClass.setListener(guiClass);
        umlClass.updateGUI();
    }

    @Override
    public void addRelationship(UMLRelationship umlRelationship) {
        GuiRelationship guiRelationship = new GuiRelationship();
        umlRelationship.setListener(guiRelationship);
        umlRelationship.updateGUI();
    }

    @Override
    public void redrawScreen(UMLDocument umlDocument) {
        
    }
    
}
