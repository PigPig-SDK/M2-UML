package org.networking;

/**
 * This packet will simply move a UML Element
 * 
 * For now, this only accounts for UMLClasses.
 */
public record PayloadMoveElement (String objectName, int x, int y)
{
    /**
     * Will find the object specified by 'objectName' and move it.
     * @return False if the move failed.
     */
    public boolean executeMove()
    {
        return false;
    }
}

