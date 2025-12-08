package org.networking;

import java.util.UUID;


public class PayloadUserDisconnect {
    public final UUID userID;
    
    public PayloadUserDisconnect(UUID userID)
    {
        this.userID = userID;
    }
    public static void handlePacket(NetworkPacket networkPacket)
    {
        if(networkPacket == null || networkPacket.packetType() != PacketType.USER_DISCONNECT || networkPacket.payload() == null) return;
        
        PayloadUserDisconnect disconnectMessage = networkPacket.payloadToObject(PayloadUserDisconnect.class);
        NetworkManager.listeners.forEach((e)->e.onClientDisconnect(disconnectMessage.userID));
    }
}
