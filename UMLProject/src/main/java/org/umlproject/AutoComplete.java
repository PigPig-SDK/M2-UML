package org.umlproject;

import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.util.*;

public class AutoComplete implements DocumentListner{

    private final ArrayList<String> autoWordList;

    private Terminal terminal;

    private static LineReader reader;

    private static AutoComplete instance;

    /**
     * AutoComplete constructor; Initializes default word list and adds itself as a documentListner
     */
    private AutoComplete(){

        this.autoWordList =  new ArrayList<>(Arrays.asList(
                "add", "help", "list", "load", "quit", "remove", "rename", "save",
                "class", "classes", "relationship", "method","field", "param", "params",
                "Add", "Help", "List", "Load", "Quit", "Remove", "Rename", "Save",
                "Class", "Classes", "Relationship", "Method", "Field", "Param", "Params"));

        reader = newLineReader();
        UMLDocument.documentListners.add(this);

    }

    /**
     * Standard singleton getInstance
     *
     * @return AutoComplete - Main instance
     */
    public static synchronized AutoComplete getInstance()
    {
        if(instance == null)
        {
            instance = new AutoComplete();
        }
        return instance;
    }

    /**
     * Builds line reader used for autocomplete
     *
     * @return LineReader - The built reader
     */

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

    /**
     * Adds a word to the line reader
     *
     * @param in - Word to be added
     */
    public void addWord(String in){

        this.autoWordList.add(in);
        reader = newLineReader();

    }

    /**
     * Adds a set of words to the line reader
     *
     * @param in - Set to be added
     */
    public void addWordSet(Set<String> in){

        this.autoWordList.addAll(in);
        reader = newLineReader();

    }

    /**
     * Removes a word from the line reader
     *
     * @param in - Word to be removed
     */
    public void removeWord(String in){

        this.autoWordList.remove(in);
        reader = newLineReader();

    }

    /**
     * Removes a set of words from the line reader
     *
     * @param in - Set to be removed
     */
    public void removeWordSet(Set<String> in){

        this.autoWordList.removeAll(in);
        reader = newLineReader();

    }

    /**
     * Reads a line. Used with the terminal launch option
     */
    public String lineInConsole(){
        return reader.readLine();
    }

    /**
     * Reads a line. Used with the GUI launch option.
     */
    public String lineInGUI(String in){
        return reader.readLine(in);
    }

    /**
     * Interface method onClassRemove. Removes all relevant autocomplete words when a class is removed.
     *
     * @param umlClass - Class to be checked
     */
    @Override
    public void onClassRemove(UMLClass umlClass) {
        removeWord(umlClass.getClassName());
        if(!umlClass.getMethodsAll().isEmpty()){
            for(String methodNames : umlClass.getMethodsAll().keySet()){
                for(UMLMethod m : umlClass.getMethods(methodNames))    {
                    removeWord(m.getMethodName());
                    for(UMLParameter p : m.getParameters()){
                        removeWord(p.getName());
                    }
                }
            }
        }
        if(!umlClass.getFieldsAll().isEmpty()){
            removeWordSet(umlClass.getFieldsAll().keySet());
        }
    }

    /**
     * Interface method. Not used.
     *
     * @param umlClass - Not Used
     */
    @Override
    public void onRelationshipRemove(UMLRelationship umlClass) {}

    /**
     * Interface method onClassAdded. Adds all relevant autocomplete words when a class is added.
     *
     * @param umlClass - Class to be checked
     */
    @Override
    public void onClassAdded(UMLClass umlClass, boolean isLoading) {
        addWord(umlClass.getClassName());
        if(!umlClass.getMethodsAll().isEmpty()){
            for(String methodNames : umlClass.getMethodsAll().keySet()){
                for(UMLMethod m : umlClass.getMethods(methodNames))    {
                    addWord(m.getMethodName());
                    for(UMLParameter p : m.getParameters()){
                        addWord(p.getName());
                    }
                }
            }
        }
        if(!umlClass.getFieldsAll().isEmpty()){
            addWordSet(umlClass.getFieldsAll().keySet());
        }
    }

    /**
     * Interface method. Not used.
     *
     * @param umlRelationship - Not Used
     * @param isLoading - Not Used
     */
    @Override
    public void onRelationshipAdded(UMLRelationship umlRelationship, boolean isLoading) {}

    /**
     * Interface method. Not used.
     *
     * @param umlDocument - Not Used
     */
    @Override
    public void loadFile(UMLDocument umlDocument) {

    }
}