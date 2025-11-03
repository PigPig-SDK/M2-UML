package org.umlproject;

import java.util.Scanner;

/**This class represents the datafields of a UMLClass instance
 *will consist of a name, visibility modifier, data type (custom or primitive)
 *as well as methods to set or get these values
 */
public class UMLDataField {
    /** name of dataField
     */
    private String name;

    /**DataType can be either an enum representing a primitive type, or it can be set to
     *"OTHER", in which case the user will have to enter a string representing their custom type
     *"In the case of wrapper classes such as "DOUBLE, INT, etc.", visibility will be set to OTHER
     *and the custom name will hold the value "DOUBLE" etc.
     */
    private DataType dataType;

    private String customNameType;

    private Visibility visibility;

    /**
     * UMLDataField constructor that takes a name, dataType and customNameType in the case where
     * dataType = OTHER. If dataType is not OTHER then set customNameType to null.
     * @param name of UMLDataField
     * @param customNameType of UMLDataFIeld
     */
    public UMLDataField(String name, String customNameType){
        this(name, customNameType, DataType.OTHER, Visibility.PRIVATE);
    }
    /**
     * This constructor is the holy trinity of UMLDataField... because customNameType may or may not be used.
     * @param name of UMLDataField
     * @param customNameType of UMLDataField
     * @param dataType of UMLDataField
     * @param visibility of UMLDataField
     */
    public UMLDataField(String name, String customNameType, DataType dataType, Visibility visibility){
        this.visibility = visibility;
        this.dataType = dataType;
        this.name = name;
        if(dataType.equals(DataType.OTHER) && customNameType == null){
            System.out.println("customNameType cannot be null for DataType == OTHER");
            throw new IllegalArgumentException();
        }
        else if(dataType.equals(DataType.OTHER)){
            this.customNameType = customNameType;
        }
        else{
            this.customNameType = null;
        }
    }
    
    /**
     * UMLDataFIeld constructor that takes a name and a dataType as parameter. If this constructor is mistakenly
     * called with OTHER as the DataType argument, an exception will be thrown and it will be left to the caller to resolve
     * the error.
     * @param name of UMLDataField
     * @param dataType of UMLDataField
     */
    public UMLDataField(String name, DataType dataType){
        this(name, null, dataType, Visibility.PRIVATE);
    }

    /**
     *UMLDataField constructor that takes a name, DataType, and Visibility argument. If DataType is OTHER
     * an exception will be thrown to be handled by the caller. Otherwise customNameType is set to null
     * @param name, name of UMLDataFIeld
     * @param dataType of UMLDataField
     * @param visibility of UMLDataField
     */
    public UMLDataField(String name, DataType dataType, Visibility visibility){
        this.name = name;
        this.dataType = dataType;
        this.visibility = visibility;

        if(this.dataType == DataType.OTHER){

            //throw exception  since dataType == OTHER. This allows caller to
            //request a user specified name as the data type.
            throw new IllegalArgumentException("Custom type name is required for OTHER data type");

        }
        this.customNameType = null;
    }


    /**setter to reset dataType. If new dataType is OTHER, then need to prompt user for a customTypeName and
     *set the customTypeName field. If previous dataType was OTHER, and is being changed to a primitive,
     *Then we need to set the customNameType to null
     * @param dataType, The DataType of the UMLDataField
     */
    public void setDataType(DataType dataType){
        if(this.dataType == DataType.OTHER && dataType != DataType.OTHER){
            this.customNameType = null;
        }

        this.dataType = dataType;
        if(this.dataType == DataType.OTHER){
            // throw IllegalArgumentException and allow caller to request a user specified
            // data type name.
            throw new IllegalArgumentException("Custom type name is required for OTHER data type\n");
        }


    }

    /**setter to reset the visibility modifier
     * @param visibility The Visibility of the UMLDataField
     */
    public void setVisibility(Visibility visibility){
        this.visibility = visibility;
    }

    /** setter to reset name of DataField Instance
     * @param name The name of the UMLDataField
     */
    public void setName(String name){
        if(name == null || name.isEmpty())
            return;
        this.name = name;
    }

    /**
     * getter to return customNameType
     * @return String representing the custom name.
     */
    public String getCustomNameType(){
        return this.customNameType;
    }

    /**setter to reset the customNameType ONLY if dataType == OTHER, otherwises prints message
     *informing user that dataType has an invalid value and then returns.
     * @param customNameType the customeNameType that corresponds to DataType.OTHER
     */
    public void setCustomNameType(String customNameType){
        if(this.dataType == DataType.OTHER)
        {
            this.customNameType = customNameType;
        }
        else
        {
            System.out.println("dataType must be set to OTHER in order to set a customNameType");
        }
    }

    /** getter for the name of DataField instance
     * @return name of UMLDataField
     */
    public String getName(){
        return this.name;
    }

    /** getter for the customNameType of DataField instance
     *should return null if dataType is not equal to OTHER
     * @return customNameType of UMLDataField
     */
    public String getTypeAsString(){
        if(this.dataType == DataType.OTHER) 
        {
            return this.customNameType;
        }
        else
        {
            return this.dataType.toString();
        }
    }

    /** getter method for visibility of DataField instance
     * @return Visibility of UMLDataField
     */
    public Visibility getVisibility(){
        return this.visibility;
    }


    /** getter method for dataType of DataField instance
     * @return DataType of UMLDataField
     */
    public DataType getDataType(){
        return this.dataType;
    }

    /** toString() method for easy testing
     * @return string representation of DataField
     */
    @Override
    public String toString(){
        if(this.visibility == null)
            return String.format("%s %s %s", "NULL" , (this.dataType == DataType.OTHER)? this.customNameType : this.dataType, this.name);
        return String.format("%s %s %s", this.visibility.toString(), (this.dataType == DataType.OTHER)? this.customNameType : this.dataType, this.name);
    }

    /** equals method for checking if two DataFields are equal
     * @param obj   the reference object with which to compare.
     * @return boolean representing whether both objects are equal
     */
    @Override
    public boolean equals(Object  obj)
    {
        if(this == obj)
            return true;
        if(obj == null || getClass() != obj.getClass())
            return false;

        UMLDataField castedObject = (UMLDataField)obj;
        //Bare bones implementation.
        //TODO: When you add more to the class, maintain this equals function.
        return castedObject.name.equals(this.name);
    }

    /**
     * hashCode method for DataField class
     * @return returns hash code for DataField
     */
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
