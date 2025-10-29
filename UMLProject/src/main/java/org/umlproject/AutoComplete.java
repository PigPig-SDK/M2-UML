package org.umlproject;

import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.TerminalBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class AutoComplete{

    private final ArrayList<String> autoWordList;
    private static LineReader reader;

    public AutoComplete(){

        this.autoWordList =  new ArrayList<>(Arrays.asList(
                "add", "help", "list", "load", "quit", "remove", "rename", "save",
                "class", "classes", "relationship", "method","field", "param", "params",
                "Add", "Help", "List", "Load", "Quit", "Remove", "Rename", "Save",
                "Class", "Classes", "Relationship", "Method", "Field", "Param", "Params"));

        reader = newLineReader();


    }

    private LineReader newLineReader(){

        try {
            AnsiConsole.systemInstall();
            return LineReaderBuilder.builder().terminal(TerminalBuilder.builder().system(true).build())
                    .completer(new StringsCompleter(this.autoWordList)).build();
        }
        catch (Exception e){
            System.out.println("Error: " + e);
            return null;
        }
    }

    public void updateLineReader(String in){

        AnsiConsole.systemInstall();
        this.autoWordList.add(in);
        try {
            reader = LineReaderBuilder.builder().terminal(TerminalBuilder.builder().system(true).build())
                    .completer(new StringsCompleter(this.autoWordList)).build();
        }
        catch (Exception e){
            System.out.println("Error: " + e);
        }

    }

    public String lineInConsole(){
        return reader.readLine();
    }

    public String lineInGUI(String in){
        return reader.readLine(in);
    }

}