package de.jabc.cinco.meta.plugin.pyro.util

import java.util.List
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.plugin.EcoreModelView
import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.plugin.PrimeModelView

class EditorViewPluginRegistry {
	def List<EditorViewPlugin> getPlugins(GeneratorCompound gc) {
		return #[
			new EcoreModelView(gc),
			new PrimeModelView(gc)
		]
	}
}
