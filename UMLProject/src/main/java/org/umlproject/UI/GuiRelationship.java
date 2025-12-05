package org.umlproject.UI;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import org.umlproject.RelationshipType;
import static org.umlproject.RelationshipType.AGGREGATION;
import static org.umlproject.RelationshipType.COMPOSITION;
import static org.umlproject.RelationshipType.GENERALIZATION;
import static org.umlproject.RelationshipType.OTHER;
import static org.umlproject.RelationshipType.REALIZATION;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;
import org.umlproject.UMLRelationship;
import org.umlproject.DiagramElementListener;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Polyline;

public final class GuiRelationship implements DiagramElementListener<UMLRelationship>, UISelectable{

    private UMLRelationship relationship;
    private Pane world;
    private Polyline lineMain, lineOutline, selectionOutline;
    private boolean isSelected = false;
    private Node relationshipDiagramElement;
    private TextField relationshipText;
    private List<Point2D> pathPoints;

    private static final double SYMBOL_DISTANCE_BUFFER = 50;
    private static final double SYMBOL_MIN_DISTANCE = 100;
    private static final double MIN_DISTANCE_FOR_REDRAW = 100;
    private static  RelationshipRouter router;

    public GuiRelationship(Pane world, UMLRelationship umlRelationship)
    {
        this.world = world;
        this.relationship = umlRelationship;
        this.router = RelationshipRouter.getRouterInstance();
        update(umlRelationship);
    }

    /** Calculates a safePath.
     */
    public void calculateAndSetPath() {
        UMLClass source = relationship.getSource();
        UMLClass target = relationship.getDestination();
        if(source == null || target == null){
            return;
        }
        List<Point2D> newPath = router.AStarAlgorithm(relationship, source, target);

        //Check if newPath is empty or null, in which case draw a default straight line.
        if(newPath == null || newPath.isEmpty()){
            System.out.println("ERROR! NO PATH FOUND! THIS SHOULD BE PHYSICALLY IMPOSSIBLE!");
            System.out.println("TODO: FIND BUG, CRITICAL FAILURE!");
            //return a default straight line
            Point2D sourceCenter = source.getLocation();
            Point2D targetCenter = target.getLocation();
            newPath = new ArrayList<>();
            newPath.add(sourceCenter);
            newPath.add(targetCenter);
        }
        //Merge multiple useless nodes into one.
        //Cases where multiple nodes are inside a class should be simplified into one node.
        //This ensures START and END of our newPath is the 'class' center.
        for(int i = newPath.size() - 2; i > 0; i--)
        {
            if(source.getListener() instanceof GuiClass guiClass && guiClass.contains(newPath.get(i)))
                newPath.remove(i);
            
            if(target.getListener() instanceof GuiClass guiClass && guiClass.contains(newPath.get(i)))
                newPath.remove(i);
        }
        this.setPathPoints(newPath);
    }

    /**
     * Getter method for pathPoints. Used by RelationshipRouter class for the AStar algorithm.
     * @return a List of points representing the relationship path.
     */
    public List<Point2D> getPathPoints(){
        return this.pathPoints;
    }

    /**
     * Setter method used by the AStar algorithm in RelationshipRouter to update the path of a given
     * relationship.
     * @param newPathPoints, the new path of points to be stored in a given GuiRelationship object.
     */
    public void setPathPoints(List<Point2D> newPathPoints){
        this.pathPoints = newPathPoints;
    }

    public UMLRelationship getRelationship()
    {
        return this.relationship;
    }
    /**
     * 
     */
    private boolean redrawRequiredForClass(UMLClass checkClass, Point2D point)
    {
        Point2D classLocation = checkClass.getLocation();
        if(classLocation.distance(point) >= MIN_DISTANCE_FOR_REDRAW)
            return  true;
        
        return false;
    }
    /**
     * Checks if the given point requires a redraw
     */
    private boolean redrawRequired()
    {
        if(pathPoints == null) return true;
        if(this.lineMain == null) return true;
        UMLClass source = relationship.getSource();
        UMLClass target = relationship.getDestination();
        if(source == null || target == null) return false;
        
        return redrawRequiredForClass(source, pathPoints.getLast()) || redrawRequiredForClass(target, pathPoints.getFirst());
    }
    @Override
    public void update(UMLRelationship desiredElement) {
        
        if(!redrawRequired())
            return;
        
        cleanUp();
        this.calculateAndSetPath();
        if (this.pathPoints == null || this.pathPoints.size() < 2) {
            return;
        }
        
        List<Double> polylinePoints = new ArrayList<>();
        for (Point2D p : this.pathPoints) {
            polylinePoints.add(p.getX());
            polylinePoints.add(p.getY());
        }
        
        placeRelationshipMarker(relationship.getDestination(), relationship.getRelationshipType(), polylinePoints);

        //initialize polyline objects
        this.lineMain = new Polyline();
        this.lineOutline = new Polyline();
        this.selectionOutline = new Polyline();

        this.lineMain.getPoints().addAll(polylinePoints);
        this.lineOutline.getPoints().addAll(polylinePoints);
        this.selectionOutline.getPoints().addAll(polylinePoints);

        this.selectionOutline.setViewOrder(102);//Behind all
        this.lineOutline.setViewOrder(101);//Behind of main
        this.lineMain.setViewOrder(100);//Send to back...

        this.selectionOutline.setStrokeWidth(0);// HIDE THE SELECTION OUTLINE!
        this.lineOutline.setStrokeWidth(15);
        this.lineMain.setStrokeWidth(10);

        this.lineMain.setStroke(GuiColor.GENERIC_LINE_COLOR);
        this.lineOutline.setStroke(Color.BLACK);
        this.selectionOutline.setStroke(GuiColor.SELECTION_COLOR);
        this.selectionOutline.getStrokeDashArray().addAll(30.0, 30.0);

        if (desiredElement.getRelationshipType() == GENERALIZATION) {
            this.lineMain.getStrokeDashArray().addAll(30.0, 15.0);
            this.lineOutline.getStrokeDashArray().addAll(30.0, 15.0);
        }
        //Not sure if placeText will work with the new Polyline
        //placeText(desiredElement);


        world.getChildren().addAll(lineMain, lineOutline, selectionOutline);
        //Add clickableness...
        lineMain.setOnMouseClicked(e -> {
            GuiSelect.getInstance().clickUiElement(e, this, false);
            e.consume(); // Prevent event from propagating to other nodes
        });
        setSelected(isSelected);//Update our selected state
        

        
    }


    /**
     * Places the 'type marker'.
     */
    void placeRelationshipMarker(UMLClass endClass, RelationshipType desiredElement, List<Double> polylinePoints)
    {
        if(endClass == null || endClass.getListener() == null)
        return;
        // In the future when we untangle the wire of relationships, only this function needs to change.
        Double angle = computeLineAngle();
        if(angle == null) return;
        Rectangle2D targetBounds = ((GuiClass)endClass.getListener()).getRectBounds();


        if(angle == null)
        return;//Cannot apply shape. Angle DNE
        Node diagramNode = createDiagramNode(desiredElement);
        this.relationshipDiagramElement = diagramNode;//this.classDiagramType CAN BE NULL. This is intended
        if(this.relationshipDiagramElement == null) return;//Cannot apply diagram, NONE EXISTS
        this.world.getChildren().add(this.relationshipDiagramElement);

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        //IMPORTANT NOTE: Realistically this should be distance from center to top/side. Using half because thats currently how the code works.
        //Simplification might have to be adjusted later.
        double heightTo = targetBounds.getHeight()/2;
        double lengthTo = targetBounds.getWidth()/2;

        double hypotHeight = Math.abs(heightTo/sin);//height/sin(theta) = Hypotenuse
        double hypotLength = Math.abs(lengthTo/cos);//width/cos(theta) = Hypotenuse

        //Get smallest 'hypot'. use that for angle.
        double vectorLength = Math.min(hypotLength, hypotHeight) + SYMBOL_DISTANCE_BUFFER;//Find the smallest sidelength
        vectorLength = Math.max(vectorLength, SYMBOL_MIN_DISTANCE);


        Point2D endPoint = new Point2D(cos * vectorLength, sin * vectorLength).add(endClass.getLocation());

        this.relationshipDiagramElement.setLayoutX(endPoint.getX());
        this.relationshipDiagramElement.setLayoutY(endPoint.getY());
        this.relationshipDiagramElement.setRotate(Math.toDegrees(angle));
        
        //Move the last to this location
        polylinePoints.set(1, endPoint.getY());//Y axis. 
        polylinePoints.set(0, endPoint.getX());//X axis.
        
        if(pathPoints.size() >= 2)//More than 2 points, our 2nd point will be immidientally outside of our class.
        {
            polylinePoints.set(3, endPoint.getY());//Y axis. 
            polylinePoints.set(2, endPoint.getX());//X axis. 
        }
    }
     
    private Node createDiagramNode(RelationshipType desiredElement)
    {
        if(desiredElement == null)
            return null;
        Polygon tempShape = null;
        double unitScale = 50;
        switch(desiredElement)
        {
            case AGGREGATION, COMPOSITION ->//Empty diamond
            {
                //Reuse same shape for AGGREGATION and COMPOSITION
                tempShape = new Polygon(-unitScale * 0.8,0,     //left most
                        0,unitScale/2,    //top
                        unitScale * 0.8,0,      //right most
                        0,-unitScale/2);  //bottom
                //Differentiate color.
                if(desiredElement == AGGREGATION)
                {
                    tempShape.setStroke(Color.BLACK);
                    tempShape.setStrokeWidth(5);
                    tempShape.setFill(GuiColor.GENERIC_LINE_COLOR);
                }
                else
                {
                    tempShape.setStroke(GuiColor.GENERIC_LINE_COLOR);
                    tempShape.setStrokeWidth(5);
                    tempShape.setFill(Color.GREY);
                }
            }
            case REALIZATION, GENERALIZATION -> //Both share the same shape...
            {
                tempShape = new Polygon(-unitScale,0,     //left most
                        0,unitScale/2,    //top
                        unitScale*0.1,0,      //right most
                        0,-unitScale/2);  //bottom
                tempShape.setStroke(Color.BLACK);
                tempShape.setStrokeWidth(5);
                tempShape.setFill(GuiColor.GENERIC_LINE_COLOR);
                tempShape.setTranslateX(25);
            }
            case OTHER ->
            {
                tempShape = new Polygon(-unitScale,0,     //left most
                        0,unitScale/2,    //top
                        unitScale*-0.5,0,      //right most
                        0,-unitScale/2);  //bottom
                tempShape.setStroke(Color.BLACK);
                tempShape.setStrokeWidth(5);
                tempShape.setFill(GuiColor.GENERIC_LINE_COLOR);
                tempShape.setTranslateX(25);
            }
        }
        return tempShape;
    }
    /**
     * Computes the lines angle in radians of the final segment of the relationship path.
     * @return Angle in RADIANS. radians. radians. not degrees.
     */
    private Double computeLineAngle()
    {
        if(this.pathPoints == null || this.pathPoints.size() < 2){
            return null;
        }
        Point2D startP = this.pathPoints.get(0);
        Point2D endP = this.pathPoints.get(1);
        
        double deltaX = endP.getX() - startP.getX();
        double deltaY = endP.getY() - startP.getY();
        return Math.atan2(deltaY, deltaX);
    }


    @Override
    public void cleanUp() {
        if(this.lineMain == null)
            return;
        this.world.getChildren().removeAll(this.lineMain, this.lineOutline, this.selectionOutline, this.relationshipDiagramElement, this.relationshipText);
    }
    @Override
    public void updateLocation(UMLRelationship desiredElement) {
        //Do nothing...
    }
    @Override
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        if(this.selectionOutline != null)
            this.selectionOutline.setStrokeWidth(isSelected? 20 : 0);
    }
    @Override
    public boolean getSelected() {
        return this.isSelected;
    }

    @Override
    public boolean contains(Point2D selectionPoint) {
        return false; //TODO: Implement
    }

    @Override
    public boolean intersects(Rectangle2D selectionRectangle) {
        return false; //TODO: Implement
    }

    @Override
    public void selectionAnimationUpdate(float time) {
        if(this.selectionOutline == null)
            return;
        this.selectionOutline.setStrokeWidth(23+ 2*Math.sin(time * 0.00000001));
        this.selectionOutline.setStrokeDashOffset(10*Math.sin(time * 0.000000001));

    }

}
