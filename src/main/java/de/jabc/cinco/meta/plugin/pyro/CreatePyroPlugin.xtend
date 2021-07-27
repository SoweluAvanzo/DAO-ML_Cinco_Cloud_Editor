package de.jabc.cinco.meta.plugin.pyro

import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.util.Set
import mgl.MGLModel
import productDefinition.CincoProduct

class CreatePyroPlugin {
	//protected extension WorkspaceExtension _we = new WorkspaceExtension()
	public static final String PYRO = "pyro"
	public static final String PRIME = "primeviewer"
	public static final String PRIME_LABEL = "pvLabel"

	def void execute(Set<MGLModel> mglModels, String projectLocation,CincoProduct cp) throws IOException, URISyntaxException {
		try {
			//pyroFolder = _we.createFolder(project, "pyro")	
			createFolder(projectLocation + "/pyro")		
		} catch(Exception e) {
			
		}
		//val absolutebasPath = pyroFolder.getLocation()
		val absolutebasPath = projectLocation + "/pyro";
		
		//generate
		val gen = new Generator
		gen.generate(mglModels,cp,absolutebasPath,projectLocation)
		
	}
	
	def createFolder(String path){		
		return new File(path).mkdirs
	}
}