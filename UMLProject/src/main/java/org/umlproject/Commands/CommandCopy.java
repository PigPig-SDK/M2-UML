package org.umlproject.Commands;

import org.umlproject.UI.GuiCopyPaste;
import org.umlproject.UMLDiagramElement;
import org.umlproject.UMLDocument;
import org.umlproject.UMLParameter;

import java.util.ArrayList;

public class CommandCopy extends BaseCommand{

    @Override
    public String actionName() {
        return "copy";
    }

    @Override
    public void act(String[] args) {
        GuiCopyPaste.getInstance().copy();
        ArrayList<UMLDiagramElement> copiedElements = GuiCopyPaste.getInstance().getCopiedObjects();
        if(copiedElements == null || copiedElements.isEmpty()){
            System.out.println("No elements to copy");
            return;
        }
        System.out.println("Currently copied elements: ");
        for(UMLDiagramElement element : GuiCopyPaste.getInstance().getCopiedObjects()){
            System.out.println(element.toString());
        }
    }

    @Override
    public String description() {
        return "Copies everything in the UML Document";
    }

}
