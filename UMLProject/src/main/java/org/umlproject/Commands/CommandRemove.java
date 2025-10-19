package org.umlproject.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.umlproject.*;

/** CommandRemove class is responsible for parsing the user input to determine which attributes of a UMLClassDocument
 * need to be removed.*/
public class CommandRemove extends BaseCommand{
    @Override public String actionName(){
        return "remove";
    }

    @Override
    public void act(String[] args)
    {
        if(args == null || args.length == 0)
        {
            System.out.println(description());
            return;
        }
        switch (args[0])
        {
            //Removes a class by name.
            case "class" -> {
                if(args.length == 2) {
                   boolean wasClassRemoved = (UMLDocument.getInstance().removeClass(args[1]) != null);
                    if(wasClassRemoved == false) {
                        System.out.println("ACTION BLOCKED! Class does not exist!");
                    }
                    else{
                        System.out.println(String.format("remove class | The class '%s' has been removed!",args[1]));
                    }
                }
                else {
                    System.out.println("""
                                        remove class | Please specify a classname
                                                           EX: remove class yourClassNameHere""");
                }
            }

            //Relationship case has two functionalites: either <source><destination> are specified, in which case a
            //specific relationship matching that description is removed, or else only <source> is provided and the user
            //can select a relationship to remove from a list.
            case "relationship" -> {
                //Specific relationship
                if (args.length == 3){
                    boolean removeSuccessful = UMLDocument.getInstance().removeRelationship(args[1], args[2]);
                    if(removeSuccessful){
                        System.out.println(String.format("remove relationship | Relationship between %s and %s has been removed!", args[1], args[2]));
                    }
                    else{
                        System.out.println("ACTION Blocked! Relationship does not exist or is invalid");
                    }
                }
                //Case when user wishes to pick a relationship from a list to remove.
                else if(args.length == 2){

                    //Make sure class exists.
                    UMLClass umlClass = UMLDocument.getInstance().getClass(args[1]);
                    if(umlClass == null){
                        System.out.println("Class name does not exist!");
                        return;
                    }

                    //Class exists, so now retrieve its relationships if they exist. If not print message and return.
                    ArrayList<UMLRelationship> relationshipList = UMLDocument.getInstance().getAllRelationships(args[1]);
                    if (relationshipList == null || relationshipList.isEmpty()) {
                        System.out.println("The specified class has no relationships to remove!");
                        return;
                    }

                    //Relationships exist, so now convert into an array and pass into promptUserSelectionIndex()
                    //to allow the user to select a relationship to remove from a list. Note, if only one relationship
                    //exists in the list, it will be removed without user selecting to do so.
                    UMLRelationship[] relationshipArray = relationshipList.toArray(UMLRelationship[]::new);
                    if(relationshipArray.length > 1) {
                        System.out.println("Please select which relationship you'd like to remove:");
                    }
                    int selectedRelationship = promptUserSelectionIndex(relationshipArray);

                    //If selectedRelationship is an index value less than 0, then the user either
                    //exited the menu without selecting something to remove, the passed array was empty.
                    if(selectedRelationship < 0){
                        System.out.println("Invalid index from relationship list");
                        return;
                    }

                    //Remove relationship
                    relationshipList.remove(selectedRelationship);
                    System.out.println("Relationship was removed!");
                    return;
                }
                else{
                    System.out.println("Invalid input!");
                    System.out.println("""
                                remove relationship | Please specify two extra arguments to remove a single relationship.
                                Ex: remove relationship class1 class2
                                Or specify a single extra argument to select a relationship from a list.
                                Ex: remove relationship class1""");

                }
            }

            //User can input a specific method to remove by specifying a list of parameters. If only the method
            //name is provided, then user will be given a choice from a list.
            case "method" -> {
                if (args.length <= 2) {
                    System.out.println("""
                            add method | Please specify a class and methhod name
                                               EX: remove method classname methodname
                                               EX: remove method classname methodname type param1Name type param2Name ... so on ... type param10Name""");
                }
                //Case where methods are listed and user specifies index to remove.
                else if(args.length == 3){

                    //Retrieve methodList and make sure it isn't null or empty.
                    UMLClass umlClass = UMLDocument.getInstance().getClass(args[1]);
                    if (umlClass == null) {
                        System.out.println("ERROR! Class does not exist!");
                        return;
                    }
                    ArrayList<UMLMethod> methodList = umlClass.getMethods(args[2]);
                    if(methodList == null || methodList.isEmpty()){
                        System.out.println("No method by that name exists!");
                        return;
                    }

                    //Convert methodList to an array to be passed to promptUserSelectionIndex()
                    UMLMethod[] methodArray = methodList.toArray(UMLMethod[]::new);

                    //When there is only one method in overloaded list
                    if(methodArray.length == 1){

                        umlClass.removeMethod(args[2], 0);
                        System.out.println(String.format("The method %s has been removed!", args[2]));
                        return;
                    }

                    //When selecting from list with more than 1 element
                    if(methodArray.length >= 1) System.out.println("Please select which instance you need to edit:");

                    int selectedMethod = promptUserSelectionIndex(methodArray);
                    if(selectedMethod < 0){
                        System.out.println("invalid index from method list.");
                        return;//User changes their mind.
                    }

                    //Remove method
                    else {
                        umlClass.removeMethod(args[2], selectedMethod);
                        System.out.println(String.format("The selected overloaded method %s has been removed.", args[2]));
                        return;
                    }

                }
                //Case where user supplies the method name and a list of parameters.
                else {
                    String className = args[1];
                    UMLClass umlClass = UMLDocument.getInstance().getClass(className);
                    if (umlClass == null) {
                        System.out.println("ERROR! Class does not exist!");
                        return;
                    }
                    String methodName = args[2];
                    UMLMethod method = new UMLMethod();
                    method.setMethodName(methodName);

                    //Arrays.asList(args) converts the argument array into a List which is a kind of Collection.
                    //The array list constructor requires a Collection data structure.

                    List<String> remainingArgsList = new ArrayList<>(Arrays.asList(args));
                    remainingArgsList.remove(2);//"methodname"
                    remainingArgsList.remove(1);//"classname"
                    remainingArgsList.remove(0);//"method"

                    //Generate the parameter list for the UMLMethod object.
                    ArrayList<UMLParameter> paramList = new ArrayList<>();
                    if (!remainingArgsList.isEmpty()) {
                        //There are an equal pair of "type, param1"
                        if (remainingArgsList.size() % 2 == 0){
                            for (int i = 0; i < remainingArgsList.size(); i += 2) {
                                String dataTypeString = remainingArgsList.get(i);
                                DataType dataType = DataType.stringToDatatype(dataTypeString);
                                String paramName = remainingArgsList.get(i + 1);

                                UMLParameter iParam = new UMLParameter(paramName, dataType, (dataType == DataType.OTHER) ? dataTypeString : null);
                                paramList.add(iParam);
                            }
                        }
                        else {
                            System.out.println("ERROR! Please ensure the command is of the form: \n remove method classname methodname type param1Name type param2Name ... so on ... type param10Name");
                            return;
                        }
                    }
                    method.setListParameters(paramList);

                    //Compare newly constructed method with the overloaded list of umlClass. If a match is found, return its
                    //index within the list and call removeMethod() from the UMLClass.
                    ArrayList<UMLMethod> umlClassMethods = umlClass.getMethods(method.getMethodName());
                    if (umlClassMethods == null) {
                        System.out.println(String.format("There is no method matching your specifications that can be removed from the class %s", args[1]));
                        return;
                    }
                    int methodIndex;
                    for (int i = 0; i < umlClassMethods.size(); i++) {
                        if (method.equals(umlClassMethods.get(i))) {
                            methodIndex = i;
                            umlClass.removeMethod(methodName, i);
                            System.out.println(String.format("remove method | removed method %s from class %s!", methodName, umlClass.getClassName()));
                            return;
                        }
                    }


                    System.out.println("Method does not exist within class or input is invalid");
                    return;
                }
            }

            // All fields are uniquely named inside a class. If the field exists and the class exists, the user
            //can remove the field from the class using the removeField() method.
            case "field" -> {
                //Needs exactly three arguments: field className fieldName
                if(args.length != 3){
                    System.out.println("Invalid inputs.");
                    System.out.println("""
                                          remove field | Please input a class name and a field name to remove a field.
                                                         Ex: remove field className fieldName.""");
                    return;
                }
                else {
                    //Make sure class exists.
                    UMLClass umlClass = UMLDocument.getInstance().getClass(args[1]);
                    if(umlClass == null) {
                        System.out.println("remove field | The given class name doesn't exist.");
                    }
                    else {
                        //Class exists so remove field
                        String fieldName = args[2];
                        boolean successfulRemoval = umlClass.removeField(fieldName);
                        if(successfulRemoval)
                            System.out.println(String.format("remove field | The field %s has been removed from the class '%s'!", fieldName, args[1]));
                        else
                            System.out.println("remove field | The field does not exist.");
                    }
                }
                return;
            }

            //User will remove a parameter from an overloaded method selected from a list.
            case "param" -> {
                //Need 5 arguments exactly to remove a parameter.
                if(args.length != 5)
                {
                    System.out.println("""
                        remove param | Please specify a class name, method name, type and param name.
                                           EX: remove param classname methodname type paramname""");
                    return;
                }
                String className = args[1];
                String methodName = args[2];
                String dataTypeString = args[3];
                DataType dataType = DataType.stringToDatatype(dataTypeString);
                String paramName = args[4];

                //Ensure that the class containing the desired method exists
                UMLClass umlClass = UMLDocument.getInstance().getClass(className);
                if(umlClass == null)
                {
                    System.out.println(String.format("remove param | No class with the name %s was found",className));
                    return;
                }

                //Check if methods corresponding to the given method name argument exist before
                //prompting user with a selection to remove.
                ArrayList<UMLMethod> methodList = umlClass.getMethodsAll().get(methodName);
                UMLMethod[] methodArray = methodList.toArray(UMLMethod[]::new);
                if(methodArray == null || methodArray.length == 0)
                {
                    System.out.println("No method exists!");
                    return;
                }

                //If the methodList has more than one entry, then we need to offer the user a choice. Print a numbered
                //list of method options to the screen and ask the user to enter the number in the list corresponding to
                //the appropriate method. Once the appropriate method is selected, remove the parameter.
                if(methodArray.length != 1) System.out.println("Please select which instance you need to edit:");
                UMLMethod selectedMethod = promptUserSelectionFromList(methodArray);
                if(selectedMethod == null) return;//User changes their mind.
                UMLParameter param = new UMLParameter(paramName, dataType, (dataType == DataType.OTHER)? dataTypeString : null);


                //Remove parameter and print according to success.
                boolean isSuccessful = selectedMethod.removeParameter(param);
                if(isSuccessful){
                    System.out.println("Parameter successfully removed!");
                }
                else{
                    System.out.println("Parameter does not exist within method!");
                }

            }
            default -> {
                System.out.println(description());
            }
        }
    }

    /**
     * description method for the help command.
     * @return returns a string representing the acceptable inputs for the remove command.
     */
    @Override
    public String description()
    {
        return """
               Possible parameters for remove:
                        class <class name> : removes a class with a given class name
                        relationship <source> <destination> : removes a relationship between two classes
                        relationship <source> : lets the user select a relationship to remove from a list.
                        method <target class> <method name> <type1> <name1> ... <type10> <name10>: removes a method from the target class
                        method <target class> <method name>: lets user select a method to remove from a list.
                        field <target class> <data Field Name> : removes a field from the target class
                        param <target class> <method name> <type> <param name> : removes a parameter from the method of the specified class.
                        param <target class> <method name>: lets user remove a parameter from a method selected from a list""";
    }
}



