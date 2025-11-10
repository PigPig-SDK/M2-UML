package org.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Server extends Thread {
    
    private static final int TICK_INTERVAL = 100;//in MS.
    private boolean isReadyForConnections = false;
    private boolean running = true;
    private int port;
    private ServerSocket serverSocket;
    private long currentTick = 0;
    
    private ArrayList<ClientHandler> clients = new ArrayList<>();
    private final Object clientLock = new Object();
    private Timer clientUpdateTimer;
    
    /**
     * @param port The Port we are going to host under.
     * @throws java.io.IOException When the socket throws.
     */
    public Server(int port) throws IOException
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
                    clients.add((ClientHandler) clientThread);
                }
                clientThread.setDaemon(true);
                clientThread.start();
            }
            catch(SocketTimeoutException e)
            {
                System.out.println("SocketBreakLoop : SERVER HANDLER...");
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
     * 
     * @param netPacket
     */
    public void sendMessageToAllClients(NetworkPacket netPacket)
    {
        synchronized (clientLock) {
            for(ClientHandler clientHandler : clients)
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
}
