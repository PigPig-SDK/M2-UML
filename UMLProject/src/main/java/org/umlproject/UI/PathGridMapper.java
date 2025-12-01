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
    private final RelationshipRouter router = RelationshipRouter.getRouterInstance();
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
     * @param gridI, x coordinate for the center of the AStarNode being tested.
     * @param gridJ, y coordinate for the center of the AStarNode being tested.
     * @return a boolean value representing whether the AStarNode being tested is viable to move to or not.
     */
    public boolean isPassable(int gridI, int gridJ, UMLClass targetClass, UMLClass sourceClass){
        double testX = toPixelCoordinate(gridI) - (GRID_SIZE / 2);
        double testY = toPixelCoordinate(gridJ) - (GRID_SIZE / 2);
        Rectangle2D testTile = new Rectangle2D(testX, testY, GRID_SIZE, GRID_SIZE);
        List<GuiClass> guiClasses = GuiController.extractGuiClasses();
        for(GuiClass nextClass : guiClasses){
            //If nextClass == target class, don't return false, this is the goal.
            if(nextClass.getParentClass() == targetClass || nextClass.getParentClass() == sourceClass){
                continue;
            }
            Rectangle2D classBounds = nextClass.getRectBounds();

            //Apply padding to classBounds before checking for containment of testX and testY.
            double margin = GRID_SIZE * Math.sqrt(2)/2;
            double paddedMinX = classBounds.getMinX() - margin;
            double paddedMinY = classBounds.getMinY() - margin;
            double paddedWidth = classBounds.getWidth() + (2 * margin);
            double paddedHeight = classBounds.getHeight() + (2 * margin);
            //Perform containment check on padded rectangle.
            if(new Rectangle2D(paddedMinX, paddedMinY, paddedWidth, paddedHeight).intersects(testTile)){

                return false;
            }
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
        UMLClass sourceClass = sourceGui.getParentClass();
        List<AStarNode> perimeterNodes = new ArrayList<>();
        Rectangle2D sourceBounds = sourceGui.getRectBounds();
        Point2D targetCenter = target.getLocation();
        double buffer = PathGridMapper.GRID_SIZE * 1.5;
        //Define padded search area around source bounds.
        double searchMinX = sourceBounds.getMinX() - buffer;
        double searchMaxX = sourceBounds.getMaxX() + buffer;
        double searchMinY = sourceBounds.getMinY() - buffer;
        double searchMaxY = sourceBounds.getMaxY() + buffer;
        //
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

                //-------------------------------------------------------------
                //test using tiles
                Rectangle2D tile = new Rectangle2D(i * 20, j * 20, 20, 20);
                if(tile.intersects(sourceBounds)){
                    continue;
                }
                //----------------------------------------------------------

                //Check for obstacles.
                if(!this.isPassable(i, j, target, sourceClass)){
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

