package org.umlproject.UI;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import org.umlproject.UIListener;
import org.umlproject.UISelectable;
import org.umlproject.UMLRelationship;

public class GuiRelationship implements UIListener<UMLRelationship>, UISelectable{

    public GuiRelationship(Group world, UMLRelationship umlRelationship)
    {
        //Bind our UI elements to 'world'
    }
    
    @Override
    public void update(UMLRelationship desiredElement) {
    }

    @Override
    public void updateSelected(UMLRelationship desiredElement) {
    }

    @Override
    public void updateLocation(UMLRelationship desiredElement) {
    }

    @Override
    public void cleanUp() {
    }

    @Override
    public void setSelected(boolean isSelected) {
    }

    @Override
    public boolean getSelected() {
        return false; //TODO: Implement
    }

    @Override
    public boolean contains(Point2D selectionPoint) {
        return false; //TODO: Implement
    }

    @Override
    public boolean intersects(Rectangle2D selectionRectangle) {
        return false; //TODO: Implement
    }
    
}
