package com.unittests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.networking.NetworkPacket;
import org.networking.PacketType;


public class NetworkPacketTests {
    @Test
    void packetToJson_returnsValidString_success() {
        //Arrange
        NetworkPacket netPacket = new NetworkPacket(0, PacketType.MESSAGE, "Hello" );
        //Act
        String jsonPacket = netPacket.packetToJson();
        NetworkPacket fromJson = NetworkPacket.jsonToPacket(jsonPacket);
        //Assert
        assertEquals(fromJson, netPacket);
    }
    @Test
    void packetToJson_nullMessageHandled_success() {
        //Arrange
        NetworkPacket netPacket = new NetworkPacket(0, PacketType.MESSAGE, null );
        //Act
        String jsonPacket = netPacket.packetToJson();
        NetworkPacket fromJson = NetworkPacket.jsonToPacket(jsonPacket);
        //Assert
        assertEquals(fromJson, netPacket);
    }
        @Test
    void packetToJson_nullEnumHandled_success() {
        //Arrange
        NetworkPacket netPacket = new NetworkPacket(0, null, "Hello" );
        //Act
        String jsonPacket = netPacket.packetToJson();
        NetworkPacket fromJson = NetworkPacket.jsonToPacket(jsonPacket);
        //Assert
        assertEquals(fromJson, netPacket);
    }
}
