package org.umlproject.Commands;

import org.umlproject.UI.GuiCopyPaste;
import org.umlproject.UMLDocument;

public class CommandPaste extends BaseCommand{

    @Override
    public String actionName() {
        return "paste";
    }

    @Override
    public void act(String[] args) {
        GuiCopyPaste.getInstance().paste();
        System.out.println("Elements successfully pasted");
    }

    @Override
    public String description() {
        return "Pastes everything in the UML Document";
    }

}
