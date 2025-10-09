package org.umlproject;

import java.util.Scanner;

public class Main 
{
    public static void main(String[] args) 
    {
        Scanner scanner = new Scanner(System.in);
        TerminalHandler.runCommand("help");
        TerminalHandler.printLineBreak();
        do
        {
            TerminalHandler.runCommand(scanner.nextLine());
        }while(TerminalHandler.isRunning);
    }
}