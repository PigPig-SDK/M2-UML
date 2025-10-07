package org.umlproject;


public class UMLDataField
{
    private String fieldName;
    public UMLDataField(String fieldName)
    {
        this.fieldName = fieldName;
    }
    public String getFieldName()
    {
        return this.fieldName;
    }
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
        return castedObject.fieldName.equals(this.fieldName);
    }
    @Override
    public int hashCode() {
        return this.fieldName.hashCode();
    }
}
