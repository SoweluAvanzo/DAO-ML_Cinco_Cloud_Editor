package de.jabc.cinco.meta.core.mgl.validation

import de.jabc.cinco.meta.core.utils.CincoUtil
import de.jabc.cinco.meta.core.utils.InheritanceUtil
import de.jabc.cinco.meta.core.utils.MGLUtil
import de.jabc.cinco.meta.core.utils.PathValidator
import java.io.File
import java.util.List
import mgl.Annotation
import mgl.Attribute
import mgl.BoundedConstraint
import mgl.ComplexAttribute
import mgl.ContainingElement
import mgl.Edge
import mgl.GraphModel
import mgl.GraphicalElementContainment
import mgl.GraphicalModelElement
import mgl.Import
import mgl.MGLModel
import mgl.MglPackage
import mgl.ModelElement
import mgl.Node
import mgl.NodeContainer
import mgl.PrimitiveAttribute
import mgl.ReferencedEClass
import mgl.ReferencedType
import mgl.Type
import mgl.UserDefinedType
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.validation.Check
import de.jabc.cinco.meta.core.utils.generator.GeneratorUtils
// import de.jabc.cinco.meta.core.utils.generator.ReservedJavaLangClassNames
import de.jabc.cinco.meta.core.utils.WorkspaceContext
import com.google.inject.Inject
import org.eclipse.xtext.workspace.IProjectConfigProvider
import de.jabc.cinco.meta.core.utils.IWorkspaceContext

class MGLValidator extends AbstractMGLValidator {
	
	@Inject(optional = true)
	IProjectConfigProvider projectConfigProvider;
	extension InheritanceUtil = new InheritanceUtil
	// TODO:SAMI: this is not supported until java-classes are registratable
	// extension ReservedJavaLangClassNames = new ReservedJavaLangClassNames
	public static val String NOT_EXPORTED = "package is not exported"
	
	@Check
	def checkPackageNameExists(MGLModel model) {
		if (model.package.nullOrEmpty || model.package == "\"\"") {
			error('Package name must be present.',MglPackage.Literals::MGL_MODEL__PACKAGE)
		}
	}
	
	@Check
	def checkNamedElementNameStartsWithCapital(ModelElement namedElement) {
		if (!Character::isUpperCase(namedElement.name.charAt(0))) {
			error('Name must start with a capital', MglPackage.Literals::TYPE__NAME)
					
		}
	}
	
	@Check
	def checkNamedElementNameNotUnique(ModelElement namedElement) {
		for (e: namedElement.eContainer.eAllContents.toIterable.filter(typeof(ModelElement))) {
			if (e.name == namedElement.name && e != namedElement)
				error('Name must be unique', MglPackage.Literals::TYPE__NAME) 
		}
	}
	
	@Check
	def checkNodeIncomingConnections(GraphicalModelElement elem) {
		if (elem.incomingEdgeConnections.length < 2) {
			return;
		}
		
		for (connection: elem.incomingEdgeConnections) {
			if(connection.connectingEdges === null) {
				error("Incoming Edges cannot have a don't care and other edges.", MglPackage.Literals::GRAPHICAL_MODEL_ELEMENT__INCOMING_EDGE_CONNECTIONS)
			}
		}
	}
	
	@Check
	def checkNodeOutgoingConnections(GraphicalModelElement elem) {
		if(elem.outgoingEdgeConnections.length < 2) {
			return;
		}
		
		for (connection: elem.outgoingEdgeConnections) {
			if (connection.connectingEdges === null) {
				error("Incoming Edges cannot have a don't care and other edges.", MglPackage.Literals::GRAPHICAL_MODEL_ELEMENT__OUTGOING_EDGE_CONNECTIONS)
			}
		}
	}
	
	@Check
	def checkIncomingEdgeConnectionsUnique(GraphicalModelElement elem) {
		val set = <List<Edge>> newHashSet()
		for (connection: elem.incomingEdgeConnections) {
			if (connection.connectingEdges !== null && !set.add(connection.connectingEdges)) {
				error("Given Edges should be unique", MglPackage.Literals::GRAPHICAL_MODEL_ELEMENT__INCOMING_EDGE_CONNECTIONS)
			}
		}
	}
	
	@Check
	def checkOutgoingEdgeConnectionsUnique(GraphicalModelElement elem) {
		val set = <List<Edge>> newHashSet()
		for (connection: elem.outgoingEdgeConnections) {
			if (connection.connectingEdges !== null && !set.add(connection.connectingEdges)) {
				error("Given Edges should be unique", MglPackage.Literals::GRAPHICAL_MODEL_ELEMENT__OUTGOING_EDGE_CONNECTIONS)
			}	
		}
	}
	
	@Check
	def checkUpperBound(Attribute attribute){
		if (attribute.upperBound == 0 || attribute.upperBound < -1) {
			error("Upper Bound of attribute " + attribute.name + " must be -1 or bigger than 0", MglPackage.Literals::ATTRIBUTE__UPPER_BOUND)
		}
		
		if (attribute.upperBound < attribute.lowerBound && attribute.upperBound != -1) {
			error("Upper Bound of attribute " + attribute.name + " can not be lower than Lower Bound", MglPackage.Literals::ATTRIBUTE__UPPER_BOUND)
		}
	}
	
	@Check
	def checkLowerBound(Attribute attribute){
		if(attribute.lowerBound<0)
			error("Lower Bound of attribute "+attribute.name+" cannot be lower than 0.",MglPackage.Literals::ATTRIBUTE__LOWER_BOUND)
		if(attribute.lowerBound>attribute.upperBound&&attribute.upperBound!=-1)
			error("Lower Bound of attribute "+attribute.name+" cannot be larger than Upper Bound.",MglPackage.Literals::ATTRIBUTE__LOWER_BOUND)
	}
	
	@Check
	def checkReservedWordsInAttributes(Attribute attr){
		 if(attr.name.toUpperCase=="ID")
			error("Attribute Name cannot be "+attr.name+".",MglPackage.Literals::ATTRIBUTE__NAME)
	}

	@Check
	def checkFeatureNameUnique(Attribute attr) {
		for (a: attr.modelElement.attributes) {
			if (a!= attr && a.name.equalsIgnoreCase(attr.name)) {
				error("Attribute Names must be unique", MglPackage.Literals::ATTRIBUTE__NAME)
			}
		}
		val container = attr.modelElement
		if(container instanceof Node){
			if(container.primeReference !== null){
				val name = container.primeReference.name
				if(attr.name.equalsIgnoreCase(name)){
					error("Attribute Names must be different from Prime Reference Names", MglPackage.Literals::ATTRIBUTE__NAME)
				}
			}
		}
		
		var element = attr.modelElement		
			
		var superType = element.extends
		val circlePath = element.checkMGLInheritance
		if(circlePath.nullOrEmpty) {
			while (superType !== null) {
				for (a : superType.attributes) {
					if(a.name.equalsIgnoreCase(attr.name)) {
						if(!(attr instanceof ComplexAttribute) || !(attr as ComplexAttribute).override) {
							error("Attribute Names must be unique", MglPackage.Literals::ATTRIBUTE__NAME)
						}
					}
				}
				superType = superType.extends
			}
		}
	}
	
	@Check
	def checkIsOverridenTypeSuperType(ComplexAttribute attr) {
		var e = attr.parent.extends
		while(e !== null ){
			e.attributes.filter(ComplexAttribute).forEach[at|
				if (at.name == attr.name && !attr.type.isSubType(at.type)) {
					error("Overriding attribute types must be subtype of overridden attribute types.", MglPackage.Literals::COMPLEX_ATTRIBUTE__TYPE)	
				}
			]
			
			e.attributes.filter(PrimitiveAttribute).forEach[at|
				if (at.name == attr.name) {
					error("Only Complex Attributes can be overridden.", MglPackage.Literals.COMPLEX_ATTRIBUTE__TYPE)
				}
			]
			
			e = e.extends
		}
	}
	
	def boolean isSubType(Type subtype, Type superType){
		if (subtype instanceof ModelElement && superType instanceof ModelElement) {
			var e = (subtype as ModelElement).extends
			while (e !== null) {
				if(e == superType) {
					return true
				} else {
					e = e.extends
				}
			}
		}
		return false
	}
	
	private def ModelElement parent(ComplexAttribute attribute) {
		attribute.eContainer as ModelElement
	}
	
	private def <T extends ModelElement> getExtends(ModelElement element) {
		switch(element){
			Node: element.extends
			Edge: element.extends
			UserDefinedType: element.extends
			GraphModel: element.extends
		}
	}
	
	@Check
	def checkCanResolveEClass(ReferencedEClass ref) {
		var eclass = ref.type
		
		if (eclass.eIsProxy) {
			try {
				var EObject obj
				ref.type = EcoreUtil2::resolve(eclass, obj) as EClass
			} catch(Exception e){
				error("Cannot resolve EClass: " + eclass, MglPackage.Literals::REFERENCED_ECLASS__TYPE)
			}
			
		}
	}
	
	@Check
	def checkNodeInheritsFromNonAbstractPrimeReferenceNode(Node node) {
		var currentNode = node
		val noCircles = node.checkMGLInheritance.nullOrEmpty
		if(noCircles) {
			while (currentNode.extends !== null){
				currentNode = currentNode.extends
				if (!currentNode.isIsAbstract && currentNode instanceof ReferencedType) {
					error("Node " + node.name + " inherits from non abstract prime node " + currentNode.name, MglPackage.Literals::NODE__EXTENDS)
				}
			}
		}
	}
	
	@Check
	def checkGraphicalModelElementUsesStyleAttribute(GraphicalModelElement graphicalModelElement) {
		if (!graphicalModelElement.isIsAbstract && (graphicalModelElement.usedStyle === null || graphicalModelElement.usedStyle.isEmpty)) {
			error("Non-abstract Graphical Model Elements have to reference a style.", MglPackage.Literals::TYPE__NAME)
		}
	}
	
	@Check
	def checkAbstractGraphicalModelElementHasUselessStyleAttributes(GraphicalModelElement graphicalModelElement) {
		if (graphicalModelElement.isIsAbstract && graphicalModelElement.usedStyle !== null) {
			warning("Referencing styles has no effect on abstract elements", MglPackage.Literals::TYPE__NAME)
		}
		
		if (graphicalModelElement.isIsAbstract && graphicalModelElement.annotations.exists[x | x.name.equals("icon")]) {
			warning("@icon annotation has no effect on abstract elements", MglPackage.Literals::TYPE__NAME)
		}
		
		if (graphicalModelElement.isIsAbstract && graphicalModelElement.annotations.exists[x | x.name.equals("palette")]) {
			warning("@palette annotation has no effect on abstract elements", MglPackage.Literals::TYPE__NAME)
		}
	}
	
	@Check
	def checkGraphModelContainableElements(ContainingElement model) {
		if(model.containableElements.size > 1) {
			for(containment:model.containableElements) {
				if(containment.types === null) {
					error("Dont't care type must not be accompanied by other containable elements.", MglPackage.Literals::CONTAINING_ELEMENT__CONTAINABLE_ELEMENTS);
				}
			}
		}
		
		if(model.containableElements.size == 1) {
			if(model.containableElements.get(0).types === null) {
				if(model.containableElements.get(0).upperBound == 0) {
					warning("Container element cannot contain any model elements by this definition.", MglPackage.Literals::CONTAINING_ELEMENT__CONTAINABLE_ELEMENTS)
				}
			}
		}
	}
	
	@Check
	def checkBoundedConstraintCardinality(BoundedConstraint bc) {
		var lower = bc.lowerBound
		var upper = bc.upperBound
		
		if (lower < 0) {
			error("Containment lower bound must not be lower 0.", MglPackage.Literals::BOUNDED_CONSTRAINT__LOWER_BOUND)
		}
		
		if (lower > upper && upper != -1) {
			error("Containment lower bound must not be bigger than upper bound.", MglPackage.Literals::BOUNDED_CONSTRAINT__LOWER_BOUND)
		}
		
		if (upper < -1) {
			error("Containment upper bound must not be lower -1", MglPackage.Literals::BOUNDED_CONSTRAINT__UPPER_BOUND)
		}
	}
	
	@Check
	def checkDiagramExtensionisNotEmpty(GraphModel m) {
		if(!m.isIsAbstract && m.fileExtension.nullOrEmpty) {
			error("Non-abstract graph models require a diagram extension.", MglPackage.Literals::GRAPH_MODEL__FILE_EXTENSION)
		}
	}
	
	@Check
	def checkDiagramExtensionWarningOnAbstract(GraphModel m) {
		if(m.isIsAbstract && !m.fileExtension.nullOrEmpty) {
			warning("Abstract graph models don't require a diagram extension.", MglPackage.Literals::GRAPH_MODEL__FILE_EXTENSION)
		}
	}
	
	@Check
	def checkDiagramExtensionInPolymorphy(GraphModel m) {
		if(m.fileExtension === null || m.isAbstract) {
			return
		}
		val superGraphModels = m.allSuperGraphModels.filter[p| p !== null && !p.isAbstract && p !== m]
		val collidingModels = superGraphModels.filter[p| m.fileExtension.equals(p.fileExtension)]
		if(!collidingModels.empty) {
			error("Graph model's diagram extension already used by an ancestor model.", MglPackage.Literals::GRAPH_MODEL__FILE_EXTENSION)
		}
	}
	
	@Check
	def checkDiagramExtensionInMGL(GraphModel m) {
		if(m.fileExtension === null || m.isAbstract) {
			return
		}
		val mgl = m.eContainer as MGLModel
		val graphModels = mgl.graphModels.filter[p| p !== null && !p.isAbstract && p !== m]
		val collidingModels = graphModels.filter[p| m.fileExtension.equals(p.fileExtension)]
		if(!collidingModels.empty) {
			error("Graph model's diagram extension already in use inside this MGL.", MglPackage.Literals::GRAPH_MODEL__FILE_EXTENSION)
		}
	}
	
	@Check
	def checkGraphModelIconPath(GraphModel gm) {
		if (!gm.iconPath.nullOrEmpty) {
			IWorkspaceContext.setLocalInstance(WorkspaceContext.createInstance(projectConfigProvider, gm));
			val exists = PathValidator.checkPath(gm, gm.iconPath)
 			if (!exists) {
 				error("Path does not exists!", MglPackage.Literals.GRAPH_MODEL__ICON_PATH, "The specified path: \"" + gm.iconPath +"\" does not exist")
			}
 		}
	}
	
	@Check
	def checkImportUris(Import imp) {
		try{
			IWorkspaceContext.setLocalInstance(WorkspaceContext.createInstance(projectConfigProvider, imp));
			val exists = PathValidator.checkPath(imp, imp.importURI)
		if (!exists)
			error("Path does not exists!", MglPackage.Literals.IMPORT__IMPORT_URI, "Could not load resource")
		}catch(Exception e){
			error("Could not load resource", MglPackage.Literals.IMPORT__IMPORT_URI, "Could not load resource")
		}
		
	}
	
	@Check
	def checkExternalMGLIsStealth(Import imp){
		if(!PathValidator.isRelativePath(imp.importURI)){
			IWorkspaceContext.setLocalInstance(WorkspaceContext.createInstance(projectConfigProvider, imp));
			if(!PathValidator.checkSameProjects(imp.importURI) && imp.importURI.mglImport && !imp.isStealth && !imp.isExternal){
				error("MGLs imported from foreign Projects must be imported stealthy or be marked as an external import",MglPackage.Literals.IMPORT__IMPORT_URI);
			}
		} 
			
	}
	
	def isMglImport(String importURI){
		importURI.endsWith(".mgl")
	}
	
	@Check
	def checkMGLInheritanceCircles(ModelElement me) {
		var retvalList = me.checkMGLInheritance
		if (!retvalList.nullOrEmpty) {
			if (me instanceof Node) {
				error("Circle in inheritance caused by: " + retvalList, MglPackage.Literals.NODE__EXTENDS)
			} else if (me instanceof GraphModel) {
				error("Circle in inheritance caused by: " + retvalList, MglPackage.Literals.GRAPH_MODEL__EXTENDS)
			} else if (me instanceof Edge) {
				error("Circle in inheritance caused by: " + retvalList, MglPackage.Literals.EDGE__EXTENDS)
			} else if (me instanceof UserDefinedType) {
				error("Circle in inheritance caused by: " + retvalList, MglPackage.Literals.USER_DEFINED_TYPE__EXTENDS)
			}
		}
	}
	
	@Check
	def checkNodeInheritsFromNode(Node node) {
		if (!(node instanceof NodeContainer) && node.extends !== null && (node.extends instanceof NodeContainer)) {
			error("Inheriting from Containers is not possible for Nodes.", MglPackage.Literals.NODE__EXTENDS)
		}
	}
	
	@Check
	def checkHasFinalDefaultValue(Attribute attr){
		if (attr.notChangeable) {
			if (attr.defaultValue === null || attr.defaultValue == "") {
				error("Final Attribute must have a default value", MglPackage.Literals.ATTRIBUTE__NOT_CHANGEABLE)
			}
		}
	}
	
	@Check
	def checkIsPackageNameValidJavaPackageName(MGLModel it) {
		var splitPN = package.split("\\.")
		for(part:splitPN){
			var ca = part.toCharArray
			var i=0;
			while(i<ca.length){
				if(i==0) {
					if(!Character.isJavaIdentifierStart(ca.get(i))) {
						error("Character "+ca.get(i)+" is no valid Java identifier start.",MglPackage.Literals.MGL_MODEL__PACKAGE);
					}
				}
						
				if(!Character.isJavaIdentifierPart(ca.get(i))) {
					error("Character "+ca.get(i)+" is no valid Java identifier part.",MglPackage.Literals.MGL_MODEL__PACKAGE);
				}
							
				i = i+1
			}
		}
	}
	
	@Check
	def checkReferencedNodeHasNameAttribute(ComplexAttribute attribute) {
		val modelElement = attribute.modelElement as ModelElement
		val graphModel = MGLUtil::mglModel(modelElement)
		
		val refNodes = graphModel.nodes.filter[Node n | n.name.equals(attribute.type)]
		val refEdges = graphModel.edges.filter[Edge e | e.name.equals(attribute.type)]		
		
		if ((!refNodes.nullOrEmpty || !refEdges.nullOrEmpty)) {
			if (!nodesContainsName(refNodes) && !edgesContainsName(refEdges)) {
				error("Add a String attribute \"name\" to the NodeType(s): " + refNodes.map[name], MglPackage.Literals.COMPLEX_ATTRIBUTE__TYPE)
			}
		}
	}
	
	private def edgesContainsName(Iterable<Edge> edges) {
		val parentEdges = <Edge> newArrayList()
		for (Edge e : edges) {
			var currentParent = e
			while (currentParent !== null) {
				parentEdges.add(currentParent)
				currentParent = currentParent.extends
			}
		}
		
		val remainingParents = parentEdges.filter[Edge p |  p.attributes.map[name].contains("name")]
		return !remainingParents.isEmpty 
	}
	
	private def nodesContainsName(Iterable<Node> nodes) {
		val parentNodes = <Node> newArrayList() 
		for (Node n : nodes) {
			var currentParent = n
			while (currentParent !== null) {
				parentNodes.add(currentParent)
				currentParent = currentParent.extends
			}
		}
		
		val remainingParents = parentNodes.filter[Node p | p.attributes.map[name].contains("name")]
		return !remainingParents.isEmpty 
	}
	
	@Check 
	def checkContainableElementIsIndependent(GraphicalElementContainment e) {
		if(e.containingElement.hasInheritanceCircle)
			return;
		var superType = getContainingSuperType(e.containingElement)
		while (superType!==null && (superType instanceof ContainingElement)) {
			if (superType.containableElements.exists[y | y.types.exists[x | e.types.contains(x)]]) {
				error("Containment must be independent from inherited containments", MglPackage.Literals.GRAPHICAL_ELEMENT_CONTAINMENT__TYPES)
			}
			superType = getContainingSuperType(superType)
		}
	}
	
	private def dispatch ContainingElement getContainingSuperType(ContainingElement modelElement){
		switch(modelElement){
			GraphModel: (modelElement.extends) as GraphModel
			NodeContainer: (modelElement.extends) as NodeContainer
		} 
	}
	
	private def dispatch ContainingElement getContainingSuperType(Node modelElement) {
		return null
	}
	
	private def hasInheritanceCircle(EObject element) {
		if(element instanceof ModelElement) {
			var retvalList = element.checkMGLInheritance
			!retvalList.nullOrEmpty	
		} else
			false;
	}
	
	@Check
	def checkMultipleAnnotation(Annotation annot) {
		val elementAnnotations = annot.parent.annotations
		if (elementAnnotations.filter[name == annot.name].size > 1 && !annot.isMultipleAllowed)
			error('''Multiple annotations of type: «annot.name»''', MglPackage.Literals.ANNOTATION__NAME)
	}
	
	def isMultipleAllowed(Annotation annotation) {
		#["mcam_checkmodule", "contextMenuAction", "postDelete", "postCreate"].contains(annotation.name)
	}
	
	@Check
	def checkCustomActionAnnotation(Annotation annotation){
		if (isCustomAction(annotation.name)) {
			if (annotation.value.nullOrEmpty || annotation.value.size != 1) {
				error("CustomAction needs exactly one Java Class as a Parameter", MglPackage.Literals.ANNOTATION__VALUE)
			} else {
				val parameter = annotation.value.get(0)
				if (parameter.empty) {
					error("Java Class cannot be an empty String", MglPackage.Literals.ANNOTATION__VALUE)
				} else {
					/* TODO:SAMI: this is not supported until java-classes are registratable
					checkIfJavaClassExistsAndIsAccessible(parameter)
					*/
				}
			}
		}
	}
	
	private def isCustomAction(String name) {
		switch (name) {
			case "contextMenuAction",
			case "doubleClickAction",
			case "postSelect",
			case "postCreate",
			case "postMove",
			case "postResize",
			case "preDelete",
			case "postDelete",
			case "postSave",
			case "postAttributeChange",
			case "possibleValuesProvider": {
				return true
			}
			default: {
				return false
			}
		}
	}
	
	/* TODO:SAMI: this is not supported until java-classes are registratable
	 * 
	private def checkIfJavaClassExistsAndIsAccessible(String fqClassName) {
		val correctFile = findClass(fqClassName)
		if (correctFile === null || !correctFile.exists) {
			error("Java Class does not exists", MglPackage.Literals.ANNOTATION__VALUE)
		} else {
			val package = findPackage(fqClassName)
			if (package === null) {
				error("Package not found", MglPackage.Literals.ANNOTATION__VALUE)
			} else {
				checkIfPackageIsExported(correctFile, package)
			}
		}
	}
	
	def findClass(String parameter) {
		var IType javaClass = null
		val root = ResourcesPlugin.workspace.root
		val projects = root.projects
		for( project : projects) {
			var jproject = JavaCore.create(project) as IJavaProject
			if (jproject.exists) {
				try {
					javaClass = jproject.findType(parameter)
					if (javaClass !== null) {
						return javaClass 
					}
				} catch (Exception e) {
					// nothing to do here (?)
				}
			}
		}
		return javaClass
	}
	
	def findPackage(String parameter) { 
		var IType javaClass = null
		val root = ResourcesPlugin.workspace.root
		val projects = root.projects
		for (project : projects) {
			var jproject = JavaCore.create(project) as IJavaProject
			if (jproject.exists) {
				try {
					javaClass = jproject.findType(parameter)
					if (javaClass !== null) {
						return jproject
					}
				} catch (Exception e) {
					// nothing to do here (?)
				}
			}
		}
		
		return null
	}
	
	private def checkIfPackageIsExported(IType correctFile, IJavaProject package_) {
		val packageExport = correctFile.packageFragment.elementName
		val root = ResourcesPlugin.workspace.root
		val projects = root.projects
		for (project : projects) {
			if (project.name == package_.elementName) {
				var folder = project.getFolder("META-INF")
				var manifest = folder.getFile("MANIFEST.MF")
				if (manifest.exists) {
					val isExported = findExportedPackage(project, packageExport)
					if (!isExported) {
				    	warning("Corresponding package is not exported", MglPackage.Literals.ANNOTATION__VALUE, NOT_EXPORTED)
					}
				}
			}
		}
	}
	
	
	def findExportedPackage(IProject project, String packageName) {
		val iManiFile= project.getFolder("META-INF").getFile("MANIFEST.MF")
		
		CincoUtil.refreshFiles(null, iManiFile)
		val manifest = new Manifest(iManiFile.getContents())
			
		var value = manifest.getMainAttributes().getValue("Export-Package")
		if (value === null) {
			value = ""
		} 
		
		return value.contains(packageName)
	}
	*/
	
	@Check
	def checkColorAnnotation(Annotation a) { 
		if (a.name == "color") {
			if (a.value.size == 1) { //one parameter allowed
				if (a.parent instanceof PrimitiveAttribute) { //only correct Type (EString)
					val attr = a.parent as PrimitiveAttribute
					if (attr.type.getName != "EString") {
						error("Attribute type has to be EString", MglPackage.Literals.ANNOTATION__NAME)
					}
					if (a.value.get(0) != "") {
						if(a.value.get(0) == "rgb") {
							if(!attr.defaultValue.empty || attr.defaultValue.empty != "") { //correct default values
								val defaultValue = attr.defaultValue
								var result = defaultValue.split(",")
								if(result.size != 3) {
									error("default value doesn't have a RGB-scheme", MglPackage.Literals.ANNOTATION__NAME)
								} else {
									checkRGBDefaultValues(result)
								}
							}
						} else if (a.value.get(0) == "hex") { //check default values
							if (!attr.defaultValue.empty || attr.defaultValue != "") {
								val defaultValue = attr.defaultValue
								if (defaultValue.length == 7) {
									if (defaultValue.startsWith("#")) {
										for (var i = 1; i < defaultValue.length ; i++) {
											val value = defaultValue.charAt(i)
											if (value < '0' || value > '9' ) {
												if (!checkLetter(value)) {
													error("hex values are between 0 and 9 or A and F",  MglPackage.Literals.ANNOTATION__NAME)
												}
											}
										}
									} else {
										error("default value of hex must start with '#'",  MglPackage.Literals.ANNOTATION__NAME)
									}
								} else {
									error("default value does not have a hex-scheme",  MglPackage.Literals.ANNOTATION__NAME)
								}
							}
						} else if (a.value.get(0) == "rgba") { //check default values
							if(!(attr.defaultValue.empty) || attr.defaultValue.empty != "") { 
								val defaultValue = attr.defaultValue
								var result = defaultValue.split(",")
								if (result.size != 4){
									error("default value doesn't have a RGBA-scheme", MglPackage.Literals.ANNOTATION__NAME)
								} else {
									checkRGBDefaultValues(result)
									var a_string = result.get(3)
									try {
										var alpha = Integer.parseInt(a_string)
										if (alpha < 0 || alpha > 255) {
											error("alpha-value has to be bigger or equal than 0 and lower or equal than 255 ", MglPackage.Literals.ANNOTATION__NAME )
										}
									} catch(Exception e){
										error("Please enter only numbers as default value", MglPackage.Literals.ANNOTATION__NAME)
									}
								}
							}	
						} else {
							error("color Annotation needs one parameter like rgb, rgba or hex", MglPackage.Literals.ANNOTATION__VALUE)
						}
					}
				} else {
					error("Attribute has to be a PrimitiveAttribute ", MglPackage.Literals.ANNOTATION__NAME)
				}
			} else {
				error("color Annotation needs exactly one parameter like rgb, hex or rgba", MglPackage.Literals.ANNOTATION__VALUE)
			}
		}
	}

	private def checkRGBDefaultValues(String[] result) {
		val r_string = result.get(0)
		val g_string = result.get(1)
		val b_string = result.get(2)
		
		try {
			val r = Integer.parseInt(r_string)	
			val g = Integer.parseInt(g_string)
			val b = Integer.parseInt(b_string)
							
			if (r < 0 || r > 255) {
				error("r-value has to be bigger or equal than 0 and lower or equal than 255", MglPackage.Literals.ANNOTATION__NAME)
			}
			
			if (g < 0 || g > 255) {
				error("g-value has to be bigger or equal than 0 and lower or equal than 255",  MglPackage.Literals.ANNOTATION__NAME)
			}
			
			if (b < 0 || b > 255) {
				error("b-value has to be bigger or equal than 0 and lower or equal than 255",  MglPackage.Literals.ANNOTATION__NAME)
			}					
		} catch(Exception e) {
			error("Please enter only numbers as default value", MglPackage.Literals.ANNOTATION__NAME)
		}
	}
	
	private def checkLetter(char c) {
		switch (c.toString) {
			case 'A',
			case 'B',
			case 'C',
			case 'D',
			case 'E',
			case 'F': {
				return true
			}
			default: {
				return false
			}
		}
	}
	
	@Check
	def checkFileAnnotations(Annotation a) {
		if (a.name == "file" && a.parent instanceof PrimitiveAttribute) { //only correct Type (EString)
			val attr = a.parent as PrimitiveAttribute
			if (attr.type.getName != "EString") {
				error("Type has to be EString",MglPackage.Literals.ANNOTATION__NAME)
			}
			
			if (!attr.defaultValue.empty || attr.defaultValue.empty != "") { //only correct default values
				val defaultValue = attr.defaultValue
				try {
					val file = new File(defaultValue);
					if (!file.exists) {
						error("Wrong Path: File doesn't exists", MglPackage.Literals.ANNOTATION__NAME)
					}
				} catch (Exception e){
					error("Wrong Path: File doesn't exists", MglPackage.Literals.ANNOTATION__NAME)
				}
			}
		}
		
	}
	
	
	@Check
	def checkImportCycleExists(Import imprt) {
		if(!imprt.isStealth) {
			val originalMGLModel = imprt.eContainer as MGLModel
			IWorkspaceContext.setLocalInstance(WorkspaceContext.createInstance(projectConfigProvider, originalMGLModel));
			val importedMGLModel = CincoUtil.getImportedMGLModel(imprt)
			
			if(importedMGLModel !== null) {
				val importsToCheck = importedMGLModel.imports.filter[!isStealth].toList
				val alreadyVisitedMGLModel = newLinkedList(originalMGLModel, importedMGLModel)
				
				for(var i = 0; i < importsToCheck.size; i++) {
					val currentImportedMGL = CincoUtil.getImportedMGLModel(importsToCheck.get(i))
					if(currentImportedMGL !== null) {
						if(alreadyVisitedMGLModel.exists[MGLUtil.equalMGLModels(it, currentImportedMGL)]) {
							error("Cyclic imports detected at " + GeneratorUtils.instance.getFileName(currentImportedMGL) + ".mgl", MglPackage.Literals.IMPORT__IMPORT_URI)
							return
						} else {
							alreadyVisitedMGLModel.add(currentImportedMGL)
							importsToCheck.addAll(currentImportedMGL.imports.filter[!isStealth].toList)
						}
					}
				}
			}
		}
		// No cycle found
		return
	}
	
	
	@Check
	def checkExternalImportsPointToMGLs(Import imprt) {
		if(imprt.isExternal && !imprt.importURI.endsWith(".mgl")) {
			error("Imports of external resources may only import MGLs", MglPackage.Literals.IMPORT__IMPORT_URI);
		}
	}
	
	
	@Check
	def checkModelElementNameForNameClashes(ModelElement me) {
		val mgl = MGLUtil.getMglModel(me)
		val imports = mgl.imports.filter[!isStealth]
		imports.forEach[imp |
			IWorkspaceContext.setLocalInstance(WorkspaceContext.createInstance(projectConfigProvider, me));
			val imp_mgl = CincoUtil.getImportedMGLModel(imp);
			if(imp_mgl !== null) {
				MGLUtil.modelElements(imp_mgl, true).forEach[
					if(!MGLUtil.equalModelElement(it, me) && it.name == me.name) {
						error("This name already exists in the imported MGL \"" + imp.name + "\"", MglPackage.Literals.TYPE__NAME);
					}
				]
			}
		]
	}
	
	@Check
	def checkImportForNameClashes(Import imprt) {
		if(!imprt.isStealth) {
			val mgl = imprt.eContainer as MGLModel
			val imports = mgl.imports.filter[!isStealth]
			IWorkspaceContext.setLocalInstance(WorkspaceContext.createInstance(projectConfigProvider, mgl));
			val importedMGL = CincoUtil.getImportedMGLModel(imprt)
			imports.forEach[imp |
				if(imprt !== imp) {
					MGLUtil.modelElements(CincoUtil.getImportedMGLModel(imp), true).forEach[importedMe |
						MGLUtil.modelElements(importedMGL).forEach[
							if(it.name == importedMe.name) {
								error("The model element name \"" + it.name + "\" leads to a name clash, as " +
									"it exists in the imported MGLs \"" + imp.name + "\" and \"" + imprt.name + "\".",
									MglPackage.Literals.IMPORT__NAME);
							}
						]
					]
				}
			]
		}
	}
}
