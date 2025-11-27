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
    private PriorityQueue<AStarNode> openSet;
    private HashMap<AStarNode, AStarNode> openSetFastLookupMap;
    private HashSet<AStarNode> closedSet;
    private HashSet<AStarNode> occupiedPathCells;
    private final PathGridMapper mapper;
    private static final double EXISTING_RELATIONSHIP_PENALTY = 1000.0;

    private static RelationshipRouter instance;
    /**
     * Constructor.
     */
    private RelationshipRouter(){
        this.openSet = new PriorityQueue<>();
        this.closedSet = new HashSet<>();
        this.openSetFastLookupMap = new HashMap<>();
        occupiedPathCells = new HashSet<>();
        //Use padding size of 20 pixels.
        this.mapper = new PathGridMapper(UMLDocument.getInstance(), 20.0);
    }

    public static RelationshipRouter getInstance(){
        if(instance == null){
            instance = new RelationshipRouter();
        }
        return instance;
    }

    /**
     * Helper method to determine if an AStarNode is within the bounds of the target class box.
     * @param nextNode, The AStarNode to be checked.
     * @param targetBounds, The bounds to be checked.
     * @return, a boolean representing whether or not the AStarNode is within the target bounds.
     */
    public boolean inTargetBounds(AStarNode nextNode, Rectangle2D targetBounds){
        double testX = PathGridMapper.toPixelCoordinate(nextNode.getGridX());
        double testY = PathGridMapper.toPixelCoordinate(nextNode.getGridY());
        if (targetBounds.contains(testX, testY)) {
            return true;
        }
        else{
            return false;
        }
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

                /**
                ArrayList<Point2D> nextPath = new ArrayList<>(guiRelationship.getPathPoints());

                if(nextPath == null || nextPath.isEmpty()){
                    continue;
                }
                 */
                relationshipPaths.add(guiRelationship.getPathPoints());
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
        //Convert to continuous coordinates:
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
    public ArrayList<Point2D> recalculatePath(AStarNode endNode){
        ArrayList<Point2D> shortestPath = new ArrayList<>();
        AStarNode nextNode = endNode;
        while(nextNode.getParent() != null){
            double continuousCoordX = PathGridMapper.toPixelCoordinate(nextNode.getGridX());
            double continuousCoordY = PathGridMapper.toPixelCoordinate(nextNode.getGridY());
            Point2D nextPoint = new Point2D(continuousCoordX, continuousCoordY);
            //Note points are being added in reverse order
            shortestPath.add(nextPoint);
            nextNode = nextNode.getParent();
        }
        return shortestPath;
    }

    /**
     * AStar algorithm used to determine the shortest path from the source class box to the target class box.
     * @param source, class box the path begins from.
     * @param target, class box where the path terminates.
     * @return, a list of points representing the path from the source to the target.
     */
    public ArrayList<Point2D> AStarAlgorithm(UMLClass source, UMLClass target){
        openSet.clear();
        openSetFastLookupMap.clear();
        closedSet.clear();
        GuiClass sourceGui = (GuiClass)source.getListener();
        GuiClass targetGui = (GuiClass)target.getListener();
        Rectangle2D targetBounds = targetGui.getRectBounds();
        //Retrieve the perimeter nodes around the source class box.
        List<AStarNode> nodes = mapper.getInitialPerimeterNodes(sourceGui, target);
        //initialize the openSet with the perimeter.
        openSet.addAll(nodes);
        //openSetFastLookupMap will provide O(1) lookup times when checking to see if a neighbor node is already in the
        //openSet.
        for(AStarNode node : nodes){
            openSetFastLookupMap.put(node, node);
        }

        while(!openSet.isEmpty()){
            //While nextNode is not contained in target class box bounds, continue building path:
            AStarNode nextNode = openSet.poll();
            openSetFastLookupMap.remove(nextNode);
            closedSet.add(nextNode);
            //If nextNode is touching the target class box, generate the path and return it.
            if(inTargetBounds(nextNode, targetBounds)){
                openSet.clear();
                openSetFastLookupMap.clear();
                ArrayList<Point2D> rawPath = recalculatePath(nextNode);
                Collections.reverse(rawPath);
                return smoothOrthogonalPath(rawPath);
            }

            //Generate neighbors of nextNode and insert into openSet (assuming they aren't in the closed set.
            for(int i = -1; i <= 1; i++){
                for(int j = -1; j <= 1; j++){
                    int neighborGridX = nextNode.getGridX() + i;
                    int neighborGridY = nextNode.getGridY() + j;
                    //Check the passability of the neighbor tile:
                    if(!mapper.isPassable(neighborGridX, neighborGridY, target)){
                        continue;
                    }
                    //Check if AStarNode is in the closedSet.
                    AStarNode neighborLookup = new AStarNode(neighborGridX, neighborGridY, 0, 0, null);
                    if(closedSet.contains(neighborLookup)){
                        continue;
                    }

                    //Check if neighbor crosses an existing relationship line:
                    double additionalGCost = (i != 0 && j != 0) ? DIAGONAL_COST : HORIZONTAL_COST;
                    if(isCrossingExistingRelationship(neighborLookup)){
                        additionalGCost += EXISTING_RELATIONSHIP_PENALTY;
                    }
                    //Generate a proper neighbor node with g and h costs.
                    double newGCost = nextNode.getGCost() + additionalGCost;
                    double hCost = calculateHCost(neighborGridX, neighborGridY, target);
                    AStarNode neighbor = new AStarNode(neighborGridX, neighborGridY,newGCost, hCost, nextNode);

                    //Check if AStarNode is already in openSet. If so, check to see if gCost is lower along current path.
                    //If so update gCost value of existingNode.
                    AStarNode existingNode = openSetFastLookupMap.get(neighbor);
                    /**
                    if(existingNode != null){
                        //If new GCost is shorter, we need to update the existingNode's value.
                        if(existingNode.getGCost() > newGCost){
                            existingNode.setGCost(newGCost);
                            existingNode.setParent(nextNode);
                            openSet.remove(existingNode);
                            openSet.add(existingNode);
                        }
                        //We don't want to add the neighbor again if it is already in the openSet.
                        continue;
                    }
                    else {
                        //Neighbor can be placed in openSet and openSetFastLookupMap HashMap:
                        openSet.add(neighbor);
                        openSetFastLookupMap.put(neighbor, neighbor);
                    }
                     */
                    if(existingNode == null){
                        openSet.add(neighbor);
                        openSetFastLookupMap.put(neighbor, neighbor);
                        System.out.println("number of nodes in openSet is: " + openSet.size());
                    }

                }
            }
        }
        //Otherwise no path could be found so return null
        return null;
    }

    //smoothing method
    private ArrayList<Point2D> smoothOrthogonalPath(ArrayList<Point2D> rawPath) {
        if (rawPath.size() <= 2) return rawPath;
        ArrayList<Point2D> smoothed = new ArrayList<>();
        smoothed.add(rawPath.get(0));
        for (int i = 1; i < rawPath.size() - 1; i++) {
            Point2D a = rawPath.get(i - 1);
            Point2D b = rawPath.get(i);
            Point2D c = rawPath.get(i + 1);
            boolean horizontal = Math.abs(a.getY() - b.getY()) < 1 && Math.abs(b.getY() - c.getY()) < 1;
            boolean vertical   = Math.abs(a.getX() - b.getX()) < 1 && Math.abs(b.getX() - c.getX()) < 1;
            if (!horizontal && !vertical) {
                smoothed.add(b);
            }
        }
        smoothed.add(rawPath.get(rawPath.size() - 1));
        return smoothed;
    }
}
