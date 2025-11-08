package org.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;


public class Client extends PacketManager
{
    private boolean isHosting = false;
    
    public Client(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream, boolean isHost) 
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
                running = false;
            }
            default ->
            {
                System.out.println("Got message : " + netPacket.jsonPayload());
            }

        }
    }

    @Override
    protected String objName() {
        return "client";
    }
}
