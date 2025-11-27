package org.umlproject.UI;

import java.util.Objects;

/**
 * This class wil represent an individual grid tile to be used in the A* path finding algorithm.
 */
public class AStarNode implements Comparable<AStarNode>{
    //gridX and gridY will be the discrete coordinates of the top-left corner of a given tile.
    int gridX;
    int gridY;

    //f(x) = g(x) + h(x) is the function used by A* to sort nodes inside the open set Heap.
    //gCost and hCost reflect the values of g(x) and h(x) for a given AStarNode.
    double gCost;
    double hCost;

    //The parent AStarNode to be used for reconstructing the final path.
    AStarNode parent;

    public AStarNode(int gridX, int gridY, double gCost, double hCost, AStarNode parent){
        this.gridX = gridX;
        this.gridY = gridY;
        this.gCost = gCost;
        this.hCost = hCost;
        this.parent = parent;
    }

    public void setHCost(double hCost){
        this.hCost = hCost;
    }
    public void setGCost(double gCost){
        this.gCost = gCost;
    }
    public void setParent(AStarNode parent){
        this.parent = parent;
    }

    public double getGCost(){
        return this.gCost;
    }

    public double getHCost(){
        return this.hCost;
    }

    public int getGridX(){
        return this.gridX;
    }
    public int getGridY(){
        return this.gridY;
    }

    public AStarNode getParent(){
        return this.parent;
    }

    /**
     * This method calculates f(n) = g(n) + h(n) for the given node n. The priority queue
     * that will serve as the open set of AStarNodes requires a compareTo() method to be implemented. This
     * compareTo method will utilize the value of f(n) to make its comparisons.
     * @return a double representing f(n).
     */
    public double getFCost(){
        return this.gCost + this.hCost;
    }

    @Override
    /**
     * The compareTo method that will be used by the open set to order AStarNodes according to their distance
     * from the goal class box.
     * @return an int value representing the relative order of the two AStarNodes.
     */
    public int compareTo(AStarNode other){
        if(this.getFCost() != other.getFCost()) {
            return Double.compare(this.getFCost(), other.getFCost());
        }
        //Break ties using hCost.
        else{
            return Double.compare(this.getHCost(), other.getHCost());
        }
    }

    /**
     * hashCode function is utilized by the closed set in A* search as well as openSetLookupMap.
     * @return int representing the hashCode of a given AStarNode.
     */
    @Override
    public int hashCode(){
        return Objects.hash(gridX, gridY);

    }

    /**
     * Equals method to determine if two AStarNodes are equal.
     * @param other   the reference object with which to compare.
     * @return a boolean value of true if the nodes are equal and false if not equal.
     */
    @Override
    public boolean equals(Object other){
        if(this == other){
            return true;
        }
        else if((other == null) || (getClass() != other.getClass())){
            return false;
        }
        AStarNode otherNode = (AStarNode)other;
        if(this.gridX == otherNode.gridX && this.gridY == otherNode.gridY){
            return true;
        }
        return false;
    }

}
