package de.jabc.cinco.meta.plugin.event.api.event

import de.jabc.cinco.meta.plugin.event.api.payload.PostDeletePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PostDoubleClickPayload
import de.jabc.cinco.meta.plugin.event.api.payload.PostSelectPayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreDeletePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreDoubleClickPayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreSelectPayload
import graphmodel.ModelElement

interface ModelElementEvent<Element extends ModelElement> extends IdentifiableElementEvent<Element> {
	
	/*** Delete ***/
	
	def void preDelete(Element element)
	def void preDelete(PreDeletePayload<Element> payload)
	
	def Runnable postDelete(Element element)
	def Runnable postDelete(PostDeletePayload<Element> payload)
	
	
	
	/*** Select ***/
	
	def void preSelect(Element element)
	def void preSelect(PreSelectPayload<Element> payload)
	
	def void postSelect(Element element)
	def void postSelect(PostSelectPayload<Element> payload)
	
	
	
	/*** DoubleClick ***/
	
	def void preDoubleClick(Element element)
	def void preDoubleClick(PreDoubleClickPayload<Element> payload)
	
	def void postDoubleClick(Element element)
	def void postDoubleClick(PostDoubleClickPayload<Element> payload)
	
}
