package org.umlproject.UI;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.umlproject.*;


import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GuiClass implements UIListener<UMLClass>, UISelectable, UIPositional {
    private Group world;
    private UMLClass parentClass;

    double mouseAnchorX;
    double mouseAnchorY;
    StackPane nodeBackground;
    public GuiClass(Group world, UMLClass parentClass)
    {
        this.world = world;
        world.setFocusTraversable(true);
        this.parentClass = parentClass;
        this.nodeBackground = new StackPane();


        //create a rectangle background for the class.
        Rectangle background = new Rectangle();
        background.setHeight(200);
        background.setWidth(200);
        background.setFill(Color.WHITESMOKE);
        background.setStroke(Color.BLACK);

        //create modifiable className and put into VBox
        TextField classNameField = new TextField(parentClass.getClassName());
        classNameField.setStyle("-fx-font-size: 16px; -fx-font-weight: bold");
        classNameField.setMaxWidth(180);
        classNameField.setFocusTraversable(false);
        //Make it so the UMLClass class name updates after modifying classNameField
        //and pressing enter or clicking away.
        classNameField.setOnAction(e -> {

            String newName = classNameField.getText();
            String oldName = parentClass.getClassName();

            //update UMLDocument aswell to allow more classes to be made with +C
            boolean updateSuccess = updateRename(oldName, newName);
            if(!updateSuccess){
                classNameField.setText(oldName);
            }
            parentClass.setListener(this);
            e.consume();
            //move focus elsewhere. This is necessary to accept changes and remove
            //cursor from textBox
            world.requestFocus();


            //for debugging to see if class name updates in UMLDocument
            /**
            Map<String, UMLClass> classes = UMLDocument.getInstance().getClassSet();
            Set<String> classNamesSet = classes.keySet();
            String[] classNamesArray = classNamesSet.toArray(new String[0]);
            for(String c : classNamesArray){
                System.out.println(c);

            }
            */
        });

        VBox vbox = new VBox(classNameField);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setPadding(new Insets(10, 0, 0, 0));


        nodeBackground.getChildren().addAll(background, vbox);




        //Here we bind the StackPane to the location of the UMLClass, then
        //make the rectangle background draggable.
        if(parentClass.getLocation() != null){
            nodeBackground.setLayoutX(parentClass.getLocation().getX());
            nodeBackground.setLayoutY(parentClass.getLocation().getY());
        }

        //make rectangle draggable
        makeDraggable(nodeBackground);
        //add classbox to world to display
        world.getChildren().add(nodeBackground);
        world.requestFocus();
    }

    //This method will make it so the StackPane that holds all the nodes
    //that represent a class can be moved by clicking and dragging the mouse.
    //the MouseAnchor calculation ensures smooth dragging, by
    //letting the new location of nodeBackground be calculated from where
    //we clicked our mouse rather than from the top left corner of the StackPane.
    //it calls setLocation on parent UMLClass to update the parents coordinates.
    //the parent in turn calls updateGUILocation() to move the classbox on the screen.
    private void makeDraggable(StackPane nodeBackground) {
        nodeBackground.setOnMousePressed(e -> {
            System.out.println("Mouse pressed on StackPane at: " + e.getSceneX() + ", " + e.getSceneY());
            mouseAnchorX = e.getSceneX() - nodeBackground.getLayoutX();
            mouseAnchorY = e.getSceneY() - nodeBackground.getLayoutY();
            e.consume(); // Prevent event from propagating to other nodes
        });

        nodeBackground.setOnMouseDragged(e -> {
            System.out.println("Mouse dragged on StackPane to: " + e.getSceneX() + ", " + e.getSceneY());
            double newX = e.getSceneX() - mouseAnchorX;
            double newY = e.getSceneY() - mouseAnchorY;
            System.out.println("Setting UMLClass location to: " + newX + ", " + newY);
            parentClass.setLocation(new Point2D(newX, newY));
            nodeBackground.getParent().requestLayout(); // Force layout update
            e.consume(); // Prevent event from propagating to other nodes
        });
    }

    
    @Override
    public void update(UMLClass desiredElement) {

    }

    @Override
    public void updateSelected(UMLClass desiredElement) {
    }


    //This method will update the location of the gui element representing
    //the umlClass. That is, any calls to this function will visibly move
    //the class box on the screen.
    @Override
    public void updateLocation(UMLClass desiredElement) {

        nodeBackground.setLayoutX(parentClass.getLocation().getX());
        nodeBackground.setLayoutY(parentClass.getLocation().getY());
        System.out.println("stackpane layout is changed to:" + parentClass.getLocation());
        nodeBackground.getParent().requestLayout();
    }

    /**
     * Runs after user input, removing the chosen class from the model
     *
     * @param input - Input class to be removed from model
     */
    public void updateRemove(UMLClass input){
        UMLClass checkClass = UMLDocument.getInstance().removeClass(input.getClassName());
        if(checkClass == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unable To Remove Class Error");
            alert.showAndWait();
        }
    }

    /**
     * Runs after user input, renaming the chosen class from the model
     *
     * @param oldName,newName - The name to be replaced and do the replacing
     */
    public boolean updateRename(String oldName, String newName){
        boolean checkClass = UMLDocument.getInstance().renameClass(oldName, newName);
        if(!checkClass){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Unable To Rename Class Error: Class Already Exists");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    @Override
    public void cleanUp() {
    }

    @Override
    public void setSelected(boolean isSelected) {
        
    }

    @Override
    public boolean getSelected() {
        return false; //TODO: Implement.
    }

    @Override
    public boolean contains(Point2D selectionPoint) {
        return false;
    }

    @Override
    public boolean intersects(Rectangle2D selectionRectangle) {
        return false;
    }

    @Override
    public Point2D getLocation() {
        return Point2D.ZERO;//TODO: Implement
    }

    @Override
    public void setLocation(Point2D location) {
    }
}
