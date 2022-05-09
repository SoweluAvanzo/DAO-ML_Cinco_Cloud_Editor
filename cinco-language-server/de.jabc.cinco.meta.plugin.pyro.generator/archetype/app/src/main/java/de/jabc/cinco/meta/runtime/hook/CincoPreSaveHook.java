package de.jabc.cinco.meta.runtime.hook;

import de.jabc.cinco.meta.runtime.CincoRuntimeBaseClass;
import graphmodel.GraphModel;

public abstract class CincoPreSaveHook<T extends GraphModel> extends CincoRuntimeBaseClass {

	public abstract void preSave(T object);

}
