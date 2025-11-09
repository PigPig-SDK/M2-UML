package org.networking;

public interface PacketMangerListener {
    
    public void onShutDown(PacketManager manager);
    
    public void onHitTimeout(PacketManager manager);
    
    public void onRecievePacket(PacketManager manager, NetworkPacket networkPacket);
}
