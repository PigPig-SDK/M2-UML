package org.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    
    private boolean isReadyForConnections = false;
    private boolean running = true;
    private int port;
    private ServerSocket serverSocket;
    private long currentTick = 0;
    
    private ArrayList<ClientHandler> clients = new ArrayList<>();
    
    /**
     * @param port The Port we are going to host under.
     * @throws java.io.IOException When the socket throws.
     */
    public Server(int port) throws IOException
    {
        this.port = port;
        this.serverSocket = new ServerSocket(port);

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
                clients.add((ClientHandler) clientThread);
                clientThread.start();
            }
            catch(IOException e)
            {
                
                System.out.println("Socket startup, Client error:" +  e);
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
        for(ClientHandler clientHandler : clients)
        {
            try
            {
                clientHandler.sendNetworkPacket(netPacket);
                System.out.println("Sent message to client.");

            }
            catch(IOException ex)
            {
                System.out.println("Failed to sendMessageToAllClients: " + clientHandler.getUserName());
            }
        }
    }
    
    /**
     * Shutsdown the current server.
     */
    public void shutdown()
    {
        //Shutdown all clients...
        System.out.println("Shutdown server. Closing all clients!");
        sendMessageToAllClients(new NetworkPacket(0, PacketType.DISCONNECT,""));
        for(ClientHandler clientHandler : clients)
        {
            System.out.println("Told client listener thread to die.");
            clientHandler.disconnect();
        }
        
        this.running = false;
        this.isReadyForConnections = false;
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
}
