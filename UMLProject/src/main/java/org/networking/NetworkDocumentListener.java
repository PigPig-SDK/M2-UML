package org.networking;

import java.util.Set;
import org.umlproject.DiagramElementListener;
import org.umlproject.DocumentListner;
import org.umlproject.DocumentState;
import org.umlproject.UMLClass;
import org.umlproject.UMLDiagramElement;
import org.umlproject.UMLDocument;
import org.umlproject.UMLRelationship;
import org.umlproject.UndoRedoManager;


public class NetworkDocumentListener implements DiagramElementListener, DocumentListner {

    private static final Set<DocumentState> invalidDocumentStates = 
            Set.of( DocumentState.FILE_LOADING, 
                    DocumentState.CLONING, 
                    DocumentState.MEMENTO_STATE_RESET);
    
    private static NetworkDocumentListener instance;
    /**
     * Get the memento of instance listener.
     * NOTE: This can be null! That is because setupListener() is expected to be called.
     */
    public NetworkDocumentListener getInstance()
    {
        //Null is an intended return value.
        return instance;
    }
    /**
     * Setup and bind the listener.
     */
    public static void setupListener()
    {
        if(instance != null)
            return;
        
        instance = new NetworkDocumentListener();
        UMLDiagramElement.globalListeners.add(instance);
        UMLDocument.documentListners.add(instance);
    }
    /**
     * Unbind the listener
     */
    public static void shutdownListener()
    {
        if(instance == null)
            return;
        UMLDiagramElement.globalListeners.remove(instance);
        UMLDocument.documentListners.remove(instance);
        instance = null;
    }
    
    /**
     * Sends the updated class to the server for validation
     */
    private void sendClassUpdate(UMLClass objectClass)
    {
        if(invalidDocumentStates.contains(UMLDocument.getDocumentState())) return;
        
        System.out.println("Update class : " + objectClass.getClassName());
    }
    /**
     * Sends the updated relationship to the server for validation
     */
    private void sendRelationshipUpdate(UMLRelationship objectLRelationship)
    {
        if(invalidDocumentStates.contains(UMLDocument.getDocumentState())) return;
        
        System.out.println("Update relationship : " + objectLRelationship.getSourceName());
    }
    
    /*---------------------------[ Listeners ]---------------------------*/
    @Override public void update(Object desiredElement) {
        switch(desiredElement)
        {
            case UMLClass umlClass -> sendClassUpdate(umlClass);
            case UMLRelationship umlRelationship -> sendRelationshipUpdate(umlRelationship);

            default ->
            {
                System.out.println("Got update from unknown source!");
            }
        }
    }
    @Override public void updateLocation(Object desiredElement) {
        switch(desiredElement)
        {
            case UMLClass umlClass -> sendClassUpdate(umlClass);
            default ->
            {
                System.out.println("Got location update from invalid source!");
            }
        }
    }
    @Override public void onClassAdded(UMLClass umlClass) { sendClassUpdate(umlClass); }
    @Override public void onRelationshipAdded(UMLRelationship umlRelationship) { sendRelationshipUpdate(umlRelationship); }
    
    @Override public void onClassRemove(UMLClass umlClass) {
        System.out.println("TODO: IMPLEMENT onClassRemove!");
    }
    @Override public void onRelationshipRemove(UMLRelationship umlClass) {
        System.out.println("TODO: IMPLEMENT onRelationshipRemove!");
    }
    /* Those no good do nothings */
    @Override public void cleanUp() {}//Do nothing!
    @Override public void loadFile(UMLDocument umlDocument) {}//Do nothing!
}
