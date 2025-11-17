package org.networking;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This is the standard for our editors 'network packet'
 * 
 * sendTick : The server tick which we sent data.
 * packetType : The type of payload the other end should expect.
 * payload : The payload. Expected to be using 'json objects', 'string', or 'null'.
 */
public record NetworkPacket(long sendTick, PacketType packetType, String payload)
{
    
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
    /**
     * Converts the current payload to the specified object
     */
    public <T> T payloadToObject(Class<T> classType) throws JsonSyntaxException
    {
        Gson gson = new Gson();
        return gson.fromJson(payload, classType);
    }
    /**
     * Creates a network packet with a wrapped payload.
     */
    public static <T> NetworkPacket objectToNetworkPacket(long sendTick, PacketType packetType, T payload) throws JsonSyntaxException
    {
        if(payload == null)
            return new NetworkPacket(sendTick, packetType, null);
        
        Gson gson = new Gson();
        String payloadString = gson.toJson(payload);
        return new NetworkPacket(sendTick, packetType, payloadString);
    }
}
