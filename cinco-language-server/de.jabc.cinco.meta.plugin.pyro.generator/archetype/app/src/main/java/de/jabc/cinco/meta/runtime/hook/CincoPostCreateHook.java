package de.jabc.cinco.meta.runtime.hook;

import graphmodel.IdentifiableElement;

/**
 * Author zweihoff
 */
public abstract class CincoPostCreateHook<T extends IdentifiableElement> extends info.scce.pyro.api.PyroControl {

    public abstract void postCreate(T element);
}
