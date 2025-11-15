package org.umlproject.UI;

import javafx.scene.layout.Pane;
import org.umlproject.App;
import org.umlproject.Main;
/**
 * This class is used only for 'managing when the screen is resized'.
 * It will resize the 'world'. This is done because javafx jank.
 */
public class GuiResizeManager 
{
    public static void bindToSizeUpdates()
    {
        App.mainStage.widthProperty().addListener((obs, oldVal, newVal) -> {GuiResizeManager.sizeUpdate(); });
        App.mainStage.heightProperty().addListener((obs, oldVal, newVal) -> {GuiResizeManager.sizeUpdate(); });
        sizeUpdate();//Call once to update locations of stuff.
    }
    public static void sizeUpdate()
    {
        Pane p = GuiController.getInstance().getViewPane();
        p.setPrefHeight(App.mainStage.getHeight());
        p.setPrefWidth(App.mainStage.getWidth());
        GuiConsole.updateSize();
    }
}
