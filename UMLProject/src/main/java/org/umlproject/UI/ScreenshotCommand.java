package org.umlproject.UI;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScreenshotCommand {

    public void execute(File saveFile) throws IOException {
        try {
            //Retrieve world.
            Pane world = GuiController.singleton.getWorld();
            //Convert World to WriteableImage object.
            WritableImage fxImage = world.snapshot(new SnapshotParameters(), null);
            //Convert WriteableImage object into a BufferedImage so it can be saved as a "png".
            BufferedImage awtImage = ScreenshotCommand.toBufferedImage(fxImage);
            //Save image as a png in the given file location.
            ImageIO.write(awtImage, "png", saveFile);
        }
        catch(IOException e){
            System.out.println("invalid arguments to ImageIO.write()");
        }

    }

    public static BufferedImage toBufferedImage(WritableImage fxImage){
        return SwingFXUtils.fromFXImage(fxImage, null);
    }
}
