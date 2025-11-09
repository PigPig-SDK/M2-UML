package org.umlproject;


public class DocumentMementoListener implements DiagramElementListener, DocumentListner
{
    private static DocumentMementoListener instance;
    
    public static void setupListener()
    {
        instance = new DocumentMementoListener();
        UMLDiagramElement.globalListeners.add(instance);
        UMLDocument.documentListners.add(instance);
    }

    
    private static void onDocumentUpdated(String from)
    {
        if(UMLDocument.getDocumentState().equals(DocumentState.FILE_LOADING))
            return;
        
        System.out.println("Update..." + from);
    }
    
    @Override
    public void update(Object desiredElement) {
        onDocumentUpdated("objUpdate");
    }

    @Override
    public void updateLocation(Object desiredElement) {
        System.out.println("LOCATIONAL CHANGE");
    }
    
    @Override
    public void onClassRemove(UMLClass umlClass) {
        onDocumentUpdated("classRemove");
    }

    @Override
    public void onRelationshipRemove(UMLRelationship umlClass) {
        onDocumentUpdated("relationship remove");
    }

    @Override
    public void onClassAdded(UMLClass umlClass, boolean isLoading) {
        onDocumentUpdated("class added");
    }

    @Override
    public void onRelationshipAdded(UMLRelationship umlRelationship, boolean isLoading) {
        onDocumentUpdated(" Relationship added");
    }
    
    @Override
    public void cleanUp() {
        //Do nothing...
    }
    
    @Override
    public void loadFile(UMLDocument umlDocument) {
        System.out.println("Loaded file");
    }
    
    public void saveMemento()
    {
        System.out.println("Memento save called");
    }
}
