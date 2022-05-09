package de.jabc.cinco.meta.runtime.provider;

import de.jabc.cinco.meta.runtime.CincoRuntimeBaseClass;
import java.util.Map;
import graphmodel.IdentifiableElement;


public abstract class CincoValuesProvider<E extends IdentifiableElement, A extends Object> extends CincoRuntimeBaseClass {
	

	public abstract Map<A, String> getPossibleValues(E modelElement);

}
