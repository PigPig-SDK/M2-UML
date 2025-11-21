package org.networking;

import com.google.gson.JsonSyntaxException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.networking.DocumentPacketHandler.handleClassPacket;
import org.umlproject.MainThreadDispatcher;
import org.umlproject.UMLDocument;


/**
 * This class is the server side management of the client.
 * This is a communication stream with a specified 'Client'.
 * 
 * Do not be confused. This is only a server side object!
 */
public class ClientHandler extends SocketManager
{
    private UserIdentification userID = new UserIdentification("Unknown", false);
    private boolean firstID = true;
    private long lastHeartbeatTime = 0;
    private static final long TIMEOUT = 5; // In seconds
    
    private UUID clientID = UUID.randomUUID();
    
    public ClientHandler(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        super(socket, dataInputStream, dataOutputStream);
        lastHeartbeatTime = System.nanoTime();
    }
    /**
     * Returns true if the clients heartbeat has expired.
     */
    public boolean hasTimedOut()
    {
        long delta = getHeartbeatDelta();
        if(delta >= TimeUnit.SECONDS.toNanos(TIMEOUT))
        {
            return true;
        }
        return false;
    }
    /**
     * Dictates if our connection should close when handling an invalid packet.
     */
    @Override
    protected boolean closeOnInvalidPacket() {
        //If we get an invalid packet, we stop sending to the client...
        return true;
    }
    /**
     * Incoming packet management for the CLIENTHANDLER
     */
    @Override
    protected void managePacket(final NetworkPacket netPacket) {
        //Packets are time bound...
        if(netPacket.sendTick() > NetworkManager.getTick())
            return;
        
        switch(netPacket.packetType())
        {
            case DISCONNECT ->
            {
                this.disconnect();
            }
            case MESSAGE ->
            {
                //Send message back to all clients...
                String message = userID.userName + " : " + netPacket.payload();
                NetworkPacket overrideNetPacket = new NetworkPacket(0, PacketType.MESSAGE, message);
                NetworkManager.getServerInstance().sendMessageToAllClients(overrideNetPacket);
            }
            case HEARTBEAT ->
            {
                //Got client heartbeat... Update their time.
                lastHeartbeatTime = System.nanoTime();
            }
            case MOUSE_UPDATE->{
                try {
                    Server server = NetworkManager.getServerInstance();
                    if(server == null)
                        return;
                    
                    PayloadNetworkMouse payload = netPacket.payloadToObject(PayloadNetworkMouse.class);
                    if(payload.getUsername() != null && payload.getUserId() != null)
                    {
                        System.err.println("CLIENT GAVE INVALID MOUSE PACKET. THROWING AWAY!");
                        return;
                    }
                    //Populate packet with useful stuff..
                    payload.setUsername(userID.userName);
                    payload.setUserId(clientID);
                    NetworkPacket tempNetPacket = NetworkPacket.objectToNetworkPacket(NetworkManager.getTick(), PacketType.MOUSE_UPDATE, payload);//Modified payload loaded!
                    //Sending...
                    Set<ClientHandler> blacklist = server.getAllTerminalUsers();
                    blacklist.add(this);//Do not send back to our client.
                    server.sendMessageToAllClients(tempNetPacket, blacklist);
                } 
                catch (JsonSyntaxException e) {
                    
                }
            }
            case IDENTIFICATION ->
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
                catch(JsonSyntaxException e)
                {
                    System.out.println("User gave us bogus...");
                }
            }
            case CLASS_EDIT ->
            {
                MainThreadDispatcher.dispatcher.dispatch(() -> DocumentPacketHandler.handleClassPacket(this, netPacket));
            }
            case RELATIONSHIP_EDIT ->
            {
                MainThreadDispatcher.dispatcher.dispatch(() -> DocumentPacketHandler.handleRelationshipPacket(this, netPacket));
            }
            case OBJECT_DELETED ->
            {
                MainThreadDispatcher.dispatcher.dispatch(()-> DocumentPacketHandler.handleRemovePacket(this, netPacket));
            }
            case ELEMENT_MOVED ->
            {
                //Server has suggested we move something...
                Server server = NetworkManager.getServerInstance();
                if(server == null) return;
                //Send to everyone besides the speaking client...
                Set<ClientHandler> blacklist = new HashSet<>();
                blacklist.add(this);
                server.sendMessageToAllClients(netPacket, blacklist);
            }
            default ->
            {
                System.out.println("Got message : " + netPacket.payload());
            }

        }
    }
    /**
     * The object name for debugging. TODO: REMOVE ME!
     */
    @Override
    protected String objectName() {
        return "ClientHandler";
    }
    /**
     * Called when the main socket connection is successful.
     */
    @Override
    protected void onConnectionStarted() {
        //Inform new users of the connection.
        NetworkManager.getServerInstance().sendMessageToAllClients(new NetworkPacket(NetworkManager.getTick(),PacketType.MESSAGE, "A new user is connecting..."));
        sendEntireDocument();
    }
    /**
     * Will attempt to send the entire UMLDocument to the client.
     */
    protected void sendEntireDocument()
    {
        sendNetworkPacket(Server.generateDocumentPacket());
    }
    /**
     * Called on connection shutdown.
     */
    @Override
    public void disconnect() {
        super.disconnect();
    }
    /**
     * Gets the heartbeat delta
     */
    public long getHeartbeatDelta()
    {
        return System.nanoTime() - lastHeartbeatTime;
    }
    /**
     * Returns the current userID of this client connection
     */
    public UserIdentification getUserID()
    {
        return userID;
    }
}
