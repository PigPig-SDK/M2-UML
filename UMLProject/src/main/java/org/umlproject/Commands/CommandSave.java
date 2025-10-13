package org.umlproject.Commands;

public class CommandSave extends BaseCommand {

    @Override
    public String actionName() {
        return "save";
    }

    @Override
    public void act(String[] args) {
    }

    @Override
    public String description() {
        return "Saves the opened UML document";
    }
    
}
