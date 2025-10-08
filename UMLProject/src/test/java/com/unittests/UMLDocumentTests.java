package com.unittests;

import static org.junit.jupiter.api.Assertions.*;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;

public class UMLDocumentTests
{
    @org.junit.jupiter.api.Test
    public void getFileLocation_constructor_success()
    {
        // Arrange
        String fileString = "test.json";
        UMLDocument umldocument = new UMLDocument(fileString);
        // Act
        // Assert
        assertEquals(fileString, umldocument.getFileLocation()); 
    }
    @org.junit.jupiter.api.Test
    public void addClass_multiclass_success()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("test.json");
        // Act
        umldocument.addClass("test class");
        umldocument.addClass("test class 2!!!!!!!!");
        // Assert
        assertEquals(2, umldocument.getClassCount());
    }
        @org.junit.jupiter.api.Test
    public void addClass_dupeclass_success()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("test.json");
        // Act
        umldocument.addClass("test class");
        UMLClass testclass = umldocument.addClass("test class");
        // Assert
        assertEquals(1, umldocument.getClassCount());
        assertNull(testclass);
    }
        @org.junit.jupiter.api.Test
    public void getClass_doesntExist_success()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("test.json");
        // Act
        UMLClass testclass1 = umldocument.addClass("test class");
        UMLClass testclass2 = umldocument.getClass("test class");
        
        
        assertEquals(testclass1,testclass2);
        assertNull(umldocument.getClass("this class isnt real"));
    }
}
