package org.umlproject.Commands;

import org.umlproject.UMLDocument;


public class CommandUndo extends BaseCommand{

    @Override
    public String actionName() {
        return "undo";
    }

    @Override
    public void act(String[] args) {
        UMLDocument.undoMementoState();
    }

    @Override
    public String description() {
        return "Undoes the latest action.";
    }
    
}
