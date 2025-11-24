package org.networking;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.umlproject.UI.GuiController;
import org.umlproject.UI.GuiDebugging;


public class NetworkMouse {
    
    public Image cursorNormal = new Image(getClass().getResource("/org/umlproject/icons/net_cursor.png").toExternalForm());
    public Image cursorDrag = new Image(getClass().getResource("/org/umlproject/icons/net_cursor_grab.png").toExternalForm());
    
    public Point2D desiredLocation = Point2D.ZERO;
    
    public String username = "Unknown";
    
    private double x,y = 0;//Location
    
    public boolean isDragging = false;
    
    private ImageView mouseImage = null;
    
    private boolean stateDragging = false;
    
    private Label usernameLabel;
    
    private static final double nameplateOffsetX = 1;
    private static final double nameplateOffsetY = -25;
    
    public NetworkMouse(String username)
    {
        this.username = username;
        //Please run on main thread... >:(
        Platform.runLater(() -> {
            this.mouseImage = new ImageView(cursorNormal);
            this.mouseImage.setFitWidth(64);
            this.mouseImage.setFitHeight(64);
            this.mouseImage.setViewOrder(-30);//Put ontop of classes and stuff, but not above most UI.
            this.mouseImage.setMouseTransparent(true);
            
            this.usernameLabel = new Label(username);
            this.usernameLabel.setViewOrder(-31);
            this.usernameLabel.setMouseTransparent(true);
            GuiController.getInstance().getWorld().getChildren().addAll(mouseImage, usernameLabel);
        });
    }
    
    public void cleanUp()
    {
        Platform.runLater(()->{GuiController.getInstance().getWorld().getChildren().removeAll(mouseImage, usernameLabel);});
    }
    
    public void update()
    {
        if(mouseImage == null) return;
        
        mouseImage.setTranslateX(lerp(mouseImage.getTranslateX(), desiredLocation.getX(), 0.1));
        mouseImage.setTranslateY(lerp(mouseImage.getTranslateY(), desiredLocation.getY(), 0.1));
        usernameLabel.setTranslateX(mouseImage.getTranslateX() + nameplateOffsetX);
        usernameLabel.setTranslateY(mouseImage.getTranslateY() + nameplateOffsetY);
        //We should only update the image if we have to...
        //This is to make the cursor cheap to render... or thats the idea...
        if(stateDragging != isDragging)
        {
            if(isDragging)
                mouseImage.setImage(cursorDrag);
            else
                mouseImage.setImage(cursorNormal);
        }
        stateDragging = isDragging;
    }
    
    //Why dosn't javafx have this???
    private static double lerp(double a, double b, double t) { return a + t * (b - a);}
}
