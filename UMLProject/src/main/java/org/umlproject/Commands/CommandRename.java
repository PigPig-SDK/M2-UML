package org.umlproject.Commands;

public class CommandRename extends BaseCommand{

    @Override
    public String actionName() {
        return "rename";
    }

    @Override
    public void act(String[] args) {
        
    }

    @Override
    public String description() {
        return """
               Possible parameters for rename:
                        <class> <classname> <newname> : Renames a class
                        ... so on...""";
    }
    
}
