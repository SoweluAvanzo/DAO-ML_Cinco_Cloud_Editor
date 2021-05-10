package de.jabc.cinco.meta.runtime.action;


import graphmodel.IdentifiableElement;
import org.eclipse.emf.ecore.EStructuralFeature;

public abstract class CincoPostAttributeChangeHook<T extends IdentifiableElement> extends de.jabc.cinco.meta.runtime.CincoRuntimeBaseClass {

    public String getName() {
        return this.getClass().getName();
    }

    public abstract boolean canHandleChange(T modelElement,EStructuralFeature element);
    public abstract void handleChange(T modelElement,EStructuralFeature element);
    
    public void execute(T element,EStructuralFeature feature) {
    	handleChange(element,feature);
    }
    	
    public boolean canExecute(T element,EStructuralFeature feature) {
        return canHandleChange(element,feature);
    }
}