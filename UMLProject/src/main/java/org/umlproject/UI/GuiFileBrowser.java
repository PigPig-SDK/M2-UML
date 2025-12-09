package org.umlproject.UI;

import java.io.File;
import javafx.stage.FileChooser;
import javax.swing.filechooser.FileSystemView;
import org.umlproject.App;
import org.umlproject.Main;
import org.umlproject.UMLDocument;

public class GuiFileBrowser {
    /**
     * Returns the default directory for file IO prompts.
     */
    private static File defaultDirectory()
    {
        return FileSystemView.getFileSystemView().getDefaultDirectory();
    }
    private static FileChooser generateChooser(String title, String initialFileName, File initialDirectory, FileChooser.ExtensionFilter ... filters)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        if(initialFileName != null) fileChooser.setInitialFileName(initialFileName);
        fileChooser.setInitialDirectory(initialDirectory);
        
        for(FileChooser.ExtensionFilter filter : filters)
        {
            fileChooser.getExtensionFilters().addAll(filter);
        }
        
        return fileChooser;
    }
    /**
     * This will open the users OS's save functionality.
     * @return The directory they have chosen to save to.
     */
    public static File promptForSaveDiectory()
    {
        return generateChooser(
                "Save UML File",
                "MyUMLDocument.json", 
                defaultDirectory(), 
                new FileChooser.ExtensionFilter("UMLDocument", "*.json")).showSaveDialog(App.mainStage);
    }

    /**
     * This method opens the user's OS's save functionality for the sake of exporting a .png screenshot
     * of the current state of the UML diagram.
     * @return The directory they have chosen to save to.
     */
    public static File promptForScreenshotExportDirectory(){
        
        return generateChooser(
                "Save Screenshot Image",
                "UMLDiagramScreenshot.png", 
                defaultDirectory(), 
                new FileChooser.ExtensionFilter("UMLDiagramScreenshot", "*.png")).showSaveDialog(App.mainStage);
    }

    /**
     * This will open the users OS's load functionality.
     * @return The directory they have chosen to load.
     */
    public static File promptForLoadDirectory()
    {
        return generateChooser(
        "Load UML File",
        null, 
        defaultDirectory(),
        new FileChooser.ExtensionFilter("UMLDocument", "*.json")
        ).showOpenDialog(App.mainStage);
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
