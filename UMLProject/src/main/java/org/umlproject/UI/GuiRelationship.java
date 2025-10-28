package org.umlproject.UI;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import org.umlproject.RelationshipType;
import static org.umlproject.RelationshipType.AGGREGATION;
import static org.umlproject.RelationshipType.COMPOSITION;
import static org.umlproject.RelationshipType.GENERALIZATION;
import static org.umlproject.RelationshipType.OTHER;
import static org.umlproject.RelationshipType.REALIZATION;
import org.umlproject.UIListener;
import org.umlproject.UISelectable;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;
import org.umlproject.UMLRelationship;

public final class GuiRelationship implements UIListener<UMLRelationship>, UISelectable{

    private Group world;
    private Line lineMain, lineOutline, selectionOutline;
    private boolean isSelected = false;
    private Node relationshipDiagramElement;
    private TextField relationshipText;
    
    private static final double SYMBOL_DISTANCE_BUFFER = 50;
    private static final double SYMBOL_MIN_DISTANCE = 100;
    private static final double MIN_LINE_DISTANCE_FOR_TEXT = 500;
    
    
    public GuiRelationship(Group world, UMLRelationship umlRelationship)
    {
        this.world = world;
        update(umlRelationship);
    }

    @Override
    public void update(UMLRelationship desiredElement) {
        cleanUp();
        
        
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
        
        if(desiredElement.getRelationshipType() == GENERALIZATION)
        {
            this.lineMain.getStrokeDashArray().addAll(30.0,15.0);
            this.lineOutline.getStrokeDashArray().addAll(30.0,15.0);
            this.selectionOutline.getStrokeDashArray().addAll(30.0,15.0);
        }
        placeText(desiredElement);
        placeRelationshipMarker(endClass, desiredElement.getRelationshipType());
        
        
        world.getChildren().addAll(lineMain, lineOutline, selectionOutline);
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
        this.relationshipText.setLayoutY(midpoint.getY() - 10);//Magical number for offsetting correctly
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
    /**
     * Places the 'type marker'.
     */
    void placeRelationshipMarker(UMLClass endClass, RelationshipType desiredElement)
    {
        Rectangle2D targetBounds = ((GuiClass)endClass.getUIListener()).getRectBounds();
        // In the future when we untangle the wire of relationships, only this function needs to change.
        Double angle = computeLineAngle();
        
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
        
        System.out.println(vectorLength == SYMBOL_MIN_DISTANCE);
        
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
     * Computes the lines angle in radians
     * @return Angle in RADIANS. radians. radians. not degrees.
     */
    private Double computeLineAngle()
    {
        if(lineMain == null)
            return null;
        
        double deltaX = lineMain.getStartX() - lineMain.getEndX();
        double deltaY = lineMain.getStartY() - lineMain.getEndY();
        
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
        this.selectionOutline.setStrokeWidth(isSelected? 20 : 0);
    }
    @Override
    public boolean getSelected() {
        return isSelected;
    }

    @Override
    public boolean contains(Point2D selectionPoint) {
        return false; //TODO: Implement
    }

    @Override
    public boolean intersects(Rectangle2D selectionRectangle) {
        return false; //TODO: Implement
    }
    
}
