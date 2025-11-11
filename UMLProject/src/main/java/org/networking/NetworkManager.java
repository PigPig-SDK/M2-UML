package org.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

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
    
    public static boolean isHosting;
    
    private static Server serverManager = null;
    private static Client clientManager = null;
    
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
            serverManager = new Server(port);
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
        }
        catch(IOException ex)
        {
            System.out.println("Connection failed! " + address);
            clientManager = null;
        }
    }
    /**
     * Shutsdown all connections (Server & client)
     */
    public static void shutdown()
    {
        if(clientManager != null)
            clientManager.disconnect();
        
        if(serverManager != null)
            serverManager.shutdown();
        
        setClientNull();
        setServerNull();
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
     * Returns the current tick of the server
     * @return -1 if the server is not valid.
     */
    public static long getServerTick()
    {
        if(serverManager == null)
            return -1;
        return serverManager.getTick();
    }
}
