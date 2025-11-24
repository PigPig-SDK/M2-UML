package org.networking;

import com.google.gson.JsonSyntaxException;
import org.umlproject.MainThreadDispatcher;
import org.umlproject.UMLDocument;

/**
 * This class handles incoming packets VIA 'client' or 'clienthandler'
 */
public class PayloadDocument extends PayloadGeneric
{
    public static NetworkPacket generateDocumentPacket()
    {
        return NetworkPacket.objectToNetworkPacket(PacketType.FULL_DOCUMENT, UMLDocument.getInstance());
    }
    /**
     * This function will validate a NetPacket and update the current UMLDocument
     * 
     * @param networkPacket with PacketType.FULL_DOCUMENT set.
     */
    public synchronized static void handleDocumentPacket(NetworkPacket networkPacket)
    {
        if(networkPacket.packetType() != PacketType.FULL_DOCUMENT)
            return;//Cannot execute packet.
        try 
        {
            UMLDocument document = networkPacket.payloadToObject(UMLDocument.class);
            MainThreadDispatcher.dispatcher.dispatch(() -> {UMLDocument.getInstance().load(document);});
        } 
        catch (JsonSyntaxException e){}//Do nothing... Invalid conversion
    }

}
