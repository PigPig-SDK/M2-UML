package org.umlproject.UI;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.umlproject.*;


import java.util.*;
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
            //Create new uniquely named dummy method and add to parent class.
            TextField newDummyMethod = new TextField(parentClass.findValidMethodDummySignature());
            String[] newDummyMethodAsArray = newDummyMethod.getText().split(" ");
            String dummyName = newDummyMethodAsArray[0];
            System.out.println("dummyName: " + dummyName);
            String dummyParamTypeAsString = newDummyMethodAsArray[1];
            System.out.println("dummyParam Type: " + dummyParamTypeAsString);
            DataType dummyParamDataType = DataType.stringToDatatype(dummyParamTypeAsString);
            String dummyParamName = newDummyMethodAsArray[2];
            System.out.println("dummy param Name: " + dummyParamName);
            UMLParameter dummyParam = new UMLParameter(dummyParamName, dummyParamDataType, null);
            ArrayList<UMLParameter> dummyParamList = new ArrayList<>();
            dummyParamList.add(dummyParam);
            UMLMethod dummyMethod = new UMLMethod(dummyName, dummyParamList);
            System.out.println("The method being added is: " + dummyMethod.toString());
            parentClass.addMethod(dummyMethod);
            //at this point update is called
            e.consume();
        });
    }

    /**
     * Method updates underlying UMLMethod whenever the method TextField is updated with an acceptable
     * value.
     * @param methodRow
     */
    public void linkTextFieldToMethod(HBox methodRow){
        if(methodRow == null || methodRow.getChildren().isEmpty()){
            throw new IllegalArgumentException("provided method row HBox is null or empty!");
        }
        //Need to convert user input to a string array to build the new UMLMethod.
        // First check to see that the user entered an appropriate number of inputs to build the method. String[] must be atleast
        //size 1 to have a name and no parameters. Remaining string consists of <type paramName> pairs, thus the string
        //Array must have an odd length.
        TextField newMethodTextField = (TextField)methodRow.getChildren().get(1);
        newMethodTextField.setOnAction(e ->{
            String[] newMethodAsStringArray = newMethodTextField.getText().split(" ");
            if(newMethodAsStringArray.length % 2 == 0){
                System.out.println("Invalid number of arguments!");
                //reset text
                newMethodTextField.setText(newMethodTextField.getText());
                return;
            }
            String newMethodName = newMethodAsStringArray[0];
            //if there is a list of parameters, construct an arrayList of UMLParameter objects
            ArrayList<UMLParameter> params = new ArrayList<UMLParameter>();
            for(int i = 1; i < newMethodAsStringArray.length; i+=2){
                String type = newMethodAsStringArray[i];
                String customTypeName;
                DataType paramType = DataType.stringToDatatype(type);
                if(paramType == DataType.OTHER){
                    customTypeName = type;
                }
                else{
                    customTypeName = null;
                }
                String paramName = newMethodAsStringArray[i + 1];
                UMLParameter newParam = new UMLParameter(paramName, paramType, customTypeName);
                params.add(newParam);
            }
            UMLMethod newMethod = new UMLMethod(newMethodName, params);
            //attempt to add method
            boolean methodAddSuccessful = parentClass.addMethod(newMethod);
            if(!methodAddSuccessful){
                System.out.println("Method is a duplicate or invalid!");
                newMethodTextField.setText((String)newMethodTextField.getUserData());
                return;
            }
            // delete old method if new input can be successfully added to UMLClass, set user data of newMethodTextField
            //to be the most recently entered string. Then set userData of methodRow to be the new method name and index
            //in ArrayList.
            String oldName;
            int oldIndex;
            String[] oldUserDataAsString = (String[])methodRow.getUserData();
            System.out.println("length of old data " + oldUserDataAsString.length);
            if(oldUserDataAsString != null && oldUserDataAsString.length == 2 && !(oldUserDataAsString[0].isEmpty() ||
                    oldUserDataAsString[1].isEmpty())) {
                //old method name is 0th index, index of old method to remove is 1st index.
                oldName = oldUserDataAsString[0];
                oldIndex = Integer.parseInt(oldUserDataAsString[1]);
                System.out.println("the old user data is: " + oldName + ", " + oldIndex);
                parentClass.removeMethod(oldName, oldIndex);
            }
            //return focus to world
            e.consume();
            world.requestFocus();
        });

    }

    /**Helper method for the Update function.
     * Needs to cycle through the hashmap of method arrayLists and construct HBoxes each consisting of
     * a delete button and a TextField matching the method signature. These HBoxes are then inserted into
     * methodTextFields VBox and the method returns.
     * @param methods, hashmap of UMLMethod array lists.
     */
    public void convertMethodsToHBoxes(HashMap<String, ArrayList<UMLMethod>> methods){

        List<String> methodKeys = new ArrayList<String>(methods.keySet());
        Collections.sort(methodKeys);
        String[] methodKeysArray = methodKeys.toArray(new String[0]);
        for(int i = 0; i < methodKeysArray.length; i++){
            ArrayList<UMLMethod> nextMethods = methods.get(methodKeysArray[i]);
            for(int j = 0; j < nextMethods.size(); j++){
                HBox methodRow = new HBox(10);
                //create delete button
                Button deleteMethod = new Button("-");
                deleteMethod.setFocusTraversable(false);
                deleteMethod.setOnAction(event -> {
                    if(((String[])methodRow.getUserData() != null)) {
                        //Note that the userData of methodRow will be a size 2 array where the index 0 stores
                        //the method name, and index 1 stores the index into the methods array list to be removed.
                        String[] userData = (String[])methodRow.getUserData();
                        parentClass.removeMethod(userData[0], Integer.parseInt(userData[1]));
                    }
                    methodTextFields.getChildren().remove(methodRow);
                    event.consume();
                });
                TextField methodText = new TextField(nextMethods.get(j).toString());
                methodRow.getChildren().addAll(deleteMethod, methodText);
                linkTextFieldToMethod(methodRow);
                methodText.setUserData(methodText.getText());
                String[] oldData = {nextMethods.get(j).getMethodName(), String.valueOf(j)};
                methodRow.setUserData(oldData);
                methodTextFields.getChildren().add(methodRow);
            }
        }

    }

    /**
     * Helper method that sets an action on the addDataField button of a class box so that a
     * user can add a new datafield to both the UMLClass and a new TextField to the class box.
     * @param addDataField, button to be modified.
     */
    public void addDataFieldButtonClickable(Button addDataField){
        addDataField.setOnAction(e -> {
            String dummyFieldSignature = parentClass.findValidFieldDummySignature();
            System.out.println("the dummyFieldSignature is: " + dummyFieldSignature);
            TextField newField = new TextField(dummyFieldSignature);
            //create a new UMLDataFIeld object and insert into parent class
            String[] dummyFieldAsArray = dummyFieldSignature.split(" ");
            Visibility dummyVisibility = Visibility.stringVisibility(dummyFieldAsArray[0]);
            //dummyDataType is always int
            DataType dummyDataType = DataType.stringToDatatype(dummyFieldAsArray[1]);
            String dummyName = dummyFieldAsArray[2];
            UMLDataField dummyField = new UMLDataField(dummyName, dummyDataType, dummyVisibility);
            parentClass.addField(dummyField);
            e.consume();
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
        TextField newField = (TextField)fieldRow.getChildren().get(1);
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
                //Must delete old data field from UMLDocument
                UMLDataField oldField = (UMLDataField)fieldRow.getUserData();
                if(oldField != null) {
                    this.parentClass.removeField(oldField.getName());
                }
                //set newField and fieldRow user data to their new values.
                newField.setUserData(newField.getText());
                fieldRow.setUserData(dataField);
                System.out.println("Field was added and class box will be updated!");
                //update is automatically called by UMLClass to redraw class box.
            }
            else{
                //if addField fails we need to reset the TextField to have its previous text.
                System.out.println("Datafield is a duplicate or invalid!");
                newField.setText(newField.getText());
            }
            world.requestFocus();
        });
        //Platform.runLater(() -> world.requestFocus());

    }

    /**
     * Converts contents of data field hashmap into HBoxes each consisting of a delete button and a TextField
     * whose description matches the data field signature. HBoxes are inserted into the dataFieldTextFields VBox
     * and then the method returns.
     * @param UMLDataFields, hashmap of UMLDataFields.
     */
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
