package org.umlproject.UI;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.ScrollEvent;
import org.umlproject.Main;

public class GuiCamera {
    private static Point2D camLocation = new Point2D(0,0);
    private static double cameraZoom = 1;
    private static final int ARROWKEY_SPEED = 30;
    private static boolean up, down, left, right;
    private static final double ZOOM_SCALE_AMMOUNT = 0.005f;
    private static final double ZOOM_SCALE_MIN = 0.1f;
    private static final double ZOOM_SCALE_MAX = 3f;

    
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
    private static void setZoom(double newZoom, ScrollEvent e) {
        if (newZoom < ZOOM_SCALE_MIN) newZoom = ZOOM_SCALE_MIN;
        if (newZoom > ZOOM_SCALE_MAX) newZoom = ZOOM_SCALE_MAX;
        Group world = GuiController.singleton.getWorld();
        Point2D before = world.sceneToLocal(e.getSceneX(), e.getSceneY());//Get og realitive
        
        world.setScaleX(newZoom);
        world.setScaleY(newZoom);
        
        Point2D after = world.sceneToLocal(e.getSceneX(), e.getSceneY());//Get realitive.
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
        Main.currentScene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP -> up = true;
                case DOWN -> down = true;
                case LEFT -> left = true;
                case RIGHT -> right = true;
            }
        });
        //Key up
        Main.currentScene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case UP -> up = false;
                case DOWN -> down = false;
                case LEFT -> left = false;
                case RIGHT -> right = false;
            }
        });
        
        Main.currentScene.setOnScroll(event ->{
            double zoomAmmount = event.getDeltaY();
            
            if(zoomAmmount == 0)
                return;
            setZoom(cameraZoom + zoomAmmount * ZOOM_SCALE_AMMOUNT, event);
            
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
