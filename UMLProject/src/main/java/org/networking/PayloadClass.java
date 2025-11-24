package org.networking;

import com.google.gson.JsonSyntaxException;
import static org.networking.PayloadGeneric.handleSuccessfulPacket;
import static org.networking.PayloadGeneric.sendValidClass;
import org.umlproject.DocumentState;
import org.umlproject.UMLClass;
import org.umlproject.UMLDiagramElement;
import org.umlproject.UMLDocument;

/**
 * Important to note: The payload for UMLClass is the UMLClass itself.
 * There is no need for a wrapper payload.
 */
public class PayloadClass extends PayloadGeneric 
{
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
}
