package de.jabc.cinco.meta.plugin.mcam.runtime.core;


import graphmodel.IdentifiableElement;

import java.util.LinkedList;
import java.util.List;

public class _CincoId {
	
	private String id = "";
	private String label = null;
	private IdentifiableElement element = null;

	private List<String> errors = new LinkedList<>();

	public List<String> getErrors() {
		return errors;
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public List<String> getInfos() {
		return infos;
	}

	private List<String> warnings = new LinkedList<>();
	private List<String> infos = new LinkedList<>();

	public _CincoId(IdentifiableElement element) {
		super();
		this.element = element;
		this.id = element.getId();
	}
	public _CincoId(IdentifiableElement element, String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public IdentifiableElement getElement() {
		return element;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public void setElement(IdentifiableElement element) {
		this.element = element;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof _CincoId))
			return false;
		_CincoId other = (_CincoId) obj;
		if (id == null) {
			if (other.getId() != null)
				return false;
		} else if (!id.equals(other.getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (label == null)
			return " [" + element.getClass().getSimpleName() + "]";
		return label + " [" + element.getClass().getSimpleName() + "]";
	}

	public void addErrorMessage(String msg) {
		errors.add(msg);
	}

	public void addWarningMessage(String msg) {
		warnings.add(msg);
	}

	public void addInfoMessage(String msg) {
		infos.add(msg);
	}
	
}
