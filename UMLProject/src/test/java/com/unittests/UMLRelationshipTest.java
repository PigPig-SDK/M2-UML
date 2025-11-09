package com.unittests;

import org.junit.jupiter.api.*;
import org.umlproject.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UMLRelationshipTest {
    //NOTE : This class is only to be used for testing!
    static class DummyListener implements UIListener<UMLRelationship> {
        public int timesUpdateCalled = 0;
        @Override public void update(UMLRelationship desiredElement) { timesUpdateCalled++; }
        @Override public void updateLocation(UMLRelationship desiredElement) {}
        @Override public void cleanUp() {}
    }
    
    @Test
    void clone_relationshipType_isDeepCopy() {
        //Arrange
        UMLRelationship umlr = new UMLRelationship("Foo", "Bar", RelationshipType.REALIZATION);
        UMLRelationship uml_clone = umlr.clone();
        //Act
        uml_clone.setRelationshipType(RelationshipType.COMPOSITION);
        //Assert
        assertNotEquals(umlr.getRelationshipType(), uml_clone.getRelationshipType());
    }
    @Test
    void clone_names_isDeepCopy() {
        //Arrange
        UMLRelationship umlr = new UMLRelationship("Foo", "Bar", RelationshipType.REALIZATION);
        UMLRelationship uml_clone = umlr.clone();
        //Act
        uml_clone.setSourceName("BOO!");
        uml_clone.setDestinationName("FAR!");
        //Assert
        assertNotEquals(umlr.getDestinationName(), uml_clone.getDestinationName());
        assertNotEquals(umlr.getSourceName(), uml_clone.getSourceName());
    }
    @Test
    void clone_customNames_isDeepCopy() {
        //Arrange
        UMLRelationship umlr = new UMLRelationship("Foo", "Bar", RelationshipType.OTHER, "Truck");
        UMLRelationship uml_clone = umlr.clone();
        //Act
        uml_clone.setCustomNameType("Fuck");
        //Assert
        assertNotEquals(umlr.getCustomNameType(), uml_clone.getCustomNameType());
    }
    @Test
    void clone_listener_isShallowCopy() {
        //Arrange
        DummyListener dl = new DummyListener();
        UMLRelationship umlr = new UMLRelationship("Foo", "Bar", RelationshipType.OTHER, "Truck");
        umlr.setListener(dl);
        UMLRelationship uml_clone = umlr.clone();
        //Act
        uml_clone.setCustomNameType("Fuck");
        umlr.setDestinationName("newName");
        //Assert
        assertEquals(2, dl.timesUpdateCalled);
    }
}
