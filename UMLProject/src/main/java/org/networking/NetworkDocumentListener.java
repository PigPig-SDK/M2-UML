package org.networking;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javafx.geometry.Rectangle2D;
import org.umlproject.DiagramElementListener;
import org.umlproject.DocumentListner;
import org.umlproject.DocumentState;
import org.umlproject.UMLClass;
import org.umlproject.UMLDiagramElement;
import org.umlproject.UMLDocument;
import org.umlproject.UMLRelationship;
import org.umlproject.UndoRedoManager;

/**
 * This class acts as a clients 'DOCUMENT LISTENER'
 * 
 * When updates happen, the client sends them to the server.
 */
public class NetworkDocumentListener implements DiagramElementListener, DocumentListner {

    private static final Set<DocumentState> invalidDocumentStates = 
            Set.of( DocumentState.FILE_LOADING, 
                    DocumentState.CLONING, 
                    DocumentState.MEMENTO_STATE_RESET,
                    DocumentState.NETWORK_OPERATION,
                    DocumentState.MASS_OPERATION_RENAME);
    
    private static NetworkDocumentListener instance;
    
    private static final double dragSendDelay = 0.02;//~50 times a second
    private static long dragLastSent = 0;
    
    /**
     * Get the memento of instance listener.
     * NOTE: This can be null! That is because setupListener() is expected to be called.
     */
    public NetworkDocumentListener getInstance()
    {
        //Null is an intended return value.
        return instance;
    }
    /**
     * Setup and bind the listener.
     */
    public static void setupListener()
    {
        if(instance != null)
            return;
        
        instance = new NetworkDocumentListener();
        UMLDiagramElement.globalListeners.add(instance);
        UMLDocument.documentListners.add(instance);
    }
    /**
     * Unbind the listener
     */
    public static void shutdownListener()
    {
        if(instance == null)
            return;
        UMLDiagramElement.globalListeners.remove(instance);
        UMLDocument.documentListners.remove(instance);
        instance = null;
    }
    /**
     * Sends the new location for a UMLClass.
     */
    private void sendClassTranslation(UMLClass objectClass)
    {
        if(invalidDocumentStates.contains(UMLDocument.getDocumentState())) return;
        
        Client client = NetworkManager.getClientInstance();//Our local client.
        if(client == null)
        {
            client.disconnect();
            return;
        }
        
        //Construct packet
        NetworkPacket networkPacket = NetworkPacket.objectToNetworkPacket(
                NetworkManager.getTick(), 
                PacketType.ELEMENT_MOVED, 
                new PayloadMoveElement( objectClass.getClassName(),
                                        (int)objectClass.getLocation().getX(), 
                                        (int)objectClass.getLocation().getY()));
        client.sendNetworkPacket(networkPacket);
    }
    /**
     * Sends the updated class to the server for validation
     */
    private void sendClassUpdate(UMLClass objectClass)
    {
        if(invalidDocumentStates.contains(UMLDocument.getDocumentState())) return;
        
        objectClass.lastNetworkEditTime++;//Increment last edit time...
        NetworkPacket networkPacket = NetworkPacket.objectToNetworkPacket(NetworkManager.getTick(), PacketType.CLASS_EDIT, objectClass);
        
        if(NetworkManager.isHosting())//Bypass communication. Enforce everyone to use this packet.
        {
            Set serverAvoidance = new HashSet<ClientHandler>();
            serverAvoidance.add(NetworkManager.getServerInstance().getServerClient());
            NetworkManager.getServerInstance().sendMessageToAllClients(networkPacket, serverAvoidance);
        }
        else //I am a client, send through my connection...
        {
            NetworkManager.getClientInstance().sendNetworkPacket(networkPacket);  
        }
    }
    /**
     * Sends the updated relationship to the server for validation
     */
    private void sendRelationshipUpdate(UMLRelationship objectLRelationship)
    {
        if(invalidDocumentStates.contains(UMLDocument.getDocumentState())) return;
        
        objectLRelationship.lastNetworkEditTime++;//Increment last edit time...
        UMLClass source = UMLDocument.getInstance().getClass(objectLRelationship.getSourceName());
        UMLClass destination = UMLDocument.getInstance().getClass(objectLRelationship.getDestinationName());
        if(source == null || destination == null) return;
        
        PayloadRelationship payloadRelationship = new PayloadRelationship(source.networkId, destination.networkId, objectLRelationship);
        NetworkPacket networkPacket = NetworkPacket.objectToNetworkPacket(NetworkManager.getTick(), PacketType.RELATIONSHIP_EDIT, payloadRelationship);
        
        if(NetworkManager.isHosting())//Bypass communication. Enforce everyone to use this packet.
        {
            Set serverAvoidance = new HashSet<ClientHandler>();
            serverAvoidance.add(NetworkManager.getServerInstance().getServerClient());
            NetworkManager.getServerInstance().sendMessageToAllClients(networkPacket, serverAvoidance);
        }
        else //I am a client, send through my connection...
        {
            NetworkManager.getClientInstance().sendNetworkPacket(networkPacket);
        }
    }
    
    /*---------------------------[ Listeners ]---------------------------*/
    @Override public void update(Object desiredElement) {
        
        if(invalidDocumentStates.contains(UMLDocument.getDocumentState())) return;
        switch(desiredElement)
        {
            case UMLClass umlClass -> sendClassUpdate(umlClass);
            case UMLRelationship umlRelationship -> sendRelationshipUpdate(umlRelationship);

            default ->
            {
                System.out.println("Got update from unknown source!");
            }
        }
    }
    @Override public void updateLocation(Object desiredElement) {
        
        // This code checks for 'mid dragging' updates.
        //We still send 'mid dragging' updates, just at a slower rate than what javafx gives.
        if(UMLDocument.getDocumentState().equals(DocumentState.SILENT_MOVEMENT))
        {
            long delta = System.nanoTime() - dragLastSent;
            double refireDelay = TimeUnit.SECONDS.toNanos(1) * dragSendDelay;//toNanos dosnt take 'double', jank workaround.
            
            if(delta < refireDelay) return;//Does not quality for sending network packet.
            dragLastSent = System.nanoTime();
        }
        
        switch(desiredElement)
        {
            case UMLClass umlClass -> sendClassTranslation(umlClass);
            default ->
            {
                System.out.println("Got location update from invalid source!");
            }
        }
    }
    @Override public void onClassAdded(UMLClass umlClass) { sendClassUpdate(umlClass); }
    @Override public void onRelationshipAdded(UMLRelationship umlRelationship) { sendRelationshipUpdate(umlRelationship); }
    
    @Override public void onClassRemove(UMLClass umlClass) {
        netRemoveDiagramElement(umlClass);
    }
    @Override public void onRelationshipRemove(UMLRelationship umlClass) {
        netRemoveDiagramElement(umlClass);
    }
    private void netRemoveDiagramElement(UMLDiagramElement element)
    {
        if(invalidDocumentStates.contains(UMLDocument.getDocumentState())) return;
        
        NetworkPacket networkPacket = NetworkPacket.objectToNetworkPacket(NetworkManager.getTick(), PacketType.OBJECT_DELETED, new PayloadRemoveObject(element.networkId));
        
        if(NetworkManager.isHosting())//Bypass communication. Enforce everyone to use this packet.
        {
            Set serverAvoidance = new HashSet<ClientHandler>();
            serverAvoidance.add(NetworkManager.getServerInstance().getServerClient());
            NetworkManager.getServerInstance().sendMessageToAllClients(networkPacket, serverAvoidance);
        }
        else //I am a client, send through my connection...
        {
            NetworkManager.getClientInstance().sendNetworkPacket(networkPacket);
        }
    }
    
    
    /* Those no good do nothings */
    @Override public void cleanUp() {}//Do nothing!

    @Override public void loadFile(UMLDocument umlDocument) {
        Server server = NetworkManager.getServerInstance();
        if(server == null)
            return;//Not hosting...
        server.sendMessageToAllClients(Server.generateDocumentPacket());
    }//Do nothing!
}
