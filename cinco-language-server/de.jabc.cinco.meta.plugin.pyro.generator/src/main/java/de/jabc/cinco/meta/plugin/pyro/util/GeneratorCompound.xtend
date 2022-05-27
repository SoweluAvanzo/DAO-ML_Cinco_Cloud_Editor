package de.jabc.cinco.meta.plugin.pyro.util

import java.util.List
import java.util.Map
import java.util.Set
import mgl.MGLModel
import org.eclipse.emf.ecore.EPackage
import productDefinition.Annotation
import productDefinition.CincoProduct

class GeneratorCompound {
	public final MGLExtension mglExtension;
	public final String projectName;
	public final Set<MGLModel> mglModels
	public final Set<EPackage> ecores
	public final String projectLocation;
	public final List<String> rootPostCreate;
	public final List<String> projectPostCreate;
	public final List<String> editorLayout;
	public final List<Annotation> projectServices;
	public final List<Annotation> projectActions;
	public final Map<String, MGLModel> transientAPIs;
	public final CincoProduct cpd;
	
	new(String projectName, Set<MGLModel> mglModels, Set<EPackage> ecores, String projectLocation,
		List<String> rootPostCreate,
		List<String> projectPostCreate,
		List<String> editorLayout,
		List<Annotation> projectServices,
		List<Annotation> projectActions,
		Map<String, MGLModel> transientAPIs,
		CincoProduct cpd
		
	) {
		this.projectName = projectName
		this.mglModels = mglModels
		this.ecores = ecores;
		this.projectLocation = projectLocation;
		this.mglExtension = MGLExtension.instance;
		
		this.rootPostCreate = rootPostCreate;
		this.projectPostCreate = projectPostCreate;
		
		this.editorLayout = editorLayout
		
		this.projectServices = projectServices
		this.projectActions = projectActions
		this.transientAPIs = transientAPIs
		
		this.cpd = cpd
	}
	
	def getGraphMopdels() {
		mglModels.map[it.graphModels].flatten.toSet.sortBy[g| mglExtension.typeName(g).toString]
	}
	
	def concreteGraphModels() {
		graphMopdels.filter[!isAbstract]
	}
}
