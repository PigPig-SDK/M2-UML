/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.umlproject.UI;

import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;


public class GuiThemes {
    
    static String curTheme = "Dark Mode";
    
    private static GuiThemes instance = new GuiThemes();
    
        public static synchronized GuiThemes getInstance()
    {
        if(instance == null)
        {
            instance = new GuiThemes();
        }
        return instance;
    }

    
    public void onThemeMenuItemPressed() {

    }
}
