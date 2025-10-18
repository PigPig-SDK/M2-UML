package org.umlproject.Commands;

import org.umlproject.*;

import java.util.ArrayList;

public class CommandRename extends BaseCommand{

    @Override
    public String actionName() {
        return "rename";
    }

    @Override
    public void act(String[] args) {

        if(args == null || args.length == 0)
        {
            System.out.println(description());
            return;
        }

        String choice = args[0];

        TerminalHandler.printLineBreak();

        switch(choice){

            case "class" -> {
                if(args.length != 3){
                    System.out.println("Incorrect amount of arguments\n" + description());
                    return;
                }

                String oldClassName = args[1];
                UMLClass umlclass = UMLDocument.getInstance().getClass(oldClassName);
                if (umlclass == null) {
                    System.out.println("Class " + oldClassName+ " doesnt exist\n" + description());
                    return;
                }

                String newClassName = args[2];


                boolean classRenamed = UMLDocument.getInstance().renameClass(oldClassName, newClassName);
                if (classRenamed) {
                    System.out.println("Class successfully renamed");
                } else{
                    System.out.println("Class rename failed");
                }

            }

            case "method" -> {
                if(args.length != 4){
                    System.out.println("Incorrect amount of arguments\n" + description());
                    return;
                }

                String className = args[1];
                UMLClass umlclass = UMLDocument.getInstance().getClass(className);
                if (umlclass == null) {
                    System.out.println("Class " + className+ " doesnt exist\n" + description());
                    return;
                }

                String oldMethodName = args[2];
                ArrayList<UMLMethod> oldMethodOverloaded = umlclass.getMethods(oldMethodName);
                if(oldMethodOverloaded == null){
                    System.out.println("Method " + oldMethodName+ " doesnt exist\n" + description());
                    return;
                }

                String newMethodName = args[3];
                //get proper oldMethod
                UMLMethod index =
                        promptUserSelectionFromList(oldMethodOverloaded.toArray(UMLMethod[]::new));
                if(index == null){
                    return;
                }
                //call method rename
                boolean methodRenamed =
                        umlclass.renameMethod(index.getMethodName(), newMethodName, index.getParameters());

                if(methodRenamed){
                    System.out.println("Method successfully renamed");
                } else{
                    System.out.println("Method rename failed");
                }

            }

            case "field" -> {
                if(args.length != 4){
                    System.out.println("Incorrect amount of arguments\n" + description());
                    return;
                }

                String className = args[1];
                UMLClass umlclass = UMLDocument.getInstance().getClass(className);
                if (umlclass == null) {
                    System.out.println("Class " + className+ " doesnt exist\n" + description());
                    return;
                }

                String oldFieldName = args[2];
                String newFieldName = args[3];

                boolean renamedField = umlclass.renameField(oldFieldName, newFieldName);
                if (renamedField) {
                    System.out.println("Field successfully renamed");
                } else {
                    System.out.println("Field rename failed");
                }

            }

            default -> {
                System.out.println(description());
            }
        }
    }

    @Override
    public String description() {
        return """
               Possible parameters for rename:
                        rename class <class name> <new name> : Renames a class
                        rename method <class name> <old method name> <new method name>: Renames a method, chosen from a list for overloaded methods
                        rename field <class name> <old field name> <new field name>: Renames a field""";
    }

}