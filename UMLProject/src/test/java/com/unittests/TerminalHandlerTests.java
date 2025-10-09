package com.unittests;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.umlproject.TerminalHandler;


public class TerminalHandlerTests 
{
    @Test
    public void runCommand_InvalidCommand_Success()
    {
        ByteArrayOutputStream testContent = new ByteArrayOutputStream();
        // Arrange
        System.setOut(new PrintStream(testContent));
        // Act
        TerminalHandler.runCommand("fake command");
        // Assert
        assertEquals("'fake' is not a command",testContent.toString().trim()); 
    }
    @Test
    public void runCommand_CommandBinds_Success()
    {
        ByteArrayOutputStream testContent = new ByteArrayOutputStream();
        // Arrange
        System.setOut(new PrintStream(testContent));
        // Act
        TerminalHandler.runCommand("help");
        // Assert
        Assertions.assertNotEquals("'help' is not a command",testContent.toString().trim());
    }
}
