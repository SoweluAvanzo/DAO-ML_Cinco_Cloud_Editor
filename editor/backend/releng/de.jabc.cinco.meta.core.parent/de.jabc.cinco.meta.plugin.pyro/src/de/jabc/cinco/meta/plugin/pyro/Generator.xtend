package de.jabc.cinco.meta.plugin.pyro

import de.jabc.cinco.meta.plugin.pyro.util.Escaper
import de.jabc.cinco.meta.plugin.pyro.util.FileHandler
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.OAuthCompound
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URL
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.security.CodeSource
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedList
import java.util.List
import java.util.Map
import java.util.Set
import mgl.Import
import mgl.MGLModel
import org.apache.commons.io.FileUtils
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import productDefinition.Annotation
import productDefinition.CincoProduct
import de.jabc.cinco.meta.plugin.pyro.util.MGLExtension
import mgl.GraphModel

class Generator {
	static var fileSystem = null as FileSystem;
	static extension MGLExtension me = MGLExtension.instance
	String projectLocation

	def generate(Set<MGLModel> mglModels, CincoProduct cpd, String base, String projectLocation) {
		// val fileHelper = new FileExtension
		val rootPostCreate = new LinkedList<String>();
		val organizationPostCreate = new LinkedList<String>();
		val editorLayout = new LinkedList<String>();
		val projectPostCreate = new LinkedList<String>();
		val initialOrganizations = new LinkedList<String>();
		val projectServices = new LinkedList<Annotation>();
		val projectActions = new LinkedList<Annotation>();
		var organizationPerUser = false
		var List<String> projetcsPerUser = null
		var OAuthCompound oauth = null
		var Map<String,GraphModel> transientGraphModels = new HashMap
		val javaPath = base+"/app/src/main/java/"
		this.projectLocation = projectLocation

		for (a : cpd.annotations) {

			if (a.name == "pyroProjectService" && a.value.size >= 3) {
				// FQN, ServiceName, Arguments...
				projectServices.add(a)
				FileHandler.copyAnnotatedClasses(a, javaPath, projectLocation)
			}

			if (a.name == "pyroProjectAction" && a.value.size == 2) {
				// FQN, Action Name
				projectActions.add(a)
				FileHandler.copyAnnotatedClasses(a, javaPath, projectLocation)
			}

			if (a.name == "pyroInitialOrganizations") {
				if (a.getValue().size() > 0) {

					initialOrganizations.addAll(a.value)
				}
			}

			if (a.name == "pyroTransientAPI") {
				if (a.getValue().size() == 1) {

					val org.eclipse.emf.common.util.URI relativeURI = org.eclipse.emf.common.util.URI.createURI(a.value.get(0));
					val org.eclipse.emf.common.util.URI rootURI = org.eclipse.emf.common.util.URI.createURI(projectLocation);
					val org.eclipse.emf.common.util.URI absoluteURI = relativeURI.resolve(rootURI);

					val m = mglModels.stream.filter[it.eResource.URI.equals(absoluteURI)].findFirst
					if (m.present) {
						for(g: m.get.graphmodels) // TODO:SAMI: check if this works
							transientGraphModels.put(projectLocation + "#" + g.name, g)
					}

				}
			}

			if (a.name == "pyroProjectPerUser") {
				projetcsPerUser = new LinkedList<String>()
				projetcsPerUser.addAll(a.value)
			}

			if (a.name == "pyroOrganizationPerUser") {
				organizationPerUser = true
			}

			if (a.name == "pyroProjectPostCreate") {
				if (a.getValue().size() == 1) {
					FileHandler.copyAnnotatedClasses(a, javaPath,projectLocation)
					projectPostCreate.add(a.value.get(0))
				}
			}

			if (a.name == "pyroOrganizationPostCreate") {
				if (a.getValue().size() == 1) {
					FileHandler.copyAnnotatedClasses(a, javaPath,projectLocation)
					organizationPostCreate.add(a.value.get(0))
				}
			}

			if (a.name == "pyroEditorLayout") {
				if (a.getValue().size() == 1) {
					FileHandler.copyAnnotatedClasses(a, javaPath,projectLocation)
					editorLayout.add(a.value.get(0))
				}
			}

			if (a.name == "pyroRootPostCreate") {
				if (a.getValue().size() == 1) {
					FileHandler.copyAnnotatedClasses(a, javaPath,projectLocation)
					rootPostCreate.add(a.value.get(0))
				}
			}

			if (a.name == "pyroOAuth") {
				if (a.getValue().size() == 1) {
					val p = FileHandler.getPropertiesFile(a)
					if (p !== null) {
						oauth = new OAuthCompound(
							p.getProperty("name", "OAuth"),
							p.getProperty("callbackURL", ""),
							p.getProperty("clientID", ""),
							p.getProperty("clientSecret", ""),
							p.getProperty("scope", "user"),
							p.getProperty("signinURL", ""),
							p.getProperty("authURL", ""),
							p.getProperty("userURL", ""),
							p.getProperty("userAccountIdentifier", ""),
							p.getProperty("userAccountName", ""),
							p.getProperty("admins", ""),
							Escaper.randomString(10) as String
						);
					}
				}
			}

		}

		val ecores = new HashSet<EPackage>();
		mglModels.forEach[ecoreImports(ecores)]
		val gc = new GeneratorCompound(
			cpd.name,
			mglModels,
			ecores,
			projectLocation,
			rootPostCreate,
			organizationPostCreate,
			projectPostCreate,
			oauth,
			editorLayout,
			initialOrganizations,
			projetcsPerUser,
			organizationPerUser,
			projectServices,
			projectActions,
			transientGraphModels,
			cpd
		);

		// generate preview
		new de.jabc.cinco.meta.plugin.pyro.preview.Generator(base).generate(gc)  

		// copy back end statics
		{
			val File baseFolder = new File(base); 
			copyResources("archetype/app",base)
			copyResources("archetype/run.sh",base)
			copyResources("archetype/nginx.conf",base)
			copyResources("archetype/docker-compose.yml",base)
			copyResources("archetype/docker-compose.debug.yml",base)
			if(!baseFolder.list.contains("docker-compose.production.yml")){
				copyResources("archetype/docker-compose.production.yml",base)
			}

			copyResources("archetype/rsync-exclude.txt",base)
			if(!baseFolder.list.contains("oauth.properties")){
				copyResources("archetype/oauth.properties",base)
			}
			//generate cinco dependencies
			
			//generate backend sources
			val backEndGenerator = new de.jabc.cinco.meta.plugin.pyro.backend.Generator(base)

			backEndGenerator.generator(gc,projectLocation)
		}

		//copy front end statics
		{
			// generate front end
			// val path = base.append("/dywa-app/app-presentation/target/generated-sources/app")
			val path = base + "/webapp"
			copyResources("frontend/app/lib", path)
			copyResources("frontend/app/web", path)
			copyResources("frontend/app/.vscode",path) // uncomment this line, only for development
			copyResources("frontend/app/build.yaml",path)
			copyResources("frontend/app/Dockerfile",path)
			copyResources("frontend/app/basic_auth",path)
			copyResources("frontend/app/default.conf",path)
			val frontEndGen = new de.jabc.cinco.meta.plugin.pyro.frontend.Generator(path)
			frontEndGen.generate(gc, projectLocation)
			// generate modeling canvas
			val modelingGen = new de.jabc.cinco.meta.plugin.pyro.canvas.Generator(path)
			modelingGen.generator(gc, projectLocation)
		}

		for (a : cpd.annotations) {	
			if (a.name == "pyroIncludeSources") {
				if (a.getValue().size() != 0) {
					for (v : a.value){
				 		copySources(v, javaPath,projectLocation)	
				 	}


				}
			}
		}
		throw new RuntimeException("backend and frontend successful");
	}


/**
 * Copies all files recursively from a given bundle to a given target path.
 * @param bundleId
 * @param target
 */
static def copyResources(String source, String target) {	
		val CodeSource src = Generator.getProtectionDomain().getCodeSource();
		try {
			
			val URL jar = src.getLocation();
			val URI jarURI = jar.toURI;
			var String newPath = jarURI + "/" + source
			val URI resolvedURI = jarURI.resolve(newPath).normalize
			val File directory = new File(resolvedURI);
			if (directory.isDirectory) {
				FileUtils.copyDirectoryToDirectory(directory, new File(target));
			} else {
				FileUtils.copyFileToDirectory(directory, new File(target));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	def ecoreImports(MGLModel g, Set<EPackage> ecores) {

		for (Import import1 : g.imports) {

			var r = findImportModels(import1, g);
			if (r !== null) {
				for (EObject eObject : r.getContents()) {
					if (eObject instanceof EPackage) {
						val ePackage = eObject;
						if (!ePackage.getName().equals(g.getName()) && !g.getNsURI().equals(ePackage.getNsURI())) {
							if (!ecores.exists[name.equals(eObject.name)]) {
								ecores.add(eObject);

							}
						}
					}
				}
			}
		}

	}

private def Resource findImportModels(Import import1, MGLModel masterModel) {
		val path = import1.getImportURI();
		val uri = org.eclipse.emf.common.util.URI.createURI(path, true);
		try {
			var Resource res = null;
			if (uri.isPlatformResource()) {
				res = new ResourceSetImpl().getResource(uri, true);
			} else {
				val projectURI = org.eclipse.emf.common.util.URI.createURI(projectLocation)
				val absoluteURI = uri.resolve(projectURI)
				val File file = new File(absoluteURI.toFileString).getAbsoluteFile();
				if (file.exists()) {
					res = new ResourceSetImpl().getResource(absoluteURI, true);
				} else {
					return null;
				}
			}

			return res;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

static def boolean createFolder(String folderPath) throws IOException {
		var file = new File(folderPath)
		if (file.exists) {
			return false;
		}
		Files.createDirectories(Paths.get(folderPath));
		return true;
	}

static def List<Path> getResourcePathsOfFiles(String folderPath, int depth, Class<?> mainClass) {
		var clsLoader = mainClass.getClassLoader
		var resource = clsLoader.getResource(folderPath);
		var uri = resource.toURI()
		var path = Paths.get("")
		if (uri.getScheme().equals("jar")) {
			if (fileSystem === null)
				fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
			path = fileSystem.getPath(folderPath)
			path = Paths.get(path.toUri) // needed
		} else {
			path = Paths.get(uri)
		}
		var pathList = new ArrayList<Path>
		var walk = Files.walk(path, depth)
		for (var it = walk.iterator; it.hasNext;) {
			var p = it.next
			if (Files.isRegularFile(p)) {
				p = Paths.get(p.toUri) // needed
				p = path.relativize(p)
				pathList.add(p)
			}
		}
		return pathList
	}

static def copyResource(String res, String dest, Class<?> mainClass) throws IOException {
		var clsLoader = mainClass.getClassLoader
		var resource = res;

		// first "/" needs to be deleted for windows-support
		if (("" + resource.charAt(0)).equals("/"))
			resource = resource.substring(1)

		val src = clsLoader.getResourceAsStream(resource)
		val destPath = Paths.get(dest)

		// create Folder holding the final file
		var lastIndex = destPath.getNameCount() - 1
		// lastIndex = lastIndex > 0 ? lastIndex : 0
		if (lastIndex > 0) {
			lastIndex = 0;
		}
		val folderPath = Paths.get(File.separator + destPath.subpath(0, lastIndex))
		createFolder(folderPath.toString)
		// create/copy file not directory
		Files.copy(src, destPath, StandardCopyOption.REPLACE_EXISTING)
	}

static def copySources(String sourceName, String target, String projectLocation) {
	
	    
		var String[] splitedName = sourceName.split("/")	
		val String pathName = sourceName.substring(splitedName.get(0).length + 1).replace('.', '/')

		try {
			if (!pathName.isEmpty) {
				if (pathName.equals("src") || pathName.equals("src/") || pathName.equals("src-gen") ||
					pathName.equals("src-gen/")) {

					val String newPath= projectLocation +"/"+pathName 
					val resolvedURI = org.eclipse.emf.common.util.URI.createURI(newPath)	
					val File directory = new File(resolvedURI.toString);
					val newTargetPath = target + sourceName.substring(0,splitedName.get(0).length).replace('.', '/')
					val File dest = new File(newTargetPath); 
										
					FileUtils.copyDirectory(directory, dest);
				} else {
					val String[] newSplitedName = pathName.split("/")
					if ((pathName.contains("src") && newSplitedName.get(0).equals("src")) ||
						(pathName.contains("src-gen") && newSplitedName.get(0).equals("src-gen"))) {
						val String newPathName = pathName.substring(newSplitedName.get(0).length + 1)
						val String newTarget = target + "/" + newPathName;

						val String newPath= projectLocation +"/"+pathName 
						val resolvedURI = org.eclipse.emf.common.util.URI.createURI(newPath)
						val File directory = new File(resolvedURI.toString);
						val File newDest =  new File(newTarget);
						FileUtils.copyDirectory(directory,newDest);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
