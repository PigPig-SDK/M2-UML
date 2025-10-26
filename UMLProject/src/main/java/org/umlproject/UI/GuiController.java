package org.umlproject.UI;

import java.io.File;
import java.util.Optional;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.umlproject.Main;
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
    private Button addClassButton;
    
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

        //make sure addClassButton is not set to default so that way it doesn't
        //trigger everytime enter is pressed
        addClassButton.setDefaultButton(false);
        menubar.setViewOrder(-100);
        console.setViewOrder(-100);
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
        File outputDirectory = GuiFileBrowser.promptForLoadDiectory();
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
     * UMLDocument instance and calls addClass(with the argument "newClass change name".
     * Not, we don't have a default constructor for UMLClass().
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
