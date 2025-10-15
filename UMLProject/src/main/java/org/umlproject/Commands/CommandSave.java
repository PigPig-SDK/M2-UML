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
                boolean saveCompleted = UMLDocument.getInstance().save();
                if(saveCompleted)
                    System.out.println(String.format("Document saved to %s",UMLDocument.getInstance().getFileLocation()));
                else
                    System.out.println("Document failed to save!");

            }
            case 1 -> {
                String fileLocation = args[0];
                boolean saveCompleted = UMLDocument.getInstance().save(fileLocation);
                if(saveCompleted)
                {
                    UMLDocument.getInstance().setFileLocation(fileLocation);
                    System.out.println(String.format("Document saved to %s",fileLocation));
                }
                else
                    System.out.println("Document failed to save!");
            }
            default -> System.out.println("Please provide a valid filename.");
        }
    }

    @Override
    public String description() {
        return "Saves the opened UML document";
    }
    
}
