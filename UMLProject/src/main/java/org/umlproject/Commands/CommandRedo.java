package org.umlproject.Commands;

import org.umlproject.UMLDocument;


public class CommandRedo extends BaseCommand{

    @Override
    public String actionName() {
        return "redo";
    }

    @Override
    public void act(String[] args) {
        UMLDocument.redoMementoState();
    }

    @Override
    public String description() {
        return "Undoes the previous undos.";
    }
    
}
