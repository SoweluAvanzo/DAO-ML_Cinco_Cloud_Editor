package de.jabc.cinco.meta.runtime.hook;


import graphmodel.ModelElement;

public abstract class CincoPostResizeHook<T extends ModelElement> extends de.jabc.cinco.meta.runtime.CincoRuntimeBaseClass {

    public abstract void postResize(T modelElement, int deltaWidth, int deltaHeight);

}

