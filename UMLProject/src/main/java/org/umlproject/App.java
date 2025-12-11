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

public class App extends Application {
    

    public static Stage mainStage;
    public static Scene currentScene;
    
    @FXML
    private Button buttonTerminal;

    @FXML
    private Button buttonGUI;
    
    public static void main(String[] args){ launch();}
    
    private static boolean mouseInWindow = true;
    
    private static boolean isFocused = true;
    
    /**
     * Important to note: Both buttons have the onButtonPressed event in side StartupScreen.XML
     */
    @FXML
    private void onButtonPressed(javafx.event.ActionEvent event) {
        Object buttonObject = event.getSource();
        if (buttonObject == buttonGUI){
            try {
                Main.setTerminalMode(false);
                //MainScreen.fxml BINDS TO GuiClass!
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
                Scene scene = new Scene(loader.load());
                currentScene = scene;
                mainStage.setScene(scene);
                mainStage.setResizable(true);
                mainStage.setTitle("UML editor");
                GuiController.getInstance().lateInitialization();
                
                //Setup blah blahs.
                mainStage.focusedProperty().addListener((obs, oldV, newV) -> isFocused = newV);
                //...Get mouse inside scene...
                scene.setOnMouseEntered(e -> mouseInWindow = true);
                scene.setOnMouseExited(e -> mouseInWindow = false);
            }
            catch(IOException e){
                System.out.println(e);//Stop crash, print error to user.
            }
        }
        else if (buttonObject == buttonTerminal) {
            //Open terminal.. I'll let someone else figure that out.
            try{
                //Terminal Launch: Assumes that terminalscript.cmd, javafxlibrary, and jar are in the same directory
                String location = Main.class.getProtectionDomain().getCodeSource().
                        getLocation().getPath();
                if(!location.contains("!")){
                    Main.main(new String[]{"-terminal"});
                }
                System.out.println(location);
                location = location.substring(6, location.indexOf("!")) + " -terminal";
                System.out.println(location);
                Runtime.getRuntime().exec("cmd /c start " + location);


                }
            catch (IOException | InterruptedException e){
                System.out.println("CMD Error");
            }
            //main(new String[]{terminalLaunchString});
        }
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        //Setup thread dispatcher for potental network traffic...
        FXDispatcher dispatcher = new FXDispatcher();
        dispatcher.start();
        MainThreadDispatcher.dispatcher = dispatcher;
        
        //JavaFX setup...
        mainStage = stage;
        //StartupScreen.fxml BINDS TO THIS CLASS!
        FXMLLoader loader = new FXMLLoader(getClass().getResource("StartupScreen.fxml"));
        Scene scene = new Scene(loader.load());
        currentScene = scene;
        stage.setScene(scene);
        stage.setTitle("Setup");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("appicon.png")));
        mainStage.setResizable(false);
        //mainStage.initStyle(StageStyle.UNDECORATED);
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
    
    @Override
    public void stop(){
        System.out.println("Stopping Application");
    }
}
