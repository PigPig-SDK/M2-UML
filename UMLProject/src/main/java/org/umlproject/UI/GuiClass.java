package org.umlproject.UI;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import org.umlproject.UIListener;
import org.umlproject.UIPositional;
import org.umlproject.UISelectable;
import org.umlproject.UMLClass;

public class GuiClass implements UIListener<UMLClass>, UISelectable, UIPositional {
    
    public GuiClass(Group world, UMLClass parentClass)
    {
        //Bind our UI elements to 'world'
    }
    
    @Override
    public void update(UMLClass desiredElement) {
    }

    @Override
    public void updateSelected(UMLClass desiredElement) {
    }

    @Override
    public void updateLocation(UMLClass desiredElement) {
    }

    @Override
    public void cleanUp() {
    }

    @Override
    public void setSelected(boolean isSelected) {
        
    }

    @Override
    public boolean getSelected() {
        return false; //TODO: Implement.
    }

    @Override
    public boolean contains(Point2D selectionPoint) {
        return false;
    }

    @Override
    public boolean intersects(Rectangle2D selectionRectangle) {
        return false;
    }

    @Override
    public Point2D getLocation() {
        return Point2D.ZERO;//TODO: Implement
    }

    @Override
    public void setLocation(Point2D location) {
    }
}
