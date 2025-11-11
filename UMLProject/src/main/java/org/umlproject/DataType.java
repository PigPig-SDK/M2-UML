package org.umlproject;
//Enum class representing primitive data types, and OTHER for custom ones
public enum DataType {
    BYTE,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    CHAR,
    BOOLEAN,
    OTHER;
    /**
     * Converts a given string to a DataType.
     * 
     * @param input String to be converted to DataType
     * @return Other if no valid input is found, Otherwise the expected DataType.
     */
    public static DataType stringToDatatype(String input) {
        if (input == null)
            return OTHER;

        return switch (input.toLowerCase()) {
            case "byte" -> BYTE;
            case "short" -> SHORT;
            case "int", "integer" -> INT;
            case "long" -> LONG;
            case "float" -> FLOAT;
            case "double" -> DOUBLE;
            case "char", "character" -> CHAR;
            case "bool", "boolean" -> BOOLEAN;
            default -> OTHER;
        };
    }
}
