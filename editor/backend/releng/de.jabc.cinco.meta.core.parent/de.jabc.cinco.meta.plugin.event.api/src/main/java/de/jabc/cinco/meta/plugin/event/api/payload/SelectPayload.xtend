package de.jabc.cinco.meta.plugin.event.api.payload

import graphmodel.ModelElement
import org.eclipse.xtend.lib.annotations.Data

interface SelectPayload<Element extends ModelElement> extends Payload<Element> {
	
	// Intentionally left blank
	
}

@Data
class PreSelectPayload<Element extends ModelElement> implements SelectPayload<Element> {
	
	val Element element
	
}

@Data
class PostSelectPayload<Element extends ModelElement> implements SelectPayload<Element> {
	
	val Element element
	
}
