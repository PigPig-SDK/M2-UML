package com.unittests;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.umlproject.TerminalHandler;

import static org.junit.jupiter.api.Assertions.*;

public class TerminalHandlerTests {

    private final PrintStream originalOut = System.out;

    @AfterEach
    void restoreSystemOut() {
        System.setOut(originalOut);
    }

    @Test
    public void runCommand_InvalidCommand_Success() {
        ByteArrayOutputStream testContent = new ByteArrayOutputStream();
        // Arrange
        System.setOut(new PrintStream(testContent));
        // Act
        TerminalHandler.runCommand("fake command");
        // Assert
        assertEquals("'fake' is not a command", testContent.toString().trim());
    }

    @Test
    public void runCommand_CommandBinds_Success() {
        ByteArrayOutputStream testContent = new ByteArrayOutputStream();
        // Arrange
        System.setOut(new PrintStream(testContent));
        // Act
        TerminalHandler.runCommand("help");
        // Assert
        Assertions.assertNotEquals("'help' is not a command", testContent.toString().trim());
    }

    /* ------------------------------------------------------------------
     * Extra tests for more coverage
     * ------------------------------------------------------------------ */

    @Test
    public void getCommandDescription_validCommand_notNull() {
        // "help" is registered in the static initializer
        String desc = TerminalHandler.getCommandDescription("help");
        assertNotNull(desc);
        assertFalse(desc.isEmpty());
    }

    @Test
    public void getCommandDescription_invalidCommand_returnsNull() {
        String desc = TerminalHandler.getCommandDescription("thisCommandDoesNotExist");
        assertNull(desc);
    }

    @Test
    public void executeCommand_validCommand_returnsTrue() {
        // Just verify it doesn't throw and returns true.
        // "help" is a registered command.
        boolean result = TerminalHandler.executeCommand("help", new String[]{});
        assertTrue(result);
    }

    @Test
    public void runCommand_emptyInput_doesNothing() {
        ByteArrayOutputStream testContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testContent));

        TerminalHandler.runCommand("");   // split -> [""], length 1, command = ""

        // It will try to execute "" and print "' ' is not a command"
        assertEquals("'' is not a command", testContent.toString().trim());
    }

    @Test
    public void printLineBreak_printsDashes() {
        ByteArrayOutputStream testContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testContent));

        TerminalHandler.printLineBreak();

        assertEquals("---------------------", testContent.toString().trim());
    }
}
