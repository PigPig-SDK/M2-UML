package com.unittests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.umlproject.*;

import static org.junit.jupiter.api.Assertions.*;


public class CommandTests
{
    //Test incomplete, temporarily prints, will add checks in a bit
    @Test
    public void list_success_test()
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

    @Test
    public void rename_class_success()
    {
        UMLDocument doc = UMLDocument.getInstance();

        doc.addClass("a");
        TerminalHandler.runCommand("rename class a b");
        assertNull(doc.getClass("a"));
        assertNotNull(doc.getClass("b"));


    }

    @Test
    public void rename_class_duplicate_exists()
    {

        ByteArrayOutputStream caught = new ByteArrayOutputStream();
        System.setOut(new PrintStream(caught));

        UMLDocument doc = UMLDocument.getInstance();

        doc.addClass("a");
        doc.addClass("b");
        TerminalHandler.runCommand("rename class a b");
        assertEquals( "---------------------\r\nClass rename failed".trim(), caught.toString().trim());


    }

    @Test
    public void rename_method_success()
    {

        UMLDocument doc = UMLDocument.getInstance();

        ArrayList<UMLParameter> temp= new ArrayList<UMLParameter>();
        temp.add(new UMLParameter("temp", DataType.INT, "other"));

        doc.addClass("a");
        doc.getClass("a").addMethod(new UMLMethod("Burger", temp));
        TerminalHandler.runCommand("rename method a Burger Sandwich");
        assertNull(doc.getClass("a").getMethods("Burger"));
        assertNotNull(doc.getClass("a").getMethods("Sandwich"));


    }
    @Test
    public void rename_field_success()
    {

        UMLDocument doc = UMLDocument.getInstance();

        doc.addClass("a");
        doc.getClass("a").addField(new UMLDataField("Burger", DataType.INT));
        TerminalHandler.runCommand("rename field a Burger Sandwich");
        assertNull(doc.getClass("a").getFields("Burger"));
        assertNotNull(doc.getClass("a").getFields("Sandwich"));


    }



}
