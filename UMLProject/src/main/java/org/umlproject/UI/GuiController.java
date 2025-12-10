package org.umlproject.UI;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.*;

import javafx.scene.control.TextField;

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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.networking.NetworkManager;
import org.networking.NetworkMouseHandler;
import org.umlproject.RelationshipType;
import org.umlproject.TerminalHandler;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;
import org.umlproject.UMLRelationship;
import org.umlproject.DocumentListner;
import org.umlproject.DiagramElementListener;
import org.umlproject.DocumentState;

public class GuiController implements DocumentListner {
    
    private static GuiController singleton;

    /**
     * @return NULL if javaFX didn't setup the singleton
     */
    public static GuiController getInstance()
    {
        //Note. This singleton can be null!
        //In terminal mode, this will 100% be null!
        return singleton;
    }
    
    @FXML
    public Pane world;//'World' is where all UI objects should live.
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
    public VBox rootVBox;
    @FXML
    public TextArea consoleOut;
    @FXML
    public CheckMenuItem viewTerminalMenuItem;
    @FXML
    public MenuItem viewThemeMenuItem;

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
    
    
    @FXML
    public void resetCameraViewMenuAction() {
        GuiCamera.resetCameraLocation();
    }
    
    @FXML
    public void zoomInViewMenuAction() {
        GuiCamera.setZoom(GuiCamera.getCameraZoom()*1.15, GuiCamera.getScreenCenter());
    }
    
    @FXML
    public void zoomOutViewMenuAction() {
        GuiCamera.setZoom(GuiCamera.getCameraZoom()*0.85, GuiCamera.getScreenCenter());
    }
    
    @FXML
    public void themeViewMenuAction() {
        GuiThemes.getInstance().onThemeMenuItemPressed();
    }

    /**
     * This is called after initialize. 
     * This is because some things are not fully initialized during the call of 'initialize'.
     */
    public void lateInitialization() { 
        
        UMLClass.initializationLocation = (UMLClass umlclass) ->
        {
            //If we are not loading
            if((UMLDocument.getDocumentState() == DocumentState.NORMAL))
            {
                return findSafeLocation(GuiCamera.getScreenCenter(), extractGuiClasses());
            }
            else
                return umlclass.getLocation();
        };
        
        GuiResizeManager.bindToSizeUpdates();
        GuiCamera.setupCamera();
        GuiKeyBinds.setupKeyBinds();
        GuiConsole.setupConsole();
        GuiNetwork.initialize();
        NetworkMouseHandler.initialize();
        //Setup button icons.
        FXUtility.getInstance().applyIconsToButtons("Add Class", addClassButton,"/org/umlproject/icons/new_class.png",50,50);
        FXUtility.getInstance().applyIconsToButtons("Add Relationship", addRelationshipButton,"/org/umlproject/icons/new_relationship.png",50,50);
        UMLDocument.getMemento().addListener(GuiSelect.getInstance());
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
     * Handles the "open" menu action.
     */
    @FXML
    public void editUndo()
    {
        UMLDocument.undoMementoState();
    }
    /**
     * Handles the "open" menu action.
     */
    @FXML
    public void editRedo()
    {
        UMLDocument.redoMementoState();
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
    private void copyEditMenuAction() {
        GuiCopyPaste.getInstance().copy();
    }
    
    @FXML 
    private void pasteEditMenuAction() {
        GuiCopyPaste.getInstance().paste();
    }
    
    @FXML 
    private void hostNetworkMenuAction() { 
        GuiNetwork.promptHostScreen();
    }
    
    @FXML 
    private void connectNetworkMenuAction() {
        GuiNetwork.promptConnectScreen();
    }
    
    @FXML 
    private void disconnectNetworkMenuAction() { 
        NetworkManager.shutdown();
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
        GuiHelpWindow.showHelp();
    }
    @FXML
    private void consoleSubmit()
    {
        GuiConsole.terminalOverrideOut(true);
        String cmd = console.getText();
        GuiConsole.addToHistory(cmd);
        List<String> restrictedCommands = GuiConsole.restrictedCommands;
        
        for (String SearchValue : restrictedCommands) {
        if (cmd.contains(SearchValue)) {
            System.out.println("This command is unavailable in GUI mode");
            console.setText("");
            return;
        }
      }
        
    TerminalHandler.runCommand(console.getText());
    console.setText("");
        
    }

    /**
     * Handler for the Export Screenshot action within the Gui's file drop down menu. This method functions
     * as the "client" in the command design pattern. It is responsible for retrieving
     * the necessary information for constructing the concrete ScreenshotCommand object
     * and then passing it to the invoker object that calls execute().
     */
    @FXML
    private void exportScreenshotMenuAction() throws IOException {
        //Generate an alert in case the file path is invalid and the screenshot cannot be saved.
        Alert alert = FXDialogueFactory.
                        createAlertWindow(
                        Alert.AlertType.WARNING, 
                        "Failed To Export Image", 
                        "Screenshot cannot be saved in this location.", 
                        "Ensure a valid file path and then retry exporting.", null);

        //Retrieve the location where the image should be exported.
        File exportLocation = GuiFileBrowser.promptForScreenshotExportDirectory();
        if(exportLocation == null){
            alert.show();
            return;
        }
        //Create a ScreenshotCommand instance to call execute() on.
        ScreenshotCommand newScreenshot = new ScreenshotCommand(exportLocation, world);
        CommandInvoker invoker = new CommandInvoker(newScreenshot);
        try {
            //Export the image.
            invoker.invoke();
        }
        catch(IOException e){
            alert.show();
            return;
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
            FXDialogueFactory.createAlertWindow(Alert.AlertType.ERROR, "Class Creation Error", null, null, null).show();
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
        
        Set<String> classSet = doc.getClassSet().keySet();

        if(classSet.size() < 2)
        {
            FXDialogueFactory.createAlertWindow(Alert.AlertType.ERROR, "Create Relationship", "Your project requires at least 2 classes", null, null).show();
            return;
        }

        //Convert to a sortable type.
        ArrayList<String> classList = new ArrayList(classSet);
        Collections.sort(classList);

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

        //Searches for available classes to autfill relationship source/destination
        String sourceClass = "";
        String destinationClass = "";
        boolean setClasses = true;
        ArrayList<UISelectable> selectedObjects = GuiSelect.getInstance().getSelectedObjects();
        for(UISelectable selectable : selectedObjects){
            if(selectable instanceof GuiClass guiClass){
                if (sourceClass.isEmpty()) {
                    sourceClass = guiClass.getParentClass().getClassName();
                    continue;
                }
                if(destinationClass.isEmpty()) {
                    destinationClass = guiClass.getParentClass().getClassName();
                    continue;
                }
                setClasses = false;
                break;
            }
        }

        //If 2 available classes were found, autofill
        if(!sourceClass.isEmpty() && setClasses){
            startBox.setValue((sourceClass));
            if(!destinationClass.isEmpty()){
                destinationBox.setValue(destinationClass);
            }
        }

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

        Alert alert = FXDialogueFactory.createAlertWindow(Alert.AlertType.INFORMATION, "Create Relationship", "Create a relationship between two classes", null, grid);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) //User Acceptance
        {
            String source = startBox.getValue();
            String destination = destinationBox.getValue();
            String rType = typebox.getValue();

            boolean success = doc.addRelationship(source, destination, rType);
        }
        
    }
    //This method will bind a guiClass listener to the new umlClass
    @Override
    public void onClassAdded(UMLClass umlClass) {
        GuiClass guiClass = new GuiClass(world, umlClass);
        umlClass.setListener(guiClass);
        
        if(UMLDocument.getDocumentState() != DocumentState.FILE_LOADING &&
           UMLDocument.getDocumentState() != DocumentState.MEMENTO_STATE_RESET &&
           UMLDocument.getDocumentState() != DocumentState.NETWORK_OPERATION)//select newly added items on our client.
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
            guiClasses.add((GuiClass)umlClassMap.get(umlClass).getListener());

        }
        return guiClasses;
    }

    /**
     * This method will compare a test Rectangles dimensions and location against that of every
     * Rectangle background in each of the GuiClass objects that already exist. If there is no intersection
     * between the test Rectangle and an existing one, then the location of the test Rectangle will be returned.
     * Utilizes a closed set to store locations that have been previously checked or are about to be checked
     * and utilizes an open set for locations that still need to be checked.
     * @param currentCameraCenter, the current camera center coordinates
     * @param existingClasses, list of existing GuiClasses
     * @return, safe location for a new class box.
     */
    public static Point2D findSafeLocation(Point2D currentCameraCenter, List<GuiClass> existingClasses) {
        //Initial class boxes have the following specifications.
        final double NEW_CLASS_WIDTH = GuiClass.CLASS_WIDTH;
        final double NEW_CLASS_HEIGHT = GuiClass.CLASS_DEFAULT_HEIGHT;
        final double PADDING = 300.0;
        Point2D testLocation = currentCameraCenter;

        //Sstep sizes for calculating the horizontal and vertical neighbor tiles of the current testLocation.
        final double STEPX = (NEW_CLASS_WIDTH/2) + PADDING;
        final double STEPY = (NEW_CLASS_HEIGHT/2) + PADDING;

        //OpenSet for tile locations to check.
        //ClosedSet stores tile locations we have already checked or that are in openSet.
        Queue<Point2D> openSet = new LinkedList<>();
        Set<Point2D> closedSet = new HashSet<>();

        //Initialize both sets with currentCameraLocation
        openSet.offer(currentCameraCenter);
        closedSet.add(currentCameraCenter);

        //Begin search for a safe location. Compare a testRectangle against all rectangle bounds of
        //existing classes.
        while (!openSet.isEmpty()) {
            testLocation = openSet.poll();
            Rectangle2D newRect = new Rectangle2D(testLocation.getX() - (NEW_CLASS_WIDTH/2), testLocation.getY() - (NEW_CLASS_HEIGHT/2), NEW_CLASS_WIDTH, NEW_CLASS_HEIGHT);
            boolean overlaps = false;
            for (GuiClass existingClass : existingClasses) {
                if (existingClass == null) continue;
                
                Rectangle2D existingBounds = existingClass.getRectBounds();
                if (existingBounds != null && newRect.intersects(existingBounds)) {
                    overlaps = true;
                    //testLocation overlapped with existing tile, so create Up, Down, Left, Right neighbors
                    Point2D up = new Point2D(testLocation.getX(), testLocation.getY() - STEPY);
                    Point2D down = new Point2D(testLocation.getX(), testLocation.getY() + STEPY);
                    Point2D left = new Point2D(testLocation.getX() - STEPX, testLocation.getY());
                    Point2D right = new Point2D(testLocation.getX() + STEPX, testLocation.getY());
                    
                    //Compare neighbors with closed set. If not in closed set, add to both open and closed sets.
                    for(Point2D checkLocation : List.of(left, right, up, down))//Order chosen because most aspect ratios are wider than they are tall
                    {
                        if (!closedSet.contains(checkLocation)) {
                            closedSet.add(checkLocation);
                            openSet.offer(checkLocation);
                        }
                    }
                    break;
                }
            }
            //if no overlapping, break from while loop and return testLocation
            if (!overlaps) {
                break;
            }
        }
        return testLocation;
    }

    public boolean isConsoleFocused(){
        return this.console.isFocused();
    }

    @Override
    public void onRelationshipAdded(UMLRelationship umlRelationship) {
        GuiRelationship guiRelationship = new GuiRelationship(world, umlRelationship);
        umlRelationship.setListener(guiRelationship);
    }
    /**
     * Calls to redraw all relationships.
     */
    private void redrawAllRelationships()
    {
        for(DiagramElementListener uIListener : UMLDocument.getInstance().getUIListeners())
        {
            if(uIListener instanceof GuiRelationship rgui)
            {
                UMLRelationship relationship = rgui.getRelationship();
                if(relationship != null) rgui.update(relationship);
            }
        }
    }
    public void redrawAllElements()
    {
        for(DiagramElementListener uIListener : UMLDocument.getInstance().getUIListeners())
        {
            if(uIListener instanceof GuiRelationship rgui)
            {
                UMLRelationship relationship = rgui.getRelationship();
                if(relationship != null) rgui.update(relationship);
            }
            else if(uIListener instanceof GuiClass cgui)
            {
                UMLClass c = cgui.getParentClass();
                if(c != null)
                    cgui.update(c);
            }
        }
    }
    @Override
    public void loadFile(UMLDocument umlDocument) {}
    @Override
    public void onClassRemove(UMLClass umlClass) {
        System.out.println("CLeaned up. " + umlClass.getListener());
        umlClass.disposeOfListener();
    }
    @Override
    public void onRelationshipRemove(UMLRelationship umlRelationship) {
        umlRelationship.disposeOfListener();
    }


}
