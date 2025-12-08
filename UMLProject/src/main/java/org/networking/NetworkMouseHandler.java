package org.networking;

import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.umlproject.App;
import org.umlproject.Main;
import org.umlproject.UI.GuiCamera;

public class NetworkMouseHandler implements NetworkManagerListener
{
    private static Map<UUID, NetworkMouse> userMice = new HashMap<>();
    
    private static AnimationTimer mouseUpdateTimer;
    
    private static Timeline timeline;
    
    public static void initialize()
    {
        if(Main.isInTerminalMode()) return;
        NetworkManager.listeners.add(new NetworkMouseHandler());
    }
    public static void setup()
    {        
        if(Main.isInTerminalMode()) return;
        
        //Handle clientside mouse location
        //We want to only send our mouse location 30 times a second...
        timeline = new Timeline(
            new KeyFrame(Duration.millis(33.333), e -> handleMouseInput())
        );
        
        //Handle serverside updates
        mouseUpdateTimer = new AnimationTimer() {
            @Override public void handle(long now) {
                updateConnectedMice();
            }
        };
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
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
       
        PayloadNetworkMouse nmp = new PayloadNetworkMouse(null, null, screenLocation.getX(), screenLocation.getY(), GuiCamera.isDragging());
        NetworkPacket networkPacket = NetworkPacket.objectToNetworkPacket(PacketType.MOUSE_UPDATE, nmp);
        NetworkManager.getClientInstance().sendNetworkPacket(networkPacket);
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
            PayloadNetworkMouse nmp = networkPacket.payloadToObject(PayloadNetworkMouse.class);
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
        
        for(NetworkMouse networkMouse : userMice.values())
        {
            networkMouse.cleanUp();
        }
        
        userMice.clear();
        mouseUpdateTimer.stop();
        timeline.stop();
    }
    
//region NetworkManagerListener
    @Override
    public void onNetworkConnect() { 
        System.out.println("Setup called");
        setup();
    }

    @Override
    public void onNetworkDisconnect() { 
        System.out.println("Shutdown called");
        shutdown();
    }
    
    @Override
    public void onClientDisconnect(UUID clientId) {
        
        if(Main.isInTerminalMode()) return;
        
        if(!userMice.containsKey(clientId)) return;
        userMice.get(clientId).cleanUp();
        userMice.remove(clientId);
    }
//endregion
}
