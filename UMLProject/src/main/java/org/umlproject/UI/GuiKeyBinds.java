package org.umlproject.UI;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.N;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.umlproject.Main;

public class GuiKeyBinds {
    /**
     * Call this after the main scene is setup.
     * setupKeyBinds() will create listeners for applications basic keybinds.
     */
    public static void setupKeyBinds()
    {
        //Setup CTRL+S for "Save"
        addAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), "Save");
        addAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN), "Open…");
        addAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN), "Save As…");
        addAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), "New");
        addAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN), "Select All");
        Main.currentScene.setOnKeyPressed(event -> {
            if(GuiController.singleton == null)//Cannot execute quickbind. The menu dosn't exist.
            {
                event.consume();
                return;
            }
            //All commands must have CTRL held down!
            if (!event.isControlDown())
                return;
            
            switch(event.getCode())
            {
                case S -> {
                    if(event.isShiftDown())//CTRL+SHIFT+S for "Save As"
                        GuiController.singleton.saveAsFileMenuAction();
                    else//CTRL+S for Save
                        GuiController.singleton.saveFileMenuAction();
                }
                //Select all (CTRL+A)
                case A -> GuiController.singleton.selectAllEditMenuAction();
                //New file (CTRL+N)
                case N -> GuiController.singleton.newFileMenuAction();
                //Open file (CTRL+O)
                case O -> GuiController.singleton.openFileMenuAction();
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
        if(GuiController.singleton == null)//Ensure not some odd case.
            return;
        //Search for menuitem in our menu bar.
        MenuItem saveMenuItem =  findMenuItemFromMenuBar(GuiController.singleton.getMenuBar(),menuItemName);
        if(saveMenuItem == null)
        {
            System.out.println(String.format("GuiKeyCodes::setupKeyCodes() : '%s' SUBMENU NOT FOUND!",menuItemName));
            return;
        }
        KeyCombination saveKeybind = keyAccelerator;
        saveMenuItem.setAccelerator(saveKeybind);
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
