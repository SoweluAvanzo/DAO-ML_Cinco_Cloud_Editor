package de.jabc.cinco.meta.plugin.event.api.payload

import graphmodel.ModelElement
import org.eclipse.xtend.lib.annotations.Data

interface DoubleClickPayload<Element extends ModelElement> extends Payload<Element> {
	
	// Intentionally left blank
	
}

@Data
class PreDoubleClickPayload<Element extends ModelElement> implements DoubleClickPayload<Element> {
	
	val Element element
	
}

@Data
class PostDoubleClickPayload<Element extends ModelElement> implements DoubleClickPayload<Element> {
	
	val Element element
	
}
