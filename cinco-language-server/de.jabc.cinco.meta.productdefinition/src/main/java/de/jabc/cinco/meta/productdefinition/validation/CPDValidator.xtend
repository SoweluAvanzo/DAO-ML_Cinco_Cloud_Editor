package de.jabc.cinco.meta.productdefinition.validation

import productDefinition.About
import productDefinition.CincoProduct
import productDefinition.Color
import productDefinition.SplashScreen
import productDefinition.ProductDefinitionPackage
import java.io.File
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.xtext.validation.Check
import org.eclipse.emf.ecore.EObject
import com.google.inject.Inject
import org.eclipse.xtext.workspace.IProjectConfigProvider
import de.jabc.cinco.meta.core.utils.WorkspaceContext
import de.jabc.cinco.meta.core.utils.ParserHelper
import org.eclipse.xtext.resource.XtextResourceSet
import com.google.inject.Provider
import productDefinition.MGLDescriptor
import org.eclipse.emf.common.util.URI
import mgl.MGLModel
import mgl.GraphModel

/**
 * Custom validation rules. 
 *
 * see http://www.eclipse.org/Xtext/documentation.html#validation
 */
 
class CPDValidator extends AbstractCPDValidator {
	
	@Inject(optional = true)
	IProjectConfigProvider projectConfigProvider;
	
	@Inject
	protected Provider<XtextResourceSet> resourceSetProvider;
	
	@Check
	def checkImage16Exists(CincoProduct cpd){
		checkPathExists(cpd.image16, ProductDefinitionPackage.Literals.CINCO_PRODUCT__IMAGE16,"Please enter the Path to the Image", cpd)
	}
	
	@Check
	def checkImage32Exists(CincoProduct cpd){
		checkPathExists(cpd.image32, ProductDefinitionPackage.Literals.CINCO_PRODUCT__IMAGE32,"Please enter the Path to the Image", cpd)		
	}
	
	@Check
	def checkImage48Exists(CincoProduct cpd){
		checkPathExists(cpd.image48, ProductDefinitionPackage.Literals.CINCO_PRODUCT__IMAGE48,"Please enter the Path to the Image", cpd)		
	}
	
	@Check
	def checkImage64Exists(CincoProduct cpd){
		checkPathExists(cpd.image64, ProductDefinitionPackage.Literals.CINCO_PRODUCT__IMAGE64,"Please enter the Path to the Image", cpd)		
	}
	
	@Check
	def checkImage128Exists(CincoProduct cpd){
		checkPathExists(cpd.image128, ProductDefinitionPackage.Literals.CINCO_PRODUCT__IMAGE128,"Please enter the Path to the Image", cpd)		
	}
	
	@Check
	def checkAboutImageExists(About about){
		checkPathExists(about.imagePath,ProductDefinitionPackage.Literals.ABOUT__IMAGE_PATH,"Please enter the Path to the Image", about)
	}
	
	@Check
	def checkBundleContainsSplashImage(SplashScreen splashscreen){
		var splashScreen = splashscreen.path.replaceAll("\"","")
		if(!splashScreen.nullOrEmpty){
			if(!splashScreen.endsWith("splash.bmp"))
				error("Please enter the path to the splash.bmp",ProductDefinitionPackage.Literals.SPLASH_SCREEN__PATH)
			var splashScreenFile = splashscreen.getFile(splashScreen)
			if(!splashScreenFile.exists){
				error("splash.bmp does not exist",ProductDefinitionPackage.Literals.SPLASH_SCREEN__PATH)
				
				}
		} else{
			error("Please enter the path to the splash.bmp",ProductDefinitionPackage.Literals.SPLASH_SCREEN__PATH)
		}
	}
	
	@Check
	def checkColor(Color color){
		if(color.r <0 ||color.r >255)
			error("Value for red must be bigger or equal 0 or smaller 256",ProductDefinitionPackage.Literals.COLOR__R)
			
		if(color.g <0 ||color.g >255)
			error("Value for green must be bigger or equal 0 or smaller 256",ProductDefinitionPackage.Literals.COLOR__G)
			
		if(color.b <0 ||color.b >255)
			error("Value for blue must be bigger or equal 0 or smaller 256",ProductDefinitionPackage.Literals.COLOR__B)
	}
	
	@Check
	def checkLinuxIcon(CincoProduct cpd){
		var linuxIcon = cpd.linuxIcon?.replaceAll("\"","")
		if (linuxIcon === null) {
			return
		}
		else if (linuxIcon.empty || !linuxIcon.endsWith(".xpm")) {
			error("Please enter the path to the xpm file",ProductDefinitionPackage.Literals.CINCO_PRODUCT__LINUX_ICON)
		}
		else {			
			var splashScreenFile = cpd.getFile(linuxIcon)
			if(!splashScreenFile.exists){
				printFileDoesNotExistError(linuxIcon,ProductDefinitionPackage.Literals.CINCO_PRODUCT__LINUX_ICON)
			}
		}
	}
	
	@Check
	def checkDiagramExtension(MGLDescriptor mglDescriptor) {
		val cpd = mglDescriptor.cincoProduct
		val mglPath = mglDescriptor.mglPath
		if(!cpd.exists(mglPath)) {
			return;
		}
		val mgl = cpd.getMGL(mglPath)
		val graphModels = mgl.graphModels.filter[!isAbstract]
		
		var allMGLPaths = cpd.mgls.map[m|m.mglPath]
		var mglPaths = allMGLPaths.filter[m|!m.equals(mglPath)]
		
		for(g : graphModels) {
			val diagramExtension = g.fileExtension
			for(mp : mglPaths) {
				val m = cpd.getMGL(mp)
				val otherGraphModels = m.graphModels.filter[!isAbstract]			
				for(o : otherGraphModels) {
					val otherDiagramExtension = o.fileExtension
					if(otherDiagramExtension.equals(diagramExtension)) {
						error(
							"DiagramExtension '" + diagramExtension
							+ "' of '" + g.name + "' in '" + mglPath
							+ "' already present in '"
							+ mp+"' on '" + o.name + "'",
							ProductDefinitionPackage.Literals.MGL_DESCRIPTOR__MGL_PATH
						)
					}
				}
			}
		}
	}
	
	@Check
	def checkMGLPath(MGLDescriptor mglDescriptor) {
		val cpd = mglDescriptor.cincoProduct
		val mglPath = mglDescriptor.mglPath
		val workspaceContext = WorkspaceContext.createInstance(projectConfigProvider, cpd)
		val exists = workspaceContext.fileExists(mglPath)
		
		if(!exists) {
			error(
				"mgl '" + mglPath
				+ "' can't be found.'",
				ProductDefinitionPackage.Literals.MGL_DESCRIPTOR__MGL_PATH
			)
		}
		
		val mglPaths = cpd.mgls.filter[m|m !== mglDescriptor].map[m|m.mglPath]
		if(mglPaths.contains(mglPath)) {
			error(
				"mgl '" + mglPath
				+ "' is referenced multiple times.'",
				ProductDefinitionPackage.Literals.MGL_DESCRIPTOR__MGL_PATH
			)
		}
	}
	
	def checkPathExists(String path, EStructuralFeature eStructuralFeature, String msg, EObject reference) {
		if(!path.nullOrEmpty &&!path.equals("\"\"")){
			if(!(new File(path.replaceAll("\"",""))).exists && !reference.getFile(path.replaceAll("\"","")).exists){
				printFileDoesNotExistError(path.replaceAll("\"",""),eStructuralFeature)
			}
		}if(!path.nullOrEmpty &&path.equals("\"\"")){
			error(msg,eStructuralFeature)
		}
	}
	
	def printFileDoesNotExistError(String fileName, EStructuralFeature feature) {
		error(String.format("File %s does not exist",fileName),feature)
	}
	
	def getCincoProduct(MGLDescriptor mglDescriptor) {
		var container = mglDescriptor.eContainer	
		while(container !== null) {
			if(container instanceof CincoProduct) {
				return container as CincoProduct
			}
			container = mglDescriptor.eContainer
		}
		return null
	}
	
	def getMGL(EObject reference, String path) {		
		val mgls = reference.getMGLs(#[path])
		return mgls.empty ? null : mgls.get(0)
	}
	
	def getMGL(GraphModel g) {
		var current = g as EObject;
		while(current !== null) {
			current = current.eContainer
			if(current instanceof MGLModel) {
				return current;
			}
		}
		return null;
	}
	
	def getMGLs(EObject reference, Iterable<String> uriStrings) {
		val workspaceContext = WorkspaceContext.createInstance(projectConfigProvider, reference)
		val uris = <URI> newArrayList()
		for(path:uriStrings) {
			val uri = workspaceContext.getFileURI(path)
			uris.add(uri)
		}
		val xtextResourceSet = resourceSetProvider.get
		val mglResources = ParserHelper.getAllResources(uris, xtextResourceSet, workspaceContext.rootURI)
		val mgls = mglResources.values().map[r| r.getContents().get(0)].filter(mgl.MGLModel)
		mgls
	}
	
	def getFile(EObject reference, String path) {
		val workspaceContext = WorkspaceContext.createInstance(projectConfigProvider, reference);
		workspaceContext.getFile(path)
	}
	
	def exists(EObject reference, String path) {
		val workspaceContext = WorkspaceContext.createInstance(projectConfigProvider, reference);
		workspaceContext.fileExists(path)
	}
}
