package com.unittests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;

public class UMLDocumentTests
{
    @Test
    public void getFileLocation_constructor_success()
    {
        // Arrange
        String fileString = "test.json";
        UMLDocument umldocument = new UMLDocument(fileString);
        // Act
        // Assert
        assertEquals(fileString, umldocument.getFileLocation());
    }
    @Test
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
    @Test
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

    @Test
    public void deleteClass_successfulDeletion_success()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("test.json");
        // Act
        umldocument.addClass("test class 1");
        // Assert
        assertTrue(umldocument.deleteClass("test class 1"));
        assertNull(umldocument.getClass("test class 1"));
    }
    @Test
    public void deleteClass_classDoesntExist_success()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("test.json");
        // Act
        umldocument.addClass("test class 1");
        // Assert
        assertFalse(umldocument.deleteClass("test class 2"));
    }
    @Test
    public void renameClass_newNameAlreadyExists_success()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("test.json");
        // Act
        umldocument.addClass("test class 1");
        umldocument.addClass("test class 2");
        // Assert
        assertFalse(umldocument.renameClass("test class 2", "test class 1"));
    }
    @Test
    public void renameClass_originalNameDoesntExist_success()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("test.json");
        // Act
        umldocument.addClass("test class 1");
        // Assert
        assertFalse(umldocument.renameClass("test class 2", "test class 3"));
    }

    @Test
    public void renameClass_successfulRename_success()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("test.json");
        // Act
        umldocument.addClass("test class 1");
        umldocument.renameClass("test class 1", "test class 2");
        // Assert

        //Currently non-functioning until addClass has functionality to add class to relationshipList
        assertNull(umldocument.getClass("test class 1"));
        assertNotNull(umldocument.getClass("test class 2"));
    }
    @Test
    public void getClass_doesntExist_success()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("test.json");
        // Act
        UMLClass testclass1 = umldocument.addClass("test class");
        UMLClass testclass2 = umldocument.getClass("test class");
        // Assert
        assertEquals(testclass1,testclass2);
        assertNull(umldocument.getClass("this class isnt real"));
    }
    @Test
    public void addRelationship_nonExisting_success()
    {
        // Arrange
        String fileString = "test.json";
        UMLDocument umldocument = new UMLDocument(fileString);
        umldocument.addClass("test");
        umldocument.addClass("endpoint");
        // Act
        umldocument.addRelationship("test", "endpoint");
        // Assert
        assertTrue(umldocument.hasRelationship("test","endpoint"));
    }
    @Test
    public void addRelationship_multiple_success()
    {
        // Arrange
        String fileString = "test.json";
        UMLDocument umldocument = new UMLDocument(fileString);
        umldocument.addClass("a");
        umldocument.addClass("b");
        umldocument.addClass("c");
        // Act
        umldocument.addRelationship("a", "b");
        umldocument.addRelationship("a", "c");
        // Assert
        assertTrue(umldocument.hasRelationship("a","b"));
        assertTrue(umldocument.hasRelationship("a","c"));
        assertFalse(umldocument.hasRelationship("c","a"));
        assertFalse(umldocument.hasRelationship("b","a"));
        assertFalse(umldocument.hasRelationship("c","b"));
    }
    @Test
    public void removeRelationship_multiple_success()
    {
        // Arrange
        String fileString = "test.json";
        UMLDocument umldocument = new UMLDocument(fileString);
        umldocument.addClass("a");
        umldocument.addClass("b");
        umldocument.addClass("c");
        // Act
        umldocument.addRelationship("a", "b");
        umldocument.addRelationship("a", "c");
        // Assert
        assertEquals(2,umldocument.getAllRelationships("a").size());

        assertTrue(umldocument.hasRelationship("a","b"));
        assertTrue(umldocument.removeRelationship("a","b"));
        assertFalse(umldocument.hasRelationship("a","b"));

        assertTrue(umldocument.hasRelationship("a","c"));
        assertTrue(umldocument.removeRelationship("a","c"));
        assertFalse(umldocument.hasRelationship("a","c"));
        assertEquals(0,umldocument.getAllRelationships("a").size());
    }
    @Test
    public void getAllRelationships_multiple_success()
    {
        // Arrange
        String fileString = "test.json";
        UMLDocument umldocument = new UMLDocument(fileString);
        umldocument.addClass("a");
        umldocument.addClass("b");
        umldocument.addClass("c");
        // Act
        umldocument.addRelationship("a", "b");
        umldocument.addRelationship("a", "c");
        // Assert
        assertEquals(2,umldocument.getAllRelationships("a").size());
    }
    @Test
    public void deleteAllRelationships_multiple_success()
    {
        // Arrange
        String fileString = "test.json";
        UMLDocument umldocument = new UMLDocument(fileString);
        umldocument.addClass("a");
        umldocument.addClass("b");
        umldocument.addClass("c");
        // Act
        umldocument.addRelationship("a", "b");
        umldocument.addRelationship("a", "c");
        // Assert
        assertEquals(2,umldocument.getAllRelationships("a").size());
        assertTrue(umldocument.removeClassKeyFromRelationships("a"));
        assertNull(umldocument.getAllRelationships("a"));
    }
    @Test
    public void addRelationship_duplicate_failure()
    {
        // Arrange
        String fileString = "test.json";
        UMLDocument umldocument = new UMLDocument(fileString);
        umldocument.addClass("a");
        umldocument.addClass("b");
        umldocument.addClass("c");
        // Act/Assert
        assertTrue(umldocument.addRelationship("a", "b"));
        assertFalse(umldocument.addRelationship("a", "b"));
        assertEquals(1,umldocument.getAllRelationships("a").size());
    }
       @Test 
        public void save_load_UMLDocument_Success()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("test1");
        UMLDocument umldocument2 = new UMLDocument("test2");
        umldocument.addClass("a");
        umldocument.addClass("b");
        umldocument.addClass("c");
        umldocument.save("ello");
        umldocument2.load("ello");
        umldocument2.addClass("d");
        umldocument2.quickLoad();
        // Act/Assert
        assertNotNull(umldocument2.getAllRelationships("a"));
        assertEquals(umldocument, umldocument2);
    }
        @Test 
        public void isFileLocationValid_Success()
    {
        // Arrange
        String fileString = "test.json";
        UMLDocument umldocument = new UMLDocument(fileString);
        // Act/Assert
        assertTrue(umldocument.isFileLocationValid());
    }
}
