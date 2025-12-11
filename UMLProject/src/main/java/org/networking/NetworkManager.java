package org.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.umlproject.Main;
import org.umlproject.UI.FXDialogueFactory;

/**
 * This class manages startup and shutdown of network connections.
 * It also contains some general network constants shared between classes.
 * 
 */
public class NetworkManager {
    /*  CONSTANTS  */
    public static final int MAX_PACKET_LENGTH = 60000;
    public static final int CONNECTION_TIMEOUT = 3;//in seconds
    public static final int unstuckTimeout = 10;//in seconds

    private static Server serverManager = null;
    private static Client clientManager = null;
    
    public static List<NetworkManagerListener>  listeners = new ArrayList<>();
    
    public static final int DEFAULT_PORT = 56329;//Random port i guess...
    
    /**
     * Called on program startup.
     */
    public static void initialize()
    {
        //While this method currently does nothing, it might come in handy later...
        
    }
    
    /**
     * Spins up a 'host', under the condition a host isn't already active.
     * @param port The PORT we are attempting to host under.
     * @param summonLocalClient should our server boot with us as the first 'client'
     *                          DO NOT SET "summonLocalClient" TO FALSE UNLESS YOU KNOW WHAT YOUR ARE DOING.
     */
    public static void startHost(int port, boolean summonLocalClient)
    {
        //No connections allowed
        if(serverManager != null || clientManager != null) return;
        
        try
        {
            System.out.println("Starting host on : " + port);
            serverManager = new Server(port, summonLocalClient);
            serverManager.setDaemon(true);
            serverManager.start();
        }
        catch(IOException ex)
        {
            System.out.println("Server startup failed + " + ex.getMessage());
            return;
        }
        
        if(summonLocalClient)
        {
            //Wait for server to be 'accepting users'
            //Control flow managed VIA dirty while loop.
            while (!serverManager.isReadyForConnections() && serverManager.isAlive()) { try { Thread.sleep(10); } catch (InterruptedException ignored) {}}
            
            //System.out.println("Starting local client: " + port);
            connect(serverManager.getAddress());
        }
    }
    /**
     * Connects to an ip/port, under the condition a connection isn't already active.
     * @param address The IP:PoRT we are attempting to connect.
     */
    public static void connect(InetSocketAddress address)
    {
        if(clientManager != null)
        {
            System.out.println("Already connected to a server.");
            return;
        }
        try
        {
            System.out.println("Connecting...");
            Socket socket = new Socket();
            socket.connect(address, CONNECTION_TIMEOUT * 1000);
            
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            //If no errors, our object is created.
            //If a server is active, our created client is marked as a 'host client'
            //This dosn't grant the user any powers, just helps us avoid specific edgecases of running a client and server on the same memory instance.
            clientManager = new Client(socket, dataInputStream, dataOutputStream, serverManager != null);
            clientManager.setDaemon(true);
            clientManager.start();
            
            NetworkDocumentListener.setupListener();
            
            listeners.forEach((e) -> e.onNetworkConnect());
        }
        catch(IOException ex)
        {
            if(Main.isInTerminalMode())
            {
                System.out.println("Connection failed! " + address);
            }
            else
            {
                FXDialogueFactory.createAlertWindow(Alert.AlertType.ERROR, "Error!", 
                        "Failed to connect to : " + address.getHostName() + ":" + address.getPort(), 
                        address.isUnresolved()? "IP cannot resolve" : "Nobody responded", null).show();
            }
            clientManager = null;
        }
        
    }
    /**
     * Shutsdown all connections (Server & client)
     */
    public static void shutdown()
    {
        try {
            if(clientManager != null)
                clientManager.disconnect();

            if(serverManager != null)
                serverManager.shutdown();

            setClientNull();
            setServerNull();
            NetworkDocumentListener.shutdownListener();
        } 
        catch (Exception e)//Don't have time to fix this. Using generic catch
        {
            System.err.println("NETWORK SHUTDOWN EXCEPTION : " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            //If something goes wrong. Our listeners still will be called.
            listeners.forEach((e) -> e.onNetworkDisconnect());
        }
    }
    /**
     * ONLY DO THIS IF YOU KNOW WHAT YOU ARE DOING!
     * 
     * This exists for edge case client shutdowns
     */
    public static void setClientNull()
    {
        clientManager = null;
    }
    /**
     * ONLY DO THIS IF YOU KNOW WHAT YOU ARE DOING!
     * 
     * This exists for edge case server shutdowns
     */
    public static void setServerNull()
    {
        serverManager = null;
    }
    /**
     * returns the client instance.
     * @return The client manager
     */
    public static Client getClientInstance()
    {
        return clientManager;
    }
    /**
     * returns the server instance.
     * @return The server handler...
     */
    public static Server getServerInstance()
    {
        return serverManager;
    }
    /**
     * Returns the current tick of the server OR client.
     * 
     * If hosting a server, this returns the SERVER TICK.
     * Else if connecting to a server this returns the CLIENT TICK
     * @return -1 if the server and client are not valid.
     */
    public static long getTick()
    {
        if(serverManager != null)
        {
            return serverManager.getTick();
        }
        else if(clientManager != null)
        {
            return clientManager.getTick();
        }
        return -1;
    }
    public static boolean isHosting()
    {
        return getServerInstance() != null;
    }
    public static boolean isConnected()
    {
        return getServerInstance() != null || getClientInstance() != null;
    }
}
