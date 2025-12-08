package org.networking;

import java.util.HashSet;
import java.util.Set;
import org.umlproject.UMLClass;


public class PayloadGeneric {
    /**
     * * Purpouse: Resending packets to everyone except the server and the person who suggested the change.
     * 
     * For when the client has already made the change on their machine and the server happened to do the same thing.
     * This should be called if you want everyone outside of the standard client-server communication to see the update.
     */
    protected static void handleSuccessfulPacket(ClientHandler client, NetworkPacket netPacket)
    {
        Server server = NetworkManager.getServerInstance();
        if(server == null) return;
        Set<ClientHandler> blacklist = new HashSet<>();
        blacklist.add(client);//Don't send back to owner.
        blacklist.add(NetworkManager.getServerInstance().getServerClient());
        server.sendMessageToAllClients(netPacket, blacklist);
    }
    /**
    * Sends back the current version of a UMLClass, this is called because the clients class is out of date...
    */
    protected static void sendValidClass(ClientHandler client, UMLClass umlClass)
    {
        Server server = NetworkManager.getServerInstance();
        if(server == null)
            return;
        
        NetworkPacket netPacket = NetworkPacket.objectToNetworkPacket(PacketType.CLASS_EDIT, umlClass);
        server.sendMessageToClient(netPacket, client);
    }
}
