package org.umlproject.UI;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.paint.Color;

/**
 * This class is responsible for converting the continuous JavaFX coordinates to discrete grid corodinates and
 * vice versa. It will also be responsible for obstacle checking and generating the perimeter AStarNodes that will
 * be used to initialize the open set for the AStar algorithm.
 */
public class PathGridMapper {

    //Grid cells should be N x N. Use this in conversion calculations.
    public static final double GRID_SIZE = 20.0;
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

    /**This method will generate a list of grid tiles from the perimeter of the source class box.
     * We will use tiles that are inside the boundary of the class box, and flush against the boundary,
     * so that the relationship line appears attached to the class box after being drawn.
     * @param sourceGui, The gui Listener of the source UMLClass.
     * @param target, the target UMLClass.
     * @param router, the RelationshipRouter instance we use to call calculateHCost().
     * @return a list of perimeter nodes.
     */
    public List<AStarSegment> getInitialPerimeterNodes(GuiClass sourceGui, UMLClass target, RelationshipRouter router) {

        UMLClass sourceClass = sourceGui.getParentClass();
        List<AStarSegment> perimeterNodes = new ArrayList<>();
        Rectangle2D sourceBounds = sourceGui.getRectBounds();
        Point2D targetCenter = target.getLocation();
         //Define padded search area around source bounds.
        double searchMinX = sourceBounds.getMinX();
        double searchMaxX = sourceBounds.getMaxX();
        double searchMinY = sourceBounds.getMinY();
        double searchMaxY = sourceBounds.getMaxY();

        int minI = PathGridMapper.toGridIndex(searchMinX);
        int maxI = PathGridMapper.toGridIndex(searchMaxX);
        int minJ = PathGridMapper.toGridIndex(searchMinY);
        int maxJ = PathGridMapper.toGridIndex(searchMaxY);
        //rewrite so we grab the perimeter just inside the class bounds:
        for(int i = minI; i <= maxI; i++){
            for(int j = minJ; j <= maxJ; j++){
                if(i == minI || i == maxI){
                    double hCost = router.calculateHCost(i, j, target);
                    AStarSegment nextPerimeterTile = new AStarSegment(i, j, 0.0, hCost, null);
                    perimeterNodes.add(nextPerimeterTile);
                }

            }
            if(i > minI && i < maxI){
                //Include j == minJ and j = maxJ tiles
                int minimumJ = minJ;
                int maximumJ = maxJ;
                double hCostMinJ = router.calculateHCost(i,minimumJ, target);
                double hCostMaxJ = router.calculateHCost(i, maximumJ, target);
                AStarSegment minJTile = new AStarSegment(i, minimumJ, 0.0, hCostMinJ, null);
                AStarSegment maxJTile = new AStarSegment(i, maximumJ, 0.0, hCostMaxJ, null);
                perimeterNodes.add(minJTile);
                perimeterNodes.add(maxJTile);
            }

        }
        //System.out.println("The size of perimeterNodes is: " + perimeterNodes.size());
//        for(AStarSegment ass : perimeterNodes)
//        {
//            GuiDebugging.drawLocationalDot(new Point2D(toPixelCoordinate(ass.gridX), toPixelCoordinate(ass.gridY)), 2.0, 10, Color.GREEN);
//        }
        return perimeterNodes;
 }

}

