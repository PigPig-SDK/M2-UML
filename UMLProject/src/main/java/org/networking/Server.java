package org.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Server is a class which manages existing connections, and establishes continuous connections.
 * It also handles sending messages to all clients, this includes heartbeats
 * 
 * In function. When a client asks to 'establish a connection', the server spins up a 
 * 'ClientHandler' to handle the connection on a different thread.
 * 
 * If a client fails to send a heartbeat in 
 */
public class Server extends Thread {
    
    private static final int TICK_INTERVAL = 100;//in MS.
    private boolean isReadyForConnections = false;
    private boolean running = true;
    private int port;
    private ServerSocket serverSocket;
    private long currentTick = 0;
    
    private Set<ClientHandler> clients = new HashSet<>();
    private final Object clientLock = new Object();
    private Timer clientUpdateTimer;
    
    
    //Local client stuff
    private boolean awaitingLocalClient = false;
    private ClientHandler localClient = null;
    
    /**
     * @param port The Port we are going to host under.
     * @throws java.io.IOException When the socket throws.
     */
    public Server(int port, boolean summonsLocalClient) throws IOException
    {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
        this.serverSocket.setSoTimeout(NetworkManager.unstuckTimeout * 1000);
        clientUpdateTimer = new Timer(true);
        clientUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                onTimerTick();
            }
        }, 0, TICK_INTERVAL);
    }
    /**
     * Executes every "TICK_INTERVAL" till the thread dies.
     */
    private void onTimerTick() {
        //Safely remove the client.
        //Careful with this code.
        synchronized (clientLock) {
            Iterator<ClientHandler> it = clients.iterator();
            while (it.hasNext()) {
                ClientHandler client = it.next();
                if (client.hasTimedOut()) {
                    it.remove();
                    client.disconnect();
                }
            }
        }
        NetworkPacket timestep = new NetworkPacket(currentTick, PacketType.HEARTBEAT, null);
        sendMessageToAllClients(timestep);
        currentTick++;
    }
    
    @Override
    public void run()
    {
        while(running)
        {
            Socket socket = null;
            try
            {
                isReadyForConnections = true;
                socket = serverSocket.accept();//Wait for client to connect...
                
                // obtaining input and out streams
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                
                //Assign new thread for the client...
                Thread clientThread = new ClientHandler(socket, dataInputStream, dataOutputStream);
                synchronized (clientLock) {
                    if(awaitingLocalClient)
                    {
                        this.awaitingLocalClient = false;
                        this.localClient = (ClientHandler)clientThread;
                    }
                    this.clients.add((ClientHandler) clientThread);
                }
                clientThread.setDaemon(true);
                clientThread.start();
            }
            catch(SocketTimeoutException e)
            {
                //Ok exception, just breaks connection listening softlocks.
            }
            catch(IOException e)
            {
                System.out.println("Server Socket:" +  e.getMessage());
                //Try close socket! Something went wrong.
                try { 
                    if(socket != null)
                        socket.close(); 
                } 
                catch(IOException socketIOEx) { System.out.println("Client socket failed to close!"); }
            }
        }
        //We are shutting down... Cleanup.
        for(ClientHandler client : clients)
        {
            client.disconnect();
        }
    }
    /**
     * Sends a network packet to all clients
     * If a client cannot receive a message because they have been terminated, their thread gets shutdown.
     * @param netPacket The network packet to transmit to all users
     */
    public synchronized void sendMessageToAllClients(NetworkPacket netPacket)
    {
        //Send without blacklist
        sendMessageToAllClients(netPacket, new HashSet<ClientHandler>());
    }
    /**
     * Sends a network packet to all clients
     * If a client cannot receive a message because they have been terminated, their thread gets shutdown.
     * @param netPacket The network packet to transmit to all users
     * @param blackList
     */
    public synchronized void sendMessageToAllClients(NetworkPacket netPacket, Set<ClientHandler> blackList)
    {
        
        Set<ClientHandler> allClients = getClients();
        allClients.removeAll(blackList);
        
        //Ensure we are not causing race conditions...
        for(ClientHandler clientHandler : allClients)
        {
            try
            {
                clientHandler.sendNetworkPacket(netPacket);
            }
            catch(IOException ex)
            {
                if("Socket closed".equalsIgnoreCase(ex.getMessage()))//Don't send messages to deadweight... Killem.
                {
                    clientHandler.disconnect();//Stop talking to them...
                }
            }
        }
        
    }
    /**
     * Shutsdown the current server.
     */
    public void shutdown()
    {
        //Shutdown all clients...
        clientUpdateTimer.cancel();
        System.out.println("Shutdown server. Closing all clients!");
        sendMessageToAllClients(new NetworkPacket(0, PacketType.DISCONNECT,""));
        try {
            Thread.sleep(50);
        } 
        catch (InterruptedException e) {
            System.out.println("Shutdown timer failure: " + e.getMessage());
        }
        
        for(ClientHandler clientHandler : clients)
        {
            clientHandler.disconnect();
        }
        
        this.running = false;
        this.isReadyForConnections = false;
        try
        {
            serverSocket.close();
        }
        catch(IOException ignoreMe){}
    }
    /**
     * Returns the bound IP and PORT.
     * @return A InetSocketAddress containing a port and ip of this machine
     * Mind you this return value is localhost/port.
     */
    public InetSocketAddress getAddress()
    {
        return new InetSocketAddress(serverSocket.getInetAddress(), port);
    }
    /**
     * Returns true if the server is accepting connections.
     * @return True if ready for connections. False otherwise.
     */
    public boolean isReadyForConnections()
    {
        return this.isReadyForConnections;
    }
    /**
     * Returns the current tick.
     */
    public long getTick()
    {
        return currentTick;
    }
    /**
     * Thread safe removal of a clientHandler.
    */
    public void removeClientHandler(ClientHandler clienthandler)
    {
        synchronized (clientLock) {
            clients.remove(clienthandler);
        }
    }
    /**
     * Get all clients
     * Note: this is a shallow copy of the list. Modifying the items in the list will affect the classes.
     * However, removing items from the list will not affect the 'OG' client list
     */
    public Set<ClientHandler> getClients()
    {
        Set<ClientHandler> tempList = new HashSet<>();
        synchronized (clientLock) {
            Iterator<ClientHandler> it = clients.iterator();
            while (it.hasNext()) {
                ClientHandler client = it.next();
                tempList.add(client);
            }
        }
        return tempList;
    }
    /**
     * Gets the server
     * @return NULL if no local client exists.
     */
    public ClientHandler getServerClient()
    {
        return localClient;
    }
}
