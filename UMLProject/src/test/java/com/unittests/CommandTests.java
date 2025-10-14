package com.unittests;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.umlproject.*;


public class CommandTests
{
    //Temporarily prints, will add checks in a bit
    @Test
    public void test()
    {
        UMLDocument.getInstance().addClass("a");
        UMLDocument.getInstance().addClass("b");
        UMLDocument.getInstance().addClass("c");
        UMLDocument.getInstance().getClass("a").addField(new UMLDataField("field 1",
                "field 1 custom"));
        UMLDocument.getInstance().getClass("a").addField(new UMLDataField("field 2",
                "field 2 custom"));
        UMLDocument.getInstance().getClass("a").addMethod(new UMLMethod("method 1",
                new ArrayList<UMLParameter>()));
        UMLDocument.getInstance().getClass("a").addMethod(new UMLMethod("method 2",
                new ArrayList<UMLParameter>()));
        UMLDocument.getInstance().addRelationship("a", "b");
        UMLDocument.getInstance().addRelationship("a", "c");
        TerminalHandler.runCommand("list classes");
        TerminalHandler.runCommand("list relationships");
        TerminalHandler.runCommand("list a");
        TerminalHandler.runCommand("list e");
    }
}
