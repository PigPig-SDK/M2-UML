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
     */
    void onClassAdded(UMLClass umlClass);
    /**
     * @param umlRelationship that was added
     */
    void onRelationshipAdded(UMLRelationship umlRelationship);
    /**
     * @param umlDocument When a UMLDocument is loaded, this is called.
     */
    void loadFile(UMLDocument umlDocument);
}

