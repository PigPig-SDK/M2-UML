package org.umlproject;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * This is an abstract base class for UML Document elements that will interface with the Observers.
 * 
 */
public abstract class UMLDiagramElement
{
    transient protected DiagramElementListener listener;
    transient public static Set<DiagramElementListener> globalListeners = new HashSet();
    /**
     * Used for storing a elements 'last edit network time'
     */
    public long lastNetworkEditTime = 0;
    
    public UUID networkId = UUID.randomUUID();
    
    /**
     * @return the dynamic listener OR null
     */
    public DiagramElementListener getListener()
    {
        return listener;
    }
    /**
     * @param listener The listener this object should bind to dynamically.
     */
    public void setListener(DiagramElementListener listener)
    {
        this.listener = listener;
    }
    /**
     * Notifies the dynamic listener, with possibly all global listeners joining in.
     * @param informGlobals If the global listeners should be notified of this update.
     */
    public void updateListener(boolean informGlobals)
    {
        if(informGlobals)
        {
            globalListeners.forEach(o->o.update(this));
        }
        
        if(listener == null)
            return;
        listener.update(this);
    }
    /**
     * Notifies the dynamic listener, with possibly all global listeners joining in.
     * Specifically updates under "positional updates"
     * @param informGlobals If the global listeners should be notified of this update.
     */
    public void updateListnerAboutLocation(boolean informGlobals)
    {
        if(informGlobals)
            globalListeners.forEach(o->o.updateLocation(this));
        
        if(listener == null)
            return;
        listener.updateLocation(this);
    }
    /**
     * Calls the dynamic listener to dispose.
     */
    public void disposeOfListener()
    {
        if(this.listener == null)
            return;
        this.listener.cleanUp();
        this.listener = null;
    }
    /**
     * Calls global listeners to 'dispose'
     */
    public static void disposeOfGlobalListeners()
    {
        globalListeners.forEach(o->o.cleanUp());
        globalListeners.clear();
    }
}
