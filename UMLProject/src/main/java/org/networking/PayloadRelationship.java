package org.networking;

import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.UUID;
import org.umlproject.DocumentState;
import org.umlproject.UMLClass;
import org.umlproject.UMLDiagramElement;
import org.umlproject.UMLDocument;
import org.umlproject.UMLRelationship;
/**
 * This record holds the generic payload data for UMLRelationships.
 * 
 * The SOURCE and DESTINATION are given as a form of validation.
 * 
 * 
 */
public class PayloadRelationship extends PayloadGeneric
{ 
    public final UUID source;
    public final UUID destination;
    public final UMLRelationship relationship;
    public PayloadRelationship(UUID source, UUID destination, UMLRelationship relationship)
    {
        this.source = source;
        this.destination = destination;
        this.relationship = relationship;
    }
    /**
    * This handles the network packet for modifying UMLRelationships...
    * 
    * Given a PayloadRelationship wrapped in a Netpacket we will replace/add the element.
    * 
    * This function matches the network id's to their associated objects.
    * It then solves/matches the relationship to our local representation.
    * These steps are taken to solve 'race conditions' of class name changes.
    * Realistically someone oughta refactor the UMLRelationships to only store netID's, 
    * but this technical debt will fall on nobody anytime soon....
    * 
    * @param client If null, than no reporting packet will be sent back.
    */
    public static void handleRelationshipPacket(ClientHandler client, NetworkPacket networkPacket)
    {
        ///
        ///
        //////////////////////////
        //  On main thread!!!!  //
        //////////////////////////
        ///
        ///
        if(networkPacket == null) return;
        try
        {
            //Validate...
            PayloadRelationship payload = networkPacket.payloadToObject(PayloadRelationship.class);//Packet view
            if(payload == null)
            {
                if(client != null) client.sendEntireDocument();//I've no idea where the client is wrong.
                return;
            }
            final Map<UUID,UMLDiagramElement> netElements = UMLDocument.getInstance().getAllNetIdElements();
            UMLRelationship clientRelationship = payload.relationship;
            if(clientRelationship == null) //Client has sent nonsense, Send back entire document!
            {
                if(client != null) client.sendEntireDocument();
                return;
            }
            
            //No source or destination found!
            UMLDiagramElement sourceElement = netElements.get(payload.source);
            UMLDiagramElement destinationElement = netElements.get(payload.destination);
            UMLDiagramElement relationshipElement = netElements.get(clientRelationship.networkId);
            NetworkPacket removeRelationshipPacket = NetworkPacket.objectToNetworkPacket(PacketType.OBJECT_DELETED, new PayloadRemoveObject(clientRelationship.networkId));
            
            //Something is seriously wrong with what they are telling us.
            if(sourceElement == null || destinationElement == null || !(sourceElement instanceof UMLClass) || !(destinationElement instanceof UMLClass))
            {
                //Suggest that the user delete the relationship on their end...
                if(client != null) 
                {
                    
                    if(!(relationshipElement instanceof UMLRelationship))//Client suggests we replace something else! Find refuge in a new document client!
                    {
                        client.sendEntireDocument();
                    }
                    else //Suggest the client remove the object.
                    {
                        client.sendNetworkPacket(removeRelationshipPacket);
                    }
                }
                return;
            }
            ///
            //Payload is valid! Replace!
            ///
            UMLRelationship serverRelationship = (UMLRelationship)relationshipElement;
            UMLDocument.executeActionUnderState(DocumentState.NETWORK_OPERATION, ()->{
                if(serverRelationship != null && (serverRelationship.lastNetworkEditTime + 1 !=  clientRelationship.lastNetworkEditTime))
                {
                    if(client != null) client.sendNetworkPacket(removeRelationshipPacket);
                    return;//A more up-to-date version exists... Send that one.
                }
                UMLDocument.getInstance().insertRelationship(clientRelationship);
                if(client != null)//Success... inform all clients of this new update.
                {
                    handleSuccessfulPacket(client, networkPacket);
                }
            });
        }
        catch (JsonSyntaxException e) {
            
            if(client != null) client.sendEntireDocument();
            return;
        }
    }
}
