package org.umlproject.UI;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.TextInputControl;
import static javafx.scene.input.MouseButton.MIDDLE;
import javafx.scene.input.ScrollEvent;
import org.umlproject.Main;

public class GuiCamera {
    private static Point2D camLocation = new Point2D(0,0);
    private static double cameraZoom = 1;
    private static final int ARROWKEY_SPEED = 40;
    private static boolean up, down, left, right;
    private static final double ZOOM_SCALE_AMMOUNT = 0.005f;
    private static final double ZOOM_SCALE_MIN = 0.1f;
    private static final double ZOOM_SCALE_MAX = 3f;

    
    private static double startDragX = 0;
    private static double startDragY = 0;
    
    public static void setCameraLocation(Point2D location)
    {
        Group world = GuiController.singleton.getWorld();
        camLocation = location;
        world.setTranslateX(camLocation.getX());
        world.setTranslateY(camLocation.getY());
    }
    public static Point2D getUserInputDirection()
    {
        double x = 0;
        double y = 0;

        double speedCalc = ARROWKEY_SPEED / Math.max(cameraZoom,5);
        
        if (left && !right)  x = speedCalc;
        else if (right && !left) x = -speedCalc;

        if (up && !down) y = speedCalc;
        else if (down && !up) y = -speedCalc;

        return new Point2D(x, y);
    }
    private static void setZoom(double newZoom, ScrollEvent event) {
        if (newZoom < ZOOM_SCALE_MIN) newZoom = ZOOM_SCALE_MIN;
        if (newZoom > ZOOM_SCALE_MAX) newZoom = ZOOM_SCALE_MAX;
        Group world = GuiController.singleton.getWorld();
        Point2D before = world.sceneToLocal(event.getSceneX(), event.getSceneY());//Get og realitive
        
        world.setScaleX(newZoom);
        world.setScaleY(newZoom);
        
        Point2D after = world.sceneToLocal(event.getSceneX(), event.getSceneY());//Get realitive.
        Point2D delta =  after.subtract(before);
        //System.out.println("delta : " + delta.getX() + " | " + delta.getY());
        setCameraLocation(new Point2D(world.getTranslateX() + delta.getX() * newZoom, world.getTranslateY() + delta.getY() * newZoom));
        cameraZoom = newZoom;
    }
    private static void manageCameraInput()
    {
        if(Main.currentScene == null)
            return;
        //Key down
        Main.currentScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP, W -> up = true;
                case DOWN, S -> down = true;
                case LEFT, A -> left = true;
                case RIGHT, D -> right = true;
            }
        });
        //Key up
        Main.currentScene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case UP, W -> up = false;
                case DOWN, S -> down = false;
                case LEFT, A -> left = false;
                case RIGHT, D -> right = false;
            }
        });
        //Mouse zoom
        Main.currentScene.setOnScroll(event ->{
            double zoomAmmount = event.getDeltaY();
            
            if(zoomAmmount == 0)
                return;
            setZoom(cameraZoom + zoomAmmount * ZOOM_SCALE_AMMOUNT, event);
        });
        
        //Logic to drag the camera around
        Main.currentScene.setOnMouseDragged(event -> {
            double currentX = event.getScreenX();
            double currentY = event.getScreenY();

            switch (event.getButton()) {
                case MIDDLE:
                case PRIMARY:
                    Point2D offset = new Point2D(currentX - startDragX, currentY - startDragY);
                    setCameraLocation(camLocation.add(offset));
                    break;
                default:
                    break;
            }
            startDragX = currentX;
            startDragY = currentY; 
        });
        //Clicking into the void deselects any textbox...
        Main.currentScene.setOnMousePressed(event -> {
            //Reset our current drag distance.
            startDragX = event.getScreenX();
            startDragY = event.getScreenY(); 
            //We click onto a type of textbox, do not remove selection.
            if (!(event.getTarget() instanceof TextInputControl)) GuiController.singleton.getViewPane().requestFocus();
        });
    }
    public static void setupCamera()
    {
        AnimationTimer cameraTimer = new AnimationTimer() {
            @Override
            public void handle(long now){
                //Now push the camera based on the input.
                setCameraLocation(camLocation.add(getUserInputDirection()));
            }
        };
        manageCameraInput();//Update input
        cameraTimer.start();
    }
}
