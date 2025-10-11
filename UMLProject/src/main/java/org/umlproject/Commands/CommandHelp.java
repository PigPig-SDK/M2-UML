package org.umlproject.Commands;

import org.umlproject.TerminalHandler;

public class CommandHelp extends BaseCommand
{
    @Override
    public String actionName() 
    {
        return "help";
    }
    @Override
    public void act(String[] args)
    {
        TerminalHandler.printLineBreak();
        System.out.println("List of possible commands:");
        for(String command : TerminalHandler.commandList.keySet())
        {
            System.out.println(String.format("[ %s ] | %s", command, TerminalHandler.getCommandDescription(command)));
        }
    }
    @Override
    public String description()
    {
        return "Prints information about this applications commands.";
    }
}
