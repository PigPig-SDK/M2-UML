package org.networking;

import com.google.gson.JsonSyntaxException;
import javafx.geometry.Point2D;
import org.umlproject.DocumentState;
import org.umlproject.MainThreadDispatcher;
import org.umlproject.UMLClass;
import org.umlproject.UMLDocument;

/**
 * This packet will simply move a UML Element
 * 
 * For now, this only accounts for UMLClasses.
 */
public class PayloadMoveElement 
{
    public final String objectName;
    public final int x;
    public final int y;
    public final double width;
    
    public PayloadMoveElement(String objectName, int x, int y, double width)
    {
        this.objectName = objectName;
        this.x = x;
        this.y = y;
        this.width = width;
    }
    /**
     * Handles a UML Element movement packet
     * @param networkPacket with PacketType.ELEMENT_MOVED
     */
    public synchronized static void handleElementMovementPacket(NetworkPacket networkPacket)
    {
        //Not on main thread!
        
        if(networkPacket.packetType() != PacketType.ELEMENT_MOVED)
            return;//Cannot execute, send client back packet
        //Ok. now on main thread.
        MainThreadDispatcher.dispatcher.dispatch(() ->
        {
            try 
            {
                //Get the packets payload
                PayloadMoveElement payloadMoveElement = networkPacket.payloadToObject(PayloadMoveElement.class);
                //Find the class if its valid
                UMLClass umlc = UMLDocument.getInstance().getClass(payloadMoveElement.objectName);
                if(umlc == null) return;
                //We got a class, try to move it.
                UMLDocument.executeActionUnderState(DocumentState.NETWORK_OPERATION, () -> {
                                                        umlc.setLocation(new Point2D(payloadMoveElement.x,payloadMoveElement.y), true);
                                                        umlc.setWidth(payloadMoveElement.width, false);
                                                    });
            } 
            catch (JsonSyntaxException e){}
        });
    }
}

