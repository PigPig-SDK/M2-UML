package com.unittests;

import org.junit.jupiter.api.Test;
import org.umlproject.Visibility;

import static org.junit.jupiter.api.Assertions.*;

class VisibilityTest {

    @Test
    void stringVisibilityRecognizesNames() {
        assertEquals(Visibility.PUBLIC, Visibility.stringVisibility("public"));
        assertEquals(Visibility.PRIVATE, Visibility.stringVisibility("private"));
        assertEquals(Visibility.PROTECTED, Visibility.stringVisibility("protected"));
        assertEquals(Visibility.PACKAGE, Visibility.stringVisibility("package"));
    }

    @Test
    void stringVisibilityRecognizesSymbols() {
        assertEquals(Visibility.PUBLIC, Visibility.stringVisibility("+"));
        assertEquals(Visibility.PRIVATE, Visibility.stringVisibility("-"));
        assertEquals(Visibility.PROTECTED, Visibility.stringVisibility("#"));
        assertEquals(Visibility.PACKAGE, Visibility.stringVisibility("~"));
    }

    @Test
    void stringVisibilityIsCaseInsensitive() {
        assertEquals(Visibility.PUBLIC, Visibility.stringVisibility("PuBlIc"));
        assertEquals(Visibility.PRIVATE, Visibility.stringVisibility("PrIvAtE"));
        assertEquals(Visibility.PROTECTED, Visibility.stringVisibility("PrOtEcTeD"));
        assertEquals(Visibility.PACKAGE, Visibility.stringVisibility("PaCkAgE"));
    }

    @Test
    void stringVisibilityDefaultsToPrivateOnInvalidInput() {
        assertEquals(Visibility.PRIVATE, Visibility.stringVisibility("invalid"));
        assertEquals(Visibility.PRIVATE, Visibility.stringVisibility(""));
        assertEquals(Visibility.PRIVATE, Visibility.stringVisibility("???"));
    }

    @Test
    void acceptableVisibilityReturnsTrueForValidNames() {
        assertTrue(Visibility.acceptableVisibility("public"));
        assertTrue(Visibility.acceptableVisibility("private"));
        assertTrue(Visibility.acceptableVisibility("protected"));
        assertTrue(Visibility.acceptableVisibility("package"));
    }

    @Test
    void acceptableVisibilityReturnsTrueForValidSymbols() {
        assertTrue(Visibility.acceptableVisibility("+"));
        assertTrue(Visibility.acceptableVisibility("-"));
        assertTrue(Visibility.acceptableVisibility("#"));
        assertTrue(Visibility.acceptableVisibility("~"));
    }

    @Test
    void acceptableVisibilityIsCaseInsensitive() {
        assertTrue(Visibility.acceptableVisibility("PuBlIc"));
        assertTrue(Visibility.acceptableVisibility("PrIvAtE"));
    }

    @Test
    void acceptableVisibilityReturnsFalseForInvalidInput() {
        assertFalse(Visibility.acceptableVisibility("wrong"));
        assertFalse(Visibility.acceptableVisibility(""));
        assertFalse(Visibility.acceptableVisibility("123"));
        assertFalse(Visibility.acceptableVisibility("**"));
    }
}
