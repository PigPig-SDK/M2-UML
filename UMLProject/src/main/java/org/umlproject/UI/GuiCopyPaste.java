package org.umlproject.UI;

import org.umlproject.*;

import java.util.*;

import javafx.geometry.Point2D;

public class GuiCopyPaste {

    private static GuiCopyPaste instance;

    private  static Map<String, String> oldToNewNameMap;

    private static ArrayList<UMLDiagramElement> copiedObjects;

    private Point2D oldCameraLocation;

    /**
     * GuiCopyPaste constructor. Initializes copiedObjects and oldToNewNameMap, only when getInstance is first called.
     */
    private GuiCopyPaste(){
        copiedObjects = new ArrayList<UMLDiagramElement>();
        oldToNewNameMap = new HashMap<>();
    }

    /**
     * Standard singletone getInstance method for GuiCopyPaste.
     */
    public static synchronized GuiCopyPaste getInstance()
    {
        if(instance == null)
        {
            instance = new GuiCopyPaste();
        }
        return instance;
    }

    /**
     * Copy method. Clears copiedObjects, assigns the camera position at the moment of copy, and fills copiedObjects
     * with deep copies of UISelectable elements that are currently selected.
     */
    public void copy(){
        //Clear currently copied objects
        copiedObjects.clear();
        //Terminal option
        if(Main.isInTerminalMode()){
            ArrayList<UMLDiagramElement> docElements = new ArrayList<>(UMLDocument.getInstance().getClassSet().values());
            for(ArrayList<UMLRelationship> classRelationships : UMLDocument.getInstance().getRelationshipList().values()){
                docElements.addAll(classRelationships);
            }
            for(UMLDiagramElement diagramElement : docElements){
                if(diagramElement instanceof UMLClass umlClass)
                {
                    UMLClass classCopy = umlClass.clone();
                    copiedObjects.add(classCopy);
                }
                if(diagramElement instanceof UMLRelationship umlRelationship){
                    copiedObjects.add(umlRelationship.clone());
                }
            }
            return;
        }
        //Save camera location
        oldCameraLocation = GuiCamera.getScreenCenter();
        //Clone all classes and relationships and add them to copiedObjects
        for(UISelectable selectable : GuiSelect.getInstance().getSelectedObjects()){
            if(selectable instanceof GuiClass guiClass)
            {
                UMLClass classCopy = guiClass.getParentClass().clone();
                copiedObjects.add(classCopy);
            }
            if(selectable instanceof GuiRelationship guiRelationship){
                copiedObjects.add(guiRelationship.getRelationship().clone());
            }
        }
    }

    /**
     * Paste method. Executes under Mass Operation so a memento is only saved once (called manually at the end of method).
     * The method first adds copies of all class objects, renaming them to name + -copy, repeating if necessary if the
     * new name is already taken. Sets the position of each class to be relative to the camera, determined by camera
     * position at the moment of copy. Then, the method creates deep copies of all relationships that have viable sources
     * and destinations, and also adds them.
     */
    public void paste(){

        UMLDocument.executeActionUnderState(DocumentState.MASS_OPERATION,()->{

            //Clears hashmap tool, then declares all relevant variables used in the method
            oldToNewNameMap.clear();
            UMLDocument doc = UMLDocument.getInstance();
            boolean errorClass;
            boolean errorRelationship;
            String source;
            String destination;
            String relationshipType;
            //Add all copied classes to the document, with relevant names
            for(UMLDiagramElement element : copiedObjects){

                if(element instanceof UMLClass umlClass)
                {
                    UMLClass classCopy = umlClass.clone();

                    int increment = 1;
                    while(UMLDocument.getInstance().getClassSet().containsKey(classCopy.getClassName() +
                            "-copy" + increment)){
                        increment++;
                    }
                    classCopy.setClassName(classCopy.getClassName() + "-copy" + increment);


                    if(!Main.isInTerminalMode()){
                        classCopy.setLocation(umlClass.getLocation().add(
                                GuiCamera.getScreenCenter().subtract(oldCameraLocation)), false);
                    }

                    errorClass = doc.addClass(classCopy);
                    if(!errorClass){
                        System.out.println("An error occurred printing a class");
                    }
                    oldToNewNameMap.put(umlClass.getClassName(), classCopy.getClassName());
                }
            }
            //Add all viable copied relationships to the document
            for(UMLDiagramElement element : copiedObjects){
                if (element instanceof UMLRelationship umlRelationship)
                {
                    source = umlRelationship.getSourceName();
                    destination = umlRelationship.getDestinationName();
                    relationshipType = umlRelationship.getRelationshipType().toString();

                    if(oldToNewNameMap.containsKey(source) &&
                            oldToNewNameMap.containsKey(destination)){

                        errorRelationship = doc.addRelationship(oldToNewNameMap.get(source),
                                oldToNewNameMap.get(destination), relationshipType);
                        if(!errorRelationship){
                            System.out.println("An error occurred printing a relationship");
                        }
                    }
                }
            }
        });
        //Update memento
        UMLDocument.saveMementoState();
    }

    public ArrayList<UMLDiagramElement> getCopiedObjects(){
        return copiedObjects;
    }


}
