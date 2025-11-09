package org.umlproject;


public class DocumentMementoListener implements DiagramElementListener, DocumentListner
{
    public static DocumentMementoListener singleton;
    
    public static void setupListener()
    {
        singleton = new DocumentMementoListener();
        UMLDiagramElement.globalListeners.add(singleton);
        UMLDocument.documentListners.add(singleton);
    }

    
    private static void onDocumentUpdated()
    {
        System.out.println("Update...");
    }
    
    @Override
    public void update(Object desiredElement) {
        onDocumentUpdated();
    }

    @Override
    public void updateLocation(Object desiredElement) {
        System.out.println("LOCATIONAL CHANGE");
    }
    
    @Override
    public void onClassRemove(UMLClass umlClass) {
        onDocumentUpdated();
    }

    @Override
    public void onRelationshipRemove(UMLRelationship umlClass) {
        onDocumentUpdated();
    }

    @Override
    public void onClassAdded(UMLClass umlClass, boolean isLoading) {
        onDocumentUpdated();
    }

    @Override
    public void onRelationshipAdded(UMLRelationship umlRelationship, boolean isLoading) {
        onDocumentUpdated();
    }
    
    @Override
    public void cleanUp() {
        //Do nothing...
    }
    
    @Override
    public void loadFile(UMLDocument umlDocument) {
        //Do nothing
    }
}
