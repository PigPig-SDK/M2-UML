package org.networking;

import java.util.UUID;
import org.umlproject.UMLRelationship;
/**
 * This record holds the generic payload data for UMLRelationships.
 * 
 * The SOURCE and DESTINATION are given as a form of validation.
 * 
 * 
 */
public record PayloadRelationship(UUID source, UUID destination, UMLRelationship relationship) { }
