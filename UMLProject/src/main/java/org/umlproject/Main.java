package org.umlproject;

import org.networking.NetworkManager;

public class Main
{
    private static boolean isInTerminalMode = true;
    private static final String terminalLaunchString = "-terminal";
    
    
    public static void main(String[] args) throws InterruptedException 
    {
        //Setup
        NetworkManager.initialize();
        UndoRedoManager.setupListener();
        UMLDocument.resetInstance(true);
        
        
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
                        try {
                            String inputString = AutoComplete.getInstance().lineInConsole();
                            if(inputString == null) continue;

                            //When we get user input, execute the input on main thread...
                            MainThreadDispatcher.dispatcher.dispatch(()->TerminalHandler.runCommand(inputString.toLowerCase()));
                        } 
                        catch (Exception e) {
                            //Hard shutdown causes issues.
                            if(!MainThreadDispatcher.dispatcher.shutDown) e.printStackTrace();
                        }
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
        //Is FX mode, launch FX app.
        App.main(args);
    }
        
    public static void setTerminalMode(boolean isTerminalMode)
    {
        isInTerminalMode = isTerminalMode;
    }
    public static boolean isInTerminalMode()
    {
        return isInTerminalMode;
    }
}