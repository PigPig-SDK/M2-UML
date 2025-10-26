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


import java.util.*;

public class GuiClass implements UIListener {
    private Group world;
    private UMLClass parentClass;

    double mouseAnchorX;
    double mouseAnchorY;
    StackPane nodeBackground;
    VBox dataFieldTextFields;
    VBox methodTextFields;
    //VBox that holds className TextField and VBoxes for data fields and methods.
    VBox parentVBox;

    public GuiClass(Group world, UMLClass parentClass)
    {
        this.world = world;
        world.setFocusTraversable(true);//lets world request focus.
        this.parentClass = parentClass;
        this.nodeBackground = new StackPane();


        //create a rectangle background for the class.
        Rectangle background = new Rectangle();
        background.setHeight(300);
        background.setWidth(200);
        background.setFill(Color.MINTCREAM);
        background.setStroke(Color.BLACK);

        //Create modifiable className and put into VBox
        TextField classNameField = new TextField(parentClass.getClassName());
        classNameField.setStyle("-fx-font-size: 10px; -fx-font-weight: bold");
        classNameField.setMaxWidth(250);
        classNameField.setFocusTraversable(false);
        //Make it so the UMLClass class name updates after modifying classNameField and pressing enter.
        makeClassNameRenamable(classNameField);

        //Create VBox for DataFields of class.
        Separator separator1 = new Separator();
        Label dataFieldsLabel = new Label("Data Fields:");
        dataFieldTextFields = new VBox(10);
        Button addDataField = new Button("Add data field");
        //Set action on addDataField so you can create new data field Textfield after clicking.
        addDataFieldButtonClickable(addDataField);

        Separator separator2 = new Separator();
        Label methodsLabel = new Label("Methods:");

        //insert className textField
        parentVBox = new VBox(classNameField);
        parentVBox.setSpacing(10);
        parentVBox.getChildren().addAll(separator1, dataFieldsLabel, dataFieldTextFields, addDataField, separator2, methodsLabel);
        parentVBox.setAlignment(Pos.TOP_CENTER);
        parentVBox.setPadding(new Insets(10, 0, 0, 0));

        nodeBackground.getChildren().addAll(background, parentVBox);


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

    /**
     * Helper function that lets the user rename the class name TextField
     * @param classNameField, TextField object representing the current class name/
     */
    private void makeClassNameRenamable(TextField classNameField){
        classNameField.setOnAction(e -> {
            String newName = classNameField.getText();
            String oldName = parentClass.getClassName();
            //update UMLDocument with new class name if new name is unique.
            boolean updateSuccess = updateRename(oldName, newName);
            if(!updateSuccess){
                classNameField.setText(oldName);
            }
            //remove class function in UMLDocument removes listener, so we need to reapply it.
            //and consume the event e.
            parentClass.setListener(this);
            e.consume();
            //Move focus elsewhere. This is necessary to accept changes to TextField and remove
            //cursor from TextField.
            world.requestFocus();
            //for debugging to see if class name updates in UMLDocument
        });
    }

    /**sets an action on the data field TextFields of class box, so that when a new string representing
     * a datafield is entered, it will be parsed and transformed into a UMLDataField and then inserted
     * into the parent class.
     * @param oldField, the TextField containing the old DataField attributes as a string.
     */
    public void linkTextFieldToDataField(TextField oldField){
        oldField.setOnAction(e ->{
            String customTypeName = "";
            String dataFieldText = oldField.getText();
            String[] textAsArray = dataFieldText.split(" ");
            if(textAsArray.length < 3){
                System.out.println("insufficient number of arguments");
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
            if(success){
                //Renaming a textField like this results in two datafields existing in UMLDocument
                //oldField and the new one. Now we must delete oldField from UMLDocument and then
                //remove its text field from the VBox and let update() draw a new one.
                parentClass.removeField(oldField.getText());
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
            newField.setFocusTraversable(false);
            linkTextFieldToDataField(newField);
            dataFieldTextFields.getChildren().add(newField);
            e.consume();
        });
    }
    
    @Override
    public void update(UMLDiagramElement desiredElement) {
       //reset VBoxes
        dataFieldTextFields = new VBox(10);
        methodTextFields = new VBox(10);
        parentVBox = new VBox(10);
        //create a rectangle background for the class.
        Rectangle background = new Rectangle();
        background.setHeight(300);
        background.setWidth(200);
        background.setFill(Color.MINTCREAM);
        background.setStroke(Color.BLACK);

        //create modifiable className and put into VBox
        TextField classNameField = new TextField(parentClass.getClassName());
        classNameField.setStyle("-fx-font-size: 10px; -fx-font-weight: bold");
        classNameField.setMaxWidth(250);
        classNameField.setFocusTraversable(false);
        //Make it so the UMLClass class name updates after modifying classNameField
        //and pressing enter.
        makeClassNameRenamable(classNameField);


        //set up dataFields
        //retrieve dataFields hashMap, retrieve keySet, convert into an array, then cycle through each
        //and create a textField and put in VBox. Put VBox into nodeBackground.
        HashMap<String, UMLDataField> UMLDataFields = ((UMLClass)(desiredElement)).getFieldsAll();
        ArrayList<String> fieldsAsStrings = convertDataFieldsToStrings(UMLDataFields);
        

        
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
        }
        return fieldsAsStrings;
    }

    @Override
    public void updateSelected(UMLDiagramElement desiredElement) {
    }


    //This method will update the location of the gui element representing
    //the umlClass. That is, any calls to this function will visibly move
    //the class box on the screen.
    @Override
    public void updateLocation(UMLDiagramElement desiredElement) {

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
    
    
}
