package de.jabc.cinco.meta.core.event

import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext

class Activator implements BundleActivator {
	
	val public static String PLUGIN_ID = 'de.jabc.cinco.meta.core.event'
	
	var static BundleContext context
	
	def static package BundleContext getContext() {
		context
	}

	/**
	 * @see BundleActivator#start(BundleContext)
	 */
	override start(BundleContext bundleContext) {
		Activator.context = bundleContext
	}

	/**
	 * @see BundleActivator#stop(BundleContext)
	 */
	override stop(BundleContext bundleContext) {
		Activator.context = null
	}
	
}
