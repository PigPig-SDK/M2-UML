package org.umlproject.UI;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;
import org.umlproject.UMLRelationship;

import java.util.*;


/**
 * AStar path finding logic goes here.
 */
public class RelationshipRouter {

    private static final double GRID_SIZE = 50.0;
    private static final double DIAGONAL_COST = GRID_SIZE * Math.sqrt(2.0);
    private static final double HORIZONTAL_COST = GRID_SIZE;
    private PriorityQueue<AStarSegment> openSet;
    private HashMap<AStarSegment, AStarSegment> openSetFastLookupMap;
    private HashSet<AStarSegment> closedSet;
    private HashSet<AStarSegment> occupiedPathCells;
    private final PathGridMapper mapper;
    private static final double EXISTING_RELATIONSHIP_PENALTY = 1000.0;
    
    
    private static final Point2D[] DIRECTIONS = {
    new Point2D(0, -1),
    new Point2D(1, -1),
    new Point2D(1, 0),
    new Point2D(1, 1),
    new Point2D(0, 1),
    new Point2D(-1, 1),
    new Point2D(-1, 0),
    new Point2D(-1, -1)
};

    private static final RelationshipRouter router = new RelationshipRouter();

    /**
     * Constructor.
     */
    private RelationshipRouter(){
        this.openSet = new PriorityQueue<>();
        this.closedSet = new HashSet<>();
        this.openSetFastLookupMap = new HashMap<>();
        occupiedPathCells = new HashSet<>();
        //Use padding size of 20 pixels.
        this.mapper = new PathGridMapper(UMLDocument.getInstance(), 15.0);
    }
    public static RelationshipRouter getRouterInstance(){
        return router;
    }

    public HashSet<AStarSegment> getClosedSet(){
        return this.closedSet;
    }

    /**
     * Helper method to determine if an AStarNode is within the bounds of the target class box.
     * @param node, The AStarNode to be checked.
     * @param targetBounds, The bounds to be checked.
     * @return, a boolean representing whether or not the AStarNode is within the target bounds.
     */
    private boolean isGoalNode(AStarSegment node, Rectangle2D targetBounds) {
        double x = PathGridMapper.toPixelCoordinate(node.getGridX());
        double y = PathGridMapper.toPixelCoordinate(node.getGridY());

        // Accept any node whose center is within 1.5 grid cells of the target box
        Rectangle2D expanded = new Rectangle2D(
                targetBounds.getMinX() - 40,
                targetBounds.getMinY() - 40,
                targetBounds.getWidth() + 80,
                targetBounds.getHeight() + 80
        );
        return expanded.contains(x, y);
    }

    /**
     * This method uses Besenthal's algorithm to generate the minimum number of tiles needed to draw a straight line
     * segment between two points. This method is necessary because the smoothPath() method can reduce the actually number
     * of points on a polyLine path down to 2, just the points in contact with the source and target class boxes. This means
     * The entire drawn line would be completely open and passable to any other relationship line that wanted to cross it.
     * Besenthal's algorithm generates a rough, impenetrable skeleton we can use to prevent these crossings. Tiles would
     * still be able to cross at their corners though, so we need to apply further padding to this skeletal line to block
     * this crossable holes. This padding is done at the bottom of extractRelationshipPathPoints.
     * @param p1, point 1 of a given line segment.
     * @param p2, point 2 of a given line segment.
     * @return, a list of the minimum number of points needed to draw a straight line between p1 and p2.
     */
    public List<AStarSegment> getGridCellsCrossed(Point2D p1, Point2D p2) {
        List<AStarSegment> crossedCells = new ArrayList<>();

        // Convert to grid coordinates
        int x1 = PathGridMapper.toGridIndex(p1.getX());
        int y1 = PathGridMapper.toGridIndex(p1.getY());
        int x2 = PathGridMapper.toGridIndex(p2.getX());
        int y2 = PathGridMapper.toGridIndex(p2.getY());

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int currentX = x1;
        int currentY = y1;

        while (true) {
            AStarSegment node = new AStarSegment(currentX, currentY, 0, 0, null);
            // We can just add it, as the HashSet in the next step will handle duplicates.
            crossedCells.add(node);

            if (currentX == x2 && currentY == y2) {
                break;
            }

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                currentX += sx;
            }
            if (e2 < dx) {
                err += dx;
                currentY += sy;
            }
        }
        return crossedCells;
    }

    /**
     * Helper method to calculate the hCost of an AStarNode.
     * @param neighborGridX, X index of the AStar grid tile.
     * @param neighborGridY, Y index of the AStar grid tile.
     * @param target, target class
     * @return hCost
     */
    public double calculateHCost(int neighborGridX, int neighborGridY, UMLClass target){
        //Convert to continous coordinates:
        double continuousCoordX = PathGridMapper.toPixelCoordinate(neighborGridX);
        double continuousCoordY = PathGridMapper.toPixelCoordinate(neighborGridY);
        double targetX = target.getLocation().getX();
        double targetY = target.getLocation().getY();
        double hCost = Math.sqrt(Math.pow(continuousCoordX - targetX, 2) + Math.pow(continuousCoordY - targetY, 2));
        return hCost;
    }

    /**
     * Helper method used to reconstruct the shortest path starting from the endNode, i.e. the node that is in contact
     * with the target class box.
     * @param endNode, The node at the end of the shortest path.
     * @return, A List of Point2D objects representing the points along the relationship line.
     */
    public List<Point2D> recalculatePath(AStarSegment endNode){
        List<Point2D> path = new ArrayList<>();
        AStarSegment current = endNode;
        while (current != null) {
            double x = PathGridMapper.toPixelCoordinate(current.getGridX());
            double y = PathGridMapper.toPixelCoordinate(current.getGridY());
            path.add(new Point2D(x, y));
            current = current.getParent();
        }
        Collections.reverse(path);
        return path;
    }



    /**
     * AStar algorithm used to determine the shortest path from the source class box to the target class box.
     * @param source, class box the path begins from.
     * @param target, class box where the path terminates.
     * @return, a list of points representing the path from the source to the target.
     */
    public List<Point2D> AStarAlgorithm(UMLRelationship relationshipToExclude,UMLClass source, UMLClass target){
        GuiClass sourceGui = (GuiClass)source.getListener();
        GuiClass targetGui = (GuiClass)target.getListener();
        Rectangle2D targetBounds = targetGui.getRectBounds();

        openSet.clear();
        closedSet.clear();
        openSetFastLookupMap.clear();

        List<AStarSegment> nodes = mapper.getInitialPerimeterNodes(sourceGui, target, this);
        //initialize the openSet with the perimeter.
        openSet.addAll(nodes);
        for(AStarSegment node : nodes){
            openSetFastLookupMap.put(node, node);
        }
        while(!openSet.isEmpty()){
            //While nextNode is not contained in target class box bounds, continue building path:
            AStarSegment nextNode = openSet.poll();
            openSetFastLookupMap.remove(nextNode);
            closedSet.add(nextNode);
            //If nextNode is touching the target class box, generate the path and return it.
            if(isGoalNode(nextNode, targetBounds)){
                List<Point2D> path = recalculatePath(nextNode);
                Collections.reverse(path);

                if(path == null || path.size() < 2){
                    System.err.println("A* reached target, but path reconstruction failed. Likely source and target are right next to each other");
                    //return null;
                    continue; // fix the bug where class boxes get locked when touching in same relationship
                }
                return SmoothPathSolver.smoothPath(path);
            }
            //Generate neighbors of nextNode and insert into openSet (assuming they aren't in the closed set.
            for(Point2D dir : DIRECTIONS)
            {
                int neighborGridX = nextNode.gridX + (int)dir.getX();
                int neighborGridY = nextNode.getGridY() + (int)dir.getY();
                //Check the passability of the neighbor tile:
                if(!mapper.isPassable(neighborGridX, neighborGridY, target, source)){
                    continue;
                }
                //Check if neighbor is in open or closed set:
                double hCost = calculateHCost(neighborGridX, neighborGridY, target);
                double additionalGCost = (dir.getX() != 0 && dir.getY() != 0) ? DIAGONAL_COST : HORIZONTAL_COST;
                //Check to see if the neighbor intersects an existing relationship line.
                AStarSegment neighborLookup = new AStarSegment(neighborGridX, neighborGridY, 0, 0, null);
                if(this.occupiedPathCells.contains(neighborLookup)){
                    additionalGCost += EXISTING_RELATIONSHIP_PENALTY;
                }
                double newGCost = nextNode.getGCost() + additionalGCost;
                AStarSegment neighbor = new AStarSegment(neighborGridX, neighborGridY,newGCost, hCost, nextNode);

                //Check if AStarNode is in the closedSet. Note that contains() relies on the hashCode function
                //of AstarNode class to determine the right bucket, and equals() is used for actual comparison.
                if(closedSet.contains(neighbor)){
                    continue;
                }
                //Check if AStarNode is already in openSet. If so, check to see if gCost is lower along current path.
                //If so update gCost value of existingNode.
                AStarSegment existingNode = openSetFastLookupMap.get(neighbor);
                if(existingNode != null){
                    //If new GCost is shorter, we need to update the existingNode's value.
                    if(existingNode.getGCost() > newGCost){
                        existingNode.setGCost(newGCost);
                        existingNode.setParent(nextNode);
                        //re-heapify by removing and then re-adding existingNode.
                        openSet.remove(existingNode);
                        openSet.add(existingNode);
                    }
                    //We don't want to add the neighbor again if it is already in the openSet.
                    continue;
                }
                //Neighbor can be placed in openSet and openSetFastLookupMap HashMap:
                openSet.add(neighbor);
                openSetFastLookupMap.put(neighbor, neighbor);

            }
        }
        //Otherwise no path could be found so return null
        return null;
    }
}