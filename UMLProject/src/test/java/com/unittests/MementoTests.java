package com.unittests;

import org.junit.jupiter.api.Test;
import org.umlproject.*;

import static org.junit.jupiter.api.Assertions.*;
import org.umlproject.UI.GuiFileBrowser;
public class MementoTests {
    
    private class ClonableClass implements Cloneable{
        public int counter;
        public ClonableClass(int counterInitial) { this.counter = counterInitial;}
        @Override
        protected ClonableClass clone() throws CloneNotSupportedException {
            return new ClonableClass(this.counter);//Value type. Is deep copy.
        }
    }
    
    private class TestListener implements MementoListener<ClonableClass>
    {
        public int updateCounter = 0;
        @Override
        public void update(Memento<ClonableClass> memento) {
            updateCounter++;
        }
        
    }
    
    @Test
    void clonable_isDeepCopy_Success() throws CloneNotSupportedException {
        //Arrange
        ClonableClass cloneClass = new ClonableClass(0);
        ClonableClass cloneClassTwo = cloneClass.clone();
        //Act
        cloneClassTwo.counter++;
        //Assert
        assertNotEquals(cloneClass.counter, cloneClassTwo.counter);
    }
    
    @Test
    void undo_saveState_returnsToFirstState() {
        //Arrange
        ClonableClass cloneClass = new ClonableClass(0);
        Memento<ClonableClass> memento = new Memento<ClonableClass>(cloneClass);
        //Act
        memento.getInstance().counter++;
        memento.saveState();//Save the current state
        memento.undo();//Retreat to the initial state.
        //Assert
        assertEquals(0, memento.getInstance().counter);
    }
    
    @Test
    void undo_undoInitialState_returnsToFirstState() {
        //Arrange
        ClonableClass cloneClass = new ClonableClass(0);
        Memento<ClonableClass> memento = new Memento<ClonableClass>(cloneClass);
        //Act
        memento.getInstance().counter++;
        memento.undo();//Retreat to the initial state, because no state was saved.
        //Assert
        assertEquals(0, memento.getInstance().counter);
        assertEquals(1, memento.getHistoryLength());//Maintains that one state always exists.
    }
    
    @Test
    void undo_saveUndoUndo_returnsToFirstState() {
        //Arrange
        ClonableClass cloneClass = new ClonableClass(0);
        Memento<ClonableClass> memento = new Memento<ClonableClass>(cloneClass);
        //Act
        memento.getInstance().counter++;
        memento.saveState();//Save the current state
        memento.undo();//Retreat
        memento.undo();//Retreat to the initial state, because no state was saved.
        //Assert
        assertEquals(0, memento.getInstance().counter);
        assertEquals(1, memento.getHistoryLength());//Maintains that one state always exists.
    }
    @Test
    void saveState_saveOnce_containsState() {
        //Arrange
        ClonableClass cloneClass = new ClonableClass(0);
        Memento<ClonableClass> memento = new Memento<ClonableClass>(cloneClass);
        //Act
        memento.getInstance().counter++;
        memento.saveState();//Save the current state
        //Assert
        assertEquals(1, memento.getInstance().counter);
        assertEquals(1, memento.getHistoryLength());//Maintains that one state always exists.
    }
    @Test
    void saveState_saveMultipleTimes_containsState() {
        //Arrange
        ClonableClass cloneClass = new ClonableClass(0);
        Memento<ClonableClass> memento = new Memento<ClonableClass>(cloneClass);
        //Act
        memento.getInstance().counter++;
        memento.saveState();//1
        memento.getInstance().counter++;
        memento.saveState();//2
        //Assert
        assertEquals(3, memento.getHistoryLength());//3 states should be saved. Initial, State 1, State 2
        assertEquals(2, memento.getInstance().counter);
        
        memento.undo();
        assertEquals(2, memento.getHistoryLength());
        assertEquals(1, memento.getInstance().counter);
        
        memento.undo();//Back to zero...
        assertEquals(1, memento.getHistoryLength());
        assertEquals(0, memento.getInstance().counter);
        
        memento.undo();//In the negative we maintain our 0'th state.
        assertEquals(1, memento.getHistoryLength());
        assertEquals(0, memento.getInstance().counter);
    }
    @Test
    void redo_oneTime_returnsToState() {
        //Arrange
        ClonableClass cloneClass = new ClonableClass(0);
        Memento<ClonableClass> memento = new Memento<ClonableClass>(cloneClass);
        //Act
        memento.getInstance().counter++;
        memento.saveState();//1
        //Assert
        assertEquals(2, memento.getHistoryLength());//2 states should be saved. Initial, State 1
        
        memento.undo();
        assertEquals(1, memento.getHistoryLength());
        assertEquals(1, memento.getRedoHistoryLength());
        assertEquals(0, memento.getInstance().counter);

        memento.redo();
        assertEquals(2, memento.getHistoryLength());
        assertEquals(0, memento.getRedoHistoryLength());
        assertEquals(1, memento.getInstance().counter);
    }
    @Test
    void redo_noStack_stopsAction() {
        //Arrange
        ClonableClass cloneClass = new ClonableClass(0);
        Memento<ClonableClass> memento = new Memento<ClonableClass>(cloneClass);
        //Act
        memento.redo();//Nothing should happen...
        
        //Assert
        assertEquals(1, memento.getHistoryLength());
        assertEquals(0, memento.getRedoHistoryLength());
    }
    @Test
    void clearHistory_clearsAllHistory_success() {
        //Arrange
        ClonableClass cloneClass = new ClonableClass(0);
        Memento<ClonableClass> memento = new Memento<ClonableClass>(cloneClass);
        //Act
        memento.saveState();
        memento.saveState();
        memento.saveState();
        memento.saveState();
        memento.undo();
        //Assert
        assertEquals(4, memento.getHistoryLength());
        assertEquals(1, memento.getRedoHistoryLength());
        
        memento.resetHistory(memento.getInstance());//Reset the current instance as 'king'
        assertEquals(1, memento.getHistoryLength());
        assertEquals(0, memento.getRedoHistoryLength());
    }
    @Test
    void listener_saveState_callsback() {
        //Arrange
        ClonableClass cloneClass = new ClonableClass(0);
        Memento<ClonableClass> memento = new Memento<ClonableClass>(cloneClass);
        TestListener testListener = new TestListener();
        memento.addListener(testListener);
        //Act
        memento.saveState();
        //Assert
        assertEquals(1, testListener.updateCounter);
    }
    @Test
    void listener_undo_callsback() {
        //Arrange
        ClonableClass cloneClass = new ClonableClass(0);
        Memento<ClonableClass> memento = new Memento<ClonableClass>(cloneClass);
        TestListener testListener = new TestListener();
        memento.addListener(testListener);
        //Act
        memento.saveState();
        memento.undo();
        //Assert
        assertEquals(2, testListener.updateCounter);
    }
    @Test
    void listener_redo_callsback() {
        //Arrange
        ClonableClass cloneClass = new ClonableClass(0);
        Memento<ClonableClass> memento = new Memento<ClonableClass>(cloneClass);
        TestListener testListener = new TestListener();
        memento.addListener(testListener);
        //Act
        memento.saveState();
        memento.undo();
        memento.redo();
        //Assert
        assertEquals(3, testListener.updateCounter);
    }
    @Test
    void listener_clearHistory_callsback() {
        //Arrange
        ClonableClass cloneClass = new ClonableClass(0);
        Memento<ClonableClass> memento = new Memento<ClonableClass>(cloneClass);
        TestListener testListener = new TestListener();
        memento.addListener(testListener);
        //Act
        memento.resetHistory(cloneClass);
        //Assert
        assertEquals(1, testListener.updateCounter);
    }
}
