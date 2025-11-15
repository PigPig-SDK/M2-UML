package com.unittests;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.umlproject.DiagramElementListener;
import org.umlproject.DocumentListner;
import org.umlproject.DocumentState;
import org.umlproject.RelationshipType;
import static org.umlproject.RelationshipType.GENERALIZATION;
import org.umlproject.UMLClass;
import org.umlproject.UMLDiagramElement;
import org.umlproject.UMLDocument;
import org.umlproject.UMLRelationship;

public class UMLDocumentTests
{

    @Test
    public void getFileLocation_constructor_success()
    {
        // Arrange
        String fileString = "test";
        UMLDocument umldocument = new UMLDocument(fileString);
        // Act
        // Assert
        assertEquals(fileString, umldocument.getFileLocation());
    }
    @Test
    public void addClass_multiclass_success()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("test");
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
        UMLDocument umldocument = new UMLDocument("test");
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
        UMLDocument umldocument = new UMLDocument("test");
        // Act
        umldocument.addClass("testclass1");
        // Assert
        assertNotNull(umldocument.removeClass("testclass1"));
        assertNull(umldocument.getClass("testclass1"));
    }
    @Test
    public void addClass_removesSpaces_success()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("test");
        // Act
        umldocument.addClass("test class 1");
        // Assert
        assertNotNull(umldocument.removeClass("testclass1"));
    }
    
    @Test
    public void deleteClass_classDoesntExist_success()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("test");
        // Act
        umldocument.addClass("test class 1");
        // Assert
        assertNull(umldocument.removeClass("test class 2"));
    }
    @Test
    public void renameClass_newNameAlreadyExists_success()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("test");
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
        UMLDocument umldocument = new UMLDocument("test");
        // Act
        umldocument.addClass("test class 1");
        // Assert
        assertFalse(umldocument.renameClass("test class 2", "test class 3"));
    }
    
    @Test
    public void renameClass_successfulRename_success()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("test");
        // Act
        umldocument.addClass("testclass1");
        umldocument.renameClass("testclass1", "testclass2");
        // Assert

        //Currently non-functioning until addClass has functionality to add class to relationshipList
        assertNull(umldocument.getClass("testclass1"));
        assertNotNull(umldocument.getClass("testclass2"));
    }
    

    
    @Test
    public void getClass_doesntExist_success()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("test");
        // Act
        UMLClass testclass1 = umldocument.addClass("testclass");
        UMLClass testclass2 = umldocument.getClass("testclass");
        // Assert
        assertEquals(testclass1,testclass2);
        assertNull(umldocument.getClass("this class isnt real"));
    }
     
    @Test
    public void addRelationship_nonExisting_success()
    {
        // Arrange
        String fileString = "test";
        UMLDocument umldocument = new UMLDocument(fileString);
        umldocument.addClass("test");
        umldocument.addClass("endpoint");
        // Act
        umldocument.addRelationship("test", "endpoint", "aggregation");
        // Assert
        assertTrue(umldocument.hasRelationship("test","endpoint"));
    }
    @Test
    public void addRelationship_multiple_success()
    {
        // Arrange
        String fileString = "test";
        UMLDocument umldocument = new UMLDocument(fileString);
        umldocument.addClass("a");
        umldocument.addClass("b");
        umldocument.addClass("c");
        // Act
        umldocument.addRelationship("a", "b", "aggregation");
        umldocument.addRelationship("a", "c", "aggregation");
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
        String fileString = "test";
        UMLDocument umldocument = new UMLDocument(fileString);
        umldocument.addClass("a");
        umldocument.addClass("b");
        umldocument.addClass("c");
        // Act
        umldocument.addRelationship("a", "b", "aggregation");
        umldocument.addRelationship("a", "c", "aggregation");
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
        String fileString = "test";
        UMLDocument umldocument = new UMLDocument(fileString);
        umldocument.addClass("a");
        umldocument.addClass("b");
        umldocument.addClass("c");
        // Act
        umldocument.addRelationship("a", "b", "aggregation");
        umldocument.addRelationship("a", "c", "aggregation");
        // Assert
        assertEquals(2,umldocument.getAllRelationships("a").size());
    }
    @Test
    public void deleteAllRelationships_multiple_success()
    {
        // Arrange
        String fileString = "test";
        UMLDocument umldocument = new UMLDocument(fileString);
        umldocument.addClass("a");
        umldocument.addClass("b");
        umldocument.addClass("c");
        // Act
        umldocument.addRelationship("a", "b", "aggregation");
        umldocument.addRelationship("a", "c", "aggregation");
        // Assert
        assertEquals(2,umldocument.getAllRelationships("a").size());
        assertTrue(umldocument.removeClassKeyFromRelationships("a"));
        assertNull(umldocument.getAllRelationships("a"));
    }
    @Test
    public void addRelationship_duplicate_failure()
    {
        // Arrange
        String fileString = "test";
        UMLDocument umldocument = new UMLDocument(fileString);
        umldocument.addClass("a");
        umldocument.addClass("b");
        umldocument.addClass("c");
        // Act/Assert
        assertTrue(umldocument.addRelationship("a", "b", "aggregation"));
        assertFalse(umldocument.addRelationship("a", "b", "aggregation"));
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
    public void isFileLocationValid_IsValid_Success()
    {
        // Arrange
        String fileString = "test";
        UMLDocument umldocument = new UMLDocument(fileString);
        // Act
        umldocument.save();
        // Assert
        assertTrue(umldocument.isFileLocationValid());
    }
    @Test 
    public void isFileLocationValid_FileNotValid_False()
    {
        // Arrange
        String fileString = "noFileCalledThisExists";
        UMLDocument umldocument = new UMLDocument(fileString);
        // Act
        // Assert
        assertFalse(umldocument.isFileLocationValid());
    }
    /********/
    /********/
    /********/
    /********/
    /*
        DOC LISTENER DEBUG stuff.
    
    */
    /********/
    /********/
    /********/
    /********/
    protected abstract class MockListener implements DiagramElementListener, DocumentListner
    {
        public int incrementor = 0;
        private MockListener()
        {
            UMLDiagramElement.globalListeners.add(this);
            UMLDocument.documentListners.add(this);
        }
        private void cleanUpListener()
        {
            UMLDiagramElement.globalListeners.remove(this);
            UMLDocument.documentListners.remove(this);
            assertFalse(UMLDiagramElement.globalListeners.contains(this));
            assertFalse(UMLDocument.documentListners.contains(this));
        }
        protected abstract void eventCalled();
        @Override public void update(Object desiredElement) { eventCalled();}
        @Override public void updateLocation(Object desiredElement) { eventCalled();}
        @Override public void onClassRemove(UMLClass umlClass) { eventCalled();}
        @Override public void onRelationshipRemove(UMLRelationship umlClass) { eventCalled();}
        @Override public void onClassAdded(UMLClass umlClass) { eventCalled();}
        @Override public void onRelationshipAdded(UMLRelationship umlRelationship) { eventCalled();}
        @Override public void cleanUp() {}
        @Override public void loadFile(UMLDocument umlDocument) {}
    }
    protected class NoMassOperationsListener extends MockListener
    {
        @Override
        protected void eventCalled() {
            if(UMLDocument.getDocumentState() == DocumentState.NORMAL)
                this.incrementor++;
        }
    }
    @Test 
    public void executeActionUnderState_MassOperation_Success()
    {
        // Arrange
        NoMassOperationsListener listener = new NoMassOperationsListener();
        UMLDocument umldocument = new UMLDocument("file");
        UMLDocument.executeActionUnderState(DocumentState.MASS_OPERATION, () -> umldocument.addClass("A"));
        // Act
        UMLClass toBeRemoved = UMLDocument.executeActionUnderState(DocumentState.MASS_OPERATION, () -> umldocument.removeClass("A"));
        
        UMLDocument.documentListners.remove(listener);
        // Assert
        assertNotNull(toBeRemoved);
        assertEquals(0, listener.incrementor);
        //Cleanup, includes assertions...
        listener.cleanUp();
    }
    
    /*
    ------------------------------------------------------------
    CLONABLE TESTING
    ------------------------------------------------------------
    */
    @Test 
    public void copy_classList_isEqual()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("Foobar");
        umldocument.addClass("Boring_Name");
        umldocument.addClass("Something_Else");
        // Act
        UMLDocument clone = umldocument.clone();
        // Assert
        assertEquals(clone.getClassCount(), umldocument.getClassCount());
    }
    @Test 
    public void copy_relationshipList_isEqual()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("Foobar");
        umldocument.addClass("a");
        umldocument.addClass("b");
        umldocument.addClass("c");
        umldocument.addRelationship("a", "b", "MyRelationshipType");
        umldocument.addRelationship("b", "c", "Someting");
        // Act
        UMLDocument clone = umldocument.clone();
        // Assert
        assertEquals(clone.getRelationshipList().size(), umldocument.getRelationshipList().size());
        assertEquals(clone.getRelationshipList(), umldocument.getRelationshipList());
    }
    @Test 
    public void copy_removeRelationship_isDeepCopy()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("Foobar");
        umldocument.addClass("a");
        umldocument.addClass("b");
        umldocument.addClass("c");
        umldocument.addRelationship("a", "b", "MyRelationshipType");
        umldocument.addRelationship("b", "c", "Someting");
        // Act
        UMLDocument clone = umldocument.clone();
        boolean isRemoved = clone.removeRelationship("a", "b");
        // Assert
        assertTrue(isRemoved);
        assertNotEquals(clone.getRelationshipList(), umldocument.getRelationshipList());
    }
    @Test 
    public void copy_modifyRelationship_isDeepCopy()
    {
        // Arrange
        UMLDocument umldocument = new UMLDocument("Foobar");
        umldocument.addClass("a");
        umldocument.addClass("b");
        umldocument.addRelationship("a", "b", "COMPOSITION");
        // Act
        UMLDocument clone = umldocument.clone();
        UMLRelationship relationship = clone.getRelationship("a", "b");
        relationship.setRelationshipType(RelationshipType.GENERALIZATION);
        // Assert
        assertNotEquals(RelationshipType.GENERALIZATION, umldocument.getRelationship("a", "b").getRelationshipType());
        assertNotEquals(umldocument.getRelationship("a", "b").getRelationshipType(), relationship.getRelationshipType());
    }
    @Test 
    public void singletonReset_isProperReset_Success()
    {
        // Arrange
        UMLDocument.resetInstance(true);
        UMLDocument.getInstance().addClass("A");
        UMLDocument.saveMementoState();
        UMLDocument.getInstance().addClass("B");
        UMLDocument.saveMementoState();
        UMLDocument.getInstance().addClass("C");
        UMLDocument.saveMementoState();
        UMLDocument.undoMementoState();
        // Act
        assertEquals(3, UMLDocument.getMemento().getHistoryLength());
        UMLDocument.resetInstance(true);
        // Assert
        assertEquals(1, UMLDocument.getMemento().getHistoryLength());
        assertEquals(0, UMLDocument.getMemento().getRedoHistoryLength());
    }
    @Test 
    public void undoMementoState_savesHistory_Success()
    {
        // Arrange
        UMLDocument.resetInstance(true);
        UMLDocument.getInstance().addClass("A");
        UMLDocument.saveMementoState();
        UMLDocument.getInstance().addClass("B");
        UMLDocument.saveMementoState();
        UMLDocument.getInstance().addClass("C");
        UMLDocument.saveMementoState();
        // UNDO 1...
        UMLDocument.undoMementoState();
        assertEquals(3, UMLDocument.getMemento().getHistoryLength());
        assertEquals(1, UMLDocument.getMemento().getRedoHistoryLength());
        assertNull(UMLDocument.getInstance().getClass("C"));
        assertNotNull(UMLDocument.getInstance().getClass("B"));
        assertNotNull(UMLDocument.getInstance().getClass("A"));
        // UNDO 2...
        UMLDocument.undoMementoState();
        assertEquals(2, UMLDocument.getMemento().getHistoryLength());
        assertEquals(2, UMLDocument.getMemento().getRedoHistoryLength());
        assertNull(UMLDocument.getInstance().getClass("C"));
        assertNull(UMLDocument.getInstance().getClass("B"));
        assertNotNull(UMLDocument.getInstance().getClass("A"));
        // UNDO 3...
        UMLDocument.undoMementoState();
        assertEquals(1, UMLDocument.getMemento().getHistoryLength());
        assertEquals(3, UMLDocument.getMemento().getRedoHistoryLength());
        assertNull(UMLDocument.getInstance().getClass("C"));
        assertNull(UMLDocument.getInstance().getClass("B"));
        assertNull(UMLDocument.getInstance().getClass("A"));
        // UNDO 4 (overflow case)
        UMLDocument.undoMementoState();
        assertEquals(1, UMLDocument.getMemento().getHistoryLength());
        assertEquals(3, UMLDocument.getMemento().getRedoHistoryLength());
        assertNull(UMLDocument.getInstance().getClass("C"));
        assertNull(UMLDocument.getInstance().getClass("B"));
        assertNull(UMLDocument.getInstance().getClass("A"));
    }
    @Test 
    public void redoMementoState_savesHistory_Success()
    {
        // Arrange
        UMLDocument.resetInstance(true);
        UMLDocument.getInstance().addClass("A");
        UMLDocument.saveMementoState();
        UMLDocument.getInstance().addClass("B");
        UMLDocument.saveMementoState();
        UMLDocument.getInstance().addClass("C");
        UMLDocument.saveMementoState();
        
        //Undo all states...
        UMLDocument.undoMementoState();
        UMLDocument.undoMementoState();
        UMLDocument.undoMementoState();
        
        //Redo 1
        UMLDocument.redoMementoState();
        assertEquals(2, UMLDocument.getMemento().getHistoryLength());
        assertEquals(2, UMLDocument.getMemento().getRedoHistoryLength());
        assertNull(UMLDocument.getInstance().getClass("C"));
        assertNull(UMLDocument.getInstance().getClass("B"));
        assertNotNull(UMLDocument.getInstance().getClass("A"));
        //Redo 2
        UMLDocument.redoMementoState();
        assertEquals(3, UMLDocument.getMemento().getHistoryLength());
        assertEquals(1, UMLDocument.getMemento().getRedoHistoryLength());
        assertNull(UMLDocument.getInstance().getClass("C"));
        assertNotNull(UMLDocument.getInstance().getClass("B"));
        assertNotNull(UMLDocument.getInstance().getClass("A"));
        //Redo 3
        UMLDocument.redoMementoState();
        assertEquals(4, UMLDocument.getMemento().getHistoryLength());
        assertEquals(0, UMLDocument.getMemento().getRedoHistoryLength());
        assertNotNull(UMLDocument.getInstance().getClass("C"));
        assertNotNull(UMLDocument.getInstance().getClass("B"));
        assertNotNull(UMLDocument.getInstance().getClass("A"));
        //Redo 4 (Overflow)
        UMLDocument.redoMementoState();
        assertEquals(4, UMLDocument.getMemento().getHistoryLength());
        assertEquals(0, UMLDocument.getMemento().getRedoHistoryLength());
        assertNotNull(UMLDocument.getInstance().getClass("C"));
        assertNotNull(UMLDocument.getInstance().getClass("B"));
        assertNotNull(UMLDocument.getInstance().getClass("A"));
    }
    @AfterEach
    public void killAnnoyingFiles() {
        
        
        
        //God i hate these files. Die.
        Path file1 = Paths.get("ello.json");
        Path file2 = Paths.get("test.json");
        try{
            Files.deleteIfExists(file1);
            Files.deleteIfExists(file2);
        }
        catch(Exception e){}
    }
}
