package org.umlproject.UI;

import java.io.File;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
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
    @FXML
    private void newFileMenuAction()
    {
        System.out.println("New file click");
    }
    @FXML
    private void openFileMenuAction()
    {
        System.out.println("Open file click");
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
        File outputDirectory = GuiFileBrowser.promptForDiectory();
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
    @Override
    public void addClass(UMLClass umlClass) {
        GuiClass guiClass = new GuiClass(world, umlClass);
        umlClass.setListener(guiClass);
    }

    @Override
    public void addRelationship(UMLRelationship umlRelationship) {
        GuiRelationship guiRelationship = new GuiRelationship(world, umlRelationship);
        umlRelationship.setListener(guiRelationship);
    }

    @Override
    public void redrawScreen(UMLDocument umlDocument) {
        
        
    }
    
}
