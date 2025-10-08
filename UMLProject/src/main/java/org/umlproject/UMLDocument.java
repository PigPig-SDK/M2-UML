package org.umlproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UMLDocument 
{
    private String fileLocation = null;
    private Map<String, UMLClass> classSet = new HashMap<>();
    private ArrayList<UMLRelationship> relationshipList = new ArrayList<>();
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
    public UMLClass getClass(String className)
    {
        return classSet.get(className);
    }
    public boolean isFileLocationValid()
    {
        throw new UnsupportedOperationException("No feature exists!");
    }
    public void save()
    {
        throw new UnsupportedOperationException("No feature exists!");
    }

    /**
     * @param className Name given to new class
     * @return Returns created class, or null if class already exists
     */
    public UMLClass addClass(String className)
    {
        if (classSet.containsKey(className)) return null;
        UMLClass umlclass = new UMLClass(className);
        classSet.put(className, umlclass);
        return umlclass;

    }

    /**
     * @return number of classes added
     */
    public int getClassCount()
    {
        return classSet.size();
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
