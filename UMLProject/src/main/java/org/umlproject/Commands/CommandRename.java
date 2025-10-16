package org.umlproject.Commands;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.umlproject.*;

import java.util.ArrayList;

public class CommandRename extends BaseCommand{

    @Override
    public String actionName() {
        return "rename";
    }

    @Override
    public void act(String[] args) {

        if(args == null || args.length != 3)
        {
            System.out.println(description());
            return;
        }

        String choice = args[0];

        TerminalHandler.printLineBreak();
        switch(choice){

            case "class" -> {
                if (args.length == 3) {
                    String oldClassName = args[1];
                    String newClassName = args[2];
                    UMLClass umlclass = UMLDocument.getInstance().getClass(oldClassName);

                    if (umlclass == null) {
                        System.out.println("Class " + oldClassName+ " doesnt exist");
                        return;
                    } else {
                        System.out.println("Class already renamed");
                    }

                    boolean classRenamed = UMLDocument.getInstance().renameClass(oldClassName, newClassName);
                    if (classRenamed) {
                        System.out.println("Class successfully renamed");
                    }

                }
            }

            case "method" -> {
                if (args.length == 4) {

                    String className = args[1];
                    String oldMethodName = args[2];
                    String newMethodName = args[3];
                    UMLClass umlclass = UMLDocument.getInstance().getClass(className); // search if the class exists
                    if (umlclass == null) {
                        System.out.println("Class " + className+ " doesnt exist");
                    }

                    boolean methodRenamed = umlclass.renameMethod(oldMethodName, newMethodName, promptUserSelectionFromList() );
                }

            }

            case "field" -> {
                if (args.length == 4) {
                    String className = args[1];
                    String oldFieldName = args[2];
                    String newFieldName = args[3];

                    UMLClass umlclass = UMLDocument.getInstance().getClass(className);

                    if (umlclass == null) {
                        System.out.println("Class " + className + " doesnt exist");
                        return;
                    }

                    boolean renamedField = umlclass.renameField(oldFieldName, newFieldName);
                    if (renamedField) {
                        System.out.println("Field successfully renamed");
                    } else {
                        System.out.println("");
                    }

                }

            }
        }
    }

    @Override
    public String description() {
        return """
               Possible parameters for rename:
                        rename class <classname> <newname> : Renames a class
                        rename method <classname> <methodname> : Renames a method, chosen from a list for overloaded methods
                        rename field <classname> <fieldname> : Renames a field""";
    }

}