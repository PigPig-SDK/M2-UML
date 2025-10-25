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
    /**
     * Removes the file UMLDocument default file extension from the input pathString.
     * @param pathString that is being referenced.
     * @return pathString without the UMLDocument default file extension.
     */
    public static String removeFileExtension(String pathString)
    {
        String fileExtension = UMLDocument.FILEEXTENT_STRING;
        //Would cause issues... Throw it back at them..
        if(pathString == null || pathString.length() <= fileExtension.length())
            return pathString;
        
        if(pathString.endsWith(fileExtension))
            return pathString.substring(0, pathString.length() - fileExtension.length());
        return pathString;
    }
}
