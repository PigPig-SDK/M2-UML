package org.umlproject.UI;

import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 * Why use GuiDebugging?
 * Because with this class you no longer have to manage the cleanup of debug draws.
 * Or the boring semantics.
 * Or the tedious setup.
 * You get the point.
 */
public class GuiDebugging {
    
    public static void drawLocationalDot(Point2D location, double lifetime, double radius, Color color)
    {
        if(GuiController.getInstance() == null || GuiController.getInstance().getWorld() == null)
        {
            System.out.println("GuiDebugging::showBounds() ERROR! World/GuiController DNE! Something is completely screwed!");
            return;
        }
        
        Circle circle = new Circle(location.getX(), location.getY(), radius);
        circle.setStroke(color);
        GuiController.getInstance().getWorld().getChildren().add(circle);
        destroyNodeAfterTime(circle, lifetime);
    }
    /**
     * 
     */
    public static void showBounds(Rectangle2D bounds, double lifetime, double strokeWidth, Color color)
    {
        if(GuiController.getInstance() == null || GuiController.getInstance().getWorld() == null)
        {
            System.out.println("GuiDebugging::showBounds() ERROR! World/GuiController DNE! Something is completely screwed!");
            return;
        }
        
        Line top = new Line(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMinY());
        Line bottom = new Line(bounds.getMinX(), bounds.getMaxY(), bounds.getMaxX(), bounds.getMaxY());
        Line left = new Line(bounds.getMinX(), bounds.getMinY(), bounds.getMinX(), bounds.getMaxY());
        Line right = new Line(bounds.getMaxX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
        
        for (Line line : new Line[]{top, bottom, left, right}) {
            line.setStrokeWidth(strokeWidth);
            line.setStroke(color);
            GuiController.getInstance().getWorld().getChildren().add(line);
            destroyNodeAfterTime(line, lifetime);
        }
    }
    /**
     * ChatGPT gave me this. I don't care because this is for debugging only.
     * 
     * @param node The FX node to destroy
     * @param seconds How long...
     */
    private static void destroyNodeAfterTime(Node node, double seconds) {
        PauseTransition lifetime = new PauseTransition(Duration.seconds(seconds));
        lifetime.setOnFinished(e -> {
            if (node.getParent() instanceof javafx.scene.Group parent) {
                parent.getChildren().remove(node);
            }
        });
        lifetime.play();
    }
}
