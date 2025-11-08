package org.networking;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public record NetworkPacket(long sendTick, PacketType packetType, String jsonPayload) {
    
    /**
     * Converts a given json string to an object.
     */
    public static NetworkPacket jsonToPacket(String jsonMessage)
    {
        Gson gson = new Gson();
        NetworkPacket data = gson.fromJson(jsonMessage, NetworkPacket.class);
        return data;
    }
    /**
     * Converts the object to a json string
     */
    public String packetToJson()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
