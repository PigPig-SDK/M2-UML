package org.networking;

import com.google.gson.Gson;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.umlproject.UMLDocument;


public class PayloadRequestDocument{
    public final String hashString;
    
    public PayloadRequestDocument(String hashString)
    {
        this.hashString = hashString;
    }
    
    public static String documentToString()
    {
        try 
        {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            Gson gson = new Gson();
            String jsonString = gson.toJson(UMLDocument.getInstance());
            
            byte[] hash = messageDigest.digest(jsonString.getBytes());
            
            //https://www.baeldung.com/sha-256-hashing-java
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } 
        catch (NoSuchAlgorithmException e) 
        {
            System.err.println("SHA-256 NOT FOUND!");
        }
        return null;
    }
    
    /**
     * Generate a payload request document packet.
     * 
     * 
     * @return The generated packet, NULL if a hash generation error has happened.
     */
    public static NetworkPacket generatePacket()
    {
        String hash = documentToString();
        if(hash == null) return null;
        return NetworkPacket.objectToNetworkPacket(PacketType.REQUEST_DOCUMENT, new PayloadRequestDocument(hash));
    }
    /**
     * TO BE ONLY CALLED ON THE SERVER!
     * 
     * @param client
     * @param netPacket
     */
    public static void handlePacket(ClientHandler client, NetworkPacket netPacket)
    {
        //Validate
        if(netPacket == null || netPacket.packetType() != PacketType.REQUEST_DOCUMENT || netPacket.payload() == null) return;
        
        PayloadRequestDocument docRequest =  netPacket.payloadToObject(PayloadRequestDocument.class);
        if(documentToString().equals(docRequest.hashString))//Clients document is not wrong.
        {
            if(client == null) return;
            client.sendChatToClient("REFRESH: Your document is already up to date!");
        }
        else
        {
            client.sendChatToClient("REFRESH: Sent new document.");
            client.sendEntireDocument();
        }
        
    }
}
