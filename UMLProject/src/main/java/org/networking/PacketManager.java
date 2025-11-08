package org.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * This class is the server side management of the client.
 * This is a communication stream with a specified 'client' (Not the client class, do not be confused).
 */
public abstract class PacketManager extends Thread
{
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Socket socket;
    protected boolean running = true;
    
    public PacketManager(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) 
    {
        this.socket = socket;
        this.in = dataInputStream;
        this.out = dataOutputStream;
    }
    @Override
    public void run() 
    {
        try 
        {
            System.out.println("Packetmanager startup under type : " + objName());
            while (running) 
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

                //Check if the client wants to disconnect.
                managePacket(netPacket);
            }
        }
        catch (EOFException e)
        {
            System.out.println("Packet size did not match. Disconnecting : " + e.getMessage());
        }
        catch (IOException e) {
            System.out.println("Generic packet failure: " + e.getMessage());
        }
        finally//Cleanup...
        {
            //Shutdown connection with client.
            try
            {
                System.out.println("Packet manager closed..." + objName());
                // closing resources
                this.socket.close();
                this.in.close();
                this.out.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        
    }
    public String getUserName()
    {
        return socket.getInetAddress().getHostName();
    }
    /**
     * Prompts this client listening thread to die.
     */
    public void disconnect()
    {
        running = false;
    }
    /**
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

    protected abstract String objName();
}
