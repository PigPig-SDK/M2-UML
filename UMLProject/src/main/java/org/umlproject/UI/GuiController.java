package org.umlproject.UI;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import org.umlproject.TerminalHandler;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;
import org.umlproject.UMLGuiController;
import org.umlproject.UMLRelationship;

public class GuiController implements UMLGuiController {
    
    @FXML
    private Group world;//'World' is where all UI objects should live.
    @FXML
    private TextField console;
    
    
    @FXML
    private void initialize() {
        //Bind to umldocument
        UMLDocument.guiController = this;
        System.out.println("Setup GUI!");
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
    @FXML
    private void saveFileMenuAction()
    {
        System.out.println("Save file click");
    }
    @FXML
    private void saveAsFileMenuAction()
    {
        System.out.println("Save As file click");
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
    private void selectAllEditMenuAction()
    {
        System.out.println("SelectAll edit click");
    }
    @FXML
    private void unSelectAllEditMenuAction()
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
