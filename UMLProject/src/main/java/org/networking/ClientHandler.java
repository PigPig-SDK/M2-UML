package org.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * This class is the server side management of the client.
 * This is a communication stream with a specified 'client' (Not the client class, do not be confused).
 */
public class ClientHandler extends PacketManager
{

    public ClientHandler(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        super(socket, dataInputStream, dataOutputStream);
    }

    @Override
    protected boolean closeOnInvalidPacket() {
        //If we get an invalid packet, we stop sending to the client...
        return true;
    }

    @Override
    protected void managePacket(NetworkPacket netPacket) {
        switch(netPacket.packetType())
        {
            case PacketType.DISCONNECT ->
            {
                System.out.println("Disconnecting client...");
                running = false;
            }
            case PacketType.MESSAGE ->
            {
                //Send message back to all clients...
                NetworkManager.getServerInstance().sendMessageToAllClients(netPacket);
            }
            default ->
            {
                System.out.println("Got message : " + netPacket.jsonPayload());
            }

        }
    }

    @Override
    protected String objName() {
        return "ClientHandler";
    }
}
