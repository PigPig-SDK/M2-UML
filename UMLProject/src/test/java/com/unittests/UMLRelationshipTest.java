package com.unittests;

import org.junit.jupiter.api.Test;
import org.umlproject.*;

import static org.junit.jupiter.api.Assertions.*;

public class UMLRelationshipTest {

    // NOTE : This class is only to be used for testing!
    static class DummyListener implements DiagramElementListener<UMLRelationship> {
        public int timesUpdateCalled = 0;
        @Override public void update(UMLRelationship desiredElement) { timesUpdateCalled++; }
        @Override public void updateTranslation(UMLRelationship desiredElement) {}
        @Override public void cleanUp() {}
    }

    /* ------------------------------------------------------------
     * Constructors & basic getters
     * ------------------------------------------------------------ */

    @Test
    void constructor_basic_setsFields() {
        UMLRelationship rel =
                new UMLRelationship("A", "B", RelationshipType.AGGREGATION);

        assertEquals("A", rel.getSourceName());
        assertEquals("B", rel.getDestinationName());
        assertEquals(RelationshipType.AGGREGATION, rel.getRelationshipType());
        assertNull(rel.getCustomNameType());
    }

    @Test
    void constructor_otherType_requiresCustomName() {
        // valid OTHER with custom name
        UMLRelationship rel =
                new UMLRelationship("A", "B", RelationshipType.OTHER, "CustomRel");

        assertEquals("A", rel.getSourceName());
        assertEquals("B", rel.getDestinationName());
        assertEquals(RelationshipType.OTHER, rel.getRelationshipType());
        assertEquals("CustomRel", rel.getCustomNameType());

        // invalid: OTHER but null custom name
        assertThrows(IllegalArgumentException.class,
                () -> new UMLRelationship("A", "B", RelationshipType.OTHER, null));

        // invalid: OTHER but empty custom name
        assertThrows(IllegalArgumentException.class,
                () -> new UMLRelationship("A", "B", RelationshipType.OTHER, ""));
    }

    /* ------------------------------------------------------------
     * getRelationshipName
     * ------------------------------------------------------------ */

    @Test
    void getRelationshipName_nonOther_returnsEnumName() {
        UMLRelationship rel =
                new UMLRelationship("A", "B", RelationshipType.GENERALIZATION);

        // enum.toString() is used directly
        assertEquals(RelationshipType.GENERALIZATION.toString(), rel.getRelationshipName());
    }

    @Test
    void getRelationshipName_other_returnsCustomName() {
        UMLRelationship rel =
                new UMLRelationship("A", "B", RelationshipType.OTHER, "MyCustom");

        assertEquals("MyCustom", rel.getRelationshipName());
    }

    /* ------------------------------------------------------------
     * Setters
     * ------------------------------------------------------------ */

    @Test
    void setSourceAndDestination_updateFields() {
        UMLRelationship rel =
                new UMLRelationship("A", "B", RelationshipType.AGGREGATION);

        rel.setSourceName("SourceNew");
        rel.setDestinationName("DestNew");

        assertEquals("SourceNew", rel.getSourceName());
        assertEquals("DestNew", rel.getDestinationName());
    }

    @Test
    void setRelationshipType_changesType() {
        UMLRelationship rel =
                new UMLRelationship("A", "B", RelationshipType.AGGREGATION);

        rel.setRelationshipType(RelationshipType.REALIZATION);
        assertEquals(RelationshipType.REALIZATION, rel.getRelationshipType());
    }

    @Test
    void setCustomNameType_setsOtherAndCustom() {
        UMLRelationship rel =
                new UMLRelationship("A", "B", RelationshipType.AGGREGATION);

        rel.setCustomNameType("AssocCustom");

        assertEquals(RelationshipType.OTHER, rel.getRelationshipType());
        assertEquals("AssocCustom", rel.getCustomNameType());
        assertEquals("AssocCustom", rel.getRelationshipName());
    }

    /* ------------------------------------------------------------
     * toString
     * ------------------------------------------------------------ */

    @Test
    void toString_includesSourceDestinationAndType() {
        UMLRelationship rel =
                new UMLRelationship("ClassA", "ClassB", RelationshipType.COMPOSITION);

        String s = rel.toString();
        assertTrue(s.contains("ClassA"));
        assertTrue(s.contains("ClassB"));
        assertTrue(s.contains(RelationshipType.COMPOSITION.toString()));
    }

    @Test
    void toString_usesCustomNameForOther() {
        UMLRelationship rel =
                new UMLRelationship("ClassA", "ClassB", RelationshipType.OTHER, "DependsOn");

        String s = rel.toString();
        assertTrue(s.contains("ClassA"));
        assertTrue(s.contains("ClassB"));
        assertTrue(s.contains("DependsOn"));
        // Should not contain the literal "OTHER"
        assertFalse(s.contains("OTHER"));
    }

    /* ------------------------------------------------------------
     * equals / hashCode
     * (only using non-null customNameType to avoid NPE in equals)
     * ------------------------------------------------------------ */

    @Test
    void equals_sameValues_true() {
        UMLRelationship r1 =
                new UMLRelationship("A", "B", RelationshipType.OTHER, "Custom");
        UMLRelationship r2 =
                new UMLRelationship("A", "B", RelationshipType.OTHER, "Custom");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void equals_differentValues_false() {
        UMLRelationship base =
                new UMLRelationship("A", "B", RelationshipType.OTHER, "Custom");

        UMLRelationship diffSource =
                new UMLRelationship("X", "B", RelationshipType.OTHER, "Custom");
        UMLRelationship diffDest =
                new UMLRelationship("A", "Y", RelationshipType.OTHER, "Custom");
        UMLRelationship diffCustom =
                new UMLRelationship("A", "B", RelationshipType.OTHER, "OtherCustom");

        assertNotEquals(base, diffSource);
        assertNotEquals(base, diffDest);
        assertNotEquals(base, diffCustom);
    }

    /* ------------------------------------------------------------
     * clone – general behavior
     * ------------------------------------------------------------ */

    @Test
    void clone_createsIndependentCopyForState() {
        UMLRelationship original =
                new UMLRelationship("A", "B", RelationshipType.AGGREGATION);

        UMLRelationship copy = original.clone();

        assertNotSame(original, copy);
        assertEquals(original.getSourceName(), copy.getSourceName());
        assertEquals(original.getDestinationName(), copy.getDestinationName());
        assertEquals(original.getRelationshipType(), copy.getRelationshipType());

        // mutate clone and ensure original is unchanged
        copy.setSourceName("NewSource");
        copy.setRelationshipType(RelationshipType.REALIZATION);

        assertEquals("A", original.getSourceName());
        assertEquals(RelationshipType.AGGREGATION, original.getRelationshipType());
    }

    /* ------------------------------------------------------------
     * clone – your original deep-copy style tests
     * ------------------------------------------------------------ */

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
        uml_clone.setCustomNameType("Truck2");
        //Assert
        assertNotEquals(umlr.getCustomNameType(), uml_clone.getCustomNameType());
    }
}
