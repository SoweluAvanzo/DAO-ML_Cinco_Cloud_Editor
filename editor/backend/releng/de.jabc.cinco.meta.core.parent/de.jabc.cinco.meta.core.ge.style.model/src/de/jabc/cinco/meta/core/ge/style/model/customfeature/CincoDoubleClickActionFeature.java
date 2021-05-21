package de.jabc.cinco.meta.core.ge.style.model.customfeature;

import org.eclipse.graphiti.features.IFeatureProvider;

import de.jabc.cinco.meta.runtime.action.CincoDoubleClickAction;
import graphmodel.IdentifiableElement;

public class CincoDoubleClickActionFeature<T extends IdentifiableElement> extends CincoCustomActionFeature<T> {

	public CincoDoubleClickActionFeature(IFeatureProvider fp, CincoDoubleClickAction<T> action) {
		super(fp, action);
	}

	
}
