package de.jabc.cinco.meta.core.ge.style.validation

import de.jabc.cinco.meta.core.utils.PathValidator
import java.util.ArrayList
import java.util.jar.Manifest
import org.eclipse.core.resources.IProject
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.xtext.validation.Check
import style.AbstractShape
import style.Alignment
import style.Appearance
import style.ConnectionDecorator
import style.Image
import style.NodeStyle
import style.Style
import style.StylePackage
import de.jabc.cinco.meta.core.utils.CincoUtil
import style.ContainerShape
import de.jabc.cinco.meta.core.utils.WorkspaceContext

/**
 * Custom validation rules. 
 *
 * see http://www.eclipse.org/Xtext/documentation.html#validation
 */
class StyleValidator extends JStyleValidator {

	public static val String NOT_EXPORTED = "package is not exported"
	public static val NO_PATH = 'noPathSpecified'
	public static val INVALID_PATH = 'invalidPath'

	@Check
	def checkConnectionDecoratorShapePosition(ConnectionDecorator cd) {
		if(cd.decoratorShape !== null && cd.decoratorShape instanceof AbstractShape){
			var abs = cd.decoratorShape as AbstractShape
			if(abs.position !== null)
				error("Positions are not allowed", StylePackage.Literals.CONNECTION_DECORATOR__DECORATOR_SHAPE)
		}
	}
	
	@Check
	def checkConnectionDecoratorShapeSize(ConnectionDecorator cd) {
		if(cd.decoratorShape !== null && cd.decoratorShape instanceof AbstractShape){
			var abs = cd.decoratorShape as AbstractShape
			if(abs.size.height < 0  || abs.size.width < 0 || abs.size.height > 1000  || abs.size.width > 1000 )
				warning("The size should be in [0,1000]", StylePackage.Literals.CONNECTION_DECORATOR__DECORATOR_SHAPE)
		}
	}
	
	@Check
	def checkConnectionDecoratorLocation(ConnectionDecorator cd) {	
		if(cd.location>1 || cd.location <0)
			warning("The location should be in [0,1]", StylePackage.Literals.CONNECTION_DECORATOR__LOCATION)
	}
	
	@Check
 	def checkImagePath(Image image) {
 		if (image.path.nullOrEmpty)
 			warning('No Path specified', StylePackage.Literals.IMAGE__PATH, NO_PATH)
 		
		val workspaceContext = WorkspaceContext.createInstance(projectConfigProvider, image)
 		val exists = PathValidator.checkPath(image, image.path, workspaceContext)
 		if (!exists)
 			error("Path does not exists!", StylePackage.Literals.IMAGE__PATH, INVALID_PATH)
 	}
	
	@Check
	def checkAppearanceInheritance(Appearance app) {
		var retvalList = checkInheritance(app)
		if (!retvalList.nullOrEmpty)
			error("Circle in appearance inheritances caused by: " + retvalList, StylePackage.Literals.APPEARANCE__PARENT)
	}
	
	def checkInheritance(Appearance app) {
		var current = app
		var apps = new ArrayList
		while (current !== null) {
			if (apps.contains(current.name))
				return apps
			apps.add(current.name)
			current = current.parent
		}
	}
	
	@Check
	def checkColorBackground(Appearance app){
		if (app.background.b > 255 || app.background.r > 255 || app.background.g > 255)
			error("Number(s) too large", StylePackage.Literals.APPEARANCE__BACKGROUND)
		if (app.background.b < 0 || app.background.r < 0 || app.background.g < 0)
			error("Number(s) has to be positiv", StylePackage.Literals.APPEARANCE__BACKGROUND)
	}
	
	@Check
	def checkColorForeground(Appearance app){
		if(app.foreground == null)
			return;
		if (app.foreground.b > 255 || app.foreground.r > 255 || app.foreground.g > 255)
			error("Number(s) too large", StylePackage.Literals.APPEARANCE__FOREGROUND)
		if (app.foreground.b < 0 || app.foreground.r < 0 || app.foreground.g < 0)
			error("Number(s) has to be positiv", StylePackage.Literals.APPEARANCE__FOREGROUND)
	}
	
	@Check
	def checkMainShapePosition(AbstractShape abs) {
		if (abs.eContainer instanceof NodeStyle) {
			val pos = abs.position
			if (pos instanceof Alignment)
				error("Relativ positions are not allowed in the topmost element", StylePackage.Literals.ABSTRACT_SHAPE__POSITION);
		}
	}
	
	@Check
	def checkMainShapeSize(AbstractShape abs) {
		if (abs.eContainer instanceof NodeStyle) {
			val size = abs.size
			if (size.isHeightFixed || size.isWidthFixed)
				error("Root shape's size is not allowed to be fixed", StylePackage.Literals.ABSTRACT_SHAPE__SIZE);
		}
	}
	
	@Check
	def checkAnchorShapeUnique(AbstractShape abs) {
		if(abs.anchorShape) {
			var rootShape = abs;
			var currentParentShape = abs.parentContainerShape;
			while(currentParentShape !== null) {
				rootShape = currentParentShape;
				currentParentShape = rootShape.parentContainerShape;
			}
			if(anchorShapeCount(rootShape) > 1) {
				error("Only one shape per style may be marked as an achor", StylePackage.Literals.ABSTRACT_SHAPE__ANCHOR_SHAPE);
			}
		}
	}
	
	private def anchorShapeCount(AbstractShape abs) {
		val shapesToCheck = new ArrayList();
		shapesToCheck.add(abs);
		var anchorShapeOccurances = 0;
		for(var i = 0; i < shapesToCheck.size; i++) {
			val currentShape = shapesToCheck.get(i);
			if(currentShape.anchorShape) {
				anchorShapeOccurances++;
			}
			if(currentShape instanceof ContainerShape) {
				shapesToCheck.addAll((currentShape as ContainerShape).children);
			}
		}
		return anchorShapeOccurances;
	}
	
	/* TODO:SAMI: this is not supported until java-classes are registratable
	@Check
	def checkAppearanceProviderAnnotation(Style style){
	if(style.appearanceProvider !== null){
		if(!style.appearanceProvider.isEmpty){ //recognize appearanceProvider
			val parameter = style.appearanceProvider.replace("\"", "")
			if(!parameter.equals("")){
				val correctFile = findClass(parameter)  //find the corresponding java class file
				if(correctFile !== null){
					if(correctFile.exists ){ //checks if class exists 
						val packageExport = correctFile.packageFragment.elementName
						val root = ResourcesPlugin.workspace.root
						val projects = root.projects
						for(project : projects){
							val package = findPackage(parameter);
							if(package === null){
								error("Package not found", StylePackage.Literals.STYLE__APPEARANCE_PROVIDER)
							}else{
								if(project.name.equals(package.elementName)){
									var folder = project.getFolder("META-INF")
									var manifest = folder.getFile("MANIFEST.MF")
									if(manifest.exists){
										val project1 = project;
										val isExported = findExportedPackage(project1, packageExport);
										if(!isExported){
											warning("Corresponding package is not exported", StylePackage.Literals.STYLE__APPEARANCE_PROVIDER, NOT_EXPORTED)
										}
									}
								}
							}
						}
									
							}else{ 
								error("Java Class does not exists",  StylePackage.Literals.STYLE__APPEARANCE_PROVIDER)
							}
						}
						else{
							error("Java Class does not exists", StylePackage.Literals.STYLE__APPEARANCE_PROVIDER)
						}
					}
					else{
						error("Java Class cannot be an empty String",  StylePackage.Literals.STYLE__APPEARANCE_PROVIDER)
					}
			}else{
				error("AppearanceProvider needs a Java Class as a parameter",  StylePackage.Literals.STYLE__APPEARANCE_PROVIDER)
			}	
		}
	}
	
	def findPackage(String parameter) { 
		var IType javaClass = null
		val root = ResourcesPlugin.workspace.root
		val projects = root.projects
		for(project : projects){
			var jproject = JavaCore.create(project) as IJavaProject
			if(jproject.exists){
				try {
						javaClass = jproject.findType(parameter)
						if (javaClass !== null) {
							return jproject
						}
					} catch (Exception e) {}
		}
		
		}
		return null;
	}
	
	def findExportedPackage(IProject project, String packageName){
		val iManiFile= project.getFolder("META-INF").getFile("MANIFEST.MF");
		CincoUtil.refreshFiles(null, iManiFile)	
			
			
			val manifest = new Manifest(iManiFile.getContents());
			
			var value = manifest.getMainAttributes().getValue("Export-Package");
			if (value === null){
				value = new String("");
			} 
			
			
			if (!value.contains(packageName)){
				return false;
			}else{
				return true;
			}
		
	}
	
	def findClass(String parameter){
		var IType javaClass = null
		val root = ResourcesPlugin.workspace.root
		val projects = root.projects
		for(project : projects){
			var jproject = JavaCore.create(project) as IJavaProject
			if(jproject.exists){
				try {
						javaClass = jproject.findType(parameter)
						if (javaClass !== null) {
							return javaClass 
						}
					} catch (Exception e) {}
		}
		
		}
		return javaClass
	}
	*/
}
