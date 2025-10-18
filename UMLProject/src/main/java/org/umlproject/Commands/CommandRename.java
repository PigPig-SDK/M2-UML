package org.umlproject.Commands;

import org.umlproject.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

            case "param","params" -> {
                //Check correct argument length
                if(args.length <= 4){
                    System.out.println("Incorrect amount of arguments\n" + description());
                    return;
                }
                //Assign arguments
                String className = args[1];
                String methodName = args[2];
                UMLClass umlClass = UMLDocument.getInstance().getClass(className);
                if (umlClass == null) {
                    System.out.println("Class " + className+ " doesnt exist\n" + description());
                    return;
                }

                //Exclude the first 3 arguments, they were consumed earlier
                List<String> remainingArgsList = Arrays.asList(args).subList(3, args.length);
                ArrayList<UMLParameter> paramList = formatInputParameters(remainingArgsList);
                if(paramList == null || paramList.isEmpty()){
                    System.out.println("ERROR! Please ensure the command is of the form: \n rename parameter" +
                            " classname methodname type param1 type param2 ... so on ... type param10");
                    return;
                }

                ArrayList<UMLMethod> methodList = umlClass.getMethodsAll().get(methodName);
                UMLMethod[] methodArray = methodList.toArray(UMLMethod[]::new);
                if(methodArray == null || methodArray.length == 0)
                {
                    System.out.println("No method exists!");
                    return;
                }

                //Prompt user to select method
                if(methodArray.length != 1) System.out.println("Please select which method you need to edit:");
                UMLMethod selectedMethod = promptUserSelectionFromList(methodArray);
                if(selectedMethod == null) return;//User changes their mind.

                //Check that renamed method doesn't already exist
                for(UMLMethod checker: methodList){
                    if(checker.getParameters().equals(paramList)){
                        System.out.println("ERROR: Existing Method Already Exists!");
                        return;
                    }
                }

                //If user wants to replace only one parameter, prompt for selection and replace
                if(args[0].equals("param")){

                    ArrayList<UMLParameter> parameters = selectedMethod.getParameters();
                    UMLParameter[] parametersArray = parameters.toArray(UMLParameter[]::new);

                    if(parametersArray == null || parametersArray.length == 0)
                    {
                        System.out.println("No parameters exists!");
                        return;
                    }
                    if(parametersArray.length != 1) System.out.println("Please select which parameter you need to edit:");

                    UMLParameter selectedParameter = promptUserSelectionFromList(parametersArray);
                    if(selectedParameter == null) return;//User changes their mind.

                    selectedMethod.changeParameter(selectedParameter, paramList);
                    System.out.println("Parameter Change Success");
                    return;
                }

                //Else, replace all parameters in method
                System.out.println("Parameter Change Success");
                selectedMethod.setListParameters(paramList);
                return;

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
                        rename field <class name> <old field name> <new field name>: Renames a field
                        rename param <class name> <method name> <type1> <name1> ... <type10> <name10>: Replaces an existing parameter with a list of new parameters
                        rename params <class name> <method name> <type1> <name1> ... <type10> <name10>: Replaces all parameters with a list of new parameters""";
    }

}