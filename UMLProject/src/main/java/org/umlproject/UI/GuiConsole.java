
package org.umlproject.UI;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.umlproject.App;

public class GuiConsole extends OutputStream {
    private static TextArea consoleOutput;
    //First command entered is emptystring. This makes looping around 0 more obvious.
    //Also clears space for user to type if they want to return to a clean slate.
    public static List<String> history = new ArrayList<>(List.of(""));
    private static int historyIndex = 0;
    
    
    
    //Commands that cannot be used while in GUI mode
    public static List<String> restrictedCommands = List.of(
        "remove method",
        "remove param",
        "rename method",
        "rename param"
    );
    
    
    public GuiConsole(TextArea consoleOutput) {
        this.consoleOutput = consoleOutput;
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
        char c = (char) b;
        if (c == '\n') {
            String line = buffer.toString();
            buffer.setLength(0); // clear buffer
            Platform.runLater(() -> consoleOutput.setText(consoleOutput.getText() + "\n" + line));
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
                    
                    
                    transparency -= deltaTime * 0.9;
                    //Message fade timer has ran out. 
                    if(transparency < 0)
                    {
                        buffer.setLength(0);
                        consoleOutput.setText("");
                        transparency = 0;
                    }
                }
            }
        }.start();
    }

    public static void updateSize() {
        TextArea consoleOut = GuiController.getInstance().consoleOut;
        consoleOut.setPrefHeight(App.mainStage.getHeight() - 80);//Allow space for console at bottom...
        consoleOut.setPrefWidth(App.mainStage.getWidth());
    }
    
    public static void addToHistory(String command) {
        history.add(command);
        historyIndex = 0;
    }
    /**
     * Use this to traverse in the GUITerminal history
     * @param direction Which direction you desire stepping in the history. (POSITIVE = BACKWARDS, NEGATIVE = FORWARDS)
     */
    public static void traverseHistory(int direction)
    {
        String historyString = GuiConsole.getCommandFromHistory(direction);
        TextField console = GuiController.getInstance().console;
        console.requestFocus();
        console.setText(historyString);
        console.positionCaret(historyString.length());
    }
    /**
     * Focuses the textarea for console input, puts carrot to end of text.
     */
    public static void smartFocus()
    {
        TextField console = GuiController.getInstance().console;
        console.requestFocus();
        console.positionCaret(console.getText().length()); 
    }
    /**
     * @param direction Which direction you desire stepping in the history. (POSITIVE = BACKWARDS, NEGATIVE = FORWARDS)
     * @return A previous command from the command history
     */
    private static String getCommandFromHistory(int direction) {
        historyIndex += direction;
        //We only get positive remainder
        historyIndex = (historyIndex % history.size() + history.size()) % history.size();
        return history.get(historyIndex);
    }
    
    public static String printList() {
        return String.join("\n", history);
    }
}

