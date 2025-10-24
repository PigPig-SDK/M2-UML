package org.umlproject.UI;

import java.io.File;
import javafx.stage.FileChooser;
import org.umlproject.Main;
import org.umlproject.UMLDocument;

public class GuiFileBrowser {
    
    /**
     * This will open the users OS's save functionality.
     * @return The directory they have chosen to save to.
     */
    public static File promptForDiectory()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save UML File");
        fileChooser.setInitialFileName("MyUMLDocument.json");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("UMLDocument", "*.json"));
        File selectedDirectory = fileChooser.showSaveDialog(Main.mainStage);
        return selectedDirectory;
    }
    public static String removeFileExtension(String input)
    {
        String fileExtension = UMLDocument.FILEEXTENT_STRING;
        //Would cause issues... Throw it back at them..
        if(input == null || input.length() <= fileExtension.length())
            return input;
        
        if(input.endsWith(fileExtension))
            return input.substring(0, input.length() - fileExtension.length());
        return input;
    }
}
