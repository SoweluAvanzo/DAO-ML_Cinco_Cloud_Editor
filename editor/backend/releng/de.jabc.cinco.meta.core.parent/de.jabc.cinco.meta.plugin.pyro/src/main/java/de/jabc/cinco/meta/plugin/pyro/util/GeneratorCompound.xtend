package de.jabc.cinco.meta.plugin.pyro.util

import java.util.List
import java.util.Map
import java.util.Set
import mgl.MGLModel
import org.eclipse.emf.ecore.EPackage
import productDefinition.Annotation
import productDefinition.CincoProduct
import mgl.GraphModel

class GeneratorCompound {
	public final MGLExtension mglExtension;
	public final String projectName;
	public final Set<MGLModel> mglModels
	public final Set<EPackage> ecores
	public final String projectLocation;
	
	public final List<String> rootPostCreate;
	public final List<String> organizationPostCreate;
	public final List<String> projectPostCreate;
	public final List<String> editorLayout;
	public final List<String> initialOrganizations;
	
	public final List<Annotation> projectServices;
	public final List<Annotation> projectActions;
	
	public final List<String> projectPerUser;
	public final boolean organizationPerUser;
	
	public final OAuthCompound authCompound;
	
	public final Map<String,GraphModel> transientGraphModels;
	
	public final CincoProduct cpd;
	
	new(String projectName,Set<MGLModel> mglModels,Set<EPackage> ecores,String projectLocation,
		List<String> rootPostCreate,
		List<String> organizationPostCreate,
		List<String> projectPostCreate,
		OAuthCompound authCompound,
		List<String> editorLayout,
		List<String> initialOrganizations,
		List<String> projectPerUser,
		boolean organizationPerUser,
		List<Annotation> projectServices,
		List<Annotation> projectActions,
		Map<String,GraphModel> transientGraphModels,
		CincoProduct cpd
		
	) {
		this.projectName = projectName
		this.mglModels = mglModels
		this.ecores = ecores;
		this.projectLocation = projectLocation;
		this.mglExtension = MGLExtension.instance;
		
		this.rootPostCreate = rootPostCreate;
		this.organizationPostCreate = organizationPostCreate;
		this.projectPostCreate = projectPostCreate;
		
		this.authCompound = authCompound
		
		this.editorLayout = editorLayout
		
		this.initialOrganizations = initialOrganizations
		
		this.projectPerUser = projectPerUser
		this.organizationPerUser = organizationPerUser
		
		this.projectServices = projectServices
		this.projectActions = projectActions
		this.transientGraphModels = transientGraphModels
		
		this.cpd = cpd
	}

	// TODO:SAMI: refactor name after migration
	def getGraphMopdels() {
		mglModels.map[it.graphModels].flatten.toSet
	}
}
