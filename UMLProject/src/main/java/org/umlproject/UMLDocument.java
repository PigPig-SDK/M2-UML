package org.umlproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;


public class UMLDocument
{
    private static UMLDocument instance;
    
    transient String fileLocation = null;
    private Map<String, UMLClass> classSet = new HashMap<>();
    private Map<String,ArrayList<UMLRelationship>> relationshipList = new HashMap<>();
    
    public static final String FILEEXTENT_STRING = ".json";
    private static final String DEFAULT_FILEDIRECTORY = "Documents" + File.separator + "NewUMLDocument";

    public static List<DocumentListner> documentListners = new ArrayList<>();

    
    /**
     * Used to return all selectable objects
     * To be used by a 'UMLGuiController' when requested
     * @return all UMLSelectables from both the classSet and relationshipList
     */
    public List<UIListener> getUIListeners()
    {
        List<UIListener> allListeners = new ArrayList();
        for(UMLClass umlclass : classSet.values())
        {
            if(umlclass == null || umlclass.listener == null) continue;
            allListeners.add(umlclass.listener);
        }
        for(ArrayList<UMLRelationship> allRealtionshipLists : relationshipList.values())
        {
            if(allRealtionshipLists == null) continue;
            for(UMLRelationship relationship : allRealtionshipLists)
            {
                if(relationship == null || relationship.listener == null) continue;
                allListeners.add(relationship.listener);
            }
        }
        return allListeners;
    }
    
    /**
     * Returns the current singleton, else creates it
     * @return The current singleton
     */
    public static synchronized UMLDocument getInstance()
    {
        if(instance == null)
        {
            setupInstance();
        }
        return instance;
    }
    public static synchronized UMLDocument setupInstance()
    {
        return instance = new UMLDocument(DEFAULT_FILEDIRECTORY);
    }
    /**
     * Creates a new UMLDocument
     * 
     * @param fileLocation The file location it should be saved. Note: Do not include the file extension!
     */
    public UMLDocument(String fileLocation)
    {
        if(fileLocation == null || fileLocation.isEmpty())
        {
            this.fileLocation = DEFAULT_FILEDIRECTORY;
        }
        else
        {
            this.fileLocation = fileLocation;
        }
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
     * @param removeRelationships Decides if the relationships should be removed.
     *
     * @return boolean - True if the class was successfully deleted
     * */
    public UMLClass removeClass(String className, boolean removeRelationships)
    {
        UMLClass removed = getClass(className);
        if (removed == null) return null;
        
        if(removeRelationships)
        {
            removeClassKeyFromRelationships(className);
            documentListners.forEach(o->o.onClassRemove(removed));
        }
        classSet.remove(className);
        return removed;
    }
    public UMLClass removeClass(String className)
    {
        return removeClass(className,true);
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
        Objects.requireNonNull(originClassName, "originClassName cannot be null");
        Objects.requireNonNull(newName, "newName cannot be null");
        
        originClassName = originClassName.replaceAll("\\s+", "");//Remove spaces
        newName = newName.replaceAll("\\s+", "");//Remove spaces
        //Ensure the newname location isnt taken.
        if(classSet.containsKey(newName) || relationshipList.containsKey(newName)) return false;
        
        //add class Car
        //add relationship car dest awre
        
        
        //Validation
        ArrayList<UMLRelationship> tempRelationshipsPointer = getAllRelationships(originClassName);
        if (tempRelationshipsPointer == null) return false;//Impossible state unless originClassName DNE
        ArrayList<UMLRelationship> tempRelationships = new ArrayList<>(tempRelationshipsPointer);//I fear that java will delete tempRelationshipsPointer when the class is deleted. 
        
        UMLClass removedClass = removeClass(originClassName, false);
        //Full send. Cannot retract at any point past here.
        if(removedClass == null) return false;//Check incase something weird has happened.
        relationshipList.remove(originClassName);//Kill duplicate.
        
        //Rename outgoing relationships
        for(UMLRelationship relationship : tempRelationships)
        {
            relationship.setSourceName(newName);
        }
        //Rename destination relationships
        for(String classString : relationshipList.keySet())
        {
            if(relationshipList.get(classString) == null) continue;
            for(UMLRelationship relationship : relationshipList.get(classString))
            {
                if(relationship.getDestinationName().trim().equals(originClassName.trim()))
                {
                    relationship.setDestinationName(newName);//Replace with new name.
                }
            }
        }

        removedClass.setClassName(newName);
        classSet.put(newName, removedClass);
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
    public boolean addRelationship(String className, String destinationName, String relationshipTypeString){
        if(!classSet.containsKey(className) && !classSet.containsKey(destinationName))
            return false;
        if(Objects.equals(className,destinationName))//Cannot have same source/destination.
            return false;
        if(!relationshipList.containsKey(className))
            relationshipList.put(className, new ArrayList<UMLRelationship>());
        
        if(hasRelationship(className,destinationName))
            return  false;
        RelationshipType relationshipType = RelationshipType.stringToRelationshipType(relationshipTypeString);
        UMLRelationship relationship = new UMLRelationship(className, destinationName, relationshipType, (relationshipType == RelationshipType.OTHER) ? relationshipTypeString : null);
        relationshipList.get(className).add(relationship);
        documentListners.forEach(o->o.onRelationshipAdded(relationship,false));
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
        UMLRelationship relationship = relationshipList.get(className).remove(index);
        documentListners.forEach(o->o.onRelationshipRemove(relationship));
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
        ArrayList<UMLRelationship> myRelationships = relationshipList.get(className);
        for(UMLRelationship relationship : myRelationships)
        {
            documentListners.forEach(o->o.onRelationshipRemove(relationship));
        }
        relationshipList.remove(className);
        //need to ensure className is removed as a destination value in all other relationships
        for(String source : new ArrayList<>(relationshipList.keySet())){
            ArrayList<UMLRelationship> relationships = relationshipList.get(source);
            if(relationships != null){
                for(int i = relationships.size() - 1; i >= 0; i--)
                {
                    if(!relationships.get(i).getDestinationName().equals(className))
                        continue;
                    UMLRelationship relationship = relationships.get(i);
                    documentListners.forEach(o->o.onRelationshipRemove(relationship));
                    relationships.remove(i);
                }
            }
        }
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
     * Returns the list of relationships belonging to a given class in the master relationship list
     *
     * @param className The source checked
     *
     * @return ArrayList - An ArrayList containing all relationships belonging to the given class,
     * null if class name is not found
     *  */
    public ArrayList<UMLRelationship> getAllRelationshipsInstanceOf(String className)
    {
        ArrayList<UMLRelationship> list = new ArrayList();
        if(relationshipList.containsKey(className))
            list.addAll(relationshipList.get(className));
        
        for(ArrayList<UMLRelationship> tempList : relationshipList.values())
        {
            for(UMLRelationship relationship : tempList)
            {
                if(relationship.getDestinationName().equals(className))
                    list.add(relationship);
            }
        }
        return list;
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
    /**
     * Gets if the file location is valid.
     */
    public boolean isFileLocationValid()
    {
        if (this.fileLocation == null || this.fileLocation.trim().isEmpty()) {
            return false;
        }
        Path path = Paths.get(this.fileLocation + FILEEXTENT_STRING);
        return Files.exists(path);
    }
    /**
     * Calls save on the
     * @return True if the save was preformed
     */
    public boolean save()
    {
        return save(this.getFileLocation());
    }
    public boolean save(String filename)
    {
        if(fileLocation == null)
            return false;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(this);
        try (FileWriter writer = new FileWriter(filename + FILEEXTENT_STRING)) {
            writer.write(jsonString);
        } 
        catch (IOException e) {
            return false;
        }
        this.fileLocation = filename;
        return true;
    }
    
    public boolean quickLoad()
    {
        return load(this.getFileLocation());
    }
    public boolean load(String filename)
    {
        
        Gson gson = new Gson();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename + FILEEXTENT_STRING))) {
            // Deserialize the JSON into your Java object
            var data = gson.fromJson(reader, UMLDocument.class);
            //Successful loading, before we update our information, clear all GUI listeners.
            cleanUpAllGuiListeners();
            this.classSet=data.classSet;
            this.fileLocation=data.fileLocation;
            this.relationshipList=data.relationshipList;
        } 
        catch (IOException e) {
            return false;
        }
        this.fileLocation=filename;
        //Send new information to our GUIListener...
        suggestGuiControllerRedraw();
        return true;
    }
    /**
     * Rebinds every UMLClass,UMLRelationship... so on ... with the guiController.
     * 
     * Useful for when a UMLDocument is switched out.
     */
    private void suggestGuiControllerRedraw()
    {
        for(UMLClass umlc : classSet.values()) {
            documentListners.forEach(o-> o.onClassAdded(umlc, true));
        }
        for(ArrayList<UMLRelationship> relationshipList : relationshipList.values())
        {
            for(UMLRelationship umlr : relationshipList){
                documentListners.forEach(o -> o.onRelationshipAdded(umlr, true));
            }
        }
        documentListners.forEach(o -> o.loadFile(this));
    }
    /**
     * Clears out the current file.
     * Used for a quick reset
     */
    public void clearFile()
    {
        cleanUpAllGuiListeners();
        classSet.clear();
        relationshipList.clear();
    }
    private void cleanUpAllGuiListeners()
    {
        List<UIListener> allListeners = getUIListeners();
        for(UIListener listener : allListeners)
        {
            listener.cleanUp();
        }
    }
    /**
     * Adds a class to the classSet map.
     *
     * @param className Name given to new class
     *
     * @return UMLClass - Returns created class, or null if class already exists
     */
    public UMLClass addClass(String className){
        
        Objects.requireNonNull(className, "newName cannot be null");
        className = className.replaceAll("\\s+", "");//Remove spaces
        
        if (classSet.containsKey(className)) return null;
        
        UMLClass umlclass = new UMLClass(className);

        classSet.put(className, umlclass);
        ArrayList<UMLRelationship> newList = new ArrayList<>();
        relationshipList.put(className, newList);
        documentListners.forEach(o -> o.onClassAdded(umlclass, false));
        return umlclass;
    }

    /**
     * @return number of classes added
     */
    public int getClassCount()
    {
        return classSet.size();
    }

    /**
     * Getter method for classSet
     *
     * @return Map - classSet
     */
    public Map<String, UMLClass> getClassSet() {
        return this.classSet;
    }

    /**
     * Getter method for relationshipList
     *
     * @return Map - relationshipList
     */
    public Map<String, ArrayList<UMLRelationship>> getRelationshipList() {
        return this.relationshipList;
    }

    /**
     * Returns the first available spot for a new class when added through GUI
     *
     * @return String - The first available name
     */
    public String findValidDummyName()
    {
        final String dummyName = "NewClass";
        int increment = 1;
        while(true)
        {
            String testName = dummyName + increment;
            if(!classSet.containsKey(testName))//Name is not taken
                return testName;
            increment++;
        }
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
        if(castedObject.fileLocation.equals(this.fileLocation)){
            if(castedObject.classSet.equals(this.classSet)){
                return castedObject.relationshipList.equals(this.relationshipList);
            }
            return false;
        }
        return false;
    }
    @Override
    public int hashCode() {
        return this.fileLocation.hashCode();
    }

}
