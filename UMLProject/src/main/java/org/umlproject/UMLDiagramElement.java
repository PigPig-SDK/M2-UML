package org.umlproject;

import java.util.HashSet;
import java.util.Set;

/**
 * This is an abstract base class for UML Document elements that will interface with the GUI.
 * Every GUI object should maintain a 'selectable' state.
 */
public abstract class UMLDiagramElement
{
    transient protected DiagramElementListener listener;
    public static Set<DiagramElementListener> globalListeners = new HashSet();
    
    public DiagramElementListener getUIListener()
    {
        return listener;
    }
    
    public void setListener(DiagramElementListener listener)
    {
        this.listener = listener;
    }

    public void updateGUI(boolean informGlobals)
    {
        if(informGlobals)
            globalListeners.forEach(o->o.update(this));
        
        if(listener == null)
            return;
        listener.update(this);
    }

    public void updateGUILocation(boolean informGlobals)
    {
        if(informGlobals)
            globalListeners.forEach(o->o.updateLocation(this));
        
        if(listener == null)
            return;
        listener.updateLocation(this);
    }
    public void disposeOfGuiListener()
    {
        if(this.listener == null)
            return;
        this.listener.cleanUp();
        this.listener = null;
    }
    public static void disposeOfGlobalListeners()
    {
        globalListeners.forEach(o->o.cleanUp());
        globalListeners.clear();
    }
}
