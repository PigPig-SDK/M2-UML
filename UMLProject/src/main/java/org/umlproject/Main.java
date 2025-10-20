package org.umlproject;

import javafx.scene.control.Button;
import java.util.Scanner;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application
{
    private static final String terminalLaunchString = "-terminal";
    public static Stage mainStage;
    
    @FXML
    private Button buttonTerminal;

    @FXML
    private Button buttonGUI;

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
                mainStage.setScene(scene);
                mainStage.setTitle("UML editor");
            }
            catch(Exception e){}
        }
        else if (buttonObject == buttonTerminal) {
            //Open terminal.. I'll let someone else figure that out.
        }
    }
    
    public static void main(String[] args) 
    {
        //If terminal launch option is requested. Override JAVAFX.
        if(args.length == 1 && args[0].equals(terminalLaunchString))
        {
            Scanner scanner = new Scanner(System.in);
            TerminalHandler.runCommand("help");
            TerminalHandler.printLineBreak();
            do
            {
                TerminalHandler.runCommand(scanner.nextLine());
            }while(TerminalHandler.isRunning);
            return;
        }
        
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        //JavaFX setup...
        mainStage = stage;
        //StartupScreen.fxml BINDS TO THIS CLASS!
        FXMLLoader loader = new FXMLLoader(getClass().getResource("StartupScreen.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Setup");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("appicon.png")));
        stage.show();
    }
}