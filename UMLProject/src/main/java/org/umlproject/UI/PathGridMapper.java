package org.umlproject.UI;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is responsible for converting the continuous JavaFX coordinates to discrete grid corodinates and
 * vice versa. It will also be responsible for obstacle checking and generating the perimeter AStarNodes that will
 * be used to initialize the open set for the AStar algorithm.
 */
public class PathGridMapper {

    //Grid cells should be 20 x 20. Use this in conversion calculations.
    private static final double GRID_SIZE = 20.0;
    private final UMLDocument doc;
    //Padding around a given class box.
    private final double padding;

    public PathGridMapper(UMLDocument doc, double padding){
        this.doc = doc;
        this.padding = padding;
    }

    /**
     * Static method for converting the continuous coordinates of JavaFX into
     * the discrete integer coordinates of an individual grid cell.
     * @param coordinate is a continuo0us JavaFX coordinate.
     * @return a discrete grid cell index.
     */
    public static int toGridIndex(double coordinate){
        int index = (int)Math.floor(coordinate / GRID_SIZE);
        return index;
    }

    /**
     * This method is responsible for reverting grid coordinates back into JavaFX
     * continuous Coordinates. It maps back to the continuous center of a grid cell.
     * @param gridIndex is a discrete integer grid cell index.
     * @return a continuous coordinate.
     */
    public static double toPixelCoordinate(int gridIndex){
        double cellCenterCoord = (gridIndex * GRID_SIZE) + (GRID_SIZE/2);
        return cellCenterCoord;
    }

    /**
     * This method will check to see if the AStarNode (tile) described by the indices (gridI, gridJ) is contained
     * by any non-goal class boxes. If it is, return false, otherwise return true.
     * @param gridI, x grid coordinate of the AStarNode tile being tested.
     * @param gridJ, y coordinate of the of the AStarNode tile being tested.
     * @return a boolean value representing whether the AStarNode being tested is viable to move to or not.
     */
    public boolean isPassable(int gridI, int gridJ, UMLClass targetClass){
        double testX = toPixelCoordinate(gridI);
        double testY = toPixelCoordinate(gridJ);
        List<GuiClass> guiClasses = GuiController.extractGuiClasses();
        for(GuiClass nextClass : guiClasses){
            //If nextClass == target class, don't return false, this is the goal.

            if(nextClass.getParentClass() == targetClass){
                continue;
            }
            Rectangle2D classBounds = nextClass.getRectBounds();
            if(classBounds.contains(testX, testY)){
                return false;
            }
            /**
            //Apply padding to classBounds before checking for containment of testX and testY.
            double paddedMinX = classBounds.getMinX() - this.padding;
            double paddedMinY = classBounds.getMinY() - this.padding;
            double paddedWidth = classBounds.getWidth() + (2 * this.padding);
            double paddedHeight = classBounds.getHeight() + (2 * this.padding);
            //Perform containment check on padded rectangle.
            if(new Rectangle2D(paddedMinX, paddedMinY, paddedWidth, paddedHeight).contains(testX, testY)){
                return false;
            }
             */

        }
        return true;
    }

    /**
     * USED AI to build this Method.
     * This method is responsible for creating AStarNodes for every tile along the perimeter of the source
     * class box. These nodes will be used to initialize the open set so that we can begin the A star search. Pad the initial
     * source class box bounds with grid tiles of size 1.5 (to ensure there is no overlap with class box,
     * then scan each tile within this padded rectangle to extract perimeter AStarNodes.
     * @param sourceGui, starting class box
     * @param target, target class box
     * @return
     */
    public List<AStarNode> getInitialPerimeterNodes(GuiClass sourceGui, UMLClass target) {
        List<AStarNode> perimeterNodes = new ArrayList<>();
        Rectangle2D sourceBounds = sourceGui.getRectBounds();
        Point2D targetCenter = target.getLocation();
        double buffer = PathGridMapper.GRID_SIZE;
        //Define padded search area around source bounds.
        double searchMinX = sourceBounds.getMinX() - buffer;
        double searchMaxX = sourceBounds.getMaxX() + buffer;
        double searchMinY = sourceBounds.getMinY() - buffer;
        double searchMaxY = sourceBounds.getMaxY() + buffer;
        //Convert continuous coordinate boundaries into discrete grid indices.
        int minI = PathGridMapper.toGridIndex(searchMinX);
        int maxI = PathGridMapper.toGridIndex(searchMaxX);
        int minJ = PathGridMapper.toGridIndex(searchMinY);
        int maxJ = PathGridMapper.toGridIndex(searchMaxY);
        //Cycle through the discrete grid tile indices and check to see which tiles are along the perimeter
        //of the class box and which tiles are at least partially inside the class box. Fill the perimeterNodes list
        //only with those tiles along the perimeter that don't intersect the class box.
        for(int i = minI; i <= maxI; i++){
            for(int j = minJ; j <= maxJ; j++){
                double testX = PathGridMapper.toPixelCoordinate(i);
                double testY = PathGridMapper.toPixelCoordinate(j);
                //Check to see if test coordinates are outside the unpadded
                //source bounds.
                if(sourceBounds.contains(testX, testY)){
                    continue;
                }
                //Check for obstacles.
                if(!this.isPassable(i, j, target)){
                    continue;
                }
                //Node is valid so create AStarNode and insert into list.
                double xValue = targetCenter.getX() - testX;
                double yValue = targetCenter.getY() - testY;
                double hCost = Math.sqrt(xValue * xValue + yValue * yValue);
                AStarNode newNode = new AStarNode(i, j, 0.0, hCost, null);
                perimeterNodes.add(newNode);
            }
        }
        return perimeterNodes;
    }
}
