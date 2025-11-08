package org.umlproject;

//Oops.
//TODO: Rename as MementoListner
public interface MementoListener<T extends Copyable<T>> {
    
    void update(Memento<T> memento);
}
