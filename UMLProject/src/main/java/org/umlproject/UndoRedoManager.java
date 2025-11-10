package org.umlproject;

import java.util.HashSet;
import java.util.Set;


public class UndoRedoManager implements DiagramElementListener, DocumentListner
{
    
    private static final Set<DocumentState> invalidDocumentStates = Set.of(DocumentState.FILE_LOADING, DocumentState.MEMENTO_STATE_RESET, DocumentState.MASS_OPERATION);
    
    private static UndoRedoManager instance;
    /**
     * Get the memento of instance listener.
     * NOTE: This can be null! That is because setupListener() is expected to be called.
     */
    public UndoRedoManager getInstance()
    {
        //Null is an intended return value.
        return instance;
    }
    /**
     * Setup and bind the listener.
     */
    public static void setupListener()
    {
        instance = new UndoRedoManager();
        UMLDiagramElement.globalListeners.add(instance);
        UMLDocument.documentListners.add(instance);
    }
    /**
     * Called whenever the document is updated.
     */
    private static void onDocumentUpdated(String from)
    {
        if(invalidDocumentStates.contains(UMLDocument.getDocumentState()))
            return;
        
        UMLDocument.saveMementoState();
        System.out.println("SAVE STATE" + from);
    }
    
    /*---------------------------[ Listeners ]---------------------------*/
    @Override public void update(Object desiredElement) {
        onDocumentUpdated("objUpdate");
    }

    @Override public void updateLocation(Object desiredElement) {
        if(UMLDocument.getDocumentState() == DocumentState.SILENT_MOVEMENT)
            return;
        
        onDocumentUpdated("location");
    }
    
    @Override public void onClassRemove(UMLClass umlClass) {
        onDocumentUpdated("classRemove");
    }

    @Override public void onRelationshipRemove(UMLRelationship umlClass) {
        onDocumentUpdated("relationship remove");
    }

    @Override public void onClassAdded(UMLClass umlClass, boolean isLoading) {
        onDocumentUpdated("class added");
    }

    @Override public void onRelationshipAdded(UMLRelationship umlRelationship, boolean isLoading) {
        onDocumentUpdated(" Relationship added");
    }
    
    @Override public void cleanUp() {
        //Do nothing...
    }
    
    @Override public void loadFile(UMLDocument umlDocument) {
        System.out.println("Loaded file");
    }
}
