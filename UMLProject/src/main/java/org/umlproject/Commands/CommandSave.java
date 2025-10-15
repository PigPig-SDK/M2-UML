package org.umlproject.Commands;

import org.umlproject.UMLDocument;

public class CommandSave extends BaseCommand {

    @Override
    public String actionName() {
        return "save";
    }

    @Override
    public void act(String[] args) {
        switch (args.length) {
            case 0 -> {
                UMLDocument.getInstance().save();
                System.out.println("Document saved.");
            }
            case 1 -> {
                UMLDocument.getInstance().save(args[0]);
                System.out.println("Document saved as " + args[0] + ".");
            }
            default -> System.out.println("Please provide a valid filename.");
        }
    }

    @Override
    public String description() {
        return "Saves the opened UML document";
    }
    
}
