package org.umlproject.UI;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.umlproject.RelationshipType;
import org.umlproject.TerminalHandler;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;
import org.umlproject.UMLRelationship;
import org.umlproject.DocumentListner;
import org.umlproject.UIListener;

public class GuiController implements DocumentListner {
    
    public static GuiController singleton;
    
    @FXML
    private Pane world;//'World' is where all UI objects should live.
    @FXML
    public TextField console;
    @FXML
    private MenuBar menubar;
    @FXML
    private Pane viewpane;
    @FXML 
    private Text workspaceText;
    @FXML
    public AnchorPane consoleAnchorPane;
    @FXML
    private VBox rootVBox;
    @FXML
    public TextArea consoleOut;
    @FXML
    public CheckMenuItem viewTerminalMenuItem;

    final double initialClassBoxWidthOffset = 100;
    final double initialClassBoxHeightOffset = 100;
    /**  This datafield is an internal program reference to the +C clickable button in
     * the FXML document.
     */
    @FXML
    private Button addClassButton;
    @FXML
    private Button addRelationshipButton;
    
    public Pane getWorld(){return this.world;}
    public TextField getTerminal(){return this.console;}
    public MenuBar getMenuBar(){return this.menubar;}
    public Pane getViewPane(){return this.viewpane;}
    private boolean saveLocationSet = false;
    
    @FXML
    private void initialize() {
        //Bind to umldocument
        UMLDocument.documentListners.add(this);

        singleton = this;

        this.world.setLayoutX(0.0);
        this.world.setLayoutY(0.0);
        this.world.setPickOnBounds(false);
        
        VBox.setVgrow(viewpane, Priority.ALWAYS);
        if(rootVBox != null){
            this.rootVBox.setAlignment(Pos.TOP_LEFT);
        }


        //Make sure addClassButton is not set to default so that way it doesn't
        //trigger everytime enter is pressed.
        this.addClassButton.setDefaultButton(false);
        this.menubar.setViewOrder(-100);
        this.console.setViewOrder(-100);
        this.consoleOut.setViewOrder(-100);
        this.workspaceText.setViewOrder(1000000);//To the back of the universe
    }

    @FXML
    public void aboutHelpMenuAction() {
        GuiAboutWindow.showAbout();
    }

    /**
     * This is called after initialize. 
     * This is because some things are not fully initialized during the call of 'initialize'.
     */
    public void lateInitialization() {
        GuiResizeManager.bindToSizeUpdates();
        GuiCamera.setupCamera();
        GuiKeyBinds.setupKeyBinds();
        GuiConsole.setupConsole();
        //Setup button icons.
        applyIconsToButtons(addClassButton,"/org/umlproject/icons/new_class.png");
        applyIconsToButtons(addRelationshipButton,"/org/umlproject/icons/new_relationship.png");
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
            saveLocationSet = false;
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
    private void deleteEditMenuAction()
    {
        GuiSelect.getInstance().deleteAllSelected();
    }
    @FXML
    public void selectAllEditMenuAction()
    {
        GuiSelect.getInstance().selectAll();
    }
    @FXML
    public void unSelectAllEditMenuAction()
    {
        GuiSelect.getInstance().resetSelect();
    }
    @FXML
    public void infoHelpMenuAction()
    {
        GuiHelp.showHelp();
    }
    @FXML
    private void consoleSubmit()
    {
        TerminalHandler.runCommand(console.getText());
        console.setText("");
    }

    /**
     * Handler for the Export Screenshot action within the File drop down menu.
     */
    @FXML
    private void exportScreenshotMenuAction() throws IOException {
        //Generate an alert in case the file path is invalid and the screenshot cannot be saved.
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Failed To Export Image");
        alert.setHeaderText("Screenshot cannot be saved in this location.");
        alert.setContentText("Ensure a valid file path and then retry exporting.");

        //Retrieve the location where the image should be exported.
        File exportLocation = GuiFileBrowser.promptForScreenshotExportDirectory();
        if(exportLocation == null){
            alert.showAndWait();
            return;
        }
        //Create a ScreenshotCommand instance to call execute() on.
        ScreenshotCommand newScreenshot = new ScreenshotCommand();
        try {
            //Export the image.
            newScreenshot.execute(exportLocation);
        }
        catch(IOException e){
            alert.showAndWait();
        }
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
            alert.setTitle("Class Creation Error");
            alert.showAndWait();
        }
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
            //Populate combo box with types.
            for(RelationshipType rType : RelationshipType.values())
            {
                if(rType != RelationshipType.OTHER)
                    typebox.getItems().add(rType.name());
            }
            typebox.setValue(RelationshipType.AGGREGATION.name());
            
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
    public void onClassAdded(UMLClass umlClass, boolean isLoading) {
        if(!isLoading)//The class addition is from 'newclass button'
        {
            Point2D safeLocation = findSafeLocation(GuiCamera.getScreenCenter(), extractGuiClasses());
            umlClass.setLocation(safeLocation);
        }
        GuiClass guiClass = new GuiClass(world, umlClass);
        umlClass.setListener(guiClass);
        if(!isLoading)//Calls this late so items are setup...
        {
            GuiSelect.getInstance().resetSelect();//Clear our selection...
            GuiSelect.getInstance().selectUiElement(guiClass);
        }
    }
    /**
     * Helper method for determining an acceptable location for a newly added class. It extracts
     * a list of GuiClass listeners from the Map of UMLClasses in the UMLDocument singleton.
     * @return, list of GuiClasses.
     */
    public static List<GuiClass> extractGuiClasses(){
        Map<String, UMLClass> umlClassMap = UMLDocument.getInstance().getClassSet();
        ArrayList<String> umlClassKeys = new ArrayList<>(umlClassMap.keySet());
        List<GuiClass> guiClasses = new ArrayList<GuiClass>();
        for(String umlClass : umlClassKeys){
            guiClasses.add((GuiClass)umlClassMap.get(umlClass).getUIListener());

        }
        return guiClasses;
    }

    /**
     * This method will compare a test Rectangles dimensions and location against that of every
     * Rectangle background in each of the GuiClass objects that already exist. If there is no intersection
     * between the test Rectangle and an existion one, then the location of the test Rectangle will be returned.
     * @param existingClasses, list of existing GuiClasses
     * @return, safe location for a new class box
     */
    public static Point2D findSafeLocation(Point2D location, List<GuiClass> existingClasses){
        final double NEW_CLASS_WIDTH = 250.0;
        final double NEW_CLASS_HEIGHT = 150.0;
        final double PADDING = 20.0;

        double currentX = location.getX();
        double currentY = location.getY();
        final double STEP = NEW_CLASS_WIDTH + PADDING;
        final int MAX_COLUMNS = 5;
        int currentColumn = 0;

        while(true){
            Rectangle2D newRect = new Rectangle2D(currentX, currentY, NEW_CLASS_WIDTH, NEW_CLASS_HEIGHT);
            boolean overlaps = false;
            for(GuiClass existingClass : existingClasses){
                if(existingClass == null)
                    continue;
                Rectangle2D existingBounds = existingClass.getRectBounds();
                if(existingBounds != null && newRect.intersects(existingBounds)){
                    overlaps = true;
                    break;
                }
            }
            if(!overlaps){
                return new Point2D(currentX, currentY);
            }
            currentX += STEP;
            currentColumn++;
            if(currentColumn >= MAX_COLUMNS){
                currentX = PADDING;
                currentY += STEP;
                currentColumn = 0;
            }
        }
    }

    @Override
    public void onRelationshipAdded(UMLRelationship umlRelationship, boolean isLoading) {
        GuiRelationship guiRelationship = new GuiRelationship(world, umlRelationship);
        umlRelationship.setListener(guiRelationship);
    }
    /**
     * Calls to redraw all relationships.
     */
    private void redrawAllRelationships()
    {
        for(UIListener uIListener : UMLDocument.getInstance().getUIListeners())
        {
            if(uIListener instanceof GuiRelationship rgui)
            {
                UMLRelationship relationship = rgui.getRelationship();
                if(relationship != null) rgui.update(relationship);
            }
        }
    }
    @Override
    public void loadFile(UMLDocument umlDocument) {
        
        //This is called 0.1 seconds later due to a GUI race condition. Really lame.
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(0.1), e -> {
                redrawAllRelationships();
            })
        );
        timeline.setCycleCount(1);
        timeline.play();

    }
    @Override
    public void onClassRemove(UMLClass umlClass) {
        umlClass.disposeOfGuiListener();
    }
    @Override
    public void onRelationshipRemove(UMLRelationship umlRelationship) {
        umlRelationship.disposeOfGuiListener();
    }

    private void applyIconsToButtons(Button button, String iconDirectory)
    {
        Image icon = new Image(getClass().getResource(iconDirectory).toExternalForm());
        button.setText("");//Clear text...
        ImageView iconView = new ImageView(icon);
        iconView.setFitWidth(50);
        iconView.setFitHeight(50);
        iconView.setPreserveRatio(true);
        //Remove background...
        button.setStyle(
            "-fx-background-color: transparent;" + "-fx-border-color: transparent;"
        );
        //Make the icon dim when mousing over.
        iconView.setOpacity(0.7);
        button.setOnMouseEntered(e -> iconView.setOpacity(1.0));
        button.setOnMouseExited(e -> iconView.setOpacity(0.7));
        //Set graphic
        button.setGraphic(iconView);
    }
}
