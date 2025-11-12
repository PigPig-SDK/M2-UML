package org.umlproject.UI;

import java.io.File;
import java.io.IOException;

/**
 * This interface is a part of the Command design pattern implementation of the exportScreenshot menu
 * option in the GUI. It designates the execute() method that must be implemented by all concrete
 * Command objects.
 */
public interface ExportCommand {
    public void execute() throws IOException;
}
