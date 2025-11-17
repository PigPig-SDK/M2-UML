package org.umlproject;

/**
 * Every object which interacts with a memento is required to implement Copyable<T> 
 * This is because a java design flaw as far as I am concerned.
 * 
 * @param <T> Your object that is being cloned
 */
public interface Copyable<T> extends Cloneable {
    //Java work around for generics of clonable type.
    public T clone();
}