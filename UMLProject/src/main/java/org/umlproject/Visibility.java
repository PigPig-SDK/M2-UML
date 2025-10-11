package org.umlproject;
//enum to set the visibility of methods and data fields
public enum Visibility {

    PUBLIC,
    PRIVATE,
    PROTECTED,
    PACKAGE;
    
    public static Visibility stringVisibility(String input) {
        return switch (input.toLowerCase()) {
            case "public","+" -> PUBLIC;
            case "private","-" -> PRIVATE;
            case "protected","#" -> PROTECTED;
            case "package","~" -> PACKAGE;
            default -> PRIVATE;
        };
    }
}
