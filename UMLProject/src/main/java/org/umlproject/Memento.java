package org.umlproject;

public class Memento<T extends Cloneable> 
{
    private T instance;
    
    public Memento(T instance)
    {
        this.instance = instance;
        
    }
    public T getInstance() {return this.instance;}
    
    /**
     * Makes a clone of instance on the undo stack
     */
    public void saveState(){}
    /**
     * Moves back one location in history.
     */
    public void undo(){}
    /**
     * Appends top of undo stack to history
     */
    public void redo(){}
    /**
     * Clears the current undo/redo. Suggesting a new object be 'king'.
     */
    public void clearHistory(T newInstance){}
    /**
     * Gets the history length
     */
    public int getHistoryLength(){ return -1;}
    /**
     * Gets the history length
     */
    public int getRedoHistoryLength(){ return -1;}
}
