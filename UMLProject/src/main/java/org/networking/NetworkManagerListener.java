package org.networking;

import java.util.UUID;


public interface NetworkManagerListener {
    public void onNetworkConnect();
    
    public void onNetworkDisconnect();
    
    public void onClientDisconnect(UUID clientId);
}
