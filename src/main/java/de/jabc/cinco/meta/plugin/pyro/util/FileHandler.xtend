package de.jabc.cinco.meta.plugin.pyro.util

import de.jabc.cinco.meta.core.utils.CincoUtil
import de.jabc.cinco.meta.core.utils.PathValidator
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Properties
import mgl.Annotation
import mgl.GraphModel
import mgl.MGLModel
import org.apache.commons.io.FileUtils
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import style.AbstractShape
import style.ConnectionDecorator
import style.ContainerShape
import style.EdgeStyle
import style.GraphicsAlgorithm
import style.Image
import style.NodeStyle
import style.Style
import style.Styles
import org.osgi.framework.Bundle
import org.eclipse.core.runtime.Platform
import org.eclipse.core.runtime.FileLocator
import java.nio.file.Paths
import java.nio.file.Files

class FileHandler {
	static val mglExtension = MGLExtension.instance;
	
	def static boolean copyXtendOrJava(java.util.List<String> folders, String projectLocation, String target, CharSequence classPath, EObject pivot) {
		val pathJava = '''/«classPath».java'''
		val pathXtend = '''/«classPath».xtend'''
			
		var found = false
		for(f:folders) {
			if(!found){
				val path = getSourcePath(pivot, f, pathXtend, pathJava)
				copyFile(pivot,'''«f»/«path»''',target,false, projectLocation)
				found=true
			}
		}
		return found
	}
	
	def static CharSequence getSourcePath(EObject pivot, String folder, CharSequence pathXtend, CharSequence pathJava) {
		// xtend
		val xtendURI = PathValidator.getURIForString(pivot, '''«folder»/«pathXtend»''')
		val xtendURISegments = xtendURI !== null ? xtendURI.segments.length : 0
		
		// java
		val javaURI = PathValidator.getURIForString(pivot, '''«folder»/«pathJava»''')
		val javaURISegments = javaURI !== null ? javaURI.segments.length : 0
		
		if(javaURISegments > xtendURISegments) {
			if(javaURI.fileExists)
				return pathJava	
		} else {
			if(xtendURI.fileExists)
				return pathXtend
		}
		return null
	}
	
	def static boolean fileExists(URI uri) {
		if(uri === null)
			return false
		if(!(uri.platformResource || uri.platform)) {
			// isJar or runtime
			val path = Paths.get(uri.toFileString)
			val exists = Files.exists(path)
			return exists
		}
		// is eclipse
		return true
	}
	
	static def resolveEclipseResourcePath(String relativePath) {
		try {
			// CASE ECLIPSE-PLUGIN
			if(!Platform.running) {
				return null
			}
			val Bundle b = Platform.getBundle("de.jabc.cinco.meta.plugin.pyro.generator");
			val directoryURL = b.getEntry(relativePath);
			return FileLocator.toFileURL(directoryURL).toURI.path
		} catch(Exception e) {
			return null
		}
	}
	
	def static void copyClasse(GraphModel g,String path, String pyroAppSourcePath, String projectLocation) {
		val classPath = path.replaceAll("\\.","/")
		val folders = #["src","src-gen","xtend-gen","model-src-gen"]
		try {
			val String target = '''«pyroAppSourcePath»/«classPath.substring(0,classPath.lastIndexOf("/"))»'''
			
			val found = copyXtendOrJava(folders, projectLocation, target, classPath, g)
			
			if(!found) {
				println('''[ERROR] could not find annotated file: «classPath»''')
			}
		} catch (IOException e) {
			e.printStackTrace()
		}
	}
	
	def static Properties getPropertiesFile(productDefinition.Annotation annotation) {
		val target = annotation.value.get(0)
		var String[] splitedName = target.split("/")
    	val String pathName = target.substring(splitedName.get(0).length+1)
           
			try {
				if(!pathName.isEmpty){
					val p = PathValidator.getURIForString(annotation, pathName)
					var fileExists = FileUtils.getFile(p.path).exists
					if(!fileExists) 
						throw new IllegalArgumentException("Properties file does not exist!"); 
					var File f = new File(p.toFileString);
					val prop = new Properties();
					val stream = new FileInputStream(f);
					prop.load(stream);
					return prop
				}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null
	}
	
	def dispatch static void copyAnnotatedClasses(productDefinition.Annotation annotation, String pyroAppSourcePath, String projectLocation) {
		val classPath = annotation.value.get(0).replaceAll("\\.","/")
		val folders = #["src","src-gen","xtend-gen","model-src-gen"]
		try {
			val String target = '''«pyroAppSourcePath»/«classPath.substring(0,classPath.lastIndexOf("/"))»'''
			
			val found = copyXtendOrJava(folders, projectLocation, target, classPath, annotation)
			
			if(!found) {
				println('''[ERROR] could not find annotated file: «classPath»''')
			}
		} catch (IOException e) {
			e.printStackTrace()
		}
	}
	
	def dispatch static void copyAnnotatedClasses(Annotation annotation, String pyroAppSourcePath, String projectLocation) {
		val classPath = annotation.value.get(0).replaceAll("\\.","/")
		val folders = #["src","src-gen","xtend-gen","model-src-gen"]
		try {
			val String target = '''«pyroAppSourcePath»/«classPath.substring(0,classPath.lastIndexOf("/"))»'''
			
			val found = copyXtendOrJava(folders, projectLocation, target, classPath, annotation)
			
			if(!found) {
				println('''[ERROR] could not find annotated file: «classPath»''')
			}
		} catch (IOException e) {
			e.printStackTrace()
		}
	}
	
	def static void copyAppearanceProviderClasses(Style style, String pyroAppSourcePath , String projectLocation) {
		val classPath = style.appearanceProvider.substring(1,style.appearanceProvider.length-1).replaceAll("\\.","/")
		val folders = #["src","src-gen","xtend-gen","model-src-gen"]
		try {
			val String target = '''«pyroAppSourcePath»/«classPath.substring(0,classPath.lastIndexOf("/"))»'''
			
			val found = copyXtendOrJava(folders, projectLocation, target, classPath, style)
			
			if(!found) {
				println('''[ERROR] could not find appearance provider file: «classPath»''')
			}
		} catch (IOException e) {
			e.printStackTrace()
		}

	}

	def static void copyImages(MGLModel mglModel, String resourcePath, String imgBasePath, String projectLocation) {
		try {
			var String path = '''«resourcePath»«imgBasePath»«mglExtension.getFileName(mglModel).toLowerCase()»/'''
			var Styles styles = CincoUtil.getStyles(mglModel)
			for (Style style : styles.getStyles()) {
				if (style instanceof NodeStyle) {
					copyImage(((style as NodeStyle)).getMainShape(), path, projectLocation)
				} else if (style instanceof EdgeStyle) {
					for (ConnectionDecorator connectionDecorator : ((style as EdgeStyle)).getDecorator()) {
						copyImage(connectionDecorator.getDecoratorShape(), path, projectLocation)
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace()
		}

	}

	def private static void copyImage(AbstractShape shape, String target, String projectLocation) throws IOException {
		if (shape instanceof Image) {
			copyImageFile((shape as Image), target, projectLocation)
		} else if (shape instanceof ContainerShape) {
			for (AbstractShape abstractShape : ((shape as ContainerShape)).getChildren()) {
				if (abstractShape instanceof Image) {
					copyImageFile((abstractShape as Image), target, projectLocation)
				}
			}
		}
	}

	def private static void copyImage(GraphicsAlgorithm shape, String target, String projectLocation) throws IOException {
		if (shape instanceof Image) {
			copyImageFile((shape as Image), target, projectLocation)
		}
	}

	def private static void copyImageFile(Image image, String target, String projectLocation) throws IOException {
		copyFile(image,image.path,target,true, projectLocation)
	}
	
	def static void copyClasses(EObject res, String resourcePath, String targetPath, String projectLocation) {
		try {
			val f = new File(resourcePath)
			for (file:f.list) {
				copyFile(res,file,targetPath,true, projectLocation)
			}
		} catch (IOException e) {
			e.printStackTrace()
		}

	}
	
	def static void copyFile(EObject res,String path, String target,boolean replaceExisiting, String projectLocation) throws IOException {
			copyFile(res,path,target,replaceExisiting,null, projectLocation)
	}
	
	
	def static void copyFile(EObject res,String path, String target,boolean replaceExisiting,String newName, String projectLocation) throws IOException {				 
		var String newPath = projectLocation+ "/" + path.replace('"','')
		var resolvedURI = org.eclipse.emf.common.util.URI.createURI(newPath)	
		var File ir = new File(resolvedURI.toString);

		//var IFile ire = (ResourcesPlugin.getWorkspace().getRoot().findMember(uriForString.toPlatformString(true)) as IFile)
		//var File ir = new File(uriForString.toString);
		var File targetFolder = new File(target)
		val targetFile = if(newName===null) {
			new File(target+"/"+ ir.name)
		} else {
			new File(target+"/"+newName)
		}
		if(targetFile.exists&&!replaceExisiting){
			return
		}
		targetFolder.mkdirs()
		try {
			FileUtils.copyFileToDirectory(ir, targetFolder)
		} catch(IOException e) {
			e.printStackTrace
		}
		if(newName !== null && !targetFile.exists) {
			FileUtils.moveFile(new File(target+"/"+ir.name),targetFile)
		}
	}
	
	def static getFileOrFolder(EObject res,String path) {
		var URI uriForString = PathValidator.getURIForString(res, path)
		//var IResource ir = (ResourcesPlugin.getWorkspace().getRoot().findMember(uriForString.toPlatformString(true)) as IResource)
		new File(uriForString.toFileString);
	}
	
	def static getAllFiles(EObject res,String path) {
		val root = getFileOrFolder(res,path)
		root.collectFiles
	}
	
	def private static Iterable<File> collectFiles(File file) {
		if(file.isDirectory) {
			return file.listFiles.map[collectFiles].flatten
		}
		return #[file]
	}
	
	def static void copyFileOrFolder(EObject res,String path, String target) {
		var source = getFileOrFolder(res,path)
		if(source.isDirectory) {
			FileUtils.copyDirectoryToDirectory(source,new File(target))							
		} else {
			FileUtils.copyFile(source,new File(target))
		}
	}
}
