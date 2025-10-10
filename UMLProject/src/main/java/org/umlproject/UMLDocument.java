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

    /**
     * Deletes a class from classSet, given the class exists. Removes all class relationships before deletion.
     * Returns false if class does not exist.
     *
     * @param className The class to be removed.
     *
     * @return boolean - True if the class was successfully deleted
     * */
    public boolean deleteClass(String className)
    {

        if (getClass(className) == null){
            return false;
        }
        removeClassKeyFromRelationships(className);
        classSet.remove(className);
        return true;

    }
    /**
     * Renames a class in classSet and relationshipList. First stores the relevant data of the old class name,
     * removes it, and adds a new class with corresponding data and updated name.
     *
     * @param originClassName The name to be changed
     * @param newName The replacement name
     *
     * @return boolean - True if the rename was successful, false if class does not exist in relationship list or
     * class set, or if the newName already exists in class set or relationship list
     * */
    public boolean renameClass(String originClassName, String newName)
    {
        if(getAllRelationships(originClassName) == null){
            return false;
        }

        ArrayList<UMLRelationship> tempRelationships = getAllRelationships(originClassName);

        if(classSet.containsKey(newName) || relationshipList.containsKey(newName) ||
                getAllRelationships(originClassName) == null || !deleteClass(originClassName)){
            return false;
        }

        //placeholder line until rename method is added to UMLClass
        classSet.put(newName, new UMLClass(newName));
        relationshipList.put(newName, tempRelationships);

        return true;
    }
    /**
     * Adds a relationship to the file
     *
     * @param className The source class
     * @param  destinationName The destination of the class.
     *
     * @return boolean - True if the class was added
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
     *
     * @param className The classname to check against
     * @param  destinationName the destination to check against
     *
     * @return boolean - Returns true if relationship exists
     * */
    public boolean hasRelationship(String className, String destinationName) {
        return getRelationship(className, destinationName) != null;
    }
    /**
     * Removes a relationship from a given class in the master relationship list
     *
     * @param className The classname we search with
     * @param destinationName The destination of the class we are searching for
     *
     * @return boolean - Returns true if relationship is successfully removed
     * */
    public boolean removeRelationship(String className, String destinationName){

        if(!relationshipList.containsKey(className))
            return false;
        int index = getRelationshipIndex(className, destinationName);
        if(index == -1)
            return false;
        relationshipList.get(className).remove(index);
        return true;
    }
    /**
     * Removes a class key from the relationship list
     *
     * @param className The class which relationships will be 'cleared out'
     *
     * @return boolean - Returns true key is successfully removed
     * */
    public boolean removeClassKeyFromRelationships(String className)
    {
        if(!relationshipList.containsKey(className))
            return false;
        relationshipList.remove(className);
        return true;
    }
    /**
     * Helper function. Iterates through the given class name provided in the map, searching for relationships
     * that have a matching className and destinationName, and returns any relationship found.
     *
     * @param  className The source of the relationship
     * @param destinationName The destination of the relationship
     *
     * @return UMLRelationship - NULL if no relationship exists.
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
     * Returns the list of relationships belonging to a given class in the master relationship list
     *
     * @param className The source checked
     *
     * @return ArrayList - An ArrayList containing all relationships belonging to the given class,
     * null if class name is not found
     *  */
    public ArrayList<UMLRelationship> getAllRelationships(String className)
    {
        if(!relationshipList.containsKey(className))
            return null;
        return relationshipList.get(className);
    }

    /**
     * Helper function. Iterates through the given class name provided in the map, searching for relationships
     * that have a matching className and destinationName, and returns a matching index if a relationship is found.
     *
     * @param  className The class relationships to search through
     * @param destinationName The relationships destination
     *
     * @return int - Representing the index returned. If the relationship is found, i >= 0. If not, -1 is returned,
     * representing that the index was not found.
     */
    private int getRelationshipIndex(String className, String destinationName){
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
     * Returns the desired UMLClass if it exists.
     *
     * @param className Name of class to get
     *
     * @return UMLClass - Returns class or null if class does not exist
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
     * Adds a class to the classSet map.
     *
     * @param className Name given to new class
     *
     * @return UMLClass - Returns created class, or null if class already exists
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
