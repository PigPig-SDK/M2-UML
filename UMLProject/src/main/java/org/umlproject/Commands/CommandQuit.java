package org.umlproject.Commands;

import org.umlproject.TerminalHandler;

public class CommandQuit extends BaseCommand
{
    @Override
    public String actionName() 
    {
        return "quit";
    }
    @Override
    public void act(String[] args)
    {
        System.out.println("Have a nice day!");
        System.exit(0);//Die.
    }
    @Override
    public String description()
    {
        return "Closes the application";
    }
}
