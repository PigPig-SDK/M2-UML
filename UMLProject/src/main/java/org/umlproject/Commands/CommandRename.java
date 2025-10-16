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

        if(args == null || (args.length != 3 && args.length != 4))
        {
            System.out.println(description());
            return;
        }

        String choice = args[0];

        TerminalHandler.printLineBreak();

        String className = args[1];
        UMLClass umlclass = UMLDocument.getInstance().getClass(className);
        if (umlclass == null) {
            System.out.println("Class " + className+ " doesnt exist\n" + description());
            return;
        }

        switch(choice){

            case "class" -> {
                if(args.length != 3){
                    System.out.println("Incorrect amount of arguments\n" + description());
                    return;
                }

                String newClassName = args[2];


                boolean classRenamed = UMLDocument.getInstance().renameClass(className, newClassName);
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

                String oldMethodName = args[2];
                String newMethodName = args[3];

                ArrayList<UMLMethod> OldMethodOverloaded = umlclass.getMethods(oldMethodName);

                //get proper oldMethod
                UMLMethod index =
                        promptUserSelectionFromList(OldMethodOverloaded.toArray(UMLMethod[]::new));

                //remove old method, add new one
                boolean methodRemoved = OldMethodOverloaded.remove(index);
                if(OldMethodOverloaded.isEmpty()){
                    umlclass.getMethodsAll().remove(oldMethodName);
                }
                index.setMethodName(newMethodName);
                boolean methodAdded = umlclass.addMethod(index);
                if(methodRemoved && methodAdded){
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

                String oldFieldName = args[2];
                String newFieldName = args[3];


                boolean renamedField = umlclass.renameField(oldFieldName, newFieldName);
                if (renamedField) {
                    System.out.println("Field successfully renamed");
                } else {
                    System.out.println("Field rename failed");
                }

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