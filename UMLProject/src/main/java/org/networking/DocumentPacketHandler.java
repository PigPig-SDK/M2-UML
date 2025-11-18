package org.networking;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
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
     * Handles a UML Element movement edit packet
     * @param networkPacket with PacketType.CLASS_MODIFY or PacketType.RELATIONSHIP_MODIFY
     */
    public synchronized static void handleElementModified(ClientHandler client, NetworkPacket networkPacket)
    {
        //Not a valid packet type...
        if(!(networkPacket.packetType() == PacketType.RELATIONSHIP_EDIT || networkPacket.packetType() == PacketType.CLASS_EDIT))
            return;//Cannot execute, send client back packet
        
        switch (networkPacket.packetType()) {
            case PacketType.RELATIONSHIP_EDIT -> {
                System.out.println("Erm... aschually bazinga bazinga.");
            }
            case PacketType.CLASS_EDIT -> {
                MainThreadDispatcher.dispatcher.dispatch(() -> handleClassPacket(client, networkPacket));
            }
            default ->
            {
                System.out.println("Unsupported type... Please implement me if you are going to allow the flow control.");
            }
        }
    }
    /**
     * This handles the network packet for modifying UMLClasses...
     * @param client If null, than no reporting packet will be sent back.
     */
    private static void handleClassPacket(ClientHandler client, NetworkPacket networkPacket)
    {
        ///
        ///
        //////////////////////////
        //  On main thread!!!!  //
        //////////////////////////
        ///
        ///
        if(networkPacket == null) return;
        //Run on main thread.
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
            UMLDiagramElement serverUMLDiagramElement = UMLDocument.getInstance().getAllDiagramElements().get(clientUMLClass.networkId);//Server view
            if(serverUMLDiagramElement instanceof UMLClass serverUMLClass)
            {
                if(serverUMLClass.lastNetworkEditTime + 1 !=  clientUMLClass.lastNetworkEditTime)
                {
                    if(client != null) client.sendEntireDocument();
                    return;//A more up-to-date version exists
                }
                
                //In this case, the incoming NETWORKID is equal but with a different name. We are now required to execute a rename before processing the packet...
                if(!serverUMLClass.getClassName().equals(clientUMLClass.getClassName()))//Names are not equal! Requires rename!
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
                
                boolean switchWorked = UMLDocument.getInstance().addClass(clientUMLClass);//On client+server

                if(client != null)//Server reports back to users.
                {
                    if(!switchWorked)//Switch failed... Send the client back the latest version...
                        sendValidClass(client, UMLDocument.getInstance().getClass(clientUMLClass.getClassName()));//Never happens... TODO REMOVE!
                    else//Send update back to everyone...
                    {
                        Server server = NetworkManager.getServerInstance();
                        if(server == null) return;//Safety first...

                        //Avoid sending back to server AND the client who sent it.
                        Set<ClientHandler> clients =  new HashSet<>();
                        clients.add(NetworkManager.getServerInstance().getServerClient());
                        clients.add(client);
                        server.sendMessageToAllClients(networkPacket, clients);//Update. Send back to all.
                    }
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
}
