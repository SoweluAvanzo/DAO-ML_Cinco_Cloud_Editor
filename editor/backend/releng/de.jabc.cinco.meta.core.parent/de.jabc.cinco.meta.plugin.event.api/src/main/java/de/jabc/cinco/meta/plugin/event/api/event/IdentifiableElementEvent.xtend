package de.jabc.cinco.meta.plugin.event.api.event

import de.jabc.cinco.meta.plugin.event.api.payload.PostAttributeChangePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PostCreatePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreAttributeChangePayload
import graphmodel.IdentifiableElement

interface IdentifiableElementEvent<Element extends IdentifiableElement> extends Event<Element> {
	
	/*** Create ***/
	
	// For preCreate see GraphModelEvent, NodeEvent and EdgeEvent
	
	def void postCreate(Element element)
	def void postCreate(PostCreatePayload<Element> payload)
	
	
	
	/*** AttributeChange ***/
	
	def void preAttributeChange(Element element, String attribute, Object newValue)
	def void preAttributeChange(PreAttributeChangePayload<Element> payload)

	def void postAttributeChange(Element element, String attribute, Object oldValue)
	def void postAttributeChange(PostAttributeChangePayload<Element> payload)
	
}
