package org.umlproject;

/**
 * This interface listens to memento updates
 * Bind it to a specified memento for updates.
 * 
 * @param <T> Which type of memento it should listen.
 */
public interface MementoListener<T extends Copyable<T>> {
    
    void update(Memento<T> memento);
}
