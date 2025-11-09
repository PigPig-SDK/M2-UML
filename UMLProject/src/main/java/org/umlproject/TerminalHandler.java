package org.umlproject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.umlproject.Commands.*;

public class TerminalHandler
{
    //This is here because the register cannot occur unless the hashmap is in the base object.
    public static Map<String, BaseCommand> commandList = new HashMap<>();
    //The binding for commands
    //Called on JVM startup...
    static
    {
        TerminalHandler.registerCommand(new CommandHelp());
        TerminalHandler.registerCommand(new CommandAdd());
        TerminalHandler.registerCommand(new CommandQuit());
        TerminalHandler.registerCommand(new CommandRemove());
        TerminalHandler.registerCommand(new CommandRename());
        TerminalHandler.registerCommand(new CommandLoad());
        TerminalHandler.registerCommand(new CommandList());
        TerminalHandler.registerCommand(new CommandSave());
        TerminalHandler.registerCommand(new CommandUndo());
        TerminalHandler.registerCommand(new CommandRedo());
    }
    
    private static final String delimiter = " ";
    public static boolean isRunning = true;
    

    /** Registers the command inside the commandList map
     * Can only be called once!
     * @param command The command subclass which is being registered.
     */
    public static void registerCommand(BaseCommand command)
    {
        commandList.put(command.actionName(),command);
    }
    /**
     * @param command What command to query
     * @return The description of the given command, if it not existing than null is returned (english is hard)
     */
    public static String getCommandDescription(String command)
    {
        if(commandList.containsKey(command))
            return commandList.get(command).description();
        else
            return null;
    }
    /**
     * @param command The attempted CMD
     * @param args Argument array
     * @return 
     */
    public static boolean executeCommand(String command, String[] args)
    {
        commandList.get(command).act(args);
        return true;
    }
    /** Splits and runs the given user input
     * @param input The users input
     */
    public static void runCommand(String input)
    {
        String[] tempArgs = input.split(delimiter);
        if(tempArgs.length == 0)
            return;
        List<String> args = new ArrayList<>(Arrays.asList(tempArgs));
        String command = args.get(0);
        args.remove(0);//Remove from args.
        //Command execution
        if(commandList.containsKey(command))
        {
            commandList.get(command).act(args.toArray(String[]::new));
        }
        else
        {
            System.out.println(String.format("'%s' is not a command", command));
        }
    }
    /**
     * This is here for continuity between console entries.
     */
    public static void printLineBreak()
    {
        System.out.println("---------------------");
    }
}
