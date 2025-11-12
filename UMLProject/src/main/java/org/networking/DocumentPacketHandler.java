package org.networking;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import javafx.application.Platform;
import org.umlproject.UMLDocument;

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
            Platform.runLater(() -> {
            UMLDocument.getInstance().load(document);//Properly load the new document...
            });
        } 
        catch (JsonSyntaxException e){}//Do nothing... Invalid conversion
    }
}
