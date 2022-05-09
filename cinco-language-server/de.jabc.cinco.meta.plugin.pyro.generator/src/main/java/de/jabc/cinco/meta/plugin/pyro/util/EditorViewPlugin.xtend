package de.jabc.cinco.meta.plugin.pyro.util

abstract class EditorViewPlugin extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
		
	def PluginComponent getPluginComponent();
	
	
	def EditorViewPluginRestController getRestController();
	
}

class EditorViewPluginRestController {
	public CharSequence filename;
	public CharSequence content;
}


