package org.umlproject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.umlproject.UI.GuiController;

import java.io.BufferedInputStream;
import java.io.IOException;
import org.networking.NetworkManager;

public class App extends Application {
    

    public static Stage mainStage;
    public static Scene currentScene;

    public static void main(String[] args){ launch();}
    
    private static boolean mouseInWindow = true;
    
    private static boolean isFocused = true;
    
    @Override
    public void start(Stage stage) throws Exception {
        //Setup thread dispatcher for potental network traffic...
        FXDispatcher dispatcher = new FXDispatcher();
        dispatcher.start();
        MainThreadDispatcher.dispatcher = dispatcher;
        
        //JavaFX setup...
        mainStage = stage;
        //StartupScreen.fxml BINDS TO THIS CLASS!
        Main.setTerminalMode(false);
        //MainScreen.fxml BINDS TO GuiClass!
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
        Scene scene = new Scene(loader.load());
        currentScene = scene;
        mainStage.setScene(scene);
        mainStage.setResizable(true);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("appicon.png")));
        setWindowContext(null);
        GuiController.getInstance().lateInitialization();

        //Setup event listeners.
        mainStage.focusedProperty().addListener((obs, oldV, newV) -> isFocused = newV);
        scene.setOnMouseEntered(e -> mouseInWindow = true);
        scene.setOnMouseExited(e -> mouseInWindow = false);
        stage.show();
    }

    public static boolean isMouseInsideWindow()
    {
        return mouseInWindow && isWindowFocused();
    }
    
    public static boolean isWindowFocused()
    {
        return isFocused;
    }
    public static void updateWindowContext()
    {
        String context = null;
        
        if(GuiController.getInstance().saveLocationSet)
            context = "["+UMLDocument.getInstance().getFileLocation()+"]";
        
        if(NetworkManager.isConnected())
        {
            if(context == null)
                context = "[Network session]";
            else
                context += " [Network session]";
        }
        
        setWindowContext(context);
    }
    private static void setWindowContext(String context)
    {
        if(mainStage == null) return;
        
        if(context == null)
        {
            mainStage.setTitle("UML editor");
        }
        else
        {
            mainStage.setTitle("UML editor - " + context);
        }
        
    }
    @Override
    public void stop(){
        System.out.println("Stopping Application");
    }
}
