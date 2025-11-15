package org.umlproject.UI;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.N;
import static javafx.scene.input.KeyCode.Z;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.umlproject.App;
import org.umlproject.Main;
import org.umlproject.UMLDocument;

public class GuiKeyBinds {
    /**
     * Call this after the main scene is setup.
     * setupKeyBinds() will create listeners for applications basic keybinds.
     */
    public static void setupKeyBinds()
    {
        //Setup CTRL+S for "Save"
        addAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), "Save");
        addAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN), "Undo");
        addAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN), "Redo");
        addAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN), "Open…");
        addAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN), "Save As…");
        addAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), "New");
        addAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN), "Select All");
        addAccelerator(new KeyCodeCombination(KeyCode.DELETE, KeyCombination.CONTROL_DOWN), "Delete");
        addAccelerator(new KeyCodeCombination(KeyCode.F1), "Help");
        addAccelerator(new KeyCodeCombination(KeyCode.F2), "About UML Editor");
        addAccelerator(new KeyCodeCombination(KeyCode.F12), GuiController.getInstance().viewTerminalMenuItem);
        
        
        App.currentScene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(GuiController.getInstance() == null)//Cannot execute quickbind. The menu dosn't exist.
                return;
          
            switch(event.getCode())
            {
                case F2->
                {
                    GuiController.getInstance().aboutHelpMenuAction();
                }
                case F1->
                {
                    GuiController.getInstance().infoHelpMenuAction();
                }
                case F12->
                {
                    CheckMenuItem terminalButton = GuiController.getInstance().viewTerminalMenuItem;
                    GuiConsole.terminalOverrideOut(!terminalButton.isSelected());//Override output, this is required due to a javafx bug...
                    terminalButton.setSelected(!terminalButton.isSelected());
                }
            }
            
            if (!event.isControlDown())
                return;
            
            switch(event.getCode())
            {
                case S -> {
                    if(event.isShiftDown())//CTRL+SHIFT+S for "Save As"
                        GuiController.getInstance().saveAsFileMenuAction();
                    else//CTRL+S for Save
                        GuiController.getInstance().saveFileMenuAction();
                    event.consume();
                }
                //Select all (CTRL+A)
                case A -> {
                    GuiController.getInstance().selectAllEditMenuAction();
                    event.consume();
                }
                //New file (CTRL+N)
                case N -> {
                    GuiController.getInstance().newFileMenuAction();
                    event.consume();
                }
                //Open file (CTRL+O)
                case O -> {
                    GuiController.getInstance().openFileMenuAction();
                    event.consume();
                }
                //Delete all (CTRL+DELETE)
                case DELETE -> {
                    GuiSelect.getInstance().deleteAllSelected();
                    event.consume();
                }
                //UNDO
                case Z ->{
                    GuiController.getInstance().editUndo();
                    event.consume();
                }
                //Redo
                case Y ->{
                    GuiController.getInstance().editRedo();
                    event.consume();
                }
            }
        });
        
    }
    /**
     * PURELY DECORATION! No function comes from this!
     * This will inform the user of specific keybinds in the main menu. (CTRL+S, ect...)
     * @param keyAccelerator The key accelerator to add to the specified menu
     * @param menuItemName The menu item name, be very specific when calling this. Look this up with SceneBuilder inside the .FXML
     */
    public static void addAccelerator(KeyCodeCombination keyAccelerator, String menuItemName)
    {
        if(GuiController.getInstance() == null)//Ensure not some odd case.
            return;
        //Search for menuitem in our menu bar.
        MenuItem saveMenuItem =  findMenuItemFromMenuBar(GuiController.getInstance().getMenuBar(),menuItemName);
        if(saveMenuItem == null)
        {
            System.out.println(String.format("GuiKeyCodes::setupKeyCodes() : '%s' SUBMENU NOT FOUND!",menuItemName));
            return;
        }
        addAccelerator(keyAccelerator, saveMenuItem);
    }
    /**
     * PURELY DECORATION! No function comes from this!
     * This will inform the user of specific keybinds in the main menu. (CTRL+S, ect...)
     * @param keyAccelerator The key accelerator to add to the specified menu
     * @param menuItem A MenuItem which the accelerator be applied.
     */
    public static void addAccelerator(KeyCodeCombination keyAccelerator, MenuItem menuItem)
    {
        KeyCombination saveKeybind = keyAccelerator;
        menuItem.setAccelerator(saveKeybind);
    }
    
    /**
     * Realistically, there should be no need to search for these menus.
     * However, to limit people poking in the .FXML file, this has been created.
     * 
     * Searches for menus in a menu bar to get a specific item
     */
    private static MenuItem findMenuItemFromMenuBar(MenuBar menuBar, String objectName)
    {
        for(Menu menu : menuBar.getMenus())
        {
            MenuItem search = findMenuItem(menu, objectName);//Sub search
            if(search != null)
                return search;
        }
        return null;
    }
    /**
     * Searches for a specific objectName in a Menu tree.
     */
    private static MenuItem findMenuItem(Menu menu, String objectName)
    {
        for(MenuItem item : menu.getItems())
        {
            if(objectName.equals(item.getText()))
                return item;
            if (item instanceof Menu submenu) {
                MenuItem search = findMenuItem(submenu, objectName);
                if (search != null)
                    return search;
            }
        }
        return null;
    }
}
