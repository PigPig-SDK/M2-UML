package org.umlproject;
//Enum class representing primitive data types, and OTHER for custom ones
public enum RelationshipType {
    AGGREGATION,
    COMPOSITION,
    GENERALIZATION,
    REALIZATION,
    OTHER;

    public static RelationshipType stringToRelationshipType(String input) {
        if (input == null)
            return OTHER;

        return switch (input.toLowerCase()) {
            case "aggregation" -> AGGREGATION;
            case "composition" -> COMPOSITION;
            case "generalization" -> GENERALIZATION;
            case "realization" -> REALIZATION;
            default -> OTHER;
        };
    }
}
