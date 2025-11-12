package org.networking;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import javafx.application.Platform;
import org.umlproject.UMLDocument;
import utility.ThreadUtility;

/**
 * This class handles incoming packets VIA 'client' or 'clienthandler'
 */
public class DocumentPacketHandler {
    /**
     * This function will validate a NetPacket and update the current UMLDocument
     * 
     */
    public synchronized static void handleDocumentPacket(NetworkPacket networkPacket)
    {
        if(networkPacket.packetType() != PacketType.FULL_DOCUMENT)
            return;//Cannot execute packet.
        
        try 
        {
            UMLDocument document = networkPacket.payloadToObject(UMLDocument.class);
            ThreadUtility.runOnMainThread(() -> {UMLDocument.getInstance().load(document);});
        } 
        catch (JsonSyntaxException e){}//Do nothing... Invalid conversion
    }
}
