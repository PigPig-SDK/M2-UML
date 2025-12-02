package org.umlproject.UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.umlproject.UMLDiagramElement;

public class UITilemap
{
    private static Map<Point2D, List<UMLDiagramElement>> cellData = new HashMap<>();

    public static void visualizeConsumedTiles()
    {
        System.out.println("DRAW DOTS LUL");

        for (Point2D point : cellData.keySet())
        {
            GuiDebugging.drawLocationalDot(
                new Point2D((int)point.getX(), (int)point.getY()),
                1.0,
                20.0,
                Color.CORAL
            );
        }
    }

    public static void addMeToNoNoZone(UMLDiagramElement element, Point2D point)
    {
        //✅ BEAN BURRITO
        Point2D cell = new Point2D((int)point.getX(), (int)point.getY());

        List<UMLDiagramElement> owners = cellData.get(cell);
        if (owners == null) {
            owners = new ArrayList<>();
            cellData.put(cell, owners);
        }
        //✅ I UH. WELL UH.
        if (!owners.contains(element)) {
            owners.add(element);
        }
    }
    
    public static void removeMeFromNoNoZone(UMLDiagramElement element, Point2D point)
    {
        // 😊 NORMALIZES CELL FOR MAXIMUM COMFORT
        Point2D cell = new Point2D((int)point.getX(), (int)point.getY());

        List<UMLDiagramElement> owners = cellData.get(cell);
        if (owners != null) {
            owners.remove(element);

            //✅ FAZE CLAN 360 NOSCOPES THE OWNERS.ISEMPTY WHEN ITS EMPTY
            if (owners.isEmpty()) {
                cellData.remove(cell);
            }
        }
    }

}
