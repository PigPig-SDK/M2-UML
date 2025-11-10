package org.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This class is the server side management of the client.
 * This is a communication stream with a specified 'client' (Not the client class, do not be confused).
 */
public class ClientHandler extends PacketManager
{
    UserIdentification userID = new UserIdentification("Unknown");
    private boolean firstID = true;
    private long lastHeartbeatTime = 0;
    private static final long TIMEOUT = 5; // In seconds
    
    public ClientHandler(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        super(socket, dataInputStream, dataOutputStream);
        lastHeartbeatTime = System.nanoTime();
    }
    /**
     * Returns true if the clients heartbeat has expired.
     */
    public boolean hasTimedOut()
    {
        long delta = System.nanoTime() - lastHeartbeatTime;
        if(delta >= TIMEOUT * 1000000000)//Multiply seconds -> Nano-Seconds
        {
            return true;
        }
        return false;
    }
    
    @Override
    protected boolean closeOnInvalidPacket() {
        //If we get an invalid packet, we stop sending to the client...
        return true;
    }

    @Override
    protected void managePacket(NetworkPacket netPacket) {
        //Packets are time bound...
        if(netPacket.sendTick() > NetworkManager.getServerTick())
            return;
        
        switch(netPacket.packetType())
        {
            case PacketType.DISCONNECT ->
            {
                this.disconnect();
            }
            case PacketType.MESSAGE ->
            {
                //Send message back to all clients...
                String message = userID.userName + " : " + netPacket.payload();
                NetworkPacket overrideNetPacket = new NetworkPacket(0, PacketType.MESSAGE, message);
                NetworkManager.getServerInstance().sendMessageToAllClients(overrideNetPacket);
            }
            case PacketType.HEARTBEAT ->
            {
                //Got client heartbeat... Update their time.
                lastHeartbeatTime = System.nanoTime();
            }
            case PacketType.IDENTIFICATION ->
            {
                //If the user has no ID, we are accepting one.
                try
                {
                    UserIdentification testId = netPacket.payloadToObject(UserIdentification.class);
                    if(!UserIdentification.isValid(testId)) return;
                    this.userID = testId;
                    if(firstID)
                    {
                        NetworkPacket netpacket = new NetworkPacket(0,PacketType.MESSAGE, this.userID.userName + " has connected.");
                        NetworkManager.getServerInstance().sendMessageToAllClients(netpacket);
                        firstID = false;
                    }
                }
                catch(IOException e)
                {
                    System.out.println("User gave us bogus...");
                }
            }
            default ->
            {
                System.out.println("Got message : " + netPacket.payload());
            }

        }
    }

    @Override
    protected String objectName() {
        return "ClientHandler";
    }

    @Override
    protected void onConnectionStarted() {
        //Inform new users of the connection.
        NetworkManager.getServerInstance().sendMessageToAllClients(new NetworkPacket(0,PacketType.MESSAGE, "A new user is connecting..."));
    }

    @Override
    public void disconnect() {
        super.disconnect();
    }
    
}
