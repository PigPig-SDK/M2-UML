package org.umlproject.UI;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.umlproject.DiagramElementListener;
import org.umlproject.UMLDocument;

import java.util.ArrayList;

public class GuiSelectDrag {

    private static Rectangle selectRectangle;

    private static Point2D initialMousePosition;

    private static final Pane world = GuiController.getInstance().getWorld();

    /**
     * select Drag method. Actively called when moused is dragged with ctrl being held. If no selection
     * rectangle exists, creates one. Resizes rectangle when dragged.
     *
     * @param event - Mouse drag event
     */
    public static void selectDrag(MouseEvent event){
        if(selectRectangle == null){
            newSelectRectangle(event);
        }

        Point2D temp = world.sceneToLocal(event.getX(), event.getY());

        //Used to actively reset rectangle position on negative x or y drags
        selectRectangle.setX(Math.min(temp.getX(), initialMousePosition.getX()));
        selectRectangle.setY(Math.min(temp.getY(), initialMousePosition.getY()));

        //Used to actively set width and bounds of rectangle
        selectRectangle.setWidth(Math.abs(temp.getX() - initialMousePosition.getX()));
        selectRectangle.setHeight(Math.abs(temp.getY() - initialMousePosition.getY()));
    }

    /**
     * selectDragRelease method. Called when mouse is un-clicked after a drag. Clears all selected elements,
     * and re-selects those that intersect with the selection drag.
     */
    public static void selectDragRelease(MouseEvent event){
        //Determines intersection/selection
        if(selectRectangle != null){
            //If right click, add to selection instead of reset
            if(!(event.getButton() == MouseButton.SECONDARY)){
                GuiSelect.getInstance().resetSelect();
            }
            //Iterates generalized UML elements to determine selection behaviour
            ArrayList<DiagramElementListener> guiElements = new ArrayList<>(UMLDocument.getInstance().getUIListeners());
            for(DiagramElementListener element  : guiElements){
                if(element instanceof UISelectable selectable){
                    if(selectable.intersects(new Rectangle2D(selectRectangle.getX(), selectRectangle.getY(),
                            selectRectangle.getWidth(), selectRectangle.getHeight()))){
                        GuiSelect.getInstance().selectUiElement( selectable);
                    }
                }
            }
            //Clear select rectangle
            world.getChildren().remove(selectRectangle);
            selectRectangle = null;
        }
    }

    /**
     * newSelectRectangle method. Called by selectDrag method. The method is a mini factory for creating a default
     * select drag rectangle.
     *
     * @param event - Mouse drag event.
     */
    private static void newSelectRectangle(MouseEvent event){
        selectRectangle = new Rectangle();
        world.getChildren().add(selectRectangle);
        //Transparent blue color for main body
        selectRectangle.setFill(Color.color(0, 0, 1, 0.2));
        //Full blue for border
        selectRectangle.setStroke(Color.BLUE);
        selectRectangle.setStrokeWidth(1);
        initialMousePosition = world.sceneToLocal(event.getX(), event.getY());
        selectRectangle.setX(initialMousePosition.getX());
        selectRectangle.setY(initialMousePosition.getY());
        selectRectangle.setVisible(true);
    }

}
