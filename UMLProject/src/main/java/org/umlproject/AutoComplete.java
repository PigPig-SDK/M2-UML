package org.umlproject;

import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.util.*;

public class AutoComplete implements MementoListener<UMLDocument> {

    private final ArrayList<String> defaultWordList;

    private final ArrayList<String> autoWordList;

    private Terminal terminal;

    private static LineReader reader;

    private static AutoComplete instance;

    /**
     * AutoComplete constructor; Initializes default word list and adds itself as a documentListner
     */
    private AutoComplete(){

        this.defaultWordList =  new ArrayList<>(Arrays.asList(
                "add", "help", "list", "load", "quit", "remove", "rename", "save",
                "class", "classes", "relationship", "method","field", "param", "params",
                "byte", "short", "int", "integer", "long", "float", "double", "char", "character",
                "bool", "boolean", "aggregation", "composition", "generalization", "realization"));

        this.autoWordList = new ArrayList<>();
        reader = newLineReader();
        UMLDocument.getMemento().addListener(this);

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

        this.autoWordList.addAll(this.defaultWordList);
        AnsiConsole.systemInstall();
        try {
            if(terminal == null) terminal = TerminalBuilder.builder().system(true).build();
            //Uses custom complete method with StringCompleter
            return LineReaderBuilder.builder().terminal(terminal)
                    .completer((non, in, available) -> {
                        String parsedIn = in.word().trim();
                        for (String options : autoWordList) {
                            if (parsedIn.isEmpty() || options.startsWith(parsedIn)) {
                                available.add(new Candidate(options));
                            }
                        }
                    }).build();
        }
        catch (Exception e){
            System.out.println("Error: " + e);
            return null;
        }
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


    @Override
    public void update(Memento<UMLDocument> memento) {
        this.autoWordList.clear();
        //Add classes
        this.autoWordList.addAll(memento.getInstance().getClassSet().keySet());
        for(UMLClass currentClass : memento.getInstance().getClassSet().values()){
            //Add class fields
            this.autoWordList.addAll(currentClass.getFieldsAll().keySet());
            //Add class methods
            this.autoWordList.addAll(currentClass.getMethodsAll().keySet());
            for(ArrayList<UMLMethod> currentMethodName : currentClass.getMethodsAll().values()){
                for(UMLMethod currentOverloadedMethod : currentMethodName){
                    for(UMLParameter currentParameter : currentOverloadedMethod.getParameters()){
                        //Add method parameters
                        this.autoWordList.add(currentParameter.getName());
                    }
                }
            }
        }
        
        for(ArrayList<UMLRelationship> currentClassRelationships : memento.getInstance().getRelationshipList().values()){
            for(UMLRelationship currentRelationship : currentClassRelationships){
                if(currentRelationship.getRelationshipType() == RelationshipType.OTHER){
                    //Add custom relationship type
                    this.autoWordList.add(currentRelationship.getCustomNameType());
                }
            }
        }
        reader = newLineReader();
    }

}