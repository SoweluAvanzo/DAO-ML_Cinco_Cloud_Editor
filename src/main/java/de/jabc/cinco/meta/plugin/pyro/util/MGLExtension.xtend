package de.jabc.cinco.meta.plugin.pyro.util

import de.jabc.cinco.meta.core.utils.InheritanceUtil
import java.util.Collections
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedList
import java.util.List
import java.util.Map
import java.util.Set
import java.util.function.Function
import java.util.stream.Collectors
import mgl.Annotatable
import mgl.Annotation
import mgl.Attribute
import mgl.ComplexAttribute
import mgl.ContainingElement
import mgl.Edge
import mgl.Enumeration
import mgl.GraphModel
import mgl.GraphicalElementContainment
import mgl.GraphicalModelElement
import mgl.MGLModel
import mgl.ModelElement
import mgl.Node
import mgl.NodeContainer
import mgl.PrimitiveAttribute
import mgl.ReferencedEClass
import mgl.ReferencedModelElement
import mgl.ReferencedType
import mgl.Type
import mgl.UserDefinedType
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.ENamedElement
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature
import productDefinition.CincoProduct
import style.ContainerShape
import style.EdgeStyle
import style.Ellipse
import style.Image
import style.NodeStyle
import style.Styles
import java.util.regex.Pattern
import java.util.ArrayList
import de.jabc.cinco.meta.core.utils.MGLUtil

class MGLExtension {

	public static int DEFAULT_WIDTH = 40
	public static int DEFAULT_HEIGHT = 40
	
	Map<MGLModel, Iterable<ModelElement>> mglElementMap;
	Map<MGLModel, Iterable<Node>> mglNodeMap;
	Map<MGLModel, Iterable<Edge>> mglEdgeMap;
	Map<MGLModel, Iterable<GraphModel>> mglGraphmodelMap;
	
	Map<ContainingElement, Iterable<ModelElement>> elementMap;
	Map<ContainingElement, Iterable<Node>> nodeMap;
	Map<ContainingElement, Iterable<Edge>> edgeMap;

	protected extension Escaper = new Escaper

	static def instance() {
		new MGLExtension()
	}

	private new() {
		mglElementMap = new HashMap
		mglNodeMap = new HashMap
		mglEdgeMap = new HashMap
		mglGraphmodelMap = new HashMap
		
		elementMap = new HashMap
		nodeMap = new HashMap
		edgeMap = new HashMap
	}

	static def String[] primitiveETypes() { // Needed to get EReal, EDate and EDateTime
		return #["EString", "EBoolean", "EInt", "EDouble", "EShort", "ELong", "EBigInteger", "EFloat", "EBigDecimal",
			"EReal", "EByte", "EChar", "EDate", "EDateTime"]
	}

	/**
	 * Returns true, if the given elements generated abstraction has to extend a Container-Class
	 */
	def hasToExtendContainer(ModelElement me) {
		me instanceof NodeContainer && (me as NodeContainer).extends !== null &&
			!((me as NodeContainer).extends instanceof NodeContainer)
	}

	/**
	 * see nodes
	 */
	@Deprecated
	def nodesTopologically(GraphModel g) {
		g.nodes
	}

	/**
	 * see nodes
	 */
	@Deprecated
	def nodesTopologically(MGLModel g) {
		g.nodes
	}
	
	/**
	 * Returns all nodes that can be contained by the given GraphModel, topologically
	 */
	def nodes(GraphModel g) {
		if (nodeMap.containsKey(g)) {
			return nodeMap.get(g)
		}
		val elements = g.elementsTopologicallyOf(Node)
		nodeMap.put(g, elements)
		elements
	}
	
	/**
	 * Returns all nodes that can be contained by the GraphModels of the given MGLModel, topologically
	 */
	def nodes(MGLModel model) {
		if (mglNodeMap.containsKey(model)) {
			return mglNodeMap.get(model)
		}
		val elements = model.elementsTopologicallyOf(Node)
		mglNodeMap.put(model, elements)
		elements
	}

	/**
	 * see edges
	 */
	@Deprecated
	def edgesTopologically(GraphModel g) {
		g.edges
	}

	/**
	 * Returns all nodes that can be contained by the given GraphModel, topologically
	 */
	def edges(GraphModel g) {
		if (edgeMap.containsKey(g)) {
			return edgeMap.get(g)
		}
		val elements = g.elementsTopologicallyOf(Edge)
		edgeMap.put(g, elements)
		elements
	}
	
	/**
	 * see edges
	 */
	@Deprecated
	def edgesTopologically(MGLModel g) {
		g.edges
	}
	
	/**
	 * Returns all nodes that can be contained by the GraphModels of the given MGLModel, topologically
	 */
	def edges(MGLModel model) {
		if (mglEdgeMap.containsKey(model)) {
			return mglEdgeMap.get(model)
		}
		val elements = model.elementsTopologicallyOf(Edge)
		mglEdgeMap.put(model, elements)
		elements
	}

	/**
	 * Returns all GraphModels that are contained by the given MGLModel
	 */
	def graphmodels(MGLModel g) {
		if (mglGraphmodelMap.containsKey(g)) {
			return mglGraphmodelMap.get(g)
		}
		val elements = g.getGraphModels()
		mglGraphmodelMap.put(g, elements)
		elements
	}

	/**
	 * see elementsTopologically
	 */
	def containments(GraphModel g) {
		g.elements
	}
	
	/**
	 * Returns value of elementsTopologically, but only of a given class
	 */
	def <T> elementsTopologicallyOf(GraphModel g, Class<T> clazz) {
		g.elements.filter(clazz)
	}
	
	/**
	 * Returns value of elementsTopologically, but only of a given class
	 */
	def <T> elementsTopologicallyOf(MGLModel g, Class<T> clazz) {
		g.elements.filter(clazz)
	}
	
	/**
	 * Returns all Elements that can be contained by the given MGLModel
	 */
	def elements(MGLModel model) {
		model.elementsTopologically
	}
	
	def elements(ContainingElement g) {
		g.elementsTopologically.toSet
	}
	
	def nodesAndEdges(GraphModel g) {
		(g.nodes + g.edges)
	}
	
	/**
	 * Returns all Elements that can be contained by the given ContainingElement
	 */
	def Iterable<ModelElement> elementsTopologically(ContainingElement g) {
		if (elementMap.containsKey(g)) {
			return elementMap.get(g)
		}
		
		val result = g.elementsTopologically(new HashSet<ModelElement>)
		
		elementMap.put(g, result.sortTopologically.toSet)
		elementMap.get(g)
	}
	
	/**
	 * Returns all Elements that are related to the given ContainingElement
	 */
	private def Iterable<ModelElement> elementsTopologically(ContainingElement g, Set<ModelElement> cache) {
		var elements = new HashSet<ModelElement>
		var result = new HashSet<ModelElement>
		
		// resolve types that are directly related
		val directContainable = g.containableElements.map[types].flatten
		val attributes = (g as ModelElement).attributeElements
		elements.addAll(attributes);
		elements.addAll(directContainable)
		
		// resolve hierarchical subTypes and superTypes, that can be contained
		for(e : elements) {
			if(!cache.contains(e) && !result.contains(e))
				result += e.collectTypes(cache)
		}
		
		result
	}
	
	def Set<ModelElement> collectTypes(ModelElement e, Set<ModelElement> cache) {
		var result = new HashSet<ModelElement>
		val subTypes = e.resolveAllSubTypesAndType;
		val superTypesAndType = e.resolveSuperTypesAndType.filter(ModelElement)
		val superAndSubTypes = subTypes + superTypesAndType
		
		for(s : superAndSubTypes) {
			if(!cache.contains(s) && !result.contains(s)) {
				result.add(s)
				cache.add(s)
				
				// handle attributes
				result += s.collectAttributes(cache)
				
				// find and handle edges
				result += s.collectEdges(cache)
				
				// handle contained elements
				result += s.collectNested(cache)
			}
		}
		result
	}
	
	def Set<ModelElement> collectAttributes(ModelElement s, Set<ModelElement> cache) {
		var result = new HashSet<ModelElement>
		val attributeTypes = (s as ModelElement).attributeElements
		for(t : attributeTypes) {
			result.addAll(
				t.collectTypes(cache)
			);
		}
		result
	}
	
	def Set<ModelElement> collectEdges(ModelElement s, Set<ModelElement> cache) {
		var result = new HashSet<ModelElement>
		if(s instanceof Node) {
			val edges = s.possibleIncoming + s.possibleOutgoing
			for(edge : edges) {
				if(!cache.contains(edge) && !result.contains(edge)) {
					result.addAll(
						edge.collectTypes(cache)
					);
				}
			}					
		}
		result
	}
	
	def Set<ModelElement> collectNested(ModelElement s, Set<ModelElement> cache) {
		var result = new HashSet<ModelElement>
		if(s instanceof ContainingElement) {
			val newNested = s.elementsTopologically((cache + result).toSet)
			result.addAll(newNested)
		}
		result
	}
	
	def getAttributeElements(ModelElement e) {
		(e as ModelElement).attributesExtended.filter[!isPrimitive].filter(ComplexAttribute).map[type].filter(ModelElement).toSet
	}
	
	/**
	 * Returns all Elements that can be contained by the given MGLModel
	 */
	def elementsTopologically(MGLModel model) {
		if (mglElementMap.containsKey(model)) {
			return mglElementMap.get(model)
		}
		// resolve types that can be contained
		val elements = new HashSet<ModelElement>
		elements.addAll(model.nodes)
		elements.addAll(model.edges)
		elements.addAll(model.graphModels)
		elements.addAll(model.types.filter(ModelElement))
		
		mglElementMap.put(model, elements.sortTopologically.toSet)
		mglElementMap.get(model)
	}
	
	/**    ************************************************************************************    */
	
	private def sortTopologically(Iterable<ModelElement> elements) {
		elements.sortBy[inheritanceScore]
	}

	private def dispatch int inheritanceScore(Node element) {
		var i = 0
		if (element.extends !== null) {
			i--
			i += element.extends.inheritanceScore
		}
		i * (-1)
	}

	private def dispatch int inheritanceScore(Edge element) {
		var i = 0
		if (element.extends !== null) {
			i++
			i += element.extends.inheritanceScore
		}
		i * (-1)
	}

	private def dispatch int inheritanceScore(UserDefinedType element) {
		var i = 0
		if (element.extends !== null) {
			i++
			i += element.extends.inheritanceScore
		}
		i * (-1)
	}

	private def dispatch int inheritanceScore(ModelElement element) {
		var i = 0
		if (element instanceof GraphModel) {
			if ((element as GraphModel).extends !== null) {
				i++
				i += element.extends.inheritanceScore
			}
			i * (-1)
		} else {
			var el = element as NodeContainer
			if ((el).extends !== null) {
				i++
				i += (el).extends.inheritanceScore
			}
			i * (-1)
		}
	}

	def elements(EPackage g) {
		return g.EClassifiers.filter(EClass)
	}

	def elementsAndEnums(EPackage g) {
		return g.EClassifiers.filter(EClass) + g.EClassifiers.filter(EEnum)
	}

	def elementsAndGraphmodels(MGLModel model) {
		return model.elementsTopologically
	}

	def elementsAndGraphmodels(GraphModel model) {
		return model.elementsTopologically
	}

	def boolean isModelElement(Attribute attribute) {
		if(attribute instanceof ComplexAttribute) {
			val modelPackage = attribute.modelPackage as MGLModel
			return modelPackage.elements.filter [!isType].exists[it.equals(attribute.type)]
		}
		return false;	
	}

	/**    ************************************************************************************    */
	def String getFileName(GraphModel model) {
		model.mglModel.fileName
	}
	
	def String getFileName(MGLModel model) {
		val pattern = Pattern.compile("[\\w\\-. ]+(?=.mgl$)")
		var uriString = ""
		val modelResourceURI = model.eResource.URI
		val platformString = modelResourceURI.toPlatformString(true)
		if (platformString !== null) {
			uriString = platformString
		} else {
			uriString = modelResourceURI.toString()
		}
		val matcher = pattern.matcher(uriString)
		if (matcher.find()) {
			return matcher.group();
		}
		throw new IllegalStateException("The name of the MGL model \"" + model.package +
			"\" could not be resolved properly.")
	}

	def static MGLModel mglModel(ModelElement modelElement) {
		if(modelElement.eContainer instanceof MGLModel) {
			return (modelElement.eContainer as MGLModel)
		} else {
			return mglModel(modelElement.eContainer as ModelElement) as MGLModel
		}
	}

	def dispatch MGLModel MGLModel(ModelElement eObject) {
		val mglModel = eObject.getPackageContainer as MGLModel
		return mglModel
	}
	
	def dispatch MGLModel MGLModel(Type eObject) {
		val mglModel = eObject.getPackageContainer as MGLModel
		return mglModel
	}
	
	def dispatch MGLModel MGLModel(Attribute eObject) {
		val mglModel = eObject.getPackageContainer as MGLModel
		return mglModel
	}
	
	def dispatch MGLModel MGLModel(ContainingElement eObject) {
		val mglModel = eObject.getPackageContainer as MGLModel
		return mglModel
	}
	
	def dispatch EObject getModelPackage(EObject eObject) {
		val modelPackage = eObject.getPackageContainer as EObject
		return modelPackage
	}
	
	def dispatch EPackage getModelPackage(ENamedElement eObject) {
		val modelPackage = eObject.getPackageContainer as EPackage
		return modelPackage
	}
	
	def dispatch EPackage getModelPackage(EPackage eObject) {
		return eObject
	}
	
	def dispatch MGLModel getModelPackage(ModelElement eObject) {
		val modelPackage = eObject.getPackageContainer as MGLModel
		return modelPackage
	}
	
	def dispatch MGLModel getModelPackage(GraphicalElementContainment eObject) {
		val modelPackage = eObject.getPackageContainer as MGLModel
		return modelPackage
	}
	
		def dispatch MGLModel getModelPackage(Attribute eObject) {
		val modelPackage = eObject.getPackageContainer as MGLModel
		return modelPackage
	}
	
	def EObject getPackageContainer(EObject eObject) {
		val alreadyVisited = #[eObject]
		var candidate = eObject as EObject
		while (candidate !== null) {
			if (candidate instanceof MGLModel || candidate instanceof EPackage) {
				return candidate
			}
			candidate = candidate.eContainer
			if (alreadyVisited.contains(candidate)) {
				return null
			}
		}
		return null
	}

	def isFile(Attribute attr) {
		if (attr instanceof PrimitiveAttribute) {
			return attr.annotations.exists[name.equals("file")]
		}
		false
	}

	def getFile(Attribute attr) {
		if (attr instanceof PrimitiveAttribute) {
			return attr.annotations.findFirst[name.equals("file")]
		}
		null
	}

	def attributeTypeName(Attribute attr) {
		if (attr instanceof PrimitiveAttribute) {
			return attr.type.getName
		}
		if (attr instanceof ComplexAttribute) {
			return attr.type.name.fuEscapeJava
		}
		throw new IllegalStateException("Exhaustive if");
	}

	def type(EAttribute attr) {
		attr.getEAttributeType
	}

	def type(EReference attr) {
		attr.getEReferenceType
	}

	def htmlType(Attribute attr) {
		switch (attr.attributeTypeName) {
			case "EBoolean": return '''checkbox'''
			case "EInt": return '''number'''
			case "ELong": return '''number'''
			case "EBigInteger": return '''number'''
			case "EByte": return '''number'''
			case "EShort": return '''number'''
			case "EFloat": return '''number'''
			case "EBigDecimal": return '''number'''
			case "EDouble": return '''number'''
			default: return '''text'''
		}
	}

	def javaType(Attribute attr, GraphModel g) {
		javaType(attr, g.mglModel)
	}

	def javaType(Attribute attr, MGLModel g) {
		if (!attr.isPrimitive) {
			return '''«attr.apiFQN»'''
		}
		if (attr.attributeTypeName.getEnum(g) !== null) {
			return '''«attr.apiFQN»'''
		}
		switch (attr.attributeTypeName) {
			case "EBoolean": {
				if (attr.list) {
					return '''Boolean'''
				}
				return '''boolean'''
			}
			case "EInt": {
				if (attr.list) {
					return '''Integer'''
				}
				return '''int'''
			}
			case "EDouble": {
				if (attr.list) {
					return '''Double'''
				}
				return '''double'''
			}
			case "ELong": {
				if (attr.list) {
					return '''Integer'''
				}
				return '''int'''
			}
			case "EBigInteger": {
				if (attr.list) {
					return '''Integer'''
				}
				return '''int'''
			}
			case "EByte": {
				if (attr.list) {
					return '''Integer'''
				}
				return '''int'''
			}
			case "EShort": {
				if (attr.list) {
					return '''Integer'''
				}
				return '''int'''
			}
			case "EFloat": {
				if (attr.list) {
					return '''Double'''
				}
				return '''double'''
			}
			case "EBigDecimal": {
				if (attr.list) {
					return '''Double'''
				}
				return '''double'''
			}
			default:
				return '''String'''
		}
	}

	def javaDBType(Attribute attr, GraphModel g) {
		javaDBType(attr, g.mglModel)
	}

	def javaDBType(Attribute attr, MGLModel g) {
		if (attr.attributeTypeName.getEnum(g) !== null) {
			return g.apiFQN + "." + attr.attributeTypeName.fuEscapeJava
		}
		if (!attr.isPrimitive) {
			return '''«attr.attributeTypeName.fuEscapeJava»'''
		}
		switch (attr.attributeTypeName) {
			case "EBoolean": {
				if (attr.list) {
					return '''Boolean'''
				}
				return '''boolean'''
			}
			case "EInt": {
				if (attr.list) {
					return '''Integer'''
				}
				return '''int'''
			}
			case "EDouble": {
				if (attr.list) {
					return '''Double'''
				}
				return '''double'''
			}
			case "ELong": {
				if (attr.list) {
					return '''Integer'''
				}
				return '''int'''
			}
			case "EBigInteger": {
				if (attr.list) {
					return '''Integer'''
				}
				return '''int'''
			}
			case "EByte": {
				if (attr.list) {
					return '''Integer'''
				}
				return '''int'''
			}
			case "EShort": {
				if (attr.list) {
					return '''Integer'''
				}
				return '''int'''
			}
			case "EFloat": {
				if (attr.list) {
					return '''Double'''
				}
				return '''double'''
			}
			case "EBigDecimal": {
				if (attr.list) {
					return '''Double'''
				}
				return '''double'''
			}
			default:
				return '''String'''
		}
	}

	def ecoreType(EReference attr, EPackage g) {
		if (attr.type instanceof EEnum) {
			return g.apiFQN + "." + attr.type.name.fuEscapeJava
		}
		return '''«attr.type.name.fuEscapeJava»'''
	}
	
	def getPrimeReferencedElements(GraphModel model) {
		val primeNodes = model.elements.filter(GraphicalModelElement).filter[isPrime].filter(Node)
		val allPrimeReferences = primeNodes.map[primeReference].map[it.type].filter(ModelElement).toSet
		allPrimeReferences 
	}

	def getPrimeReferencedElements(MGLModel model) {		
		val primeNodes = model.elements.filter(GraphicalModelElement).filter[isPrime].filter(Node)
		val allPrimeReferences = primeNodes.map[primeReference].map[it.type].filter(ModelElement).toSet
		allPrimeReferences
	}
	
	def resolveAllPrimeReferencedGraphModels(MGLModel modelPackage) {
		val primeReferencedElements = modelPackage.primeReferencedElements
		primeReferencedElements.map[it.graphModels].flatten.filter[!isAbstract].toSet
	}
	
	def resolveAllPrimeReferencedGraphModels(GraphModel g) {
		val primeReferencedElements = g.primeReferencedElements
		primeReferencedElements.map[it.graphModels].flatten.filter[!isAbstract].toSet
	}

	def getPrimeReferencingElements(Type referenced, Set<MGLModel> referencingSet) {
		var referencingElements = new HashSet<Node>
		for (model : referencingSet) {
			var primeReferencesOfModel = model.nodes.filter[primeReference !== null];
			referencingElements.addAll(
				primeReferencesOfModel.map [ it |
				val ref = getPrimeReference
				if (ref instanceof ReferencedModelElement) {
					val refElem = ref.referencedElement
					val refElemId = refElem.entityFQN.toString
					val referencedId = referenced.entityFQN.toString

					if (refElemId.equals(referencedId)) {
						return it
					}
				} else if (ref.referencedElementAttributeName.equals(referenced.name))
					return it
				return null
			])
		}
		referencingElements.filter[it !== null]
	}

	def getPrimeReferencingElements(MGLModel reference, ModelElement referenced) {
		var referencingSet = new HashSet
		referencingSet.add(reference)
		referenced.getPrimeReferencingElements(referencingSet)
	}

	def getPrimeReferencingElements(MGLModel model, GraphModel refG) {
		model.primeRefs.filter[referencedElement.graphModels.contains(refG)].toSet
	}

	def getPrimeReferencingElements(GraphModel model, GraphModel refG) {
		model.primeRefs.filter[referencedElement.graphModels.contains(refG)].toSet
	}

	def getAllPrimeRefs(MGLModel model) {
		model.primeRefs + model.ecorePrimeRefs
	}

	def getPrimeRefs(MGLModel model) {
		model.nodes.filter[primeReference !== null].map[primeReference].filter(ReferencedModelElement).toSet
	}

	def getPrimeRefs(GraphModel model) {
		model.nodes.filter[isPrime].map[primeReference].filter(ReferencedModelElement).toSet
	}

	def getEcorePrimeRefs(MGLModel model) {
		model.nodes.filter[isPrime].map[primeReference].filter(ReferencedEClass).toSet
	}

	def getEcorePrimeRefsModels(MGLModel model) {
		model.ecorePrimeRefs.map[type].map[EPackage].filter[it instanceof EPackage].toList.stream.distinct.collect(
			Collectors.toList()).map[EPackage.cast(it)]
	}

	def getEcorePrimeRefsElements(MGLModel g, EPackage ecore) {
		g.ecorePrimeRefs.filter[it.type.EPackage.equals(ecore)]
	}
	
	def getGraphModels(ModelElement me) {
		val modelPackage = me.modelPackage as MGLModel
		val allGraphModels = modelPackage.graphmodels
		val types = me.resolveSuperTypesAndType
		val result = new HashSet<GraphModel>
		for(g:allGraphModels) {
			if(!result.contains(g)) {
				for(t:types) {
					if(g.elements.contains(t)) {
						for(typeOfG:g.resolveSubTypesAndType) {
							if(!result.contains(typeOfG)) {
								result.add(typeOfG)
							}
						}
					}
				}
			}
		}
		result
	}

	def getReferencedElement(ReferencedModelElement rt) {
		rt.type
	}

	def getReferenceName(ReferencedModelElement rt) {
		rt.name
	}

	def getReferencedElementAttributeName(ReferencedType rt) {
		val a = rt.annotations.findFirst[name.equals("pvLabel")]
		if (a !== null) {
			return a.value.get(0)
		}
		return null
	}

	def hasClassAnnotation(Annotation a) {
		val ca = #[
			"postDelete",
			"postCreate",
			"preDelete",
			"postMove",
			"postResize",
			"postSelect",
			"postAttributeChange",
			"generatable",
			"contextMenuAction",
			"doubleClickAction",
			"mcam_checkmodule",
			"preSave",
			"pyroEditorButton",
			"pyroInterpreter"
		]
		ca.contains(a.name)
	}

	def hasIncludeResourcesAnnotation(GraphModel g) {
		g.annotations.exists[name.equals("pyroGeneratorResource")]
	}

	def hasIncludeJARAnnotation(GraphModel g) {
		g.annotations.exists[name.equals("pyroAdditionalJAR")]
	}

	def defaultRights(GraphModel g) {
		if (g.annotations.exists[name.equals("pyroUserRights")]) {
			return g.annotations.findFirst[name.equals("pyroUserRights")].value.filter [ r |
				r.equals("create") || r.equals("update") || r.equals("delete")
			]
		}
		#[]
	}

	def dispatch boolean hasAppearanceProvider(MGLModel model, Styles styles) {
		model.elements.filter[!isIsAbstract].exists[it.hasAppearanceProvider(styles)]
	}
	
	def hasChecks(GraphModel g) {
		g.annotations.exists[name.equals("mcam")]
	}

	def isGenerating(GraphModel g) {
		g.annotations.exists[name.equals("generatable")]
	}

	def isInterpreting(GraphModel g) {
		g.annotations.exists[name.equals("pyroInterpreter")]
	}

	def interperters(GraphModel g) {
		g.annotations.filter[name.equals("pyroInterpreter")]
	}

	def generators(GraphModel g) {
		g.annotations.filter[name.equals("generatable")]
	}

	def isHidden(Attribute attr) {
		(attr.annotations.exists[name.equals("propertiesViewHidden")])
	}

	def isReadOnly(Attribute attr) {
		(attr.annotations.exists[name.equals("readOnly")])
	}

	def hasPostCreateHook(ModelElement me) {
		me.hasHook("postCreate")
	}

	def hasJumpToAnnotation(ModelElement me) {
		me.hasAnnotation("jumpToPrime", true)
	}

	def getPostCreateHook(ModelElement me) {
		me.getHookFQN("postCreate")
	}

	def hasPostDeleteHook(ModelElement me) {
		me.hasHook("postDelete")
	}

	def getPostDeleteHook(ModelElement me) {
		me.getHookFQN("postDelete")
	}

	def hasPreDeleteHook(ModelElement me) {
		me.hasHook("preDelete")
	}

	def getPreDeleteHook(ModelElement me) {
		me.getHookFQN("preDelete")
	}

	def getEditorButtons(GraphModel g) {
		g.annotations.filter[name.equals("pyroEditorButton")]
	}

	def hasPostMove(ModelElement me) {
		me.hasHook("postMove")
	}

	def getPostMoveHook(ModelElement me) {
		me.getHookFQN("postMove")
	}

	def hasPostResize(ModelElement me) {
		me.hasHook("postResize")
	}

	def getPostResizeHook(ModelElement me) {
		me.getHookFQN("postResize")
	}

	def hasPostSelect(ModelElement me) {
		me.hasHook("postSelect")
	}

	def getPostSelectHook(ModelElement me) {
		me.getHookFQN("postSelect")
	}

	def hasPostAttributeValueChange(ModelElement me) {
		me.hasHook("postAttributeChange")
	}

	def getPostAttributeValueChange(ModelElement me) {
		me.getHookFQN("postAttributeChange")
	}

	def hasPreSave(ModelElement me) {
		me.hasHook("preSave")
	}

	def getPreChange(ModelElement me) {
		me.getHookFQN("preSave")
	}

	def hasAnnotation(ModelElement me, String annotation) {
		me.hasAnnotation(annotation, false)
	}

	def hasAnnotation(ModelElement me, String annotation, boolean includeAbstract) {
		(me.annotations.exists[name.equals(annotation)]) && (!me.isIsAbstract || includeAbstract)
	}

	def hasHook(ModelElement me, String hook) {
		(me.annotations.exists[name.equals(hook) && !value.nullOrEmpty])
	}

	def containsPostAttributeValueChange(GraphModel g) {
		g.containsHook("postAttributeChange")
	}

	def containsHook(GraphModel g, String hook) {
		val modelElements = g.elementsAndTypes
		modelElements.exists[it.hasHook(hook)]
	}

	def getHookFQN(ModelElement me, String hook) {
		me.annotations.findFirst[name.equals(hook) && !value.nullOrEmpty].value.get(0)
	}

	def primeCreatabel(GraphicalModelElement gme) {
		(!gme.annotations.exists[name.equals("disable") && value.contains("create")]) && !gme.isIsAbstract &&
			gme.isPrime
	}

	def creatabel(GraphicalModelElement gme) {
		(!gme.annotations.exists[name.equals("disable") && value.contains("create")]) && !gme.isIsAbstract &&
			!gme.isPrime
	}

	def creatabelPrimeRef(GraphicalModelElement gme) {
		(!gme.annotations.exists[name.equals("disable") && value.contains("create")]) && !gme.isIsAbstract
	}

	/**
	 * Returns the set of elements in the given MGLModel, that are referenced by the given set of nodes 
	 */
	def importedPrimeNodes(Iterable<Node> nodes, MGLModel model) {
		nodes.filter[prime].map[primeReference].filter[n|
			!model.elementsAndGraphmodels.toList.contains(n.type)
		]
	}
	
	/**
	 * TODO: returns the set of imported MGLs of the given  MGL
	 */
	def importedMGLs(MGLModel mgl) {
		val mglSet = new HashSet<MGLModel>
		val imports = mgl.imports
		val importURIs = imports.map[it.importURI]
		// TODO: SAMI: I will make this work
		return mglSet
	}

	def importedPrimeTypes(GraphModel g) {
		val modelPackage = g.modelPackage as MGLModel
		val models = modelPackage.importedMGLs
		val nodes = g.nodesTopologically
		val importedPrimeTypes = new java.util.HashSet<ReferencedType>
		
		for(m : models) {
			val importedPrimes = nodes.importedPrimeNodes(m).groupBy[type].entrySet.map[value.get(0)]
			importedPrimeTypes.addAll(importedPrimes)
		}
		
		importedPrimeTypes
	}

	def isPrime(GraphicalModelElement gme) {
		if (gme instanceof Node) {
			if (gme.primeReference !== null) {
				return true
			}
		}
		return false
	}

	def dispatch getType(ReferencedEClass type) {
		type.type
	}

	def dispatch getType(ReferencedModelElement type) {
		type.type
	}

	def isEcorePrime(GraphicalModelElement gme) {
		if (gme instanceof Node) {
			if (gme.primeReference !== null) {
				return gme.primeReference instanceof ReferencedEClass
			}
		}
		return false
	}

	def isModelPrime(GraphicalModelElement gme) {
		if (gme instanceof Node) {
			if (gme.primeReference !== null) {
				return gme.primeReference instanceof ReferencedModelElement
			}
		}
		return false
	}

	def removable(Annotatable gme) {
		(!gme.annotations.exists[name.equals("disable") && value.contains("delete")])
	}

	def resizable(GraphicalModelElement gme) {
		(!gme.annotations.exists[name.equals("disable") && value.contains("resize")])
	}

	def information(GraphicalModelElement gme) {
		gme.attributesExtended.exists[annotations.exists[name.equals("pyroInformation")]]
	}

	def multiline(Attribute it) {
		annotations.exists[name.equals("multiLine")]
	}

	def String displayName(ModelElement g) {
		if (g.annotations.exists[name.equals("displayName")]) {
			return g.annotations.findFirst[name.equals("displayName")].value.get(0)
		}
		return g.name.fuEscapeDart
	}

	def directlyEditable(Node node) {
		node.attributesExtended.filter(PrimitiveAttribute).exists[annotations.exists[name.equals("pyroDirectEdit")]]
	}

	def directlyEditableAttribute(Node node) {
		node.attributesExtended.filter(PrimitiveAttribute).findFirst[annotations.exists[name.equals("pyroDirectEdit")]]
	}

	def informationAttribute(GraphicalModelElement gme) {
		gme.attributesExtended.findFirst[annotations.exists[name.equals("pyroInformation")]]
	}

	def boolean connectable(Node node) {
		node.name.parentTypes(node.modelPackage as MGLModel).filter(Node).exists[!outgoingEdgeConnections.empty]
	}

	def movable(GraphicalModelElement gme) {
		(!gme.annotations.exists[name.equals("disable") && value.contains("move")])
	}

	def selectbale(GraphicalModelElement gme) {
		(!gme.annotations.exists[name.equals("disable") && value.contains("select")])
	}

	def hasIcon(GraphicalModelElement gme) {
		(gme.annotations.exists[name.equals("icon") && !value.empty]) && (gme instanceof Node)
	}

	def eclipseIconPath(Annotatable gme) {
		gme.annotations.findFirst[name.equals("icon")].value.get(0)
	}

	def boolean getHasClosedRegistration(CincoProduct product) {
		product.annotations.exists[name.equals("pyroClosedRegistration")]
	}

	def getAdminUsers(CincoProduct product) {
		product.annotations.filter[name.equals("pyroClosedRegistration")].map[value].flatten
	}

	def hasCustomAction(ModelElement gme) {
		(gme.annotations.exists[name.equals("contextMenuAction") && !value.empty])
	}

	def getCustomAction(ModelElement gme) {
		(gme.annotations.filter[name.equals("contextMenuAction") && !value.empty])
	}

	def hasDoubleClickAction(ModelElement gme) {
		(gme.annotations.exists[name.equals("doubleClickAction") && !value.empty])
	}

	def getDoubleClickAction(ModelElement gme) {
		(gme.annotations.filter[name.equals("doubleClickAction") && !value.empty])
	}

	def isCreatable(GraphModel it) {
		return !annotations.exists[name.equals("disable") && value.contains("create")]
	}
	
	def isDeletable(GraphModel it) {
		return !annotations.exists[name.equals("disable") && value.contains("delete")]
	}

	def isEditable(GraphModel it) {
		return !annotations.exists[name.equals("disable") && value.contains("edit")]
	}

	def isReadable(GraphModel it) {
		return !annotations.exists[name.equals("disable") && value.contains("read")]
	}

	def creatableGraphmodels(GeneratorCompound gc) {
		gc.mglModels.map[it.graphModels].flatten.filter[isCreatable]
	}	

	def iconPath(GraphicalModelElement gme) {
		return gme.iconPath(true)
	}

	def iconPath(GraphModel gme) {
		return gme.iconPath(true)
	}
	
	def iconPath(Annotatable gme, boolean includeFile) {
		val path = gme.eclipseIconPath
		'''img/«gme.modelPackage.name.lowEscapeDart»«IF includeFile»«path.substring(path.lastIndexOf("/"),path.length)»«ENDIF»'''
	}

	def iconPath(Image gme, boolean includeFile) {
		val path = gme.path
		'''img/«gme.modelPackage.name.lowEscapeDart»«IF includeFile»«path.substring(path.lastIndexOf("/"),path.length)»«ENDIF»'''
	}

	def iconPath(GraphModel gme, boolean includeFile) {
		val path = gme.iconPath
		'''img/«gme.modelPackage.name.lowEscapeDart»«IF includeFile»«path.substring(path.lastIndexOf("/"),path.length)»«ENDIF»'''
	}
	
	def cpdImagePath(String path) {
		"cpd/" + path.substring(0, path.lastIndexOf("/")).trimQuotes
	}

	def paletteGroup(GraphicalModelElement gme) {
		val groupId = gme.annotations.findFirst[name.equals("palette")]
		if (groupId === null) {
			switch (gme) {
				NodeContainer: return "Container"
			}
			return "Node"
		} else {
			if(groupId.value.empty) return "Node"
			return groupId.value.get(0)
		}

	}

	def elementsAndTypesAndGraphModels(GraphModel g) {
		return (g.elementsAndTypes + #[g]).toSet
	}

	def elementsAndTypesAndEnums(MGLModel model) {
		return model.elements + model.enumerations
	}

	def elementsAndTypesAndEnums(GraphModel g) {
		return g.elementsAndTypes + g.mglModel.enumerations
	}

	def elementsAndTypes(MGLModel model) {
		return model.elements.filter[it instanceof ModelElement]
	}

	def elementsAndTypes(GraphModel g) {
		return g.elements.filter[it instanceof ModelElement]
	}

	def boolean getIsType(ModelElement element) {
		element instanceof UserDefinedType
	}

	def boolean isUserDefinedType(Attribute attr) {
		switch (attr) {
			ComplexAttribute: attr.getType instanceof UserDefinedType
			default: false
		}
	}

	def enumerations(MGLModel model) {
		return model.types.filter(Enumeration)
	}

	def isPrimitive(Attribute attr) {
		val mglModel = attr.MGLModel as MGLModel
		if (attr instanceof ComplexAttribute
			&& !((attr as ComplexAttribute).type instanceof Enumeration)
		)
			return false
		primitiveETypes.contains(attr.attributeTypeName) || attr.attributeTypeName.getEnum(mglModel) !== null
	}
	
	def isPrimitive(EStructuralFeature attr) {
		attr instanceof EAttribute
	}

	def isPrimitive(EStructuralFeature attr, EPackage g) {
		if (attr.EType instanceof EDataType) {
			return primitiveETypes.contains((attr.EType as EDataType).name)
		}
		false
	}

	def getEnum(String type, MGLModel g) {
		g.enumerations.findFirst[name.equals(type)]
	}

	def getEnum(String type, EPackage g) {
		g.EClassifiers.filter(EEnum).findFirst[name.equals(type)]
	}

	def init(Attribute it, MGLModel g, String prefix) {
		if (isPrimitive) {
			if (attributeTypeName.getEnum(g) !== null) {
				return '''«prefix»«attributeTypeName».«attributeTypeName.getEnum(g).literals.get(0).escapeDart»'''
			}
			if (!defaultValue.nullOrEmpty) {
				return '''«primitiveBolster»«defaultValue»«primitiveBolster»'''
			}
			return '''«primitiveBolster»«initValue»«primitiveBolster»'''
		}
		'''null'''
	}

	def init(EStructuralFeature it) {
		if (isPrimitive()) {
			if (it.EType instanceof EEnum) {
				return '''«it.EType.name.fuEscapeDart».«it.defaultValue»'''
			}
			if (it instanceof EAttribute) {
				if (defaultValue !== null) {
					return '''«primitiveBolster»«defaultValue»«primitiveBolster»'''
				}
				return '''«primitiveBolster»«initValue»«primitiveBolster»'''

			}
		}
		'''null'''
	}

	def initValue(Attribute attr) {
		switch (attr.attributeTypeName) {
			case "EBoolean": return '''false'''
			case "EInt": return '''0'''
			case "ELong": return '''0'''
			case "EBigInteger": return '''0'''
			case "EByte": return '''0'''
			case "EShort": return '''0'''
			case "EFloat": return '''0.0'''
			case "EReal": return '''0.0'''
			case "EBigDecimal": return '''0.0'''
			case "EDouble": return '''0.0'''
			case "EDate": return '''0.0''' // TODO: SAMI: missing
			case "EDateTime": return '''0.0''' // TODO: SAMI: missing
			default: return ''''''
		}
	}

	def initValue(EAttribute attr) {
		switch (attr.EType.name) {
			case "EBoolean": return '''false'''
			case "EInt": return '''0'''
			case "ELong": return '''0'''
			case "EBigInteger": return '''0'''
			case "EByte": return '''0'''
			case "EShort": return '''0'''
			case "EFloat": return '''0.0'''
			case "EReal": return '''0.0'''
			case "EBigDecimal": return '''0.0'''
			case "EDouble": return '''0.0'''
			case "EDate": return '''0.0''' // TODO: SAMI: missing
			case "EDateTime": return '''0.0''' // TODO: SAMI: missing
			default: return ''''''
		}
	}

	def canContain(ModelElement element) {
		element instanceof GraphModel || element instanceof NodeContainer || element instanceof ContainingElement
	}

	def isExtending(ModelElement element) {
		switch element {
			NodeContainer: {
				return element.extends !== null
			}
			Node: {
				return element.extends !== null
			}
			Edge: {
				return element.extends !== null
			}
			UserDefinedType: {
				return element.extends !== null
			}
			GraphModel: {
				return element.extends !== null
			}
		}
		return false
	}

	def isExtending(EClass element) {
		!element.ESuperTypes.empty
	}

	def extendingWithouTypes(ModelElement element) {
		return element.extendingModelElement(false, "");
	}

	def extendingWithouTypes(ModelElement element, String prefix) {
		return element.extendingModelElement(false, prefix);
	}

	def extending(ModelElement element) {
		return element.extendingModelElement(true, "");
	}

	def extending(ModelElement element, String prefix) {
		return element.extendingModelElement(true, prefix);
	}

	def extending(EClass element, String prefix) {
		if(element.ESuperTypes.empty) return prefix + "PyroElement"
		return element.ESuperTypes.map[name].join(", ");
	}

	def extending(EClass element) {
		element.extending("")
	}

	def extending(EPackage element) {
		return "PyroModelFile";
	}

	def scopedTypeName(EClass t, EPackage g, CharSequence prefix) {
		if (t.EPackage.equals(g)) {
			return t.name.fuEscapeDart
		} else {
			return '''«prefix»«t.typeName»'''
		}
	}

	def scopedTypeName(EClassifier t, EPackage g, CharSequence prefix) {
		if (t.EPackage.equals(g)) {
			return t.name.fuEscapeDart
		} else {
			return '''«prefix»«t.typeName»'''
		}
	}

	def extendingModelElement(ModelElement element, boolean extendType, String prefix) {
		switch element {
			GraphModel: {
				if(element.extends === null) return prefix + "GraphModel"
				return element.extends.name
			}
			NodeContainer: {
				if(element.extends === null) return prefix + "Container"
				return element.extends.name
			}
			Node: {
				if(element.extends === null) return prefix + "Node"
				return element.extends.name
			}
			Edge: {
				if(element.extends === null) return prefix + "Edge"
				return element.extends.name
			}
			UserDefinedType: {
				if(element.extends === null || !extendType) return prefix + "IdentifiableElement"

				return element.extends.name
			}
			Enumeration: {
				return ""
			}
		}
		return ""
	}

	def ModelElement extendingModelType(ModelElement element) {
		switch element {
			NodeContainer: {
				if(element.extends === null) return null
				return element.extends
			}
			GraphModel: {
				if(element.extends === null) return null
				return element.extends
			}
			Node: {
				if(element.extends === null) return null
				return element.extends
			}
			Edge: {
				if(element.extends === null) return null
				return element.extends
			}
			UserDefinedType: {
				if(element.extends === null) return null
				return element.extends
			}
		}
		return null
	}

	def javaExtending(ModelElement element) {
		switch element {
			GraphModel: {
				if(element.extends === null) return "graphmodel.GraphModel"
				return element.extends.name
			}
			NodeContainer: {
				if(element.extends === null) return "graphmodel.Container"
				return element.extends.name
			}
			Node: {
				if(element.extends === null) return "graphmodel.Node"
				return element.extends.name
			}
			Edge: {
				if(element.extends === null) return "graphmodel.Edge"
				return element.extends.name
			}
			UserDefinedType: {
				if(element.extends === null) return "graphmodel.IdentifiableElement"
				return element.extends.name
			}
			Enumeration: {
				return ""
			}
		}
		return ""
	}

	def primitiveDartType(Attribute attr, GraphModel g) {
		attr.primitiveDartType(g.mglModel)
	}

	def primitiveDartType(Attribute attr, MGLModel g) {
		if (attr.attributeTypeName.getEnum(g) !== null) {
			return attr.attributeTypeName.getEnum(g).name.fuEscapeDart
		}
		switch (attr.attributeTypeName) {
			case "EBoolean": return '''bool'''
			case "EInt": return '''int'''
			case "ELong": return '''int'''
			case "EBigInteger": return '''int'''
			case "EByte": return '''int'''
			case "EShort": return '''int'''
			case "EFloat": return '''double'''
			case "EReal": return '''double'''
			case "EBigDecimal": return '''double'''
			case "EDouble": return '''double'''
			default: return '''String'''
		}
	}

	def primitiveDartType(EStructuralFeature attr, EPackage g) {
		if (attr.EType instanceof EEnum) {
			return attr.EType.name
		}
		switch (attr.EType.name) {
			case "EBoolean": return '''bool'''
			case "EInt": return '''int'''
			case "ELong": return '''int'''
			case "EBigInteger": return '''int'''
			case "EByte": return '''int'''
			case "EShort": return '''int'''
			case "EFloat": return '''double'''
			case "EReal": return '''double'''
			case "EBigDecimal": return '''double'''
			case "EDouble": return '''double'''
			default: return '''String'''
		}
	}

	def primitiveJavaType(EStructuralFeature attr, EPackage g) {
		if (attr.EType instanceof EEnum) {
			if (attr.list) {
				return '''java.util.List<«attr.EType.name.fuEscapeJava»>'''
			}
			return attr.EType.name.fuEscapeJava
		}
		if (attr.list) {
			switch (attr.EType.name) {
				case "EBoolean": return '''java.util.List<Boolean>'''
				case "EInt": return '''java.util.List<Integer>'''
				case "EDouble": return '''java.util.List<Double>'''
				case "ELong": return '''java.util.List<Integer>'''
				case "EBigInteger": return '''java.util.List<Integer>'''
				case "EByte": return '''java.util.List<Integer>'''
				case "EShort": return '''java.util.List<Integer>'''
				case "EFloat": return '''java.util.List<Double>'''
				case "EReal": return '''java.util.List<Double>'''
				case "EBigDecimal": return '''java.util.List<Double>'''
				default: return '''java.util.List<String>'''
			}
		}
		switch (attr.EType.name) {
			case "EBoolean": return '''boolean'''
			case "EInt": return '''int'''
			case "ELong": return '''int'''
			case "EBigInteger": return '''int'''
			case "EByte": return '''int'''
			case "EShort": return '''int'''
			case "EFloat": return '''double'''
			case "EReal": return '''double'''
			case "EBigDecimal": return '''double'''
			case "EDouble": return '''double'''
			default: return '''String'''
		}
	}

	def complexJavaType(EStructuralFeature attr, EPackage g) {
		if (attr.list) {
			return '''java.util.List<info.scce.pyro.«g.name.lowEscapeJava».rest.«attr.EType.name.fuEscapeJava»>'''
		}
		return '''info.scce.pyro.«g.name.lowEscapeJava».rest.«attr.EType.name.fuEscapeJava»'''

	}

	def serialize(Attribute it, GraphModel g, String s) {
		serialize(it, g.mglModel, s)
	}

	def serialize(Attribute it, MGLModel g, String s) {
		if (isPrimitive) {
			if (attributeTypeName.getEnum(g) !== null) {
				return '''«attributeTypeName.fuEscapeDart»Parser.toJSOG(«s»)'''
			}
			switch (attributeTypeName) {
				case "EBoolean": return '''«s»?"true":"false"'''
				case "EInt": return '''«s»'''
				case "ELong": return '''«s»'''
				case "EBigInteger": return '''«s»'''
				case "EByte": return '''«s»'''
				case "EShort": return '''«s»'''
				case "EFloat": return '''«s»'''
				case "EReal": return '''«s»'''
				case "EBigDecimal": return '''«s»'''
				case "EDouble": return '''«s»'''
				default: return '''«s»'''
			}
		}
		return '''«s».toJSOG(cache)'''
	}

	def serialize(
		EStructuralFeature it,
		EPackage g,
		String s
	) {
		if (isPrimitive) {
			if (EType.name.getEnum(g) !== null) {
				return '''«EType.name.fuEscapeDart»Parser.toJSOG(«s»)'''
			}
			switch (EType.name) {
				case "EBoolean": return '''«s»?"true":"false"'''
				case "EInt": return '''«s»'''
				case "ELong": return '''«s»'''
				case "EBigInteger": return '''«s»'''
				case "EByte": return '''«s»'''
				case "EShort": return '''«s»'''
				case "EDouble": return '''«s»'''
				case "EFloat": return '''«s»'''
				case "EReal": return '''«s»'''
				case "EBigDecimal": return '''«s»'''
				default: return '''«s»'''
			}
		}
		return '''«s».toJSOG(cache)'''
	}

	def deserialize(Attribute it) {
		deserialize(it, it.MGLModel as MGLModel)	
	}
	
	def deserialize(Attribute it, MGLModel mglModel) {
		if (isPrimitive) {
			if(attributeTypeName.getEnum(mglModel) !== null) return ""
			switch (attributeTypeName) {
				case "EBoolean": return '''=="true"||jsogObj==true'''
				case "EInt": return ''''''
				case "ELong": return ''''''
				case "EBigInteger": return ''''''
				case "EByte": return ''''''
				case "EShort": return ''''''
				case "EDouble": return ''''''
				case "EFloat": return ''''''
				case "EReal": return ''''''
				case "EBigDecimal": return ''''''
				default: return '''.toString()'''
			}
		}
		return ".toString()"
	}

	def deserialize(EStructuralFeature it, EPackage g) {
		if (isPrimitive) {
			if(EType.name.getEnum(g) !== null) return ""
			switch (EType.name) {
				case "EBoolean": return '''=="true"||jsogObj==true'''
				case "EInt": return ''''''
				case "ELong": return ''''''
				case "EBigInteger": return ''''''
				case "EByte": return ''''''
				case "EShort": return ''''''
				case "EDouble": return ''''''
				case "EFloat": return ''''''
				case "EReal": return ''''''
				case "EBigDecimal": return ''''''
				default: return '''.toString()'''
			}
		}
		return ".toString()"
	}

	def complexDartType(Attribute attr) {
		attr.dartFQN.toString
	}

	def complexDartType(EStructuralFeature attr) {
		attr.EType.name
	}
	
	def Iterable<ModelElement> parentTypes(ModelElement type) {
		type.name.parentTypes(type.modelPackage as MGLModel)
	}
	
	def Iterable<ModelElement> parentTypes(String typeName, MGLModel g) {
		if (typeName === null) {
			return Collections.EMPTY_LIST
		}
		val all = g.elements
		val subType = all.findFirst[name.equals(typeName)]
		if (subType !== null) {
			if (subType instanceof Node) {
				val l = new LinkedList
				l.add(subType)
				if (subType.extends !== null) {
					l.addAll(subType.extends.name.parentTypes(g))
				}
				return l.filter(ModelElement)
			}
			if (subType instanceof Edge) {
				val l = new LinkedList
				l.add(subType)
				if (subType.extends !== null) {
					l.addAll(subType.extends.name.parentTypes(g))
				}
				return l.filter(ModelElement)
			}
			if (subType instanceof GraphModel) {
				val l = new LinkedList
				l.add(subType)
				if (subType.extends !== null) {
					l.addAll(subType.extends.name.parentTypes(g))
				}
				return l.filter(ModelElement)
			}
			if (subType instanceof UserDefinedType) {
				val l = new LinkedList
				l.add(subType)
				if (subType.extends !== null) {
					l.addAll(subType.extends.name.parentTypes(g))
				}
				return l.filter(ModelElement)
			}
		}
		return Collections.EMPTY_LIST
	}
	
	def dispatch Iterable<ModelElement> subTypesAndType(String typeName, MGLModel model) {
		return model.elements.filter[name.equals(typeName)] + typeName.subTypes(model).filter(ModelElement)
	}
	
	def dispatch Iterable<ModelElement> subTypesAndType(String typeName, GraphModel g) {
		val model = g.modelPackage as MGLModel
		return model.elements.filter[name.equals(typeName)] + typeName.subTypes(model).filter(ModelElement)
	}

	def dispatch Iterable<ModelElement> subTypes(String typeName, MGLModel model) {
		val directSubTypes = model.elements.filter[isExtending].filter[n|extending(n).equals(typeName)]
		return directSubTypes + (directSubTypes.map[n|n.name.subTypes(model).filter(ModelElement)].flatten)
	}

	def dispatch Iterable<EClass> subTypesAndType(String typeName, EPackage g) {
		return g.EClassifiers.filter(EClass).filter[name.equals(typeName)] + typeName.subTypes(g).filter(EClass)
	}

	def dispatch Iterable<EClass> subTypes(String typeName, EPackage g) {
		g.EClassifiers.filter(EClass).filter[isSubTypeOf(typeName)]
	}

	def boolean isSubTypeOf(EClass e, String typeName) {
		return e.ESuperTypes.map[name].contains(typeName)
	}

	def primitiveBolster(Attribute attr) {
		switch (attr.attributeTypeName) {
			case "EString": return '''"'''
			case "EChar": return '''"'''
			case "EDate": return '''"'''
			case "EDateTime": return '''"'''
			default: return ""
		}
	}

	def primitiveBolster(EAttribute attr) {
		switch (attr.EType.name) {
			case "EString": return '''"'''
			case "EChar": return '''"'''
			case "EDate": return '''"'''
			case "EDateTime": return '''"'''
			default: return ""
		}
	}
	
	def styling(GraphicalModelElement element, Styles styles) {
		val styleName = element.usedStyle
		styles.styles.findFirst[
			name.equals(styleName)
		]
	}

	def dispatch Iterable<Image> getImages(NodeStyle ns) {
		ns.mainShape.collectImages
	}

	def dispatch Iterable<Image> getImages(EdgeStyle ns) {
		ns.decorator.map[decoratorShape].map[collectImages].flatten
	}

	def Iterable<Image> collectImages(EObject abs) {
		if (abs instanceof Image) {
			return #[abs]
		}
		if (abs instanceof ContainerShape) {
			return abs.children.map[collectImages(it)].flatten.filter[it !== null]
		}
		#[]
	}

	def getImage(NodeStyle ns) {
		ns.mainShape
	}

	def dispatch boolean hasAppearanceProvider(Node n, Styles styles) {
		val styleForNode = n.styleFor(styles)
		return !styleForNode.appearanceProvider.nullOrEmpty
	}

	def dispatch boolean hasAppearanceProvider(Edge n, Styles styles) {
		val styleForEdge = n.styleFor(styles)
		return !styleForEdge.appearanceProvider.nullOrEmpty
	}

	def dispatch boolean hasAppearanceProvider(GraphModel n, Styles styles) {
		return false;
	}

	def dispatch styleFor(Node n, Styles styles) {
		n.styling(styles) as NodeStyle
	}

	def dispatch styleFor(Edge n, Styles styles) {
		n.styling(styles) as EdgeStyle
	}

	def isList(Attribute attr) {
		attr.upperBound > 1 || attr.upperBound < 0
	}

	def isList(EStructuralFeature attr) {
		attr.upperBound > 1 || attr.upperBound < 0
	}

	def getGroupContainables(GraphicalElementContainment group, MGLModel g) {
		if (group.types.empty) {
			return g.nodes.filter[!isIsAbstract]
		}
		(group.types.filter[!isIsAbstract] + group.types.map[name].map[subTypes(g)].flatten).filter(ModelElement).filter [
			!isIsAbstract
		].toSet
	}

	def Set<GraphicalModelElement> possibleEmbeddingTypes(ContainingElement ce, GraphModel g) {
		ce.possibleEmbeddingTypes(g.mglModel)
	}

	def Set<GraphicalModelElement> possibleEmbeddingTypes(ContainingElement ce, MGLModel g) {
		val directContainable = ce.containableElements.map[types].flatten
		val subTypesOfDirectContainable = directContainable.map[n|n.name.subTypes(g)].flatten.filter(
			GraphicalModelElement)
		if (ce instanceof NodeContainer) {
			if (ce.extends !== null) {
				if (ce.extends instanceof NodeContainer) {
					return (directContainable + subTypesOfDirectContainable +
						(ce.extends as NodeContainer).possibleEmbeddingTypes(g)).toSet
				}
			}
		}
		if (directContainable.empty) {
			return g.nodes.filter(GraphicalModelElement).toSet
		}

		return (directContainable + subTypesOfDirectContainable).toSet
	}

	def Iterable<GraphicalElementContainment> allContainableElement(ContainingElement ce, GraphModel g) {
		val containable = ce.containableElements
		if (ce instanceof NodeContainer) {
			if (ce.extends !== null) {
				if (ce.extends instanceof NodeContainer) {
					containable += (ce.extends as NodeContainer).allContainableElement(g)
				}
			}
		}

		return containable
	}

	def Iterable<Edge> parentEgdes(Edge e) {
		if (e.extends !== null) {
			return #[e] + parentEgdes(e.extends)
		}
		return #[e]
	}

	def Set<Edge> possibleOutgoing(Node node) {
		val model =node.modelPackage as MGLModel;
		var directOutgoing = !node.outgoingWildcards.empty?
			model.edges : node.outgoingEdgeConnections.map[connectingEdges].flatten.toSet
		if (node.outgoingEdgeConnections.exists[connectingEdges.empty]) {
			return this.edges(model).toSet
		}
		val subTypesOfDirectOutgoing = directOutgoing.map[n|n.name.subTypes(model)].flatten.filter(Edge)
		if (node.extends !== null) {
			return (directOutgoing + subTypesOfDirectOutgoing + node.extends.possibleOutgoing ).toSet
		}
		return (directOutgoing + subTypesOfDirectOutgoing).toSet
	}

	def Set<Edge> possibleIncoming(Node node) {
		val model =node.modelPackage as MGLModel;
		var directIncoming = !node.incomingWildcards.empty?
			model.edges : node.incomingEdgeConnections.map[connectingEdges].flatten.toSet
		if (node.incomingEdgeConnections.exists[connectingEdges.empty]) {
			return this.edges(model).toSet
		}
		val subTypesOfDirectIncoming = directIncoming.map[n|n.name.subTypes(model)].flatten.filter(Edge)
		if (node.extends !== null) {
			return (directIncoming + subTypesOfDirectIncoming + node.extends.possibleIncoming ).toSet
		}
		return (directIncoming + subTypesOfDirectIncoming).toSet
	}

	def Set<Node> possibleSources(Edge edge) {
		val modelPackage = edge.modelPackage as MGLModel;
		val possibleDirectPredecessors = this.nodes(modelPackage).filter[possibleOutgoing.contains(edge)]
		return (possibleDirectPredecessors + possibleDirectPredecessors.map[name.subTypes(modelPackage)].flatten.filter(Node)).toSet
	}

	def Set<Node> possibleTargets(Edge edge) {
		val modelPackage = edge.modelPackage as MGLModel;
		val possibleDirectSuccessors = this.nodes(modelPackage).filter[possibleIncoming.contains(edge)].toSet
		return (possibleDirectSuccessors + possibleDirectSuccessors.map[name.subTypes(modelPackage)].flatten.filter(Node)).toSet
	}

	def Iterable<Attribute> attributesExtended(ModelElement me) {
		val attrs = new LinkedList
		attrs += me.attributes
		if (me.isExtending) {
			switch (me) {
				Node: attrs += me.extends.attributesExtended
				GraphModel: attrs += me.extends.attributesExtended
				Edge: attrs += me.extends.attributesExtended
				UserDefinedType: attrs += me.extends.attributesExtended
			}
		}
		attrs
	}

	def Iterable<EAttribute> attributesExtended(EClass me) {
		val attrs = new LinkedList<EAttribute>
		attrs += me.eContents.filter(EAttribute)
		attrs += me.ESuperTypes.map[attributesExtended].flatten
		attrs
	}

	def Iterable<EReference> referencesExtended(EClass me) {
		val attrs = new LinkedList
		attrs += me.eContents.filter(EReference)
		attrs += me.ESuperTypes.map[referencesExtended].flatten
		attrs
	}
	
	/**
	 * TODO: Alternated FROM MGLEcoreGenerator
	 */
	private def allContainingElements(GraphicalModelElement element) {
		val containingElements = new HashSet<ContainingElement>
		if(element !== null){
			var graphModels = element.mglModel.graphModels
			for (gm : graphModels) {
				val containable = gm.allContainmentConstraints
				if (containable.exists[(types.contains(element)||types.empty) && upperBound !== 0] || gm.containableElements.empty) {
					containingElements += gm
				}
			}

			containingElements += element.containingElements
			containingElements += MGLUtil.allSuperTypes(element).map[(it as GraphicalModelElement).containingElements].flatten
			containingElements += MGLUtil.subTypes(element).map[(it as GraphicalModelElement).containingElements].flatten
		}
		containingElements
	}
	
	/**
	 * TODO: Alternated FROM MGLEcoreGenerator
	 */
	private def containingElements(GraphicalModelElement element){
		val elementsGraphModel = element.graphModels
		val result = new HashSet<ContainingElement>
		for(g : elementsGraphModel) {
			val cE = MGLUtil.getContainingElements(g).filter[allContainmentConstraints.exists[types.contains(element) && upperBound !== 0]]
			result.addAll(cE)
		}
		return result
	}
	
	/**
	 * Alternated FROM MGLEcoreGenerator
 	 */
	private def getAllContainmentConstraints(ContainingElement ce){
		val superTypes = new ArrayList<ModelElement>
		var sType = ce.extend
		var result = new HashSet<GraphicalElementContainment>
		while(sType!==null){
			if(superTypes.contains(sType)) {
				throw new IllegalArgumentException("InheritanceCircle Detected: "+superTypes)
			} else {
				superTypes.add(sType)
				sType = sType.extend
			}
		}
		result += ce.containableElements + superTypes.filter(ContainingElement).map[containableElements].flatten
		result
	}
	
	/**
	 * FROM NodeMethodsGenerator
 	 */
	def extend(ContainingElement ce){
		switch(ce){
			case ce instanceof GraphModel: return ((ce as GraphModel).extends)
			case ce instanceof NodeContainer: return ((ce as NodeContainer).extends)
			default : throw new IllegalArgumentException(String.format("Can not match Type: %s", ce))
		}
	}
	def static ModelElement extend(ModelElement element) {
		switch element {
			Node : element.extends
			Edge : element.extends
			UserDefinedType : element.extends
			GraphModel : element.extends
			default: null
		}
	}
 	
	def getPossibleContainmentTypes(GraphicalModelElement node) {
		allContainingElements(node)
	}

	@Deprecated // This method needs be redesigned for cinco2.0
	def ModelElement getBestContainerSuperType(GraphicalModelElement node) {
		val util = new InheritanceUtil
		val containers = node.getPossibleContainmentTypes
		if (!containers.nullOrEmpty && containers.filter(GraphModel).size == 0) {
			val lmsn = util.getLowestMutualSuperNode(containers.filter(Node))
			println(containers)
			println(lmsn)
			if (lmsn !== null) {
				return lmsn;
			}
		} else if (containers.size == 1 && containers.head instanceof GraphModel) {
			return containers.head as GraphModel // TODO: fixme - no single bestContainerSuperType, rather a set
		}
		null
	}

	def String getBestContainerSuperTypeNameAPI(GraphicalModelElement node) {
		return "graphmodel.ModelElementContainer"
	}

	def getBestContainerSuperTypeNameDart(GraphicalModelElement node) {
		return "core.ModelElementContainer"
	}

	def boolean isElliptic(ModelElement element, Styles styles) {
		if (element instanceof Node) {
			val style = element.styling(styles) as NodeStyle
			return style.mainShape instanceof Ellipse
		}
		false
	}

	def String baseTypeName(ModelElement me) {
		switch (me) {
			NodeContainer: return "Container"
			Node: return "Node"
			Edge: return "Edge"
			GraphModel: return "GraphModel"
		}
	}

	def String getName(EObject object) {
		if(object instanceof EPackage) {
			return object.name
		}
		else if(object instanceof MGLModel) {
			return object.fileName
		}
		else if (object instanceof Type) {
			return object.name
		}
		else if (object instanceof ENamedElement) {
			return object.name
		}
		else if (object instanceof Attribute) {
			return object.name
		}
		throw new IllegalStateException(object.toString)
	}

	def dispatch boolean isAbstract(GraphModel ce) {
		ce.isAbstract
	}

	def dispatch boolean isAbstract(UserDefinedType ce) {
		ce.isIsAbstract
	}

	def dispatch boolean isAbstract(NodeContainer ce) {
		ce.isIsAbstract
	}

	def dispatch boolean isAbstract(Edge ce) {
		ce.isIsAbstract
	}

	def dispatch boolean isAbstract(Node ce) {
		ce.isIsAbstract
	}

	def dispatch boolean isAbstract(EClassifier e) {
		switch (e) {
			EClass: e.abstract
			EReference: e.getEReferenceType().abstract
			default: false
		}
	}

	def boolean isAbstractAttr(Attribute attr) {
		switch (attr) {
			ComplexAttribute: attr.getType.isAbstractType()
			default: false
		}
	}

	def dispatch boolean isAbstractType(Type type) {
		switch (type) {
			UserDefinedType: type.isIsAbstract()
			ModelElement: type.isIsAbstract()
			default: false
		}
	}

	def dispatch boolean isAbstractType(EClassifier type) {
		type.isAbstract
	}

	/*
	 * RECURSIVE-TYPE-RESOLVER
	 */
	/**
	 * Resolves the customAction annotations of the element and it's superTypes to a list.
	 * The list is ordered from the "oldest" to the "youngest" inherited annotation.
	 */
	def resolveCustomActions(ModelElement e) {
		e.resolveByFunc([t|t.customAction], [hasCustomAction], Annotation)
	}

	/**
	 * Resolves the doubleClick annotations of the element and it's superTypes to a list.
	 * The list is ordered from the "oldest" to the "youngest" inherited annotation.
	 */
	def resolveDoubleClickActions(ModelElement e) {
		e.resolveByFunc([doubleClickAction], [hasDoubleClickAction], Annotation)
	}

	/**
	 * Resolves the postMove annotations of the element and it's superTypes to a list.
	 * The list is ordered from the "oldest" to the "youngest" inherited annotation.
	 */
	def resolvePostMove(ModelElement e) {
		e.resolveByFunc([postMoveHook], [hasPostMove], String)
	}

	/**
	 * Resolves the postResizeHook annotations of the element and it's superTypes to a list.
	 * The list is ordered from the "oldest" to the "youngest" inherited annotation.
	 */
	def resolvePostResize(ModelElement e) {
		e.resolveByFunc([postResizeHook], [hasPostResize], String)
	}

	/**
	 * Resolves the postCreateHook annotations of the element and it's superTypes to a list.
	 * The list is ordered from the "oldest" to the "youngest" inherited annotation.
	 */
	def resolvePostCreate(ModelElement e) {
		e.resolveByFunc([postCreateHook], [hasPostCreateHook], String)
	}

	/**
	 * Resolves the postCreateHook annotations of the element and it's superTypes to a list.
	 * The list is ordered from the "oldest" to the "youngest" inherited annotation.
	 */
	def resolvePostDelete(ModelElement e) {
		e.resolveByFunc([postDeleteHook], [hasPostDeleteHook], String)
	}

	/**
	 * Resolves the postAttributeValueChange annotations of the element and it's superTypes to a list.
	 * The list is ordered from the "oldest" to the "youngest" inherited annotation.
	 */
	def resolvePostAttributeValueChange(ModelElement e) {
		e.resolveByFunc([postAttributeValueChange], [hasPostAttributeValueChange], String)
	}

	/**
	 * Resolves the preDeleteHook annotations of the element and it's superTypes to a list.
	 * The list is ordered from the "oldest" to the "youngest" inherited annotation.
	 */
	def resolvePreDelete(ModelElement e) {
		e.resolveByFunc([preDeleteHook], [hasPreDeleteHook], String)
	}

	def <T, E> List<E> resolveByFunc(ModelElement e, Function<ModelElement, T> foo,
		Function<ModelElement, Boolean> guard, Class<E> ret) {
		val result = new LinkedList<E>
		val superTypesAndType = e.name.parentTypes(e.modelPackage as MGLModel)
		for (t : superTypesAndType) {
			if (guard.apply(t)) {
				val l = foo.apply(t)
				if (l instanceof Iterable<?>)
					l.forEach [
						result.add(
							ret.cast(it)
						)
					]
				else
					result.add(
						ret.cast(l)
					)
			}
		}
		return result.reverse
	}

	def hasIncoming(Node me, MGLModel g) {
		!me.possibleIncoming.empty
	}

	def hasOutgoing(Node me, MGLModel g) {
		!me.possibleOutgoing.empty
	}

	def hasTargets(Edge me, MGLModel g) {
		!me.possibleTargets.empty
	}

	def hasSources(Edge me, MGLModel g) {
		!me.possibleSources.empty
	}

	def hasContainer(GraphicalModelElement me, MGLModel g) {
		!me.possibleContainmentTypes.empty
	}

	def resolvePossibleSources(Edge me) {
		me.possibleSources.resolveSubTypesAndType
	}

	def resolvePossibleTargets(Edge me) {
		me.possibleTargets.resolveSubTypesAndType
	}
	
	def resolvePossibleContainer(ModelElement me) {
		if(me instanceof Node) {
			me.resolvePossibleContainerNode
		} else if(me instanceof Edge) {
			me.resolvePossibleContainerEdge
		} else
			throw new IllegalStateException("Exhaustive if");
	}

	def resolvePossibleContainerNode(Node me) {		
		val possibleContainer = new HashSet<ContainingElement>
		val types = me.resolveSuperTypesAndType
		val mglModel = me.mglModel
		
		mglModel.graphmodels.forEach[g|
			g.elements.filter(ContainingElement).forEach[container|
				if(!possibleContainer.contains(container)) {
					val containableTypes = container.resolvePossibleContainingTypes.map[it.types].flatten.filter(Node)
					containableTypes.forEach[n|
						// container of contained Element n is also container of edge
						possibleContainer.addAll(
							container.resolveSubTypesAndType.filter(ContainingElement)
						)
					]
				}
			]
		]
		// add graphModels
		possibleContainer.addAll(
			mglModel.graphmodels.filter[g|
				types.exists[t|
					g.nodes.contains(t)
				]
			]
		)
		possibleContainer.map[ModelElement.cast(it)].toSet.filter[!isAbstract]
	}
	
	def resolvePossibleContainerEdge(Edge me) {
		val possibleContainer = new HashSet<ContainingElement>
		val types = me.resolveSuperTypesAndType
		val mglModel = me.mglModel
		
		mglModel.graphmodels.forEach[g|
			g.elements.filter(ContainingElement).forEach[container|
				if(!possibleContainer.contains(container)) {
					val containableTypes = container.resolvePossibleContainingTypes.map[it.types].flatten.filter(Node)
					containableTypes.forEach[n|
						if(
							types.exists[t|
								// any (super) type of edge can be incoming or outgoing edge
								n.possibleIncoming.contains(t)
								||
								n.possibleOutgoing.contains(t)	
							]
						
						) {
							// container of contained Element n is also container of edge
							possibleContainer.addAll(
								container.resolveSubTypesAndType.filter(ContainingElement)
							)
						}
					]
				}
			]
		]
		// add graphModels
		possibleContainer.addAll(
			mglModel.graphmodels.filter[g|
				types.exists[t|
					g.edges.contains(t)
				]
			]
		)
		possibleContainer.map[ModelElement.cast(it)].toSet.filter[!isAbstract]
	}

	def HashSet<GraphicalElementContainment> resolvePossibleContainingTypes(ContainingElement container) {
		var containingTypes = new HashMap<Type, GraphicalElementContainment>
		var resolved = new HashSet<GraphicalElementContainment>
		if (container instanceof ModelElement) {
			if (container.isExtending && container instanceof ContainingElement) {
				switch (container) {
					NodeContainer: {
						val extending = container.extends
						if (extending instanceof ContainingElement)
							resolved = extending.resolvePossibleContainingTypes
					}
					GraphModel: {
						val extending = container.extends
						if (extending instanceof ContainingElement)
							resolved = extending.resolvePossibleContainingTypes
					}
				}
			}
		}
		// inherited
		for (value : resolved) {
			for (type : value.types) {
				val key = type
				containingTypes.put(key, value)
			}
		}
		// own
		for (value : container.containableElements) {
			for (type : value.types) {
				val key = type
				containingTypes.put(key, value)
			}
		}
		return new HashSet<GraphicalElementContainment>(containingTypes.values)
	}

	def HashSet<Type> resolvePrimeReferences(Iterable<MGLModel> graphModels) {
		var types = new HashSet<Type>
		for (g : graphModels) {
			var primeRefs = g.primeRefs
			for (ref : primeRefs) {
				var t = ref.type
				types.addAll(g.resolveSubTypesAndType(t));
			}
		}
		types
	}

	def resolveConcreteSuperTypes(Type t) {
		t.resolveSuperTypes.filter[!isAbstract]
	}

	def resolveConcreteSuperTypesAndType(MGLModel g, Type t) {
		t.resolveSuperTypesAndType.filter[!isAbstract]
	}

	def resolveSuperTypes(Type t) {
		t.resolveSuperTypesAndType.filter[it.name != t.name]
	}

	def HashSet<Type> resolveSuperTypesAndType(Type type) {		
		var result = new HashSet<Type>

		// early break
		if (type === null)
			return result
		result.add(type)

		// resolve descendent-types
		switch (type) {
			Node: {
				val superType = type.extends as Type
				val descendents = superType.resolveSuperTypesAndType as HashSet<Type>
				result.addAll(descendents)
			}
			Edge: {
				val superType = type.extends as Type
				val descendents = superType.resolveSuperTypesAndType as HashSet<Type>
				result.addAll(descendents)
			}
			GraphModel: {
				val superType = type.extends as Type
				val descendents = superType.resolveSuperTypesAndType as HashSet<Type>
				result.addAll(descendents)
			}
			UserDefinedType: {
				val superType = type.extends as Type
				val descendents = superType.resolveSuperTypesAndType as HashSet<Type>
				result.addAll(descendents)
			}
		}
		return result
	}

	
	def <G extends EObject, T extends EObject> resolveSubTypesAndType(T t) {
		val modelPackage = t.modelPackage
		modelPackage.resolveSubTypesAndType(t)
	}

	def <T extends Type> List<T> resolveSubTypesAndType(T element) {
		val model = element.MGLModel
		if(model instanceof MGLModel)
			element.resolveSubTypesAndType(new LinkedList<T>)
	}

	def <T extends Type> List<T> resolveSubTypesAndType(Iterable<T> elements) {
		var result = new LinkedList<T>
		for (element : elements) {
			var resolved = element.resolveSubTypesAndType(new LinkedList<T>)
			result.addAll(resolved)
		}
		result.stream.distinct.collect(Collectors.toList)
	}

	private def <G extends EObject, T extends EObject> resolveSubTypesAndType(G g, T t) {
		if (g instanceof MGLModel)
			return resolveSubTypesAndType(g as MGLModel, t as Type)
		else if (g instanceof EPackage)
			return resolveSubTypesAndType(g as EPackage, t as EClassifier)
		else if (g instanceof GraphModel)
			throw new RuntimeException("GraphModel is Package is Deprecated!")
	}
	
	def <T extends Type> List<T> resolveAllSubTypesAndType(T element) {
		element.resolveSubTypesAndType(
			new LinkedList<T>,
			[e|false],
			[el|el.name.subTypes(element.modelPackage) as Iterable<T>]
		)
	}
	
	private def <T extends Type> List<T> resolveSubTypesAndType(MGLModel g, T element) {
		element.resolveSubTypesAndType(new LinkedList<T>)
	}

	private def <T extends EClassifier> List<T> resolveSubTypesAndType(EPackage g, T element) {
		element.resolveSubTypesAndType(new LinkedList<T>)
	}

	def <T extends Type> List<T> resolveSubTypesAndType(T e, List<T> cached) {
		e.resolveSubTypesAndType(
			cached,
			[element|element.isAbstractType],
			[element|element.name.subTypes(e.modelPackage) as Iterable<T>]
		)
	}

	def <T extends EClassifier> List<T> resolveSubTypesAndType(T e, List<T> cached) {
		e.resolveSubTypesAndType(
			cached,
			[element|element.abstract],
			[element|element.name.subTypes(e.modelPackage) as Iterable<T>]
		)
	}

	def <G, T> List<T> resolveSubTypesAndType(T element, List<T> cached, Function<T, Boolean> filterAway,
		Function<T, Iterable<T>> getSubTypes) {
		if (cached.contains(element))
			return cached;
		var cachedSubTypes = new LinkedList<T>
		cachedSubTypes.addAll(cached)
		if (!filterAway.apply(element)) {
			cachedSubTypes.add(element)
		}
		// resolve subTypes further recursively
		val subTypes = getSubTypes.apply(element)
		for (subType : subTypes) {
			cachedSubTypes = subType.resolveSubTypesAndType(cachedSubTypes.toList, filterAway,
				getSubTypes) as LinkedList<T>
		}
		return cachedSubTypes
	}

	def List<EClass> resolveSuperTypesAndType(EPackage g, EClass element) {
		g.resolveSuperTypesAndType(element, new LinkedList<EClass>, true)
	}

	private def List<EClass> resolveSuperTypesAndType(EPackage g, EClass element, List<EClass> cached,
		boolean withAbstract) {
		if (cached.contains(element))
			return cached;
		var cachedSuperType = new LinkedList<EClass>;
		cachedSuperType.addAll(cached)
		if ((withAbstract || !element.abstract)) {
			cachedSuperType.add(element)
		}
		// resolve subTypes further recursively
		for (superType : element.ESuperTypes) {
			cachedSuperType = g.resolveSuperTypesAndType(superType, cachedSuperType, withAbstract) as LinkedList<EClass>
		}
		return cachedSuperType
	}

	def ecoreType(EAttribute attr, EPackage g) {
		if (attr.isPrimitive(g) && getEnum(attr.EType.name, g) === null) {
			if (attr.isList)
				return (attr.EType as EDataType).name.getPrimitveObjectTypeLiteral
			return (attr.EType as EDataType).name.getPrimitveTypeLiteral
		}
		return "String"
	}

	def getPrimitveTypeLiteral(String pType) {
		switch pType {
			case "EBoolean": "boolean"
			case "EInt": "long"
			case "EByte": "long"
			case "EShort": "long"
			case "EBigInteger": "long"
			case "ELong": "long"
			case "EDouble": "double"
			case "EFloat": "double"
			case "EBigDecimal": "double"
			case "EReal": "double"
			// case "EDate": "java.time.LocalDate" // TODO: SAMI: make date available...edge case String -> Date (Entity -> Api)
			// case "EDateTime": "java.time.LocalDate"
			default: "String"
		}
	}

	def getPrimitveObjectTypeLiteral(String pType) {
		switch pType {
			case "EBoolean": "Boolean"
			case "EInt": "Long"
			case "EByte": "Long"
			case "EShort": "Long"
			case "EBigInteger": "Long"
			case "ELong": "Long"
			case "EDouble": "Double"
			case "EFloat": "Double"
			case "EBigDecimal": "Double"
			case "EReal": "Double"
			// case "EDate": "java.time.LocalDate" // TODO: SAMI: make date available...edge case String -> Date (Entity -> Api)
			// case "EDateTime": "java.time.LocalDate"
			default: "String"
		}
	}

	def getPrimitiveDefault(Attribute attr) {
		if (attr.defaultValue !== null) {
			switch (attr.attributeTypeName) {
				case "EInt": return '''«attr.defaultValue»L'''
				case "ELong": return '''«attr.defaultValue»L'''
				case "EBigInteger": return '''«attr.defaultValue»L'''
				case "EByte": return '''«attr.defaultValue»L'''
				case "EShort": return '''«attr.defaultValue»L'''
				case "EString": return '''"«attr.defaultValue»"'''
				default: return '''«attr.defaultValue»'''
			}
		}
		switch (attr.attributeTypeName) {
			case "EBoolean": return '''false'''
			case "ELong": return '''0L'''
			case "EBigInteger": return '''0L'''
			case "EShort": return '''0L'''
			case "EInt": return '''0L'''
			case "EByte": return '''0L'''
			case "EFloat": return '''0.0'''
			case "EBigDecimal": return '''0.0'''
			case "EDouble": return '''0.0'''
			case "EString": return '''""'''
			default: return '''null'''
		}
	}

	def getPrimitiveDefaultDart(Attribute attr) {
		if (attr.defaultValue !== null) {
			switch (attr.attributeTypeName) {
				case "EString": return '''"«attr.defaultValue»"'''
				default: return '''«attr.defaultValue»'''
			}
		}
		switch (attr.attributeTypeName) {
			case "EBoolean": return '''false'''
			case "ELong": return '''0'''
			case "EBigInteger": return '''0'''
			case "EShort": return '''0'''
			case "EInt": return '''0'''
			case "EByte": return '''0'''
			case "EFloat": return '''0.0'''
			case "EBigDecimal": return '''0.0'''
			case "EDouble": return '''0.0'''
			case "EString": return '''"«attr.defaultValue»"'''
			default: return '''null'''
		}
	}

	def packagePath(MGLModel g) '''«g.package.lowEscapeJava.replaceAll("\\.","/")»'''

	def packagePath(EPackage g) '''«g.name.lowEscapeJava.replaceAll("\\.","/")»'''

	def dispatch controllerName(GraphModel g) '''«g.name.fuEscapeJava»Controller'''
	def dispatch controllerName(EPackage g) '''«g.name.fuEscapeJava»Controller'''
	def dispatch controllerFQN(GraphModel g) '''info.scce.pyro.core.«g.controllerName»'''
	def dispatch controllerFQN(EPackage g) '''info.scce.pyro.core.«g.controllerName»'''

	def commandExecuterClass() '''CommandExecuter'''
	def commandExecuterFQN() '''info.scce.pyro.core.command.«commandExecuterClass»'''
	def dispatch commandExecuter(GraphModel g) '''«g.name.fuEscapeJava»CommandExecuter'''
	def dispatch commandExecuter(EPackage g) '''«g.name.fuEscapeJava»CommandExecuter'''
	def dispatch commandExecuterFQN(GraphModel g) '''info.scce.pyro.core.command.«g.commandExecuter»'''
	def dispatch commandExecuterFQN(EPackage g) '''info.scce.pyro.core.command.«g.commandExecuter»'''
	def commandExecuterVar(GraphModel g) '''«g.name.lowEscapeJava»CommandExecuter'''

	def typeRegistryName() '''TypeRegistry'''
	def typeRegistryFQN(MGLModel g) '''«g.apiFQNBase».util.«typeRegistryName»'''

	def dbTypeName() '''PanacheEntity'''
	def dbTypeFQN() '''io.quarkus.hibernate.orm.panache.«dbTypeName»'''

	def dispatch entityFQNBase(MGLModel g) '''entity.«g.name.lowEscapeJava»'''
	def dispatch entityFQNBase(EPackage g) '''entity.«g.name.lowEscapeJava»'''
	def dispatch entityFQN(Attribute me) '''«me.modelPackage.entityFQNBase».«me.attributeTypeName.fuEscapeJava»DB'''
	def dispatch entityFQN(ContainingElement me) '''«me.modelPackage.entityFQNBase».«me.name.fuEscapeJava»DB'''
	def dispatch entityFQN(Type me) '''«me.modelPackage.entityFQNBase».«me.name.fuEscapeJava»DB'''
	def dispatch entityFQN(ENamedElement e) '''«e.modelPackage.entityFQNBase».«e.name.fuEscapeJava»DB'''
	def dispatch entityFQN(String e, EPackage g) '''«g.entityFQNBase».«e.fuEscapeJava»DB'''
	def dispatch entityFQN(String e, MGLModel g) '''«g.entityFQNBase».«e.fuEscapeJava»DB'''
	def dispatch entityFQN(EPackage g) '''«g.entityFQNBase».«g.name.fuEscapeJava»DB'''
	
	@Deprecated
	def dispatch entityFQN(Attribute me, MGLModel g) '''«g.entityFQNBase».«me.attributeTypeName.fuEscapeJava»DB'''
	@Deprecated
	def dispatch entityFQN(ContainingElement me, MGLModel g) '''«g.entityFQNBase».«me.name.fuEscapeJava»DB'''
	@Deprecated
	def dispatch entityFQN(Type me, MGLModel g) '''«g.entityFQNBase».«me.name.fuEscapeJava»DB'''
	
	def dispatch apiPath(MGLModel g) '''«g.packagePath»/«g.name.lowEscapeJava»'''
	def dispatch apiPath(EPackage g) '''«g.packagePath»'''
	def dispatch apiFQNBase(MGLModel g) '''«g.package.lowEscapeJava».«g.name.lowEscapeJava»'''
	//def dispatch apiFQNBase(GraphicalElementContainment me) '''«me.modelPackage.apiFQNBase»'''
	//def dispatch apiFQNBase(ModelElement me) '''«me.modelPackage.apiFQNBase»'''	
	//def dispatch apiFQNBase(EObject me) '''«me.modelPackage.apiFQNBase»'''
	//def dispatch apiFQNBase(Attribute me) '''«me.modelPackage.apiFQNBase»'''
	def dispatch apiFQNBase(EPackage g) '''«g.name.lowEscapeJava»'''
	def dispatch apiFQN(ComplexAttribute me) '''«me.modelPackage.apiFQNBase».«me.type.name.fuEscapeJava»'''
	def dispatch apiFQN(GraphicalElementContainment me) '''«me.modelPackage.apiFQNBase».«me.name.fuEscapeJava»'''
	def dispatch apiFQN(ModelElement me) '''«me.modelPackage.apiFQNBase».«me.name.fuEscapeJava»'''
	def dispatch apiFQN(EObject me) '''«me.modelPackage.apiFQNBase».«me.name.fuEscapeJava»'''
	def dispatch apiFQNWithoutName(Attribute me) '''«me.modelPackage.apiFQNBase»'''
	def dispatch apiFQNWithoutName(GraphicalElementContainment me) '''«me.modelPackage.apiFQNBase»'''
	def dispatch apiFQNWithoutName(ModelElement me) '''«me.modelPackage.apiFQNBase»'''
	def dispatch apiFQNWithoutName(EObject me) '''«me.modelPackage.apiFQNBase»'''
	
	
	def dispatch apiImplPath(MGLModel g) '''«g.apiPath»/impl'''
	def dispatch apiImplPath(EPackage g) '''«g.apiPath»/impl'''
	def dispatch apiImplFQNBase(MGLModel g) '''«g.apiFQNBase».impl'''
	def dispatch apiImplFQNBase(EPackage g) '''«g.apiFQNBase».impl'''
	def dispatch apiImplFQN(ModelElement me) '''«me.modelPackage.apiImplFQNBase».«me.name.fuEscapeJava»Impl'''
	def dispatch apiImplFQN(EObject me) '''«me.modelPackage.apiImplFQNBase».«me.name.fuEscapeJava»Impl'''
	
	@Deprecated
	def dispatch apiFQN(Type me, MGLModel g) '''«g.apiFQNBase».«me.name.fuEscapeJava»'''
	@Deprecated
	def dispatch apiFQN(Attribute me, MGLModel g) '''«g.apiFQNBase».«me.attributeTypeName.fuEscapeJava»'''
	@Deprecated
	def dispatch apiFQN(GraphicalElementContainment me, MGLModel g) '''«g.apiFQNBase».«me.name.fuEscapeJava»'''
	@Deprecated
	def dispatch apiFQN(EObject me, EPackage g) '''«g.apiFQNBase».«me.name.fuEscapeJava»'''
	@Deprecated
	def dispatch apiImplFQN(Type me, MGLModel g) '''«g.apiImplFQNBase».«me.name.fuEscapeJava»Impl'''
	@Deprecated
	def dispatch apiImplFQN(EObject me, EPackage g) '''«g.apiImplFQNBase».«me.name.fuEscapeJava»Impl'''

	def interpreterPackage(GraphModel g) '''info.scce.pyro.interpreter.«g.mglModel.name.lowEscapeJava»'''
	def interpreter(GraphModel g) '''«g.name.fuEscapeJava»Interpreter'''
	def interpreterFQN(GraphModel g) '''«g.interpreterPackage».«g.interpreter»'''

	def apiFactoryImplFQN(GraphModel g) '''«g.mglModel.apiImplFQNBase».«g.apiFactoryImpl»'''
	def apiFactoryImplFQN(MGLModel g) '''«g.apiImplFQNBase».«g.apiFactoryImpl»'''
	
	def dispatch apiFactoryImpl(GraphModel g) '''«g.apiFactory»Impl'''
	def dispatch apiFactoryImpl(EPackage g) '''«g.apiFactory»Impl'''

	def dispatch apiFactory(GraphModel g) '''«g.name.toCamelCase.fuEscapeJava»Factory'''
	def dispatch apiFactory(MGLModel g) '''«g.name.toCamelCase.fuEscapeJava»Factory'''
	def dispatch apiFactory(EPackage g) '''«g.name.toCamelCase.fuEscapeJava»Factory'''
	def dispatch apiFactoryFQN(GraphModel g) '''«g.mglModel.apiFQNBase».«g.apiFactory»'''
	def dispatch apiFactoryFQN(EPackage g) '''«g.apiFQNBase».«g.apiFactory»'''

	def dispatch restFQNBase(MGLModel g) '''info.scce.pyro.«g.name.lowEscapeJava».rest'''
	def dispatch restFQNBase(EPackage g) '''info.scce.pyro.«g.name.lowEscapeJava».rest'''

	def restFQN(EObject me) {
		val modelPackage = me.modelPackage
		if (modelPackage instanceof MGLModel) {
			switch (me) {
				ComplexAttribute: '''«modelPackage.restFQNBase».«(me as ComplexAttribute).type.name.fuEscapeDart»'''
				PrimitiveAttribute: '''«modelPackage.restFQNBase».«attributeTypeName(me).fuEscapeDart»'''
				Type: '''«modelPackage.restFQNBase».«me.name.escapeJava»'''
			}
		} else if (modelPackage instanceof EPackage) {
			'''«modelPackage.restFQNBase».«me.name.fuEscapeJava»'''
		}
	}

	def getReferenceName(Node primeNode) {
		"ref_" + primeNode.primeReference.name
	}

	def typeName(EObject e, CharSequence prefix) '''«prefix»«e.typeName»'''

	def typeName(EObject e) {
		val packageId = e.modelPackage.name.lowEscapeDart
		if (e instanceof ModelElement) {
			switch (e) {
				ComplexAttribute: '''«packageId».«attributeTypeName(e).fuEscapeDart»'''
				PrimitiveAttribute: '''«packageId».«attributeTypeName(e).fuEscapeDart»'''
				Type: '''«packageId».«e.name.fuEscapeDart»'''
			}
		} else {
			'''«packageId».«e.name.fuEscapeDart»'''
		}
	}

	def jarFilename(String path) {
		if (path.contains("/")) {
			return path.subSequence(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
		}
		path.subSequence(0, path.lastIndexOf("\\."));
	}

	def projectServiceName(productDefinition.Annotation s) '''PyroProjectService«s.value.get(1).escapeJava»'''
	def projectServiceClassName(productDefinition.Annotation s) '''«s.projectServiceName»DB'''
	def projectServiceFQN(productDefinition.Annotation s) '''entity.core.«s.projectServiceClassName»'''

	def String getNsURI(MGLModel model) {
		var result = ""
		var prefix = ""
		var cnt = 0
		for (packagePart : model.package.split("\\.")) {
			if (cnt == 0) {
				prefix = packagePart
				result = prefix
				cnt++
			} else if (cnt < 3) {
				prefix = packagePart + "." + prefix
				result = prefix
				cnt++
			} else if (cnt == 4) {
				result = prefix + "/" + packagePart
				cnt++
			} else {
				result += "/" + packagePart
			}
		}
		return "http://" + result
	}
	
	def mcamFQN(GraphModel g) '''«(g.modelPackage as MGLModel).package».mcam'''
	def mcamExecutionFQN(GraphModel g) '''«g.mcamFQN».cli.«g.name.fuEscapeJava»Execution'''
	def mcamAdapterFQN(GraphModel g) '''«g.mcamFQN».adapter.«g.name.fuEscapeJava»Adapter'''
	def mcamAdapterIdFQN(GraphModel g) '''«g.mcamFQN».adapter.«g.name.fuEscapeJava»Id'''
	def coreAPIFQN(String elementType) {
		switch(elementType) {
			case "GraphModel": {
				return "graphmodel.GraphModel"
			}
			case "NodeContainer": {
				return "graphmodel.Container"
			}
			case "ModelElementContainer": {
				return "graphmodel.ModelElementContainer"
			}
			case "Node": {
				return "graphmodel.Node"
			}
			case "Edge": {
				return "graphmodel.Edge"
			}
			case "UserDefinedType": {
				return "graphmodel.IdentifiableElement"
			}
			case "Enumeration": {
				return ""
			}
		}
		return ""
	}
	
	def commandExecuterSwitch(MGLModel m, Function<CharSequence, CharSequence> proc) {
		m.discreteGraphModels.commandExecuterSwitch(proc)
	}
	
	def commandExecuterSwitch(ModelElement me, Function<CharSequence, CharSequence> proc) {
		val graphModels = me.graphModels.filter[!isAbstract].toSet
		commandExecuterSwitch(graphModels, proc)
	}
	
	def commandExecuterSwitch(Set<GraphModel> graphModels, Function<CharSequence, CharSequence> proc) {
		'''
			«FOR gM:graphModels SEPARATOR " else "
			»if(cmdExecuter instanceof «gM.commandExecuter») {
				«gM.commandExecuter» «gM.commandExecuterVar» = («gM.commandExecuter») cmdExecuter;
				«proc.apply(gM.commandExecuterVar)»
			}«
			ENDFOR»
			«IF !graphModels.empty»else
				«ENDIF»if(cmdExecuter != null) throw new RuntimeException("GraphModelCommandExecuter can not handle this type!");
		'''
	}
	
	def getContainableElementsDefinition(mgl.ContainingElement c) {
		val possibleContainments = new LinkedList<Type>
		val containerTypes = (c as ModelElement).resolveSuperTypesAndType.filter(ContainingElement)
		for(container : containerTypes) {
			val containments = container.containableElements.map[types].flatten
			possibleContainments.addAll(containments)
		}
		var result = possibleContainments.stream.distinct.collect(Collectors.toList).filter(Type).toSet
		if(c instanceof GraphModel) {
			result += c.elements.filter(Edge) // edges are always part of the modelElements, since they are used to render the set
		}
		result
	}
	
	def dispatch firstDiscreteType(GraphModel element) {
 		// searching for first discrete-type in hierarchy-chain
 		var e = element
 		while(e.isAbstract && e.extends !== null) {
 			e = e.extends
 		}
 		return e.isAbstract? null : e
	}
	
	def dispatch firstDiscreteType(Edge element) {
 		// searching for first discrete-type in hierarchy-chain
 		var e = element
 		while(e.isAbstract && e.extends !== null) {
 			e = e.extends
 		}
 		return e.isAbstract? null : e
	}
	
	def dispatch firstDiscreteType(Node element) {
 		// searching for first discrete-type in hierarchy-chain
 		var e = element
 		while(e.isAbstract && e.extends !== null) {
 			e = e.extends
 		}
 		return e.isAbstract? null : e
	}
	
	def dispatch firstDiscreteType(UserDefinedType element) {
 		// searching for first discrete-type in hierarchy-chain
 		var e = element
 		while(e.isAbstract && e.extends !== null) {
 			e = e.extends
 		}
 		return e.isAbstract? null : e
	}
	
	// FRONTEND // TODO:SAMI: search for all, and fix if wrong
	def propertyDeserializer(GraphModel g)'''«(g.modelPackage as MGLModel).propertyDeserializer»'''
	def propertyDeserializer(MGLModel g)'''«g.name.fuEscapeDart»PropertyDeserializer'''
	def propertyDeserializerFile(GraphModel g) '''«(g.modelPackage as MGLModel).propertyDeserializerFile»'''
	def propertyDeserializerFile(MGLModel g) '''«g.name.lowEscapeDart»_property_deserializer.dart'''
	
	def commandGraphPath(GraphModel g) '''src/pages/editor/canvas/graphs/«g.modelPackage.name.lowEscapeDart»/«g.commandGraphFile»'''
	def commandGraphFile(GraphModel g) '''«g.name.lowEscapeDart»_command_graph.dart'''
	
	def shapePath(GraphModel g)'''js/«g.modelPackage.name.lowEscapeDart»/«g.modelPackage.name.lowEscapeDart»_shapes.js'''
	def controllerPath(GraphModel g) '''js/«g.modelPackage.name.lowEscapeDart»/«g.name.lowEscapeDart»/controller.js'''
	
	def componentFileDart(GraphModel g) '''«g.name.lowEscapeDart»_component.dart'''
	def componentFileHTML(GraphModel g) '''«g.name.lowEscapeDart»_component.html'''
	def componentCanvasPath(GraphModel g) '''«componentCanvasPath»/«g.modelPackage.name.lowEscapeDart»'''
	def componentCanvasPath() '''src/pages/editor/canvas/graphs'''
	def componentFilePath(GraphModel g) '''«g.componentCanvasPath»/«g.componentFileDart»'''
	
	def modelFilePath(GraphModel g) '''«(g.modelPackage as MGLModel).modelFilePath»'''
	def modelFilePath(MGLModel g) '''src/model/«g.modelFile»'''
	def modelFilePath(EPackage g) '''src/model/«g.modelFile»'''
	def modelFile(MGLModel g) '''«g.name.lowEscapeDart».dart'''
	def modelFile(EPackage g) '''«g.name.lowEscapeDart».dart'''
	
	def treeFile(GraphModel g) '''«g.name.lowEscapeDart»_tree.dart'''
	def propertyElementFileDart(ModelElement me) '''«me.name.lowEscapeDart»_property_component.dart'''
	def propertyElementFileHTML(ModelElement me) '''«me.name.lowEscapeDart»_property_component.html'''
	def propertyComponentFileDart() '''property_component.dart'''
	def propertyComponentFileHTML() '''property_component.html'''
	
	def paletteBuilderFile(GraphModel g) '''«g.name.lowEscapeDart»_palette_builder.dart'''
	def paletteBuilderPackage(GraphModel g) '''src/pages/editor/palette/graphs/«g.modelPackage.name.lowEscapeDart»'''
	def paletteBuilderPath(GraphModel g) '''«g.paletteBuilderPackage»/«g.paletteBuilderFile»'''
	
	def treeFilePath(GraphModel g) '''«g.propertyPackagePath»/«g.treeFile»'''
	def propertyFilePath(GraphModel g) '''«g.propertyGraphModelPath»/«propertyComponentFileDart»'''
	def propertyGraphModelPath(GraphModel g) '''«g.propertyPackagePath»/«g.name.lowEscapeDart»'''
	def propertyElementFilePath(ModelElement me) '''«me.propertyPackagePath»/«me.propertyElementFileDart»'''
	def propertyPackagePath(ModelElement g) '''«(g.modelPackage as MGLModel).propertyPackagePath»'''
	def propertyPackagePath(MGLModel m) '''src/pages/editor/properties/graphs/«m.name.lowEscapeDart»'''
	
	def dartFQN(EObject e) '''«e.modelPackage.name.lowEscapeJava».«e.dartClass»'''
	def dartFQN(ComplexAttribute e) '''«e.type.dartFQN»'''
	def dartFQN(EObject e, CharSequence alias) '''«alias».«e.dartClass»'''
	def dispatch dartClass(EObject e) '''«e.name.fuEscapeJava»'''
	def dispatch dartClass(ComplexAttribute e) '''«e.type.name.fuEscapeJava»'''
	
	def dartImplClass(EObject e) '''impl_«e.dartFQN»'''
	def dartImplPackage(MGLModel e) '''impl_«e.name.lowEscapeJava»'''
	def dartImplPackage(EPackage e) '''impl_«e.name.lowEscapeJava»'''
	
	def jsCall(ModelElement me, GraphModel g) '''«g.name.lowEscapeDart»_«me.name.lowEscapeDart»'''
	def jsCall(GraphModel g) '''«g.name.lowEscapeDart»'''
	
	def lowerType(GraphModel g) '''«g.name.lowEscapeDart»'''
	
	def shapeFQN(ModelElement me) '''«(me.modelPackage as MGLModel).shapeFQN».«me.name.fuEscapeDart»'''
	def shapeFQN(MGLModel m) '''joint.shapes.«m.name.lowEscapeDart»'''
	
	def discreteGraphModels(MGLModel m) {
		return m.graphModels.filter[!isAbstract].toSet
	}
 }
