package com.unittests;

import org.junit.jupiter.api.*;
import org.umlproject.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UMLRelationshipTest {
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
}
