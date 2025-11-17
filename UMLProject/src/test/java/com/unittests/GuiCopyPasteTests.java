package com.unittests;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.umlproject.*;
import org.umlproject.UI.GuiCamera;
import org.umlproject.UI.GuiController;
import org.umlproject.UI.GuiCopyPaste;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GuiCopyPasteTests {

    @BeforeEach
    void setUp() {
        UMLDocument.getInstance().getClassSet().clear();
        UMLDocument.getInstance().getRelationshipList().clear();
    }
    @Test
    public void copySingleClass(){
        // Arrange
        UMLDocument.getInstance().addClass("test");
        // Act
        GuiCopyPaste.getInstance().copy();
        GuiCopyPaste.getInstance().paste();
        // Assert
        assertTrue(UMLDocument.getInstance().getClassSet().containsKey("test-copy1"));
    }

    @Test
    public void copyMultipleClasses(){
        // Arrange
        UMLDocument doc = UMLDocument.getInstance();
        doc.addClass("test");
        doc.addClass("test2");
        // Act
        GuiCopyPaste.getInstance().copy();
        GuiCopyPaste.getInstance().paste();
        // Assert
        assertTrue(UMLDocument.getInstance().getClassSet().containsKey("test-copy1"));
        assertTrue(UMLDocument.getInstance().getClassSet().containsKey("test2-copy1"));
    }

    @Test
    public void copySingleRelationship(){
        // Arrange
        UMLDocument doc = UMLDocument.getInstance();
        doc.addClass("test");
        doc.addClass("test2");
        doc.addRelationship("test", "test2", "AGGREGATION");
        // Act
        GuiCopyPaste.getInstance().copy();
        GuiCopyPaste.getInstance().paste();
        // Assert
        assertNotNull(UMLDocument.getInstance().getRelationship("test-copy1", "test2-copy1"));
    }

    @Test
    public void copyMultipleRelationships(){
        // Arrange
        UMLDocument doc = UMLDocument.getInstance();
        doc.addClass("test");
        doc.addClass("test2");
        doc.addClass("test3");
        doc.addRelationship("test", "test2", "AGGREGATION");
        doc.addRelationship("test2", "test3", "AGGREGATION");
        // Act
        GuiCopyPaste.getInstance().copy();
        GuiCopyPaste.getInstance().paste();
        // Assert
        assertNotNull(UMLDocument.getInstance().getRelationship("test-copy1", "test2-copy1"));
        assertNotNull(UMLDocument.getInstance().getRelationship("test2-copy1", "test3-copy1"));
    }

    @Test
    public void justPasteMultipleTimes(){
        // Arrange
        UMLDocument doc = UMLDocument.getInstance();
        doc.addClass("test");
        // Act
        GuiCopyPaste.getInstance().copy();
        GuiCopyPaste.getInstance().paste();
        GuiCopyPaste.getInstance().paste();
        // Assert
        assertTrue(UMLDocument.getInstance().getClassSet().containsKey("test-copy1"));
        assertTrue(UMLDocument.getInstance().getClassSet().containsKey("test-copy2"));
    }
    @Test
    public void copyPasteMultipleTimes(){
        // Arrange
        UMLDocument doc = UMLDocument.getInstance();
        doc.addClass("test");
        // Act
        GuiCopyPaste.getInstance().copy();
        GuiCopyPaste.getInstance().paste();
        GuiCopyPaste.getInstance().copy();
        GuiCopyPaste.getInstance().paste();
        // Assert
        assertTrue(UMLDocument.getInstance().getClassSet().containsKey("test-copy1"));
        assertTrue(UMLDocument.getInstance().getClassSet().containsKey("test-copy2"));
        assertTrue(UMLDocument.getInstance().getClassSet().containsKey("test-copy1-copy1"));
    }

    @Test
    public void justPasteMultipleTimesWithRelationship(){
        // Arrange
        UMLDocument doc = UMLDocument.getInstance();
        doc.addClass("test");
        doc.addClass("test2");
        doc.addRelationship("test", "test2", "AGGREGATION");
        // Act
        GuiCopyPaste.getInstance().copy();
        GuiCopyPaste.getInstance().paste();
        GuiCopyPaste.getInstance().paste();
        // Assert
        assertNotNull(UMLDocument.getInstance().getRelationship("test-copy1", "test2-copy1"));
        assertNotNull(UMLDocument.getInstance().getRelationship("test-copy2", "test2-copy2"));
    }

    @Test
    public void copyPasteMultipleTimesWithRelationship(){
        // Arrange
        UMLDocument doc = UMLDocument.getInstance();
        doc.addClass("test");
        doc.addClass("test2");
        doc.addRelationship("test", "test2", "AGGREGATION");
        // Act
        GuiCopyPaste.getInstance().copy();
        GuiCopyPaste.getInstance().paste();
        GuiCopyPaste.getInstance().copy();
        GuiCopyPaste.getInstance().paste();
        // Assert
        assertNotNull(UMLDocument.getInstance().getRelationship("test-copy1", "test2-copy1"));
        assertNotNull(UMLDocument.getInstance().getRelationship("test-copy2", "test2-copy2"));
        assertNotNull(UMLDocument.getInstance().getRelationship("test-copy1-copy1", "test2-copy1-copy1"));
    }




}
