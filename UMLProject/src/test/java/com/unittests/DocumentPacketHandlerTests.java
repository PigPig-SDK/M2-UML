package com.unittests;

import com.google.gson.Gson;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.networking.DocumentPacketHandler;
import org.networking.NetworkPacket;
import org.networking.PacketType;
import org.umlproject.MainThreadDispatcher;
import org.umlproject.UMLDocument;


public class DocumentPacketHandlerTests {
    
    @Test
    public void handleDocumentPacket_validPacket_updatesDocument() throws IOException {
        //Assign
        MainThreadDispatcher.dispatcher = new MainThreadDispatcher();
        UMLDocument doc = new UMLDocument("test");
        doc.addClass("a");
        NetworkPacket packet = NetworkPacket.objectToNetworkPacket(0, PacketType.FULL_DOCUMENT, doc);
        //Act
        DocumentPacketHandler.handleDocumentPacket(packet);
        MainThreadDispatcher.dispatcher.processQueuedActions();
        //Assert
        Assertions.assertNotNull(UMLDocument.getInstance().getClass("a"));
        UMLDocument.resetInstance(true);
        
        MainThreadDispatcher.dispatcher = null;
    }
    @Test
    public void handleDocumentPacket_invalidPacket_doesNothing() throws IOException {
        //Assign
        MainThreadDispatcher.dispatcher = new MainThreadDispatcher();
        UMLDocument.getInstance().addClass("a");
        NetworkPacket packet = NetworkPacket.objectToNetworkPacket(0, PacketType.FULL_DOCUMENT, null);
        //Act
        DocumentPacketHandler.handleDocumentPacket(packet);
        MainThreadDispatcher.dispatcher.processQueuedActions();
        //Assert
        Assertions.assertNotNull(UMLDocument.getInstance().getClass("a"));
        UMLDocument.resetInstance(true);
        MainThreadDispatcher.dispatcher = null;
    }
    @Test
    public void handleDocumentPacket_invalidStringInPacket_doesNothing() throws IOException {
        //Assign
        UMLDocument.getInstance().addClass("a");
        NetworkPacket packet = new NetworkPacket(0, PacketType.FULL_DOCUMENT, "Fake string! Woooowaaa!");
        //Act
        DocumentPacketHandler.handleDocumentPacket(packet);
        //Assert
        Assertions.assertNotNull(UMLDocument.getInstance().getClass("a"));
        UMLDocument.resetInstance(true);
    }
    @Test
    public void handleDocumentPacket_invalidStringInPacketFragmented_doesNothing() throws IOException {
        //Assign
        UMLDocument.getInstance().addClass("b");
        UMLDocument doc = new UMLDocument("test");
        doc.addClass("a");
        Gson gson = new Gson();
        String jsonString = gson.toJson(doc);
        NetworkPacket packet = new NetworkPacket(0, PacketType.FULL_DOCUMENT, jsonString.substring(10));
        //Act
        DocumentPacketHandler.handleDocumentPacket(packet);
        //Assert
        Assertions.assertNotNull(UMLDocument.getInstance().getClass("b"));
        UMLDocument.resetInstance(true);
    }
}
