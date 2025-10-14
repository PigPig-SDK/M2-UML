package com.unittests;

import org.junit.jupiter.api.*;
import org.umlproject.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for UMLMethod
 * -------------------------------------------------------------
 * This test suite verifies the behavior of the UMLMethod class,
 * including parameter addition, removal, and replacement logic.
 * It also validates constructor input handling, equals/hashCode,
 * and edge-case behaviors such as duplicates or null inputs.
 */
public class UMLMethodTest {

    private UMLParameter paramIntA;
    private UMLParameter paramIntB;
    private UMLParameter paramOther;
    private ArrayList<UMLParameter> baseParams;

    /**
     * Creates reusable test parameters before each test.
     */
    @BeforeEach
    void setUp() {
        paramIntA = new UMLParameter("height", DataType.INT, null);
        paramIntB = new UMLParameter("width", DataType.INT, null);
        paramOther = new UMLParameter("animal", DataType.OTHER, "Dog");

        baseParams = new ArrayList<>();
        baseParams.add(paramIntA);
        baseParams.add(paramIntB);
    }

    // ------------------------------------------------------------------------
    // Constructor Tests
    // ------------------------------------------------------------------------

    @Test
    @DisplayName("Constructor initializes method name and parameter list correctly")
        // Verifies that a valid method name and parameter list are assigned properly.
    void testConstructorValidInputs() {
        UMLMethod method = new UMLMethod("calculateArea", baseParams);
        assertEquals("calculateArea", method.getMethodName());
        assertEquals(2, method.getParameters().size());
    }

    @Test
    @DisplayName("Constructor throws exception for null or empty method name")
        // Ensures IllegalArgumentException is thrown if method name is null or blank.
    void testConstructorInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> new UMLMethod(null, baseParams));
        assertThrows(IllegalArgumentException.class, () -> new UMLMethod("", baseParams));
    }

    @Test
    @DisplayName("Default constructor initializes empty list and blank name")
        // Confirms that default constructor initializes empty parameters and empty methodName string.
    void testDefaultConstructor() {
        UMLMethod method = new UMLMethod();
        assertNotNull(method.getParameters());
        assertEquals(0, method.getParameters().size());
        assertEquals("", method.getMethodName());
    }

    // ------------------------------------------------------------------------
    // addParameter() Tests
    // ------------------------------------------------------------------------

    @Test
    @DisplayName("addParameter successfully adds new unique parameter")
        // Checks that adding a new non-duplicate parameter succeeds and increases list size.
    void testAddParameterSuccess() {
        UMLMethod method = new UMLMethod("addParam", new ArrayList<>(baseParams));
        boolean added = method.addParameter(paramOther);
        assertTrue(added);
        assertEquals(3, method.getParameters().size());
    }

    @Test
    @DisplayName("addParameter prevents duplicate parameters (same name and type)")
        // Ensures duplicate parameter (matching name and DataType) is rejected and list size remains unchanged.
    void testAddParameterDuplicate() {
        UMLMethod method = new UMLMethod("dupTest", new ArrayList<>(baseParams));
        UMLParameter duplicate = new UMLParameter("height", DataType.INT, null);
        boolean added = method.addParameter(duplicate);
        assertFalse(added);
        assertEquals(2, method.getParameters().size());
    }

    // ------------------------------------------------------------------------
    // removeParameter() Tests
    // ------------------------------------------------------------------------

    @Test
    @DisplayName("removeParameter removes matching parameter object")
        // Confirms that removing a parameter object present in the list succeeds.
    void testRemoveParameterSuccess() {
        UMLMethod method = new UMLMethod("removeTest", new ArrayList<>(baseParams));
        boolean removed = method.removeParameter(paramIntA);
        assertTrue(removed);
        assertEquals(1, method.getParameters().size());
    }

    @Test
    @DisplayName("removeParameter returns false if parameter not found")
        // Ensures removeParameter() returns false when parameter does not exist.
    void testRemoveParameterNotFound() {
        UMLMethod method = new UMLMethod("removeFail", new ArrayList<>(baseParams));
        UMLParameter nonExisting = new UMLParameter("depth", DataType.INT, null);
        boolean result = method.removeParameter(nonExisting);
        assertFalse(result);
        assertEquals(2, method.getParameters().size());
    }

    // ------------------------------------------------------------------------
    // changeParameter() Tests
    // ------------------------------------------------------------------------

    @Test
    @DisplayName("changeParameter swaps old parameter object with new one")
        // Ensures an existing parameter is replaced by the new parameter object.
    void testChangeParameterSingleReplacement() {
        UMLMethod method = new UMLMethod("changeOne", new ArrayList<>(baseParams));
        UMLParameter newParam = new UMLParameter("depth", DataType.INT, null);

        boolean changed = method.changeParameter(paramIntB, newParam);
        assertTrue(changed);
        assertEquals("depth", method.getParameters().get(1).getName());
    }

    @Test
    @DisplayName("changeParameter returns false if old parameter not found")
        // Ensures changeParameter() returns false when old parameter does not exist in list.
    void testChangeParameterNotFound() {
        UMLMethod method = new UMLMethod("noChange", new ArrayList<>(baseParams));
        UMLParameter oldParam = new UMLParameter("depth", DataType.INT, null);
        UMLParameter newParam = new UMLParameter("width", DataType.INT, null);

        boolean result = method.changeParameter(oldParam, newParam);
        assertFalse(result);
        assertEquals(2, method.getParameters().size());
    }

    @Test
    @DisplayName("changeParameter (ArrayList version) replaces one with multiple new parameters")
        // Verifies that the overloaded changeParameter() correctly inserts multiple parameters.
    void testChangeParameterListVersion() {
        UMLMethod method = new UMLMethod("multiChange", new ArrayList<>(baseParams));

        ArrayList<UMLParameter> newParams = new ArrayList<>();
        newParams.add(paramOther);
        newParams.add(new UMLParameter("depth", DataType.INT, null));

        boolean result = method.changeParameter(paramIntA, newParams);
        assertTrue(result);
        assertEquals(3, method.getParameters().size());
        assertEquals("animal", method.getParameters().get(0).getName());
    }

    @Test
    @DisplayName("changeParameter (ArrayList version) returns false when old parameter not found")
        // Confirms method correctly returns false and list remains unchanged if old parameter missing.
    void testChangeParameterListVersionNotFound() {
        UMLMethod method = new UMLMethod("multiFail", new ArrayList<>(baseParams));

        ArrayList<UMLParameter> newParams = new ArrayList<>();
        newParams.add(paramOther);

        UMLParameter nonExisting = new UMLParameter("ghost", DataType.INT, null);
        boolean result = method.changeParameter(nonExisting, newParams);

        assertFalse(result);
        assertEquals(2, method.getParameters().size());
    }

    // ------------------------------------------------------------------------
    // Getter / Setter / Utility Tests
    // ------------------------------------------------------------------------

    @Test
    @DisplayName("getMethodName and setMethodName work correctly")
        // Ensures getter and setter for methodName update and return the correct value.
    void testGetSetMethodName() {
        UMLMethod method = new UMLMethod("oldName", new ArrayList<>(baseParams));
        method.setMethodName("newName");
        assertEquals("newName", method.getMethodName());
    }

    @Test
    @DisplayName("setListParameters replaces entire list successfully")
        // Confirms setter overwrites the old parameter list completely.
    void testSetListParameters() {
        UMLMethod method = new UMLMethod("replaceList", new ArrayList<>(baseParams));
        ArrayList<UMLParameter> newList = new ArrayList<>();
        newList.add(paramOther);

        method.setListParameters(newList);
        assertEquals(1, method.getParameters().size());
        assertEquals("animal", method.getParameters().get(0).getName());
    }

    @Test
    @DisplayName("toString() contains method name")
        // Verifies that toString() includes method’s name.
    void testToStringOutput() {
        UMLMethod method = new UMLMethod("printMe", baseParams);
        String output = method.toString();
        assertTrue(output.contains("printMe"));
    }

    // ------------------------------------------------------------------------
    // equals() and hashCode() Tests
    // ------------------------------------------------------------------------

    @Test
    @DisplayName("equals() returns true for methods with same name and parameters")
        // Ensures equality is determined by method name and parameter list.
    void testEqualsTrue() {
        UMLMethod method1 = new UMLMethod("sameMethod", new ArrayList<>(baseParams));
        UMLMethod method2 = new UMLMethod("sameMethod", new ArrayList<>(baseParams));

        assertTrue(method1.equals(method2));
        assertEquals(method1.hashCode(), method2.hashCode());
    }

    @Test
    @DisplayName("equals() returns false for methods with different names or parameters")
        // Confirms unequal methods are correctly identified as not equal.
    void testEqualsFalse() {
        UMLMethod method1 = new UMLMethod("methodOne", new ArrayList<>(baseParams));
        UMLMethod method2 = new UMLMethod("methodTwo", new ArrayList<>(baseParams));
        UMLMethod method3 = new UMLMethod("methodOne", new ArrayList<>());

        assertFalse(method1.equals(method2));
        assertFalse(method1.equals(method3));
    }

    @Test
    @DisplayName("equals() handles null and non-UMLMethod comparisons safely")
        // Ensures equals() does not throw when comparing against null or a non-UMLMethod object.
    void testEqualsEdgeCases() {
        UMLMethod method = new UMLMethod("edgeCase", new ArrayList<>(baseParams));

        assertFalse(method.equals(null));
        assertFalse(method.equals("NotAClass"));
    }
}

