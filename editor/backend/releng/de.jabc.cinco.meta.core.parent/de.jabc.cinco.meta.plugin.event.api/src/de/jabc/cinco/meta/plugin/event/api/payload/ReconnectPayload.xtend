package de.jabc.cinco.meta.plugin.event.api.payload

import graphmodel.Edge
import graphmodel.Node
import org.eclipse.xtend.lib.annotations.Data

interface ReconnectPayload<Element extends Edge> extends Payload<Element> {
	
	// Intentionally left blank
	
}

@Data
class PreReconnectPayload<Element extends Edge> implements ReconnectPayload<Element> {
	
	val Element element
	val Node newSourceNode
	val Node newTargetNode
	
}

@Data
class PostReconnectPayload<Element extends Edge> implements ReconnectPayload<Element> {
	
	val Element element
	val Node oldSourceNode
	val Node oldTargetNode
	
}
