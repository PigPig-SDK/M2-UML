package org.umlproject.UI;

import java.io.IOException;

/**
 * Serves as the Invoker in the Command design pattern for the Export Screenshot menu option in the GUI
 * File drop down menu. The Invoker gets initialized with a concrete export command. Note that an invoker
 * calls invoke() without any knowledge about the type of export command passed to it.
 */
public class CommandInvoker{
    private ExportCommand command;

    public CommandInvoker(ExportCommand nextCommand){
        if(nextCommand == null){
            throw new IllegalArgumentException("nextCommand is null.");
        }
        this.command = nextCommand;
    }

    /**
     * Calls the execute() method of the passed in command.
     * @throws IOException
     */
    public void invoke() throws IOException {
        command.execute();
    }
}
