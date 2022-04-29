package de.jabc.cinco.meta.plugin.event.api.payload

import graphmodel.Direction
import graphmodel.Node
import org.eclipse.xtend.lib.annotations.Data

interface ResizePayload<Element extends Node> extends Payload<Element> {
	
	// Intentionally left blank
	
}

@Data
class PreResizePayload<Element extends Node> implements ResizePayload<Element> {
	
	val Element element
	val int newWidth
	val int newHeight
	val int newX
	val int newY
	val Direction direction
	
}

@Data
class PostResizePayload<Element extends Node> implements ResizePayload<Element> {
	
	val Element element
	val int oldWidth
	val int oldHeight
	val int oldX
	val int oldY
	val Direction direction
	
}
