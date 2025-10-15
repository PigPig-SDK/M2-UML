package org.umlproject.Commands;

import org.umlproject.UMLDocument;


public class CommandLoad extends BaseCommand{

    @Override
    public String actionName() {
        return "load";
    }

    @Override
    public void act(String[] args) {
                switch (args.length) {
            case 0 -> {
                UMLDocument.getInstance().load();
                System.out.println("Document loaded.");
            }
            case 1 -> {
                UMLDocument.getInstance().load(args[0]);
                System.out.println("Document " + args[0] + " loaded.");
            }
            default -> System.out.println("Please provide a valid filename.");
        }
    }

    @Override
    public String description() {
        return "Loads a saved UMLDocument.";
    }
    
}
