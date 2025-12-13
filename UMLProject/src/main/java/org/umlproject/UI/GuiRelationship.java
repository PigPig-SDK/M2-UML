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
import java.util.Collections;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Polyline;

public final class GuiRelationship implements DiagramElementListener<UMLRelationship>, UISelectable{

    private UMLRelationship relationship;
    private Pane world;
    private Polyline lineMain, lineOutline, selectionOutline;
    private boolean isSelected = false;
    private Node relationshipDiagramElement;
    private TextField relationshipText;
    private List<Point2D> pathPoints;
    private List<AStarSegment> nodeSpacePoints;
    public boolean queuedRedraw = false;
    private RelationshipType lastKnownRelationship = OTHER;

    private static final double MIN_DISTANCE_FOR_REDRAW = 15;
    private static final double TEXT_LENGTH = 200.0;
    private static final double MIN_LINE_DISTANCE_FOR_TEXT = 12;
    private static  RelationshipRouter router;

    public GuiRelationship(Pane world, UMLRelationship umlRelationship)
    {
        this.world = world;
        this.relationship = umlRelationship;
        this.router = RelationshipRouter.getInstance();
        update(umlRelationship);
    }

    /** 
     * Calculates a safePath.
     */
    private void calculateAndSetPath() {
        UMLClass source = relationship.getSource();
        UMLClass target = relationship.getDestination();
        
        if(relationship.getRelationshipType() == AGGREGATION || relationship.getRelationshipType() == COMPOSITION)//Flip for these specifically.
        {
            source = target;
            target = relationship.getSource();
        }
        
        if(source == null || target == null){
            return;
        }
        
        nodeSpacePoints = router.AStarAlgorithm(relationship, source, target);
        
        List<Point2D> newPath = new ArrayList<>();
        if(nodeSpacePoints != null)
        {
            for(AStarSegment ass : nodeSpacePoints)
            {
                if(ass == null) continue;

                double x = PathGridMapper.toPixelCoordinate(ass.getGridX());
                double y = PathGridMapper.toPixelCoordinate(ass.getGridY());
                newPath.add(new Point2D(x, y));
            }
        }

        
        //Check if newPath is empty or null, in which case draw a default straight line.
        if(newPath == null || newPath.isEmpty() || newPath.size() == 1){
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
        if(newPath.size() >= 3)
        {
            for(int i = newPath.size() - 2; i > 0; i--)
            {
                if(target.getListener() instanceof GuiClass guiClass && guiClass.contains(newPath.get(i)))
                    newPath.remove(i);
            }
        }
        //Update centers.
        newPath.set(0, target.getLocation());
        newPath.set(newPath.size()-1, source.getLocation());
        this.pathPoints = newPath;
    }
    /**
     * Returns the relationship which this element is associated
     */
    public UMLRelationship getRelationship()
    {
        return this.relationship;
    }
    /**
     * Checks if a redraw is required per class basis
     */
    private boolean redrawRequiredForClass(UMLClass checkClass, Point2D point)
    {
        if(checkClass.getLocation().distance(point) >= MIN_DISTANCE_FOR_REDRAW)
            return  true;
        return false;
    }
    /**
     * Checks if the given point requires a redraw
     */
    private boolean redrawRequired()
    {
        if(queuedRedraw)
        {
            queuedRedraw = false;
            return true;
        }
        
        if(lastKnownRelationship != this.relationship.getRelationshipType()) return true;
        
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
        
        //Update.
        lastKnownRelationship = desiredElement.getRelationshipType();
        
        cleanUp();
        this.calculateAndSetPath();

        if (this.pathPoints == null || this.pathPoints.size() < 2) return;
        
        
        if(nodeSpacePoints != null)//Incase failure inside calculateAndSetPath()
        {
            for(AStarSegment ass : nodeSpacePoints)
            {
                RelationshipRouter.getInstance().addOccupationToCell(relationship, ass);
            }
        }

        List<Double> polylinePoints = new ArrayList<>();
        //Exclude last node.
        for(int i = 1; i < this.pathPoints.size(); i++)
        {
            Point2D p = this.pathPoints.get(i);
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
            if(e.getButton() != MouseButton.PRIMARY) return;
            
            GuiSelect.getInstance().clickUiElement(e, this, false);
            e.consume(); // Prevent event from propagating to other nodes
        });
        placeText(relationship);
        
        setSelected(isSelected);//Update our selected state
    }
    private void placeText(UMLRelationship desiredElement)
    {
        if(nodeSpacePoints == null)
            return;
        
        if(nodeSpacePoints.size() < MIN_LINE_DISTANCE_FOR_TEXT)
            return;
        //Bias starting towards the endpoint
        Point2D midpoint = pathPoints.get(pathPoints.size()/2);
        
        this.relationshipText = new TextField(desiredElement.getRelationshipName());
        this.relationshipText.setAlignment(Pos.CENTER);
        this.relationshipText.setMinWidth(TEXT_LENGTH);
        this.relationshipText.setLayoutX(midpoint.getX() - (TEXT_LENGTH/2));//Magical number for offsetting correctly
        this.relationshipText.setLayoutY(midpoint.getY() - 12.5);//Magical number for offsetting correctly
        this.relationshipText.setViewOrder(40);//Send to back..
        //Text update
        //When the user gives our textbox a new value, we push the data and that causes a redraw...
        this.relationshipText.setOnAction(event -> {
            String input = relationshipText.getText();
            
            if(input == null || input.strip().isEmpty())
            {
                RelationshipType type = desiredElement.getRelationshipType();
                if(type == OTHER)
                    input = desiredElement.getCustomNameType();
                else
                    input = type.toString();
            }
            
            RelationshipType relationship = RelationshipType.stringToRelationshipType(input);
            if(relationship == OTHER)
                desiredElement.setCustomNameType(input);
            else
                desiredElement.setRelationshipType(relationship);
        });
        
        this.world.getChildren().add(relationshipText);
        this.world.requestFocus();
    }

    /**
     * Places the 'type marker'.
     */
    private void placeRelationshipMarker(UMLClass endClass, RelationshipType desiredElement, List<Double> polylinePoints)
    {
        if(endClass == null || endClass.getListener() == null)
        return;
        // In the future when we untangle the wire of relationships, only this function needs to change.
        Double angle = computeLineAngle();

        if(angle == null) return;//Cannot apply shape. Angle DNE
        
        Node diagramNode = createDiagramNode(desiredElement);
        
        this.relationshipDiagramElement = diagramNode;//this.classDiagramType CAN BE NULL. This is intended
        if(this.relationshipDiagramElement == null) return;//Cannot apply diagram, NONE EXISTS
        this.world.getChildren().add(this.relationshipDiagramElement);

        this.relationshipDiagramElement.setLayoutX(pathPoints.get(1).getX());
        this.relationshipDiagramElement.setLayoutY(pathPoints.get(1).getY());
        this.relationshipDiagramElement.setRotate(Math.toDegrees(angle));
    }
    /**
     * Creates a diagram node depending on the RelationshipType
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
        Point2D startP = this.pathPoints.get(0);
        Point2D endP = this.pathPoints.get(1);
        
        double deltaX = endP.getX() - startP.getX();
        double deltaY = endP.getY() - startP.getY();
        return Math.atan2(deltaY, deltaX);
    }


    @Override
    public void cleanUp() {
        //Stop cell occupation
        if(nodeSpacePoints != null)
        {
            for(AStarSegment ass : nodeSpacePoints)
            {
                RelationshipRouter.getInstance().removeOccupationToCell(relationship, ass);
            }
        }

        if(this.lineMain == null)
            return;
        this.world.getChildren().removeAll(this.lineMain, this.lineOutline, this.selectionOutline, this.relationshipDiagramElement, this.relationshipText);
    }
    @Override
    public void updateTranslation(UMLRelationship desiredElement) {
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
        
        if(pathPoints == null) return false;
        
        for(Point2D point : pathPoints)
        {
            if(selectionRectangle.contains(point)) return true;
        }
        return false;
    }

    @Override
    public void selectionAnimationUpdate(float time) {
        if(this.selectionOutline == null)
            return;
        this.selectionOutline.setStrokeWidth(23+ 2*Math.sin(time * 0.00000001));
        this.selectionOutline.setStrokeDashOffset(10*Math.sin(time * 0.000000001));

    }
}
