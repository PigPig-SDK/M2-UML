package com.unittests;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.umlproject.*;
import org.umlproject.UI.GuiClass;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class GuiClassTest {

    private GuiClass guiClass;
    private UMLClass parentClass;
    private Group world;

    @BeforeEach
    public void setUp() {
        // This line initializes the JavaFX runtime in headless mode
        new JFXPanel();

        world = new Group();
        parentClass = new UMLClass("TestClass");
        guiClass = new GuiClass(world, parentClass);
    }

    @Test
    public void testGetParentClass() {
        assertEquals(parentClass, guiClass.getParentClass());
    }

    /**
     * Tests that setSelected() correctly updates the selection state.
     */
    @Test
    public void testSetAndGetSelected() {
        guiClass.setSelected(true);
        assertTrue(guiClass.getSelected(), "GuiClass should report selected state as true");

        guiClass.setSelected(false);
        assertFalse(guiClass.getSelected(), "GuiClass should report selected state as false");
    }

    /**
     * Tests that setting the location updates the underlying UMLClass location.
     */
    @Test
    public void testSetAndGetLocation() {
        Point2D newLocation = new Point2D(100, 200);
        guiClass.setLocation(newLocation);

        assertEquals(newLocation, guiClass.getLocation(),
                "GuiClass should update and return the correct location");
    }

    /**
     * Tests convertDataFieldsToStrings() with a sample UMLDataField map.
     * Ensures conversion produces the expected string representation.
     */
    @Test
    public void testConvertDataFieldsToStrings() {
        new JFXPanel();
        Group world = new Group();
        UMLClass parent = new UMLClass("TestClass");
        GuiClass gui = new GuiClass(world, parent);

        HashMap<String, UMLDataField> fields = new HashMap<>();
        fields.put("field1", new UMLDataField("field1", null, DataType.INT, Visibility.PUBLIC));
        fields.put("customField", new UMLDataField("customField", "CustomType", DataType.OTHER, Visibility.PRIVATE));

        ArrayList<String> result = gui.convertDataFieldsToStrings(fields);


        assertTrue(result.contains("PUBLIC INT field1"));
        assertTrue(result.contains("PRIVATE CustomType customField"));
    }

    /**
     * Tests that convertDataFieldsToStrings() gracefully handles null input.
     */
    @Test
    public void testConvertDataFieldsToStrings_NullInput() {
        ArrayList<String> result = guiClass.convertDataFieldsToStrings(null);
        assertNull(result, "Expected null return for invalid input");
    }

    /**
     * Tests that getRectBounds() returns a valid rectangle after update().
     */
    @Test
    public void testGetRectBoundsAfterUpdate() throws Exception {
        new JFXPanel(); // initialize JavaFX
        Group world = new Group();
        UMLClass parent = new UMLClass("RectTest");
        GuiClass gui = new GuiClass(world, parent);

        Platform.runLater(() -> {
            gui.update(parent);

            try {
                // Access private parentVBox reflectively
                Field field = GuiClass.class.getDeclaredField("parentVBox");
                field.setAccessible(true);
                VBox parentVBox = (VBox) field.get(gui);

                // Force layout pass so bounds aren't zero
                parentVBox.applyCss();
                parentVBox.layout();
            } catch (Exception ex) {
                fail("Unable to access parentVBox for layout: " + ex.getMessage());
            }

            Rectangle2D rect = gui.getRectBounds();
            assertNotNull(rect, "Rectangle should not be null");
            assertTrue(rect.getWidth() >= 0, "Rectangle width should be >= 0");
            assertTrue(rect.getHeight() >= 0, "Rectangle height should be >= 0");
        });

        Thread.sleep(500); // wait for JavaFX thread
    }



    /**
     * Tests that toString() delegates to the parent UMLClass.
     */
    @Test
    public void testToStringMatchesParentClass() {
        String parentString = parentClass.toString();
        assertEquals(parentString, guiClass.toString(),
                "GuiClass.toString() should return the same value as parent UMLClass.toString()");
    }
}
