package info.scce.cinco.product.flowgraph.hooks;

import org.eclipse.emf.ecore.EStructuralFeature;

import de.jabc.cinco.meta.runtime.action.CincoPostAttributeChangeHook;
import graphmodel.IdentifiableElement;

/**
 * Example post-create hook that randomly sets the name of the activity. Possible
 * names are inspired by the action verbs of old-school point&click adventure games :)
 */
public class PostAttributeChange2 extends CincoPostAttributeChangeHook<IdentifiableElement> {

	@Override
	public boolean canHandleChange(IdentifiableElement element, EStructuralFeature changedAttribute) {
		System.out.println("Attribute can change2!");
		return true;
	}

	@Override
	public void handleChange(IdentifiableElement element, EStructuralFeature changedAttribute) {
		System.out.println("Attribute changed2!");
	}
}

