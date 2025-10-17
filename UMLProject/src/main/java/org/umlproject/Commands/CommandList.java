package org.umlproject.Commands;

import org.umlproject.*;
import scala.Array;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;


public class CommandList extends BaseCommand{

    @Override
    public String actionName() {
        return "list";
    }

    @Override
    public void act(String[] args) {

        if(args == null || args.length != 1)
        {
            System.out.println(description());
            return;
        }

        String choice = args[0];

        TerminalHandler.printLineBreak();
        switch(choice){

            case "classes" -> {
                for(UMLClass currentClass : UMLDocument.getInstance().getClassSet().values()){
                    //Print class name
                    System.out.println("Class name: " + currentClass.getClassName());
                    //Print class fields
                    System.out.println("Fields: ");
                    for(UMLDataField currentField : currentClass.getFieldsAll().values()){
                        System.out.println("    " + currentField);
                    }
                    //Print class methods
                    System.out.println("Methods: ");
                    for(ArrayList<UMLMethod> currentMethod : currentClass.getMethodsAll().values()){
                        System.out.println("    " + currentMethod);
                    }
                    //Print class relationships
                    System.out.println("Relationships: ");
                    for(UMLRelationship currentRelationship : UMLDocument.getInstance().getRelationshipList()
                            .get(currentClass.getClassName())){
                        System.out.println("    " + currentRelationship);
                    }
                    System.out.println();
                }
            }

            case "relationships" -> {
                //Prints all relationships
                for(String relationshipSource : UMLDocument.getInstance().getRelationshipList().keySet()){
                    System.out.println(relationshipSource + "'s relationships: ");
                    for(UMLRelationship relationshipDestination :
                            UMLDocument.getInstance().getRelationshipList().get(relationshipSource)){
                        System.out.println("    " + relationshipDestination);
                    }

                }
            }

            default -> {

                UMLClass tempClass = UMLDocument.getInstance().getClass(choice);
                if(tempClass == null){
                    System.out.println("Class: " + choice + " does not exist");
                    System.out.println(description());
                    return;
                }

                //Print class name
                System.out.println("Class name: " + choice + "\nFields: ");
                //Print class fields
                for(UMLDataField currentField : tempClass.getFieldsAll().values()){
                    System.out.println("    " + currentField);
                }
                //Print class methods
                System.out.println("Methods: ");
                for(ArrayList<UMLMethod> currentMethod : tempClass.getMethodsAll().values()){
                    System.out.println("    " + currentMethod);
                }
                //Print class relationships
                System.out.println("Relationships: ");
                for(UMLRelationship currentRelationship : UMLDocument.getInstance().getRelationshipList()
                        .get(tempClass.getClassName())){
                    System.out.println("    " + currentRelationship);
                }
            }

        }
    }



    @Override
    public String description() {
        return """
               This commands lists information of a single valid class, all classes, or all relationships.
               Possible parameters for list:
                        list <class name> : Lists information of the class provided
                        list relationships : Lists information of all saved relationships
                        list classes : Lists information of all saved classes""";
    }

}
