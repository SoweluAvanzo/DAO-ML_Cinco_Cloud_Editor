package de.jabc.cinco.meta.plugin.pyro.util

import de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.plugin.EcoreModelView
import java.util.List

class EditorViewPluginRegistry {
	def List<EditorViewPlugin> getPlugins(GeneratorCompound gc) {
		return #[
			new EcoreModelView(gc)
		]
	}
}
