package de.jabc.cinco.meta.runtime.hook;


import graphmodel.ModelElement;
import graphmodel.ModelElementContainer;

public abstract class CincoPostMoveHook<T extends ModelElement> extends info.scce.pyro.api.PyroControl {

    abstract public void postMove(T modelElement, ModelElementContainer sourceContainer, ModelElementContainer targetContainer,
                                  int x, int y,
                                  int deltaX, int deltaY);

}