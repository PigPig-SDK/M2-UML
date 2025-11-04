package org.umlproject;

import java.util.UUID;

/**
 * Interface for controlling and updating the UML GUI.
 * Implementations of this interface handle adding, removing, 
 * and updating UML classes and relationships in the graphical view.
 * 
 */
public interface DocumentListner {
    /**
     * Suggests the GUI controller to create and bind a UMLObjectUpdateListener
     * @param umlClass that was removed
     */
    void onClassRemove(UMLClass umlClass);
    /**
     * @param umlClass that was removed
     */
    void onRelationshipRemove(UMLRelationship umlClass);
    /**
     * @param umlClass that was added
     * @param isLoading If the addition is due to the document reloading
     */
    void onClassAdded(UMLClass umlClass, boolean  isLoading);
    /**
     * @param umlRelationship that was added
     * @param isLoading If the addition is due to the document reloading
     */
    void onRelationshipAdded(UMLRelationship umlRelationship, boolean  isLoading);
    /**
     * @param umlDocument When a UMLDocument is loaded, this is called.
     */
    void loadFile(UMLDocument umlDocument);
}

