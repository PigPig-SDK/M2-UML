package org.umlproject;

import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.util.*;

public class AutoComplete{

    private final ArrayList<String> autoWordList;

    private Terminal terminal;

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

        AnsiConsole.systemInstall();
        try {
            if(terminal == null) terminal = TerminalBuilder.builder().system(true).build();
            return LineReaderBuilder.builder().terminal(terminal)
                    .completer(new StringsCompleter(this.autoWordList)).build();
        }
        catch (Exception e){
            System.out.println("Error: " + e);
            return null;
        }
    }

    public void addWord(String in){

        this.autoWordList.add(in);
        reader = newLineReader();

    }

    public void addWordSet(Set<String> in){

        this.autoWordList.addAll(in);
        reader = newLineReader();

    }

    public void removeWord(String in){

        this.autoWordList.remove(in);
        reader = newLineReader();

    }



    public String lineInConsole(){
        return reader.readLine();
    }

    public String lineInGUI(String in){
        return reader.readLine(in);
    }

}