package com.unittests;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.umlproject.*;
import org.umlproject.UI.GuiClass;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.scene.layout.Pane;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Unit tests for the GuiClass class.
 * These tests validate model-to-GUI synchronization, selection state,
 * location handling, and basic layout generation in JavaFX headless mode.
 */
public class GuiClassTest {

    private GuiClass guiClass;
    private UMLClass parentClass;
    private Pane world;

    @BeforeEach
    public void setUp() {
        // Initialize JavaFX runtime (needed even in headless mode)
        assumeTrue(!"true".equals(System.getenv("CLI")), "Skipping GUI test in CLI");
        new JFXPanel();
        world = new Pane();
        parentClass = new UMLClass("TestClass");
        guiClass = new GuiClass(world, parentClass);
    }

    /** Ensures getParentClass() returns the correct UMLClass reference. */
    @Test
    public void testGetParentClass() {
        assertEquals(parentClass, guiClass.getParentClass(),
                "GuiClass should return its parent UMLClass");
    }

    /** Ensures setSelected() correctly updates internal state. */
    @Test
    public void testSetAndGetSelected() {
        guiClass.setSelected(true);
        assertTrue(guiClass.getSelected(), "GuiClass should report selected = true");

        guiClass.setSelected(false);
        assertFalse(guiClass.getSelected(), "GuiClass should report selected = false");
    }

    /** Verifies that setLocation() and getLocation() behave correctly. */
    @Test
    public void testSetAndGetLocation() {
        Point2D newLocation = new Point2D(150, 250);
        guiClass.setLocation(newLocation);

        assertEquals(newLocation, guiClass.getLocation(),
                "GuiClass should store and return correct location");
    }

    /**
     * Tests that convertDataFieldsToHBoxes() populates the VBox with TextFields
     * corresponding to UMLDataFields, without throwing exceptions.
     */
    @Test
    public void testConvertDataFieldsToHBoxes() throws Exception {
        new JFXPanel(); // Initialize JavaFX environment

        Pane world = new Pane();
        UMLClass parent = new UMLClass("TestClass");
        GuiClass gui = new GuiClass(world, parent);

        // Prepare test data
        HashMap<String, UMLDataField> fields = new HashMap<>();
        fields.put("id", new UMLDataField("id", DataType.INT, Visibility.PUBLIC));
        fields.put("name", new UMLDataField("name", "String", DataType.OTHER, Visibility.PRIVATE));

        // Access private VBox via reflection
        Field vboxField = GuiClass.class.getDeclaredField("dataFieldTextFields");
        vboxField.setAccessible(true);
        VBox dataVBox = new VBox();
        vboxField.set(gui, dataVBox);

        // Run the method
        Platform.runLater(() -> gui.convertDataFieldsToHBoxes(fields));

        // Wait briefly for FX thread to execute
        Thread.sleep(300);

        // Now assert the VBox got populated
        assertFalse(dataVBox.getChildren().isEmpty(),
                "VBox should contain children after convertDataFieldsToHBoxes()");

        // Check that each entry contains a TextField with expected content
        List<Node> nodes = dataVBox.getChildren();
        boolean foundPublicInt = false;
        boolean foundPrivateCustom = false;

        for (Node node : nodes) {
            if (node instanceof HBox box) {
                if (box.getChildren().size() > 1 && box.getChildren().get(1) instanceof javafx.scene.control.TextField tf) {
                    String text = tf.getText();
                    if (text.contains("PUBLIC") && text.contains("INT")) foundPublicInt = true;
                    if (text.contains("PRIVATE") && text.contains("String")) foundPrivateCustom = true;
                }
            }
        }

        assertTrue(foundPublicInt, "Should contain PUBLIC INT field");
        assertTrue(foundPrivateCustom, "Should contain PRIVATE String field");
    }

    /**
     * Tests that getRectBounds() returns a non-null rectangle after update().
     */
    @Test
    public void testGetRectBoundsAfterUpdate() throws Exception {
        new JFXPanel(); // Initialize JavaFX runtime
        Pane world = new Pane();
        UMLClass parent = new UMLClass("RectTest");
        GuiClass gui = new GuiClass(world, parent);

        Platform.runLater(() -> {
            gui.update(parent);

            try {
                Field field = GuiClass.class.getDeclaredField("parentVBox");
                field.setAccessible(true);
                VBox parentVBox = (VBox) field.get(gui);

                if (parentVBox != null) {
                    parentVBox.applyCss();
                    parentVBox.layout();
                }

                Rectangle2D rect = gui.getRectBounds();
                assertNotNull(rect, "Rectangle should not be null");
                assertTrue(rect.getWidth() >= 0, "Width should be >= 0");
                assertTrue(rect.getHeight() >= 0, "Height should be >= 0");
            } catch (Exception ex) {
                fail("Failed to access VBox or bounds: " + ex.getMessage());
            }
        });

        Thread.sleep(400); // wait for FX thread
    }

    /** Ensures toString() mirrors the parent UMLClass string. */
    @Test
    public void testToStringMatchesParentClass() {
        assertEquals(parentClass.toString(), guiClass.toString(),
                "GuiClass.toString() should return parent UMLClass.toString()");
    }
}
