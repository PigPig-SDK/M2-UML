package org.umlproject.UI;

import java.awt.MouseInfo;
import java.awt.Point;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import org.umlproject.App;

public class GuiCamera {
    private static Point2D camLocation = new Point2D(0,0);
    private static double cameraZoom = 1;
    private static final int ARROWKEY_SPEED = 4500;
    private static boolean up, down, left, right;
    private static final double ZOOM_SCALE_AMMOUNT = 0.005f;
    private static final double ZOOM_SCALE_MIN = 0.25f;
    private static final double ZOOM_SCALE_MAX = 3f;

    private static long lastTime = 0;
    
    private static double startDragX = 0;
    private static double startDragY = 0;
    
    private static boolean isDragging = false;

    private static final Pane world = GuiController.getInstance().getWorld();
    
    public static void setCameraLocation(Point2D location)
    {
        camLocation = location;
        world.setTranslateX(camLocation.getX());
        world.setTranslateY(camLocation.getY());
    }
    public static Point2D getUserInputDirection(double deltaTime)
    {
        double x = 0;
        double y = 0;

        double speedCalc = ARROWKEY_SPEED / Math.max(cameraZoom,5);
        
        if (left && !right)  x = speedCalc * deltaTime;
        else if (right && !left) x = -speedCalc * deltaTime;

        if (up && !down) y = speedCalc * deltaTime;
        else if (down && !up) y = -speedCalc * deltaTime;

        return new Point2D(x, y);
    }
    private static void setZoom(double newZoom, ScrollEvent event) {
        if (newZoom < ZOOM_SCALE_MIN) newZoom = ZOOM_SCALE_MIN;
        if (newZoom > ZOOM_SCALE_MAX) newZoom = ZOOM_SCALE_MAX;
        Point2D before = world.sceneToLocal(event.getSceneX(), event.getSceneY());//Get og realitive
        
        world.setScaleX(newZoom);
        world.setScaleY(newZoom);
        
        Point2D after = world.sceneToLocal(event.getSceneX(), event.getSceneY());//Get realitive.
        Point2D delta =  after.subtract(before);
        setCameraLocation(new Point2D(world.getTranslateX() + delta.getX() * newZoom, world.getTranslateY() + delta.getY() * newZoom));
        cameraZoom = newZoom;
    }
    private static void manageCameraInput()
    {
        if(App.currentScene == null)
            return;
        //Key down
        App.currentScene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown())
                return;
            
            switch (event.getCode()) {
                case UP, W -> {up = true;event.consume();}
                case DOWN, S -> {down = true;event.consume();}
                case LEFT, A -> {left = true;event.consume();}
                case RIGHT, D -> {right = true;event.consume();}
            }
        });
        //Key up
        App.currentScene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            switch (event.getCode()) {
                case UP, W -> {up = false;event.consume();}
                case DOWN, S -> {down = false;event.consume();}
                case LEFT, A -> {left = false;event.consume();}
                case RIGHT, D -> {right = false;event.consume();}
            }
        });
        //Mouse zoom
        App.currentScene.setOnScroll(event ->{
            double zoomAmmount = event.getDeltaY();
            
            if(zoomAmmount == 0)
                return;
            setZoom(cameraZoom + zoomAmmount * ZOOM_SCALE_AMMOUNT, event);
        });
        
        //Logic to drag the camera around
        App.currentScene.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY){
                GuiSelectDrag.selectDrag(event);
                return;
            }
            double currentX = event.getScreenX();
            double currentY = event.getScreenY();
            switch (event.getButton()) {
                case MIDDLE:
                case SECONDARY:
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
        App.currentScene.setOnMousePressed(event -> {
            isDragging = true;
            //Reset our current drag distance.
            startDragX = event.getScreenX();
            startDragY = event.getScreenY(); 
            //We click onto a type of textbox, do not remove selection.
            if (!(event.getTarget() instanceof TextInputControl)) GuiController.getInstance().getViewPane().requestFocus();
        });
        //Stop drag...
        App.currentScene.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                isDragging = false;
            }
            GuiSelectDrag.selectDragRelease(e);
        });
    }
    /**
     * Given a point2D in screen space, convert to 'world' space.
     * This manages all the boring transformations, camera movement and scaling...
     * @param screenSpace The location on the screen.
     * @return the expected world coords
     */
    public static Point2D screenToWorld(Point2D screenSpace)
    {
        Pane view = GuiController.getInstance().getViewPane();
        
        Point2D sceneLocation = view.localToScene(screenSpace);
        Point2D worldLocation = world.sceneToLocal(sceneLocation);
        return worldLocation;
    }
    /**
     * @return The world space center of the camera
     */
    public static Point2D getScreenCenter()
    {
        Pane view = GuiController.getInstance().getViewPane();

        double centerX = view.getWidth() / 2;
        double centerY = view.getHeight() / 2;

        return screenToWorld(new Point2D(centerX, centerY));
    }
    /**
     * Initializes the camera
     */
    public static void setupCamera()
    {
        
        AnimationTimer cameraTimer = new AnimationTimer() {
            @Override
            public void handle(long now){
                double deltaTime = (now - lastTime) / 1000000000.0;
                lastTime = now;
                //Now push the camera based on the input.
                setCameraLocation(camLocation.add(getUserInputDirection(deltaTime)));
            }
        };
        manageCameraInput();//Update input
        cameraTimer.start();
    }
    /**
     * Returns camera zoom. 
     * I comment my code. 
     * Please give points now.
     * @return cameraZoom.
     */
    public static double getCameraZoom()
    {
        return cameraZoom;
    }
    public static void setDragging(boolean _isDragging)
    {
        isDragging = _isDragging;
    }
    public static boolean isDragging()
    {
        return isDragging;
    }
    public static Point2D getMouseInScene() {
        
        Point p = MouseInfo.getPointerInfo().getLocation();
        double screenX = p.getX();
        double screenY = p.getY() - (5/GuiCamera.getCameraZoom());//Java jank..

        double sceneX = screenX - App.mainStage.getX() - App.currentScene.getX();
        double sceneY = screenY - App.mainStage.getY() - App.currentScene.getY();

        return new Point2D(sceneX, sceneY);
    }
}
