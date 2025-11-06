package org.umlproject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public final class Memento<T extends Copyable<T>> 
{
    private T instance;
    private Set<MementoListener<T>> listeners = new HashSet<>();
    
    Stack<T> undoHistory = new Stack<>();
    Stack<T> redoHistory = new Stack<>();
    
    
    public Memento(T instance)
    {
        this.instance = instance;
        saveState();
    }
    public T getInstance() 
    {
        return this.instance;
    }
    /**
     * Makes a clone of instance on the undo stack.
     * Calls listeners
     */
    public void saveState()
    {
        undoHistory.add(instance.clone());
        callListenersToUpdate();
    }
    /**
     * Moves back one location in history.
     * Calls listeners
     */
    public void undo()
    {
        if(!(undoHistory.size() == 1)){
            redoHistory.push(undoHistory.pop());
        }
        instance = undoHistory.peek().clone();

        callListenersToUpdate();
    }
    /**
     * Appends top of undo stack to history
     * Calls listeners
     */
    public void redo()
    {
        if(redoHistory.isEmpty()){
            return;
        }

        undoHistory.push(redoHistory.pop());
        instance = undoHistory.peek().clone();

        callListenersToUpdate();
    }
    /**
     * Clears the current undo/redo. Suggesting a new object be 'king'.
     * Calls listeners
     */
    public void resetHistory(T newInstance)
    {
        redoHistory.clear();
        undoHistory.clear();
        //Suggest new king, populate them as first in list.
        instance = newInstance;
        saveState();
    }
    /**
     * Gets the history length
     */
    public int getHistoryLength()
    {
        return undoHistory.size();
    }
    /**
     * Gets the history length
     */
    public int getRedoHistoryLength()
    { 
        return redoHistory.size();
    }
    /**
     * Adds a listener
     * @param mementoListener The listener which will attach to this memento
     */
    public void addListener(MementoListener<T> mementoListener)
    {
        if(!listeners.contains(mementoListener))
            listeners.add(mementoListener);
    }
    /**
     * Removes a listener
     * @param mementoListener The listener to detach
     */
    public void removeListener(MementoListener<T> mementoListener)
    {
        if(listeners.contains(mementoListener))        
            listeners.remove(mementoListener);
    }
    
    private void callListenersToUpdate()
    {
        for(MementoListener<T> mementoListener : listeners)
        {
            mementoListener.update(this);
        }
    }
}
