package de.jabc.cinco.meta.runtime.hook;


import graphmodel.ModelElement;

public abstract class CincoPostDeleteHook<T extends ModelElement> extends info.scce.pyro.api.PyroControl {

    abstract public Runnable getPostDeleteFunction(T modelElement);

}