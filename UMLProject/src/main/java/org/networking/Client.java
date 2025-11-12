package org.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This class is the 'client' logic for handling server packets
 * This is a communication stream with a specified 'ClientHandler'.
 * 
 * Do not be confused, this is a clientside object! 
 * The server in most cases will contain a self client connection. 
 * This allows the server to interact with the client server architecture through a 'fair' control field.
 * Also simplifies the design of most interactions.
 * If there is an action you only want executed on non server clients, use 'isHosting' in control flow.
 */
public class Client extends SocketManager
{
    private boolean isHosting = false;
    private long tick = 0;
    private long lastHeartbeatTick = 0;
    private static final int heartbeatDelta = 20;
    
    public Client(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream, boolean isHost) throws IOException
    {
        super(socket, dataInputStream, dataOutputStream);
        this.isHosting = isHost;
    }
    /**
     * Dictates if our connection should close when handling an invalid packet.
     */
    @Override
    protected boolean closeOnInvalidPacket() {
        System.out.println("Disconnecting from host! Invalid packet length!");
        return true;
    }
    /**
     * Incoming packet management for the CLIENT
     */
    @Override protected void managePacket(NetworkPacket netPacket) {
        switch(netPacket.packetType())
        {
            case PacketType.DISCONNECT ->
            {
                System.out.println("Server suggested shutdown.");
                this.disconnect();
            }
            case PacketType.IDENTIFICATION ->{
                System.out.println("Got information... Ignoring it...");
            }
            case PacketType.HEARTBEAT ->{
                
                long delta = netPacket.sendTick() - lastHeartbeatTick;
                tick = netPacket.sendTick();
                if(delta >= heartbeatDelta)
                {
                    try{
                        sendNetworkPacket(new NetworkPacket(tick, PacketType.HEARTBEAT, null));
                        lastHeartbeatTick = tick;//Success. Update our last heartbeat time.
                    }
                    catch(IOException ex)
                    {
                        System.out.println("heartbeat sending exception..." + ex.getMessage());
                    }
                }
            }
            case PacketType.FULL_DOCUMENT ->
            {
                //Go for it bud...
                DocumentPacketHandler.handleDocumentPacket(netPacket);
            }
            default ->
            {
                System.out.println("-> " +netPacket.payload());
            }

        }
    }
    /**
     * The object name for debugging. TODO: REMOVE ME!
     */
    @Override
    protected String objectName() {
        return "client";
    }
    /**
     * Called when the main socket connection is successful.
     */
    @Override
    protected void onConnectionStarted() {
        UserIdentification myId = UserIdentification.generateAnonymousUserInfo();
        try
        {
            NetworkPacket netPacket = NetworkPacket.objectToNetworkPacket(0, PacketType.IDENTIFICATION, myId);
            this.sendNetworkPacket(netPacket);
        }
        catch(IOException ex)
        {
            System.out.println("Error initializing, could not send identification : " + ex);
        }
    }
    /**
     * Called on connection shutdown.
     */
    @Override
    public void disconnect()
    {
        NetworkManager.setClientNull();
        super.disconnect();
    }
}
