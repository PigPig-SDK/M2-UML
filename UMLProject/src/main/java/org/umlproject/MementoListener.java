package org.umlproject;

//Oops.
//TODO: Rename as MementoListner
public interface MementoListener<T extends Cloneable> {
    
    void update(Memento<T> memento);
}
