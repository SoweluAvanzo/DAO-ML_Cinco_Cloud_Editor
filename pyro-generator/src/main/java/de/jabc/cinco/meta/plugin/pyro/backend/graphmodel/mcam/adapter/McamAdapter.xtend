package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.mcam.adapter

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.GraphModel

class McamAdapter extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)'''«g.name.fuEscapeJava»Adapter.java'''
	
	
	
	def content(GraphModel g)
	'''
	package «g.MGLModel.package».mcam.adapter;

	

	import de.jabc.cinco.meta.plugin.mcam.runtime.core._CincoAdapter;
	import graphmodel.ModelElement;
	import graphmodel.IdentifiableElement;
	import «g.apiFQN»;
	

	
	public class «g.name.fuEscapeJava»Adapter extends _CincoAdapter<«g.name.fuEscapeJava»Id,«g.name.fuEscapeJava»> {
	
		
		@Override
		protected «g.name.fuEscapeJava»Id createId(IdentifiableElement obj) {
			return new «g.name.fuEscapeJava»Id(obj);
		}
		
		@Override
		public String getLabel(ModelElement element) {
			return element.getClass().getSimpleName();
		}
	
	}
	
	'''
	
}
