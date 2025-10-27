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
            System.out.println("Mouse dragged on StackPane to: " + e.getSceneX() + ", " + e.getSceneY());
            double newX = e.getSceneX() - this.mouseAnchorX;
            double newY = e.getSceneY() - this.mouseAnchorY;
            System.out.println("Setting UMLClass location to: " + newX + ", " + newY);
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
     * @param fieldRow, the HBox containing the old DataField attributes as a string.
     */
    public void linkTextFieldToDataField(HBox fieldRow){

        if(fieldRow == null || fieldRow.getChildren().isEmpty()){
            throw new IllegalArgumentException("provided field row HBox is null");
        }
        //retrieve old name of datafield
        String oldName;
        TextField newField = (TextField)fieldRow.getChildren().get(1);

        String[] newFieldAsString = ((String)(newField.getUserData())).split(" ");
        if(newFieldAsString.length == 3) {
            oldName = newFieldAsString[2];
        }
        else{
            //if this textfield is brand new, i.e. has the value: Visibility Type Name, then oldName is empty
            oldName = "";
        }
        newField.setOnAction(e ->{
            String customTypeName = "";
            String dataFieldText = newField.getText();
            String[] textAsArray = dataFieldText.split(" ");
            if(textAsArray.length != 3){

                System.out.println("Invalid number of arguments! Enter Visibility DataType Name.");
                newField.setText("Visibility Type Name");
                return;
            }
            //make sure the user entered a valid Visibility value.
            boolean acceptableVisibilityStatus = Visibility.acceptableVisibility(textAsArray[0]);
            if(!acceptableVisibilityStatus){
                System.out.println("Invalid visibility type! Enter: Public, Private, Protected, or Package.");
                newField.setText("Visibility Type Name");
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
            UMLDataField dataField = new UMLDataField(dataFieldName, customTypeName, dataType, visibility);


            //attempt to add the field
            boolean success = this.parentClass.addField(dataField);
            if(success){
                //Renaming a textField like this results in two datafields existing in UMLDocument
                //oldField and the new one. Now we must delete oldField, using the name of the oldField
                // from UMLDocument and then
                //remove its text field from the VBox and let update() draw a new one.
                //then set the UserData of newField to its current value.
                if(!oldName.isEmpty()) {
                    this.parentClass.removeField(oldName);
                }
                newField.setUserData(newField.getText());
                //set the userData on the field row to be the constructed data field. This way if you delete the
                //default Visibility Type Name field you won't get an error.
                fieldRow.setUserData(dataField);
                //this.dataFieldTextFields.getChildren().remove(oldName);
                System.out.println("Field was added and class box will be updated!");
            //update is automatically called by UMLClass to redraw class box.

            }
            else{
                System.out.println("Datafield is a duplicate or invalid!");
                newField.setText("Visibility Type Name");
            }
            world.requestFocus();
        });
        Platform.runLater(() -> world.requestFocus());

    }

    /**
     * Helper method that sets the action on the addMethod button. This method will
     * create a method TextField with the argument: Visibility ReturnType MethodName Type param1 Type param2 ...
     * indicating that the user should enter a visibility followed by a space, then a return type followed by a space
     * and so on to generate a method signature. Set an action on the TextField to make it create the new method
     * if the input is valid and the signature isn't a duplicate.
     * @param addMethod, button to set an action on.
     */
    public void addMethodButtonClickable(Button addMethod){

        addMethod.setOnAction(e -> {
            TextField newMethod = new TextField("Visibility ReturnType Name Type Param1 Type Param2 ... Type ParamN");
            newMethod.setUserData(newMethod.getText());
            //check method textbox isn't a duplicate
            boolean duplicateTextMethod = false;
            for(Node fieldBox : this.methodTextFields.getChildren()){
                if(fieldBox instanceof HBox){
                    if(((HBox)(fieldBox)).getChildren().size() == 2){
                        //retrieve TextField from HBox
                        TextField method = (TextField)((HBox)(fieldBox)).getChildren().get(1);
                        if(method.getText().equals(newMethod.getText())){
                            duplicateTextMethod = true;
                            System.out.println("Method is a duplicate!");
                            break;
                        }
                    }
                }
            }
            //if duplicate TextField, print message for user, consume event and return.
            if(duplicateTextMethod){
                System.out.println("Must update Visibility ReturnType Name Type Param1 ... of previous" +
                        "method before another one can be added.");
                e.consume();
                return;
            }
            else{

                //Link this TextField to a UMLMethod in underlying UMLDocument and put it in HBox with delete button.
                newMethod.setFocusTraversable(false);
                HBox methodRow = new HBox(10);
                Button deleteField = new Button("-");
                deleteField.setFocusTraversable(false);
                deleteField.setOnAction(event -> {
                    if(((String[])methodRow.getUserData() != null)) {
                        //Note that the userData of methodRow will be a size 2 array where the index 0 stores
                        //the method name, and index 1 stores the index into the methods array list to be removed.
                        String[] userData = (String[])methodRow.getUserData();
                        parentClass.removeMethod(userData[0], Integer.parseInt(userData[1]));
                    }
                    methodTextFields.getChildren().remove(methodRow);
                    event.consume();
                });
                methodRow.getChildren().addAll(deleteField, newMethod);
                linkTextFieldToMethod(methodRow);
                this.methodTextFields.getChildren().add(methodRow);
                e.consume();
            }
        });
    }

    public void linkTextFieldToMethod(HBox methodRow){

    }

    public void convertMethodsToHBoxes(HashMap<String, ArrayList<UMLMethod>> methods){

    }

    /**
     * Helper method that sets an action on the addDataField button of a class box so that a
     * user can add a new datafield to both the UMLClass and a new TextField to the class box.
     * @param addDataField, button to be modified.
     */
    public void addDataFieldButtonClickable(Button addDataField){
        addDataField.setOnAction(e -> {
            TextField newField = new TextField("Visibility Type Name");
            //UserData will typically store the previous string of the TextField. But for the creation of a new
            //button there is no data to store.
            newField.setUserData("");
            //need to make sure there isn't a duplicate "Visibility Type Name" TextField in classbox already.
            boolean duplicateTextField = false;
            for(Node fieldBox : this.dataFieldTextFields.getChildren()){
                if(fieldBox instanceof HBox){
                    if(((HBox)(fieldBox)).getChildren().size() == 2){
                        //retrieve TextField from HBox
                        TextField field = (TextField)((HBox)(fieldBox)).getChildren().get(1);
                        if(field.getText().equals(newField.getText())){
                            duplicateTextField = true;
                            System.out.println("Field is a duplicate!");
                            break;
                        }
                    }
                }
            }
            if(duplicateTextField){
                System.out.println("Must update Visibility Type Name of previous data field before adding a new one.");
                e.consume();
                return;
            }
            else {

                //Link this TextField to a DataField in underlying UMLDocument and put it in HBox with delete button.
                newField.setFocusTraversable(false);
                HBox fieldRow = new HBox(10);
                Button deleteField = new Button("-");
                deleteField.setFocusTraversable(false);
                deleteField.setOnAction(event -> {
                    if(((UMLDataField)fieldRow.getUserData() != null)) {
                        parentClass.removeField(((UMLDataField) fieldRow.getUserData()).getName());
                    }
                    dataFieldTextFields.getChildren().remove(fieldRow);
                    event.consume();
                });
                fieldRow.getChildren().addAll(deleteField, newField);
                linkTextFieldToDataField(fieldRow);

                this.dataFieldTextFields.getChildren().add(fieldRow);
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
        convertDataFieldsToHBoxes(UMLDataFields);
        //create AddField Button, set action to make a new DataField
        Button addDataField = new Button("Add data field");
        addDataFieldButtonClickable(addDataField);

        //convertDataFieldsToHBoxes filled the dataFIeldTexxtFields VBox with all the delete button
        //TextField combinations. Note that dataFieldTextFields VBox was reset upon calling update().
        this.parentVBox.getChildren().addAll(dataFieldsLabel, this.dataFieldTextFields, addDataField, new Separator());

        //-------------------------------------------------------------------------------------------
        //create methods
        Label methodsLabel = new Label("Methods:");
        HashMap<String, ArrayList<UMLMethod>> umlMethods = desiredElement.getMethodsAll();
        convertMethodsToHBoxes(umlMethods);
        Button addMethod = new Button("Add method");
        addMethodButtonClickable(addMethod);
        this.parentVBox.getChildren().addAll(methodsLabel, this.methodTextFields, addMethod);
//-------------------------------------------------------------------------------------------------

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
    }

    public void convertDataFieldsToHBoxes(HashMap<String, UMLDataField> UMLDataFields){
        if(UMLDataFields == null){
            System.out.println("invalidInput");
            return;
        }

        String fieldAsString;
        Set<String> dataFieldKeys = UMLDataFields.keySet();
        List<String> keyList = new ArrayList<>(dataFieldKeys);
        Collections.sort(keyList);
        String[] sortedKeys = keyList.toArray(new String[0]);
        //test print
        System.out.println("length of sorted keys is: " + sortedKeys.length);
        for(int i = 0; i < sortedKeys.length; i++){

            System.out.println(sortedKeys[i]);
        }
        StringBuilder nextText = new StringBuilder();
        for(int i = 0; i < sortedKeys.length; i++){
            HBox fieldRow = new HBox(10);
            UMLDataField nextField = UMLDataFields.get(sortedKeys[i]);
            //store the UMLDataField for easy removal with delete button
            fieldRow.setUserData(nextField);

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
            fieldAsString = nextText.toString();
            TextField nextTextField = new TextField(fieldAsString);
            //setUserData as the string representing the field so that we can easily delete the field later if need be.
            nextTextField.setUserData(fieldAsString);

            Button deleteField = new Button("-");
            deleteField.setFocusTraversable(false);
            deleteField.setOnAction(e -> {
                parentClass.removeField(((UMLDataField)fieldRow.getUserData()).getName());
                dataFieldTextFields.getChildren().remove(fieldRow);
                e.consume();
            });
            fieldRow.getChildren().addAll(deleteField, nextTextField);
            linkTextFieldToDataField(fieldRow);
            dataFieldTextFields.getChildren().add(fieldRow);
            nextText = new StringBuilder();
        }
        return;
    }


    @Override
    public void updateSelected(UMLClass desiredElement) {
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
        this.nodeBackground.setLayoutX(desiredElement.getLocation().getX());
        this.nodeBackground.setLayoutY(desiredElement.getLocation().getY());
        //System.out.println("stackpane layout is changed to:" + ((UMLClass)(desiredElement)).getLocation());
        this.nodeBackground.getParent().requestLayout();
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
}
