package org.umlproject.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.umlproject.DataType;
import org.umlproject.UMLClass;
import org.umlproject.UMLDataField;
import org.umlproject.UMLDocument;
import org.umlproject.UMLMethod;
import org.umlproject.UMLParameter;
import org.umlproject.Visibility;

public class CommandAdd extends BaseCommand
{
    @Override
    public String actionName() 
    {
        return "add";
    }
    @Override
    public void act(String[] args)
    {
        if(args == null || args.length == 0)
        {
            System.out.println(description());
            return;
        }
        switch (args[0])//Check 1st word
        {
            case "class" -> addClass(args);
            case "relationship"-> addRelationship(args);
            case "method"-> addMethod(args);
            case "field"-> addField(args);
            case "param"->addParam(args);
            default -> {
                System.out.println(description());
            }
        }
    }
    /**
    * Pulled from the switch statement for better control flow clarity.
    */
    private void addParam(String[] args)
    {
        if(args.length <= 4)
        {
            System.out.println("""
                add param | Please specify a class name, method name, type and param name.
                                   EX: add param classname methodname type paramname""");
            return;
        }
        String className = args[1];
        String methodName = args[2];
        String paramName = args[4];
        String dataTypeString = args[3];
        DataType dataType = DataType.stringToDatatype(dataTypeString);

        UMLClass umlClass = UMLDocument.getInstance().getClass(className);
        if(umlClass == null)
        {
            System.out.println(String.format("add param | No class with the name %s was found",className));
            return;
        }
        //Bad api. Should not be getting the map then sifting through it manually. Oh well.
        List<UMLMethod> methodList = umlClass.getMethods(methodName);
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

        boolean isSuccessful = selectedMethod.addParameter(param);
        if(isSuccessful)
        {
            System.out.println("Param edited");
        }
        else
        {
            System.out.println("Parameter already exists.");
        }
    }
    /**
    * Pulled from the switch statement for better control flow clarity.
    */
    private void addField(String[] args)
    {
        if(args.length == 5)
        {
            UMLClass umlClass = UMLDocument.getInstance().getClass(args[1]);
            if(umlClass == null)
            {
                System.out.println("add field | The given classname dosn't exist.");
            }
            else
            {
                String fieldName = args[4];
                String dataTypeString = args[3];
                UMLDataField field = new UMLDataField(fieldName, dataTypeString, DataType.stringToDatatype(dataTypeString), Visibility.stringVisibility(args[2]));
                boolean success = umlClass.addField(field);
                if(success)
                    System.out.println(String.format("add field | The field has been added to '%s' : %s",args[1], field.toString()));
                else
                    System.out.println("add field | The field already exists.");
            }
        }
        else
        {
            System.out.println("""
                                    add field | Please specify the correct ammount of parameters
                                                       EX: add field ClassName private int FieldName""");
        }
    }
     /**
     * Pulled from the switch statement for better control flow clarity.
     */
    private void addClass(String[] args)
    {
        if(args.length == 2) {
            UMLClass retClass = UMLDocument.getInstance().addClass(args[1]);
            if(retClass == null) {
                System.out.println("ACTION BLOCKED! Class already exists!");
            } 
            else{
                System.out.println(String.format("add class | The class '%s' has been created!",args[1]));
            }
        }
        else {
            System.out.println("""
                                add class | Please specify a classname
                                                   EX: add class yourClassNameHere""");
        }
    }
    /**
    * Pulled from the switch statement for better control flow clarity.
    */
    private void addRelationship(String[] args)
    {
        if(args.length == 3)
        {
            boolean isActionValid = UMLDocument.getInstance().addRelationship(args[1], args[2]);
            if(isActionValid)
            {
                System.out.println(String.format("add relationship | Relationship between %s and %s has been made!",args[1],args[2]));
            }
            else
            {
                System.out.println("ACTION BLOCKED! Connection already exists or is invalid!");
            }
        }
        else 
        {
            System.out.println("""
                                    add relationship | Please specify only two extra parameters
                                                       EX: add relationship class1 class2""");
        }
    }
    /**
     * Pulled from the switch statement for better control flow clarity.
     */
    private void addMethod(String[] args)
    {
        if(args.length <= 2)
        {
            System.out.println("""
                 add method | Please specify a class and methhod name
                                    EX: add method classname methodname
                                    EX: add method classname methodname type param1 type param2 ... so on ... type param10"""); 
        }
        else
        {
            String className = args[1];
            UMLClass umlClass = UMLDocument.getInstance().getClass(className);
            if(umlClass == null)
            {
                System.out.println("ERROR! Class does not exist!");
                return;
            }
            //Get params and such.
            String methodName = args[2];
            UMLMethod method = new UMLMethod();
            method.setMethodName(methodName);
            List<String> remainingArgsList = Arrays.asList(args).subList(3, args.length);//Exclude the first 3 arguments, they were consumed earlier

            ArrayList<UMLParameter> paramList = new ArrayList<>();
            if(!remainingArgsList.isEmpty())//If there is something to format
            {
                if(remainingArgsList.size() % 2 == 0)//There are an equal pair of "type, param1"
                {
                    for(int i = 0; i < remainingArgsList.size(); i += 2)
                    {
                        String dataTypeString = remainingArgsList.get(i);
                        DataType dataType = DataType.stringToDatatype(dataTypeString);
                        String paramName = remainingArgsList.get(i+1);

                        UMLParameter iParam = new UMLParameter(paramName, dataType, (dataType == DataType.OTHER)? dataTypeString : null);
                        paramList.add(iParam);
                    }
                }
                else
                {
                    System.out.println("ERROR! Please ensure the command is of the form: \n add method classname methodname type param1 type param2 ... so on ... type param10");
                    return;
                }
            }
            method.setListParameters(paramList);
            boolean isMethodAdditionValid = umlClass.addMethod(method);
            //Report back to the user
            if(isMethodAdditionValid)
            {
                System.out.println(String.format("add method | Added method '%s' to '%s' with the following params:", methodName,className));
                if(paramList.isEmpty())
                {
                    System.out.println("No params!");
                }
                else//Print their freshly added params.
                {
                    int index = 1;
                    for(UMLParameter paramPrint : paramList)
                    {
                        System.out.println(String.format("[%d] : %s %s", index,
                                paramPrint.getDataType() == DataType.OTHER? paramPrint.getCustomNameType() : paramPrint.getDataType().toString(),
                                paramPrint.getName()));
                        index++;
                    }
                }
            }
            else
            {
               System.out.println("ERROR! Cannot add method. Method already exists."); 
            }
        }
    }
    @Override
    public String description()
    {
        return """
               Possible parameters for add:
                        class <classname> : Adds a class with a given classname
                        relationship <source> <destination> : Adds a relationship between two classes
                        method <target class> <method name> <type1> <name1> ... <type10> <name10>: Adds a method to the target class
                        field <target class> <visibility> <type> <name> : Adds a field to the target class
                        param <target class> <method name> : Starts the process for adding a param to a classes method""";
    }
}
