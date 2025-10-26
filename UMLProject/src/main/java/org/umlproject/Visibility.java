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

    /**
     * This method is to be used for those times when you don't want to set the default
     * visibility to private if an unacceptable Visibility type was entered. Instead return a
     * boolean so that if false is returned you can deny the creation of a new UMLDataField.
     * @param input, string representing the Visibility to be tested.
     * @return boolean representing whether the input is a valid Visibility status or not.
     */
    public static boolean acceptableVisibility(String input){
        return switch(input.toLowerCase()) {
            case "public","+" -> true;
            case "private","-" -> true;
            case "protected","#" -> true;
            case "package","~" -> true;
            default -> false;

        };

    }
}
