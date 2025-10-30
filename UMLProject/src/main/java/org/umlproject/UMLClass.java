package org.umlproject;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.umlproject.UI.GuiClass;

public class UMLClass extends UMLDiagramElement {
    private String className;
    /**
     * use hashmap for UMLDataFields where a unique DataField name is the key
     */
    private HashMap<String, UMLDataField> fields;
    /**
     * Use hashmap that looks up an array list of overloaded methods by common name
     */
    private HashMap<String, ArrayList<UMLMethod>> methods;

    /**
     * This has to be contained within UMLClass because the document layout must be retained between saves.
     */
    private double locationX, locationY = 0;
    
    public Point2D getLocation() { return new Point2D(locationX, locationY); }
    
    public void setLocation(Point2D location) { 
        this.locationX = location.getX();
        this.locationY = location.getY();
        updateGUILocation();
    }
    
    /**
     * UMLClass constructor that takes a class name as input. Assigns empty hashMaps for
     * UMLDataFields and UMLMethods
     *
     * @param className string representing class name.
     */
    public UMLClass(String className) {
        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("class name cannot be null or empty");
        }
        this.className = className;
        this.fields = new HashMap<>();
        this.methods = new HashMap<>();
    }

    /**
     * UMLClass Constructor that takes a className, fields HashMap and methods HashMap all as parameters.
     * @param className, String representing the new Class name
     * @param fields, hashMap representing the DataFields of the class
     * @param methods, hashMap representing the methods of the class.
     */
    public UMLClass(String className, HashMap<String, UMLDataField> fields, HashMap<String, ArrayList<UMLMethod>> methods){
        if(className == null || className.isEmpty() || fields == null || methods == null){
            throw new IllegalArgumentException("The arguments provided are invalid!");
        }
        
        
        this.className = className;
        this.fields = fields;
        this.methods = methods;
    }
    /**
     * getter method for a single field
     *
     * @return fields hashMap
     */
    public UMLDataField getFields(String fieldName) {
        return this.fields.get(fieldName);
    }

    /**
     * getter method for a single method.
     *
     * @return methods an ArrayList of overloaded UMLMethods
     */
    public ArrayList<UMLMethod> getMethods(String methodName) {
        return this.methods.get(methodName);
    }

    /**
     * getter method for fields
     *
     * @return fields hashMap
     */
    public HashMap<String, UMLDataField> getFieldsAll() {
        return this.fields;
    }

    /**
     * getter method for methods
     *
     * @return methods hashmap
     */
    public HashMap<String, ArrayList<UMLMethod>> getMethodsAll() {
        return this.methods;
    }

    /**
     * getClassName returns the className
     *
     * @return string representing class name
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * getClassName returns the className
     *
     * @param name to set className
     */
    public void setClassName(String name) {
        this.className = name;
        updateGUI();
    }
    /**
     * addField method will add a new UMLDataField object to the fields hashMap under the
     * condition that its name is unique and valid(i.e. not null), upon success a boolean value
     * of true will be returned, otherwise false.
     *
     * @param field UMLDataField object representing the field to be added to the class
     * @return boolean value that indicates whether add was successful or not
     */
    public boolean addField(UMLDataField field) {
        //return false if name or field is invalid or empty
        if (field == null) {
            return false;
        }
        String name = field.getName();

        //check to see if dataField is already in hashMap
        if (fields.containsKey(name)) {
            return false;
        }

        fields.put(name, field);
        updateGUI();
        return true;
    }

    /**
     * The removeField method requires a String fieldName as input (note the hashMap
     * ensures all DataFields are uniquely named atm). If a matching name can be located in fields hashmap,
     * then it will be removed and true will be returned. If fieldName is not in hashMap, then false will be
     * returned.
     *
     * @param fieldName  unique name of the UMLDataField object to be removed
     * @return boolean value representing success of removal
     */
    public boolean removeField(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return false;
        }
        //The remove function removes the value associated with the given key parameter.
        //Normally, remove() returns the removed value and if it didn't exist, null is returned.
        //The != null ensures a boolean value is returned
        boolean isRemoved = fields.remove(fieldName) != null;
        if(isRemoved)//Update our GUI listener.
            updateGUI();
        return isRemoved;
    }

    /**
     * the renameField class will take the old name of the field and the desired new name of the field
     * as parameters. method will test to make sure the new name is not null, empty, or a duplicate. If any
     * of these outcomes occur, false will be returned. Next the method will check to see if the DataField with
     * the old name is in the hashmap fields. If so we can begin renaming process, if not false is returned.
     *
     * @param oldName is a string representing the old name of DataField
     * @param newName is string representing new name of DataField
     * @return boolean representing success of rename
     */
    public boolean renameField(String oldName, String newName) {
        if (newName == null || newName.isEmpty() || fields.containsKey(newName)) {
            return false;
        }
        //Note that remove returns null if key didn't exist.
        UMLDataField field = fields.remove(oldName);
        if (field == null) {
            return false;
        }
        //update field name
        field.setName(newName);
        fields.put(newName, field);
        updateGUI();
        return true;
    }


    /**
     * The renameMethod method will take the old method name, the old methods parameter list,
     * and the desired new name of the method all as parameters. If either the old method name
     * or the new method name are invalid in any way, i.e. null or empty, then
     * method will return false. Otherwise, attempt to extract the arrayList of methods that
     * corresponds to the old method name. If no such list exists that corresponds to the given
     * old method name, then return false. If it does, search the list for a method that has an
     * equivalent list of method parameters. If no such list can be found, then return false
     * because that overloaded method is not in the list. If a match is found, we need to make sure
     * that a method with the newName and with a matching list of parameters doesn't already exist. If it does
     * return false. If it doesn't, then we can remove the old method from its previous list, give it a new
     * name, and then insert it into the other list of UMLMethods.
     *
     * @param oldName    of method
     * @param newName    of method
     * @param parameters list of parameters for method overload matching
     * @return boolean representing success of method renaming
     */
    public boolean renameMethod(String oldName, String newName, ArrayList<UMLParameter> parameters) {
        if (oldName == null || oldName.isEmpty() || newName == null || newName.isEmpty()) {
            return false;
        }

        //If the oldName exists in HashMap, then return its array list of overloaded methods
        ArrayList<UMLMethod> oldList = methods.get(oldName);
        if (oldList == null) {
            //old name was not in hashMap, thus list was null. return false
            return false;
        }

        //old name was in Hashmap, so search for matching parameter list
        UMLMethod target = null;
        for (UMLMethod m : oldList) {
            if (m.getParameters().equals(parameters)) {
                target = m;
                break;
            }
        }

        //if method with matching parameters was not found, return false
        if (target == null) {
            return false;
        }

        //newName may correspond to an existing method with matching parameter list. Need
        //to check for duplicates. Note that getOrDefault() will search the hashMap for a method
        //whose name matches the newName parameter. If one is found, an ArrayList of overloaded
        //methods with that name is returned, otherwise a default empty array list is returned.

        ArrayList<UMLMethod> newList = methods.getOrDefault(newName, new ArrayList<>());
        for (UMLMethod m : newList) {
            if (m.getParameters().equals(parameters)) {
                /**  return false if you find a method whose parameter list matches the list of
                 * the method to be renamed.*/
                return false;
            }
        }

        //remove from old list
        oldList.remove(target);
        if (oldList.isEmpty()) {
            methods.remove(oldName);
        }

        //update name and insert into new list
        target.setMethodName(newName);
        newList.add(target);
        methods.put(newName, newList);
        updateGUI();
        return true;
    }


    /**
     * The addMethod class will seek to add a new method to the class. It will check to see that
     * the argument method is not null and has a valid name. Next it will check to ensure none of the
     * parameters are null and all have non-empty string names. Next it will compare the parameter
     * list of the argument method with that of all overloaded methods with the same name to ensure
     * to duplicate methods are inserted. if no duplicates are found, the method will be added to the
     * list.
     *
     * @param method is the UMLMethod object to be added to the UMLClass
     * @return boolean representing whether adding a new method was successful
     */
    public boolean addMethod(UMLMethod method) {
        //make sure method argument is not null and has a valid name
        if (method == null) {
            return false;
        }

        //search the parameters to make sure that they are valid inputs, i.e. none are null
        //and none have an empty string as a name
        ArrayList<UMLParameter> params = method.getParameters();
        if (params == null) {
            return false;
        }
        for (UMLParameter p : params) {
            if (p == null || p.getName() == null || p.getName().isEmpty()) {
                return false;
            }
        }

        //check for duplicate methods before adding. Note that getOrDefault() returns a
        //list of UMLMethods that correspond to the given name. If no such list exists, a
        //default empty list is assigned.
        String name = method.getMethodName();
        ArrayList<UMLMethod> list = methods.getOrDefault(name, new ArrayList<>());

        for (UMLMethod m : list) {
            if (m.getParameters().equals(params)) {
                return false;
            }
        }

        //no duplicates found. Safe to add to list.
        list.add(method);
        methods.put(name, list);
        updateGUI();
        return true;
    }

    /**
     * The removeMethod will locate and remove a method by matching against a given methodName and parameter list.
     * method will ensure arguments are valid. Then will ensure methodName exists as a key in the hashMap, if not return
     * false. If it exists, compare the parameter lists of each of the overloaded methods with a matching name
     * against the argument parameter list. If no match is found, return false. if a match is found, remove the method and
     * return true.
     *
     * @param methodName, of the method to be removed.
     * @param index the index within the list
     * @return boolean indicating whether method was successfully removed
     */
    public boolean removeMethod(String methodName, int index) {
        //make sure inputs are valid
        if (methodName == null || methodName.isEmpty()) {
            return false;
        }
        //    private HashMap<String, ArrayList<UMLMethod>> methods;

        if(!methods.containsKey(methodName))
            return  false;
        boolean isRemoved = methods.get(methodName).remove(index) != null;
        if(isRemoved)
            updateGUI();
        return isRemoved;
    }

    /**
     * The addParameter method will add a new parameter to an existing list of parameters. Method takes a methodName, an
     * ArrayList of parameters to using in matching an overloaded method name, and the newParameter to be added all as arguments.
     * Method will ensure arguments are not null. Then will check to ensure that the new parameter is not a duplicate of one already
     * in the provided parameter list. Then the method will determine if a method with a matching parameter list exists, in which case
     * the new parameter will be added and true will be returned, otherwise false will be returned.
     *
     * @param methodName   name of method.
     * @param parameters   used to find correct overloaded method.
     * @param newParameter to be added.
     * @return boolean representing whether parameter addition was successful
     */
    public boolean addParameter(String methodName, ArrayList<UMLParameter> parameters, UMLParameter newParameter) {
        //ensure inputs are not invalid, i.e. not null and no empty strings
        if (methodName == null || methodName.isEmpty() || parameters == null || newParameter == null) {
            return false;
        }

        //ensure newParameter is not a duplicate of one in the provided parameter list argument
        for (int i = 0; i < parameters.size(); i++) {
            if (parameters.get(i).equals(newParameter)) {
                return false;
            }
        }

        //check to see if method name exists in hashMap, if not return false.
        ArrayList<UMLMethod> list = methods.get(methodName);
        if (list == null) {
            return false;
        }
        //method exists, so track down the proper overloaded version. And insert new parameter
        for (UMLMethod method : list) {
            if (method.getParameters().equals(parameters)) {
                method.addParameter(newParameter);
                updateGUI();
                return true;
            }
        }
        //method doesn't exist in hashMap
        return false;
    }

    /**
     * remove parameter will locate the proper overloaded method and remove the argument parameter.
     * The method will ensure all inputs are valid, i.e. not null and not empty string. Then will check to see if
     * a method by the given argument name exists in the hashMap. If so an arrayList of overloaded methods with
     * that name will be returned. That list will be searched for a method whose parameter list matches the given
     * argument parameter list. If a matching method is found, the argument parameter will be removed and true will
     * be returned. If not, return false.
     *
     * @param methodName,    name of method.
     * @param parameters,    used to locate proper overloaded method.
     * @param paramToRemove, UMLParameter to remove.
     * @return boolean representing whether removal of parameter was successful.
     */
    public boolean removeParameter(String methodName, ArrayList<UMLParameter> parameters, UMLParameter paramToRemove) {
        //ensure inputs are valid. i.e. nothing is null and no empty strings
        if (methodName == null || methodName.isEmpty() || parameters == null || paramToRemove == null) {
            return false;
        }
        //check if overloaded method exists. If no list of methods with given
        //argument name is returned, return false.
        ArrayList<UMLMethod> overloads = methods.get(methodName);
        if (overloads == null) {
            return false;
        }
        //search for appropriate method to remove parameter from
        for (UMLMethod method : overloads) {
            if (method.getParameters().equals(parameters)) {
                boolean removed = method.removeParameter(paramToRemove);
                if(removed) updateGUI();
                return removed;
            }
        }
        //no matching overloaded method
        return false;
    }

    /**
     * Overloaded changeParameters method. This method functions roughly the same as its counterpart method except that
     * it instead replaces a single parameter in a method with a list of new parameters.
     *
     * @param methodName,    name of the Method
     * @param paramToRemove, UMLParameter object to remove
     * @param oldParameters  used to locate right overloaded method
     * @param newParameters  to replace old Parameters
     * @return boolean indicating whether changing of parameter was successful
     */
    public boolean changeParameter(String methodName, UMLParameter paramToRemove, ArrayList<UMLParameter> oldParameters, ArrayList<UMLParameter> newParameters) {
        //ensure inputs are valid
        if (methodName == null || methodName.isEmpty() || oldParameters == null || newParameters == null) {
            return false;
        }

        //ensure the specified methodName exists in class, if not return false
        ArrayList<UMLMethod> overloads = methods.get(methodName);
        if (overloads == null) {
            return false;
        }

        //search for correct overloaded method by comparing parameter list
        for (UMLMethod method : overloads) {
            if (method.getParameters().equals(oldParameters)) {
                //match found, so swap parameter with parameter list
                method.changeParameter(paramToRemove, newParameters);
                updateGUI();
                return true;
            }
        }
        return false;
    }


    /**
     * The changeParameter method will be overloaded. This version replaces an entire parameter list of a given overloaded
     * method with a new parameter list. Method checks that arguments are valid. Then ensures methodName exists in class. If
     * not it returns false. If method name exists, then list of overloaded methods is searched for one that matches the
     * oldParameters argument. If match is found, the old list is swapped out with the newParameter list.
     *
     * @param methodName,   name of method
     * @param oldParameters to be replaced
     * @param newParameters to be swapped with oldParameters
     * @return boolean representing whether changing of parameter was successful
     */
    public boolean changeParameter(String methodName, ArrayList<UMLParameter> oldParameters, ArrayList<UMLParameter> newParameters) {
        //ensure inputs are valid
        if (methodName == null || methodName.isEmpty() || oldParameters == null || newParameters == null) {
            return false;
        }

        //ensure the specified methodName exists in class, if not return false
        ArrayList<UMLMethod> overloads = methods.get(methodName);
        if (overloads == null) {
            return false;
        }

        //search for correct overloaded method by comparing parameter list
        for (UMLMethod method : overloads) {
            if (method.getParameters().equals(oldParameters)) {
                //match found, so swap parameter with parameter list
                method.setListParameters(newParameters);
                updateGUI();
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the first available spot for a new data field when added through GUI
     *
     * @return String - The first available name
     */
    public String findValidFieldDummySignature()
    {
        final String dummySignature = "PRIVATE INT ";
        final String dummyName = "Dummy";
        //print the fields so we can see whats happening
        ArrayList<String> fieldList = new ArrayList<>(fields.keySet());
        System.out.println("printing dummy fields in existence");
        for(int i = 0; i < fieldList.size(); i++){
            System.out.println(fieldList.get(i));
        }
        int increment = 1;
        while(true)
        {
            String testName = dummyName + increment;
            if(!fields.containsKey(testName))//Name is not taken
                return dummySignature + testName;
            increment++;
        }
    }

    /**
     * Returns the first available spot for a new method when added through GUI
     *
     * @return String - The first available name
     */
    public String findValidMethodDummySignature()
    {
        final String dummyName = "method";
        final String dummySignature = " INT P1";
        int increment = 1;
        while(true)
        {
            String testName = dummyName + increment;
            if(!methods.containsKey(testName)) {//Name is not taken
                System.out.println("the signature being returned is: " + testName + dummySignature);
                return testName + dummySignature;
            }
            increment++;
        }
    }

    //--------------------------------Whats below needs updating-----------------------------------


    @Override
    public String toString() {
        return String.format("UML class print info: %s", className);
    }

    @Override

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        UMLClass castedObject = (UMLClass) obj;
        //Bare bones implementation.
        //TODO: When you add more to the class, maintain this equals function.
        return castedObject.className.equals(this.className);
    }

    @Override
    public int hashCode() {
        return this.className.hashCode();
    }
}