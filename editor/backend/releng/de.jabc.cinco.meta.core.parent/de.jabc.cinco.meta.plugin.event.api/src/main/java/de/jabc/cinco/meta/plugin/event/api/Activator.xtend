package de.jabc.cinco.meta.plugin.event.api

import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext

class Activator implements BundleActivator {
	
	val public static String PLUGIN_ID = 'de.jabc.cinco.meta.plugin.event.api'
	
	var static BundleContext context

	def static package BundleContext getContext() {
		context
	}

	/**
	 * @see BundleActivator#start(BundleContext)
	 */
	override start(BundleContext bundleContext) {
		context = bundleContext
	}

	/**
	 * @see BundleActivator#stop(BundleContext)
	 */
	override stop(BundleContext bundleContext) {
		context = null
	}
	
}
