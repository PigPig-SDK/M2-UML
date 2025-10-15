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
                boolean isLoadSuccessful = UMLDocument.getInstance().load();
                if(isLoadSuccessful)
                    System.out.println("Document loaded.");
                else
                    System.err.println("DOCUMENT FAILED TO LOAD! No default document exists!");
            }
            case 1 -> {
                String fileLocation = args[0];
                boolean isLoadSuccessful = UMLDocument.getInstance().load(fileLocation);
                if(isLoadSuccessful)
                    System.out.println("Document " + fileLocation + " loaded.");
                else
                {
                    System.err.println("Document " + fileLocation + " FAILED to load.");
                }
            }
            default -> System.out.println("Please provide a valid filename.");
        }
    }

    @Override
    public String description() {
        return "Loads a saved UMLDocument.";
    }
    
}
