package org.umlproject;

import java.util.Objects;

/**
 * The UMLParameter class will be a dependent class for the UMLMethod class.
 * UMLParameter objects will be created to populate array lists of parameters within
 * a given UMLMethod object
 */
public class UMLParameter {

    private String name;
    private DataType dataType;
    private String customNameType;

    /**
     * UMLParameter constructor, that takes a name and dataType parameter. If dataType == OTHER and no value
     * other than null is specified for customNameType, then an exception will be thrown that needs
     * to be resolved by the caller.
     * @param name of UMLParamter
     * @param dataType of UMLParamter
     * @param customNameType of UMLParamter
     */
    public UMLParameter(String name, DataType dataType, String customNameType) {

        this.name = name;
        this.dataType = dataType;
        if (this.dataType == DataType.OTHER) {
            if(customNameType == null || customNameType.isEmpty()){
                throw new IllegalArgumentException("Custom name is required for OTHER data type");
            }
            this.customNameType = customNameType;
        }
        else{
            this.customNameType = null;
        }
    }

    /**
     * Default constructor, sets everything to null
     */
    public UMLParameter() {
        this.name = null;
        this.dataType = null;
        this.customNameType = null;
    }

    /**
     * getter method for parameter name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * getter method for dataType. Note that you may need to retrieve the customNameType
     * If dataType == OTHER
     * @return DataType
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * getter for the customNameType data field
     * @return customNameType
     */
    public String getCustomNameType() {
        return this.customNameType;
    }

    /**
     * returns a string representation of the parameter
     * @return string representation of UMLParameter
     */
    @Override
    public String toString(){
        return this.name + ", " + this.dataType + ", " + this.customNameType;
    }

    /** equals method will return true if both objects share a memory location, or if their
     * name, dataType, and customNameType fields all match
     * the reference object with which to compare.
     * @param obj   the reference object with which to compare.
     * @return boolean representing whether the two UMLParamter objects were equal
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(!(obj instanceof UMLParameter)) return false;
        UMLParameter otherObj = (UMLParameter) obj;
        return Objects.equals(this.name, otherObj.name) && this.dataType == otherObj.dataType
                && Objects.equals(this.customNameType, otherObj.customNameType);
    }

    /**
     * Hashcode method for UMLParameter. generates hash code from name, dataType, and customNameType
     * @return hash code of the UMLParamter
     */
    @Override
    public int hashCode(){
        return Objects.hash(name, dataType, customNameType);
    }






}