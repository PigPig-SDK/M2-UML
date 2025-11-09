package org.umlproject;

import javafx.scene.control.Button;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.umlproject.UI.GuiController;

public class Main extends Application
{

    private static final String terminalLaunchString = "-terminal";

    public static Stage mainStage;
    public static Scene currentScene;
    
    @FXML
    private Button buttonTerminal;

    @FXML
    private Button buttonGUI;
    
    
    public static void main(String[] args) 
    {
        //Setup
        DocumentMementoListener.setupListener();
        
        //If terminal launch option is requested. Override JAVAFX.
        if(args.length == 1 && args[0].equals(terminalLaunchString))
        {
            //Scanner scanner = new Scanner(System.in);
            TerminalHandler.runCommand("help");
            TerminalHandler.printLineBreak();
            do
            {
                TerminalHandler.runCommand(AutoComplete.getInstance().lineInConsole().toLowerCase());
            }while(TerminalHandler.isRunning);
            return;
        }
        
        launch(args);
    }

    /**
     * Important to note: Both buttons have the onButtonPressed event in side StartupScreen.XML
     */
    @FXML
    private void onButtonPressed(javafx.event.ActionEvent event) {
        Object buttonObject = event.getSource();
        if (buttonObject == buttonGUI){
            try {
                //MainScreen.fxml BINDS TO GuiClass!
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
                Scene scene = new Scene(loader.load());
                currentScene = scene;
                mainStage.setScene(scene);
                mainStage.setResizable(true);
                mainStage.setTitle("UML editor");
                GuiController.getInstance().lateInitialization();
            }
            catch(Exception e){
                System.out.println(e);//Stop crash, print error to user.
            }
        }
        else if (buttonObject == buttonTerminal) {
            //Open terminal.. I'll let someone else figure that out.
            try{

                //Terminal Launch: Assumes that terminalscript.cmd, javafxlibrary, and jar are in the same directory
                String location = Main.class.getProtectionDomain().getCodeSource().
                        getLocation().getPath();
                location = location.substring(6, location.indexOf("!")) + " -terminal";
                System.out.println(location);
                Runtime.getRuntime().exec("cmd /c start " + location);


            }
            catch (Exception e){
                System.out.println("CMD Error");
            }
            //main(new String[]{terminalLaunchString});
        }
    }
    
    @Override
    public void start(Stage stage) throws Exception {
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

    @Override
    public void stop(){
        System.out.println("Stopping Application");

    }
}