package org.umlproject.UI;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.umlproject.TerminalHandler;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;
import org.umlproject.UMLGuiController;
import org.umlproject.UMLRelationship;

public class GuiController implements UMLGuiController {
    
    public static GuiController singleton;
    
    @FXML
    private Group world;//'World' is where all UI objects should live.
    @FXML
    private TextField console;
    @FXML
    private MenuBar menubar;
    @FXML
    private Pane viewpane;
    @FXML 
    private Text workspaceText;

    /**  This datafield is an internal program reference to the +C clickable button in
     * the FXML document.
     */
    @FXML
    private Button addClassButton;
    @FXML
    private Button addRelationshipButton;
    
    public Group getWorld(){return this.world;}
    public TextField getTerminal(){return this.console;}
    public MenuBar getMenuBar(){return this.menubar;}
    public Pane getViewPane(){return this.viewpane;}
    private boolean saveLocationSet = false;
    
    @FXML
    private void initialize() {
        //Bind to umldocument
        UMLDocument.guiController = this;
        System.out.println("Setup GUI!");
        singleton = this;

        //Make sure addClassButton is not set to default so that way it doesn't
        //trigger everytime enter is pressed.
        addClassButton.setDefaultButton(false);
        menubar.setViewOrder(-100);
        console.setViewOrder(-100);
        workspaceText.setViewOrder(1000000);//To the back of the universe
    }
    /**
     * This is called after initialize. 
     * This is because some things are not fully initialized during the call of 'initialize'.
     */
    public void lateInitialization()
    {
        GuiResizeManager.bindToSizeUpdates();
        GuiCamera.setupCamera();
        GuiKeyBinds.setupKeyBinds();
    }
    //----------------- Menu bar callbacks -----------------
    /**
     * Handles the "new file" menu action.
     */
    @FXML
    public void newFileMenuAction()
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("New File");
        alert.setHeaderText("Any unsaved progress will be lost!");
        alert.setContentText("Are you sure you want to create a new file?");
        ButtonType yesButton = new ButtonType("New File", ButtonBar.ButtonData.YES);
        ButtonType noButton  = new ButtonType("Close", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == yesButton) {
            GuiCamera.setCameraLocation(Point2D.ZERO);//Reset camera...
            UMLDocument.getInstance().clearFile();
        }
    }
    /**
     * Handles the "open" menu action.
     */
    @FXML
    public void openFileMenuAction()
    {
        File outputDirectory = GuiFileBrowser.promptForLoadDirectory();
        if(outputDirectory == null || outputDirectory.getAbsoluteFile() == null)
            return;
        String pathString = GuiFileBrowser.removeFileExtension(outputDirectory.getAbsolutePath());
        UMLDocument.getInstance().load(pathString);
    }
    /**
     * Handles the "Save" menu action.
     * 
     * If no save location is set, SaveAs will be executed
     */
    @FXML
    public void saveFileMenuAction()
    {
        if(saveLocationSet)
            UMLDocument.getInstance().save();
        else
            saveAsFileMenuAction();
    }
    /**
     * Handles the "SaveAs" menu action.
     */
    @FXML
    public void saveAsFileMenuAction()
    {
        File outputDirectory = GuiFileBrowser.promptForSaveDiectory();
        if(outputDirectory == null || outputDirectory.getAbsoluteFile() == null)
            return;
        UMLDocument.getInstance().setFileLocation(GuiFileBrowser.removeFileExtension(outputDirectory.getAbsolutePath()));
        
        if(UMLDocument.getInstance().save())
            saveLocationSet = true;
    }
    @FXML
    private void quitFileMenuAction()
    {
        //TODO: double check saving
        Platform.exit();
    }
    @FXML
    private void copyEditMenuAction()
    {
        System.out.println("Copy edit click");
    }
    @FXML
    private void pasteEditMenuAction()
    {
        System.out.println("Paste edit click");
    }
    @FXML
    private void deleteEditMenuAction()
    {
        System.out.println("Delete edit click");
    }
    @FXML
    public void selectAllEditMenuAction()
    {
        System.out.println("SelectAll edit click");
    }
    @FXML
    public void unSelectAllEditMenuAction()
    {
        System.out.println("SelectAll but like backwards edit click");
    }
    @FXML
    private void aboutHelpMenuAction()
    {
        System.out.println("about");
    }
    @FXML
    private void consoleSubmit()
    {
        TerminalHandler.runCommand(console.getText());
        console.setText("");
    }
    //----------------- UMLGuiController Interface -----------------

    /** This method will listen for when +C is pushed inside gui. It then retrieves the
     * UMLDocument instance and calls addClass() with findValidDummyName() as the argument. This argument
     * function will generate a new unique name for the new class that isn't one already stored in UMLDocument.".
     *
      */
    @FXML
    public void addClassButtonPushed(){
        UMLDocument doc = UMLDocument.getInstance();
        UMLClass checkClass = doc.addClass(doc.findValidDummyName());
        if(checkClass == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Dummy Creation Error");
            alert.showAndWait();
            System.out.println("Class add failed");
        }
        else{
            System.out.println("Class was added");
        }
        System.out.println("+C was called");

    }
    /**
     * This method listens for the +R button inside the main GUI.
     */
    @FXML
    public void addRelationshipButtonPushed(){
        UMLDocument doc = UMLDocument.getInstance();
        //This scope contains the 'window creation' for uml relationships..
        //The scope exists for additional functionality.
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Create Relationship");
            alert.setContentText("");
            
            Set<String> classSet = doc.getClassSet().keySet();
            
            if(classSet.size() < 2)
            {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setHeaderText("Your project requires at least 2 classes");
                alert.showAndWait();
                return;
            }
            
            //Convert to a sortable type.
            ArrayList<String> classList = new ArrayList(classSet);
            Collections.sort(classList);
            
            alert.setHeaderText("Create a relationship between two classes");
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            ComboBox<String> startBox = new ComboBox<>();
            startBox.getItems().addAll(classList);
            startBox.setValue("...");
            
            ComboBox<String> destinationBox = new ComboBox<>();
            destinationBox.getItems().addAll(classList);
            destinationBox.setValue("...");
            
            ComboBox<String> typebox = new ComboBox<>();
            typebox.getItems().addAll("Aggregation", "Composition", "Generalization", "Realization");
            typebox.setValue("Composition");
            
            grid.add(new Label("Source"), 0, 0);
            grid.add(startBox, 1, 0);
            grid.add(new Label("Destination"), 0, 1);
            grid.add(destinationBox, 1, 1);
            grid.add(new Label("Type"), 0, 2);
            grid.add(typebox, 1, 2);
            
            alert.getDialogPane().setContent(grid);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) //User Acceptance
            {
                String source = startBox.getValue();
                String destination = destinationBox.getValue();
                String rType = typebox.getValue();

                boolean success = doc.addRelationship(source, destination, rType);
            }
        }
    }
    //This method will bind a guiClass listener to the new umlClass
    @Override
    public void onClassAdded(UMLClass umlClass) {
        GuiClass guiClass = new GuiClass(world, umlClass);
        umlClass.setListener(guiClass);
    }

    @Override
    public void onRelationshipAdded(UMLRelationship umlRelationship) {
        GuiRelationship guiRelationship = new GuiRelationship(world, umlRelationship);
        umlRelationship.setListener(guiRelationship);
    }

    @Override
    public void redrawScreen(UMLDocument umlDocument) {
        
        
    }
    
}
