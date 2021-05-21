package de.jabc.cinco.meta.plugin.event.api.payload

import graphmodel.GraphModel
import org.eclipse.xtend.lib.annotations.Data

interface SavePayload<Element extends GraphModel> extends Payload<Element> {
	
	// Intentionally left blank
	
}

@Data
class PreSavePayload<Element extends GraphModel> implements SavePayload<Element> {
	
	val Element element
	
}

@Data
class PostSavePayload<Element extends GraphModel> implements SavePayload<Element> {
	
	val Element element
	
}
