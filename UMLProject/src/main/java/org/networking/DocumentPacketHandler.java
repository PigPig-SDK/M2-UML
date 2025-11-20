package org.networking;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import org.umlproject.DocumentState;
import org.umlproject.MainThreadDispatcher;
import org.umlproject.UMLClass;
import org.umlproject.UMLDiagramElement;
import org.umlproject.UMLDocument;
import org.umlproject.UMLRelationship;

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
            UMLRelationship clientRelationship = payload.relationship();
            if(clientRelationship == null) //Client has sent nonsense, Send back entire document!
            {
                if(client != null) client.sendEntireDocument();
                return;
            }
            
            //No source or destination found!
            UMLDiagramElement sourceElement = netElements.get(payload.source());
            UMLDiagramElement destinationElement = netElements.get(payload.destination());
            UMLDiagramElement relationshipElement = netElements.get(clientRelationship.networkId);
            NetworkPacket removeRelationshipPacket = NetworkPacket.objectToNetworkPacket(NetworkManager.getTick(), PacketType.OBJECT_DELETED, new PayloadRemoveObject(clientRelationship.networkId));
            
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
                if(serverRelationship.lastNetworkEditTime + 1 !=  clientRelationship.lastNetworkEditTime)
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
    /**
     * This handles the network packet for modifying UMLClasses...
     * @param client If null, than no reporting packet will be sent back.
     */
    public static void handleClassPacket(ClientHandler client, NetworkPacket networkPacket)
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
            UMLClass clientUMLClass = networkPacket.payloadToObject(UMLClass.class);//Packet view
            if(clientUMLClass == null || clientUMLClass.getClassName() == null)
            {
                //Ok, something is wrong with their view... refresh it...
                if(client != null) client.sendEntireDocument();
                return;
            }
            
            //Check for rename...
            UMLDiagramElement serverUMLDiagramElement = UMLDocument.getInstance().getAllNetIdElements().get(clientUMLClass.networkId);//Server view
            if(serverUMLDiagramElement instanceof UMLClass serverUMLClass)
            {
                if(serverUMLClass.lastNetworkEditTime + 1 !=  clientUMLClass.lastNetworkEditTime)
                {
                    if(client != null) sendValidClass(client, UMLDocument.getInstance().getClass(clientUMLClass.getClassName()));
                    return;//A more up-to-date version exists... Send that one.
                }
                //In this case, the incoming NETWORKID is equal but with a different name. We are now required to execute a rename before processing the packet...
                if(serverUMLClass.getClassName() != clientUMLClass.getClassName())//Names are not equal! Requires rename!
                    UMLDocument.executeActionUnderState(DocumentState.NETWORK_OPERATION, 
                            () -> UMLDocument.getInstance().renameClass(serverUMLClass.getClassName(), clientUMLClass.getClassName()));//Rename old ins to new ins.
            }
            else if(serverUMLDiagramElement != null)//Got an illegal item from list.
            {
                System.out.println("Got class packet for non UMLCLASS! \n > Sending entire document back to user");
                client.sendEntireDocument();
                return;
            }

            //May or not be renamed past this point.
            //Should not matter, we insert the packet in its rightful place.
            UMLDocument.executeActionUnderState(DocumentState.NETWORK_OPERATION, () -> {
                
                UMLDocument.getInstance().insertClass(clientUMLClass);//On client+server

                if(client != null)//Server reports back to users.
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
    /**
     * Sends back the current version of a UMLClass, this is called because the clients class is out of date...
     */
    private static void sendValidClass(ClientHandler client, UMLClass umlClass)
    {
        Server server = NetworkManager.getServerInstance();
        if(server == null)
            return;
        
        NetworkPacket netPacket = NetworkPacket.objectToNetworkPacket(NetworkManager.getTick(), PacketType.CLASS_EDIT, umlClass);
        server.sendMessageToClient(netPacket, client);
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
            if(ropl.idToRemove() == null) return;
            
            //Get object to be removed...
            UMLDiagramElement element = UMLDocument.getInstance().getAllNetIdElements().get(ropl.idToRemove());
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
    /**
     * * Purpouse: Resending packets to everyone except the server and the person who suggested the change.
     * 
     * For when the client has already made the change on their machine and the server happened to do the same thing.
     * This should be called if you want everyone outside of the standard client-server communication to see the update.
     */
    private static void handleSuccessfulPacket(ClientHandler client, NetworkPacket netPacket)
    {
        Server server = NetworkManager.getServerInstance();
        if(server == null) return;
        Set<ClientHandler> blacklist = new HashSet<>();
        blacklist.add(client);//Don't send back to owner.
        blacklist.add(NetworkManager.getServerInstance().getServerClient());
        server.sendMessageToAllClients(netPacket, blacklist);
    }
}
