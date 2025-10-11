package org.umlproject.Commands;

import java.io.InputStream;
import java.util.Scanner;


public abstract class BaseCommand 
{
    private static final int INVALID_ENTRY = -1;
    private static final int EXIT = -2;

    
    /** Each command MUST have a unique identifier!
     * The 'actionName' defines the 'hook' of the command
     * @return the heading command string ex: 'add'
     */
    public abstract String actionName();
    /** This function is called when the console registers a command.
     * @param args the arguments given by the user
     */
    public abstract void act(String[] args);
    /**
     * When the "Help" command is called, a brief description should be given
     * @return The description that appears in the 'help' function.
     */
    public abstract String description();
    
    /**
     * User will makes a selection from the given list of objects.
     * @param <T> The expected input type
     * @param objects Users selection list
     * @return The object that the user has selected
     */
    protected static <T> T promptUserSelectionFromList(T[] objects)
    {
        return promptUserSelectionFromList(objects, System.in);
    }
    /**
     * User will makes a selection from the given list of objects.
     * @param <T> The type of objects the user is selecting
     * @param objects A users objects to select
     * @return The index the user has selected
     */
    protected static <T> int promptUserSelectionIndex(T[] objects)
    {
        return promptUserSelectionIndex(objects, System.in);
    }
    /**
     * This function prints the object array and prompts the user for a selection
     * ex:
     *      1. Object 0's name
     *      2. Object 1's name
     *      3. Object 2's name
     *      ... So on
     *      N. Object N's name
     *      Please input a selection number.
     *        
     * @param <T> The users selectable object
     * @param objects The list of possibilities for the user to select.
     * @param inputSteam The input stream for item selection
     * @return null if no option was selected or the list was empty. 
     * If the length of the list is 1, than it just returns the single object.
     * Returns the users selection if nothing is chosen.
     */
    protected static <T> T promptUserSelectionFromList(T[] objects, InputStream inputSteam)
    {
        int output = promptUserSelectionIndex(objects,inputSteam);
        if(output == -1)
            return null;
        
        return objects[output];
    }
    protected static <T> int promptUserSelectionIndex(T[] objects, InputStream inputSteam)
    {
        if(objects.length == 0)
            return INVALID_ENTRY;
        if(objects.length == 1)
            return 0;//No need to prompt
        
        int index;
        for(index = 0; index < objects.length; index++)
        {
            System.out.println(String.format("[ %d ] : %s", index+1, objects[index].toString()));
        }
        System.out.println(String.format("[ %d ] : Exit this menu. \n\n", ++index));
        System.out.println("-----------------------------");
        
        //Scanning
        int selection = -1;//invalid
        Scanner scanner = new Scanner(inputSteam);
        do {
            System.out.print(String.format("Please select an option between[1 & %d]", index));
            if (scanner.hasNextInt())
            {
                selection = scanner.nextInt();
            } 
            else
            {
                System.out.println("Invalid input, please enter a number.");
                scanner.next(); // discard invalid token
            }
        }while(selection <= 0 || selection > index);
        //Quit menu...
        if(selection == index)
        {
            return EXIT;
        }
        else
        {
            return (selection - 1);
        }
    }
}
