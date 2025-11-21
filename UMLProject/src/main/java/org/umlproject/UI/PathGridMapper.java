package org.umlproject.UI;

import javafx.geometry.Rectangle2D;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is responsible for converting the continuous JavaFX coordinates to discrete grid corodinates and
 * vice versa. It will also be responsible for obstacle checking.
 */
public class PathGridMapper {

    //Grid cells should be 20 x 20. Use this conversion calculations.
    private static final double GRID_SIZE = 20.0;
    private static final double DIAGONAL_COST_MULTIPLIER = Math.sqrt(2.0);
    private final UMLDocument doc;
    //Padding around a given class box.
    private final double padding;

    public PathGridMapper(UMLDocument doc, double padding){
        this.doc = doc;
        this.padding = padding;
    }


    /**
     * Static method for converting the continous coordinates of JavaFX into
     * the discrete integer coordiantes of an individual grid cell.
     * @param coordinate is a continous JavaFX coordinate.
     * @return a discrete grid cell index.
     */
    public static int toGridIndex(double coordinate){
        int index = (int)Math.floor(coordinate / GRID_SIZE);
        return index;
    }

    /**
     * This method is responsible for reverting grid coordinates back into JavaFX
     * continuous Coordinates. It maps back to the continuous center of a grid cell.
     * @param gridIndex is a discrete integer gridcell index.
     * @return a continous coordinate.
     */
    public static double toPixelCoordinate(int gridIndex){
        double cellCenterCoord = (gridIndex * GRID_SIZE) + (GRID_SIZE/2);
        return cellCenterCoord;
    }


    /**
     * This method will check to see if the AStarNode whose center is given by (gridI, gridJ) is contained
     * by any non-goal class boxes. If it is, return false, otherwise return true.
     * @param gridI, x coordinate for the center of the AStarNode being tested.
     * @param gridJ, y coordinate for the center of the AStarNode being tested.
     * @return a boolean value representing whether the AStarNode being tested is viable to move to or not.
     */
    public boolean isPassable(int gridI, int gridJ, UMLClass targetClass){
        double testX = toPixelCoordinate(gridI);
        double testY = toPixelCoordinate(gridJ);
        List<GuiClass> guiClasses = GuiController.extractGuiClasses();
        for(GuiClass nextClass : guiClasses){
            if(nextClass == targetClass.getListener()){
                continue;
            }
            Rectangle2D classBounds = nextClass.getRectBounds();

            //Apply padding to classBounds before checking for containment of testX and testY.
            double paddedMinX = classBounds.getMinX() - this.padding;
            double paddedMinY = classBounds.getMinY() - this.padding;
            double paddedWidth = classBounds.getWidth() + (2 * this.padding);
            double paddedHeight = classBounds.getHeight() + (2 * this.padding);
            //Perform containment check on padded rectangle.
            if(new Rectangle2D(paddedMinX, paddedMinY, paddedWidth, paddedHeight).contains(testX, testY)){
                return false;
            }
        }
        return true;
    }


}
