package org.umlproject;

import java.util.ArrayList;


/** class representing a UMLMethod
 *  Note that an ArrayList was chosen for the parameters because order is important
 * for overloading methods. Thus, will need to check for duplicate parameters by cycling through
 * the list. Generally, each UMLMethod instance will consist of a Visibility status, a return type, a custom type name
 * in case the DataType of the return type is OTHER, a method
 * name, list of parameters, as well as a boolean variable to indicate if method is static or not.
 *But for the mean time all that is relevant is the methodName and the arrayList of parameters.
 */
public class UMLMethod
{
    /** The below is functionality that this method will most likely need in the future
     *private boolean isStatic;
     *private Visibility visibility;
     *private DataType returnType;
     *private String customReturnTypeName;
     */
    private String methodName;
    private ArrayList<UMLParameter> parameters;


    /** Constructor for UMLMethod class. Requires a Visibility status, return type in the form of a DataType
     * enum, methodName, and an ArrayList of parameters.
     *
     */
    public UMLMethod(String methodName, ArrayList<UMLParameter> parameters){
        if(methodName == null || parameters == null){
            throw new IllegalArgumentException("name is null or empty string");
        }
        this.methodName = methodName;
        this.parameters = parameters;

    }

    /** default constructor
     */
    public UMLMethod(){
        this.methodName = "";
        this.parameters = new ArrayList<UMLParameter>();
    }

    /**
     *  addParameter method will take a UMLParameter object as a parameter.
     * Method will cycle through list of current method parameters and search for
     * duplicate parameters with .equals() method of UMLParameter class. IF
     * Duplicate is found, return false. If no duplicate is found, add new parameter to
     * list and return true.
     * @param param, UMLParamter to add
     * @return boolean representing success
     */
    public boolean addParameter(UMLParameter param){
        for(UMLParameter p: parameters){
            if(p.getName().equals(param.getName()) && p.getDataType() == param.getDataType()){//TODO: update equals method. this should use .equals()
                return false;
            }

        }
        parameters.add(param);
        return true;
    }

    /** Initially this method is just being written to remove a single instance of a parameter
     * by name. Method will return true if successful and false if no such name
     * is in the parameter list.
     * @param paramToRemove, UMLParamter object to remove
     * @return boolean representing success
     */
    public boolean removeParameter(UMLParameter paramToRemove){
        for(int i = 0; i < parameters.size(); i++){
            if(parameters.get(i).equals(paramToRemove)){
                parameters.remove(i);
                return true;
            }
        }
        return false;
    }

    /** changeParameter will change out an old parameter with a specific new one. It will
     * swap the first instance of a given parameter name that it finds. If the old
     * parameter name is in the list, then return true upon swap, otherwise return false.
     *
     * @param oldParameter, to be removed
     * @param newParameter, replaces oldParameter
     * @return boolean representing success
     */
    public boolean changeParameter(UMLParameter oldParameter, UMLParameter newParameter){
        for(int i = 0; i < parameters.size(); i++){
            if(parameters.get(i).equals(oldParameter)){
                parameters.set(i, newParameter);
                return true;
            }
        }
        return false;
    }

    /** This version of changeParameter method will swap out the first instance of an old parameter name
     * with a list of new parameters. If the old name appears in the list, then return true upon swap
     * , otherwise return false.
     * @param oldParameter, UMLParamter to be swapped
     * @param newParameters, UMLParamter that is swapped with oldParamter
     * @return
     */
    public boolean changeParameter(UMLParameter oldParameter, ArrayList<UMLParameter> newParameters){
        for(int i = 0; i < parameters.size(); i++){
            UMLParameter current = parameters.get(i);
            if(current.equals(oldParameter)){
                parameters.remove(i);

                /** insert params */
                parameters.addAll(i, newParameters);
                return true;
            }
        }
        /** old name not found */
        return false;
    }

    /** getter method for methodName
     * @return returns method name
     */
    public String getMethodName()
    {
        return this.methodName;
    }

    /** getter method for parameter list
     * @return the parameter list
     */
    public ArrayList<UMLParameter> getParameters(){
        return this.parameters;
    }

    /** setter method for methodName
     * @param methodName, the new name of the method
     */
    public void setMethodName(String methodName){
        this.methodName = methodName;
    }

    /**setter method for list of parameters
     * @param parameters, the new list of paramters for the method
     */
    public void setListParameters(ArrayList<UMLParameter> parameters){
        this.parameters = parameters;
    }


    /** toString method will convert method into a string consisting of its name
     * and a list of its parameters
     * @return string representation of method
     */
    @Override
    public String toString() {
        return String.format("%s %s", methodName, parameters);
    }


    /** equals method will compare to methods according to name and contents of parameter list
     * @param obj   the reference object with which to compare.
     * @return boolean representing whether both methods are equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null || getClass() != obj.getClass())
            return false;

        UMLMethod castedObject = (UMLMethod)obj;
        //Bare bones implementation.
        //TODO: When you add more to the class, maintain this equals function.
        return castedObject.methodName.equals(this.methodName) && castedObject.parameters.equals(this.parameters);
    }

    /**
     * HashCode method for UMLMethod class
     * @return hashcode for the method
     */
    @Override
    public int hashCode() {
        return this.methodName.hashCode();
    }

}
