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
    UserIdentification userID = null;
    
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
                System.out.println("Disconnecting client... Killing listner.");
                this.disconnect();
            }
            case PacketType.MESSAGE ->
            {
                //Send message back to all clients...
                String message = ((userID == null)? "Unknown" : userID.userName) + " : " + netPacket.payload();
                NetworkPacket overrideNetPacket = new NetworkPacket(0, PacketType.MESSAGE, message);
                NetworkManager.getServerInstance().sendMessageToAllClients(overrideNetPacket);
            }
            case PacketType.IDENTIFICATION ->
            {
                //If the user has no ID, we are accepting one.
                try
                {
                    UserIdentification testId = netPacket.payloadToObject(UserIdentification.class);
                    if(!UserIdentification.isValid(testId)) return;
                    
                    boolean isFirstID = this.userID == null;//If this is the first time the user has set their id.
                    
                    this.userID = testId;
                    if(isFirstID)
                    {
                        NetworkPacket netpacket = new NetworkPacket(0,PacketType.MESSAGE, this.userID.userName + " has connected.");
                        NetworkManager.getServerInstance().sendMessageToAllClients(netpacket);
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
}
