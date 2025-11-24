package org.umlproject.UI;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;

import java.util.*;


/**
 * AStar path finding logic goes here.
 */
public class RelationshipRouter {

    private static final double GRID_SIZE = 20.0;
    private static final double DIAGONAL_COST = GRID_SIZE * Math.sqrt(2.0);
    private static final double HORIZONTAL_COST = GRID_SIZE;
    private PriorityQueue<AStarNode> openSet;
    private HashSet<AStarNode> openSetFastLookup;
    private HashSet<AStarNode> closedSet;
    private final PathGridMapper mapper;

    public RelationshipRouter(){
        this.openSet = new PriorityQueue<>();
        this.closedSet = new HashSet<>();
        this.openSetFastLookup = new HashSet<>();
        //Use padding size of 20 pixels.
        this.mapper = new PathGridMapper(UMLDocument.getInstance(), 20.0);
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
     * Helper method to calculat the hCost of an AStarNode.
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
        List<Point2D> shortestPath = new ArrayList<>();
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
    public List<Point2D> AStarAlgorithm(UMLClass source, UMLClass target){
        GuiClass sourceGui = (GuiClass)source.getListener();
        GuiClass targetGui = (GuiClass)target.getListener();
        Rectangle2D targetBounds = targetGui.getRectBounds();
        List<AStarNode> nodes = mapper.getInitialPerimeterNodes(sourceGui, target);
        //initialize the openSet with the perimeter.
        openSet.addAll(nodes);
        openSetFastLookup.addAll(nodes);

        while(!openSet.isEmpty()){
            //While nextNode is not contained in target class box bounds, continue building path:
            AStarNode nextNode = openSet.poll();
            openSetFastLookup.remove(nextNode);
            closedSet.add(nextNode);
            //If nextNode is touching the target class box, generate the path and return it.
            if(inTargetBounds(nextNode, targetBounds)){
                return recalculatePath(nextNode);
            }

            //Generate neighbors of nextNode and insert into openSet (assuming they aren't in the closed set.
            for(int i = -1; i <= 1; i++){
                for(int j = -1; j <= 1; j++){
                    int neighborGridX = nextNode.gridX + i;
                    int neighborGridY = nextNode.getGridY() + j;
                    //Check the passability of the neighbor tile:
                    if(!mapper.isPassable(neighborGridX, neighborGridY, target)){
                        continue;
                    }
                    //Check if neighbor is in open or closed set:
                    double hCost = calculateHCost(neighborGridX, neighborGridY, target);
                    double additionalGCost = (i != 0 && j != 0) ? DIAGONAL_COST : HORIZONTAL_COST;
                    //--------------------------------------------------------------------------------
                    //Implement a penalty check for crossing a relationship line here:


                    //--------------------------------------------------------------------------------
                    double newGCost = nextNode.getGCost() + additionalGCost;
                    AStarNode neighbor = new AStarNode(neighborGridX, neighborGridY,newGCost, hCost, nextNode);

                    //Check if AStarNode is in the closedSet.
                    if(closedSet.contains(neighbor)){
                        continue;
                    }
                    //Check if AStarNode is already in openSet. If so, check to see if gCost is lower along current path.
                    //If so update gCost value of existingNode.
                    if(openSetFastLookup.contains(neighbor)){
                        AStarNode existingNode;
                        for(AStarNode queueNode : openSet){
                            if(neighbor.equals(queueNode)){
                                existingNode = queueNode;
                                //If new GCost is shorter, we need to update the existingNode's value.
                                if(existingNode.getGCost() > newGCost){
                                    existingNode.setGCost(newGCost);
                                    //If we update an existingNode, then we need to remove and re-add it to the openSet so
                                    //it can re-heapify and put the node in its proper order according to its value of f(n).
                                    openSet.remove(existingNode);
                                    openSet.add(existingNode);
                                }
                            }
                        }
                        //We don't want to add the neighbor again if it is already in the openSet.
                        continue;
                    }
                    //Neighbor can be placed in openSet and openSetFastLookup Hash set:
                    openSet.add(neighbor);
                    openSetFastLookup.add(neighbor);
                }
            }
        }
        //Otherwise no path could be found so return null
        return null;
    }
}
