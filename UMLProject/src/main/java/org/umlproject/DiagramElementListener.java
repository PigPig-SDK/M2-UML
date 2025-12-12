package org.umlproject;

import javafx.geometry.Rectangle2D;

public interface DiagramElementListener<T> {  
    /** 
     * The GUI listener should catch the update and draw its required bullshit.
     */
    void update(T desiredElement);
    /**
     * Tells the GUIListener to only update the 'location' of its drawn elements.
     */
    void updateTranslation(T desiredElement);
    /**
     * When this is called, our object removes all leftover UI elements.
     * Called when the bound Object is no longer valid.
     */
    void cleanUp();

}
