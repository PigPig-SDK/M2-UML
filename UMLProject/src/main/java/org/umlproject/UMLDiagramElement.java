package org.umlproject;
/**
 * This is an abstract base class for UML Document elements that will interface with the GUI.
 * Every GUI object should maintain a 'selectable' state.
 */
public abstract class UMLDiagramElement
{
    transient protected UIListener listener;
    
    public void setListener(UIListener listener)
    {
        this.listener = listener;
    }
    public void updateGUI()
    {
        if(listener == null)
            return;
        listener.update(this);
    }
    public void updateGUILocation()
    {
        if(listener == null)
            return;
        listener.updateLocation(this);
    }
    public void updateGUISelectionState()
    {
        if(listener == null)
            return;
        listener.updateSelected(this);
    }
    public void disposeOfGuiListener()
    {
        if(this.listener == null)
            return;
        this.listener.cleanUp();
        this.listener = null;
    }
}
