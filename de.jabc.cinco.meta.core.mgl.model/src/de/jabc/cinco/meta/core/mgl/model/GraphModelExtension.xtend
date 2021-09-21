package de.jabc.cinco.meta.core.mgl.model

import graphmodel.IdentifiableElement
import graphmodel.ModelElement

class GraphModelExtension {
	/**
	 * Checks whether to identifiable elements are equal by comparing their ids.
	 * 
	 * @param it the left hand side of the equals check.
	 * @param that the right hand side of the equals check.
	 * @return the result of the equals check.
	 */
	// TODO Should be moved to Identifiable#equals!!!
	static def ==(IdentifiableElement it, IdentifiableElement that) { 
		(it === that) || (!(that === null) && id == that.id)
	}
	
	static def !=(IdentifiableElement it, IdentifiableElement that) { !(it == that) }
	
	/**
	 * Gets the path to root for any model element.
	 * 
	 * @param el the model element.
	 * @return the path to the root model element.
	 */
	// TODO Why is the GraphModel a ModelElementContainer and not at the same time a ModelElement (now it is excluded of the path to root).
	static def Iterable<ModelElement> getPathToRoot(ModelElement el) {
		if (el.container != null && el.container instanceof ModelElement)
			#[el] + (el.container as ModelElement).pathToRoot
		else #[el]
	}
}