package org.umlproject.Commands;

import org.umlproject.UMLDocument;


public class CommandLoad extends BaseCommand{

    @Override
    public String actionName() {
        return "load";
    }

    @Override
    public void act(String[] args) {
        UMLDocument.getInstance().load();
        System.out.println("Document loaded.");
    }

    @Override
    public String description() {
        return "Loads a saved UMLDocument.";
    }
    
}
