package org.umlproject.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import org.umlproject.DataType;
import org.umlproject.UMLClass;
import org.umlproject.UMLDataField;
import org.umlproject.UMLDocument;
import org.umlproject.UMLMethod;
import org.umlproject.UMLParameter;
import org.umlproject.Visibility;

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
            case "class" -> {
                if(args.length == 2) {
                   boolean wasClassRemoved = UMLDocument.getInstance().deleteClass(args[1]);
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
            //needs to cover the case of a specific relationship to be removed and also the case of removing All
            //relationships involving a class name.
            case "relationship" -> {
                //specific relationship
                if (args.length == 3){
                    boolean removeSuccessful = UMLDocument.getInstance().removeRelationship(args[1], args[2]);
                    if(removeSuccessful){
                        System.out.println(String.format("remove relationship | Relationship between %s and %s has been removed!", args[1], args[2]));
                    }
                    else{
                        System.out.println("ACTION Blocked! Relationship does not exist or is invalid");
                    }

                }
                //case when all relationships involving a given class name need to be removed
                if(args.length == 2){
                    boolean removeSuccessful = UMLDocument.getInstance().removeClassKeyFromRelationships(args[1]);
                    if(removeSuccessful){
                        System.out.println(String.format("remove relationship | All relationships involving %s have been removed!", args[1]));
                    }
                    else{
                        System.out.println("""
                                remove relationship | Please specify two extra arguments to remove a single relationship.
                                Ex: remove relationship class1 class2
                                Or specify a single extra argument to remove all relationships associated with a class name.
                                Ex: remove relationship class1""");
                    }
                }
            }
            case "method" -> {
                if (args.length <= 2) {
                    System.out.println("""
                            add method | Please specify a class and methhod name
                                               EX: remove method classname methodname
                                               EX: remove method classname methodname type param1Name type param2Name ... so on ... type param10Name""");
                } else {
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
                    System.out.println(remainingArgsList);
                    remainingArgsList.remove(2);//"methodname"
                    remainingArgsList.remove(1);//"classname"
                    remainingArgsList.remove(0);//"method"

                    //generate the parameter list for the UMLMethod object.

                    ArrayList<UMLParameter> paramList = formatInputParameters(remainingArgsList);
                    if(paramList == null){
                        System.out.println("ERROR! Please ensure the command is of the form: \n remove method classname methodname type param1Name type param2Name ... so on ... type param10Name");
                        return;
                    }


                    method.setListParameters(paramList);
                    //compare newly constructed method with the overloaded list of umlClass. If a match is found, return its
                    //index within the list and call removeMethod() from the UMLClass
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
            //Note that Cain's current specifications only require a unique field name to remove a field from a class.
            //It is likely this section will need to be rewritten in the future to require Visibility and DataType inputs from the
            //user to actually build a DataField object. Note that in the UMLClass file, the hashmap will need to be changed
            //to be <String dataFieldName, ArrayList<DataFields>, similarly the addField and removeField methods will need to
            //be updated. Also, the CommandAdd class will need to be updated in its section that deals with adding a new field.
            case "field" -> {
                if(args.length == 3)
                {
                    UMLClass umlClass = UMLDocument.getInstance().getClass(args[1]);
                    if(umlClass == null)
                    {
                        System.out.println("remove field | The given class name doesn't exist.");
                    }
                    else
                    {
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
            case "param" -> {
                if(args.length <= 4)
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


                UMLClass umlClass = UMLDocument.getInstance().getClass(className);
                if(umlClass == null)
                {
                    System.out.println(String.format("remove param | No class with the name %s was found",className));
                    return;
                }

                //if the methodList has more than one entry, then we need to offer the user a choice. Print a numbered
                //list of method options to the screen and ask the user to enter the number in the list corresponding to
                //the appropriate method. Once the appropriate method is selected, remove the parameter.
                ArrayList<UMLMethod> methodList = umlClass.getMethodsAll().get(methodName);
                UMLMethod[] methodArray = methodList.toArray(UMLMethod[]::new);
                if(methodArray == null || methodArray.length == 0)
                {
                    System.out.println("No method exists!");
                    return;
                }
                if(methodArray.length != 1) System.out.println("Please select which instance you need to edit:");

                UMLMethod selectedMethod = promptUserSelectionFromList(methodArray);
                if(selectedMethod == null) return;//User changes their mind.
                UMLParameter param = new UMLParameter(paramName, dataType, (dataType == DataType.OTHER)? dataTypeString : null);

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
    @Override
    public String description()
    {
        return """
               Possible parameters for remove:
                        class <class name> : removes a class with a given class name
                        relationship <source> <destination> : removes a relationship between two classes
                        relationship <source> : removes all relationships where given argument appears as the source name or destination name within the relationship.
                        method <target class> <method name> <type1> <name1> ... <type10> <name10>: removes a method from the target class
                        field <target class> <data Field Name> : removes a field from the target class
                        param <target class> <method name> <type> <param name> : removes a parameter from the method of the specified class""";
    }
}



