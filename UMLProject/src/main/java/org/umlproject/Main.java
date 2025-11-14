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
import org.networking.NetworkManager;
import org.umlproject.UI.GuiController;

public class Main extends Application
{
    private static boolean isInTerminalMode = true;
    private static final String terminalLaunchString = "-terminal";

    public static Stage mainStage;
    public static Scene currentScene;
    
    @FXML
    private Button buttonTerminal;

    @FXML
    private Button buttonGUI;
    
    
    public static void main(String[] args) throws InterruptedException 
    {
        //Setup
        NetworkManager.initialize();
        UndoRedoManager.setupListener();
        
        //If terminal launch option is requested. Override JAVAFX.
        if(args.length == 1 && args[0].equals(terminalLaunchString))
        {
            //Run main terminal stuff...
            TerminalHandler.runCommand("help");
            TerminalHandler.printLineBreak();
            //Create dispatcher for main thread...
            MainThreadDispatcher.dispatcher = new MainThreadDispatcher();
            //Push the user onto their own thread.
            Thread userInputThread = new Thread(()->{
                    while(true)
                    {
                        String inputString = AutoComplete.getInstance().lineInConsole().toLowerCase();
                        //When we get user input, execute the input on main thread...
                        MainThreadDispatcher.dispatcher.dispatch(()->TerminalHandler.runCommand(inputString));
                    }
                });
            userInputThread.setDaemon(true);
            userInputThread.start();//Bombs away!
            
            //Do incoming work forever on main thread
            while(true)
            {
                MainThreadDispatcher.dispatcher.processQueuedActions();
                Thread.sleep(15);//Stop 100% cpu usage when nothing is going on...
            }
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
                isInTerminalMode = false;
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

    @Override
    public void stop(){
        System.out.println("Stopping Application");

    }
    public static boolean isInTerminalMode()
    {
        return isInTerminalMode;
    }
}