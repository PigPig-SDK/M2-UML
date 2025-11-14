package org.networking;

import com.google.gson.stream.MalformedJsonException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Timer;

/**
 * This class is the server side management of the client.
 * This is a communication stream with a specified 'client' (Not the client class, do not be confused).
 */
public abstract class SocketManager extends Thread
{
    
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Socket socket;
    protected boolean running = true;
    
    public SocketManager(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException
    {
        this.socket = socket;
        this.in = dataInputStream;
        this.out = dataOutputStream;
        this.socket.setSoTimeout(NetworkManager.unstuckTimeout * 1000);

    }
    /**
     * Manages incoming packets on the dedicated thread
     */
    @Override
    public void run() 
    {
        onConnectionStarted();
        while (running) 
        {
            try 
            {
                //Read packet sizse
                int packetSize = in.readInt();

                if(packetSize <= 0 || packetSize >= NetworkManager.MAX_PACKET_LENGTH)
                {
                    if(closeOnInvalidPacket())
                    {
                        System.out.println("Invalid packet length. Kicking client.");
                        break;
                    }
                    else
                    {
                        continue;
                    }
                }

                //Read packet body...
                byte[] data = new byte[packetSize];
                in.readFully(data);

                NetworkPacket netPacket = NetworkPacket.jsonToPacket(new String(data, StandardCharsets.UTF_8));
                
                managePacket(netPacket);
            }
            catch(MalformedJsonException e)
            {
                System.out.println("Malformed JSON!" + e.getMessage());
                continue;
            }
            catch(SocketTimeoutException e)//Timeout
            {
                System.out.println("Timeout hit");
                continue;
            }
            catch (SocketException e) {
                if ("Socket closed".equalsIgnoreCase(e.getMessage()))
                {
                    //System.out.println("SOCKET CLOSED: " + objectName());
                    break;
                } 
            }
            catch (IOException e) {
                if(e.getMessage() == null)//Our socket has closed...? What the fuck java?!?!?!
                {
                    //System.out.println("Socket closed " + objectName());
                    break;
                }
                else
                {
                    System.out.println("Message error: " + e.getMessage());
                }
            }
        }
        //Shutdown connection with client.
        //System.out.println("Packet manager closed..." + objectName());
        disconnect();
    }
    /**
     * Returns the IP of the client we are communicating with.
     */
    public String getUserIP()
    {
        return socket.getInetAddress().getHostName();
    }
    /**
     * Prompts this client listening thread to die.
     */
    public void disconnect()
    {
        try
        {
            this.running = false;
            this.socket.close();
            this.in.close();
            this.out.close();
        }
        catch(IOException ignoreMe){}
    }
    /**
     * Sends a network packet to the client.
     * @param netpacket The network packet we decide to send to the client...
     */    /**
     * Sends a network packet to the client.
     * @param netpacket The network packet we decide to send to the client...
     */
    public void sendNetworkPacket(NetworkPacket netpacket) throws IOException 
    {
        byte[] jsonBytes = netpacket.packetToJson().getBytes(StandardCharsets.UTF_8);
        out.writeInt(jsonBytes.length);//Start by informing the client of our packet size.
        out.write(jsonBytes);//Now send the packet to our client.
        out.flush();
    }
    
    /**
     * Define how an invalid packet size be managed.
     * @return true if the connection should die when a invalid packet is relieved
     */
    protected abstract boolean closeOnInvalidPacket();
    
    /**
     * Manage packet
     * @param netPacket The packet we are reacting to
     */
    protected abstract void managePacket(NetworkPacket netPacket);
    /**
     * The debug object name
     * TODO: remove me when finished with a majority of networking code.
     */
    protected abstract String objectName();//The name of the object... For debugging only...
    /**
     * Called when a connection starts.
     */
    protected abstract void onConnectionStarted();
}
