package org.networking;

/**
 * This enum describes what the 'payload' of a packet signifies.
 * 
 * ex: MESSAGE should be expected to contain strings..
 *     IDENTIFICATION should contain a UserIdentification
 *     ...So on...
 */
public enum PacketType {
    MESSAGE,
    IDENTIFICATION,
    DISCONNECT,
    HEARTBEAT,
    FULL_DOCUMENT
}
