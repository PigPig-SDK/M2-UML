package org.umlproject;

public interface UIListener<T> {  
    /** 
     * The GUI listener should catch the update and draw its required bullshit.
     */
    void update(T desiredElement);
    /**
     * Tells the GUIListener they should only update the 'selected' state.
     */
    void updateSelected(T desiredElement);
    /**
     * Tells the GUIListener to only update the 'location' of its drawn elements.
     */
    void updateLocation(T desiredElement);
    /**
     * When this is called, our object removes all leftover UI elements.
     * Called when the bound Object is no longer valid.
     */
    void cleanUp();
}
