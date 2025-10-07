package org.umlproject;

public class UMLMethod
{
    private String methodName;
    public UMLMethod(String methodName)
    {
        this.methodName = methodName;
    }
    public String getMethodName()
    {
        return this.methodName;
    }
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
        return castedObject.methodName.equals(this.methodName);
    }
    @Override
    public int hashCode() {
        return this.methodName.hashCode();
    }
}
