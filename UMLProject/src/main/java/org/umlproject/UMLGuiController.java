package org.umlproject;

/**
 * Interface for controlling and updating the UML GUI.
 * Implementations of this interface handle adding, removing, 
 * and updating UML classes and relationships in the graphical view.
 * 
 */
public interface UMLGuiController {
    /**
     * Suggests the GUI controller to create and bind a UMLObjectUpdateListener
     * @param umlClass we should bind to
     */
    void addClass(UMLClass umlClass);
    /**
     * Suggests the GUI controller to create and bind a UMLObjectUpdateListener
     * @param umlRelationship we should bind to
     */
    void addRelationship(UMLRelationship umlRelationship);
    /**
     * @param umlDocument Calls addClass() and addRelationship() on all the umlDocuments.
     */
    void redrawScreen(UMLDocument umlDocument);
}

