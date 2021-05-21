package de.jabc.cinco.meta.productdefinition.validation

import productDefinition.About
import productDefinition.CincoProduct
import productDefinition.Color
import productDefinition.SplashScreen
import productDefinition.ProductDefinitionPackage
// import de.jabc.cinco.meta.core.utils.projects.ProjectCreator
import java.io.File
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.xtext.validation.Check
import org.eclipse.emf.ecore.EObject
import com.google.inject.Inject
import org.eclipse.xtext.workspace.IProjectConfigProvider
import de.jabc.cinco.meta.core.utils.WorkspaceContext

/**
 * Custom validation rules. 
 *
 * see http://www.eclipse.org/Xtext/documentation.html#validation
 */
 
 // TODO:SAMI: test
class CPDValidator extends AbstractCPDValidator {
	
	@Inject(optional = true)
	IProjectConfigProvider projectConfigProvider;
	
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
	
	def getFile(EObject reference, String path) {
		val workspaceContext = new WorkspaceContext(projectConfigProvider, reference.eResource);
		workspaceContext.getFile(path)
	}
}
