package org.umlproject;

import org.controlsfx.control.textfield.TextFields;
import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultHighlighter;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.terminal.impl.ExternalTerminal;
import org.umlproject.UI.GuiConsole;
import org.umlproject.UI.GuiController;
import org.umlproject.UI.GuiRelationship;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

        AnsiConsole.systemInstall();
        this.autoWordList =  new ArrayList<>(Arrays.asList(
                "add", "help", "list", "load", "quit", "remove", "rename", "save",
                "class", "classes", "relationship", "method","field", "param", "params",
                "byte", "short", "int", "integer", "long", "float", "double", "char", "character",
                "bool", "boolean", "aggregation", "composition", "generalization", "realization"));

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
        GuiController.singleton.updateGUIAutoComplete();

    }

    /**
     * Adds a set of words to the line reader
     *
     * @param in - Set to be added
     */
    public void addWordSet(Set<String> in){

        this.autoWordList.addAll(in);
        reader = newLineReader();
        GuiController.singleton.updateGUIAutoComplete();

    }

    /**
     * Removes a word from the line reader
     *
     * @param in - Word to be removed
     */
    public void removeWord(String in){

        this.autoWordList.remove(in);
        reader = newLineReader();
        GuiController.singleton.updateGUIAutoComplete();

    }

    /**
     * Removes a set of words from the line reader
     *
     * @param in - Set to be removed
     */
    public void removeWordSet(Set<String> in){

        this.autoWordList.removeAll(in);
        reader = newLineReader();
        GuiController.singleton.updateGUIAutoComplete();

    }

    /**
     * Reads a line. Used with the terminal launch option
     */
    public String lineInConsole(){
        return reader.readLine();
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

    public ArrayList<String> getAutoWordList(){
        return this.autoWordList;
    }
}