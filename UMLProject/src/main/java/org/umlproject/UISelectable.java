package org.umlproject;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/**
 * UMLPositional is for storing GUI related information within each UMLClass, UMLRelationship, and so on...
 * 
 * This is kept within the UMLClass because it's inherently related to the 'model' we wish to preserve with GSON.
 * Classes that extend this will have their positional data stored in the saved JSON.
 */
public interface UISelectable {
    /**
     * Sets the selection status on the UMLSelectable.
     * @param isSelected
     */
    void setSelected(boolean isSelected);
    /**
     * Gets the selection status on the UMLSelectable.
     * @return the boolean for selectivity
     */
    boolean getSelected();
    /**
     * This is for detecting if a given point is within the 'selection' area of the element.
     * @param selectionPoint The testing selection space
     * @return True if the object is capable of being selected.
     */
    boolean contains(Point2D selectionPoint);
    /**
     * Checks to see if the object intersects a given selection rectangle
     * @param selectionRectangle
     * @return true if an intersection occurs.
     */
    boolean intersects(Rectangle2D selectionRectangle);
}
