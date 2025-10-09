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
    public boolean addRelationship(String className, String destinationName){

        if(relationshipSearch(className, destinationName) == -1){
            relationshipList.add(new UMLRelationship(className, destinationName));
            return true;
        }
        return false;

    }
    public boolean deleteRelationship(String relationshipName, String destinationName)
    {

        int found = relationshipSearch(relationshipName, destinationName);
        if(found != -1){
            relationshipList.remove(found);
            return true;
        }
        return false;
        //throw new UnsupportedOperationException("No feature exists!");
    }
    public boolean deleteAllClassRelationships(String className)
    {

        boolean checked = false;
        for(UMLRelationship iRelationship : relationshipList){
            if(iRelationship.getSourceName().equals(className)){
                relationshipList.remove(iRelationship);
                checked = true;
            }
        }
        return checked;

    }
    public UMLRelationship findRelationship(String relationshipName, String destinationName)
    {

        int found = relationshipSearch(relationshipName, destinationName);
        if(found != -1){
            return relationshipList.get(found);
        }
        return null;

        //throw new UnsupportedOperationException("No feature exists!");
    }
    public ArrayList<UMLRelationship> findAllRelationships(String relationshipName)
    {

        ArrayList<UMLRelationship> allList = new ArrayList<UMLRelationship>();

        for(UMLRelationship iRelationship : relationshipList){
            if(iRelationship.getSourceName().equals(relationshipName)){
                allList.add(iRelationship);
            }
        }

        return allList;
        //throw new UnsupportedOperationException("No feature exists!");
    }

    /**
     * Placeholder reminder, replace with contains if possible
     */
    private int relationshipSearch(String relationshipName, String destinationName){

        for(int i = 0; i < relationshipList.size(); i++){
            if(relationshipList.get(i).getSourceName().equals(relationshipName) &&
                    relationshipList.get(i).getDestinationName().equals(destinationName)){
                return i;
            }
        }

        return -1;

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
