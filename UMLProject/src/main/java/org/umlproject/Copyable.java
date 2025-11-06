package org.umlproject;

public interface Copyable<T> extends Cloneable {
    public T clone();
}