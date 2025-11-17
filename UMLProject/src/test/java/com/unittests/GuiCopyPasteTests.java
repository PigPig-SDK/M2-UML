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
        assertTrue(UMLDocument.getInstance().getClassSet().containsKey("test-copy"));
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
        assertTrue(UMLDocument.getInstance().getClassSet().containsKey("test-copy"));
        assertTrue(UMLDocument.getInstance().getClassSet().containsKey("test2-copy"));
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
        assertNotNull(UMLDocument.getInstance().getRelationship("test-copy", "test2-copy"));
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
        assertNotNull(UMLDocument.getInstance().getRelationship("test-copy", "test2-copy"));
        assertNotNull(UMLDocument.getInstance().getRelationship("test2-copy", "test3-copy"));
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
        assertTrue(UMLDocument.getInstance().getClassSet().containsKey("test-copy"));
        assertTrue(UMLDocument.getInstance().getClassSet().containsKey("test-copy-copy"));
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
        assertTrue(UMLDocument.getInstance().getClassSet().containsKey("test-copy"));
        assertTrue(UMLDocument.getInstance().getClassSet().containsKey("test-copy-copy"));
        assertTrue(UMLDocument.getInstance().getClassSet().containsKey("test-copy-copy-copy"));
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
        assertNotNull(UMLDocument.getInstance().getRelationship("test-copy", "test2-copy"));
        assertNotNull(UMLDocument.getInstance().getRelationship("test-copy-copy", "test2-copy-copy"));
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
        assertNotNull(UMLDocument.getInstance().getRelationship("test-copy", "test2-copy"));
        assertNotNull(UMLDocument.getInstance().getRelationship("test-copy-copy",
                "test2-copy-copy-copy"));
        assertNotNull(UMLDocument.getInstance().getRelationship("test-copy-copy-copy",
                "test2-copy-copy"));
    }




}
