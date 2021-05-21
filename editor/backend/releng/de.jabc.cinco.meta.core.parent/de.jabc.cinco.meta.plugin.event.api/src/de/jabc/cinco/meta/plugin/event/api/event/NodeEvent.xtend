package de.jabc.cinco.meta.plugin.event.api.event

import de.jabc.cinco.meta.plugin.event.api.payload.PostMovePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PostResizePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreCreateNodePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreMovePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreResizePayload
import graphmodel.Direction
import graphmodel.ModelElementContainer
import graphmodel.Node

interface NodeEvent<Element extends Node> extends ModelElementEvent<Element> {
	
	/*** Create ***/
	
	def void preCreate(Class<? extends Element> elementClass, ModelElementContainer container, int x, int y, int width, int height)
	def void preCreate(PreCreateNodePayload<Element> payload)
	
	// For postCreate see IdentifiableElementEvent
	
	
	
	/*** Move ***/
	
	def void preMove(Element element, ModelElementContainer newContainer, int newX, int newY)
	def void preMove(PreMovePayload<Element> payload)
	
	def void postMove(Element element, ModelElementContainer oldContainer, int oldX, int oldY)
	def void postMove(PostMovePayload<Element> payload)
	
	
	
	/*** Resize ***/
	
	def void preResize(Element element, int newWidth, int newHeight, int newX, int newY, Direction direction)
	def void preResize(PreResizePayload<Element> payload)
	
	def void postResize(Element element, int oldWidth, int oldHeight, int oldX, int oldY, Direction direction)
	def void postResize(PostResizePayload<Element> payload)
	
}
