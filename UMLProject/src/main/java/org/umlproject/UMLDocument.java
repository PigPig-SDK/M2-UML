package org.umlproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UMLDocument 
{
    private String fileLocation = null;
    private Map<String, UMLClass> classSet = new HashMap<>();
    private Map<String,ArrayList<UMLRelationship>> relationshipList = new HashMap<>();
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
    /**
     * Adds a relationship to the file
     * @param className The source class
     * @param  destinationName The destination of the class.
     * @return True if the class was added
     * */
    public boolean addRelationship(String className, String destinationName){
        if(!relationshipList.containsKey(className))
            relationshipList.put(className, new ArrayList<UMLRelationship>());
        if(hasRelationship(className,destinationName))
            return  false;
        relationshipList.get(className).add(new UMLRelationship(className, destinationName));
        return true;
    }
    /**
     * Checks to see if a specified classname, destination exists
     * @param className The classname to check against
     * @param  destinationName the destination to check against
     * */
    public boolean hasRelationship(String className, String destinationName) {
        return getRelationship(className, destinationName) != null;
    }
    /**
     * @param className The classname we search with
     * @param destinationName The destination of the class we are searching for
     * */
    public boolean removeRelationship(String className, String destinationName){

        if(!relationshipList.containsKey(className))
            return false;
        int index = relationshipSearch(className, destinationName);
        if(index == -1)
            return false;
        relationshipList.get(className).remove(index);
        return true;
    }
    /**
     * @param className The class which relationships will be 'cleared out'
     * */
    public boolean removeAllClassRelationships(String className)
    {
        if(!relationshipList.containsKey(className))
            return false;
        relationshipList.remove(className);
        return true;
    }
    /**
     * @param  className The source of the relationship
     * @param destinationName The destination of the relationship
     * @return NULL if no relationship exists.
     * */
    public UMLRelationship getRelationship(String className, String destinationName)
    {
        if(!relationshipList.containsKey(className))
            return null;//CANNOT EXIST!
        for(UMLRelationship relationship : relationshipList.get(className))
        {
            if(relationship.getSourceName().equals(className) && relationship.getDestinationName().equals(destinationName))
                return relationship;
        }
        return null;
    }
    /**
     * @param className The source checked
     * @return A list of
     *  */
    public ArrayList<UMLRelationship> getAllRelationships(String className)
    {
        if(!relationshipList.containsKey(className))
            return null;
        return relationshipList.get(className);
    }

    /**
     * @param  className The class relationships to search through
     * @param destinationName The relationships destination
     */
    private int relationshipSearch(String className, String destinationName){
        if(!relationshipList.containsKey(className))
            return -1;
        for(int i = 0; i < relationshipList.get(className).size(); i++)
        {
            if(relationshipList.get(className).get(i).getSourceName().equals(className)
                    && relationshipList.get(className).get(i).getDestinationName().equals(destinationName)){
                return i;
            }
        }
        return -1;
    }
    /***
     * @return Returns the desired UMLClass if it exists.
     */
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
    public UMLClass addClass(String className){
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
    public boolean equals(Object obj) {
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
