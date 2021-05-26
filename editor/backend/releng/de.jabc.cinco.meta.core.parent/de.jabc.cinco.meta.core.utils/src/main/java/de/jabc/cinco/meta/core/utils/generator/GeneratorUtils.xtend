package de.jabc.cinco.meta.core.utils.generator

import de.jabc.cinco.meta.core.utils.CincoUtil
import de.jabc.cinco.meta.core.utils.InheritanceUtil
import de.jabc.cinco.meta.core.utils.MGLUtil
import de.jabc.cinco.meta.plugin.event.api.util.EventApiExtension
import de.jabc.cinco.meta.plugin.event.api.util.EventEnum
/*
import de.jabc.cinco.meta.runtime.xapi.GraphModelExtension
import de.jabc.cinco.meta.util.xapi.CollectionExtension
import de.jabc.cinco.meta.util.xapi.FileExtension
import de.jabc.cinco.meta.util.xapi.ResourceExtension
import de.jabc.cinco.meta.util.xapi.WorkbenchExtension
import de.jabc.cinco.meta.util.xapi.WorkspaceExtension
*/
import graphmodel.Container
import graphmodel.IdentifiableElement
import graphmodel.internal.InternalIdentifiableElement
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.List
import java.util.Map
import java.util.Map.Entry
import java.util.Set
import java.util.regex.Pattern
/*
import javax.el.ExpressionFactory
*/
import mgl.Annotatable
import mgl.Annotation
import mgl.Attribute
import mgl.ComplexAttribute
import mgl.ContainingElement
import mgl.Edge
import mgl.Enumeration
import mgl.GraphModel
import mgl.GraphicalModelElement
import mgl.MGLModel
import mgl.ModelElement
import mgl.Node
import mgl.NodeContainer
import mgl.ReferencedEClass
import mgl.ReferencedModelElement
import mgl.Type
import mgl.UserDefinedType
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage
import productDefinition.CincoProduct

import static extension de.jabc.cinco.meta.core.utils.MGLUtil.*

class GeneratorUtils extends InheritanceUtil {
	
	/*
	protected extension CollectionExtension = new CollectionExtension
	*/
	protected extension EventApiExtension = new EventApiExtension
	/*
	protected extension FileExtension = new FileExtension
	protected extension GraphModelExtension = new GraphModelExtension
	protected extension InheritanceUtil = new InheritanceUtil
	protected extension ResourceExtension = new ResourceExtension
    protected extension WorkspaceExtension = new WorkspaceExtension
    protected extension WorkbenchExtension = new WorkbenchExtension
    */
    
	protected static extension MGLUtil
	
	val static String ID_NODES = "Nodes";
	
	public var CincoProduct cpd;
	public var Set<MGLModel> allMGLs = #{};
	
	val HashMap<ContainingElement, Set<Edge>> usableEdgesCache = new HashMap<ContainingElement, Set<Edge>>()
	val HashMap<ContainingElement, Set<Enumeration>> usableEnumsCache = new HashMap<ContainingElement, Set<Enumeration>>()
	val HashMap<ContainingElement, Set<Node>> usableNodesAnyDepthCache = new HashMap<ContainingElement, Set<Node>>()
	val HashMap<ContainingElement, Set<Node>> usableNodesFlatCache = new HashMap<ContainingElement, Set<Node>>()
	val HashMap<ContainingElement, Set<UserDefinedType>> usableUserDefinedTypesCache = new HashMap<ContainingElement, Set<UserDefinedType>>()
	
	// singleton pattern
	private new() {}
	static GeneratorUtils INSTANCE
	private static def synchronized newInstance() { INSTANCE = new GeneratorUtils }
	static def synchronized getInstance() { INSTANCE ?: newInstance }
	
	def clearCaches() {
		usableEdgesCache.clear
		usableEnumsCache.clear
		usableNodesAnyDepthCache.clear
		usableNodesFlatCache.clear
		usableUserDefinedTypesCache.clear
	}
	
	/**
	 * Generates the body of an if-condition for an instanceof
	 * check.
	 * 
	 * @param varName The name of the variable on the generated code
	 * which should be checked against the type of the given {@link ModelElement}
	 * @param me The {@link ModelElemet} against which the check is executed. 
	 */
	def instanceofCheck(ModelElement me, String varName) 
	'''«varName» instanceof «me.fqBeanName»'''
	
	def internalInstanceofCheck(Type t, String varName) 
	'''«varName» instanceof «t.fqInternalBeanName»'''
	
	
	def instanceofCheck(Annotatable a, String varName){
		if (a instanceof ModelElement) {
			var me = a as ModelElement
			return me.instanceofCheck(varName)
		}
	}
	
	/** 
	 * Returns the name of the attribute with an underscore prefix 
	 * 
	**/
	def attributeName(Attribute attr) {
		"_"+attr.name
	}
	
	/**
	 * Returns the {@link ModelElement}'s name in first upper case
	 */
	def fuName(Type t) {
		t.name.toFirstUpper
	}
	
	def fuName(ModelElement me) {
		me.name.toFirstUpper
	}
	
	def fuName(MGLModel mgl) {
		mgl.fileName.toFirstUpper
	}

	def fuName(Attribute attr) {
		attr.name.toFirstUpper
	}
	
	dispatch def fuCName(Type me) {
		"C"+me.name.toFirstUpper
	}
	
	dispatch def fuCName(ContainingElement me) {
		switch(me){
		NodeContainer: "C"+me.name.toFirstUpper
		GraphModel:  "C"+me.name.toFirstUpper
		}
	}
	
	def fuCViewName(Type me) { 
		me.fuCName+"View"
	}
	
	
	def fuCImplName(Type me) {
		"C"+me.name.toFirstUpper+"Impl"
	}
	
	/**
	 * Returns the {@link ModelElement}'s name in first lower
	 */
	def flName(Type me) {
		"_"+me.name.toFirstLower
	}
	
	/**
	 * Returns the {@link ModelElement}'s graphiti api name in first lower
	 */
	def flCName(Type me) {
		'c'+me.fuName
	}
	
	def fuInternalName(ModelElement me) {
		"Internal"+me.name.toFirstUpper
	}
	
	/**
	 * Returns the project name of the project containing the MGL for the given {@link GraphModel}
	 * 
	 */
	def dispatch projectName(GraphModel gm) 
	'''«gm.mglModel.package»'''
	
	def dispatch projectName(MGLModel it)'''«package»'''
	/**
	 * Returns package for generated sources
	 * @param gm The processed {@link GraphModel}
	 */ 
	def getPackage(GraphModel gm){
		gm.mglModel.package + "." + gm.name.toLowerCase
	}
	 
	/**
	 * Returns the package name prefix for the generated graphiti sources.
	 * 
	 * @param gm The processed {@link GraphModel}
	 */
	def dispatch packageName(GraphModel gm)
	'''«gm.mglModel.package».editor.graphiti'''
	
	def dispatch packageName(MGLModel mgl)
	'''«mgl.package».editor.graphiti'''
	
	def packageNameAPI(GraphModel gm)
	'''«gm.packageName».api'''
	
	def packageNameAPI(MGLModel mgl)
	'''«mgl.packageName».api'''
	
	/**
	 * Returns the package name prefix for the generated graphiti sources.
	 * 
	 * @param me The processed {@link ModelElement}
	 */
	def dispatch packageName(ModelElement me)
	'''«me.mglModel.package».editor.graphiti'''
	
	def packageNameAPI(Type me)
	'''«me.mglModel.package».editor.graphiti.api'''
	
	def packageNameEContentAdapter(Type me)
	'''«me.mglModel.package».adapter'''
	
	/**
	 * Returns the package name for the generated {@link ExpressionFactory}
	 * class
	 */
	def packageNameExpression(GraphModel gm)
	'''«gm.mglModel.package».editor.graphiti.expression'''
	
	/**
	 * Returns the package name for the generated {@link IAddFeature} implementing classes
	 */
	def packageNameAdd(ModelElement me)
	'''«me.mglModel.packageName».features.add'''
	
	/**
	 * Returns the package name for the generated {@link ICreateFeature} implementing classes
	 */
	def packageNameCreate(ModelElement me)
	'''«me.mglModel.packageName».features.create'''
	
	/**
	 * Returns the package name for the generated {@link IDeleteFeature} implementing classes
	 */
	def packageNameDelete(ModelElement me)
	'''«me.mglModel.packageName».features.delete'''
	
	/**
	 * Returns the package name for the generated {@link ILayoutFeature} implementing classes
	 */
	def packageNameLayout(ModelElement me)
	'''«me.mglModel.packageName».features.layout'''
	
	/**
	 * Returns the package name for the generated {@link IResizeFeature} implementing classes
	 */
	def packageNameResize(ModelElement me)
	'''«me.mglModel.packageName».features.resize'''
	
	/**
	 * Returns the package name for the generated {@link IMoveFeature} implementing classes
	 */
	def packageNameMove(ModelElement me)
	'''«me.mglModel.packageName».features.move'''
	
	/**
	 * Returns the package name for the generated {@link IUpdateFeature} implementing classes
	 */
	def packageNameUpdate(ModelElement me)
	'''«me.mglModel.packageName».features.update'''
	
	def packageNameReconnect(ModelElement me)
	'''«me.mglModel.packageName».features.reconnect'''
	
	/**
	 * Returns the package name of the business object's java class which is generated for the given node
	 * 
	 * @param me The {@link ModelElement} for which the bean package name is retrieved
	 */
	def beanPackage(Type me)
	'''«IF me instanceof UserDefinedType»«var m = me.eContainer as MGLModel»«m.package».«m.fileName.toLowerCase»
		«ELSE»
		«me.mglModel.package».«me.mglModel.fileName.toLowerCase»«ENDIF»''' 
	
	/**
	 * Returns the fully qualified name of the generated business object java bean for the given {@link ModelElement}
	 * 
	 * @param me The {@link ModelElement} for which the fully qualified bean name should be retrieved
	 */
	def dispatch CharSequence fqBeanName(Type type)
	'''«type.beanPackage».«type.fuName»'''
	 
	def dispatch CharSequence fqBeanName(ModelElement me)
	'''«me.beanPackage».«me.fuName»'''
	
	def dispatch CharSequence fqBeanName(MGLModel mgl)
	'''«mgl.package».«mgl.fileName.toLowerCase».«mgl.fuName»'''
	
	def CharSequence fqBeanNameEscaped(ModelElement me)
	'''«me.beanPackage».«me.fuName.escape»'''
	
	def escape(String s) {
		if (ReservedKeyWords.values.map[it.keyword.toLowerCase].contains(s.toLowerCase))
			"^"+s
		else s
	}
	
	def paramEscape(String s){
		if (ReservedKeyWords.values.map[it.keyword].contains(s))
			s+'_'
		else s
	}
	
	def dispatch CharSequence fqBeanName(ContainingElement ce) {
		switch ce {
			GraphModel : (ce as ModelElement).fqBeanName
			NodeContainer : (ce as ModelElement).fqBeanName
		}
	}
	
	/**
	 * Returns the fully qualified name of the generated internal business object java bean for the given {@link ModelElement}
	 * 
	 * @param me The {@link ModelElement} for which the fully qualified bean name should be retrieved
	 */
	def fqInternalBeanName(Type me)
	'''«me.beanPackage».internal.Internal«me.fuName»'''
	
	def fqBeanImplName(ModelElement me)
	'''«me.beanPackage».impl.«me.fuName»Impl'''
	
	def fqBeanViewName(ModelElement me)
	'''«me.beanPackage».views.impl.«me.fuName»ViewImpl'''
	
	/**
	 * Returns the fully qualified name of the generated Factory for the new API.
	 */
	def fqFactoryName(ModelElement me) 
	'''«me.mglModel.package».factory.«me.mglModel.fileName»Factory'''
	
	def fqFactoryName(MGLModel mgl) 
	'''«mgl.package».factory.«mgl.fileName»Factory'''
	
	
	def fqCreateFeatureName(ModelElement me)
	'''«me.packageNameCreate».CreateFeature«me.fuName»'''
	
	def fqPrimeAddFeatureName(ModelElement me)
	'''«me.packageNameAdd».AddFeaturePrime«me.fuName»'''
	
	/**
	 * Returns the fully qulified name of the generated property view class
	 */
	def fqPropertyView(GraphModel me) 
	'''«me.packageName».property.view.«me.fuName»PropertyView'''
	 
	def fqPropertyView(ModelElement me) 
	'''«me.graphModel.packageName».property.view.«me.graphModel.fuName»PropertyView'''
	
	/**
	 * Returns all model elements of the {@link GraphModel} including the GraphModel itself
	 */
	def modelElements(MGLModel mm) {
		var List<ModelElement> mes = new ArrayList<ModelElement>;
		mes.addAll(mm.nodes)
		mes.addAll(mm.edges)
		mes.addAll(mm.graphModels)
		return mes
	}
	
	def getDtpId(Type t) {
		t.graphModel.dtpId
	}
	
	def getDtpId(GraphModel model)
	'''«model.packageName».«model.name»DiagramTypeProvider'''
	 
	/**
	 * Returns the {@link GraphModel} of the given {@link ModelElement}
	 * 
	 * @param me The {@link ModelElement} for which to retrieve the {@link GraphModel} 
	 */
	 //TODO: FIXME: This won't work for multiple gms
	def getGraphModel(Type me) {
		return (me.eContainer as MGLModel).graphModels.head
	}
	
	def getMGLModel(Type me) {
		return (me.eContainer as MGLModel)
	}
	
	def superClass(ModelElement me) {
		switch (me) {
			NodeContainer : '''«Container.name»'''
			Node : '''«graphmodel.Node.name»'''
			Edge : '''«graphmodel.Edge»'''
			GraphModel : '''«graphmodel.GraphModel.name»'''
		}
	}
	
	/**
	 * Returns the {@link ModelElement}'s name. All letters except for the first letter are in lower case
	 * 
	 * @param The {@link ModelElement}
	 */
	def firstUpperOnly(ModelElement me)
	'''«me.name.toLowerCase.toFirstUpper»'''
	
	/** 
	 * Returns the fully qualified name of the {@link Entry} class
	 */
	def entryName(Class<Entry> e) '''java.util.Map.Entry'''
	
	/**
	 * Returns a map of palette group names to a list of {@link GraphicalModelElement}s. The map is used to
	 * create the appropriate palette groups and the corresponding create tools
	 * 
	 * @param gm The processes {@link GraphModel}
	 */
	def LinkedHashMap<String, List<GraphicalModelElement>> getPaletteGroupsMap(GraphModel gm) {
		val map = new LinkedHashMap<String, List<GraphicalModelElement>>
		map.put(ID_NODES, new ArrayList)
		val usableNodes = gm.getUsableNodes(true)
		for (Node n : usableNodes.filter[!isIsAbstract && primeReference === null && !isCreateDisabled]) {
			if (!hasPaletteCategory(n))
				map.get(ID_NODES).add(n);

			n.annotations.filter[name.equals("palette")].forEach[value.forEach[v | addToMap(map, v, n)]]
		}
		
		for (Edge e : gm.usableEdges.filter[e | !e.isIsAbstract && !e.isCreateDisabled]){
			e.annotations.filter[name.equals("palette")].forEach[value.forEach[v | addToMap(map,v, e)]]
		}
		
		map.remove("none");
		map.remove("None");
		map.remove("NONE");
		
		map.entrySet.sortBy[key]
		
		return map
	}
	
	/**
	 * Returns a set of nodes that can be contained <b>directly</b> in the provided {@link ContainingElement}.
	 * 
	 * Additionally this method can be called with an anyDepth flag. It defaults to false.
	 *
	 * @param model The {@link ContainingElement} to return the usable nodes for
	 */
	def Set<Node> getUsableNodes(ContainingElement model){
		return getUsableNodes(model, false)
	}
	
	/**
	 * Returns a set of nodes that can be contained in the provided {@link ContainingElement}.
	 * 
	 * Additionally this method can be called with an <code>anyDepth</code> flag.
	 * If set to true every node that is usable in any container that is usable within the
	 *  provided {@link ContainingElement} in any depth will be returned as well.
	 *
	 * @param model The {@link ContainingElement} to return the usable nodes for
	 * @param anyDepth Flag whether nodes in usable containers of any depth should be taken into account too
	 */
	def Set<Node> getUsableNodes(ContainingElement model, boolean anyDepth){
		var nodes = _getUsableNodes(model, anyDepth, new HashSet<ContainingElement>())
		
		return nodes
	}
	
	private def Set<Node> _getUsableNodes(ContainingElement model, boolean anyDepth, Set<ContainingElement> alreadyVisited){
		if(anyDepth) {
			val cachedResult = usableNodesAnyDepthCache.entrySet.findFirst[MGLUtil.equalModelElement(model as Type, key as Type)]?.value
			if(!cachedResult.nullOrEmpty) {
				return cachedResult
			}
		} else {
			val cachedResult = usableNodesFlatCache.entrySet.findFirst[MGLUtil.equalModelElement(model as Type, key as Type)]?.value
			if(!cachedResult.nullOrEmpty) {
				return cachedResult
			}
		}
		
		alreadyVisited.add(model)
		var containableElems = model.containableElements
		
		var nodes = containableElems.filter[upperBound != 0].flatMap[ce| ce.types].filter[i | i instanceof Node].map[i| i as Node].toSet
		
		//Also add children of all available MGLs if not prohibited
		var childNodes = allNodeSubTypes(nodes)
		nodes.addAll(childNodes)
		
		//Also add containableElems of containers within the parameter if flag is set
		if(anyDepth) {
			val containers = nodes.filter(ContainingElement).filter[!alreadyVisited.contains(it)].toSet
			alreadyVisited.addAll(containers)
			for(var i = 0; i < containers.size; i++) {
				val newNodes = _getUsableNodes(containers.get(i), anyDepth, alreadyVisited)
				nodes.addAll(newNodes)
				containers.addAll(newNodes.filter(ContainingElement).filter[!alreadyVisited.contains(it)].toSet)
			}
		}		
		
		// Remove duplicates
		val result = new HashSet<Node>()
		nodes.forEach[node |
			if(!result.exists[resultNode | MGLUtil.equalNodes(resultNode, node)]) {
				result.add(node)
			}
		]
		
		// Cache results
		if(anyDepth) {
			usableNodesAnyDepthCache.put(model, result)
		} else {
			usableNodesFlatCache.put(model, result)
		}
		
		return result
	}
	
	def Iterable<? extends Node> allNodeSubTypes(Node it){
		#[it].allNodeSubTypes
	}
	
	def Iterable<? extends Node> allNodeSubTypes(Iterable<Node> it){
		allOtherNodes?.filter[node|
				(node -> node.allSuperNodes).value.exists[superNode|
					it.exists[innerNode | MGLUtil.equalNodes(superNode, innerNode)]
				]
			]
	}
	
	def allOtherNodes(Iterable<Node> nodes){
		return allMGLs?.flatMap[
			if(!it.nodes.nullOrEmpty){
				return it.nodes.filter[!nodes.contains(it)]
			} else {
				return #[]
			}
		]
	}
	
	def Set<Edge> getUsableEdges(ContainingElement model){
		val cachedResult = usableEdgesCache.entrySet.findFirst[MGLUtil.equalModelElement(model as Type, key as Type)]?.value
		if(!cachedResult.nullOrEmpty) {
			return cachedResult
		}
		
		var nodes = model.getUsableNodes(true)
		var edges = nodes.flatMap[node| node.outgoingConnectingEdges + node.incomingConnectingEdges].toSet
		edges += edges.flatMap[MGLUtil.getAllSubclasses(it)].filter(Edge).toSet
		
		// Remove duplicates
		val result = new HashSet<Edge>()
		edges.forEach[edge |
			if(!result.exists[resultEdge | MGLUtil.equalEdges(resultEdge, edge)]) {
				result.add(edge)
			}
		]
		
		usableEdgesCache.put(model, result)
		return result
	}
	
	def Set<Enumeration> getUsableEnums(ContainingElement model){
		val cachedResult = usableEnumsCache.get(model)
		if(!cachedResult.nullOrEmpty) {
			return cachedResult
		}
		
		val Set<ModelElement> nodesAndEdges = model.getUsableNodes.map[it as ModelElement].toSet
		nodesAndEdges.addAll(model.getUsableEdges)
		val result = nodesAndEdges.map[attributes].flatten.filter(Enumeration).toSet
		
		usableEnumsCache.put(model, result)
		return result
	}
	
	def Set<UserDefinedType> getUsableUserDefinedType(ContainingElement model){
		val cachedResult = usableUserDefinedTypesCache.get(model)
		if(!cachedResult.nullOrEmpty) {
			return cachedResult
		}
		
		val Set<ModelElement> nodesAndEdges = model.getUsableNodes(true).flatMap[allSuperNodes].map[it as ModelElement].toSet
		nodesAndEdges.addAll(model.getUsableEdges.flatMap[allSuperEdges.filter(Edge)])
		val resultCandidates = nodesAndEdges.flatMap[attributes].filter(ComplexAttribute).map[type].filter(UserDefinedType).toSet
		
		var currentContainingElement = model as ModelElement
		while(currentContainingElement !== null) {
			resultCandidates.addAll(currentContainingElement.attributes.filter(ComplexAttribute).map[type].filter(UserDefinedType).toSet)
			currentContainingElement = currentContainingElement.extend
		}
		
		// Also add user-defined types usable in other user-defined types
		var newlyAddedUserDefinedTypes = new HashSet(resultCandidates)
		while(!newlyAddedUserDefinedTypes.nullOrEmpty) {
			val newUserDefinedTypes = new HashSet
			for(newlyAddedUserDefinedType : newlyAddedUserDefinedTypes) {
				newlyAddedUserDefinedType.attributes.filter(ComplexAttribute).map[type].filter(UserDefinedType).forEach[candidate |
					if(!resultCandidates.exists[resultCandidate | MGLUtil.equalUserDefinedTypes(candidate, resultCandidate)]) {
						newUserDefinedTypes.add(candidate)
						resultCandidates.add(candidate)
					}
				]
			}
			newlyAddedUserDefinedTypes = newUserDefinedTypes
		}
		
		// Remove duplicates
		val result = new HashSet<UserDefinedType>()
		resultCandidates.forEach[userDefinedType |
			if(!result.exists[resultUserDefinedType | MGLUtil.equalUserDefinedTypes(resultUserDefinedType, userDefinedType)]) {
				result.add(userDefinedType)
			}
		]
		
		usableUserDefinedTypesCache.put(model, result)
		return result
	}
	
	/**
	 * Adds a {@link ModelElement} to the list of the corresponding palette group.
	 * 
	 * @param m The map holding the palette group name to {@link GraphicalModelElement} list mapping
	 * @param paletteName The palette group name the {@link GraphicalModelElement} should be added
	 * @param me The {@link GraphicalModelElement} that should be added into the given palette group
	 */
	def addToMap(Map<String, List<GraphicalModelElement>> m, String paletteName, GraphicalModelElement me) {
		if (m.get(paletteName) === null)
			m.put(paletteName, new ArrayList)
		m.get(paletteName).add(me)
	}
	
	/**
	 * Checks if the given {@link ModelElement} contains a palette annotation
	 * 
	 * @param me The processed {@link ModelElement}
	 * @return true if the {@link ModelElement} contains a palette annotation
	 */
	def hasPaletteCategory(ModelElement me) {
		me.annotations.filter[a | a.name.equals("palette")].size > 0
	}
	
	/**
	 * @param gm The processed {@link GraphModel}
	 * @return All {@link mgl.Attributes} (including {@link Node}, 
	 * {@link Edge}, {@link mgl.Container}, and {@link GraphModel})
	 * used in the definition of the given {@link GraphModel}.
	 */
	def allModelAttributes(GraphModel gm) {
		gm.eResource.allContents.toIterable.filter[c | c instanceof Attribute].map[a | a as Attribute]
	}
	
	/**
	 * @param n The processed {@link Node}
	 * @return The {@link String} value of the {@link Node}'s "icon" annotation 
	 * and the empty {@link String} if no icon annotation provided  
	 */
	def String getIconNodeValue(ModelElement n){
		var icon ="";
		var EList <Annotation> annots = n.annotations;
		for (annot : annots){
			if(annot.name.equals("icon")){
				icon = annot.value.get(0);
			}
		}
		if (n instanceof GraphModel)
			return n.iconPath
		return icon;		
	}
	
	/**
	 * @param n The processed {@link Node}
	 * @return True, if the given node is a primeNode
	 */
	def boolean isPrime(Node n)	{
		return isPrime(n, true)
	}
	
	/**
	 * @param n The processed {@link Node}
	 * @return True, if the given node is a primeNode
	 */
	def boolean isPrime(Node n, boolean includeParentNodes)	{
		if (n === null) return false
		else if(n.retrievePrimeReference(includeParentNodes) !== null)
			return true
		else if (includeParentNodes)
			return n.extends?.isPrime(includeParentNodes)
		else
			return false
	}

	def isCreateDisabled(ModelElement me) {
		CincoUtil::isCreateDisabled(me)
	}

	/**
	 * @param rme The {@link ReferencedModelElement} of a prime node. 
	 * @return The name of the prime reference's type
	 */
	dispatch def primeType(ReferencedModelElement rme) {
		return "Internal"+rme.type.name
	}
	
	def dispatch primeTypeElement(ReferencedModelElement rme) {
		return rme.type.name
	}
	
	def dispatch primeTypeElement(ReferencedEClass rme) {
		return rme.type.name
	}
	
	/**
	 * @param The {@link ReferencedEClass} of a prime node
	 * @return The name of the prime reference's type
	 */
	dispatch def primeType(ReferencedEClass rec) {
		return rec.type.name
	}
	
	/**
	 * @param The processed {@ling Node}
	 * @return The Referenced EClass
	 */
	 def EClass primeTypeEClass(Node n){
	 	val prime = n.retrievePrimeReference
	 	switch prime{
	 		ReferencedEClass : prime.type
	 	}
	 	
	 } 
	
	def retrievePrimeReference(Node n) {
		return MGLUtil::retrievePrimeReference(n)
	}
	
	/**
	 * @param The processed {@link Node}
	 * @return The {@link Node}'s prime reference name
	 */
	def primeName(Node n) {
		return n.retrievePrimeReference.name
	}
	
	def primeFqTypeName(Node n) {
		val prime = n.retrievePrimeReference
		switch prime {
			ReferencedEClass : prime.type.fqBeanName
			ReferencedModelElement : prime.type.fqBeanName
		}
	}

	def primeTypeName(Node n) {
		val prime = n.retrievePrimeReference
		switch prime {
			ReferencedEClass : prime.type.name
			ReferencedModelElement : prime.type.name
		}
	}
	
	
	def String primeTypePackagePrefix(Node n) {
		val prime = n.retrievePrimeReference
		switch prime {
			ReferencedEClass : prime.type.EPackage.nsPrefix
			ReferencedModelElement : prime.type.graphModel.name.toLowerCase
		}
	}
	
	
	/**
	 * This method retrieves the {@link GraphModel} of the {@link ReferencedModelElement}'s type
	 * and returns its nsUri {@see GraphModel}
	 * 
	 * @param rem The {@link ReferencedModelElement} of a prime node
	 * @return The {@link GraphModel#getNsURI nsURI} of the {@link ReferencedModelElement}'s {@link GraphModel}
	 */
	dispatch def String nsURI(ReferencedModelElement rem) {
		MGLUtil.nsURI(rem.type.mglModel)		
	}
	
	/**
	 * This method retrieves the {@link EPackage} of the {@link ReferencedEClass}' type
	 * and returns its {@link EPackage#getNsURI nsURI}.
	 * 
	 * @param refEClass The prime referenced {@link EClass}
	 * @param The {@link EPackage#getNsURI nsURI} of the given {@link EClass}
	 */
	dispatch def String nsURI(ReferencedEClass refEClass) {
		return refEClass.type.EPackage.nsURI
	}
	
	/**
	 * @param n The processed {@link Node}
	 * @return The name of the (additional) {@link IAddFeature} for a prime node
	 */
	def addFeaturePrimeCode(Node n) '''
	new AddFeaturePrime«n.fuName»(this)
	'''
	
	/**
	 *  Checks if a postCreateHook is annotated at the {@link ModelElement}
	 * 
	 * @param me The processed {@link ModelElement} 
	 */
	def booleanWriteMethodCallPostCreate(ModelElement me){
		
		var annot = CincoUtil.findAnnotationPostCreate(me);
		if(annot !== null)
			return true;
		return false;
	}
	
	/**
	 * Generates the postCreate code for the {@link ModelElement}
	 * 
	 * @param me The processed {@link ModelElement} 
	 */
	def writeMethodCallPostCreate(ModelElement me){
		var annot = CincoUtil.findAnnotationPostCreate(me);
		if(annot !== null)
		{
			var class = annot.value.get(0)
			return '''new «class»().postCreate((«me.fqBeanName») modelCreate);'''	
		}
	}
	
	/**
	 *  Checks if a preSaveHook is annotated at the {@link ModelElement}
	 * 
	 * @param me The processed {@link ModelElement} 
	 */
	 def booleanWriteMethodCallPreSave(ModelElement me){
		CincoUtil.findAnnotation(me, 'preSave') !== null ||
		(me.isEventEnabled && EventEnum.PRE_SAVE.accepts(me))
	}
	
	/**
	 * Generates the preSave code for the {@link ModelElement}
	 * 
	 * @param me The processed {@link ModelElement} 
	 */
	def writeMethodCallPreSave(ModelElement me, String gmName) {
		var annot = CincoUtil.findAnnotation(me, 'preSave')
		return '''
			«IF annot !== null»
				new «annot.value.head»().preSave(«gmName»);
			«ENDIF»
			«EventEnum.PRE_SAVE.getNotifyCallJava(me, gmName)»
		'''
	}
	
	/**
	 *  Checks if a postSaveHook is annotated at the {@link ModelElement}
	 * 
	 * @param me The processed {@link ModelElement} 
	 */
	 def booleanWriteMethodCallPostSave(ModelElement me){
		CincoUtil.findAnnotation(me, 'postSave') !== null ||
		(me.isEventEnabled && EventEnum.POST_SAVE.accepts(me))
	}
	
	/**
	 * Generates the postSave code for the {@link ModelElement}
	 * 
	 * @param me The processed {@link ModelElement} 
	 */
	def writeMethodCallPostSave(ModelElement me, String gmName) {
		var annot = CincoUtil.findAnnotation(me, 'postSave')
		return '''
			«IF annot !== null»
				new «annot.value.head»().postSave(«gmName»);
			«ENDIF»
			«EventEnum.POST_SAVE.getNotifyCallJava(me, gmName)»
		'''
	}
	
	/**
	 *  Checks if a preMoveHook is annotated at the {@link ModelElement}
	 * 
	 * @param me The processed {@link ModelElement} 
	 */
	 def booleanWriteMethodCallPreMove(ModelElement me) {
		me.isEventEnabled && EventEnum.PRE_MOVE.accepts(me)
	}
	
	/**
	 * Generates the preMove code for the {@link ModelElement}
	 * 
	 * @param me The processed {@link ModelElement} 
	 */
	def writeMethodCallPreMove(ModelElement me, String meName, String targetContainerName, String xName, String yName) {
		EventEnum.PRE_MOVE.getNotifyCallJava(me, meName, targetContainerName, xName, yName)
	}
	
	/**
	 *  Checks if a postMoveHook is annotated at the {@link ModelElement}
	 * 
	 * @param me The processed {@link ModelElement} 
	 */
	 def booleanWriteMethodCallPostMove(ModelElement me) {
		CincoUtil.findAnnotationPostMove(me) !== null ||
		(me.isEventEnabled && EventEnum.POST_MOVE.accepts(me))
	}
	
	/**
	 * Generates the postMove code for the {@link ModelElement}
	 * 
	 * @param me The processed {@link ModelElement} 
	 */
	def writeMethodCallPostMove(ModelElement me, String meName, String sourceName, String targetName, 
		String xName, String yName, 
		String deltaXName, 	String deltaYName) {
		var annot = CincoUtil.findAnnotationPostMove(me)
		return '''
			«IF annot !== null»
				new «annot.value.head»().postMove(«meName», «sourceName», «targetName», «xName», «yName», «deltaXName», «deltaYName»);
			«ENDIF»
			«EventEnum.POST_MOVE.getNotifyCallJava(me, meName, sourceName, '''«xName» - «deltaXName»''', '''«yName» - «deltaYName»''')»
		'''
	}
	
	/**
	 * Checks if a preResizeHook is annotated at the {@link ModelElement}
	 * 
	 * @param me The processed {@link ModelElement} 
	 */
	def booleanWriteMethodCallPreResize(ModelElement me) {
		me.isEventEnabled && EventEnum.PRE_RESIZE.accepts(me)
	}
	
	/**
	 * Generates the preResize code for the {@link ModelElement}
	 * 
	 * @param me The processed {@link ModelElement} 
	 */
	def writeMethodCallPreResize(ModelElement me, String meName, String widthName, String heightName, String xName, String yName, String directionName) {
		EventEnum.PRE_RESIZE.getNotifyCallJava(me, meName, widthName, heightName, xName, yName, directionName)
	}
	
	/**
	 * Checks if a postResizeHook is annotated at the {@link ModelElement}
	 * 
	 * @param me The processed {@link ModelElement} 
	 */
	def booleanWriteMethodCallPostResize(ModelElement me){
		CincoUtil.findAnnotationPostResize(me) !== null ||
		(me.isEventEnabled && EventEnum.POST_RESIZE.accepts(me))
	}
	
	/**
	 * Generates the postResize code for the {@link ModelElement}
	 * 
	 * @param me The processed {@link ModelElement} 
	 */
	def writeMethodCallPostResize(ModelElement me, String meName, String widthName, String heightName, String xName, String yName, String directionName) {
		var annot = CincoUtil.findAnnotationPostResize(me)
		return '''
			«IF annot !== null»
				«var postResizeClass = annot.value.head»
				new «postResizeClass»().postResize(«meName», «widthName», «heightName», «xName», «yName», «directionName»);
			«ENDIF»
			«EventEnum.POST_RESIZE.getNotifyCallJava(me, meName, widthName, heightName, xName, yName, directionName)»
		'''
	}
	
	/**
	 *  Checks if a postMoveHook is annotated at the {@link ModelElement}
	 * 
	 * @param me The processed {@link ModelElement} 
	 */
	def booleanWriteMethodCallPostSelect(ModelElement me){
		var annot = CincoUtil.findAnnotationPostSelect(me);
		if(annot !== null)
			return true;
		return false;
	}
	
	def writeMethodCallPostSelect(ModelElement me){
		var annot = CincoUtil.findAnnotationPostSelect(me);
		if(annot !== null)
		{
			var class = annot.value.get(0)
			return '''new «class»().postSelect((«me.fqBeanName»)modelSelect);'''	
		}
	}

	def booleanWriteMethodCallDoubleClick(ModelElement me){
		var annot = CincoUtil.findAnnotationDoubleClick(me);
		return annot !== null;
	}
	
	def dispatch getName(ContainingElement ce) {
		switch ce {
			GraphModel : ce.name
			NodeContainer : ce.name
		}
	}
	
	def fqInternalName(ModelElement me) {
		'''«me.beanPackage».internal.«me.fuInternalName»'''
	}
	
	def fqInternalFactoryName(ModelElement me) 
		'''«me.beanPackage».internal.InternalFactory''' 

	def fqInternalPackageName(ModelElement me) 
		'''«me.beanPackage».internal.InternalPackage'''
	
	def <T>Iterable<? extends T> iterable(T t){
		if(t!==null){
			Collections.singletonList(t)
		
		}else{
			new ArrayList<T>
		}
	}
	
	def toInternalElement(String varName) '''
		if («varName» instanceof «IdentifiableElement.name») {
			«varName» = ((«IdentifiableElement.name»)«varName»).getInternalElement();
		}
	'''
	
	def toNonInternalElement(String varName) '''
		if («varName» instanceof «InternalIdentifiableElement.name») {
			«varName» = ((«InternalIdentifiableElement.name»)«varName»).getElement();
		}
	'''

	def getWizardLabel(GraphModel gm) {
		gm.getAnnotationValue("wizard", 0)
	}

	def getWizardClass(GraphModel gm) {
		gm.getAnnotationValue("wizard", 1)
	}
	
	def getAnnotationValue(ModelElement me, String annotationName, int valueIndex) {
		val annot = CincoUtil.findAnnotation(me, annotationName)
		if (annot?.value?.size > valueIndex)
			annot.value.get(valueIndex)
		else null
	}
	
	def ePackageName(GraphModel it)'''«mglModel.fileName.toLowerCase.toFirstUpper»Package'''
	/**
	 * generates the fully qualified EPackage name for a MGL GraphModel
	 */
	def fqEPackageName(GraphModel it)'''«graphModel.beanPackage».«ePackageName»'''
	
	
	def fqBeanPath(ModelElement it){
		fqBeanName.toString.replace('''.''','''/''')
	}
	
	def String getFileName(MGLModel model) {
		val pattern = Pattern.compile("[\\w\\-. ]+(?=.mgl$)")
		var uriString = ""
		val modelResourceURI = model.eResource.URI
		val platformString = modelResourceURI.toPlatformString(true)
		if(platformString !== null) {
			uriString = platformString
		} else {
			uriString = modelResourceURI.toFileString()
		}
		val matcher = pattern.matcher(uriString)
		if(matcher.find()) {
			return matcher.group();
		}
		throw new IllegalStateException("The name of the MGL model \"" + model.package + "\" could not be resolved properly.")
	}
	
}
