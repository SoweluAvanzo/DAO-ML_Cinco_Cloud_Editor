package de.jabc.cinco.meta.core.ge.style.generator.runtime.appearance;

import style.Appearance;

public interface StyleAppearanceProvider<T> {
	public Appearance getAppearance(T modelElement, String styleElementName);
}
