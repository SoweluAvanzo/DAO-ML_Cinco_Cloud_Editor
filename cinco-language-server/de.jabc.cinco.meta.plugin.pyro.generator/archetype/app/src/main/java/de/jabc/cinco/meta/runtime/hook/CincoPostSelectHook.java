package de.jabc.cinco.meta.runtime.hook;

import de.jabc.cinco.meta.runtime.CincoRuntimeBaseClass;
import graphmodel.ModelElement;

public abstract class CincoPostSelectHook<T extends ModelElement> extends CincoRuntimeBaseClass {

	public abstract void postSelect(T modelElement);

}
