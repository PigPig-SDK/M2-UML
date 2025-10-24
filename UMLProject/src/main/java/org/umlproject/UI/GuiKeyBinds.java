package org.umlproject.UI;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.umlproject.Main;

public class GuiKeyBinds {
    public static void setupKeyBinds()
    {
        //Setup CTRL+S for "Save"
        addAccelerator( new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), "Save");
        Main.currentScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.S && event.isControlDown()) {
                if(GuiController.singleton == null)
                    return;
                GuiController.singleton.saveFileMenuAction();
            }
        });
        //Setup CTRL+SHIFT+S for "Save As"
        addAccelerator( new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN), "Save As…");
        Main.currentScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.S && event.isControlDown() && event.isShiftDown()) {
                if(GuiController.singleton == null)
                    return;
                GuiController.singleton.saveAsFileMenuAction();
            }
        });
        //Setup Select All
        addAccelerator( new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN), "Select All");
        Main.currentScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.A && event.isControlDown()) {
                if(GuiController.singleton == null)
                    return;
                GuiController.singleton.selectAllEditMenuAction();
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
