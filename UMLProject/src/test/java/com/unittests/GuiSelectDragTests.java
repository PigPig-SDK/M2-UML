package com.unittests;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.umlproject.UI.GuiClass;
import org.umlproject.UI.GuiSelectDrag;
import org.umlproject.UMLClass;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class GuiSelectDragTests {

    private GuiClass guiClass1;

    private GuiClass guiClass2;
    private Pane world;

    @BeforeEach
    public void setUp() {
        assumeTrue(!"true".equals(System.getenv("CLI")), "Skipping GUI test in CLI");
        // Initialize JavaFX runtime (needed even in headless mode)
        new JFXPanel();
        world = new Pane();
        UMLClass parentClass1 = new UMLClass("TestClass1");
        guiClass1 = new GuiClass(world, parentClass1);
        UMLClass parentClass2 = new UMLClass("TestClass2");
        guiClass2 = new GuiClass(world, parentClass2);
    }

    @Test
    public void intersectionSelects(){
        Platform.runLater( () ->{
            //Arrange
            MouseEvent event = new MouseEvent(MouseEvent.MOUSE_DRAGGED, 5, 5, 5, 5,
                    MouseButton.PRIMARY, 1, true, false, false, false,
                    true, false, false, false,
                    false, false, new PickResult(world, 5, 5));

            guiClass1.setLocation(new Point2D(5, 5));
            //Act
            GuiSelectDrag.selectDrag(event);
            //Assert
            assertTrue(guiClass1.getSelected());
        });
    }

    @Test
    public void nonIntersectionDoesNotSelect(){
        Platform.runLater( () ->{
            //Arrange
            MouseEvent event = new MouseEvent(MouseEvent.MOUSE_DRAGGED, 0, 0, 0, 0,
                    MouseButton.PRIMARY, 1, true, false, false, false,
                    true, false, false, false,
                    false, false, new PickResult(world, 0, 0));

            guiClass1.setLocation(new Point2D(5, 5));
            //Act
            GuiSelectDrag.selectDrag(event);
            //Assert
            assertFalse(guiClass1.getSelected());
        });
    }

    @Test
    public void selectingNothingDeselects(){
        Platform.runLater( () ->{
            //Arrange
            MouseEvent event = new MouseEvent(MouseEvent.MOUSE_DRAGGED, 0, 0, 0, 0,
                    MouseButton.PRIMARY, 1, true, false, false, false,
                    true, false, false, false,
                    false, false, new PickResult(world, 0, 0));

            guiClass1.setLocation(new Point2D(5, 5));
            guiClass1.setSelected(true);
            //Act
            GuiSelectDrag.selectDrag(event);
            //Assert
            assertFalse(guiClass1.getSelected());
        });
    }

    @Test
    public void intersectionSelectsMultiple(){
        Platform.runLater( () ->{
            //Arrange
            MouseEvent event = new MouseEvent(MouseEvent.MOUSE_DRAGGED, 5, 5, 5, 5,
                    MouseButton.PRIMARY, 1, true, false, false, false,
                    true, false, false, false,
                    false, false, new PickResult(world, 5, 5));

            guiClass1.setLocation(new Point2D(5, 5));
            guiClass2.setLocation(new Point2D(4, 4));
            //Act
            GuiSelectDrag.selectDrag(event);
            //Assert
            assertTrue(guiClass1.getSelected());
            assertTrue(guiClass2.getSelected());
        });
    }

    @Test
    public void nonIntersectionDoesNotSelectMultiple(){
        Platform.runLater( () ->{
            //Arrange
            MouseEvent event = new MouseEvent(MouseEvent.MOUSE_DRAGGED, 0, 0, 0, 0,
                    MouseButton.PRIMARY, 1, true, false, false, false,
                    true, false, false, false,
                    false, false, new PickResult(world, 0, 0));

            guiClass1.setLocation(new Point2D(5, 5));
            guiClass2.setLocation(new Point2D(4, 4));
            //Act
            GuiSelectDrag.selectDrag(event);
            //Assert
            assertFalse(guiClass1.getSelected());
        });
    }

    @Test
    public void selectingNothingDeselectsMultiple(){
        Platform.runLater( () ->{
            //Arrange
            MouseEvent event = new MouseEvent(MouseEvent.MOUSE_DRAGGED, 0, 0, 0, 0,
                    MouseButton.PRIMARY, 1, true, false, false, false,
                    true, false, false, false,
                    false, false, new PickResult(world, 0, 0));

            guiClass1.setLocation(new Point2D(5, 5));
            guiClass1.setSelected(true);
            guiClass2.setLocation(new Point2D(4, 4));
            guiClass2.setSelected(true);
            //Act
            GuiSelectDrag.selectDrag(event);
            //Assert
            assertFalse(guiClass1.getSelected());
            assertFalse(guiClass2.getSelected());
        });
    }
}
