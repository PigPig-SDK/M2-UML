package org.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class Client extends PacketManager
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

    @Override
    protected boolean closeOnInvalidPacket() {
        System.out.println("Disconnecting from host! Invalid packet length!");
        return true;
    }

    @Override
    protected void managePacket(NetworkPacket netPacket) {
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
            default ->
            {
                System.out.println("-> " +netPacket.payload());
            }

        }
    }

    @Override
    protected String objectName() {
        return "client";
    }

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
    @Override
    public void disconnect()
    {
        NetworkManager.setClientNull();
        super.disconnect();
    }
}
