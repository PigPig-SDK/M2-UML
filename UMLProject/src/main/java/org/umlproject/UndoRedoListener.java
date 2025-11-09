package org.umlproject;


public class UndoRedoListener implements DiagramElementListener, DocumentListner
{
    
    private static UndoRedoListener instance;
    /**
     * Get the memento of instance listener.
     * NOTE: This can be null! That is because setupListener() is expected to be called.
     */
    public UndoRedoListener getInstance()
    {
        //Null is an intended return value.
        return instance;
    }
    /**
     * Setup and bind the listener.
     */
    public static void setupListener()
    {
        instance = new UndoRedoListener();
        UMLDiagramElement.globalListeners.add(instance);
        UMLDocument.documentListners.add(instance);
    }
    /**
     * Called whenever the document is updated.
     */
    private static void onDocumentUpdated(String from)
    {
        if(UMLDocument.getDocumentState().equals(DocumentState.FILE_LOADING))
            return;
        UMLDocument.saveMementoState();
        System.out.println("SAVE STATE" + from);
    }
    
    /*---------------------------[ Listeners ]---------------------------*/
    @Override public void update(Object desiredElement) {
        onDocumentUpdated("objUpdate");
    }

    @Override public void updateLocation(Object desiredElement) {
        System.out.println("LOCATIONAL CHANGE");
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
