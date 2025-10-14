package org.umlproject.Commands;


public class CommandLoad extends BaseCommand{

    @Override
    public String actionName() {
        return "load";
    }

    @Override
    public void act(String[] args) {
    }

    @Override
    public String description() {
        return "Loads a saved UMLDocument.";
    }
    
}
