package de.jabc.cinco.meta.core.utils.xapi

import de.jabc.cinco.meta.core.utils.MGLUtil
import de.jabc.cinco.meta.core.utils.generator.GeneratorUtils
import de.jabc.cinco.meta.core.utils.registry.NonEmptyRegistry
import java.util.IdentityHashMap
import java.util.Set
import mgl.Annotatable
import mgl.Annotation
import mgl.Attribute
import mgl.ComplexAttribute
import mgl.ContainingElement
import mgl.Edge
import mgl.Enumeration
import mgl.GraphModel
import mgl.GraphicalModelElement
import mgl.Import
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
import org.eclipse.emf.codegen.ecore.genmodel.GenModel
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage

import static org.apache.commons.io.FilenameUtils.removeExtension

import static extension de.jabc.cinco.meta.core.utils.MGLUtil.*
import de.jabc.cinco.meta.core.utils.IWorkspaceContext
import org.eclipse.emf.ecore.EObject

class GraphModelExtension {
	
	GeneratorUtils generatorUtils = GeneratorUtils.instance
	
	public val GenerationContext generationContext = new GenerationContext
	
	
	//================================================================================
    // GraphModel Extensions
    //================================================================================
	def String getProjectName() {
		IWorkspaceContext.getLocalInstance().rootFolderName
	}
	
	def String getProjectSymbolicName(MGLModel model) {
		// TODO: SAMI: ???
		throw new RuntimeException("Not implemented");
	}
	
	def Iterable<GenModel> getImportedGenModels(MGLModel model) {
		model.mglModel.imports
			.filter[importURI.endsWith(".ecore")]
			.map[getGenModel]
	}

	def Iterable<GraphModel> getImportedGraphModels(MGLModel model) {
		model.mglModel.imports
			.filter[importURI.endsWith(".mgl")]
			.map[getImportedGraphModelsOfImport]
			.flatten
	}
	
	def canContain(ContainingElement elm, ModelElement element) {
		elm.containables.exists[it == element]
	}
	
	/**
	 * Returns an iterable of all nodes that be contained in cont.
	 * 
	 * @param cont Node in that all returned nodes can be contained in. 
	 */
	def Iterable<Node> getContainableNodes(ContainingElement cont){
		cont.containables.filter(Node)
	}
	
	/**
	 * Returns an iterable of all containers and can be contained in cont.
	 * 
	 * @param cont Node in that all returned containers can be contained in. 
	 */
	def Iterable<NodeContainer> getContainableNodeContainers(ContainingElement cont){
		cont.containables.filter(NodeContainer)
	}
	
	/**
	 * Returns an iterable of all nodes that are no containers and can be contained in cont.
	 * 
	 * @param cont Node in that all returned nodes can be contained in. 
	 */
	 def Iterable<Node> getContainableNonContainerNodes(ContainingElement cont){
		cont.containableNodes.filter[!(it instanceof NodeContainer)]
	}
	
	/**
	 * Returns an Iterable of all Containers that can contain cont.
	 * 
	 * @param cont ModelElement that can be contained in the returned containers.
	 * The Graphmodel is inferenced from the node cont.
	 */
	def Iterable<NodeContainer> getContainingContainers(Node cont){
		cont.getContainingContainers(cont.mglModel)
	}
	
	/**
	 * Returns an Iterable of all Containers in model that can contain cont.
	 * 
	 * @param cont ModelElement that can be contained in the returned containers.
	 * @param model Graphmodel to be searched for containers. 
	 */
	def Iterable<NodeContainer> getContainingContainers(GraphicalModelElement cont, MGLModel it){
		containers.filter[canContain(cont)]
	}
	
	def getContainables(ContainingElement elm) {
		_containables.get(elm)
	}
	
	val _containables = new NonEmptyRegistry<ContainingElement,Iterable<? extends ModelElement>> [
		val types = containmentRestrictions
		if (types.isEmpty) switch it {
			GraphModel: mglModel.nodes
			NodeContainer: mglModel.nodes
		}
		else types.map[#[it] + subTypes].flatten.toSet
	]
	
	dispatch def getContainmentRestrictions(GraphModel it) {
		containableElements.map[types].flatten.toSet
	}
	
	dispatch def Set<ModelElement> getContainmentRestrictions(NodeContainer it) {
		( #{extends}.filter(NodeContainer)
			.map[containmentRestrictions].flatten
		  + containableElements.map[types].flatten ).toSet
	}
	
	def <T extends ModelElement> getSubTypes(T elm) {
		_subTypes.get(elm) as Set<T>
	}
	
	val _subTypes = new NonEmptyRegistry<ModelElement,Set<ModelElement>> [elm|
		elm.graphModel.modelElements
			.filter[superTypes.exists[it === elm]].toSet
	]
	
	def getSuperType(Type it) {
		switch it {
			GraphModel: extends
			Node: extends
			Edge: extends
			UserDefinedType: extends
		}
	}
	
	def Iterable<? extends ModelElement> getSuperTypes(Type elm) {
		_superTypes.get(elm)
	}
	
	val _superTypes = new NonEmptyRegistry<ModelElement,Iterable<ModelElement>> [elm|
		val superType = elm.superType
		if (superType === null)
			#[]
		else #[superType] + superType.superTypes
	]
	
	def getModelElement(GraphModel it, String name) {
		modelElements.findFirst[it.name == name]
	}
	
	def getModelElements(MGLModel it) {
		graphModels + nodes + edges + userDefinedTypes
	}
	
	def containsModelElement(MGLModel it, String name) {
		modelElements.exists[it.name == name]
	}
	
	def getGraphicalModelElement(GraphModel it, String name) {
		graphicalModelElements.findFirst[it.name == name]
	}
	
	def getGraphicalModelElements(GraphModel it) {
		mglModel.nodes + mglModel.edges
	}
	
	def containsGraphicalModelElement(GraphModel it, String name) {
		graphicalModelElements.exists[it.name == name]
	}
	
	def getContainers(MGLModel it) {
		nodes.filter(NodeContainer)
	}
	
	def getContainers(GraphModel it) {
		MGLUtil.nodes(it).filter(NodeContainer)
	}
	
	def containsContainer(MGLModel it, String name) {
		containers.exists[it.name == name]
	}
	
	def getNonContainerNodes(MGLModel it) {
		nodes.filter[!(it instanceof NodeContainer)]
	}
	
	def getNonContainerNodes(GraphModel it) {
		MGLUtil.nodes(it).filter[!(it instanceof NodeContainer)]
	}
	
	def getUniqueNodesInContainers(GraphModel it) {
		val nodes = MGLUtil.nodesInContainers(it) 
		nodes.removeAll(it.nonContainerNodes)
		return nodes
	}
	
	def containsNonContainerNode(MGLModel it, String name) {
		nonContainerNodes.exists[it.name == name]
	}
	
	def getEnumerations(MGLModel it) {
		types.filter(Enumeration)
	}
	
	def getEnumerations(GraphModel it) {
		MGLUtil.types(it).filter(Enumeration).toSet
	}
	
	def containsEnumeration(MGLModel it, String name) {
		enumerations.exists[it.name == name]
	}
	
	def getUserDefinedTypes(MGLModel it) {
		mglModel.types.filter(UserDefinedType)
	}
	
	def getUserDefinedTypes(GraphModel it) {
		MGLUtil.types(it).filter(UserDefinedType)
	}
	
	def containsUserDefinedType(MGLModel it, String name) {
		userDefinedTypes.exists[it.name == name]
	}
	
	def getPrimeReferences(MGLModel it) {
		nodes.map[primeReference].filterNull
	}
	
	/**
	 * Returns an iterable of all Nodes that are defined in model and have the annotation annot.
	 * 
	 * @param model GraphModel in which the returned nodes are defined.
	 * @param annName Name of the annotation the returned nodes have.
	 */
	def getAllNodesWithAnnotation(MGLModel model, String annName){
		model.mglModel.nodes.filter[hasAnnotation(annName)]
	}
	
	/**
	 * Returns an iterable that contains all values the annotation referenced by annName
	 * has in the mglmodel model.
	 * 
	 * @param model MGLModel to be searched for the annotation
	 * @param annName Name of the annotation
	 */
	def getAllAnnotationValues(MGLModel model, String annName){
		model.getAllNodesWithAnnotation(annName)
			.flatMap[annotations.filter[it.name == annName]]
			.flatMap[value]
	}
	
	//================================================================================
    // ModelElement Extensions
    //================================================================================
	def Iterable<Attribute> getAllAttributes(ModelElement it) {
		_allAttributes.get(it)
	}
	
	val _allAttributes = new NonEmptyRegistry<ModelElement,Iterable<Attribute>> [
		(superType?.allAttributes ?: #[]) + attributes
	]
	
	def hasAnnotation(Annotatable e, String annotationName) {
		if(e.annotations !== null && !e.annotations.empty) {
			e.annotations.exists[name == annotationName]
		}
		false
	}
	
	//================================================================================
    // Node Extensions
    //================================================================================
	def getIncomingEdges(Node node) {
		_incomingEdges.get(node)
	}
	
	val _incomingEdges = new NonEmptyRegistry<Node,Set<Edge>> [node|
		node.incomingEdgeConnections
			.flatMap[connectingEdges]
			.flatMap[#[it] + subTypes].toSet
	]
	
	def getOutgoingEdges(Node node) {
		_outgoingEdges.get(node)
	}
	
	val _outgoingEdges = new NonEmptyRegistry<Node,Set<Edge>> [node|
		node.outgoingEdgeConnections
			.flatMap[connectingEdges]
			.flatMap[#[it] + subTypes].toSet
	]
	
	def isEdgeSource(Node it) {
		!outgoingEdges.isEmpty
	}
	
	def ReferencedType getAnyPrimeReference(Node node) {
		node.primeReference ?: node.extends?.anyPrimeReference
	}
	
	def hasPrimeReference(Node it) {
		anyPrimeReference !== null
	}
	
	//================================================================================
    // Edge Extensions
    //================================================================================
	def getSourceNodes(Edge edge) {
		_sourceNodes.get(edge)
	}
	
	val _sourceNodes = new NonEmptyRegistry<Edge,Iterable<Node>> [edge|
		edge.mglModel.nodes.filter[outgoingEdges.exists[it == edge]]
	]
	
	def getTargetNodes(Edge edge) {
		_targetNodes.get(edge)
	}
	
	val _targetNodes = new NonEmptyRegistry<Edge,Iterable<Node>> [edge|
		edge.mglModel.nodes.filter[incomingEdges.exists[it == edge]]
	]
	
	//================================================================================
    // Attribute Extensions
    //================================================================================
	def getType(Attribute attribute) {
		switch it:attribute {
			PrimitiveAttribute: type
			ComplexAttribute: type
		}
	}
	
	def isList(Attribute attribute) {
		attribute.upperBound != 1
	}
	
	//================================================================================
    // Import Extensions
    //================================================================================
	def getImportedModel(Import imprt) {
		val workspaceContext = IWorkspaceContext.getLocalInstance();
		val path = imprt.importURI;
		val uri = workspaceContext.getFileURI(path)
		workspaceContext.getContent(uri, EObject)
	}
	
	def GenModel getGenModel(Import imprt) {
		val workspaceContext = IWorkspaceContext.getLocalInstance();
		val path = removeExtension(imprt.importURI).concat(".genmodel")
		val uri = workspaceContext.getFileURI(path)
		workspaceContext.getContent(uri, GenModel)
	}
	
	def Set<GraphModel> getImportedGraphModelsOfImport(Import imprt) {
		val workspaceContext = IWorkspaceContext.getLocalInstance();
		var uri = workspaceContext.getFileURI(imprt.importURI)
		if(uri === null)
			throw new RuntimeException("import \""+imprt.importURI+"\" could not resolve to a file")
		val mglModel = workspaceContext.getContent(uri, MGLModel)
		mglModel.graphModels
			.filter[(imprt.eContainer as MGLModel).graphModels.exists[gm | gm.extends?.name == it.name]]
			.toSet
	}
	
	//================================================================================
    // Type Extensions
    //================================================================================
	//TODO modularization: This has been adapted to match new syntax, maybe overhauled to work with multiple graph models
	dispatch def GraphModel getGraphModel(MGLModel model) {
		model.graphModels?.get(0)
	}
	
	dispatch def GraphModel getGraphModel(Type t) {
		if(t.eContainer instanceof MGLModel)
			return (t.eContainer as MGLModel).graphModels?.get(0)
		return (t.eContainer ?: {
			t.eResource.contents.filter[it instanceof GraphModel].get(0)
		}) as GraphModel
	}
	
	def getBeanName(Type it)
		'''«name.toFirstUpper»'''
	
	def dispatch String getBeanPackage(Type type) {
		generatorUtils.beanPackage(type).toString
	}
	def dispatch String getBeanPackage(MGLModel model){
		_beanPackage.get(model)
	}
	val _beanPackage = new NonEmptyRegistry<Object,String> [switch it {
		GraphModel: '''«mglModel.package».«name.toLowerCase»'''
		MGLModel:  '''«mglModel.package»'''
		default: {
				val uri = mglModel.eResource.URI
				val fileName = uri.segment(uri.segmentCount - 2) // TODO:SAMI: test needed
				'''«mglModel.package».«fileName».toLowerCase»'''
			}
	}]
	
	def getFqBeanName(Type it) 
		'''«beanPackage».«beanName»'''
	
	def getInternalBeanName(Type it)
		'''Internal«name.toFirstUpper»'''
	
	def String getInternalBeanPackage(Type type) {
		_internalBeanPackage.get(type)
	}
	
	val _internalBeanPackage = new NonEmptyRegistry<Type,String> [switch it {
		GraphModel: '''«mglModel.package».«generatorUtils.getFileName(mglModel).toLowerCase».internal'''
		default: graphModel.internalBeanPackage
	}]
	
	def getFqInternalBeanName(Type it)
		'''«internalBeanPackage».«internalBeanName»'''
	
	//================================================================================
	// ReferencedType Extensions
	//================================================================================
	def getType(ReferencedType it) {
		switch it {
			ReferencedModelElement: type
			ReferencedEClass: type
		}
	}
	
	def getTypeName(ReferencedType primeRef) {
		switch it:primeRef {
			ReferencedEClass : type.name
			ReferencedModelElement : type.name
		}
	}
	
	def getFqBeanName(ReferencedType primeRef) {
		switch primeRef {
			ReferencedModelElement: primeRef.type.fqBeanName
			ReferencedEClass: {
				val primeEPackage = primeRef.type.EPackage
				val genPkg = primeRef.getGenModel.genPackages.findFirst[name == primeEPackage.name]
				primeRef.getFqBeanName(genPkg)
			}
		}
	}
	
	def getFqBeanName(ReferencedType primeRef, GenPackage genPkg) {
		var pkg = ""
		if (genPkg.basePackage !== null)
			pkg += genPkg.basePackage + "."
		val genPkgName = genPkg.name
		if (genPkgName !== null)
			pkg += genPkgName + "."
		return pkg + primeRef.typeName
	}
	
	def getImportedModel(ReferencedEClass primeRef) {
		getImportedModel(primeRef.imprt)
	}
	
	def getGenModel(ReferencedEClass primeRef) {
		getGenModel(primeRef.imprt)
	}
	
	def getName(GenPackage genPkg) {
		genPkg.getEcorePackage?.name
		?: genPkg.prefix?.toLowerCase
	}
	
	//================================================================================
    // Annotation Extensions
    //================================================================================
    /**
	 * Returns the ModelElement associated with the Annotation annot.
	 * 
	 * @param annot
	 */
	def getAnnotatedModelElement(Annotation annot) {
		switch it:annot.parent {
			ModelElement: it
		}
	}
	
	/**
	 * Returns the Attribute associated with the Annotation annot.
	 * 
	 * @param annot
	 */
	def getAnnotatedAttribute(Annotation annot) {
		switch it:annot.parent {
			Attribute: it
		}
	}
	
	//================================================================================
    // Static Utility Classes
    //================================================================================
	static class GenerationContext {
		
		val IdentityHashMap<Object,Object> map = new IdentityHashMap
		
		def <T> T get(T key) {
			val value = map.get(key)
			if (value !== null) value as T else null
		}
		
		def <T> T put(T key, T value) {
			val oldVal = map.put(key, value)
			if (oldVal !== null) oldVal as T else null 
		}
	}
}
