package com.unittests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.umlproject.*;

import static org.junit.jupiter.api.Assertions.*;

class UndoRedoManagerTests {

    @BeforeEach
    void setup() {
        // Fresh document & memento for each test
        UMLDocument.resetInstance(true);

        // Clean out any existing listeners to avoid interference
        UMLDiagramElement.globalListeners.clear();
        UMLDocument.documentListners.clear();

        // Register the UndoRedoManager as a listener
        UndoRedoManager.setupListener();
    }

    /**
     * Helper to grab the UndoRedoManager instance that setupListener() registered.
     */
    private UndoRedoManager getManagerFromGlobalListeners() {
        for (DiagramElementListener listener : UMLDiagramElement.globalListeners) {
            if (listener instanceof UndoRedoManager manager) {
                return manager;
            }
        }
        return null;
    }

    @Test
    void setupListenerRegistersUndoRedoManager() {
        UndoRedoManager manager = getManagerFromGlobalListeners();
        assertNotNull(manager, "UndoRedoManager should be registered as a global listener");
        assertTrue(UMLDocument.documentListners.contains(manager),
                "UndoRedoManager should also be registered as a document listener");

        // getInstance() should return the same instance set up by setupListener()
        assertSame(manager, manager.getInstance(),
                "getInstance() should return the same UndoRedoManager singleton");
    }

    @Test
    void normalDocumentUpdatesSaveMementoState() {
        int before = UMLDocument.getMemento().getHistoryLength();

        // This will fire onClassAdded(), which UndoRedoManager listens to
        UMLDocument.getInstance().addClass("A");

        int after = UMLDocument.getMemento().getHistoryLength();
        assertEquals(before + 1, after,
                "Adding a class in NORMAL state should save a new memento state");
    }

    @Test
    void updatesDuringInvalidStateDoNotSaveMementoState() {
        int before = UMLDocument.getMemento().getHistoryLength();

        // FILE_LOADING is one of the invalidDocumentStates in UndoRedoManager
        UMLDocument.executeActionUnderState(DocumentState.FILE_LOADING, () -> {
            UMLDocument.getInstance().addClass("B");
            return null;
        });

        int after = UMLDocument.getMemento().getHistoryLength();
        assertEquals(before, after,
                "No new memento state should be saved while in FILE_LOADING state");
    }

    @Test
    void updateLocationInNormalStateSavesMemento() {
        UndoRedoManager manager = getManagerFromGlobalListeners();
        assertNotNull(manager, "UndoRedoManager should be registered");

        int before = UMLDocument.getMemento().getHistoryLength();

        // Directly call the listener method
        manager.updateLocation(new Object());

        int after = UMLDocument.getMemento().getHistoryLength();
        assertEquals(before + 1, after,
                "updateLocation() in NORMAL state should save a new memento state");
    }

    @Test
    void updateLocationDuringSilentMovementDoesNotSaveMemento() {
        UndoRedoManager manager = getManagerFromGlobalListeners();
        assertNotNull(manager, "UndoRedoManager should be registered");

        int before = UMLDocument.getMemento().getHistoryLength();

        // SILENT_MOVEMENT is handled specially in updateLocation()
        UMLDocument.executeActionUnderState(DocumentState.SILENT_MOVEMENT, () -> {
            manager.updateLocation(new Object());
            return null;
        });

        int after = UMLDocument.getMemento().getHistoryLength();
        assertEquals(before, after,
                "updateLocation() during SILENT_MOVEMENT should not save a new memento state");
    }
}
