package de.jabc.cinco.meta.plugin.event.api.payload

import graphmodel.ModelElement
import org.eclipse.xtend.lib.annotations.Data

interface DeletePayload<Element extends ModelElement> extends Payload<Element> {
	
	// Intentionally left blank
	
}

@Data
class PreDeletePayload<Element extends ModelElement> implements DeletePayload<Element> {
	
	val Element element
	
}

@Data
class PostDeletePayload<Element extends ModelElement> implements DeletePayload<Element> {
	
	val Element element
	
}
