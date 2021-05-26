package de.jabc.cinco.meta.plugin.event.api.event

import graphmodel.Container

interface ContainerEvent<Element extends Container> extends NodeEvent<Element>, ModelElementContainerEvent<Element> {
	
	// Intentionally left blank
	
}
