package org.umlproject.UI;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.umlproject.*;


import java.util.*;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;

public class GuiClass implements UIListener<UMLClass>, UISelectable, UIPositional {
    private Group world;
    private UMLClass parentClass;

    double mouseAnchorX;
    double mouseAnchorY;
    StackPane nodeBackground;
    VBox dataFieldTextFields;
    VBox methodTextFields;
    //VBox that holds className TextField and VBoxes for data fields and methods.
    VBox parentVBox;
    /**
     * Constructor for GuiClass responsible for building the initial class box and setting all the proper
     * actions on its nodes. TextFields will be editable and those edits will be reflected in the underlying
     * UMLDocument singleton. "Add Field Button" will create new TextFields in the Data Fields section of the class
     * box. "Add Method Button" will create new TextFields in the Methods section of the class box.
     * @param world, representing the group that holds all class boxes
     * @param parentClass, the UMLClass which a given GuiClass instance listens to.
     */
    public GuiClass(Group world, UMLClass parentClass)
    {
        this.world = world;
        world.setFocusTraversable(true);//lets world request focus.
        this.parentClass = parentClass;
        update(parentClass);
    }

    /**This method will make it so the StackPane that holds all the nodes
      *that represent a class can be moved by clicking and dragging the mouse.
      *The MouseAnchor calculation ensures smooth dragging, by
      *letting the new location of nodeBackground be calculated from where
      *we clicked our mouse rather than from the top left corner of the StackPane.
      *It calls setLocation on parent UMLClass to update the parents coordinates.
      *The parent in turn calls updateGUILocation() to move the classbox on the screen.
      * @param nodeBackground, a StackPane representing the container for the class box contents.
      */
    private void makeDraggable(StackPane nodeBackground) {
        nodeBackground.setOnMousePressed(e -> {
            System.out.println("Mouse pressed on StackPane at: " + e.getSceneX() + ", " + e.getSceneY());
            this.mouseAnchorX = e.getSceneX() - nodeBackground.getLayoutX();
            this.mouseAnchorY = e.getSceneY() - nodeBackground.getLayoutY();
            e.consume(); // Prevent event from propagating to other nodes
        });

        this.nodeBackground.setOnMouseDragged(e -> {
            //System.out.println("Mouse dragged on StackPane to: " + e.getSceneX() + ", " + e.getSceneY());
            double newX = e.getSceneX() - this.mouseAnchorX;
            double newY = e.getSceneY() - this.mouseAnchorY;
            //System.out.println("Setting UMLClass location to: " + newX + ", " + newY);
            this.parentClass.setLocation(new Point2D(newX, newY));
            this.nodeBackground.getParent().requestLayout(); // Force layout update
            e.consume(); // Prevent event from propagating to other nodes
        });
    }

    /**
     * Helper function that lets the user rename the class name TextField
     * @param classNameField, TextField object representing the current class name/
     */
    private void makeClassNameRenamable(TextField classNameField){
        classNameField.setOnAction(e -> {
            String newName = classNameField.getText();
            String oldName = this.parentClass.getClassName();
            //update UMLDocument with new class name if new name is unique.
            boolean updateSuccess = updateRename(oldName, newName);
            if(!updateSuccess){
                classNameField.setText(oldName);
            }
            e.consume();
            world.requestFocus();
        });
    }

    /**Sets an action on the data field TextFields of class box, so that when a new string representing
     * a datafield is entered, it will be parsed and transformed into a UMLDataField and then inserted
     * into the parent class.
     * @param oldField, the TextField containing the old DataField attributes as a string.
     */
    public void linkTextFieldToDataField(TextField oldField){

        oldField.setOnAction(e ->{
            String customTypeName = "";
            String dataFieldText = oldField.getText();
            String[] textAsArray = dataFieldText.split(" ");
            if(textAsArray.length != 3){

                System.out.println("Invalid number of arguments! Enter Visibility DataType Name.");
                oldField.setText("Visibility Type Name");
                return;
            }
            //make sure the user entered a valid Visibility value.
            boolean acceptableVisibilityStatus = Visibility.acceptableVisibility(textAsArray[0]);
            if(!acceptableVisibilityStatus){
                System.out.println("Invalid visibility type! Enter: Public, Private, Protected, or Package.");
                oldField.setText("Visibility Type Name");
                return;
            }
            Visibility visibility = Visibility.stringVisibility(textAsArray[0]);


            DataType dataType = DataType.stringToDatatype(textAsArray[1]);
            if(dataType == DataType.OTHER){
                customTypeName = textAsArray[1];
            }
            else{
                customTypeName = null;
            }
            String dataFieldName = textAsArray[2];
            UMLDataField newField = new UMLDataField(dataFieldName, customTypeName, dataType, visibility);


            //attempt to add the field
            boolean success = this.parentClass.addField(newField);
            System.out.println("field was added? " + success);
            if(success){
                //Renaming a textField like this results in two datafields existing in UMLDocument
                //oldField and the new one. Now we must delete oldField from UMLDocument and then
                //remove its text field from the VBox and let update() draw a new one.
                this.parentClass.removeField(oldField.getText());
                this.dataFieldTextFields.getChildren().remove(oldField);
                System.out.println("Field was added and class box will be updated!");
            //update is automatically called by UMLClass to redraw class box.

            }
            else{
                System.out.println("Datafield is a duplicate or invalid!");
                oldField.setText("Visibility Type Name");
            }
            world.requestFocus();
        });
    }

    /**
     * Helper method that sets an action on the addDataField button of a class box so that a
     * user can add a new datafield to both the UMLClass and a new TextField to the class box.
     * @param addDataField, button to be modified.
     */
    public void addDataFieldButtonClickable(Button addDataField){
        addDataField.setOnAction(e -> {
            TextField newField = new TextField("Visibility Type Name");
            //need to make sure there isn't a duplicate "Visibility Type Name" TextField in classbox already.
            boolean duplicateTextField = false;
            for(Node node : this.dataFieldTextFields.getChildren()){
                if(((TextField)(node)).getText().equals(newField.getText())){
                    duplicateTextField = true;
                    break;
                }
            }
            if(duplicateTextField){
                System.out.println("Must update Visibility Type Name of previous data field before adding a new one.");
                e.consume();
                return;
            }
            else {
                //Link this TextField to a DataField in underlying UMLDocument
                newField.setFocusTraversable(false);
                linkTextFieldToDataField(newField);
                this.dataFieldTextFields.getChildren().add(newField);
                e.consume();
            }
        });
    }

    /**
     * update is responsible for redrawing the classBox every time a data field or method is added or removed.
     * It will recreate the class box with the update nodes in a fashion reminiscent of the GuiClass constructor.
     * @param desiredElement, the UMLClass object that the GuiClass instance listens to.
     */
    @Override
    public final void update(UMLClass desiredElement) {
        //Clear out the previous GUI.
        cleanUp();
        //--=======================================================Clone start
        this.dataFieldTextFields = new VBox(10);
        this.methodTextFields = new VBox(10);
        this.parentVBox = new VBox(10);
        this.parentVBox.setSpacing(10);
        this.parentVBox.setPadding(new Insets(10, 10, 10, 10));
        this.parentVBox.setMinWidth(200);
        this.parentVBox.setPrefWidth(250);
        
        this.nodeBackground = new StackPane();
        this.nodeBackground.setManaged(false);

        //create a rectangle background for the class.
        Rectangle background = new Rectangle();
        background.setFill(Color.MINTCREAM);
        background.setStroke(Color.BLACK);
        background.widthProperty().bind(this.parentVBox.widthProperty().add(20));
        background.heightProperty().bind(this.parentVBox.heightProperty().add(20));

        //Create modifiable className and put into VBox
        TextField classNameField = new TextField(parentClass.getClassName());
        classNameField.setStyle("-fx-font-size: 10px; -fx-font-weight: bold");
        classNameField.setMaxWidth(250);
        classNameField.setFocusTraversable(false);
        //Make it so the UMLClass class name updates after modifying classNameField
        makeClassNameRenamable(classNameField);

        this.parentVBox.getChildren().addAll(classNameField, new Separator());

        //set up dataFields
        //retrieve dataFields hashMap, retrieve keySet, convert into an array, then cycle through each
        //and create a textField and put in dataFieldsVBox.
        Label dataFieldsLabel = new Label("Data Fields:");
        HashMap<String, UMLDataField> UMLDataFields = desiredElement.getFieldsAll();
        ArrayList<String> fieldsAsStrings = convertDataFieldsToStrings(UMLDataFields);
        for(int i = 0; i < fieldsAsStrings.size(); i++){
            TextField nextField = new TextField(fieldsAsStrings.get(i));
            this.dataFieldTextFields.getChildren().add(nextField);
        }
        this.parentVBox.getChildren().addAll(dataFieldsLabel, this.dataFieldTextFields);

        //create AddField Button, set action to make a new DataField
        Button addDataField = new Button("Add data field");
        addDataFieldButtonClickable(addDataField);
        this.parentVBox.getChildren().addAll(addDataField, new Separator());

        //create methods
        Label methodsLabel = new Label("Methods:");
        this.parentVBox.getChildren().addAll(methodsLabel, this.methodTextFields);
        this.nodeBackground.getChildren().addAll(background, this.parentVBox);


        //Here we bind the StackPane to the location of the UMLClass, then
        //make the rectangle background draggable.
        if(this.parentClass.getLocation() != null){
            this.nodeBackground.setLayoutX(this.parentClass.getLocation().getX());
            this.nodeBackground.setLayoutY(this.parentClass.getLocation().getY());
        }

        //make rectangle draggable
        makeDraggable(this.nodeBackground);
        //add classbox to world to display
        this.world.getChildren().add(this.nodeBackground);
        this.world.requestFocus();
        updateAllRelationships(desiredElement);
    }
    /**
     * Helper function for update() that converts a hashMap of data fields into a String[] array
     * where each entry represents a data field according to the TextField format: Visibility Type Name.
     * @param UMLDataFields, hashmap of datafields
     * @return ArrayList<String> representing each of the dataFields
     */
    public ArrayList<String> convertDataFieldsToStrings(HashMap<String, UMLDataField> UMLDataFields){
        if(UMLDataFields == null){
            System.out.println("invalidInput");
            return null;
        }
        ArrayList<String> fieldsAsStrings = new ArrayList<String>();
        Set<String> dataFieldKeys = UMLDataFields.keySet();
        List<String> keyList = new ArrayList<>(dataFieldKeys);
        Collections.sort(keyList);
        String[] sortedKeys = keyList.toArray(new String[0]);
        StringBuilder nextText = new StringBuilder();
        for(int i = 0; i < sortedKeys.length; i++){
            UMLDataField nextField = UMLDataFields.get(sortedKeys[i]);
            nextText.append(nextField.getVisibility());
            nextText.append(" ");
            if(nextField.getDataType() == DataType.OTHER){
                nextText.append(nextField.getCustomNameType());
            }
            else{
                nextText.append(nextField.getDataType());
            }
            nextText.append(" ");
            nextText.append(nextField.getName());
            fieldsAsStrings.add(nextText.toString());
            nextText = new StringBuilder();
        }
        return fieldsAsStrings;
    }
    /**This method will update the location of the gui element representing
     *the umlClass. That is, any calls to this function will visibly move
     *the class box on the screen.
     * @param desiredElement, this is the UMLClass whose location data field is used to update nodeBackground
     */
     @Override
    public void updateLocation(UMLClass desiredElement) {
        if(this.nodeBackground == null)
            return;
        updateAllRelationships(desiredElement);
        this.nodeBackground.setLayoutX(desiredElement.getLocation().getX());
        this.nodeBackground.setLayoutY(desiredElement.getLocation().getY());

        //System.out.println("stackpane layout is changed to:" + ((UMLClass)(desiredElement)).getLocation());
        this.nodeBackground.getParent().requestLayout();
    }
    public void updateAllRelationships(UMLClass desiredElement)
    {
        ArrayList<UMLRelationship> list = UMLDocument.getInstance().getAllRelationshipsInstanceOf(desiredElement.getClassName());
        for(UMLRelationship relationship : list)//Update all relationship GUI
            relationship.updateGUI();
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
        updateAllRelationships(parentClass);
        return true;
    }

    @Override
    public void cleanUp() {
        if(this.nodeBackground == null)
            return;
        this.world.getChildren().remove(this.nodeBackground);
    }
    
    @Override
    public void setSelected(boolean isSelected) {
        
    }

    @Override
    public boolean getSelected() {
        
        return false;
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
        if(parentClass == null)
            return Point2D.ZERO;
        return parentClass.getLocation();
    }

    @Override
    public void setLocation(Point2D location) {
        if(parentClass == null)
            return;
        parentClass.setLocation(location);
    }
    
    public Rectangle2D getRectBounds()
    {
        if(this.parentVBox == null)
            return null;
        Bounds bounds = this.parentVBox.getBoundsInLocal();
        Point2D offset = getLocation();
        Rectangle2D rect = new Rectangle2D(
                bounds.getMinX() + offset.getX(), 
                bounds.getMinY() + offset.getY(), 
                bounds.getWidth(), 
                bounds.getHeight()
            );
        return rect;
    }
}
