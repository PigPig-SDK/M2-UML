package org.umlproject;

import java.util.ArrayList;

public class Memento<T extends Cloneable> 
{
    private T instance;
    private ArrayList<MementoListener<T>> listeners = new ArrayList<>();
    
    public Memento(T instance)
    {
        this.instance = instance;
        
    }
    public T getInstance() {return this.instance;}
    /**
     * Makes a clone of instance on the undo stack.
     * Calls listeners
     */
    public void saveState(){}
    /**
     * Moves back one location in history.
     * Calls listeners
     */
    public void undo(){}
    /**
     * Appends top of undo stack to history
     * Calls listeners
     */
    public void redo(){}
    /**
     * Clears the current undo/redo. Suggesting a new object be 'king'.
     * Calls listeners
     */
    public void resetHistory(T newInstance){}
    /**
     * Gets the history length
     */
    public int getHistoryLength(){ return -1;}
    /**
     * Gets the history length
     */
    public int getRedoHistoryLength(){ return -1;}
    /**
     * Adds a listener
     * @param mementoListener The listener which will attach to this memento
     */
    public void addListener(MementoListener<T> mementoListener){}
    /**
     * Removes a listener
     * @param mementoListener The listener to detach
     */
    public void removeListener(MementoListener<T> mementoListener){}
}
