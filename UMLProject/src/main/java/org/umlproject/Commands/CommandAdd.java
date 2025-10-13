package org.umlproject.Commands;

public class CommandAdd extends BaseCommand
{
    @Override
    public String actionName() 
    {
        return "add";
    }
    @Override
    public void act(String[] args)
    {
        System.out.println("Please implement ADD method");
    }
    @Override
    public String description()
    {
        return """
               Possible parameters for add:
                        <class> <classname> : Adds a class with a given classname
                        <relationship> <source> <destination> : Adds a relationship between two classes
                        <method> <target class> <method name> <params...> : Adds a method to the specified class""";
    }
}
