package org.networking;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import org.umlproject.DocumentState;
import org.umlproject.MainThreadDispatcher;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;

/**
 * This class handles incoming packets VIA 'client' or 'clienthandler'
 */
public class DocumentPacketHandler {
    
    /**
     * The thread lock for UMLDocument updates...
     */
    public static Object documentlock = new Object();
    
    
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
    /**
     * Handles a UML Element movement packet
     * @param networkPacket with PacketType.ELEMENT_MOVED
     */
    public synchronized static void handleElementMovementPacket(NetworkPacket networkPacket)
    {
        if(networkPacket.packetType() != PacketType.ELEMENT_MOVED)
            return;//Cannot execute, send client back packet
        
        MainThreadDispatcher.dispatcher.dispatch(() ->
        {
            try 
            {
                //Get the packets payload
                PayloadMoveElement payloadMoveElement = networkPacket.payloadToObject(PayloadMoveElement.class);
                //Find the class if its valid
                UMLClass umlc = UMLDocument.getInstance().getClass(payloadMoveElement.objectName());
                if(umlc == null) return;
                //We got a class, try to move it.
                UMLDocument.executeActionUnderState(DocumentState.NETWORK_OPERATION,
                                                    () -> umlc.setLocation(new Point2D(payloadMoveElement.x(),payloadMoveElement.y()), true));
            } 
            catch (JsonSyntaxException e){}
        });
    }
}
