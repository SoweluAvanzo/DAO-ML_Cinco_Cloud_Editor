package de.jabc.cinco.meta.plugin.event.api.payload

import graphmodel.ModelElementContainer
import graphmodel.Node
import org.eclipse.xtend.lib.annotations.Data

interface MovePayload<Element extends Node> extends Payload<Element> {
	
	// Intentionally left blank
	
}

@Data
class PreMovePayload<Element extends Node> implements MovePayload<Element> {
	
	val Element element
	val ModelElementContainer newContainer
	val int newX
	val int newY
	
}

@Data
class PostMovePayload<Element extends Node> implements MovePayload<Element> {
	
	val Element element
	val ModelElementContainer oldContainer
	val int oldX
	val int oldY
	
}
