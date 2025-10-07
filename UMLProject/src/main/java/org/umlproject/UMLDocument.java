package org.umlproject;

import java.util.ArrayList;

public class UMLDocument 
{
    private String fileLocation = null;
    public UMLDocument(String fileLocation)
    {
        this.fileLocation = fileLocation;
    }
    public String getFileLocation()
    {
        return this.fileLocation;
    }
    public void setFileLocation(String newFileLocation)
    {
        this.fileLocation = newFileLocation;
    }
    public boolean deleteClass(String className)
    {
        throw new UnsupportedOperationException("No feature exists!");
    }
    public boolean renameClass(String originClassName, String newName)
    {
        throw new UnsupportedOperationException("No feature exists!");
    }
    public boolean deleteRelationship(String relationshipName)
    {
        throw new UnsupportedOperationException("No feature exists!");
    }
    public boolean deleteAllRelationships(String relationshipName)
    {
        throw new UnsupportedOperationException("No feature exists!");
    }
    public UMLRelationship findRelationship(String relationshipName, String destinationName)
    {
        throw new UnsupportedOperationException("No feature exists!");
    }
    public ArrayList<UMLRelationship> findAllRelationships(String relationshipName)
    {
        throw new UnsupportedOperationException("No feature exists!");
    }
    public UMLClass findClass(String className)
    {
        throw new UnsupportedOperationException("No feature exists!");
    }
    public boolean isFileLocationValid()
    {
        throw new UnsupportedOperationException("No feature exists!");
    }
    public void save()
    {
        throw new UnsupportedOperationException("No feature exists!");
    }
    @Override
    public boolean equals(Object obj) 
    {
        if(this == obj) 
            return true;
        if(obj == null || getClass() != obj.getClass()) 
            return false;
        
        UMLDocument castedObject = (UMLDocument)obj;
        //Bare bones implementation.
        //TODO: When you add more to the class, maintain this equals function.
        return castedObject.fileLocation.equals(this.fileLocation);
    }
    @Override
    public int hashCode() {
        return this.fileLocation.hashCode();
    }
}
