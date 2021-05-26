package de.jabc.cinco.meta.plugin.event.api.event

interface Event<Element> {
	
	/*** Subscribe ***/
	
	def void subscribe()

	def void unsubscribe()

}
