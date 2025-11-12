
package org.umlproject.UI;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import org.umlproject.AutoComplete;
import org.umlproject.Main;

public class GuiConsole extends OutputStream {
    private static TextArea console;
    
    //Commands that cannot be used while in GUI mode
    public static List<String> restrictedCommands = List.of(
        "remove method",
        "remove param",
        "rename method",
        "rename param"
    );
    
    
    public GuiConsole(TextArea console) {
        this.console = console;
    }

    private static StringBuilder buffer = new StringBuilder();
    
    private static double messageFuel = 0;
    
    private static double transparency = 1.0;

    private static final double MESSAGE_APPEAR_TIME = 7;//In seconds
    
    private static PrintStream printStream;
    
    private static PrintStream initialOutputStream; 

    @Override
    public void write(int b) {
        messageFuel = MESSAGE_APPEAR_TIME;
        transparency = 1.0;
        updateTransparency();
        char c = (char) b;
        if (c == '\n') {
            String line = buffer.toString();
            buffer.setLength(0); // clear buffer
            Platform.runLater(() -> console.setText(console.getText() + "\n" + line));
        } else {
            buffer.append(c);
        }
       
    }   
    
    public static void setupConsole() {
        AnchorPane consoleAnchorPane = GuiController.getInstance().consoleAnchorPane;
        CheckMenuItem viewTerminalMenuItem = GuiController.getInstance().viewTerminalMenuItem;
        TextArea consoleOut = GuiController.getInstance().consoleOut;
        
        //Store OG output stream
        initialOutputStream = System.out;
        
        printStream = new PrintStream(new GuiConsole(consoleOut));
        
        consoleOut.setDisable(true);
        consoleOut.setMouseTransparent(true);
        
        //Hide terminal initially.
        consoleOut.setVisible(false);
        consoleOut.setManaged(false);
        
        //Bind with toggle button.
        consoleOut.visibleProperty().bind(viewTerminalMenuItem.selectedProperty());
        consoleOut.managedProperty().bind(consoleOut.visibleProperty());
        consoleAnchorPane.visibleProperty().bind(viewTerminalMenuItem.selectedProperty());
        consoleAnchorPane.managedProperty().bind(consoleAnchorPane.visibleProperty());
        
        viewTerminalMenuItem.setOnAction(event -> {
            terminalOverrideOut(viewTerminalMenuItem.isSelected());
        });
        setupAnimationTimer();
    }
    
    public static void terminalOverrideOut(boolean isOverriding)
    {
        if(isOverriding)//Show in our override console
            setSystemPrintLocation(printStream);
        else
            setSystemPrintLocation(initialOutputStream);
    }
    
    private static void setSystemPrintLocation(PrintStream ps)
    {
        System.setOut(ps);
        //System.setErr(ps);//Impossibly hard to debug with err pushed into the tiny box.
    }
    
    private static void setupAnimationTimer()
    {
        //Fade timer
        new AnimationTimer() {
            long lastTime = 0;
            @Override
            public void handle(long now) {
                //Handle delta
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                double deltaTime = (now - lastTime) / 1000000000.0;//seconds
                lastTime = now;
                //Decrement message timer
                messageFuel -= deltaTime;
                //message time expired
                if(messageFuel <= 0 && transparency > 0)
                {
                    updateTransparency();
                    
                    transparency -= deltaTime * 0.9;
                    //Message fade timer has ran out. 
                    if(transparency < 0)
                    {
                        buffer.setLength(0);
                        console.setText("");
                        transparency = 0;
                    }
                }
            }
        }.start();
    }
    private static void updateTransparency()
    {
        GuiController.getInstance().consoleOut.setStyle("-fx-text-fill: rgba(255, 255, 255," +  
                Math.max(transparency, 0.005)//Failsafe, as transparency approaches zero, a crash will occur. Javafx error!
                +");");
    }
    public static void updateSize() {
        TextArea consoleOut = GuiController.getInstance().consoleOut;
        consoleOut.setPrefHeight(Main.mainStage.getHeight() - 80);//Allow space for console at bottom...
        consoleOut.setPrefWidth(Main.mainStage.getWidth());
    }

}

