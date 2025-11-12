package org.umlproject;

/**
 * This enum describes which 'operation' the UMLDocument singleton might be under.
 * 
 * Purpose: To allow observers more information over the action they retrieve.
 */
public enum DocumentState 
{
    NORMAL,
    MASS_OPERATION,
    FILE_LOADING,
    MEMENTO_STATE_RESET,
    SILENT_MOVEMENT,
    CLONING,
    NETWORK_OPERATION
}
