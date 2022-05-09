package de.jabc.cinco.meta.plugin.pyro.generator

import de.jabc.cinco.meta.plugin.pyro.util.Escaper
import de.jabc.cinco.meta.plugin.pyro.util.FileHandler
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.MGLExtension
import de.jabc.cinco.meta.plugin.pyro.util.OAuthCompound
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedList
import java.util.List
import java.util.Map
import java.util.Set
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import mgl.Import
import mgl.MGLModel
import org.apache.commons.io.FileUtils
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import productDefinition.Annotation
import productDefinition.CincoProduct
import java.io.FileOutputStream
import java.io.FileNotFoundException
import de.jabc.cinco.meta.core.utils.IWorkspaceContext
import java.util.stream.Collectors

class Generator {
	static var fileSystem = null as FileSystem;
	static extension MGLExtension me = MGLExtension.instance
	String projectLocation
	
	public static boolean deleteSources = true
	public static boolean generateZip = true

	def generate(Set<MGLModel> allWorkspaceMglModels, CincoProduct cpd, String base, String projectLocation) {
		var mglModels = allWorkspaceMglModels.stream.filter[m |
			// only take imported mgls from the set of all mgls in the workspace
			cpd.getMgls().stream().anyMatch[mglDescriptor|
				var relativePath = mglDescriptor.mglPath
				var mglUri = IWorkspaceContext.localInstance.getFileURI(relativePath)
				var importPath = mglUri.devicePath
				var mglPath = m.eResource.URI.devicePath
				importPath == mglPath;
			]
		].collect(Collectors.toSet())
		
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
		var Map<String, MGLModel> transientAPIs = new HashMap
		val javaPath = base + "/app/src/main/java/"
		this.projectLocation = projectLocation
		
		for (a : cpd.annotations) { // TODO: SAMI - some of these annotation do not exist anymore

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
					val URI relativeURI = URI.createURI(a.value.get(0));
					val URI absoluteURI = IWorkspaceContext.localInstance.getFileURI(relativeURI);
					val m = allWorkspaceMglModels.stream.filter[it.eResource.URI.equals(absoluteURI)].findFirst
					if (m.present) {
						val transientAPI = m.get
						transientAPIs.put(transientAPI.fileName.toString, transientAPI)
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
					FileHandler.copyAnnotatedClasses(a, javaPath, projectLocation)
					projectPostCreate.add(a.value.get(0))
				}
			}

			if (a.name == "pyroOrganizationPostCreate") {
				if (a.getValue().size() == 1) {
					FileHandler.copyAnnotatedClasses(a, javaPath, projectLocation)
					organizationPostCreate.add(a.value.get(0))
				}
			}

			if (a.name == "pyroEditorLayout") {
				if (a.getValue().size() == 1) {
					FileHandler.copyAnnotatedClasses(a, javaPath, projectLocation)
					editorLayout.add(a.value.get(0))
				}
			}

			if (a.name == "pyroRootPostCreate") {
				if (a.getValue().size() == 1) {
					FileHandler.copyAnnotatedClasses(a, javaPath, projectLocation)
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
		
		val allProjectMGLModels = (mglModels + transientAPIs.values).toSet;
		val allEcoreReferences = allProjectMGLModels.map[ecoreImports].flatten.toSet
		var ecores = new HashSet<EPackage>;
		for(e : allEcoreReferences) {
			if(!ecores.map[nsURI].contains(e.nsURI)) {
				ecores.add(e)
			}
		}
		
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
			transientAPIs,
			cpd
		);

		// generate spec
		new de.jabc.cinco.meta.plugin.pyro.spec.Generator(base).generate(gc)

		// generate preview
		new de.jabc.cinco.meta.plugin.pyro.preview.Generator(base).generate(gc)

		// copy back end statics
		{
			val File baseFolder = new File(base);
			copyResources("archetype/app", base)
			copyResources("archetype/buildDocker.sh", base)
			copyResources("archetype/compile.sh", base)
			copyResources("archetype/compileBackend.sh", base)
			copyResources("archetype/compileFrontend.sh", base)
			copyResources("archetype/develop.sh", base)
			copyResources("archetype/Dockerfile", base)
			copyResources("archetype/env.list", base)
			if (!baseFolder.list.contains("oauth.properties")) {
				copyResources("archetype/oauth.properties", base)
			}
			copyResources("archetype/postgres.yml", base)
			copyResources("archetype/readme.md", base)
			copyResources("archetype/run.sh", base)
			copyResources("archetype/runDocker.sh", base)
			
			// generate backend sources
			val backEndGenerator = new de.jabc.cinco.meta.plugin.pyro.backend.Generator(base)
			backEndGenerator.generator(gc, projectLocation)
		}

		// copy front end statics
		{
			// generate front end
			val path = base + "/webapp"
			copyResources("frontend/app/build.yaml", path)
			copyResources("frontend/app/lib", path)
			copyResources("frontend/app/web", path)
			copyResources("frontend/app/.vscode", path) // uncomment this line, only for development
			copyResources("frontend/app/Dockerfile", path)
			copyResources("frontend/app/basic_auth", path)
			copyResources("frontend/app/default.conf", path)
			val frontEndGen = new de.jabc.cinco.meta.plugin.pyro.frontend.Generator(path)
			frontEndGen.generate(gc, projectLocation)
			// generate modeling canvas
			val modelingGen = new de.jabc.cinco.meta.plugin.pyro.canvas.Generator(path)
			modelingGen.generator(gc, projectLocation)
		}

		for (a : cpd.annotations) {
			if (a.name == "pyroIncludeSources") {
				if (a.getValue().size() != 0) {
					for (v : a.value) {
						copySources(v, javaPath, projectLocation)
					}

				}
			}
		}
		
		if(generateZip) {
			packageZip(base)
		}
		if (deleteSources) {
			cleanup(base)
		}
	}
	
	def packageZip(String base) {
        val File fileToZip = new File(base);
        val File[] srcFiles = fileToZip.listFiles();
        val FileOutputStream fos = new FileOutputStream(projectLocation + File.separator + "pyro.zip");
        val ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (File srcFile : srcFiles) {
        zipFile(zipOut,srcFile,srcFile.name)
        }     
        zipOut.flush();
		fos.flush();
		zipOut.close();
		fos.close();
	}

	/**
	 * Copies all files recursively from a given bundle to a given target path.
	 * @param bundleId
	 * @param target
	 */
	def copyResources(String source, String target) {
		copyResourceFiles(source, target, this.class)
	}

	/**
	 * Copies all files recursively from a given bundle to a given target path.
	 * @param bundleId
	 * @param target
	 */
	static def copyResourceFiles(String source, String target, Class<?> mainClass) {
		val baseUri = URI.createURI(source);
		var files = getResourcePathsOfFiles(source, Integer.MAX_VALUE, mainClass)
		val base = baseUri.segment(baseUri.segmentCount - 1)
		for (f : files) {
			val from = f.toString.empty ? source : source + File.separator + f
			val to = target + File.separator + (
				f.toString.empty ? base : base + File.separator + f
			)
			copyResource(from, to, mainClass)
		}
	}

	def ecoreImports(MGLModel g) {
		val ecores = new HashSet<EPackage>();
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
		ecores
	}

	private def Resource findImportModels(Import import1, MGLModel masterModel) {
		val path = import1.getImportURI();
		val uri = URI.createURI(path, true);
		try {
			var Resource res = null;
			if (uri.isPlatformResource()) {
				res = new ResourceSetImpl().getResource(uri, true);
			} else {
				val projectURI = URI.createFileURI(projectLocation + "/")
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
		var file = new File(folderPath + File.separator)
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
			val resolvedPath = FileHandler.resolveEclipseResourcePath(folderPath)
			if(resolvedPath !== null) {
				// case eclipse-platform
				path = Paths.get(resolvedPath)
			} else {
				// case runtime-development/test
				path = Paths.get(uri)
			}
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
		
		// windows-path workaround
		resource = resource.replace(File.separator, "/");
		// first "/" needs to be deleted for windows-support
		if (("" + resource.charAt(0)).equals("/"))
			resource = resource.substring(1)
		val src = clsLoader.getResourceAsStream(resource)
		
		// windows-path workaround
		var sanitizedPath = dest.replace(File.separator, "/");
		val fileURI = URI.createFileURI(sanitizedPath);
		var sanitizedURI = java.net.URI.create(fileURI.toString)
		val destPath = Paths.get(sanitizedURI);
		
		// create Folder holding the final file
		var lastIndex = destPath.getNameCount() - 1
		// lastIndex = lastIndex > 0 ? lastIndex : 0
		if (lastIndex > 0) {
			lastIndex = 0;
		}
		// val folderPath = Paths.get(File.separator + destPath.subpath(0, lastIndex))
		createFolder(destPath.toString)
		// create/copy file not directory
		
		Files.copy(src, destPath, StandardCopyOption.REPLACE_EXISTING)
	}

	static def copySources(String sourceName, String target, String projectLocation) {
		val separatorIndex = sourceName.indexOf('/')
		var String packagePath = sourceName.substring(0, separatorIndex).replace('.', '/')
		var String srcPath = sourceName.substring(separatorIndex)
		var pathSegments = packagePath.split('/')
		val String pathName = Paths.get(srcPath, pathSegments).toString()

		try {
			if (!pathName.isEmpty) {
				val String newPath = projectLocation + "/" + pathName
				val resolvedURI = URI.createURI(newPath)
				val File directory = new File(resolvedURI.toString);
				val destPath = target + "/" + packagePath
				val File dest = new File(destPath);
				FileUtils.copyDirectory(directory, dest);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static def void zipFile( ZipOutputStream zipOut,File fileToZip, String fileName) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            val File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile( zipOut, childFile, fileName + "/" + childFile.getName());
            }
            return;
        }
        val FileInputStream fis = new FileInputStream(fileToZip);
        val ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        val  byte[] bytes = newByteArrayOfSize(1024);
        var int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

	def cleanup(String dirName) {
		val File pyroDir = new File(dirName);
		
		if (pyroDir.exists()) {
			try {
				org.eclipse.xtext.util.Files.cleanFolder(pyroDir, null, true, true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}