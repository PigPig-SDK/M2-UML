package org.umlproject.Commands;

public class CommandRemove extends BaseCommand{

    @Override
    public String actionName() {
        return "remove";
    }

    @Override
    public void act(String[] args) {
    }

    @Override
    public String description() {
        return """
               Possible parameters for remove:
                        <class> <classname> : Removes a class
                        <relationship> <source> : Removes a relationship from a list of options
                        <relationship> <source> <destination> : Removes a relationship
                        <method> <target class> <method name> <params...> : Adds a method to the specified class""";
    }
    
}
