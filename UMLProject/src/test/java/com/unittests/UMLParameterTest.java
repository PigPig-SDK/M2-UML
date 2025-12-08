package com.unittests;

import org.junit.jupiter.api.Test;
import org.umlproject.DataType;
import org.umlproject.UMLParameter;

import static org.junit.jupiter.api.Assertions.*;

class UMLParameterTest {

    @Test
    void constructorCreatesPrimitiveParameter() {
        UMLParameter p = new UMLParameter("x", DataType.INT, null);

        assertEquals("x", p.getName());
        assertEquals(DataType.INT, p.getDataType());
        assertNull(p.getCustomNameType());
    }

    @Test
    void constructorCreatesCustomParameterWhenOtherTypeProvided() {
        UMLParameter p = new UMLParameter("obj", DataType.OTHER, "CustomClass");

        assertEquals("obj", p.getName());
        assertEquals(DataType.OTHER, p.getDataType());
        assertEquals("CustomClass", p.getCustomNameType());
    }

    @Test
    void constructorThrowsExceptionWhenOtherWithoutCustomName() {
        assertThrows(IllegalArgumentException.class,
                () -> new UMLParameter("obj", DataType.OTHER, null));

        assertThrows(IllegalArgumentException.class,
                () -> new UMLParameter("obj", DataType.OTHER, ""));
    }

    @Test
    void defaultConstructorInitializesFieldsToNull() {
        UMLParameter p = new UMLParameter();

        assertNull(p.getName());
        assertNull(p.getDataType());
        assertNull(p.getCustomNameType());
    }

    @Test
    void toStringWorksForPrimitiveType() {
        UMLParameter p = new UMLParameter("count", DataType.INT, null);
        assertEquals("INT count", p.toString());
    }

    @Test
    void toStringWorksForCustomType() {
        UMLParameter p = new UMLParameter("user", DataType.OTHER, "UserClass");
        assertEquals("UserClass user", p.toString());
    }

    @Test
    void equalsReturnsTrueForSameObject() {
        UMLParameter p = new UMLParameter("a", DataType.INT, null);
        assertEquals(p, p);
    }

    @Test
    void equalsReturnsFalseForDifferentType() {
        UMLParameter p = new UMLParameter("a", DataType.INT, null);
        assertNotEquals(p, "not a parameter");
    }

    @Test
    void equalsReturnsTrueForMatchingFields() {
        UMLParameter p1 = new UMLParameter("a", DataType.FLOAT, null);
        UMLParameter p2 = new UMLParameter("a", DataType.FLOAT, null);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode(),
                "Equal objects must have equal hashcodes");
    }

    @Test
    void equalsReturnsFalseForDifferentFields() {
        UMLParameter p1 = new UMLParameter("a", DataType.INT, null);
        UMLParameter p2 = new UMLParameter("b", DataType.INT, null);

        assertNotEquals(p1, p2);
    }

    @Test
    void cloneCreatesDeepCopy() throws CloneNotSupportedException {
        UMLParameter p1 = new UMLParameter("num", DataType.INT, null);
        UMLParameter p2 = p1.clone();

        assertNotSame(p1, p2);
        assertEquals(p1, p2);
    }

    @Test
    void cloneWorksForCustomType() throws CloneNotSupportedException {
        UMLParameter p1 = new UMLParameter("thing", DataType.OTHER, "Widget");
        UMLParameter p2 = p1.clone();

        assertNotSame(p1, p2);
        assertEquals(p1, p2);
        assertEquals("Widget", p2.getCustomNameType());
    }
}
