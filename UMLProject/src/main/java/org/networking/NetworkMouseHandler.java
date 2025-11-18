package org.networking;

import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.IOException;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.umlproject.App;
import org.umlproject.Main;
import org.umlproject.UI.GuiCamera;

public class NetworkMouseHandler 
{
    private static Map<UUID, NetworkMouse> userMice = new HashMap<>();
    
    private static final double timeBetweenUpdates = 0.05f;//seconds
    
    private static AnimationTimer mouseUpdateTimer;
    
    
    
    public static void initialize()
    {        
        if(Main.isInTerminalMode()) return;
        
        mouseUpdateTimer = new AnimationTimer() {
            @Override public void handle(long now) {
                handleMouseInput();//Handle clientside mouse location
                updateConnectedMice();
            }
        };
        mouseUpdateTimer.start();
    }
    /**
     * Called 60 times a second...
     * 
     * This is to package and send our mouse location VIA innernet
     */
    private static void handleMouseInput()
    {
        if(NetworkManager.getClientInstance() == null)
            return;
        if(!App.isMouseInsideWindow())
            return;//Don't give our info.. We are gone man...
        
        Point2D screenLocation = GuiCamera.screenToWorld(GuiCamera.getMouseInScene());
       
        NetworkMousePayload nmp = new NetworkMousePayload(null, null, screenLocation.getX(), screenLocation.getY(), GuiCamera.isDragging());
        NetworkPacket networkPacket = NetworkPacket.objectToNetworkPacket(NetworkManager.getTick(), PacketType.MOUSE_UPDATE, nmp);
        try {
            NetworkManager.getClientInstance().sendNetworkPacket(networkPacket);
        } 
        catch (IOException e) {
            System.out.println("Failed to send mouse input!");
            return;
        }
    }
    
    /**
     * Calls the mice to 'update'
     */
    private static void updateConnectedMice()
    {
        for(NetworkMouse mouse : userMice.values())
        {
            mouse.update();
        }
    }
    
    public static void handleMousePacket(NetworkPacket networkPacket)
    {
        if(networkPacket == null || networkPacket.payload() == null)
            return;
        try {
            NetworkMousePayload nmp = networkPacket.payloadToObject(NetworkMousePayload.class);
            if(nmp == null || nmp.getUserId() == null || nmp.getUsername() == null)
                return;
            UUID userid = nmp.getUserId();
            
            //Add new key...
            if(!userMice.containsKey(userid))
            {
                userMice.put(userid, new NetworkMouse(nmp.getUsername()));
            }
            userMice.get(userid).desiredLocation = nmp.getMouseLocation();
            userMice.get(userid).isDragging = nmp.isDragging();
        } 
        catch (JsonSyntaxException e) {
            System.err.println("Mouse packet exception : " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }
    
    public static void shutdown()
    {
        if(Main.isInTerminalMode()) return;
        
        userMice.clear();
        mouseUpdateTimer.stop();
    }
}
