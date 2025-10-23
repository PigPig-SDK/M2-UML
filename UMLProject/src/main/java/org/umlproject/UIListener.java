package org.umlproject;

public interface UIListener {  
    /** 
     * The GUI listener should catch the update and draw its required bullshit.
     */
    void update(UMLDiagramElement desiredElement);
    /**
     * Tells the GUIListener they should only update the 'selected' state.
     */
    void updateSelected(UMLDiagramElement desiredElement);
    /**
     * Tells the GUIListener to only update the 'location' of its drawn elements.
     */
    void updateLocation(UMLDiagramElement desiredElement);
    /**
     * When this is called, our object removes all leftover UI elements.
     * Called when the bound Object is no longer valid.
     */
    void cleanUp();
}
