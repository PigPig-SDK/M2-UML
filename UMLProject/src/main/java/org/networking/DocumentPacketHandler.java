package org.networking;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import org.umlproject.DocumentState;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;
import utility.ThreadUtility;

/**
 * This class handles incoming packets VIA 'client' or 'clienthandler'
 */
public class DocumentPacketHandler {
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
            ThreadUtility.runOnMainThread(() -> {UMLDocument.getInstance().load(document);});
        } 
        catch (JsonSyntaxException e){}//Do nothing... Invalid conversion
    }
    /**
     * Handles a UML Element movement packet
     * @param client The client who is suggesting the packet, 
     *              NULL overrides and executes the packet no questions asked.
     * @param networkPacket with PacketType.ELEMENT_MOVED
     * @return True if the packet is accepted, False rejected
     */
    public synchronized static boolean handleElementMovementPacket(Client client, NetworkPacket networkPacket)
    {
        if(networkPacket.packetType() != PacketType.ELEMENT_MOVED)
            return false;//Cannot execute, send client back packet
        if(client == null)
            return false;
        
        final String strFalse = "false";
        AtomicReference<String> ref = new AtomicReference<>("javaJank");//fuck java fuck java fuck java
        ThreadUtility.runOnMainThread(() ->
        {
            try 
            {
                //Get the packets payload
                PayloadMoveElement payloadMoveElement = networkPacket.payloadToObject(PayloadMoveElement.class);
                //Find the class if its valid
                UMLClass umlc = UMLDocument.getInstance().getClass(payloadMoveElement.objectName());
                if(umlc == null) ref.set(strFalse);//No class found, Failure!
                //We got a class, try to move it.
                UMLDocument.executeActionUnderState( DocumentState.NETWORK_OPERATION, 
                                                    () -> umlc.setLocation(new Point2D(payloadMoveElement.x(),payloadMoveElement.y()), true));
            } 
            catch (JsonSyntaxException e){
                ref.set(strFalse);
            }
        });
        return !ref.get().equals(strFalse);//Fucking so shit.
    }
}
