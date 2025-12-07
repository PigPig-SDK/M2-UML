package org.umlproject.UI;

import java.awt.Desktop.Action;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.umlproject.*;


import java.util.*;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.umlproject.UMLClass;

public class GuiClass implements DiagramElementListener<UMLClass>, UISelectable, UIPositional {
    public static final Font DEFAULT_CLASS_FONT = Font.font("Monospaced", FontWeight.NORMAL, FontPosture.REGULAR, 18);
    
    private static final double SHOTGUN_DISTANCE_REFIRE = 50;
    
    private Pane world;
    private UMLClass parentClass;
    double mouseAnchorX;
    double mouseAnchorY;
    StackPane nodeBackground;
    VBox dataFieldTextFields;
    VBox methodTextFields;
    //VBox that holds className TextField and VBoxes for data fields and methods.
    VBox parentVBox;
    Point2D lastShotgunLocation = Point2D.ZERO;

    Point2D mouseWorldSpace;
    double padding = 20.0;
    Point2D dragStartLocation = Point2D.ZERO;
    
    List<Button> guiButtons = new LinkedList<>();//No random access is required. Using linked list.
    private boolean isDragging = false;
    private boolean isSelected = false;
    /**
     * Constructor for GuiClass responsible for building the initial class box and setting all the proper
     * actions on its nodes. TextFields will be editable and those edits will be reflected in the underlying
     * UMLDocument singleton. "Add Field Button" will create new TextFields in the Data Fields section of the class
     * box. "Add Method Button" will create new TextFields in the Methods section of the class box.
     * @param world, representing the group that holds all class boxes
     * @param parentClass, the UMLClass which a given GuiClass instance listens to.
     */
    public GuiClass(Pane world, UMLClass parentClass)
    {
        this.world = world;
        world.setFocusTraversable(true);//lets world request focus.
        this.parentClass = parentClass;
        // --- END OF BLOCK ---
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

        //Mouse down.
        nodeBackground.setOnMousePressed(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                GuiCamera.setDragging(true);
                GuiSelect.getInstance().clickUiElement(e, this, isDragging);
                mouseWorldSpace = GuiCamera.screenToWorld(new Point2D(e.getSceneX(), e.getSceneY()));
                Point2D paneWorldSpace = new Point2D(nodeBackground.getLayoutX(), nodeBackground.getLayoutY());
                this.mouseAnchorX = mouseWorldSpace.getX() - paneWorldSpace.getX();
                this.mouseAnchorY = mouseWorldSpace.getY() - paneWorldSpace.getY();

                dragStartLocation = this.parentClass.getLocation();

                //Setup dragStartLocation for multi-drag
                if(!GuiSelect.getInstance().getSelectedObjects().isEmpty() &&
                        GuiSelect.getInstance().getSelectedObjects().contains(this)){
                    for(UISelectable selectable : GuiSelect.getInstance().getSelectedObjects()){
                        if(selectable instanceof org.umlproject.UI.GuiClass guiClass) {
                            guiClass.dragStartLocation = guiClass.parentClass.getLocation();
                        }
                    }
                }
                nodeBackground.requestFocus();
            }
            e.consume(); // Prevent event from propagating to other nodes
        });
        
        //Used for selection
        //Mouse up...
        nodeBackground.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                GuiSelect.getInstance().clickUiElement(e, this, isDragging);
                if (isDragging) {
                    Point2D worldSpace = GuiCamera.screenToWorld(new Point2D(e.getSceneX(), e.getSceneY()));
                    Point2D selectionOffset = new Point2D(worldSpace.getX() - this.mouseAnchorX, worldSpace.getY() - this.mouseAnchorY);
                    if (!selectionOffset.equals(dragStartLocation))
                        this.parentClass.setLocation(selectionOffset, true);
                }
                isDragging = false;
                GuiCamera.setDragging(false);
            }
            e.consume(); // Prevent event from propagating to other nodes
        });
        //On drag...
        this.nodeBackground.setOnMouseDragged(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                isDragging = true;
                //I swear if i have to instantiate another immutable point2d im going to create a wrapper class.
                Point2D worldSpace = GuiCamera.screenToWorld(new Point2D(e.getSceneX(), e.getSceneY()));
                Point2D selectionOffset = new Point2D(worldSpace.getX() - this.mouseAnchorX, worldSpace.getY() - this.mouseAnchorY);
                UMLDocument.executeActionUnderState(DocumentState.SILENT_MOVEMENT, () -> this.parentClass.setLocation(selectionOffset, true));

                //Multi-Drag
                if(!e.isControlDown()) {
                    if (!GuiSelect.getInstance().getSelectedObjects().isEmpty() &&
                            GuiSelect.getInstance().getSelectedObjects().contains(this)) {
                        for (UISelectable selectable : GuiSelect.getInstance().getSelectedObjects()) {
                            if (selectable instanceof org.umlproject.UI.GuiClass guiClass) {
                                if (guiClass == this) continue;
                                Point2D locationOffset = worldSpace.subtract(mouseWorldSpace);
                                UMLDocument.executeActionUnderState(DocumentState.SILENT_MOVEMENT, () ->
                                        guiClass.getParentClass().setLocation(guiClass.dragStartLocation.add(locationOffset),true));
                            }
                        }
                    }
                }
                this.nodeBackground.getParent().requestLayout(); // Force layout update
                
            }
            e.consume(); // Prevent event from propagating to other nodes
        });
    }
    /**
     * This 'shotguns' into the 'occupiedPathCells' to see if any relationships might desire a redraw.
     */
    void shotgunCheckRelationshipOverlap()
    {
        if(lastShotgunLocation.distance(this.getLocation())  <= SHOTGUN_DISTANCE_REFIRE) return;
        
        lastShotgunLocation = getLocation();
        
        List<AStarSegment> perimeterNodes = new ArrayList<>();
        Rectangle2D sourceBounds = getRectBounds();
        Point2D targetCenter = getLocation();
         //Define padded search area around source bounds.
        double searchMinX = sourceBounds.getMinX() - 100;
        double searchMaxX = sourceBounds.getMaxX() + 100;
        double searchMinY = sourceBounds.getMinY() - 100;
        double searchMaxY = sourceBounds.getMaxY() + 100;

        int minI = PathGridMapper.toGridIndex(searchMinX);
        int maxI = PathGridMapper.toGridIndex(searchMaxX);
        int minJ = PathGridMapper.toGridIndex(searchMinY);
        int maxJ = PathGridMapper.toGridIndex(searchMaxY);
        //rewrite so we grab the perimeter just inside the class bounds:
        for(int i = minI; i <= maxI; i++){
            for(int j = minJ; j <= maxJ; j++){
                if(i == minI || i == maxI){
                    AStarSegment nextPerimeterTile = new AStarSegment(i, j, 0.0, 0.0, null);
                    perimeterNodes.add(nextPerimeterTile);
                }

            }
            if(i > minI && i < maxI){
                //Include j == minJ and j = maxJ tiles
                int minimumJ = minJ;
                int maximumJ = maxJ;
                AStarSegment minJTile = new AStarSegment(i, minimumJ, 0.0, 0.0, null);
                AStarSegment maxJTile = new AStarSegment(i, maximumJ, 0.0, 0.0, null);
                perimeterNodes.add(minJTile);
                perimeterNodes.add(maxJTile);
            }
        }
        
        //Find a set list of desired redraws.
        Set<UMLRelationship> allRedrawCalls = new HashSet<>();
        for(AStarSegment ass : perimeterNodes)
        {
            //ass.drawDebug();
            List<UMLRelationship> temp = RelationshipRouter.getInstance().shotgunGetNode(ass);
            if(temp == null) continue;
            
            allRedrawCalls.addAll(temp);
        }
        for(UMLRelationship relationship : allRedrawCalls)
        {
            if(relationship.getSource() == parentClass || relationship.getDestination() == parentClass)
                continue;
            
            if(relationship.getListener() instanceof GuiRelationship guiRelationship)
            {
                guiRelationship.queuedRedraw = true;
                guiRelationship.update(relationship);
            }
        }
    }
    /**
     * Helper function that lets the user rename the class name TextField.
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
        });
    }

    /**
     * Helper method that sets the action on the addMethod button. The action will
     * generate a dummy method with a unique name, for example: method1 INT PARAM1,
     * and then insert it into the UMLDocument singleton.
     * @param addMethod, button to set an action on.
     */
    public void addMethodButtonClickable(Button addMethod){
        addMethod.setOnAction(e -> {
            //Create new uniquely named dummy method and add to parent class.
            TextField newDummyMethod = new TextField(parentClass.findValidMethodDummySignature());
            String[] newDummyMethodAsArray = newDummyMethod.getText().split(" ");
            String dummyName = newDummyMethodAsArray[0];
            String dummyParamTypeAsString = newDummyMethodAsArray[1];
            DataType dummyParamDataType = DataType.stringToDatatype(dummyParamTypeAsString);
            String dummyParamName = newDummyMethodAsArray[2];
            UMLParameter dummyParam = new UMLParameter(dummyParamName, dummyParamDataType, null);
            ArrayList<UMLParameter> dummyParamList = new ArrayList<>();
            dummyParamList.add(dummyParam);
            UMLMethod dummyMethod = new UMLMethod(dummyName, dummyParamList);
            parentClass.addMethod(dummyMethod);
            //at this point update is called
            e.consume();
        });
    }

    /**
     * Method sets actions on the methodRow TextField that
     * updates underlying UMLMethod whenever the method TextField is updated with an acceptable
     * value.
     * @param methodRow, HBox containing a delete button and a TextField representing a method signature.
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
            handleMethodUpdate(newMethodTextField, methodRow);
            e.consume();
        });
        newMethodTextField.focusedProperty().addListener((obs, oldVal, newVal) ->{
            if(!newVal) {
                handleMethodUpdate(newMethodTextField, methodRow);
            }
        });
    }

    /**
     * Helper method for the linkTextFieldToMethod function. It will generate a new UMLMethod object
     * from TextField input after Enter is pressed or the user clicks somewhere else in the UML editor taking
     * focus away from the TextField.
     * @param newMethodTextField, TextField containing the Method data.
     * @param methodRow, HBox used to hold the TextField and a delete button.
     */
    public void handleMethodUpdate(TextField newMethodTextField, HBox methodRow){
        String[] newMethodAsStringArray = newMethodTextField.getText().split(" ");
        if(newMethodAsStringArray.length % 2 == 0){
            System.out.println("Invalid number of arguments!");
            //reset text
            newMethodTextField.setText(newMethodTextField.getText());
            return;
        }
        String newMethodName = newMethodAsStringArray[0];
        //if there is a list of parameters, construct an arrayList of UMLParameter objects
        ArrayList<UMLParameter> params = new ArrayList<>();
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
        UMLDocument.executeActionUnderState(DocumentState.MASS_OPERATION, ()->
        {
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
            UMLDocument.saveMementoState();
        });
    }

    /**Helper method for the Update function.
     * Needs to cycle through the hashmap of method arrayLists and construct HBoxes. Each HBox will consist of
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
                HBox methodRow = new HBox(-5);
                methodRow.setAlignment(Pos.CENTER);
                //create delete button
                Button deleteMethod = new Button("-");
                FXUtility.getInstance().applyIconsToButtons("Remove",deleteMethod,"/org/umlproject/icons/minus_button.png",25,25);
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
                methodText.setStyle("-fx-font-size: 16px; "
                + "-fx-font-weight: bold; "
                + "-fx-background-radius: 0 10 10 0; "
                + "-fx-border-radius: 0;" 
                + "-fx-border-width: 0;");
                
                methodText.setPrefWidth(300);
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
     * into the parent class. Responds to either pressing Enter within the textbox or clicking away from the
     * text box, i.e. taking focus away.
     * @param fieldRow, the HBox containing the old DataField attributes as a string.
     */
    public void linkTextFieldToDataField(HBox fieldRow){

        if(fieldRow == null || fieldRow.getChildren().isEmpty()){
            throw new IllegalArgumentException("provided field row HBox is null");
        }
        TextField newField = (TextField)fieldRow.getChildren().get(1);
        newField.setOnAction(e ->{
            handleDataFieldUpdate(newField, fieldRow);
            e.consume();
        });
        //make it so when the textField loses focus, then the handleDataFieldUpdate() method is called aswell
        //with whatever text the user has typed in.
        //newVal and oldVal are booleans representing the focus of the textField. When clicked, oldVal == false
        //newVal == true. When click away from TextField, their values switch. Thus when TextField loses focus,
        //handleDataFieldUpdate() is called.
        newField.focusedProperty().addListener((obs, oldVal, newVal)->{
            if(!newVal){
                handleDataFieldUpdate(newField, fieldRow);
            }
        });

    }

    /**Helper method for linkTextFieldToDataField method. This will be the setOnAction and focusedProperty().addListener()
     * method that gets assigned to a given TextField. The method will parse the contents of the TextField and
     * attempt to create a new UMLDataField object and insert it into the UMLDocument. If it succeeds, then the old
     * UMLDataField object will be deleted, and the corresponding TextBox will be deleted from the dataFieldsVBox
     * @param newField, TextField representing new user input.
     * @param fieldRow, Hbox which will hold the newField and delete button aswell as a copy of the UMLDataField for
     *                  future deletion.
     */
    public void handleDataFieldUpdate(TextField newField, HBox fieldRow){
        String dataFieldText = newField.getText();
        String[] textAsArray = dataFieldText.split(" ");
        if(textAsArray.length != 3){

            System.out.println("Invalid number of arguments! Enter Visibility DataType Name.");
            newField.setText("Visibility Type Name");
            return;
        }

        String visibilityString = textAsArray[0];
        String typeString = textAsArray[1];
        String dataFieldName = textAsArray[2];

        //make sure the user entered a valid Visibility value.
        if(!Visibility.acceptableVisibility(visibilityString)){
            System.out.println("Invalid visibility type! Enter: Public, Private, Protected, or Package.");
            newField.setText("Visibility Type Name");
            return;
        }
        Visibility visibility = Visibility.stringVisibility(visibilityString);
        DataType dataType = DataType.stringToDatatype(typeString);

        
        UMLDataField dataField = new UMLDataField(dataFieldName, (dataType == DataType.OTHER)? textAsArray[1] : null , dataType, visibility);
        
        UMLDocument.executeActionUnderState(DocumentState.MASS_OPERATION, () ->
        {
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
                UMLDocument.saveMementoState();
            }
            else{
                //if addField fails we need to reset the TextField to have its previous text.
                System.out.println("Datafield is a duplicate or invalid!");
                newField.setText(newField.getText());
            }
        });
        
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
        for (String sortedKey : sortedKeys) {
            HBox fieldRow = new HBox(-5);
            fieldRow.setAlignment(Pos.CENTER);
            UMLDataField nextField = UMLDataFields.get(sortedKey);
            //store the UMLDataField for easy removal with delete button
            fieldRow.setUserData(nextField);
            fieldAsString = String.format("%s %s %s",nextField.getVisibility(), nextField.getTypeAsString(), nextField.getName());
            TextField nextTextField = new TextField(fieldAsString);
            nextTextField.setStyle("-fx-font-size: 16px; "
                + "-fx-font-weight: bold; "
                + "-fx-background-radius: 0 10 10 0; "
                + "-fx-border-radius: 0;" 
                + "-fx-border-width: 0;");
            
            nextTextField.setPrefWidth(300);
            //setUserData as the string representing the field so that we can easily delete the field later if need be.
            nextTextField.setUserData(fieldAsString);
            Button deleteField = new Button("-");
            FXUtility.getInstance().applyIconsToButtons("Remove",deleteField,"/org/umlproject/icons/minus_button.png",25,25);
            deleteField.setFocusTraversable(false);
            deleteField.setOnAction(e -> {
                parentClass.removeField(((UMLDataField)fieldRow.getUserData()).getName());
                dataFieldTextFields.getChildren().remove(fieldRow);
                e.consume();
            });
            
            fieldRow.getChildren().addAll(deleteField, nextTextField);
            linkTextFieldToDataField(fieldRow);
            dataFieldTextFields.getChildren().add(fieldRow);
        }
    }
    /**
     * update is responsible for redrawing the classBox every time a data field or method is added or removed.
     * It will recreate the class box with the updated nodes in a fashion reminiscent of the GuiClass constructor.
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
        this.parentVBox.setMinWidth(300);
        this.parentVBox.setPrefWidth(300);
        this.parentVBox.setAlignment(Pos.CENTER);
        this.nodeBackground = new StackPane();
        this.nodeBackground.setManaged(false);


        //create a rectangle background for the class.
        updateVbox(false, 0);

        //Create modifiable className and put into VBox
        TextField classNameField = new TextField(parentClass.getClassName());
        classNameField.setStyle("-fx-font-size: 16px; "
                + "-fx-font-weight: bold; "
                + "-fx-background-radius: 0 0 10 10; "
                + "-fx-border-radius: 0;" 
                + "-fx-border-width: 0;");
        classNameField.setMaxWidth(250);
        classNameField.setFocusTraversable(false);
        //Make it so the UMLClass class name updates after modifying classNameField
        makeClassNameRenamable(classNameField);
        Separator separator = new Separator();
        separator.setMouseTransparent(true);
        this.parentVBox.getChildren().addAll(classNameField, separator);

        //set up dataFields
        //retrieve dataFields hashMap, retrieve keySet, convert into an array, then cycle through each
        //and create a textField and put in dataFieldsVBox.
        Label dataFieldsLabel = new Label("Data Fields");
        dataFieldsLabel.setFont(DEFAULT_CLASS_FONT);
        HashMap<String, UMLDataField> UMLDataFields = desiredElement.getFieldsAll();
        convertDataFieldsToHBoxes(UMLDataFields);
        //create AddField Button, set action to make a new DataField
        HBox addFieldConfiguration = addClassParam("Add Data Field", this::addDataFieldButtonClickable);
        
        //convertDataFieldsToHBoxes filled the dataFIeldTexxtFields VBox with all the delete button
        //TextField combinations. Note that dataFieldTextFields VBox was reset upon calling update().
        this.parentVBox.getChildren().addAll(dataFieldsLabel, this.dataFieldTextFields, addFieldConfiguration, new Separator());

        //-------------------------------------------------------------------------------------------
        //create methods
        Label methodsLabel = new Label("Methods");
        methodsLabel.setFont(DEFAULT_CLASS_FONT);
        
        HashMap<String, ArrayList<UMLMethod>> umlMethods = desiredElement.getMethodsAll();
        convertMethodsToHBoxes(umlMethods);
        HBox addMethodConfiguration = addClassParam("Add method", this::addMethodButtonClickable);
        this.parentVBox.getChildren().addAll(methodsLabel, this.methodTextFields, addMethodConfiguration);
        //-------------------------------------------------------------------------------------------------

        this.nodeBackground.getChildren().add(this.parentVBox);

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
        updateAllRelationships(desiredElement);
        setSelected(isSelected);//Update our selected state
    }
    
    public HBox addClassParam(String title, Consumer<Button> onClicked)
    {
        HBox fieldRow = new HBox(1);
        Button addDataField = new Button(title);
        FXUtility.getInstance().applyIconsToButtons(title, addDataField,"/org/umlproject/icons/add_button.png",30,30);
        
        onClicked.accept(addDataField);
        
        guiButtons.add(addDataField);

        fieldRow.getChildren().addAll(addDataField);//Incase we want to add anything else...
        fieldRow.setAlignment(Pos.CENTER);
        return fieldRow;
    }
    
    private void updateVbox(boolean isSelected, double time)
    {
        if(this.parentVBox == null)
            return;
        
        double arc = 30;
        double borderWidth = 5;
        double radiiSpecial = arc - borderWidth / 2;
        if(isSelected)
        {
            borderWidth = 5 + 2*Math.sin(time * 0.00000001);
            //this.parentVBox.setStrokeDashOffset();
            
            BorderStrokeStyle dashedStyle = new BorderStrokeStyle(
                    StrokeType.INSIDE,                  // stroke type
                    StrokeLineJoin.MITER,               // corner join
                    StrokeLineCap.BUTT,                 // line cap
                    10,                                 // miter limit
                    10*Math.sin(time * 0.000000001),
                Arrays.asList(30.0, 15.0)
            );
            Insets borderInsets = new Insets(5);
            // Background with adjusted radii for the border
            parentVBox.setBackground(new Background(new BackgroundFill(
                GuiColor.CLASS_BACKGROUND_COLOR,
                new CornerRadii(0,0,radiiSpecial,radiiSpecial, false),
                new Insets(-10)
            )));

            // Border with proper radii
            parentVBox.setBorder(new Border(new BorderStroke(
                GuiColor.SELECTION_COLOR,
                dashedStyle,
                new CornerRadii(0,0,arc,arc, false),
                new BorderWidths(borderWidth),
                new Insets(-15)
            )));
        }
        else
        {
            Insets borderInsets = new Insets(5);
            
            // Background with adjusted radii for the border
            parentVBox.setBackground(new Background(new BackgroundFill(
                GuiColor.CLASS_BACKGROUND_COLOR,
                new CornerRadii(0,0,radiiSpecial,radiiSpecial, false),
                new Insets(-10)
            )));

            // Border with proper radii
            parentVBox.setBorder(new Border(new BorderStroke(
                Color.BLACK,
                    BorderStrokeStyle.SOLID,
                new CornerRadii(0,0,arc,arc, false),
                new BorderWidths(borderWidth),
                new Insets(-15)
            )));
        }
    }
    
    public void formatForScreenshot()
    {
        for(Button b : guiButtons)
        {
            b.setVisible(false);
        }
    }
    /**This method will update the location of the gui element representing
     *the umlClass. That is, any calls to this function will visibly move
     *the class box on the screen.
     * @param desiredElement, this is the UMLClass whose location data field is used to update nodeBackground
     */
     @Override
    public void updateLocation(UMLClass desiredElement) {
        shotgunCheckRelationshipOverlap();
        
        if(this.nodeBackground == null)
            return;
        updateAllRelationships(desiredElement);
        this.nodeBackground.setLayoutX(desiredElement.getLocation().getX());
        this.nodeBackground.setLayoutY(desiredElement.getLocation().getY());

        //System.out.println("stackpane layout is changed to:" + ((UMLClass)(desiredElement)).getLocation());
        if(this.nodeBackground.getParent() == null) return;
        
        this.nodeBackground.getParent().requestLayout();
    }
    /**
     * Updates the GUI element of all my associated relationships.
     * Note: this bypasses global listener updates.
     */
    public void updateAllRelationships(UMLClass desiredElement)
    {

        if(UMLDocument.getDocumentState() != DocumentState.MEMENTO_STATE_RESET)//select newly added items.
        {
            ArrayList<UMLRelationship> list = UMLDocument.getInstance().getAllRelationshipsInstanceOf(desiredElement.getClassName());
            for(UMLRelationship relationship : list)//Update all relationship GUI
            {
                if(relationship == null)
                {
                   continue;
                }
                relationship.updateListener(false);
            }
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
        guiButtons.clear();
        if(this.nodeBackground == null)
            return;
        this.world.getChildren().remove(this.nodeBackground);
    }
    @Override
    public void setSelected(boolean isSelected) {
        
        this.isSelected = isSelected;
        updateVbox(isSelected, 0);
    }

    @Override
    public boolean getSelected() {
        
        return this.isSelected;
    }

    public UMLClass getParentClass(){
        return this.parentClass;
    }

    @Override
    public boolean contains(Point2D selectionPoint) {
        return this.getRectBounds().contains(selectionPoint);
    }

    @Override
    public boolean intersects(Rectangle2D selectionRectangle) {

        //Reused getRectBounds code with more accurate to visual bounds.
        if(this.parentVBox == null)
            return false;
        //debugging lines
        //GuiDebugging.showBounds(selectionRectangle, 5, 5, Color.RED);
        //GuiDebugging.showBounds(rect, 5, 5, Color.GREEN);
        return selectionRectangle.intersects(getRectBounds());
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
        parentClass.setLocation(location, false);
    }

    /**
     * Helper method to prevent classbox overlap when making a new method.
     * It is used to construct a rectangle representing an existing GuiClass Object.
     *
     * @return, rectangle representing the dimensions of an existing GuiClass object.
     */
    public Rectangle2D getRectBounds()
    {
        if(this.nodeBackground == null)
            return null;
        Bounds bounds = this.parentVBox.getBoundsInLocal();

        Point2D offset = getLocation();

        //Takes border outsets into account when calculating bounds
        Rectangle2D rect = new Rectangle2D(
                bounds.getMinX() + offset.getX()
                        - bounds.getWidth() / 2.0 + this.parentVBox.getBorder().getOutsets().getLeft(),
                bounds.getMinY() + offset.getY()
                        - bounds.getHeight() / 2.0 + this.parentVBox.getBorder().getOutsets().getTop(),
                bounds.getWidth(),
                bounds.getHeight()
        );
        return rect;
    }

    @Override
    public String toString(){
        return this.getParentClass().toString();
    }

    @Override
    public void selectionAnimationUpdate(float time) {

        updateVbox(true, time);
    }
}
