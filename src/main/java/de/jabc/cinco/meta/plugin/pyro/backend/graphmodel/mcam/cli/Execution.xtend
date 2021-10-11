package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.mcam.cli

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.GraphModel

class Execution extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)'''«g.name.fuEscapeJava»Execution.java'''
	
	
	def content(GraphModel g)
	'''
	package «g.MGLModel.package».mcam.cli;

	
	import «g.MGLModel.package».mcam.adapter.«g.name.fuEscapeJava»Adapter;
	
	import «g.apiFQN»;
	
	import de.jabc.cinco.meta.plugin.mcam.runtime.core.CincoCheckModule;
	
	
	
	import java.util.HashSet;
	import java.util.Set;

	
	
	
	public class «g.name.fuEscapeJava»Execution {
	
		public «g.name.fuEscapeJava»Adapter initApiAdapter(«g.name.fuEscapeJava» file) {
			«g.name.fuEscapeJava»Adapter model = new «g.name.fuEscapeJava»Adapter();
			model.setModel(file);
			return model;
		}
	
		public Set<CincoCheckModule> getCheckModuleRegistry() {
			Set<CincoCheckModule> reg = new HashSet<>();
	
			reg.add(new «g.MGLModel.package».mcam.modules.checks.«g.name.fuEscapeJava»ContainmentCheck());
			reg.add(new «g.MGLModel.package».mcam.modules.checks.«g.name.fuEscapeJava»IncomingCheck());
			reg.add(new «g.MGLModel.package».mcam.modules.checks.«g.name.fuEscapeJava»OutgoingCheck());
			«FOR check:g.annotations.filter[name.equals("mcam_checkmodule")].map[value.get(0)]»
			reg.add(new «check»());
			«ENDFOR»
	
			return reg;
		}
	
	}

	
	'''
	
}
