package org.umlproject.Commands;

import org.umlproject.UMLDocument;

public class CommandSave extends BaseCommand {

    @Override
    public String actionName() {
        return "save";
    }

    @Override
    public void act(String[] args) {
        UMLDocument.getInstance().save();
        System.out.println("Document saved.");
    }

    @Override
    public String description() {
        return "Saves the opened UML document";
    }
    
}
