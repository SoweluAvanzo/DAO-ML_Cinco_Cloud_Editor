package de.jabc.cinco.meta.plugin.event.api.payload

import graphmodel.Edge
import graphmodel.GraphModel
import graphmodel.IdentifiableElement
import graphmodel.ModelElementContainer
import graphmodel.Node
import org.eclipse.core.runtime.IPath
import org.eclipse.xtend.lib.annotations.Data

interface CreatePayload<Element extends IdentifiableElement> extends Payload<Element> {
	
	// Intentionally left blank
	
}

interface PreCreatePayload<Element extends IdentifiableElement> extends CreatePayload<Element> {
	
	// Intentionally left blank
	
}

@Data
class PreCreateGraphModelPayload<Element extends GraphModel> implements PreCreatePayload<Element> {
	
	val Class<? extends Element> elementClass
	val String name
	val IPath path
	
}

@Data
class PreCreateNodePayload<Element extends Node> implements PreCreatePayload<Element> {
	
	val Class<? extends Element> elementClass
	val ModelElementContainer container
	val int x
	val int y
	val int width
	val int height
	
}

@Data
class PreCreateEdgePayload<Element extends Edge> implements PreCreatePayload<Element> {
	
	val Class<? extends Element> elementClass
	val Node sourceNode
	val Node targetNode
	
}

@Data
class PostCreatePayload<Element extends IdentifiableElement> implements CreatePayload<Element> {
	
	val Element element
	
}
