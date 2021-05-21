package de.jabc.cinco.meta.plugin.event.api.payload

import graphmodel.IdentifiableElement
import org.eclipse.xtend.lib.annotations.Data

interface AttributeChangePayload<Element extends IdentifiableElement> extends Payload<Element> {
	
	// Intentionally left blank
	
}

@Data
class PreAttributeChangePayload<Element extends IdentifiableElement> implements AttributeChangePayload<Element> {
	
	val Element element
	val String attribute
	val Object newValue
	
}

@Data
class PostAttributeChangePayload<Element extends IdentifiableElement> implements AttributeChangePayload<Element> {
	
	val Element element
	val String attribute
	val Object oldValue
	
}
