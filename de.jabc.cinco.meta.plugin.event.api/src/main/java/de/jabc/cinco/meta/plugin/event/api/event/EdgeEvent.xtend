package de.jabc.cinco.meta.plugin.event.api.event

import de.jabc.cinco.meta.plugin.event.api.payload.PostReconnectPayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreCreateEdgePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreReconnectPayload
import graphmodel.Edge
import graphmodel.Node

interface EdgeEvent<Element extends Edge> extends ModelElementEvent<Element> {
	
	/*** Create ***/
	
	def void preCreate(Class<? extends Element> elementClass, Node sourceNode, Node targetNode)
	def void preCreate(PreCreateEdgePayload<Element> payload)
	
	// For postCreate see IdentifiableElementEvent
	
	
	
	/*** Reconnect ***/
	
	def void preReconnect(Element element, Node newSourceNode, Node newTargetNode)
	def void preReconnect(PreReconnectPayload<Element> payload)
	
	def void postReconnect(Element element, Node oldSourceNode, Node oldTargetNode)
	def void postReconnect(PostReconnectPayload<Element> payload)
	
}
