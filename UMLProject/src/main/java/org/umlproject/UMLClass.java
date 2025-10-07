package org.umlproject;

public class UMLClass 
{
    private String className;
    public UMLClass(String className)
    {
        this.className = className;
    }
    public String getClassName()
    {
        return this.className;
    }
    @Override
    public String toString() {
        return String.format("UML class print info: %s", className);
    }
    @Override
    public boolean equals(Object  obj) 
    {
        if(this == obj) 
            return true;
        if(obj == null || getClass() != obj.getClass()) 
            return false;
        
        UMLClass castedObject = (UMLClass)obj;
        //Bare bones implementation.
        //TODO: When you add more to the class, maintain this equals function.
        return castedObject.className.equals(this.className);
    }
    @Override
    public int hashCode() {
        return this.className.hashCode();
    }
}
