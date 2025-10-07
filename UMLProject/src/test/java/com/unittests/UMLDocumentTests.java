package com.unittests;

import static org.junit.jupiter.api.Assertions.*;
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
}
