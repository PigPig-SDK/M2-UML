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

    private static final double GRID_SIZE = 20.0;
    private static final double DIAGONAL_COST = GRID_SIZE * Math.sqrt(2.0);
    private static final double HORIZONTAL_COST = GRID_SIZE;
    private PriorityQueue<AStarNode> openSet;
    private HashMap<AStarNode, AStarNode> openSetFastLookupMap;
    private HashSet<AStarNode> closedSet;
    private HashSet<AStarNode> occupiedPathCells;
    private final PathGridMapper mapper;
    private static final double EXISTING_RELATIONSHIP_PENALTY = 100000000000000.0;

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

    public HashSet<AStarNode> getClosedSet(){
        return this.closedSet;
    }

    /**
     * Helper method to determine if an AStarNode is within the bounds of the target class box.
     * @param node, The AStarNode to be checked.
     * @param targetBounds, The bounds to be checked.
     * @return, a boolean representing whether or not the AStarNode is within the target bounds.
     */
    private boolean isGoalNode(AStarNode node, Rectangle2D targetBounds) {
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
     * This is a helper method to the isCrossingExistingRelationship method and will be used when checking to see if
     * a neighbor to an existing node occupies a tile that a relationship line crosses.
     * @return an ArrayList containing ArrayLists of Point2D objects representing points on a given relationship line.
     */
    public void extractRelationshipPoints(){
        occupiedPathCells.clear();
        ArrayList<ArrayList<Point2D>> relationshipPaths = new ArrayList<>();
        //We need to extract a list containing all the relationship paths between any two pair of classes with a relationship.
        Map<String, ArrayList<UMLRelationship>> relationshipsMap = UMLDocument.getInstance().getRelationshipList();
        Set<String> keys = relationshipsMap.keySet();
        //Note every key is a source class.
        ArrayList<String> relationshipKeys = new ArrayList<>(keys);
        ArrayList<ArrayList<UMLRelationship>> listOfRelationshipLists = new ArrayList<>();
        for(String key : relationshipKeys){
            listOfRelationshipLists.add(relationshipsMap.get(key));
        }
        //We now need to extract the lists of Point2D objects.
        for(ArrayList<UMLRelationship> existingRel : listOfRelationshipLists){
            //Extract GuiRelationship listener so we can then extract the list of Points associated with the listener.
            for(int i = 0; i < existingRel.size(); i++) {
                UMLRelationship nextRelationship = existingRel.get(i);
                //A relationship whose path is being constructed will not yet have a listener and so needs to be skipped.
                if(nextRelationship.getListener() == null){
                    continue;
                }
                GuiRelationship guiRelationship = (GuiRelationship)nextRelationship.getListener();

                ArrayList<Point2D> nextPath = new ArrayList<>(guiRelationship.getPathPoints());
                if(nextPath == null || nextPath.isEmpty()){
                    continue;
                }
                relationshipPaths.add(nextPath);
            }
        }
        for(int i = 0; i < relationshipPaths.size(); i++){
            for(Point2D nextPoint : relationshipPaths.get(i)){
                int gridX = PathGridMapper.toGridIndex(nextPoint.getX());
                int gridY = PathGridMapper.toGridIndex(nextPoint.getY());
                AStarNode occupiedTile = new AStarNode(gridX, gridY, 0.0, 0.0, null);
                occupiedPathCells.add(occupiedTile);
            }
        }
    }

    /**
     * Helper method for the AStar Algorithm method. It will check to see if the neighbor node under consideration
     * occupies the same grid tile as any of the existing relationship lines. If so, we will consider the neighbor
     * intractable for a new path and will return false. Otherwise return true.
     * @param neighbor, The new node to be processed.
     * @return, a boolean representing whether or not the neighbor node should be accepted as part of the path.
     */
    public boolean isCrossingExistingRelationship( AStarNode neighbor){
        return this.occupiedPathCells.contains(neighbor);
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
    public List<Point2D> recalculatePath(AStarNode endNode){
        List<Point2D> path = new ArrayList<>();
        AStarNode current = endNode;
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
    public List<Point2D> AStarAlgorithm(UMLClass source, UMLClass target){
        RelationshipRouter.getRouterInstance().extractRelationshipPoints();
        GuiClass sourceGui = (GuiClass)source.getListener();
        GuiClass targetGui = (GuiClass)target.getListener();
        Rectangle2D targetBounds = targetGui.getRectBounds();

        openSet.clear();
        closedSet.clear();
        openSetFastLookupMap.clear();
        System.out.println("the size of occupied is: " + occupiedPathCells.size());

        List<AStarNode> nodes = mapper.getInitialPerimeterNodes(sourceGui, target);
        //initialize the openSet with the perimeter.
        openSet.addAll(nodes);
        for(AStarNode node : nodes){
            openSetFastLookupMap.put(node, node);
        }

        //this.extractRelationshipPoints();
        while(!openSet.isEmpty()){
            //While nextNode is not contained in target class box bounds, continue building path:
            AStarNode nextNode = openSet.poll();
            openSetFastLookupMap.remove(nextNode);
            closedSet.add(nextNode);
            //If nextNode is touching the target class box, generate the path and return it.
            if(isGoalNode(nextNode, targetBounds)){
                List<Point2D> path = recalculatePath(nextNode);
                Collections.reverse(path);

                return smoothPath(path);
            }

            //Generate neighbors of nextNode and insert into openSet (assuming they aren't in the closed set.
            for(int i = -1; i <= 1; i++){
                for(int j = -1; j <= 1; j++){
                    int neighborGridX = nextNode.gridX + i;
                    int neighborGridY = nextNode.getGridY() + j;
                    //Check the passability of the neighbor tile:
                    if(!mapper.isPassable(neighborGridX, neighborGridY, target, source)){
                        System.out.println("node is in a non-target class box");
                        continue;
                    }
                    //Check if neighbor is in open or closed set:
                    double hCost = calculateHCost(neighborGridX, neighborGridY, target);
                    double additionalGCost = (i != 0 && j != 0) ? DIAGONAL_COST : HORIZONTAL_COST;
                    //Check to see if the neighbor intersects an existing relationship line.
                    AStarNode neighborLookup = new AStarNode(neighborGridX, neighborGridY, 0, 0, null);
                    if(isCrossingExistingRelationship(neighborLookup)){
                        additionalGCost += EXISTING_RELATIONSHIP_PENALTY;
                    }
                    double newGCost = nextNode.getGCost() + additionalGCost;
                    AStarNode neighbor = new AStarNode(neighborGridX, neighborGridY,newGCost, hCost, nextNode);

                    //Check if AStarNode is in the closedSet. Note that contains() relies on the hashCode function
                    //of AstarNode class to determine the right bucket, and equals() is used for actual comparison.
                    if(closedSet.contains(neighbor)){
                        continue;
                    }
                    //Check if AStarNode is already in openSet. If so, check to see if gCost is lower along current path.
                    //If so update gCost value of existingNode.
                    AStarNode existingNode = openSetFastLookupMap.get(neighbor);
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
        }
        //Otherwise no path could be found so return null
        return null;
    }

    public List<Point2D> smoothPath(List<Point2D> rawPath){
        if(rawPath == null || rawPath.size() < 2){
            throw new IllegalArgumentException("Raw Path is null or incorrect size for a path to be drawn.");
        }
        List<Point2D> smoothedPath = new ArrayList<>();
        List<GuiClass> guiClasses = GuiController.extractGuiClasses();
        smoothedPath.add(rawPath.get(0));
        Point2D lastAcceptedPoint = smoothedPath.get(0);
        Point2D safeFrontierPoint = smoothedPath.get(0);
        boolean intersectsRectangle = false;
        for(int i = 1; i < rawPath.size(); i++) {
            Point2D lookAheadPoint = rawPath.get(i);
            for (GuiClass nextClass : guiClasses) {
                Rectangle2D nextRectangle = nextClass.getRectBounds();
                intersectsRectangle = segmentIntersectsRectangle(lastAcceptedPoint, lookAheadPoint, nextRectangle);
                if(intersectsRectangle){
                    break;
                }
            }
            if(intersectsRectangle == true && i != 1){
                smoothedPath.add(safeFrontierPoint);
                lastAcceptedPoint = safeFrontierPoint;
            }
            safeFrontierPoint = lookAheadPoint;
        }
        //Add endpoint of rawPath.
        smoothedPath.add(rawPath.get(rawPath.size() - 1));
        return smoothedPath;
    }

    /**
     *
     * @param a
     * @param b
     * @param c
     * @return
     */
    public int orientation(Point2D a, Point2D b, Point2D c){
        //Cross product of the vectors ab and ac. These vectors span a parallelogram. The signed area of this parallelogram
        //indicates the orientation of these two vectors to each other. If the sign is negative, then c lies to the left of ab
        //if positive, then c lies to the right of ab.
        double val = (b.getX() - a.getX()) * (c.getY() - a.getY()) - (b.getY() - a.getY())*(c.getX() - a.getX());
        //floating point arithmetic considers numbers extremely close to zero to be equal to zero.
        double epsilon = 1e-9;
        // If val is 0, this means the vectors are colinear.
        if(Math.abs(val) < epsilon) return 0;
        //1 means c is to the right, and 2 means c is to the left of ab.
        return (val > 0) ? 1 : 2;

    }

    /**
     *
     * @param p1
     * @param q1
     * @param p2
     * @param q2
     * @return
     */
    private boolean segmentsIntersect(Point2D p1, Point2D q1, Point2D p2, Point2D q2){
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);
        //Check to see if the two segments intersect each other:
        if(o1 != o2 && o3 != o4){
            return true;
        }
        //Consider cases where the two segments are collinear:
        if(o1 == 0 && onSegment(p1, q1, p2))
            return true;
        if(o2 == 0 && onSegment(p1, q1, q2))
            return true;
        if(o3 == 0 && onSegment(p2, q2, p1))
            return true;
        if(o4 == 0 && onSegment(p2, q2, q1))
            return true;
        return false;
    }

    /**
     * This method is used for edge cases where two line segments are collinear. If two segments are collinear, then we
     * check to see if the segments overlap in anyway by looking to see if either of the end points of one segment lies on
     * the other segment.
     * @param a
     * @param b
     * @param c
     * @return
     */
    public boolean onSegment(Point2D a, Point2D b, Point2D c){
        //Let ab be the segment to be crossed.
        return c.getX() <= Math.max(a.getX(), b.getX()) &&
                c.getX() >= Math.min(a.getX(), b.getX()) &&
                c.getY() <= Math.max(a.getY(), b.getY()) &&
                c.getY() >= Math.min(a.getY(), b.getY());
    }

    /**
     *
     * @param a
     * @param b
     * @param rect
     * @return
     */
    public boolean segmentIntersectsRectangle(Point2D a, Point2D b, Rectangle2D rect){
        //We need to check and see if the segment ab intersects any of the
        //boundary edges of the given rectangle. To this end we generate
        //the four corner points of the rectangle from which we will form the edges.
        Point2D topLeft = new Point2D(rect.getMinX(), rect.getMinY());
        Point2D topRight = new Point2D(rect.getMaxX(), rect.getMinY());
        Point2D bottomLeft = new Point2D(rect.getMinX(), rect.getMaxY());
        Point2D bottomRight = new Point2D(rect.getMaxX(), rect.getMaxY());

        //First check to see if either a or b lies inside the rectangle. Note
        //contains() does not check for containment along the boundary.
        if(rect.contains(a) || rect.contains(b)){
            return true;
        }
        //Next check whether ab intersects any of the four edges of the rectangle.
        if(segmentsIntersect(a, b, topLeft, topRight)) {
            return true;
        }
        if(segmentsIntersect(a, b, topRight, bottomRight)){
            return true;
        }
        if(segmentsIntersect(a, b, bottomRight, bottomLeft)){
            return true;
        }
        if(segmentsIntersect(a, b, bottomLeft, topLeft)){
            return true;
        }
        return false;
    }

}

