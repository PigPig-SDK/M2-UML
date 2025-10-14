package com.unittests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.umlproject.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Suite for UMLDataField
 * ---------------------------------------------------------------------
 * This suite verifies that UMLDataField behaves correctly across:
 * - all constructor overloads,
 * - setter and getter behavior,
 * - exception handling for invalid usage,
 * - equality, hashing, and toString() consistency,
 * - and edge case handling for null or invalid inputs.
 *
 * Each test includes comments explaining its purpose and rationale.
 */
public class UMLDataFieldTest {

    // ------------------------------------------------------------------
    // Constructor Tests
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Constructor: valid primitive type initializes all fields correctly")
    void testConstructorNameAndDataType() {
        // Creates a field with a primitive DataType (INT)
        UMLDataField field = new UMLDataField("age", DataType.INT);

        // Checks all fields were initialized as expected
        assertEquals("age", field.getName());
        assertEquals(DataType.INT, field.getDataType());
        assertEquals(Visibility.PRIVATE, field.getVisibility(), "Default visibility should be PRIVATE");
        assertEquals("INT", field.getTypeAsString());
    }

    @Test
    @DisplayName("Constructor: valid custom type assigns DataType.OTHER automatically")
    void testConstructorNameAndCustomType() {
        // Uses constructor for custom type, automatically sets DataType = OTHER
        UMLDataField field = new UMLDataField("pet", "Dog");

        // Ensures OTHER type with custom name assigned properly
        assertEquals("pet", field.getName());
        assertEquals(DataType.OTHER, field.getDataType());
        assertEquals("Dog", field.getTypeAsString());
        assertEquals(Visibility.PRIVATE, field.getVisibility());
    }

    @Test
    @DisplayName("Constructor: name, DataType, and Visibility work correctly for primitive types")
    void testConstructorWithVisibility() {
        // Explicitly sets visibility modifier in constructor
        UMLDataField field = new UMLDataField("weight", DataType.FLOAT, Visibility.PUBLIC);

        // Confirms visibility and type were stored correctly
        assertEquals("weight", field.getName());
        assertEquals(DataType.FLOAT, field.getDataType());
        assertEquals(Visibility.PUBLIC, field.getVisibility());
        assertEquals("FLOAT", field.getTypeAsString());
    }

    @Test
    @DisplayName("Constructor: throws IllegalArgumentException when DataType == OTHER without custom name")
    void testConstructorThrowsForOtherType() {
        // Guards against incorrect usage that would otherwise cause inconsistent state
        assertThrows(IllegalArgumentException.class, () -> new UMLDataField("fail", DataType.OTHER));
        assertThrows(IllegalArgumentException.class, () -> new UMLDataField("fail", DataType.OTHER, Visibility.PUBLIC));
    }

    // ------------------------------------------------------------------
    // Setter Tests
    // ------------------------------------------------------------------

    @Test
    @DisplayName("setDataType: updates DataType for valid primitive values")
    void testSetDataTypeValid() {
        // Changes DataType from INT → FLOAT, which is valid
        UMLDataField field = new UMLDataField("test", DataType.INT);
        field.setDataType(DataType.FLOAT);

        // Ensures both internal and string representations match new type
        assertEquals(DataType.FLOAT, field.getDataType());
        assertEquals("FLOAT", field.getTypeAsString());
    }

    @Test
    @DisplayName("setDataType: throws exception for OTHER type (no custom name provided)")
    void testSetDataTypeThrowsForOther() {
        // Prevents user from switching to OTHER without specifying a custom type name
        UMLDataField field = new UMLDataField("field", DataType.INT);
        assertThrows(IllegalArgumentException.class, () -> field.setDataType(DataType.OTHER));
    }

    @Test
    @DisplayName("setVisibility: properly updates the visibility modifier")
    void testSetVisibility() {
        // Changes visibility modifier to PROTECTED
        UMLDataField field = new UMLDataField("visible", DataType.BOOLEAN);
        field.setVisibility(Visibility.PROTECTED);
        assertEquals(Visibility.PROTECTED, field.getVisibility());
    }

    @Test
    @DisplayName("setName: ignores null or empty strings but accepts valid names")
    void testSetNameEdgeCases() {
        // Prevents null or empty names that could break UML display
        UMLDataField field = new UMLDataField("field", DataType.INT);

        field.setName(null);
        assertEquals("field", field.getName(), "Null name should not overwrite existing value");

        field.setName("");
        assertEquals("field", field.getName(), "Empty name should not overwrite existing value");

        field.setName("updated");
        assertEquals("updated", field.getName(), "Valid name should be updated successfully");
    }

    @Test
    @DisplayName("setCustomNameType: only updates if DataType == OTHER")
    void testSetCustomNameTypeBehavior() {
        // Works when DataType == OTHER
        UMLDataField field1 = new UMLDataField("custom", "Car");
        field1.setCustomNameType("Truck");
        assertEquals("Truck", field1.getTypeAsString());

        // Should have no effect if DataType != OTHER
        UMLDataField field2 = new UMLDataField("basic", DataType.INT);
        field2.setCustomNameType("Ignored");
        assertEquals("INT", field2.getTypeAsString());
    }

    // ------------------------------------------------------------------
    // Getter and Utility Tests
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getTypeAsString: returns readable representation for all types")
    void testGetTypeAsString() {
        // Confirms primitive and custom types are represented properly
        UMLDataField primitive = new UMLDataField("height", DataType.DOUBLE);
        UMLDataField custom = new UMLDataField("species", "Feline");

        assertEquals("DOUBLE", primitive.getTypeAsString());
        assertEquals("Feline", custom.getTypeAsString());
    }

    @Test
    @DisplayName("toString: includes name, dataType, and visibility fields")
    void testToStringContent() {
        // Ensures toString() generates a readable and complete representation
        UMLDataField field = new UMLDataField("test", DataType.FLOAT);
        String output = field.toString();

        assertTrue(output.contains("name:"), "Should include name field");
        assertTrue(output.contains("dataType:"), "Should include dataType field");
        assertTrue(output.contains("Visibility:"), "Should include visibility field");
    }

    // ------------------------------------------------------------------
    // Equality and Hashing
    // ------------------------------------------------------------------

    @Test
    @DisplayName("equals: true when names match, regardless of data type")
    void testEqualsByName() {
        // UMLDataField equality is defined solely by the name
        UMLDataField f1 = new UMLDataField("x", DataType.INT);
        UMLDataField f2 = new UMLDataField("x", DataType.FLOAT);
        UMLDataField f3 = new UMLDataField("y", DataType.INT);

        assertTrue(f1.equals(f2), "Fields with same name should be equal");
        assertFalse(f1.equals(f3), "Different names should not be equal");
    }

    @Test
    @DisplayName("equals: handles nulls and different object types safely")
    void testEqualsEdgeCases() {
        // Ensures defensive coding (no NullPointerExceptions)
        UMLDataField f1 = new UMLDataField("z", DataType.INT);

        assertFalse(f1.equals(null), "Comparing to null should return false");
        assertFalse(f1.equals("notAField"), "Comparing to non-UMLDataField should return false");
    }

    @Test
    @DisplayName("hashCode: consistent with name equality")
    void testHashCodeConsistency() {
        // Ensures hashCode is consistent with equals() definition
        UMLDataField f1 = new UMLDataField("id", DataType.INT);
        UMLDataField f2 = new UMLDataField("id", DataType.FLOAT);

        assertEquals(f1.hashCode(), f2.hashCode(), "Equal names should have same hash code");
    }

    // ------------------------------------------------------------------
    // Edge Case Robustness
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Handles null name safely in toString and equals")
    void testNullNameSafety() {
        // Ensures class will not crash even if name becomes null
        UMLDataField field = new UMLDataField("safe", DataType.INT);
        field.setName(null);

        // toString() should still work
        assertDoesNotThrow(field::toString);

        // equals() with another valid object should return false safely
        UMLDataField other = new UMLDataField("other", DataType.INT);
        assertFalse(field.equals(other));
    }

    @Test
    @DisplayName("Handles null visibility safely without throwing errors")
    void testNullVisibilitySafety() {
        // Ensures visibility can be null (temporarily unset) without breaking toString()
        UMLDataField field = new UMLDataField("nullVis", DataType.INT);
        field.setVisibility(null);

        assertDoesNotThrow(field::toString);
        assertNull(field.getVisibility());
    }
}
