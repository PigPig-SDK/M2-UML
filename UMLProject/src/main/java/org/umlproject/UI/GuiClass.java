package org.umlproject.UI;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.umlproject.*;

import java.util.Optional;

public class GuiClass implements UIListener {
    private Group world;
    private UMLClass parentClass;


    public GuiClass(Group world, UMLClass parentClass)
    {
        this.world = world;
        this.parentClass = parentClass;

        //create a rectangle background for the class.
        Rectangle background = new Rectangle();
        background.setHeight(200);
        background.setWidth(200);
        background.setFill(Color.LIGHTGRAY);
        background.setStroke(Color.BLACK);

        StackPane nodeBackground = new StackPane();
        nodeBackground.getChildren().add(background);

        //needs to create a node object and insert it into group
        //gui object will consist of an outer VBox which will hold a textField for
        //class name and two VBoxes for the fields and methods, there will be two HBoxes one that
        //holds a dataFields: label and a addDataFields button and then another one that holds a
        //methods: label and a addMethods button.
        VBox guiClassNode = new VBox();
        Text className = new Text(parentClass.getClassName());

        //create data field section of gui class node. Will have label "Data Fields" and a button
        //to add new DataFields followed by a list of current Data fields.
        HBox dataFieldsLabelAndAdd = new HBox();
        Label dataFields = new Label("Data Fields:");
        Button addField = new Button("add Data Field");

        //add listener to addField button that prompts user for dataField name and type
        //then use these inputs to create a new dataField and update the umlClass
        addField.setOnAction(e -> {
            String newFieldTypeAndName = promptForFieldTypeAndName();
            if(newFieldTypeAndName != null && !newFieldTypeAndName.isEmpty()){
                String[] dataFieldTypeAndName = newFieldTypeAndName.split(" ");

                String typeName = dataFieldTypeAndName[0];
                DataType typeOfField = DataType.stringToDatatype(typeName);

                String nameOfField = dataFieldTypeAndName[1];

                String visibility = dataFieldTypeAndName[2];

                //create dataField and add to umlClass
                UMLDataField newField = new UMLDataField(nameOfField, typeOfField, Visibility.stringVisibility(visibility));
                boolean success = parentClass.addField(newField);
                if(success){
                    System.out.println("Success Field was added!");
                }
                else{
                    System.out.println("Failure! datafield was invalid or a duplicate!");
                }

            }
        });
        dataFieldsLabelAndAdd.getChildren().addAll(dataFields, addField);

        //create methods section of gui class node. Will have label "methods" and a button to
        //add new methods followed by a list of current methods.
        HBox methodsLabelAndAdd = new HBox();
        Label methods = new Label("methods:");
        Button addMethod = new Button("add method");

        //add listener to addMethod button that prompts user for method name and parameter types
        methodsLabelAndAdd.getChildren().addAll(methods, addMethod);

        VBox dataFieldList = new VBox();
        VBox methodList = new VBox();

        //add all nodes to guiClassNode and put in scene
        guiClassNode.getChildren().addAll(className, dataFieldsLabelAndAdd,dataFieldList, methodsLabelAndAdd, methodList);
        nodeBackground.getChildren().add(guiClassNode);
        world.getChildren().add(nodeBackground);
    }

    /**
     * method will create a popup window within the gui after the user clicks "add Data Field" button
     * The prompt will ask them to type in a field name
     * @return String representing the field name.
     */
    private String promptForFieldTypeAndName(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Data Field name and Type and visibility");
        dialog.setHeaderText("Enter type, name, visiblity of DataField:");
        dialog.setContentText("Field name, type, visibility:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    
    @Override
    public void update(UMLDiagramElement desiredElement) {

        
    }

    @Override
    public void updateSelected(UMLDiagramElement desiredElement) {
    }

    @Override
    public void updateLocation(UMLDiagramElement desiredElement) {
    }

    @Override
    public void cleanUp() {
    }
    
    
}
