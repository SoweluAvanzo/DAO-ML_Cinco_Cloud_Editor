package de.jabc.cinco.meta.plugin.event.api.event

import de.jabc.cinco.meta.plugin.event.api.payload.PostSavePayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreCreateGraphModelPayload
import de.jabc.cinco.meta.plugin.event.api.payload.PreSavePayload
import graphmodel.GraphModel
import org.eclipse.core.runtime.IPath

interface GraphModelEvent<Element extends GraphModel> extends ModelElementContainerEvent<Element> {
	
	/*** Create ***/
	
	def void preCreate(Class<? extends Element> elementClass, String name, IPath path)
	def void preCreate(PreCreateGraphModelPayload<Element> payload)
	
	// For postCreate see IdentifiableElementEvent
	
	
	
	/*** Save ***/
	
	def void preSave(Element element)
	def void preSave(PreSavePayload<Element> payload)
	
	def void postSave(Element element)
	def void postSave(PostSavePayload<Element> payload)
	
}
