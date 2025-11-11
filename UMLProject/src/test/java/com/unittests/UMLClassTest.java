package com.unittests;

import org.junit.jupiter.api.*;
import org.umlproject.*;

import java.util.*;
import javafx.geometry.Point2D;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests suite for UMLClassTest
 */
public class UMLClassTest {

    UMLClass clazz;

    @BeforeEach
    void setup() {
        clazz = new UMLClass("Person");
    }

    /* ------------------------------------------------------------
     * Constructor and basic getters
     * ------------------------------------------------------------ */

    @Test
    void constructor_valid_setsNameAndEmptyMaps() {
        assertEquals("Person", clazz.getClassName());
        assertNull(clazz.getFields("any"));
        assertNull(clazz.getMethods("any"));
    }

    @Test
    void constructor_nullOrEmpty_throws() {
        assertThrows(IllegalArgumentException.class, () -> new UMLClass(null));
        assertThrows(IllegalArgumentException.class, () -> new UMLClass(""));
    }
    @Test
    void constructor_locationDynamic_usedLambda() {
        //Arrange
        UMLClass.initializationLocation = (p) -> {return new Point2D(10, 10);};
        //Act
        UMLClass tester = new UMLClass("test");
        //Assert
        assertEquals(tester.getLocation().getX(), 10);
        assertEquals(tester.getLocation().getY(), 10);
        //UNDO for next test...
        UMLClass.initializationLocation = (p) -> {return Point2D.ZERO;};
    }
    /* ------------------------------------------------------------
     * Field operations
     * ------------------------------------------------------------ */

    @Test
    void addField_unique_addsSuccessfully() {
        UMLDataField age = new UMLDataField("age", DataType.INT, Visibility.PRIVATE);
        boolean result = clazz.addField(age);
        assertTrue(result, "Field should be added when unique");
        assertSame(age, clazz.getFields("age"));
    }

    @Test
    void addField_duplicateName_fails() {
        UMLDataField f1 = new UMLDataField("name", DataType.INT, Visibility.PUBLIC);
        UMLDataField f2 = new UMLDataField("name", DataType.INT, Visibility.PRIVATE);
        clazz.addField(f1);
        assertFalse(clazz.addField(f2), "Duplicate field names must be rejected");
    }

    @Test
    void addField_null_returnsFalse() {
        assertFalse(clazz.addField(null));
    }

    @Test
    void removeField_existing_removesAndReturnsTrue() {
        UMLDataField f = new UMLDataField("email", DataType.INT, Visibility.PUBLIC);
        clazz.addField(f);
        assertTrue(clazz.removeField("email"));
        assertNull(clazz.getFields("email"), "Field should be gone after removal");
    }

    @Test
    void removeField_nonexistent_returnsFalse() {
        assertFalse(clazz.removeField("doesNotExist"));
    }

    @Test
    void removeField_nullOrEmpty_returnsFalse() {
        assertFalse(clazz.removeField(null));
        assertFalse(clazz.removeField(""));
    }

    @Test
    void renameField_valid_changesKey() {
        UMLDataField f = new UMLDataField("old", DataType.INT, Visibility.PUBLIC);
        clazz.addField(f);
        assertTrue(clazz.renameField("old", "new"));
        assertNotNull(clazz.getFields("new"));
        assertNull(clazz.getFields("old"));
    }

    @Test
    void renameField_invalidCases_returnFalse() {
        UMLDataField a = new UMLDataField("a", DataType.INT, Visibility.PUBLIC);
        UMLDataField b = new UMLDataField("b", DataType.INT, Visibility.PUBLIC);
        clazz.addField(a);
        clazz.addField(b);

        assertFalse(clazz.renameField("a", "b"), "New name duplicates existing");
        assertFalse(clazz.renameField("missing", "c"), "Old name missing");
        assertFalse(clazz.renameField("a", ""), "New name empty");
    }

    /* ------------------------------------------------------------
     * Method operations
     * ------------------------------------------------------------ */

    @Test
    void addMethod_valid_addsToMap() {
        UMLMethod m = new UMLMethod("greet", new ArrayList<>());
        assertTrue(clazz.addMethod(m));
        assertEquals(1, clazz.getMethods("greet").size());
    }

    @Test
    void addMethod_duplicateOverload_fails() {
        UMLMethod m1 = new UMLMethod("foo", new ArrayList<>());
        UMLMethod m2 = new UMLMethod("foo", new ArrayList<>());
        clazz.addMethod(m1);
        assertFalse(clazz.addMethod(m2));
    }
    
    @Test
    void addMethod_invalidInputs_returnFalse() {
        // case 1: null method
        UMLMethod nullMethod = null;
        assertFalse(clazz.addMethod(nullMethod));

        // case 2: constructor throws for empty name
        assertThrows(IllegalArgumentException.class,
                () -> new UMLMethod("", new ArrayList<>()),
                "Empty method name should throw an exception");

        // case 3: null parameter list
        //This test is not allowed, as an error is thrown if UMLMethod params are null.
        //UMLMethod nullParams = new UMLMethod("f", null);
        //assertFalse(clazz.addMethod(nullParams));

        // case 4: parameter with empty name (invalid parameter)
        UMLParameter badParam = new UMLParameter("", DataType.INT, null);
        UMLMethod badParamName = new UMLMethod("f", new ArrayList<>(List.of(badParam)));
        assertFalse(clazz.addMethod(badParamName));
    }

    @Test
    void removeMethod_invalidNameOrIndex_returnsFalseOrThrows() {
        UMLMethod m = new UMLMethod("x", new ArrayList<>());
        clazz.addMethod(m);

        // invalid method name -> should return false
        assertFalse(clazz.removeMethod("nope", 0), "Unknown name should return false");
        assertFalse(clazz.removeMethod(null, 0), "Null name should return false");

        // invalid index -> should throw IndexOutOfBoundsException
        assertThrows(IndexOutOfBoundsException.class, () -> clazz.removeMethod("x", 5),
                "Out of bounds index should throw IndexOutOfBoundsException");
    }


    /* ------------------------------------------------------------
     * renameMethod
     * ------------------------------------------------------------ */

    @Test
    void renameMethod_success_movesEntry() {
        UMLParameter p = new UMLParameter("a", DataType.INT, null);
        UMLMethod m = new UMLMethod("oldName", new ArrayList<>(List.of(p)));
        clazz.addMethod(m);

        ArrayList<UMLParameter> params = new ArrayList<>(List.of(p));
        assertTrue(clazz.renameMethod("oldName", "newName", params));
        assertNotNull(clazz.getMethods("newName"));
        assertNull(clazz.getMethods("oldName"));
    }

    @Test
    void renameMethod_invalidScenarios_returnFalse() {
        UMLParameter p = new UMLParameter("a", DataType.INT, null);
        UMLMethod m = new UMLMethod("sum", new ArrayList<>(List.of(p)));
        clazz.addMethod(m);

        assertFalse(clazz.renameMethod(null, "add", new ArrayList<>(List.of(p))));
        assertFalse(clazz.renameMethod("sum", "", new ArrayList<>(List.of(p))));
        assertFalse(clazz.renameMethod("missing", "new", new ArrayList<>(List.of(p))));
        assertFalse(clazz.renameMethod("sum", "add", new ArrayList<>()));
    }

    /* ------------------------------------------------------------
     * addParameter / removeParameter / changeParameter
     * ------------------------------------------------------------ */

    @Test
    void addParameter_valid_addsSuccessfully() {
        UMLParameter x = new UMLParameter("x", DataType.INT, null);
        UMLMethod m = new UMLMethod("calc", new ArrayList<>(List.of(x)));
        clazz.addMethod(m);

        UMLParameter y = new UMLParameter("y", DataType.FLOAT, null);
        assertTrue(clazz.addParameter("calc", new ArrayList<>(List.of(x)), y));
    }

    @Test
    void addParameter_invalidInputs_returnFalse() {
        UMLParameter x = new UMLParameter("x", DataType.INT, null);
        UMLMethod m = new UMLMethod("calc", new ArrayList<>(List.of(x)));
        clazz.addMethod(m);
        UMLParameter dup = new UMLParameter("x", DataType.INT, null);

        assertFalse(clazz.addParameter("calc", new ArrayList<>(List.of(x)), dup));
        assertFalse(clazz.addParameter(null, new ArrayList<>(List.of(x)), dup));
        assertFalse(clazz.addParameter("calc", null, dup));
        assertFalse(clazz.addParameter("calc", new ArrayList<>(List.of(x)), null));
    }

    @Test
    void removeParameter_valid_removes() {
        UMLParameter x = new UMLParameter("x", DataType.INT, null);
        UMLMethod m = new UMLMethod("calc", new ArrayList<>(List.of(x)));
        clazz.addMethod(m);
        assertTrue(clazz.removeParameter("calc", new ArrayList<>(List.of(x)), x));
    }

    @Test
    void removeParameter_invalidInputs_returnFalse() {
        UMLParameter x = new UMLParameter("x", DataType.INT, null);
        UMLMethod m = new UMLMethod("calc", new ArrayList<>(List.of(x)));
        clazz.addMethod(m);
        UMLParameter wrong = new UMLParameter("z", DataType.INT, null);

        assertFalse(clazz.removeParameter("calc", new ArrayList<>(List.of(x)), wrong));
        assertFalse(clazz.removeParameter(null, new ArrayList<>(List.of(x)), wrong));
        assertFalse(clazz.removeParameter("calc", null, wrong));
    }

    @Test
    void changeParameter_replaceWholeList_success() {
        UMLParameter old = new UMLParameter("a", DataType.INT, null);
        UMLMethod m = new UMLMethod("f", new ArrayList<>(List.of(old)));
        clazz.addMethod(m);

        ArrayList<UMLParameter> oldList = new ArrayList<>(List.of(old));
        ArrayList<UMLParameter> newList = new ArrayList<>(List.of(new UMLParameter("b", DataType.INT, null)));
        assertTrue(clazz.changeParameter("f", oldList, newList));
    }

    @Test
    void changeParameter_invalidInputs_returnFalse() {
        assertFalse(clazz.changeParameter(null, new ArrayList<>(), new ArrayList<>()));
        assertFalse(clazz.changeParameter("x", null, new ArrayList<>()));
        assertFalse(clazz.changeParameter("x", new ArrayList<>(), null));
    }

    /* ------------------------------------------------------------
     * equals / hashCode / toString
     * ------------------------------------------------------------ */

    @Test
    void equals_sameName_true() {
        UMLClass same = new UMLClass("Person");
        assertEquals(clazz, same);
        assertEquals(clazz.hashCode(), same.hashCode());
    }

    @Test
    void equals_differentName_false() {
        UMLClass other = new UMLClass("Animal");
        assertNotEquals(clazz, other);
    }

    @Test
    void toString_containsClassName() {
        assertTrue(clazz.toString().contains("Person"));
    }
    
    /* ------------------------------------------------------------
     * Cloning
     * ------------------------------------------------------------ */
    @Test
    void clone_name_isDeepCopy() {
        //Arrange
        UMLClass tester = new UMLClass("test");
        //Act
        UMLClass testerClone = tester.clone();
        testerClone.setClassName("test2");
        //Assert
        assertEquals("test", tester.getClassName());
    }
    @Test
    void clone_methodRemove_isDeepCopy() {
        //Arrange
        String methodName = "foo";
        
        UMLClass tester = new UMLClass("test");
        ArrayList<UMLParameter> parameters  = new ArrayList<>();
        parameters.add(new UMLParameter("bar", DataType.DOUBLE, null));
        tester.addMethod(new UMLMethod(methodName,parameters));
        
        //Act
        UMLClass testerClone = tester.clone();
        boolean isRemoved = testerClone.removeMethod(methodName, 0);//Remove the 0th item.
        //Assert
        assertTrue(isRemoved);
        assertTrue(tester.getMethods(methodName) != null);//Was not removed from the base object.
    }
    @Test
    void clone_methodAdjust_isDeepCopy() {
        //Arrange
        String methodName = "foo";
        
        UMLClass tester = new UMLClass("test");
        ArrayList<UMLParameter> parameters  = new ArrayList<>();
        parameters.add(new UMLParameter("bar", DataType.DOUBLE, null));
        tester.addMethod(new UMLMethod(methodName,parameters));
        
        //Act
        UMLClass testerClone = tester.clone();
        var list = testerClone.getMethods(methodName);
        list.get(0).setListParameters(null);
        //Assert
        assertNotEquals(tester.getMethods(methodName), testerClone.getMethods(methodName));
    }
    @Test
    void clone_datafield_isDeepCopy() {
        //Arrange
        String dataFieldNameCustomType = "foo";
        String dataFieldName = "bar";

        UMLClass tester = new UMLClass("test");
        tester.addField(new UMLDataField(dataFieldNameCustomType, "TestType"));
        tester.addField(new UMLDataField(dataFieldName, DataType.BOOLEAN));
        //Act
        UMLClass testerClone = tester.clone();
        testerClone.getFields(dataFieldNameCustomType).setCustomNameType("Edited");
        testerClone.getFields(dataFieldName).setDataType(DataType.BYTE);

        //Assert
        assertNotEquals(tester.getFields(dataFieldNameCustomType).getCustomNameType(), testerClone.getFields(dataFieldNameCustomType).getCustomNameType());
        assertNotEquals(tester.getFields(dataFieldName).getDataType(), testerClone.getFields(dataFieldName).getDataType());
    }
    @Test
    void clone_datafieldList_isDeepCopy() {
        //Arrange
        String dataFieldName = "foo";

        UMLClass tester = new UMLClass("test");
        tester.addField(new UMLDataField(dataFieldName, "TestType"));
        //Act
        UMLClass testerClone = tester.clone();
        testerClone.removeField(dataFieldName);
        //Assert
        assertNotEquals(tester.getFieldsAll().size(),  testerClone.getFieldsAll().size());
        assertNull(testerClone.getFields(dataFieldName));
        assertNotNull(tester.getFields(dataFieldName));
    }

    //NOTE : This class is only to be used for testing!
    static class DummyListener implements DiagramElementListener<UMLClass> {
        public int timesUpdateCalled = 0;

        @Override public void update(UMLClass desiredElement) { timesUpdateCalled++; }
        @Override public void updateLocation(UMLClass desiredElement) {}
        @Override public void cleanUp() {}
    }
    
    @Test
    void clone_UIListner_isShallowCopy() {
        
        //Arrange
        
        //Mock : used for testing.
        DiagramElementListener<UMLClass> dummyListner = new DummyListener();
        UMLClass tester = new UMLClass("test");
        tester.setListener(dummyListner);
        
        //Act
        UMLClass testerClone = tester.clone();
        testerClone.updateListener(false);//Calls update...
        
        //Assert
        assertEquals(1, ((DummyListener)tester.getListener()).timesUpdateCalled);
        assertEquals(tester.getListener(), testerClone.getListener());//How get the value of timesUpdateCalled?
    }
    
}
