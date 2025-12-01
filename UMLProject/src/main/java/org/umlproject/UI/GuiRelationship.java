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
    private static final double MIN_LINE_DISTANCE_FOR_TEXT = 500;
    private static  RelationshipRouter router;

    public GuiRelationship(Pane world, UMLRelationship umlRelationship)
    {
        this.world = world;
        this.relationship = umlRelationship;
        this.router = RelationshipRouter.getRouterInstance();
        update(umlRelationship);
    }

    /** Calculates a safePath.
     *
     */
    public void calculateAndSetPath() {
        UMLClass source = relationship.getSource();
        UMLClass target = relationship.getDestination();
        if(source == null || target == null){
            return;
        }
        List<Point2D> newPath = router.AStarAlgorithm(relationship, source, target);
        System.out.println("The size of the newPath is: " + newPath.size());
        //Check if newPath is empty or null, in which case draw a default straight line.
        if(newPath == null || newPath.isEmpty()){
            System.out.println("no path between " + source.getClassName() + " and " + target.getClassName() + " found.");
            //return a default straight line
            Point2D sourceCenter = source.getLocation();
            Point2D targetCenter = target.getLocation();
            newPath = new ArrayList<>();
            newPath.add(sourceCenter);
            newPath.add(targetCenter);
            this.setPathPoints(newPath);
        }
        //If AStarAlgorithm returns a path that is not empty or null.
            this.setPathPoints(newPath);

        /**
        else{
            //fallbackPath is a straight line. May want to modify this later.
            System.err.println("A* failed to find a path for: " + source.getClassName() + " -> " + target.getClassName());
            List<Point2D> fallbackPath = List.of(source.getLocation(), target.getLocation());
            this.setPathPoints(fallbackPath);
        }
         */
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

    @Override
    public void update(UMLRelationship desiredElement) {
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
        //placeRelationshipMarker(endClass, desiredElement.getRelationshipType());


        world.getChildren().addAll(lineMain, lineOutline, selectionOutline);

        //Add clickableness...
        lineMain.setOnMouseClicked(e -> {
            GuiSelect.getInstance().clickUiElement(e, this);
            e.consume(); // Prevent event from propagating to other nodes
        });
        setSelected(isSelected);//Update our selected state


        /**
         UMLClass startClass = UMLDocument.getInstance().getClass(desiredElement.getSourceName());
         UMLClass endClass = UMLDocument.getInstance().getClass(desiredElement.getDestinationName());

         //Flip the diagram under these shapes.
         if(desiredElement.getRelationshipType() == COMPOSITION || desiredElement.getRelationshipType() == AGGREGATION)
         {
         UMLClass temp = startClass;
         startClass = endClass;
         endClass = temp;
         }

         //If no start or end... do nothing
         //THIS IS A POSSIBLE STATE! Think 'non existing' start or end...
         if(startClass == null || endClass == null) return;

         Point2D startLocation = startClass.getLocation();
         Point2D endLocation = endClass.getLocation();
         this.lineMain = new Line(startLocation.getX(), startLocation.getY(), endLocation.getX(), endLocation.getY());
         this.lineOutline = new Line(startLocation.getX(), startLocation.getY(), endLocation.getX(), endLocation.getY());
         this.selectionOutline = new Line(startLocation.getX(), startLocation.getY(), endLocation.getX(), endLocation.getY());

         this.selectionOutline.setViewOrder(102);//Behind all
         this.lineOutline.setViewOrder(101);//Behind of main
         this.lineMain.setViewOrder(100);//Send to back...

         this.selectionOutline.setStrokeWidth(0);// HIDE THE SELECTION OUTLINE!
         this.lineOutline.setStrokeWidth(15);
         this.lineMain.setStrokeWidth(10);

         this.lineMain.setStroke(GuiColor.GENERIC_LINE_COLOR);
         this.lineOutline.setStroke(Color.BLACK);
         this.selectionOutline.setStroke(GuiColor.SELECTION_COLOR);
         this.selectionOutline.getStrokeDashArray().addAll(30.0,30.0);

         if(desiredElement.getRelationshipType() == GENERALIZATION)
         {
         this.lineMain.getStrokeDashArray().addAll(30.0,15.0);
         this.lineOutline.getStrokeDashArray().addAll(30.0,15.0);
         }
         placeText(desiredElement);
         placeRelationshipMarker(endClass, desiredElement.getRelationshipType());


         world.getChildren().addAll(lineMain, lineOutline, selectionOutline);

         //Add clickableness...
         lineMain.setOnMouseClicked(e -> {
         GuiSelect.getInstance().clickUiElement(e, this);
         e.consume(); // Prevent event from propagating to other nodes
         });
         setSelected(isSelected);//Update our selected state
         */
    }

    double getLineDistance(Line line)
    {
        double dx = line.getEndX() - line.getStartX();
        double dy = line.getEndY() - line.getStartY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    private static boolean requiresClamp(double val, double min, double max) {
        double clampEnd = Math.max(min, Math.min(max, val));
        return (clampEnd != val);//if a clamp occurs.
    }
    /**
     void placeText(UMLRelationship desiredElement)
     {
     double lineDistance = getLineDistance(this.lineMain);
     if(lineDistance < MIN_LINE_DISTANCE_FOR_TEXT)
     return;

     Double angle = computeLineAngle();
     if(angle == null)
     return;

     //Make textbox 'flip' as desired.
     if(Math.cos(angle) < 0)
     {
     //Hack fix
     if(Math.sin(angle) > 0)
     angle -= 2*Math.PI;
     angle += Math.PI;
     }
     //Stops the textbox from becoming completely vertical (Hard to read/modify)
     if(requiresClamp((double)angle,-0.95,0.95))//so bad...
     angle = 0.0;//Clamp angle to hard 0.

     //Bias starting towards the endpoint
     Point2D startPoint = new Point2D(this.lineMain.getStartX(),this.lineMain.getStartY());
     Point2D endPoint = new Point2D(this.lineMain.getEndX(),this.lineMain.getEndY());
     Point2D midpoint = startPoint.interpolate(endPoint, 0.45);

     this.relationshipText = new TextField(desiredElement.getRelationshipName());
     this.relationshipText.setRotate(Math.toDegrees(angle));
     this.relationshipText.setLayoutX(midpoint.getX() - 75);//Magical number for offsetting correctly
     this.relationshipText.setLayoutY(midpoint.getY() - 12.5);//Magical number for offsetting correctly
     this.relationshipText.setViewOrder(40);//Send to back..
     //Text update
     //When the user gives our textbox a new value, we push the data and that causes a redraw...
     this.relationshipText.setOnAction(event -> {
     String input = relationshipText.getText();
     RelationshipType relationship = RelationshipType.stringToRelationshipType(input);
     if(relationship == OTHER)
     desiredElement.setCustomNameType(input);
     else
     desiredElement.setRelationshipType(relationship);
     });

     this.world.getChildren().add(relationshipText);
     this.world.requestFocus();
     }
     */

    /**
     * Places the 'type marker'.
     */
    /**
     void placeRelationshipMarker(UMLClass endClass, RelationshipType desiredElement)
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

     //Re-route all endlines to conclude at our diagramCenter
     for (Line line : new Line[]{this.lineMain, this.lineOutline, this.selectionOutline}) {
     line.setEndX(endPoint.getX());
     line.setEndY(endPoint.getY());
     }
     }
     */
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
        Point2D start = this.pathPoints.get(this.pathPoints.size() - 2);
        Point2D end = this.pathPoints.get(this.pathPoints.size() -1);
        double deltaX = start.getX() - end.getX();
        double deltaY = start.getY() - end.getY();
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
