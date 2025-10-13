package org.umlproject.Commands;

public class CommandList extends BaseCommand{

    @Override
    public String actionName() {
        return "list";
    }

    @Override
    public void act(String[] args) {
    }

    @Override
    public String description() {
        return "Something about listing the UMLDocument file here...";
    }
    
}
