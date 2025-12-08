package org.networking;

import com.google.gson.JsonSyntaxException;
import java.util.UUID;
import org.umlproject.DocumentState;
import org.umlproject.UMLClass;
import org.umlproject.UMLDiagramElement;
import org.umlproject.UMLDocument;
import org.umlproject.UMLRelationship;

public class PayloadRemoveObject extends PayloadGeneric
{
    public final UUID idToRemove;
    
    public PayloadRemoveObject(UUID idToRemove)
    {
        this.idToRemove = idToRemove;
    }
    
    /**
     * Removes a network object from the document
     * @param client The client the packet came from. If null, than server sent the packet and we are the client.
     * @param netPacket The packet to be processed
     */
    public static void handleRemovePacket(ClientHandler client, NetworkPacket netPacket)
    {
        ///
        ///
        //////////////////////////
        //  On main thread!!!!  //
        //////////////////////////
        ///
        ///
        ///
        try
        {
            if(netPacket == null)
                return;
            PayloadRemoveObject ropl = netPacket.payloadToObject(PayloadRemoveObject.class);
            if(ropl == null) return;
            if(ropl.idToRemove == null) return;
            
            //Get object to be removed...
            UMLDiagramElement element = UMLDocument.getInstance().getAllNetIdElements().get(ropl.idToRemove);
            boolean successfulRemoval = false;
            if(element instanceof UMLClass classobj)
            {
                successfulRemoval = UMLDocument.executeActionUnderState(DocumentState.NETWORK_OPERATION, () ->{
                    return UMLDocument.getInstance().removeClass(classobj.getClassName(), true) != null;//Something was removed.
                });

            }
            else if(element instanceof UMLRelationship rObject)
            {
                successfulRemoval = UMLDocument.executeActionUnderState(DocumentState.NETWORK_OPERATION, () ->{
                    return UMLDocument.getInstance().removeRelationship(rObject.getSourceName(), rObject.getDestinationName());
                });
            }
            
            if(successfulRemoval)
            {
                handleSuccessfulPacket(client, netPacket);
                return;
            }
        }
        catch(JsonSyntaxException e)
        {
            System.out.println("Delete object : Json malformed! " + e.getMessage());
        }
        //Fallback! Send whole document back to client...
        if(client != null) client.sendEntireDocument();
    }
}
