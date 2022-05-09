package de.jabc.cinco.meta.runtime.hook;

import graphmodel.IdentifiableElement;

public abstract class CincoPreDeleteHook<T extends IdentifiableElement> extends info.scce.pyro.api.PyroControl {

    public abstract void preDelete(T modelElement);
    
}
