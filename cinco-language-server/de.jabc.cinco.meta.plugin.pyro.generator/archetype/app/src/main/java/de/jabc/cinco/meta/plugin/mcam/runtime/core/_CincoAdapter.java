package de.jabc.cinco.meta.plugin.mcam.runtime.core;

import java.util.ArrayList;
import java.util.List;


import graphmodel.GraphModel;
import graphmodel.IdentifiableElement;
import graphmodel.ModelElement;

public abstract class _CincoAdapter<T extends _CincoId, M extends GraphModel>{

	protected M model = null;

	protected String modelName = "";

	public List<T> getEntityIds() {
		List<T> ids = new ArrayList<>();
		final T id = createId(getModel());
		id.setElement(getModel());
		ids.add(id);

		getModel().getAllEdges().forEach((n)->{
				final T ide = createId(getModel());
				ide.setElement(n);
				ids.add(ide);
		});

		getModel().getAllNodes().forEach((n)->{
			final T idn = createId(getModel());
			idn.setElement(n);
			ids.add(idn);
		});

		return ids;
	}

	public M getModel() {
		return this.model;	
	}

	public void setModel(M model) {
		this.model = model;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	public IdentifiableElement getElementById(T id) {
		return id.getElement();
	}

	public T getIdByString(String idString) {
		for (T id : getEntityIds()) {
			if (idString.equals(id.getId()))
				return id;
		}
		return null;
	}

	public String getModelName() {
		return modelName;
	}

	
	protected abstract T createId(IdentifiableElement obj);

	public abstract String getLabel(ModelElement element);


}
