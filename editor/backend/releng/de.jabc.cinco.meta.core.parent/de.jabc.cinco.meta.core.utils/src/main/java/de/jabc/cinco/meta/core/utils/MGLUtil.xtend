package de.jabc.cinco.meta.core.utils

import de.jabc.cinco.meta.core.utils.dependency.DependencyGraph
import de.jabc.cinco.meta.core.utils.dependency.DependencyNode
import de.jabc.cinco.meta.core.utils.generator.GeneratorUtils
import de.jabc.cinco.meta.plugin.event.api.util.EventApiExtension
import de.jabc.cinco.meta.plugin.event.api.util.EventEnum
import java.net.URL
import java.util.AbstractMap
import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedHashSet
import java.util.List
import java.util.Map.Entry
import java.util.Set
import java.util.Stack
import java.util.stream.Collectors
import mgl.Annotation
import mgl.Attribute
import mgl.ComplexAttribute
import mgl.ContainingElement
import mgl.Edge
import mgl.EdgeElementConnection
import mgl.GraphModel
import mgl.GraphicalElementContainment
import mgl.Import
import mgl.IncomingEdgeElementConnection
import mgl.MGLModel
import mgl.MglFactory
import mgl.ModelElement
import mgl.Node
import mgl.NodeContainer
import mgl.OutgoingEdgeElementConnection
import mgl.PrimitiveAttribute
import mgl.ReferencedModelElement
import mgl.ReferencedType
import mgl.Type
import mgl.UserDefinedType
import mgl.Wildcard
import mgl.impl.MglFactoryImpl
import org.eclipse.emf.common.util.BasicEList
import org.eclipse.emf.common.util.TreeIterator
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.util.EcoreUtil
import productDefinition.CincoProduct
import style.Image
import style.Styles
import org.eclipse.emf.common.util.URI

class MGLUtil {
	static extension GeneratorUtils = GeneratorUtils.instance
	static extension EventApiExtension = new EventApiExtension
	
	public static HashMap<MGLModel, CincoProduct> mglModelCpdMap = new HashMap<MGLModel, CincoProduct>()

	/**
	 * Preprocesses connections and containments the model elements of the
	 *  <code>mglModels</code> to ensure proper handling of other processes later.
	 * This includes:
	 * 	<ul>
	 * 		<li>Handle incoming and outgoing edges if no are stated or a wildcard token has been used</li>
	 * 		<li>
	 * 			Handle containments for graph models and node containers if no relationships
	 * 			 have been stated or a wildcard token has been used 
	 * 		</li>
	 * 		<li>Inherit connection constraints of any parent model elements</li>
	 * 	</ul>
	 * 
	 * @param	mglModels	the {@link Iterable} of {@link MGLModel} for which all of model element should be preprocessed
	 */
	def static Set<MGLModel> prepareMglModels(Set<MGLModel> mglModels) {
		for(mglModel : mglModels) {
			var connectableElements = new ArrayList<Node>()
			connectableElements.addAll(mglModel.nodes)
			
			for(graphModel: mglModel.graphModels){
				handleWildcardContainments(graphModel)
			}
	
			for (nc : mglModel.nodes.filter(NodeContainer)) {
				handleWildcardContainments(nc)
			}
		}
		
		mglModels.forEach[nodes.forEach[handleWildcardConnections]]
		
		for(mglModel : mglModels) {
			inheritAllConnectionConstraints(mglModel)
			inheritAllContainmentConstraints(mglModel)
		}

		return mglModels
	}
	
	/**
	 * Resolves the connection wildcards and replaces them with constraints for all edges contained in the same MGL
	 * in which the provided <code>node</code> has been declared in.
	 * 
	 * @param	node	the {@link Node} for which all connection wildcards should be resolved
	 */
	def static handleWildcardConnections(Node node) {
		for (incomingWildcard : node.incomingWildcards) {
			addEdgesAsIncomingConnection(
				node,
				incomingWildcard.lowerBound,
				incomingWildcard.upperBound,
				incomingWildcard.mglModel?.edges
			)
		}
		for (outgoingWildcard : node.outgoingWildcards) {
			addEdgesAsOutgoingConnection(
				node,
				outgoingWildcard.lowerBound,
				outgoingWildcard.upperBound,
				outgoingWildcard.mglModel?.edges
			)
		}
	}
	
	/**
	 * Adds all incoming and outgoing connection constraints to all nodes of the parameter {@link MGLModel}.
	 * As a result all nodes contains all incoming and outgoing edge connections of all their parent nodes of any depth.
	 * 
	 * @param	mglModel	the {@link MGLModel} for which all nodes should get their parent connections constraints added
	 */
	def static inheritAllConnectionConstraints(MGLModel mglModel) {
		for (n : mglModel.nodes) {
			val inheritedConnectionConstraints = getInheritedConnectionConstraints(n)
			val copiedIncoming = copyConnectionConstraints(inheritedConnectionConstraints.key)
			val copiedOutgoing = copyConnectionConstraints(inheritedConnectionConstraints.value)
			EcoreUtil.deleteAll(n.incomingEdgeConnections, false)
			n.incomingEdgeConnections += copiedIncoming.filter(IncomingEdgeElementConnection)
			EcoreUtil.deleteAll(n.outgoingEdgeConnections, false)
			n.outgoingEdgeConnections += copiedOutgoing.filter(OutgoingEdgeElementConnection)
		}
	}
	
	/**
	 * Adds all containment constraints to all graph models and containers of the parameter {@link MGLModel}.
	 * As a result all graph models and containers contain all containment constraints of all their parents of any depth.
	 * 
	 * @param	mglModel	the {@link MGLModel} for which all graph models and containers should get their parent containment constraints added
	 */
	def static inheritAllContainmentConstraints(MGLModel mglModel) {
		for (containingElement : (mglModel.nodes.filter(NodeContainer) + mglModel.graphModels)) {
			val inheritedConnectionConstraints = getInheritedContainmentConstraints(containingElement)
			val copiedConstraints = copyContainmentConstraints(inheritedConnectionConstraints)
			EcoreUtil.deleteAll(containingElement.containableElements, true)
			containingElement.containableElements += copiedConstraints
		}
	}
	
	/**
	 * Creates copies of the provided <code>elementConnectionsToCopy</code>.
	 * The copy contains the same <code>connectingEdges</code>, <code>upperBound</code> and <code>lowerBound</code>.
	 * 
	 * This function can be used to utilize connection constraints on multiple model elements without violating the cardinality of
	 * the model elements constraint cardinality.
	 * 
	 * @param	elementConnectionsToCopy an {@link Iterable} that contains {@link EdgeElementConnection EdgeElementConnections}
	 * 										that should be copied
	 */
	def static <T extends EdgeElementConnection> copyConnectionConstraints(Iterable<T> elementConnectionsToCopy) {
		val result = new ArrayList<T>()
		val mglFactory = MglFactoryImpl.init
		for(elementConnectionToCopy : elementConnectionsToCopy) {
			if(elementConnectionToCopy.eContainer === null) {
				result.add(elementConnectionToCopy)
			} else {
				var EdgeElementConnection newElementConnection;
				if(elementConnectionToCopy instanceof IncomingEdgeElementConnection) {
					newElementConnection = mglFactory.createIncomingEdgeElementConnection
				} else {
					newElementConnection = mglFactory.createOutgoingEdgeElementConnection
				}
				newElementConnection.connectingEdges += elementConnectionToCopy.connectingEdges
				newElementConnection.upperBound = elementConnectionToCopy.upperBound
				newElementConnection.lowerBound = elementConnectionToCopy.lowerBound
				
				result.add(newElementConnection as T)
			}			
		}
		return result
	}
	
	/**
	 * Creates copies of the provided <code>containmentConstraintsToCopy</code>.
	 * The copy contains the same <code>types</code>, <code>upperBound</code> and <code>lowerBound</code>.
	 * 
	 * This function can be used to utilize containment constraints on multiple containing elements without violating the cardinality of
	 * the containing elements constraint cardinality.
	 * 
	 * @param	containmentConstraintsToCopy an {@link Iterable} that contains {@link GraphicalElementContainment GraphicalElementContainments}
	 * 										that should be copied
	 */
	def static copyContainmentConstraints(Iterable<GraphicalElementContainment> containmentConstraintsToCopy) {
		val result = new ArrayList<GraphicalElementContainment>()
		val mglFactory = MglFactoryImpl.init
		for(containmentConstraintToCopy : containmentConstraintsToCopy) {
			val newContainmentConstraint = mglFactory.createGraphicalElementContainment
			newContainmentConstraint.types += containmentConstraintToCopy.types
			newContainmentConstraint.upperBound = containmentConstraintToCopy.upperBound
			newContainmentConstraint.lowerBound = containmentConstraintToCopy.lowerBound
			
			result.add(newContainmentConstraint)			
		}
		return result
	}
	
	/**
	 * Returns all connection constraints the provided <code>node</code> should possess, inherited or self-defined.
	 * This includes all parent connection constraints but also respects overridden constraints by child nodes.
	 * 
	 * Returned inherited constraints are copied by {@link #copyConnectionConstraints(Iterable) copyConnectionConstraints}.
	 * 
	 * @param	node	the {@link Node} for which all connection constraints it should possess should be returned
	 * @returns	an {@link Entry} that contains a {@link List} as its key that again contains all {@link IncomingEdgeElementConnection}
	 * 			the provided <code>node</node> should possess. The entry's value is a {@link List} that contains all
	 * 			{@link OutgoingEdgeElementConnection} the provided <code>node</node> should possess
	 */
	def static Entry<List<IncomingEdgeElementConnection>, List<OutgoingEdgeElementConnection>>  getInheritedConnectionConstraints(Node node) {
		val inheritedConstraints = new AbstractMap.SimpleEntry<List<IncomingEdgeElementConnection>, List<OutgoingEdgeElementConnection>>(
			new ArrayList<IncomingEdgeElementConnection>(),
			new ArrayList<OutgoingEdgeElementConnection>()
		)
		
		if(node.extends !== null) {
			// Recursive call and add aggregate the results
			val inheritedParentConstraints = getInheritedConnectionConstraints(allMGLs.flatMap[nodes].findFirst[equalNodes(node.extends)])
			inheritedConstraints.key.addAll(copyConnectionConstraints(inheritedParentConstraints.key))
			inheritedConstraints.value.addAll(copyConnectionConstraints(inheritedParentConstraints.value))
		}
		
		// Integrate current node's constraints and remove parent constraints if overridden
		node.incomingEdgeConnections.forEach[incomingConnectionConstraint |
			incomingConnectionConstraint.connectingEdges.forEach[edge |
				inheritedConstraints.key.forEach[inheritedConstraint |
					inheritedConstraint.connectingEdges.removeIf[MGLUtil.equalEdges(it, edge)]
				]
			]
			inheritedConstraints.key.add(incomingConnectionConstraint)
		]
		node.outgoingEdgeConnections.forEach[outgoingConnectionConstraint |
			outgoingConnectionConstraint.connectingEdges.forEach[edge |
				inheritedConstraints.value.forEach[connectingEdges.removeIf[MGLUtil.equalEdges(it, edge)]]
			]
			inheritedConstraints.value.add(outgoingConnectionConstraint)
		]
		
		// Remove constraints with upper bound of zero or if they are empty
		inheritedConstraints.key.removeIf[upperBound == 0 || connectingEdges.nullOrEmpty]
		inheritedConstraints.value.removeIf[upperBound == 0 || connectingEdges.nullOrEmpty]
		
		return inheritedConstraints
	}
	
	/**
	 * Returns all containment constraints the provided <code>node</code> should possess, inherited or self-defined.
	 * This includes all parent containment constraints but also respects overridden constraints by child nodes.
	 * 
	 * Returned inherited constraints are copied by {@link #copyContainmentConstraints(Iterable) copyContainmentConstraints}.
	 * 
	 * @param	node	the {@link Node} for which all containment constraints it should possess should be returned
	 * @returns	a {@link List} that contains all {@link GraphicalElementContainment GraphicalElementContainments}
	 * 			the provided <code>node</node> should possess
	 */
	def static List<GraphicalElementContainment>  getInheritedContainmentConstraints(ContainingElement containingElement) {
		val inheritedConstraints = new ArrayList<GraphicalElementContainment>()
		
		if(containingElement instanceof GraphModel) {
			if(containingElement.extends !== null) {
				// Recursive call and add aggregate the results
				inheritedConstraints.addAll(copyContainmentConstraints(getInheritedContainmentConstraints(allMGLs.flatMap[graphModels].findFirst[equalGraphModels(containingElement.extends)])))
			}
		} else if(containingElement instanceof NodeContainer) {
			var parentElement = containingElement.extends
			while(parentElement !== null) {
				if(parentElement instanceof NodeContainer) {
					// Recursive call and add aggregate the results
					inheritedConstraints.addAll(copyContainmentConstraints(getInheritedContainmentConstraints(allMGLs.flatMap[nodes].findFirst[equalNodes(containingElement.extends)] as NodeContainer)))
					parentElement = null
				} else {
					parentElement = parentElement.extends
				}
			}
		}
		
		// Integrate current node's constraints and remove parent constraints if overridden
		containingElement.containableElements.forEach[containmentConstraint |
			containmentConstraint.types.forEach[type |
				inheritedConstraints.forEach[types.removeIf[MGLUtil.equalModelElement(it, type)]]
			]
			inheritedConstraints.add(containmentConstraint)
		]
		
		// Remove constraints with upper bound of zero or if they are empty
		inheritedConstraints.removeIf[upperBound == 0 || types.nullOrEmpty]
		
		return inheritedConstraints
	}

	/**
	 * Adds all parameter <code>nodes</code> with the parameter <code>lower</code> and <code>upper</code> bounds
	 * as a newly created {@link ContainingElement} to the provided {@link ContainingElement}.
	 * 
	 * @param	ce	the {@link ContainingElement} which should retrieve the new containments
	 * @param	lower	the lower bound for the containment
	 * @param	upper	the upper bound for the containment
	 * @param	nodes	all nodes which should be added as a possible containment
	 */
	def static addNodesAsContainment(ContainingElement ce, int lower, int upper, Node... nodes) {
		var gec = MglFactory.eINSTANCE.createGraphicalElementContainment;
		gec.setLowerBound(lower);
		gec.setUpperBound(upper);
		gec.getTypes().addAll(Arrays.asList(nodes));
		ce.getContainableElements().add(gec);
	}
	
	/**
	 * Adds all parameter <code>edges</code> with the parameter <code>lower</code> and <code>upper</code> bounds
	 * as a newly created {@link IncomingEdgeElementConnection} to the provided <code>node</code>.
	 * 
	 * @param	node	the {@link Node} which should retrieve the new incoming connections
	 * @param	lower	the lower bound for the incoming connection
	 * @param	upper	the upper bound for the incoming connection
	 * @param	edges	all {@link Edge Edges} which should be added as a possible incoming connection
	 */
	def static addEdgesAsIncomingConnection(Node node, int lower, int upper, Edge... edges) {
		var ieec = MglFactory.eINSTANCE.createIncomingEdgeElementConnection;
		ieec.setLowerBound(lower);
		ieec.setUpperBound(upper);
		ieec.getConnectingEdges().addAll(Arrays.asList(edges));
		node.incomingEdgeConnections.add(ieec);
	}
	
	/**
	 * Adds all parameter <code>edges</code> with the parameter <code>lower</code> and <code>upper</code> bounds
	 * as a newly created {@link OutgoingEdgeElementConnection} to the provided <code>node</code>.
	 * 
	 * @param	node	the {@link Node} which should retrieve the new outgoing connections
	 * @param	lower	the lower bound for the outgoing connection
	 * @param	upper	the upper bound for the outgoing connection
	 * @param	edges	all {@link Edge Edges} which should be added as a possible outgoing connection
	 */
	def static addEdgesAsOutgoingConnection(Node node, int lower, int upper, Edge... edges) {
		var ieec = MglFactory.eINSTANCE.createOutgoingEdgeElementConnection;
		ieec.setLowerBound(lower);
		ieec.setUpperBound(upper);
		ieec.getConnectingEdges().addAll(Arrays.asList(edges));
		node.outgoingEdgeConnections.add(ieec);
	}

	/**
	 * Adds all nodes of the referenced {@link MGLModel} as possible containments to the <code>containingElement</code>.
	 * This is only done if any containment contains the wildcard (*) token.
	 * The upper and lower bound of the containment are adopted from the containment with the wildcard token.
	 * If multiple wildcard containments are present only the first will be processed.
	 * 
	 * @param	containingElement	the {@link ContainingElement} for which wildcard containments should be processed
	 */
	def static handleWildcardContainments(ContainingElement containingElement) {
		for (containmentWildcard : containingElement.containmentWildcards) {
			addNodesAsContainment(containingElement, containmentWildcard.lowerBound,
				containmentWildcard.upperBound, containmentWildcard.mglModel.nodes
			);
		}
	}	

	/**
	 * Returns all {@link ContainingElement ContainingElements} the MGL of the <code>graphModel</code> contains.
	 * The returned {@link Set} contains the <code>graphModel</code> itself too.
	 * 
	 * @param	graphModel	the <code>GraphModel</code> to retrieve the {@link ContainingElement ContainingElements}
	 * 							of its MGL from
	 * @return	a {@link Set} which contains all {@link ContainingElement ContainingElements} defined in the MGL
	 * 				the parameter <code>graphModel</code> has been defined in
	 */
	def static Set<ContainingElement> getContainingElements(GraphModel graphModel) {
		(graphModel.mglModel.nodes.filter(ContainingElement)+#[graphModel]).toSet
	}

	/**
	 * Returns all {@link Node Nodes} the <code>edge</code> can have as a target.
	 * These are nodes which have specified the edge as one of their <code>incomingEdges</code> in the MGL.
	 * <br /> <br />
	 * To retrieve also all nodes which are targetable through parent edge types see {@link #getAllPossibleTargets(Edge) getAllPossibleTargets}.
	 * 
	 * @param	edge	the <code>Edge</code> for which the possible target's should be returned
	 * @return	a {@link Set} which contains all {@link Node Nodes} the <code>edge</code> can have as its target
	 * @see		#getAllPossibleTargets(Edge)	getAllPossibleTargets(Edge)
	 */
	def static Set<Node> getPossibleTargets(Edge edge) {
		val HashSet<Node> targets = new HashSet()
		targets += allMGLs.flatMap[nodes].filter[incomingConnectingEdges.exists[equalEdges(edge)]]
		return targets.removeDuplicateModelElements.filter(Node).toSet
	}
	
	/**
	 * Returns all {@link Node Nodes} the <code>edge</code> can have as a targets.
	 * Includes nodes which can be targets of any of <code>edge</code>'s parents.
	 * These are nodes which have specified the edge or any of its parents as one of their <code>incomingEdges</code> in the MGL.
	 * <br /> <br/>
	 * To retrieve only nodes which are targetable by the edge directly without considering its parents see {@link #getPossibleTargets(Edge) getPossibleTargets}.
	 * 
	 * @param	edge	the <code>Edge</code> for which the possible target's should be returned
	 * @return	a {@link Set} which contains all {@link Node Nodes} the <code>edge</code> and any of its parents can have as its target
	 * @see		#getPossibleTargets(Edge)	getPossibleTargets(Edge)
	 */
	def static getAllPossibleTargets(Edge edge){
		(edge.allSuperTypes.map[it as Edge].flatMap[it.possibleTargets] + edge.possibleTargets).toSet
	}

	/**
	 * Returns all {@link Node Nodes} the <code>node</code> can have as possible successors.
	 * These nodes are all node types that can be possible targets of any outgoing edge type of the <code>node</code>.
	 * The result doesn't consider inheritances of the {@link Node Nodes} or <code>Edge</code>s.
	 * 
	 * @param	node	the {@link Node} for which all possible successors should be returned
	 * @return	a {@link Set} which contains all {@link Node Nodes} the <code>node</code> can have as its
	 * 				successor via an outgoing <code>Edge</code>
	 * @see		#getPossiblePredecessors(Node)	getPossiblePredecessors(Node)
	 * @see		#getPossibleTargets(Edge)		getPossibleTagets(Edge)
	 */
	def static Set<Node> getPossibleSuccessors(Node node) {
		var Set<Node> possibleSuccessors = new HashSet<Node>()
		var Set<Edge> outgoingEdgess = getOutgoingConnectingEdges(node)
		for (edge : outgoingEdgess) {
			possibleSuccessors.addAll(getPossibleTargets(edge))
		}
		return possibleSuccessors
	}

	/**
	 * Returns all {@link Node Nodes} the <code>node</code> can have as possible predecessors.
	 * These nodes are all node types that can be possible sources of any incoming edge type of the <code>node</code>.
	 * The result doesn't consider inheritances of the {@link Node Nodes} or <code>Edge</code>s.
	 * 
	 * @param	node	the {@link Node} for which all possible predecessors should be returned
	 * @return	a {@link Set} which contains all {@link Node Nodes} the <code>node</code> can have as its
	 * 				predecessor via an incoming <code>Edge</code>
	 * @see		#getPossibleSuccessors(Node)	getPossibleSuccessors(Node)
	 * @see		#getPossibleSources(Edge)		getPossibleSources(Edge)
	 */
	def static Set<Node> getPossiblePredecessors(Node node) {
		var Set<Node> possiblePredecessors = new HashSet<Node>()
		var Set<Edge> incomingEdges = getIncomingConnectingEdges(node)
		for (edge : incomingEdges) {
			possiblePredecessors.addAll(getPossibleSources(edge))
		}
		return possiblePredecessors
	}

	/**
	 * Returns all {@link Node Nodes} the <code>edge</code> can possibly originate from.
	 * These nodes have <code>edge</code>'s type declared as one of their <code>outgoingEdges</code>.
	 * <br /> <br />
	 * To retrieve also all nodes from which any parent edge types can originate see {@link #getAllPossibleSources(Edge) getAllPossibleSources}.
	 * 
	 * @param 	edge	the <code>edge</code> for which all possible source <code>Nodes</code> should be returned
	 * @return	a {@link Set} which contains all {@link Node Nodes} which have the <code>edge</code>'s type
	 * 				defined as any of their <code>outgoingEdges</code>
	 * @see		#getAllPossibleSources(Edge)	getAllPossibleSources(Edge)
	 */
	def static Set<Node> getPossibleSources(Edge edge) {
		val HashSet<Node> sources = new HashSet()
		sources += allMGLs.flatMap[nodes].filter[outgoingConnectingEdges.exists[equalEdges(edge)]]
		return sources.removeDuplicateModelElements.filter(Node).toSet
	}
	
	/**
	 * Returns all {@link Node Nodes} the <code>edge</code> or any of its parent <code>Edge</code>s can possibly originate from.
	 * These nodes have <code>edge</code>'s or any of its parent's types declared as one of their <code>outgoingEdges</code>.
	 * <br /> <br />
	 * To retrieve only these nodes from which the edge itself can originate see {@link #getPossibleSources(Edge) getPossibleSources}.
	 * 
	 * @param 	edge	the <code>edge</code> for which all possible source <code>Nodes</code> should be returned
	 * @return	a {@link Set} which contains all {@link Node Nodes} which have the <code>edge</code>'s type
	 * 				defined as any of their <code>outgoingEdges</code>
	 * @see		#getAllPossibleSources(Edge)	getAllPossibleSources(Edge)
	 */
	def static getAllPossibleSources(Edge edge){
		(edge.allSuperTypes.map[it as Edge].flatMap[it.possibleSources] + edge.possibleSources).toSet
	}

	/**
	 * Returns all <code>Edge</code>s the <code>node</code> can be the target for.
	 * These edges are declared as one of the <code>incomingEdges</code> of the <code>node</code>.
	 * 
	 * @param	node	the {@link Node} for which all possible incoming <code>Edge</code>s should be returned
	 * @return	a {@link Set} which contains all <code>Edge</code>s which can have the <code>node</code> as
	 * 			their target
	 */
	def static Set<Edge> getIncomingConnectingEdges(Node node) {
		val HashSet<Edge> result = new HashSet<Edge>()
		val incomingEdgeConnections = node.incomingEdgeConnections
		if(incomingEdgeConnections.size == 1 && incomingEdgeConnections.get(0).connectingEdges.empty) {
			result.addAll(node.mglModel.edges)
		} else {
			incomingEdgeConnections.forEach[result.addAll(it.getConnectingEdges())]			
		}
		return result
	}
	
	/**
	 * Returns all <code>Edge</code>s the <code>node</code> or any of it parents can be the source for.
	 * These edges have been declared in the <code>node</code>'s or one of its parent's <code>outgoingEdges</code>.
	 * <br /> <br/>
	 * This method calls {@link #getOutgoingConnectingEdges(Node, boolean) getPossibleSources(Node, boolean)}
	 * with the <code>includeNodeSuperTypes</code> parameter set to true.
	 * 
	 * @param	node	the {@link Node} for which the outgoing <code>Edge</codes>s of it and its parents should be returned
	 * @returns	a {@link Set} which contains all <code>Edge</code>s which can have the <code>node</code> or any of its parents
	 * 			as their source
	 * @see		#getOutgoingConnectingEdges(Node, boolean) getPossibleSources(Node, boolean)
	 */
	def static Set<Edge> getOutgoingConnectingEdges(Node node) {
		return getOutgoingConnectingEdges(node, true)
	}

	/**
	 * Returns all <code>Edge</code>s the <code>node</code> can be the source for.
	 * These edges have been declared in the <code>node</code>'s or one of its parent's <code>outgoingEdges</code>.
	 * <br /> <br/>
	 * If <code>includeNodeSuperTypes</code> has been set to true all of <code>node</code>'s parents are considered
	 * as well when collecting the result's <code>Edge</code>s.
	 * 
	 * @param	node	the {@link Node} for which the outgoing <code>Edge</codes>s should be returned
	 * @param	includeNodeSuperTypes	a <code>boolean</code> to state whether <code>node</code>'s parents should
	 * 			be considered as well
	 * @returns	a {@link Set} which contains all <code>Edge</code>s which can have the <code>node</code>
	 * 			as their source
	 * @see		#getOutgoingConnectingEdges(Node) getPossibleSources(Node)
	 */
	def static Set<Edge> getOutgoingConnectingEdges(Node node, boolean includeNodeSuperTypes) {
		val HashSet<Edge> result = new HashSet<Edge>()
		val outgoingEdgeConnections = node.outgoingEdgeConnections
		if(outgoingEdgeConnections.size == 1 && outgoingEdgeConnections.get(0).connectingEdges.empty) {
			result.addAll(node.mglModel.edges)
		} else {
			outgoingEdgeConnections.forEach[result.addAll(it.getConnectingEdges())]			
		}
		if(includeNodeSuperTypes) {
			node.allSuperTypes.map[(it as Node)].forEach[result += getOutgoingConnectingEdges]
		}
		return result
	}

	/**
	 * Returns all {@link Node Nodes} the <code>containingElement</code> or a parent type can contain directly.
	 * 
	 * @param	containingElement	the <code>containingElement</code> for which all containable nodes should be returned
	 * @return	a {@link Set} which contains all {@link Node Nodes} that are containable in the <code>containingElement</code>
	 * 			or any parent <code>containingElement</code>
	 * @see		#isContained(ContainingElement, Node)	isContained(ContainingElement, Node)
	 */
	def static Set<Node> getContainableNodes(ContainingElement containingElement) {
		return allMGLs.flatMap[nodes.filter[node | isContained(containingElement, node)]].toSet
	}
	
	/**
	 * Returns the {@link MGLModel} the <code>containingElement</code> is contained in.
	 * 
	 * @param	containingElement	the {@link ContainingElement} to retrieve the containing {@link MGLModel} for
	 * @return	the {@link MGLModel} the <code>containingElement</code> has been defined in
	 */
	def private static dispatch MGLModel getRootElement(ContainingElement containingElement){
		return containingElement.eContainer as MGLModel
	}
	
	/**
	 * Returns the {@link ContainingElement ContainingElements} within the same {@link MGLModel} the <code>node</code> can be contained in.
	 * The returned {@link ContainingElement ContainingElements} have declared the <code>node</code> in their <code>containableElements</code>.
	 * Extending types of the {@link ContainingElement ContainingElements} or the <code>node</code> are not considered.
	 * 
	 * @param	node	the {@link Node} for which the {@link ContainingElement ContainingElements} it can be contained in should be returned
	 * @return	a {@link Set} which contains all {@link ContainingElement ContainingElements} the <code>node</code> can be contained in, within
	 * 			the same {@link MGLModel}
	 */
	def static Set<ContainingElement> getPossibleContainers(Node node) {
		return node.rootElement.nodeContainers.filter[nodeContainer | nodeContainer.containableNodes.contains(node) && if(nodeContainer instanceof GraphModel) !nodeContainer.isAbstract else true].toSet
	}

	/**
	 * Returns the {@link MGLModel} the <code>containingElement</code> is contained in.
	 * 
	 * @param	type	the {@link Type} to retrieve the containing {@link MGLModel} for
	 * @return	the {@link MGLModel} the <code>type</code> has been defined in
	 */
	def private static dispatch MGLModel getRootElement(Type type) {
		type.eContainer as MGLModel
	}

	/**
	 * Returns if a {@link Node} is contained by the {@link ContainingElement}. The containment is computed
	 * by direct containment or a super type containment. 
	 * 
	 * @param	containingElement	the {@link ContainingElement} that should be checked whether it can contain the <code>node</code
	 * @param	node				the {@link Node} that should be checked whether is can be contained in the <code>containingElement</code>
	 * @return	a <code>boolean</code> that indicated whether <code>node</code> can be contained in <code>containingElement</code>
	 */
	def private static boolean isContained(ContainingElement containingElement, Node node) {
		if (containingElement instanceof GraphModel && containingElement.getContainableElements().isEmpty()) return true
		
		var Set<GraphicalElementContainment> containments = containingElement.getContainableElements().filter[ce |
			(ce.types.exists[equalModelElement(it, node)] || node.allSuperTypes.toSet.exists[st | ce.types.toSet.exists[equalModelElement(it, st)]]
			) && (ce.getUpperBound() != 0)
		].toSet
		
		var containedInSuperType = false
		switch (containingElement) {
			NodeContainer: containedInSuperType = containingElement.allSuperTypes.filter(NodeContainer).toSet.exists[isContained((it as NodeContainer), node)]
		}
		
		return containments.size() > 0 || containedInSuperType 
	}

	/** 
	 * This methods retrieves all images used in the MGL and Style specification.
	 * 
	 * @param gm the {@link GraphModel} which should be searched for images
	 * @param workspaceContext is the workspaceContext containing project-specific information
	 * @return a {@link HashMap} containing the defined path in the meta description as a {@link String} and the {@link URL} of the 
	 * actual image file.
	 */
	def static HashMap<String, URI> getAllImages(GraphModel gm, IWorkspaceContext workspaceContext) {
		var HashMap<String, URI> paths = new HashMap()
		var URI uri = null
		for (var TreeIterator<EObject> it = gm.eResource().getAllContents(); it.hasNext();) {
			var EObject o = it.next()
			if (o instanceof Annotation) {
				var Annotation a = (o as Annotation)
				if ("icon".equals(a.getName())) {
					if (a.getValue().size() === 1 && PathValidator::isRelativePath(a.getValue().get(0))) {
						uri = workspaceContext.getFileURI(a.getValue().get(0))
						paths.put(a.getValue().get(0), uri)
					} else if (a.getValue().size() > 1 && PathValidator::isRelativePath(a.getValue().get(1))) {
						uri = workspaceContext.getFileURI(a.getValue().get(1))
						paths.put(a.getValue().get(1), uri)
					}
				}
			}
			if (o instanceof GraphModel) {
				var String iconPath = ((o as GraphModel)).getIconPath()
				if(iconPath !== null && !iconPath.isEmpty()) {
					uri = workspaceContext.getFileURI(iconPath)
					paths.put(iconPath, uri)
				}
			}
		}
		var Styles styles = CincoUtil::getStyles(gm.eContainer as MGLModel, workspaceContext)
		for (var TreeIterator<EObject> it = styles.eResource().getAllContents(); it.hasNext();) {
			var EObject o = it.next()
			if (o instanceof Image) {
				var Image img = (o as Image)
				var String path = img.getPath()
				if (PathValidator::isRelativePath(path)) {
					uri = workspaceContext.getFileURI(path)
					paths.put(path, uri)
				}
			}
		}
		return paths
	}

	/**
	 * Returns the {@link Annotation} with the name <code>annotationName</code> if it is used for the
	 *  <code>modelElement</code>.
	 * Returns <code>null</code> if no {@link Annotation} with the <code>annotationName</code> is present
	 * at <code>modelElement</code>.
	 * 
	 * @param	modelElement	the {@link ModelElement} which will be searched for the {@link Annotation}
	 * @param	annotationName	the name of the {@link Annotation} that should be searched
	 * @return	the {@link Annotation} with the name <code>annotationName</code> present at the <code>modelElement</code>
	 * 			or <code>null</code> if no such {@link Annotation} is present
	 */
	def static getAnnotation(ModelElement modelElement, String annotationName) {
		return modelElement.annotations.filter[name == annotationName]?.head
	}
	
	/**
	 * Returns the {@link MGLModel} the <code>modelElement</code> has been defined in.
	 * 
	 * @param	modelElement the {@link ModelElement} for which the {@link MGLModel} it has been defined in should be returned
	 * @return	the {@link MGLModel} the <code>modelElement</code> has been defined in
	 */
	def static getMglModel(ModelElement modelElement) {
		return (modelElement.eContainer as MGLModel)
	}

	/** 
	 * This methods returns all values of the {@link Annotation Annotations} with the given <code>annotationName</code>
	 * present at the provided <code>modelElement</code>.
	 * 
	 * @param annotationName	the name of the {@link Annotation Annotations} which's values should be returned
	 * @param modelElement		the {@link ModelElement} which should be searched for the {@link Annotation Annotations}
	 * @return	a {@link List} which contains all values of {@link Annotation Annotations} with the provided <code>annotationName</code>
	 * 			present at the <code>modelElement</code> as a {@link String}
	 */
	def static List<String> getAllAnnotation(String annotationName, ModelElement modelElement) {
		var List<String> values = modelElement.getAnnotations().stream()
			.filter([annotation|annotation.getName().equals(annotationName)])
			.map([annotation | annotation.getValue().get(0)]).collect(Collectors::toList())
		return values
	}
	
	/**
	 * Returns the name of the provided <code>attribute</code>'s type as a {@link String}.
	 * 
	 * @param	attribute	the {@link Attribute} of which the type's name should be returned
	 * @return	a {@link String} representing the name of <code>attribute</code>'s type
	 */
	def static getType(Attribute attribute) {
	 	switch attribute {
	 		ComplexAttribute :  return attribute.type.name 
	 		PrimitiveAttribute : return attribute.type.getName
		}
	}
	
	/**
	 * Returns all parents of the provided <code>modelElement</code> of any depth.
	 * 
	 * @param	modelElement	the {@link ModelElement} for which all parents should be returned
	 * @return	an {@link ArrayList} which contains all parent {@link ModelElement} types of the provided
	 * 			<code>modelElement</code> of any depth
	 */
	def static ArrayList<? extends ModelElement> allSuperTypes(ModelElement modelElement) {
		val superTypes = new ArrayList<ModelElement>
		var current = modelElement.extend
		while (current.extend !== null || current != current.extend) {
			superTypes += current
			current = current.extend
		}

		return superTypes
	}
	
	/**
	 * Returns all subclasses of all <code>modelElements</code> defined within the provided <code>mglModel</code>.
	 * The result does not contain the elements of the <code>modelElements</code> parameter.
	 * Only {@link ModelElement ModelElements} defined in the provided <code>mglModel</code> will be considered.
	 * <br />
	 * Inheritances of any depth are considered.
	 * 
	 * @param mglModel 			the {@link MGLModel} to do the check in
	 * @param modelElements	the {@link ModelElement ModelElements} to find the subclasses for
	 * @return	a {@link Set} which contains all {@link ModelElement ModelElements} that have any of the provided
	 * 			<code>modelElements</code> as their super type and are defined in the provided <code>mglModel</code>
	 */
	def static Set<ModelElement> getAllSubclasses(MGLModel mglModel, Set<ModelElement> modelElements) {
		val elementsToCheck = mglModel.modelElements.filter[!modelElements.contains(it)].toSet
		val graphModelsToCheck = elementsToCheck.filter(GraphModel).toSet
		val nodesToCheck = elementsToCheck.filter(Node).toSet
		val edgesToCheck = elementsToCheck.filter(Edge).toSet
		val result = new HashSet<ModelElement>()
		for(modelElement : modelElements) {
			switch it : modelElement {
				case it instanceof GraphModel:
					for(gm : graphModelsToCheck) {
						if((modelElement as GraphModel).isSuperGraphModelOf(gm)) {
							result.add(gm)
						}
					}
				case it instanceof Node:
					for(node : nodesToCheck) {
						if((modelElement as Node).isSuperNodeOf(node)) {
							result.add(node)
						}
					}
				case it instanceof Edge:
					for(edge : edgesToCheck) {
						if((modelElement as Edge).isSuperEdgeOf(edge)) {
							result.add(edge)
						}
					}
			}
		}
		return result
	}
	
	/**
	 * Returns all {@link ModelElement ModelElements} that extend the provided <code>modelElement</code>.
	 * This methods considers extension relationships of any depth.
	 * 
	 * @param	modelElement	the {@link ModelElement} for which all extending {@link ModelElement ModelElements}
	 * 							should be returned
	 * @return	a {@link Set} which contains every {@link ModelElement} that extends the provided <code>modelElement</code>
	 * 			in any depth
	 */
	def static getAllSubclasses(ModelElement modelElement) {
		return allMGLs.flatMap[
					modelElements.filter[ sub |
						sub.allSuperTypes.exists[subSuper | MGLUtil.equalModelElement(subSuper, modelElement)]
					]
				].toSet
	}
	
	/**
	 * Returns a {@link String} surrounding the provided <code>string</code> with "${" and "}".
	 * If the provided <code>node</code> is also a prime reference and the <code>string</code>
	 * starts with the prime reference's name then the first occurrence of "\." in
	 * the provided <code>string</code> will also be replaced by ".internalElement.".
	 * 
	 * @param	node	the {@link Node} which will be checked if it is a prime reference
	 * @param	string	the {@link String} which will be embedded and possibly partially replaced
	 * @return	a {@link String} that contains the newly embedded string as described above
	 */
	def static refactorIfPrimeAttribute(Node node, String string) {
		if (node.isPrime && (node.retrievePrimeReference instanceof ReferencedModelElement) 
			&& string.startsWith(node.retrievePrimeReference.name) ) {
			return "${" + string.toFirstLower.replaceFirst("\\.", ".internalElement.") + "}"
		} else {
			return "${" + string + "}"	
		}
	}
	
	/**
	 * Returns the {@link ReferencedType} of the prime reference if <code>node</code>
	 * or any of its parents is a prime reference.
	 * The first discovered prime reference will be returned.
	 * <br />
	 * Returns null if no prime reference could be found.
	 * <br />
	 * This methods simply calls {@link #retrievePrimeReference(Node, boolean) retrievePrimeReference(Node, boolean)}
	 * with the <code>includeParents</code> flag set to true.
	 * 
	 * @param	node			the {@link Node} which should be checked for prime references
	 * @return	the {@link ReferencedType} of the <code>node</code>'s prime reference or the first discovered
	 * 			prime reference of an extended type
	 * @see	#retrievePrimeReference(Node, boolean) retrievePrimeReference(Node, boolean)
	 */
	def static ReferencedType retrievePrimeReference(Node n) {
		return retrievePrimeReference(n, true)
	}
	
	/**
	 * Returns the {@link ReferencedType} of the prime reference if <code>node</code> is a prime reference.
	 * If <code>includeParents</code> is set to <code>true</code> any extended {@link Node} of any depth will
	 * be searched for a prime reference as well. If any parent is a prime reference the first discovered 
	 * {@link ReferencedType} will be returned.
	 * <br />
	 * Returns null if no prime reference could be found.
	 * 
	 * @param	node			the {@link Node} which should be checked for prime references
	 * @param	includeParents	a flag whether {@link Node Nodes} that <code>node</code> extends should be
	 * 							searched for prime references as well
	 * @return	the {@link ReferencedType} of the <code>node</code>'s prime reference or the first discovered
	 * 			prime reference of an extended type if <code>includeParents</code> is set to <code>true</code>
	 */
	def static ReferencedType retrievePrimeReference(Node node, boolean includeParents) {
		if (node.primeReference !== null) {
			return node.primeReference
		} else if(includeParents) {
			return node.extends?.retrievePrimeReference(includeParents)
		} else {
			return null
		}
	}
	
	/**
	 * Returns {@link String Strings} for methods of all post create hooks used in the <code>mglModel</code>.
	 * The {@link String} for each post create hook is created by {@link #postCreates(Iterable) postCreates(Iterable)}
	 * <br />
	 * Also includes all post create hooks of all imported MGLs of any depth in the <code>mglModel</code>.
	 * 
	 * @param	mglModel	the {@link MGLModel} for which all post create hooks should be returned
	 * @return	a {@link String} which represents methods for every post create hook used in the <code>mglModel</code>
	 * 			and any of it imported MGLs of any depth
	 * @see		#postCreates(Iterable) postCreates(Iterable)
	 */
	def static String getPostCreateHooks(MGLModel mglModel) {
		mglModel.getPostCreateHooksRecursive(newHashSet(#{mglModel})).join
	}
	
	private def static List<CharSequence> getPostCreateHooksRecursive(MGLModel mglModel, Set<MGLModel> alreadyVisited) {
		var List<CharSequence> postCreateHooks = newLinkedList
		postCreateHooks.addAll(mglModel.types.filter(ModelElement).map[postCreates])
		postCreateHooks.addAll(mglModel.modelElements.map[postCreates])
		for (model: mglModel.getAllImportedMGLs(true, false)) {
			if (!alreadyVisited.exists[equalMGLModels(model)]) {
				alreadyVisited.add(model)
				postCreateHooks.addAll(model.getPostCreateHooksRecursive(alreadyVisited))
			}
		}
		return postCreateHooks
	}
	
	/**
	 * Returns true when the provided <code>modelElement</code> possesses at least one postCreate annotation.
	 * 
	 * @param	modelElement	the {@link ModelElement} which should be checked for postCreate annotations
	 * @return	<code>true</code> if the <code>modelElement</code> possesses any postCreate annotations, <code>false</code> otherwise
	 */
	def static hasPostCreateHook(ModelElement modelElement) {
		modelElement.annotations.exists[name == 'postCreate'] ||
		(modelElement.isEventEnabled && EventEnum.POST_CREATE.accepts(modelElement))
	}
	
	private def static postCreates(Type type) {
		val element = type as ModelElement
		if (element.hasPostCreateHook)  '''
			
			def postCreates(«element.fqBeanNameEscaped» me) {
				me.transact [
					«element.generatePostCreateCalls»
					«EventEnum.POST_CREATE.getNotifyCallXtend(element, 'me')»
				]
			}
		'''
		else {
			''
		}
	}
	
	private def static generatePostCreateCalls(ModelElement it) '''
		«FOR annotation: annotations.filter[name == 'postCreate']»
			new «annotation.value.head»().postCreate(me)
		«ENDFOR»
	'''
	
	/**
	 * Returns a {@link String} that calls the <code>modelElement</code>'s postCreate hooks, if at least one exists.
	 * If no postCreate hook exists for the <code>modelElement</code> <code>null</code> will be returned.
	 * 
	 * @param	modelElement	the {@link ModelElement} for which the postCreate hooks call should be returned
	 * @return	a {@link String} which calls the postCreate hooks of the model element, if at least one postCreate hook
	 * 			is present at the <code>modelElement</code>. Returns <code>null</code> otherwise 
	 */
	def static postCreateHook(ModelElement modelElement) {
		if (modelElement.hasPostCreateHook)
			'''if (hook) postCreates'''
	}
	
	/**
	 * Returns {@link String Strings} which calls handlers for each postAttributeChange annotation of the <code>modelElement</code>.
	 * If no postAttributeChange annotations are present <code>null</code> is returned.
	 * The <code>varName</code> will be used to access the element.
	 * 
	 * @param	modelElement	the {@link ModelElement} for which the postAttributeChange hooks should be created
	 * @param	varName			a {@link String} which represents the variable on which the attribute will be accessed
	 * @return	a {@link String} which contains handlers for all postAttributeChange hooks of the provided <code>modelElement</code>
	 */
	def static postAttributeValueChange(ModelElement modelElement, String varName) {
		modelElement.annotations.filter[name == "postAttributeChange"].map[generatePostAttributeValueChange(varName)].join("\n")
	}
	
	private def static generatePostAttributeValueChange(Annotation it, String varName) '''
		if (new «value.get(0)»().canHandleChange(«varName».element as «parent.fqBeanName», feature))
			new «value.get(0)»().handleChange(«varName».element as «parent.fqBeanName», feature)
	'''
	
	/**
	 * Returns all {@link Attribute Attributes} present in the provided <code>modelElement</code> or any of its parents.
	 * 
	 * @param	modelElement	the {@link ModelElement} for which all {@link Attribute Attributes} of itself and all of it
	 * 							parents should be returned
	 * @return	an {@link Iterable} which contains all {@link Attribute Attributes} that are contained in the provided
	 * 			 <code>modelElement</code> or any of its parents
	 */
	def static Iterable<? extends Attribute> allAttributes(ModelElement modelElement){
		return allAttributes(modelElement, true)
	}
	
	/**
	 * Returns all {@link Attribute Attributes} present in the provided <code>modelElement</code>.
	 * If <code>includeParentElements</code> is true all attributes of <code>modelElement</code>'s parents are also included.
	 * 
	 * @param	modelElement			the {@link ModelElement} for which all {@link Attribute Attributes} should be returned
	 * @param	includeParentElements	a flag whether all parents of any depth of <code>modelElement</code> should be considered as well
	 * 									 when collecting the attributes
	 * @return	an {@link Iterable} which contains all {@link Attribute Attributes} that are contained in the provided
	 * 			 <code>modelElement</code> or any of its parents if <code>includeParentElements</code> is <code>true</code>
	 */
	def static Iterable<? extends Attribute> allAttributes(ModelElement modelElement, boolean includeParentElements){
		val allAttributes = new HashMap<String,Attribute>
		val mes = new Stack<ModelElement>()
		if(includeParentElements) {
			mes += modelElement.allSuperTypes.topSort
		}
		mes += modelElement
		mes.forEach[attributes.forEach[allAttributes.put(name,it)]]
		allAttributes.values
	}

	/**
	 * Returns all {@link Attribute Attributes} of the provided <code>modelElement</code> which are either a <strong>not</strong> a
	 * {@link ComplexAttribute} or are a {@link ComplexAttribute} which is not overridden with the same name in any of <code>modelElement</code>'s
	 * sub types.
	 * 
	 * @param	modelElement	the {@link ModelElement} for which the non-conflicting attribute should be returned for
	 * @return	an {@link Iterable} which contains all {@link Attribute Attributes} which are either no {@link ComplexAttribute} or are
	 * 			{@link ComplexAttribute ComplexAttributes} which are not overridden in any of <code>modelElemenet</code>'s sub types.
	 */
	def static Iterable<?extends Attribute> nonConflictingAttributes(ModelElement modelElement){
		modelElement.allAttributes(false).filter [attr|
			!(attr instanceof ComplexAttribute) || !(modelElement.subTypes.map[st|st.allAttributes].flatten.exists [e|
				e.name == attr.name && (e as ComplexAttribute).override
			])
		]
	}
	
	/**
	 * Return whether <code>subTypeCandidate</code> is in fact a subtype of <code>superTypeCandidate</code>.
	 * A model element is considered a subtype of another if any of it parents' types matches the type of the other model element.
	 * 
	 * @param	superTypeCandidate	the {@link ModelElement} to check for the potential supertype (parent of any depth)
	 * @param	subTypeCandidate	the {@link ModelElement} to check for the potential subtype (child of any depth)
	 * @return <code>true</code> if any of <code>subTypeCandidate</code>'s parents of any depth are of the same type as <code>superTypeCandidate</code>;
	 * 			<code>false</code> otherwise
	 */
	def static boolean isSubType(ModelElement superTypeCandidate, ModelElement subTypeCandidate) {
		var currentSubTypeCandidate = subTypeCandidate
		while(currentSubTypeCandidate !== null) {
			if(equalModelElement(currentSubTypeCandidate, superTypeCandidate)) {
				return true
			} else {
				currentSubTypeCandidate = currentSubTypeCandidate.extend
			}
		}
		return false
	}
	
	/**
	 * Returns all {@link ModelElement ModelElements} for which the provided <code>modelElement</code> is a parent.
	 * Only {@link ModelElemenet ModelElements} defined within the same MGL file are considered.
	 * The parent relationship is considered in any depth.
	 * 
	 * @param	modelElement	the {@link ModelElement} for which the sub types should be returned
	 * @return	an {@link Iterable} which contains all {@link ModelElement ModelElements} within the same MGL file for which
	 * 			<code>modelElement</code> is a parent of any depth
	 */
	def static Iterable<?extends ModelElement> subTypes(ModelElement modelElement){
		modelElement.mglModel.modelElements.filter[me|me.allSuperTypes.exists[e | e == modelElement]]
	}
	
	/**
	 * Returns a {@link Stack} which contains the provided <code>elements</code> in topologically sorted order.
	 * Elements without dependencies on other elements are placed first the in resulting {@link Stack}.
	 * The elements with the most dependencies are located last in the returned {@link Stack}.
	 * 
	 * @param	elements	an {@link Iterable} which contains all {@link ModelElement ModelElements} which should be sorted topologically
	 * @return	a {@link Stack} which holds the provided {@link ModelElement ModelElements} in topological order 
	 */
	def static topSort(Iterable<? extends ModelElement> elements) {
		new DependencyGraph<ModelElement>().createGraph(elements.map[dependencies]).
			topSort
	}
	
	
	private def static DependencyNode<ModelElement> dependencies(ModelElement it) {
		val dNode = new DependencyNode<ModelElement>(it)
		dNode.addDependencies(allSuperTypes.map[t|t].toList)
		dNode
	}
	
	/**
	 * Returns a {@link Stack} which contains the provided <code>mglModels</code> in topologically sorted order.
	 * MGLModels without dependencies on other elements are placed first the in resulting {@link Stack}.
	 * The MGLModels with the most dependencies are located last in the returned {@link Stack}.
	 * 
	 * @param	mglModels	an {@link Iterable} which contains all {@link MGLModel MGLModels} which should be sorted topologically
	 * @return	a {@link Stack} which holds the provided {@link MGLModel MGLModels} in topological order 
	 */
	def static topSortMGLModels(Iterable<MGLModel> mglModels) {
		new DependencyGraph<MGLModel>().createGraph(mglModels.map[dependenciesMGLModel]).
			topSort
	}
	
	/**
	 * Returns whether the provided <code>type1</code> and <code>type2</code> are equal.
	 * If they are of type {@link Node}, {@link Edge}, {@link GraphModel}, or {@link MGLModel} custom equal methods are used.
	 * Otherwise the default equals method will be used.
	 * 
	 * @param	type1	the first element to check the equality for
	 * @param	type2	the second element to check the equality for
	 * @return	a boolean whether the two provided elements are equal
	 * @see		#equalNodes(Node, Node) equalNodes(Node, Node)
	 * @see		#equalEdges(Edge, Edge) equalEdges(Edge, Edge)
	 * @see		#equalGraphModels(GraphModel, GraphModel) equalGraphModels(GraphModel, GraphModel)
	 * @see		#equalMGLModels(MGLModel, MGLModel) equalMGLModels(MGLModel, MGLModel)
	 */
	def static equalModelElement(Type type1, Type type2) {
		if(type1 instanceof Node && type2 instanceof Node) {
			return equalNodes(type1 as Node, type2 as Node)
		} else if(type1 instanceof Edge && type2 instanceof Edge) {
			return equalEdges(type1 as Edge, type2 as Edge)
		} else if(type1 instanceof GraphModel && type2 instanceof GraphModel) {
			return equalGraphModels(type1 as GraphModel, type2 as GraphModel)
		} else if(type1 instanceof MGLModel && type2 instanceof MGLModel) {
			return equalMGLModels(type1 as MGLModel, type2 as MGLModel)
		} else if(type1 instanceof UserDefinedType && type2 instanceof UserDefinedType) {
			return equalUserDefinedTypes(type1 as UserDefinedType, type2 as UserDefinedType)
		} else {
			return type1 == type2
		}
	}
	
	/**
	 * Returns whether the provided <code>node1</code> and <code>node2</code> are equal.
	 * The equality is checked by comparing the nodes' names and whether they are contained
	 * in the same resource.
	 * 
	 * @param	node1	the first {@link Node} to check the equality for
	 * @param	node2	the second {@link Node} to check the equality for
	 * @return	a boolean whether the two provided {@link Node Nodes} are equal
	 * @see		#equalModelElement(Type, Type) equalModelElement(Type, Type)
	 * @see		#equalEdges(Edge, Edge) equalEdges(Edge, Edge)
	 * @see		#equalGraphModels(GraphModel, GraphModel) equalGraphModels(GraphModel, GraphModel)
	 * @see		#equalMGLModels(MGLModel, MGLModel) equalMGLModels(MGLModel, MGLModel)
	 */
	def static equalNodes(Node node1, Node node2) {
		if(node1 !== null && node2 !== null) {
			if(node1.name !== null &&
				node1.name == node2.name) {
				if(node1.eContainer instanceof MGLModel && 
				node2.eContainer instanceof MGLModel) {
					return equalMGLModels(node1.eContainer as MGLModel, node2.eContainer as MGLModel)	
				} else {
					return node1.eContainer == node2.eContainer
				}
			} else {
				return false
			}
		} else {
			return node1 === node2
		}
	}
	
	/**
	 * Returns whether the provided <code>type1</code> and <code>type2</code> are equal.
	 * The equality is checked by comparing the types' names and whether they are contained
	 * in the same resource.
	 * 
	 * @param	type1	the first {@link Type} to check the equality for
	 * @param	type2	the second {@link Type} to check the equality for
	 * @return	a boolean whether the two provided {@link Type Types} are equal
	 * @see		#equalModelElement(Type, Type) equalModelElement(Type, Type)
	 * @see		#equalEdges(Edge, Edge) equalEdges(Edge, Edge)
	 * @see		#equalNodes(Node, Node) equalNodes(Node, Node)
	 * @see		#equalGraphModels(GraphModel, GraphModel) equalGraphModels(GraphModel, GraphModel)
	 * @see		#equalMGLModels(MGLModel, MGLModel) equalMGLModels(MGLModel, MGLModel)
	 */
	def static equalTypes(Type type1, Type type2) {
		if(type1 !== null && type2 !== null) {
			if(type1.name !== null &&
				type1.name == type2.name) {
				if(type1.eContainer instanceof MGLModel && 
				type1.eContainer instanceof MGLModel) {
					return equalMGLModels(type1.eContainer as MGLModel, type2.eContainer as MGLModel)	
				} else {
					return type1.eContainer == type2.eContainer
				}
			} else {
				return false
			}
		} else {
			return type1 === type2
		}
	}
	
	/**
	 * Returns whether the provided <code>graphModel1</code> and <code>graphModel2</code> are equal.
	 * The equality is checked by comparing the graph models' names and whether they are contained
	 * in the same resource.
	 * 
	 * @param	graphModel1	the first {@link GraphModel} to check the equality for
	 * @param	graphModel2	the second {@link GraphModel} to check the equality for
	 * @return	a boolean whether the two provided {@link GraphModel GraphModels} are equal
	 * @see		#equalModelElement(Type, Type) equalModelElement(Type, Type)
	 * @see		#equalEdges(Edge, Edge) equalEdges(Edge, Edge)
	 * @see		#equalNodes(Node, Node) equalNodes(Node, Node)
	 * @see		#equalMGLModels(MGLModel, MGLModel) equalMGLModels(MGLModel, MGLModel)
	 */
	def static equalGraphModels(GraphModel graphModel1, GraphModel graphModel2) {
		if(graphModel1 !== null && graphModel2 !== null) {
			if(graphModel1.name !== null &&
				graphModel1.name == graphModel2.name) {
				if(graphModel1.eContainer instanceof MGLModel && 
				graphModel1.eContainer instanceof MGLModel) {
					return equalMGLModels(graphModel1.eContainer as MGLModel, graphModel2.eContainer as MGLModel)	
				} else {
					return graphModel1.eContainer == graphModel2.eContainer
				}
			} else {
				return false
			}
		} else {
			return graphModel1 === graphModel2
		}
	}
	
	/**
	 * Returns whether the provided <code>edge1</code> and <code>edge2</code> are equal.
	 * The equality is checked by comparing the edges' names and whether they are contained
	 * in the same resource.
	 * 
	 * @param	edge1	the first {@link Edge} to check the equality for
	 * @param	edge2	the second {@link Edge} to check the equality for
	 * @return	a boolean whether the two provided {@link Edge Edges} are equal
	 * @see		#equalModelElement(Type, Type) equalModelElement(Type, Type)
	 * @see		#equalNodes(Node, Node) equalNodes(Node, Node)
	 * @see		#equalGraphModels(GraphModel, GraphModel) equalGraphModels(GraphModel, GraphModel)
	 * @see		#equalMGLModels(MGLModel, MGLModel) equalMGLModels(MGLModel, MGLModel)
	 */
	def static boolean equalEdges(Edge edge1, Edge edge2) {
		if(edge1 !== null && edge2 !== null) {
			if(edge1.name !== null &&
				edge1.name == edge2.name) {
				if(edge1.eContainer instanceof MGLModel && 
				edge2.eContainer instanceof MGLModel) {
					return equalMGLModels(edge1.eContainer as MGLModel, edge2.eContainer as MGLModel)	
				} else {
					return edge1.eContainer == edge2.eContainer
				}
			} else {
				return false
			}
		} else {
			return edge1 === edge2
		}
	}
	
	/**
	 * Returns whether the provided <code>mglModel1</code> and <code>mglModel1</code> are equal.
	 * The equality is checked by comparing the types' names and packages.
	 * 
	 * @param	mglModel1	the first {@link MGLModel} to check the equality for
	 * @param	mglModel2	the second {@link MGLModel} to check the equality for
	 * @return	a boolean whether the two provided {@link MGLModel MGLModels} are equal
	 * @see		#equalModelElement(Type, Type) equalModelElement(Type, Type)
	 * @see		#equalEdges(Edge, Edge) equalEdges(Edge, Edge)
	 * @see		#equalNodes(Node, Node) equalNodes(Node, Node)
	 * @see		#equalGraphModels(GraphModel, GraphModel) equalGraphModels(GraphModel, GraphModel)
	 */
	def static equalMGLModels(MGLModel mglModel1, MGLModel mglModel2) {
		if(mglModel1 !== null && mglModel2 !== null) {
			return(
				mglModel1.fileName !== null &&
				mglModel1.fileName == mglModel2.fileName &&
				mglModel1.package !== null &&
				mglModel1.package == mglModel2.package
			)
		} else {
			return mglModel1 === mglModel2
		}
	}
	
	/**
	 * Returns whether the provided <code>userDefinedType1</code> and <code>userDefinedType2</code> are equal.
	 * The equality is checked by comparing the types' names and whether they are contained
	 * in the same resource.
	 * 
	 * @param	userDefinedType1	the first {@link Type} to check the equality for
	 * @param	userDefinedType2	the second {@link Type} to check the equality for
	 * @return	a boolean whether the two provided {@link Type Types} are equal
	 * @see		#equalModelElement(Type, Type) equalModelElement(Type, Type)
	 * @see		#equalEdges(Edge, Edge) equalEdges(Edge, Edge)
	 * @see		#equalNodes(Node, Node) equalNodes(Node, Node)
	 * @see		#equalGraphModels(GraphModel, GraphModel) equalGraphModels(GraphModel, GraphModel)
	 * @see		#equalMGLModels(MGLModel, MGLModel) equalMGLModels(MGLModel, MGLModel)
	 */
	def static equalUserDefinedTypes(UserDefinedType userDefinedType1, UserDefinedType userDefinedType2) {
		if(userDefinedType1 !== null && userDefinedType2 !== null) {
			if(userDefinedType1.name !== null &&
				userDefinedType1.name == userDefinedType2.name) {
				if(userDefinedType1.eContainer instanceof MGLModel && 
				userDefinedType2.eContainer instanceof MGLModel) {
					return equalMGLModels(userDefinedType1.eContainer as MGLModel, userDefinedType2.eContainer as MGLModel)	
				} else {
					return userDefinedType1.eContainer == userDefinedType2.eContainer
				}
			} else {
				return false
			}
		} else {
			return userDefinedType1 === userDefinedType2
		}
	}
	
	private def static DependencyNode<MGLModel> dependenciesMGLModel(MGLModel it) {
		return dependenciesMGLModel(it, true, true);
	}
	
	private def static DependencyNode<MGLModel> dependenciesMGLModel(MGLModel it, boolean ignoreStealthImports, boolean ignoreExternalImports) {
		val dNode = new DependencyNode<MGLModel>(it)
		dNode.addDependencies(getAllImportedMGLs(ignoreStealthImports, ignoreExternalImports))
		dNode
	}
	
	/**
	 * Returns all {@link MGLModel MGLModels} the provided <code>mglModel</code> possesses references for in its import statements.
	 * Stealth imports are ignored and not contained in the returned {@link Set}.
	 * 
	 * @param	mglModel	the {@link MGLModel} in which the lookup should be made
	 * @return	a {@link Set} that contains all imported {@link MGLModel MGLModels}
	 * @see		#getAllImportedMGLs(MGLModel, boolean)	getAllImportedMGLs(MGLModel, boolean)
	 */
	def static Set<MGLModel> getAllImportedMGLs(MGLModel mglModel) {
		return getAllImportedMGLs(mglModel, true, true);
	}
	
	/**
	 * Returns all {@link MGLModel MGLModels} the provided <code>mglModel</code> possesses references for in its import statements.
	 * If <code>ignoreStealthImports</code> is true stealth imported {@link MGLModel MGLModels} are not contained in the returned {@link Set}.
	 * If <code>ignoreExternalImports</code> is true imported external {@link MGLModel MGLModels} are not contained in the returned {@link Set}.
	 * 
	 * @param	mglModel				the {@link MGLModel} in which the lookup should be made
	 * @param	ignoreStealthImports	a flag whether stealth imported {@link MGLModel MGLModels} should be ignored when collecting the {@link MGLModel MGLModels}
	 * @param	ignoreExternalImports	a flag whether imported external {@link MGLModel MGLModels} should be ignored when collecting the {@link MGLModel MGLModels}.
	 * 									Imports are considered external if the <code>external</code> keyword has been set
	 * @return	a {@link Set} that contains all imported {@link MGLModel MGLModels}
	 * @see		#getAllImportedMGLs(MGLModel)	getAllImportedMGLs(MGLModel)
	 */
	def static Set<MGLModel> getAllImportedMGLs(MGLModel mglModel, boolean ignoreStealthImports, boolean ignoreExternalImports) {
		val resultList = new HashSet<MGLModel>()
		if(mglModel.imports !== null) {
			for(import : mglModel.imports) {
				if((!ignoreStealthImports || !import.isStealth) && (!ignoreExternalImports || !import.isExternal) && import.mglModel !== null) {
					_getAllImportedMGLs(import.mglModel, resultList, mglModel, ignoreStealthImports, ignoreExternalImports)
				}
			}
		}
		return resultList
	}
	
	private def static Set<MGLModel> _getAllImportedMGLs(MGLModel it, Set<MGLModel> resultList, MGLModel originalModel, boolean ignoreStealthImports, boolean ignoreExternalImports) {
		if(!resultList.exists[item | item.equalMGLModels(it)] && !it.equalMGLModels(originalModel)) {
			resultList.add(it)
			if(it.imports !== null) {
				for(import : it.imports) {
					if((!ignoreStealthImports || !import.isStealth) && (!ignoreExternalImports || !import.isExternal) && import.mglModel !== null && !import.isExternal) {
						_getAllImportedMGLs(import.mglModel, resultList, originalModel, ignoreStealthImports, ignoreExternalImports)
					}
				}
			}
		}
		return resultList
	}
	
	/**
	 * Returns a new list that contains the same elements as the provided <code>modelElements</code> but without duplicates.
	 * 
	 * Duplicate model elements are identified using {@link #equalModelElement(Type, Type)}.
	 * 
	 * @param	modelElements	an {@link Iterable} containing {@link ModelElement ModelElements} that should be duplicated withoud duplicates
	 * @returns a {@link Set} that contains all provided <code>modelElements</code> but without duplicates
	 */
	static def Set<? extends ModelElement> removeDuplicateModelElements(Iterable<? extends ModelElement> modelElements) {
		val result = new HashSet<ModelElement>
		modelElements.forEach[modelElement |
			if(!result.exists[equalModelElement(modelElement)]) {
				result.add(modelElement)
			}
		]
		return result
	}
	
	/**
	 * Returns all {@link Node Nodes} the provided <code>containingElement</code> can contain.
	 * 
	 * @param	containingElement	the {@link ContainingElement} for which the containable {@link Node Nodes} should be returned
	 * @return	an {@link Iterable} which contains all {@link Node Nodes} types the provided <code>containingElement</code> can contain
	 * @see	#containableContainerElements(ContainingElement)	containableContainerElements(ContainingElement)
	 */
	static def containableNodeElements(ContainingElement containingElement){
		containingElement.containableElements.map[types].flatten.filter(Node)
	}
	 
	/**
	 * Returns all {@link NodeContainer NodeContainers} the provided <code>containingElement</code> can contain.
	 * 
	 * @param	containingElement	the {@link ContainingElement} for which the containable {@link Node Nodes} should be returned
	 * @return	an {@link Iterable} which contains all {@link NodeContainer NodeContainers} types the provided <code>containingElement</code> can contain
	 * @see	#containableNodeElements(ContainingElement)	containableNodeElements(ContainingElement)
	 */
	static def containableContainerElements(ContainingElement containingElement){
		containingElement.containableElements.map[types].flatten.filter(NodeContainer)
	}
	
	/**
	 * Returns the {@link ModelElement} the provided <code>element</code> extends.
	 * If it doesn't extend anything <code>null</code> is returned instead.
	 * 
	 * @param	element	the {@link ModelElement} for which the extended {@link ModelElement} should be returned
	 * @return	the {@link ModelElement} <code>element</code>'s extend or <code>null</code> if it doesn't extend anything
	 */	 
	def static ModelElement extend(ModelElement element) {
		switch element {
			Node : element.extends
			Edge : element.extends
			UserDefinedType : element.extends
			GraphModel : element.extends
			default: null
		}
	}
	
	dispatch def static MGLModel mglModel(Import imprt, IWorkspaceContext workspaceContext) {
		CincoUtil.getImportedMGLModel(imprt, workspaceContext)
	}
	
	dispatch def static MGLModel mglModel(EObject object, IWorkspaceContext workspaceContext) {
		object.mglModel
	}
	
	dispatch def static MGLModel mglModel(MGLModel mglModel){
		mglModel
	}
	
	dispatch def static MGLModel mglModel(Type type){
		type.eContainer as MGLModel
	}
	
	dispatch def static MGLModel mglModel(ContainingElement containingElement){
		switch(containingElement){
			case GraphModel: containingElement.mglModel
			case NodeContainer: containingElement.mglModel
		}
	}
	
	dispatch def static MGLModel mglModel(Wildcard wildcard) {
		if(wildcard.selfWildcard) {
			return wildcard.eContainer.mglModel
		} else {
			return wildcard.referencedImport?.mglModel
		}
	}
	
	dispatch def static MGLModel mglModel(EObject eObject) {
		val alreadyVisited = #[eObject]
		var candidate = eObject
		while(candidate !== null) {
			if(candidate instanceof MGLModel) {
				return candidate
			}
			candidate = candidate.eContainer
			if(alreadyVisited.contains(candidate)) {
				return null
			}
		}
		return null
	}
	
	dispatch def static MGLModel mglModel(Object o){
		throw new RuntimeException(String.format("Can not determine MGLModel for Object: %s",o))
	}

	/**
	 * Returns the provided <code>mglModel</code>'s file name in lowercase.
	 * This can be used as a name for the ecore packages.
	 * 
	 * @param	mglModel	the {@link MGLModel} for which the name should be returned
	 * @return	a {@link String} which represents the provided <code>mglModel</code>'s file name in lowercase
	 */
	def static ePackageName(MGLModel mglModel){
		mglModel.fileName.toLowerCase
	}
	
	/**
	 * Returns an namespace URI for the provided <code>model</code>.
	 * The URI consists of the first three segments of the <code>model</code>'s <code>package</code> property in reversed order, separated with ".".
	 * All following segments are then concatenated after the first three segments and separated by "/". Their order remains unaltered.
	 * Of course, the whole URI starts with "http://"
	 * <br />
	 * <br />
	 * Example:<br />
	 * <code>package</code> "info.scce.cinco.some.example"<br />
	 * <strong>result</strong> "http://cinco.scce.info/some/example"
	 * 
	 * @param	model	the {@link MGLModel} for which the namespace URI should be returned
	 * @return	a {@link String} representing the nsURI for the <code>model</code> of the above described format
	 */
	def dispatch static String nsURI(MGLModel model){
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
	
	/**
	 * Returns an namespace URI for the provided <code>graphModel</code>'s containing {@link MGLModel}.
	 * The URI consists of the first three segments of the <code>graphModel</code>'s containing {@link MGLModel}'s <code>package</code> property in reversed order, separated with ".".
	 * All following segments are then concatenated after the first three segments and separated by "/". Their order remains unaltered.
	 * Of course, the whole URI starts with "http://"
	 * <br />
	 * <br />
	 * Example:<br />
	 * <code>package</code> "info.scce.cinco.some.example"<br />
	 * <strong>result</strong> "http://cinco.scce.info/some/example"
	 * 
	 * @param	graphModel	the {@link GraphModel} for which's containing {@link MGLModel} the namespace URI should be returned
	 * @return	a {@link String} representing the nsURI for the <code>graphModel</code>'s containing {@link MGLModel} of the above described format
	 */
	def dispatch static String nsURI(GraphModel graphModel){
		graphModel.mglModel.nsURI
	}
	
	/**
	 * Returns an namespace URI for the provided <code>modelElement</code>'s containing {@link MGLModel}.
	 * The URI consists of the first three segments of the <code>modelElement</code>'s containing {@link MGLModel}'s <code>package</code> property in reversed order, separated with ".".
	 * All following segments are then concatenated after the first three segments and separated by "/". Their order remains unaltered.
	 * Of course, the whole URI starts with "http://"
	 * <br />
	 * <br />
	 * Example:<br />
	 * <code>package</code> "info.scce.cinco.some.example"<br />
	 * <strong>result</strong> "http://cinco.scce.info/some/example"
	 * 
	 * @param	modelElement	the {@link ModelElement} for which's containing {@link MGLModel} the namespace URI should be returned
	 * @return	a {@link String} representing the nsURI for the <code>modelElement</code>'s containing {@link MGLModel} of the above described format
	 */
	def dispatch static String nsURI(ModelElement modelElement){
		modelElement.mglModel.nsURI
	}
	
	/**
	 * Returns all {@link Type Types} that can be contained in any context within the provided <code>graphModel</code>.
	 * This includes all containable {@link Node Nodes}, {@link Edge Edges}, and {@link ComplexAttribute ComplexAttributes}.
	 * 
	 * @param	graphModel	the {@link GraphModel} for which all {@link Type Types} should be returned
	 * @return	a {@link HashSet} which contains all {@link Type Types} that are containable in any context within the provided <code>graphModel</code>
	 */
	def static types(GraphModel graphModel){
		var result = new HashSet<Type>
		var modelElementsToCheck = new LinkedHashSet<ModelElement>
		var userDefinedTypesToCheck = new LinkedHashSet<UserDefinedType>
		modelElementsToCheck.add(graphModel)
		modelElementsToCheck.addAll(graphModel.getUsableNodes(true))
		modelElementsToCheck.addAll(graphModel.usableEdges)
		for(modelElement : modelElementsToCheck) {
			for(attribute : modelElement.attributes) {
				if(attribute instanceof ComplexAttribute) {
					result.add(attribute.type)
					if(attribute.type instanceof UserDefinedType) {
						userDefinedTypesToCheck.add(attribute.type as UserDefinedType)
					}
				}
			}
		}
		for(var i = 0; i < userDefinedTypesToCheck.size; i++) {
			val userDefinedType = userDefinedTypesToCheck.get(i)
			for(attribute : userDefinedType.attributes) {
				if(attribute instanceof ComplexAttribute) {
					result.add(attribute.type)
					if(attribute.type instanceof UserDefinedType) {
						userDefinedTypesToCheck.add(attribute.type as UserDefinedType)
					}
				}
			}
		}
		return result
	}
	
	/**
	 * Returns all {@link Node Nodes} the <code>graphModel</code> or any of it parent {@link GraphModel GraphModels} can contain.
	 * The result only contains the explicitly stated {@link Node Nodes} (in <code>containableElements</code>).
	 * For example, nodes that extend any of the containable nodes are not included in the result. For this purpose see
	 *  {@link GeneratorUtils#getUsableNodes(ContainingElement) GeneratorUtils#getUsableNodes(ContainingElement)}.
	 * 
	 * @param	graphModel	the {@link GraphModel} for which all containable {@link Node Nodes} of itself and any of its parents should be returned
	 * @return	a {@link Set} which contains all {@link Node Nodes} the provided <code>graphModel</code> or any of it parents can contain
	 * @see		de.jabc.cinco.meta.core.utils.generator.GeneratorUtils#getUsableNodes(ContainingElement) GeneratorUtils#getUsableNodes(ContainingElement)
	 */
	def static nodes(GraphModel graphModel){
		(graphModel.containableNodeElements + graphModel.importedNodes).toSet
	}
	
	/**
	 * Returns all {@link NodeContainer NodeContainers} the <code>graphModel</code> or any of it parent {@link GraphModel GraphModels} can contain.
	 * The result only contains the explicitly stated {@link NodeContainer NodeContainers} (in <code>containableElements</code>).
	 * For example, node container that extend any of the containable node containers are not included in the result. For this purpose see
	 *  {@link GeneratorUtils#getUsableNodes(ContainingElement) GeneratorUtils#getUsableNodes(ContainingElement)}.
	 * 
	 * @param	graphModel	the {@link GraphModel} for which all containable {@link NodeContainer NodeContainers} of itself and any of its parents should be returned
	 * @return	a {@link Set} which contains all {@link NodeContainer NodeContainers} the provided <code>graphModel</code> or any of it parents can contain
	 * @see		de.jabc.cinco.meta.core.utils.generator.GeneratorUtils#getUsableNodes(ContainingElement) GeneratorUtils#getUsableNodes(ContainingElement)
	 */
	def static containers(GraphModel graphModel){
		graphModel.containableContainerElements + graphModel.importedContainers
	}
	
	/**
	 * Returns all {@link Node Nodes} that can be contained in any container the provided <code>graphModel</code> or any of its parents has stated as one of its 
	 * <code>containableElements</code>.
	 * 
	 * @param	graphModel	the {@link GraphModel} for which all nodes in containers should be returned
	 * @return	a {@link Set} which contains all {@link Node Nodes} that can be contained in any {@link NodeContainer} the <code>graphModel</code> or any of its parents
	 * 			has stated as one of its <code>containableElements</code>
	 */
	def static nodesInContainers(GraphModel graphModel) {
		graphModel.containers.map[graphModel.containableNodeElements].flatten.toSet
	}
	
	/**
	 * Returns all {@link Node Nodes} any {@link Node} of <code>mglModel</code> depends on.
	 * This includes:
	 * <ul>
	 * 	<li>All {@link Node Nodes} containable in any depth through <code>containableElements</code>-attribute of any node,</li>
	 * 	<li>all {@link Node Nodes} which extend any node in any depth,</li>
	 * 	<li>all {@link Node Nodes} which any node extends in any depth,
	 * 	<li>and also the three above relationships recursively for all matched elements
	 * </ul>
	 * 
	 * @param	mglModel	the {@link MGLModel} for which the all nodes' node dependencies should be returned
	 * @return	an {@link ArrayList} which contains all {@link Node Nodes} that have one of the above described relationships to any of <code>mglModel</code>'s nodes
	 * @see	#nodeDependencies(GraphModel) nodeDependencies(GraphModel)
	 * @see	#nodeDependencies(Node) nodeDependencies(Node)
	 */
	def static nodeDependencies(MGLModel mglModel) {
		val result = new ArrayList<Node>
		mglModel.graphModels.forEach[gm | result.addAll(nodeDependencies(gm))]
		mglModel.nodes.forEach[n | result.addAll(nodeDependencies(n))]
		return result.filterNull.toList
	}
	
	/**
	 * Returns all {@link Node Nodes} any {@link Node} of <code>graphModel</code> depends on.
	 * This includes:
	 * <ul>
	 * 	<li>All {@link Node Nodes} containable in any depth through <code>containableElements</code>-attribute of any node,</li>
	 * 	<li>all {@link Node Nodes} which extend any node in any depth,</li>
	 * 	<li>all {@link Node Nodes} which any node extends in any depth,
	 * 	<li>and also the three above relationships recursively for all matched elements
	 * </ul>
	 * 
	 * @param	graphModel	the {@link GraphModel} for which the all nodes' node dependencies should be returned
	 * @return	an {@link ArrayList} which contains all {@link Node Nodes} that have one of the above described relationships to any of <code>graphModel</code>'s nodes
	 * @see	#nodeDependencies(MGLModel) nodeDependencies(MGLModel)
	 * @see	#nodeDependencies(Node) nodeDependencies(Node)
	 * @see #nodes(GraphModel) nodes(GraphModel)
	 */
	def static nodeDependencies(GraphModel graphModel) {
		val containableElems = graphModel.nodes.toList
		containableElems.addAll(graphModel.nodes.map[nodeDependencies].flatten)
		return containableElems.filterNull
	}
	
	/**
	 * Returns all {@link Node Nodes} the provided <code>node</code> depends on.
	 * This includes:
	 * <ul>
	 * 	<li>All {@link Node Nodes} containable in any depth through <code>containableElements</code>-attribute,</li>
	 * 	<li>all {@link Node Nodes} which extend the provided <code>node</code> in any depth,</li>
	 * 	<li>all {@link Node Nodes} which <code>node</code> extends in any depth,
	 * 	<li>and also the three above relationships recursively for all matched elements
	 * </ul>
	 * 
	 * @param	node	the {@link Node} for which the node dependencies should be returned
	 * @return	an {@link ArrayList} which contains all {@link Node Nodes} that have one of the above described relationships to <code>node</code>
	 * @see	#nodeDependencies(MGLModel) nodeDependencies(MGLModel)
	 * @see	#nodeDependencies(GraphModel) nodeDependencies(GraphModel)
	 */
	def static nodeDependencies(Node node) {
		val result = new ArrayList<Node>()
		if(node instanceof NodeContainer) {
			val containableElems = node.containableNodeElements
			containableElems.forEach[ce | _nodeDependencies(ce, result)]
		}
		if(node.extends !== null) {
			_nodeDependencies(node.extends, result)
		}
		return result
	}
	
	
	def private static List<Node> _nodeDependencies(Node it, List<Node> aggregatedNodes) {
		if(!aggregatedNodes.contains(it)) {
			aggregatedNodes.add(it)
			
			//Also add nodes which extend this node type
			aggregatedNodes.addAll(getExtendingNodes(it))
			
			if(it instanceof NodeContainer) {
				val containableElems = it.containableNodeElements
				aggregatedNodes.addAll(containableElems)
				containableElems.forEach[ce | _nodeDependencies(ce, aggregatedNodes)]
			}
			if(it.extends !== null) {
				_nodeDependencies(it.extends, aggregatedNodes)
			}
		}
		return aggregatedNodes
	}
	
	/**
	 * Returns all {@link Node Nodes} which extend the provided <code>node</code> within the same {@link MGLModel} in any depth.
	 * 
	 * @param	node	the {@link Node} for which the extending nodes should be returned
	 * @return	a {@link List} which contains all {@link Node Nodes} that extend <code>node</code> in any depth within the same {@link MGLModel}
	 */
	def static List<Node> getExtendingNodes(Node node) {
		val result = new ArrayList<Node>()
		for(otherNode : node.mglModel.nodes) {
			if(node.equalNodes(otherNode.extends)) {
				result.add(otherNode)
				result.addAll(getExtendingNodes(otherNode))
			}
		}
		return result
	}
	
	/**
	 * Returns all {@link Edge Edges} any {@link Edge} of <code>mglModel</code> depends on.
	 * This includes:
	 * <ul>
	 * 	<li>All {@link Edge Edges} createable at an containable {@link Node} in any depth through <code>containableElements</code>-attribute of any node reachable,</li>
	 * 	<li>all {@link Edge Edges} which any edge extends in any depth,
	 * 	<li>and also the two above relationships recursively for all matched elements
	 * </ul>
	 * 
	 * @param	mglModel	the {@link MGLModel} for which the all edges' edge dependencies should be returned
	 * @return	an {@link ArrayList} which contains all {@link Edge Edges} that have one of the above described relationships to any of <code>mglModel</code>'s nodes
	 * @see	#edgeDependencies(GraphModel) edgeDependencies(GraphModel)
	 * @see	#edgeDependencies(Edge) edgeDependencies(Edge)
	 */
	def static edgeDependencies(MGLModel mglModel) {
		val result = new ArrayList<Edge>
		mglModel.graphModels.forEach[gm | result.addAll(edgeDependencies(gm))]
		return result.filterNull
	}
	
	/**
	 * Returns all {@link Edge Edges} any {@link Edge} of <code>graphModel</code> depends on.
	 * This includes:
	 * <ul>
	 * 	<li>All {@link Edge Edges} createable at an containable {@link Node} in any depth through <code>containableElements</code>-attribute of any node reachable,</li>
	 * 	<li>all {@link Edge Edges} which any edge extends in any depth,
	 * 	<li>and also the two above relationships recursively for all matched elements
	 * </ul>
	 * 
	 * @param	graphModel	the {@link GraphModel} for which the all edges' edge dependencies should be returned
	 * @return	an {@link ArrayList} which contains all {@link Edge Edges} that have one of the above described relationships to any of <code>graphModel</code>'s nodes
	 * @see	#edgeDependencies(MGLModel) edgeDependencies(MGLModel)
	 * @see	#edgeDependencies(Edge) edgeDependencies(Edge)
	 */
	def static edgeDependencies(GraphModel graphModel) {
		val containableElems = graphModel.nodes.toList
		containableElems.addAll(graphModel.nodes.map[nodeDependencies].flatten)
		val result = new HashSet<Edge>
		result.addAll(containableElems.map[incomingConnectingEdges].flatten)
		result.addAll(containableElems.map[outgoingConnectingEdges].flatten)
		val edgesToCheckForInheritance = new HashSet<Edge>(result)
		result.addAll(edgesToCheckForInheritance.map[edgeDependencies].flatten)
		return result.filterNull
	}
	
	/**
	 * Returns all {@link Edge Edges} the provided <code>edge</code> depends on.
	 * This includes:
	 * <ul>
	 * 	<li>all {@link Edge Edges} which any edge extends in any depth,
	 * </ul>
	 * 
	 * @param	edge	the {@link Edge} for which the all edges' edge dependencies should be returned
	 * @return	an {@link ArrayList} which contains all {@link Edge Edges} that have one of the above described relationships to the provided <code>edge</code>
	 * @see	#edgeDependencies(MGLModel) edgeDependencies(MGLModel)
	 * @see	#edgeDependencies(GraphModel) edgeDependencies(GraphModel)
	 */
	def static edgeDependencies(Edge edge) {
		val result = new ArrayList<Edge>()
		if(edge.extends !== null) {
			_edgeDependencies(edge.extends, result)
		}
		return result
	}
	
	def private static List<Edge> _edgeDependencies(Edge it, List<Edge> aggregatedEdges) {
		if(!aggregatedEdges.contains(it)) {
			aggregatedEdges.add(it)
			if(it.extends !== null) {
				_edgeDependencies(it.extends, aggregatedEdges)
			}
		}
		return aggregatedEdges
	}
	
	/**
	 * Returns a maps which returns all {@link Node Nodes} of any {@link MGLModel} the provided <code>containingElement</code> can contain and matches
	 * it to the {@link MGLModel} the respective {@link Node} has been declared in.
	 * All {@link Node Nodes} which can be contained transitively in <code>containingElement</code> are included
	 * in the result as well.
	 * See {@link #nodesWithOrigins(ContainingElement, boolean) nodesWithOrigins(ContainingElement, boolean)} if you like to exclude transitively contained {@link Node Nodes}.
	 * 
	 * @param	containingElement	the {@link ContainingElement} for which all usable nodes and their origins should be returned
	 * @return	a {@link HashMap} which maps all usable {@link Node Nodes} of the <code>containingElement</code> to the {@link MGLModel} they have been declared in
	 * @see		de.jabc.cinco.meta.core.utils.generator.GeneratorUtils#getUsableNodes(ContainingElement) GeneratorUtils#getUsableNodes(ContainingElement)
	 * @see		#nodesWithOrigins(ContainingElement, boolean) nodesWithOrigins(ContainingElement, boolean)
	 */
	def static HashMap<MGLModel, Set<Node>> nodesWithOrigins(ContainingElement it) {
		nodesWithOrigins(it, false)
	}
	
	/**
	 * Returns a maps which returns all {@link Node Nodes} of any {@link MGLModel} the provided <code>containingElement</code> can contain and matches
	 * it to the {@link MGLModel} the respective {@link Node} has been declared in.
	 * If <code>anyDepth</code> is <code>true</code> {@link Node Nodes} which can be contained transitively in <code>containingElement</code> are included
	 * in the result as well.
	 * 
	 * @param	containingElement	the {@link ContainingElement} for which all usable nodes and their origins should be returned
	 * @param	anyDepth			a flag to state whether transitive containments should be respected
	 * @return	a {@link HashMap} which maps all usable {@link Node Nodes} of the <code>containingElement</code> to the {@link MGLModel} they have been declared in
	 * @see		de.jabc.cinco.meta.core.utils.generator.GeneratorUtils#getUsableNodes(ContainingElement) GeneratorUtils#getUsableNodes(ContainingElement)
	 */
	def static HashMap<MGLModel, Set<Node>> nodesWithOrigins(ContainingElement containingElement, boolean anyDepth) {
		val result = new HashMap<MGLModel, Set<Node>>()
		val usableNodes = containingElement.getUsableNodes(anyDepth)
		for(currentMgl : allMGLs) {
			var currentNodes = new HashSet<Node>()
			for(node : currentMgl.nodes) {
				val matchingNode = usableNodes.findFirst[equalNodes(node)]
				if(matchingNode !== null) {
					usableNodes.removeIf[equalNodes(matchingNode)]
					currentNodes.add(matchingNode)			
				}
			}
			result.put(currentMgl, currentNodes)
			
			if(usableNodes.empty) {
				return result
			}
		}
		
		return result
	}
	
	/**
	 * Returns a maps which returns all {@link Type Types} of any {@link MGLModel} the provided <code>graphModel</code> can contain anywhere and matches
	 * it to the {@link MGLModel} the respective {@link Type} has been declared in.
	 * 
	 * @param	graphModel	the {@link GraphModel} for which all usable types and their origins should be returned
	 * @return	a {@link HashMap} which maps all usable {@link Type Types} of the <code>graphModel</code> to the {@link MGLModel} they have been declared in
	 */
	def static HashMap<MGLModel, Set<Type>> typesWithOrigins(GraphModel graphModel) {
		val result = new HashMap<MGLModel, Set<Type>>()
		val usableTypes = graphModel.types
		for(currentMgl : allMGLs) {
			var currentTypes = new HashSet<Type>()
			for(type : currentMgl.types) {
				val matchingType = usableTypes.findFirst[equalTypes(type)]
				if(matchingType !== null) {
					usableTypes.removeIf[equalTypes(matchingType)]
					currentTypes.add(matchingType)			
				}
			}
			result.put(currentMgl, currentTypes)
			
			if(usableTypes.empty) {
				return result
			}
		}
		
		return result
	}
	
	/**
	 * Returns a maps which returns all {@link Edge Edges} of any {@link MGLModel} the provided <code>graphModel</code> can contain anywhere and matches
	 * it to the {@link MGLModel} the respective {@link Edge} has been declared in.
	 * 
	 * @param	graphModel	the {@link GraphModel} for which all usable edges and their origins should be returned
	 * @return	a {@link HashMap} which maps all usable {@link Edge Edges} of the <code>graphModel</code> to the {@link MGLModel} they have been declared in
	 * @see		de.jabc.cinco.meta.core.utils.generator.GeneratorUtils#getUsableEdges(ContainingElement) GeneratorUtils#getUsableEdges(ContainingElement)
	 */
	def static HashMap<MGLModel, Set<Edge>> edgesWithOrigins(GraphModel graphModel) {
		val result = new HashMap<MGLModel, Set<Edge>>()
		val usableEdges = graphModel.getUsableEdges
		for(currentMgl : allMGLs) {
			var currentEdges = new HashSet<Edge>()
			for(edge : currentMgl.edges) {
				val matchingEdge = usableEdges.findFirst[equalTypes(edge)]
				if(matchingEdge !== null) {
					usableEdges.removeIf[equalTypes(matchingEdge)]
					currentEdges.add(matchingEdge)			
				}
			}
			result.put(currentMgl, currentEdges)
			
			if(usableEdges.empty) {
				return result
			}
		}
		
		return result
	}
	
	/**
	 * Returns a maps which returns all {@link UserDefinedType UserDefinedTypes} of any {@link MGLModel} the provided <code>graphModel</code> can contain anywhere and matches
	 * it to the {@link MGLModel} the respective {@link UserDefinedType} has been declared in.
	 * 
	 * @param	graphModel	the {@link GraphModel} for which all usable user-defined types and their origins should be returned
	 * @return	a {@link HashMap} which maps all usable {@link UserDefinedType UserDefinedTypes} of the <code>graphModel</code> to the {@link MGLModel} they have been declared in
	 * @see		de.jabc.cinco.meta.core.utils.generator.GeneratorUtils#getUsableUserDefinedType(ContainingElement) GeneratorUtils#getUsableUserDefinedType(ContainingElement)
	 */
	def static HashMap<MGLModel, Set<UserDefinedType>> userDefinedTypesWithOrigins(GraphModel graphModel) {
		val result = new HashMap<MGLModel, Set<UserDefinedType>>()
		val usableUserDefinedTypes = graphModel.usableUserDefinedType

		for(currentMgl : allMGLs) {
			var currentUserDefinedTypes = new HashSet<UserDefinedType>()
			for(userDefinedType : currentMgl.types.filter(UserDefinedType)) {
				val matchingUserDefinedType = usableUserDefinedTypes.findFirst[equalTypes(userDefinedType)]
				if(matchingUserDefinedType !== null) {
					usableUserDefinedTypes.removeIf[equalTypes(matchingUserDefinedType)]
					currentUserDefinedTypes.add(matchingUserDefinedType)			
				}
			}
			result.put(currentMgl, currentUserDefinedTypes)
			
			if(usableUserDefinedTypes.empty) {
				return result
			}
		}
		
		return result
	}
	
	//Returns all extending or extended Edges and the Edge itself
	/**
	 * Returns the provided <code>edge</code> itself, all {@link Edge Edges} it extends, and all {@link Edge Edges} it is extended by usable within the provided <code>graphModel</code>.
	 * 
	 * @param	edge	the {@link Edge} for which the generalised edges should be returned
	 * @param	graphModel	the {@link GraphModel} in which the extending edges have to be usable in
	 * @return	a {@link Set} which contains all {@link Edge Edges} with the above described relationship to the provided <code>edge</code>
	 * @see		de.jabc.cinco.meta.core.utils.generator.GeneratorUtils#getUsableEdges(ContainingElement) GeneratorUtils#getUsableEdges(ContainingElement)
	 */
	def static Set<Edge> generaliseEdge(Edge edge, GraphModel graphModel) {
		val result = new HashSet<Edge>
		result.add(edge)
		
		//Collect extended edges
		var extendedEdge = edge.extends 
		while(extendedEdge !== null) {
			result.add(extendedEdge)
			extendedEdge = extendedEdge.extends
		}
		
		//Collect extending edges
		val moreGeneralEdges = new HashSet<Edge>
		val moreGeneralEdgesIterator = moreGeneralEdges.iterator
		val allEdges = graphModel.usableEdges
		for(allEdge : allEdges) {
			if(allEdge.extends.equalEdges(edge)) {
				result.add(allEdge)
				moreGeneralEdges.add(allEdge)
			}
		}
		while(moreGeneralEdgesIterator.hasNext) {
			val inspectingEdge = moreGeneralEdgesIterator.next
			for(allEdge : allEdges) {
				if(allEdge.extends.equalEdges(inspectingEdge)) {
					result.add(inspectingEdge)
					moreGeneralEdges.add(inspectingEdge)
				}
			}
		}
		
		return result
	}
	
	/**
	 * Returns all extended {@link GraphModel GraphModels} of any depth starting with the provided <code>graphModel</code>.
	 * Does not include the <code>graphModel</code> itself.
	 * 
	 * @param	graphModel	the {@link GraphModel} for which all extended {@link GraphModel GraphModels} should be returned
	 * @return	a {@link Set} which contains all {@link GraphModel GraphModels} that <code>graphModel</code> extends, including transitively extensions
	 */
	def static getAllExtendedGraphModels(GraphModel graphModel){
		return _getAllExtendedGraphModels(graphModel, new HashSet<GraphModel>())
	}
	
	private def static Set<GraphModel> _getAllExtendedGraphModels(GraphModel it, Set<GraphModel> importedGraphModels){
		val extendedModel = it.extends
		if(extendedModel !== null && !importedGraphModels.contains(extendedModel)) {
			importedGraphModels.add(extendedModel)
			_getAllExtendedGraphModels(extendedModel, importedGraphModels)
		}
		return importedGraphModels
	}
	
	/**
	 * Returns all {@link Edge Edges} that are either connecting one of <code>graphModel</code>'s <code>containableElements</code>, 
	 * connecting any node containable in any depth in the <code>graphModel</code>, or an edge with one of the two described relationships
	 * in any {@link GraphModel} <code>graphModel</code> extends.
	 * 
	 * @param	graphModel	the {@link GraphModel} for which all {@link Edge Edges} with the above mentioned relationships should be returned
	 * @return	a {@link HashSet} which contains all {@link Edge Edges} with the above mentioned properties
	 */
	def static edges(GraphModel graphModel){
		val result = new HashSet<Edge>
		result.addAll(graphModel.containableNodeElements.map[incomingConnectingEdges].flatten)
		result.addAll(graphModel.containableNodeElements.map[outgoingConnectingEdges].flatten)
		result.addAll(graphModel.nodesInContainers.map[incomingConnectingEdges].flatten)
		result.addAll(graphModel.nodesInContainers.map[outgoingConnectingEdges].flatten)
		result.addAll(graphModel.importedEdges)
		return result
	}
	
	/**
	 * Returns all {@link ModelElement ModelElements} the <code>graphModel</code>'s {@link MGLModel} contains excluding any {@link GraphModel}
	 * that is not the provided <code>graphModel</code> itself.
	 * 
	 * @param	graphModel	the {@link GraphModel} for which the {@link MGLModel MGLModel's} {@link ModelElement ModelElements} should be returned
	 * @return	a {@link List} that contains all {@link ModelElement ModelElements} the <code>graphModel</code>'s {@link MGLModel} contains except any other {@link GraphModel}
	 * 			than the <code>graphModel</code> itself
	 */
	def static modelElements(GraphModel graphModel){
		var elements = graphModel.mglModel.modelElements
		elements.removeIf(e | e instanceof GraphModel && e.name != graphModel.name)
		return elements
	}
	
	/**
	 * Returns all {@link ModelElement ModelElements} defined in the provided <code>mgl</code>.
	 * 
	 * @param	mgl	the {@link MGLModel} for which the {@link ModelElement ModelElements} should be returned
	 * @return	a {@link List} that contains all {@link ModelElement ModelElements} defined in the provided <code>mgl</code>
	 */
	def static modelElements(MGLModel mgl){
		return modelElements(mgl, true)
	}
	
	/**
	 * Returns all {@link ModelElement ModelElements} defined in the provided <code>mgl</code>.
	 * If <code>containGraphModels</code> is set to <code>true</code> {@link GraphModel GraphModels} are included
	 * in the result as well. Otherwise they are excluded.
	 * 
	 * @param	mgl	the {@link MGLModel} for which the {@link ModelElement ModelElements} should be returned
	 * @param	containGraphModels	a flag to state whether {@link GraphModel GraphModels} should be included or excluded from the result
	 * @return	a {@link List} that contains all {@link ModelElement ModelElements} defined in the provided <code>mgl</code>. If the
	 * 			<code>containGraphModels</code> flag is set to <code>false</code> {@link GraphModel GraphModels} will be excluded from the result
	 */
	def static modelElements(MGLModel mgl, boolean containGraphModels){
		var elements = mgl.modelElements
		if(!containGraphModels) {
			elements.removeIf(e | e instanceof GraphModel)
		}
		return elements
	}
	
	/**
	 * Returns all {@link GraphModel GraphModels} and {@link NodeContainer NodeContainers} defined in the provided <code>mglModel</code>.
	 * 
	 * @param	mglModel	the {@link MGLModel} for which all {@link GraphModel GraphModels} and {@link NodeContainer NodeContainers} defined in it should be returned
	 * @return	an {@link Iterable} which contains all {@link GraphModel GraphModels} and {@link NodeContainer NodeContainers} defined in the provided <code>mglModel</code>.
	 * 			The elements in the resulting {@link Iterable} are types as {@link ContainingElement ContainingElements}
	 */
	def static Iterable<ContainingElement> nodeContainers(MGLModel mglModel){
		mglModel.graphModels + mglModel.nodes.filter(NodeContainer) 
	}
	
	/**
	 * Returns all {@link Node Nodes} the parent {@link GraphModel GraphModels} of the provided <code>graphModel</code> can contain.
	 * This function is called recursively and therefore contains the {@link Node Nodes} of <strong>all</strong> parents.
	 * 
	 * @param	graphModel	the {@link GraphModel} for which the {@link Node Nodes} of all parents should be returned
	 * @return	a {@link List} which contains all {@link Node Nodes} any parent of the provided <code>graphModel</code> can contain
	 */
	def static List<Node> getImportedNodes(GraphModel graphModel) {
		val extendedModel = graphModel.extends
		if(extendedModel !== null) {
			val result = nodes(extendedModel).toList
			result.addAll(getImportedNodes(extendedModel))
			return result	
		} else {
			return new BasicEList<Node>
		}
	}
	
	/**
	 * Returns all {@link NodeContainer NodeContainers} the parent {@link GraphModel GraphModels} of the provided <code>graphModel</code> can contain.
	 * This function is called recursively and therefore contains the {@link NodeContainer NodeContainers} of <strong>all</strong> parents.
	 * 
	 * @param	graphModel	the {@link GraphModel} for which the {@link NodeContainer NodeContainers} of all parents should be returned
	 * @return	a {@link List} which contains all {@link NodeContainer NodeContainers} any parent of the provided <code>graphModel</code> can contain
	 */
	def static List<NodeContainer> getImportedContainers(GraphModel graphModel) {
		val extendedModel = graphModel.extends
		if(extendedModel !== null) {
			val result = containers(extendedModel).toList
			result.addAll(getImportedContainers(extendedModel))
			return result
		} else {
			return new BasicEList<NodeContainer>
		}
	}
	
	/**
	 * Returns all {@link Edge Edges} the parent {@link GraphModel GraphModels} of the provided <code>graphModel</code> can contain.
	 * This function is called recursively and therefore contains the {@link Edge Edges} of <strong>all</strong> parents.
	 * 
	 * @param	graphModel	the {@link GraphModel} for which the {@link Edge Edges} of all parents should be returned
	 * @return	a {@link List} which contains all {@link Edge Edges} any parent of the provided <code>graphModel</code> can contain
	 */
	def static Set<Edge> getImportedEdges(GraphModel graphModel) {
		val extendedModel = graphModel.extends
		if(extendedModel !== null) {
			val result = new HashSet<Edge>()
			result.addAll(edges(extendedModel))
			result.addAll(getImportedEdges(extendedModel))
			return result
		} else {
			return new HashSet<Edge>
		}
	}
	
	/**
	 * Returns whether the provided <code>edge</code> can be an incoming or outgoing connection to <code>node</code>.
	 * 
	 * @param	node	the {@link Node} that should be checked whether it can be connected by an {@link Edge}
	 * 					of <code>edge</code>'s type
	 * @param	edge	the {@link Edge} that should be checked whether it can connect <code>node</code>
	 * @return	a <code>boolean</code> whether <code>edge</code> can connect <code>node</code>
	 */
	def static boolean canNodeBeReferencedByEdge(Node node, Edge edge) {
		return edge.allPossibleSources.exists[equalNodes(node)] || edge.allPossibleTargets.exists[equalNodes(node)]
	}
	
	/**
	 * Returns a {@link Set} of {@link Node Nodes} that can be contained <b>directly</b> in the provided <code>graphModel</code>.
	 * This includes all nodes usable through inherited properties but not nodes usable in {@link ContainableElement ContainableElements}
	 * within the <code>graphModel</code>.
	 * 
	 * @param	graphModel	the {@link GraphModel} to return the usable nodes for
	 * @return	a {@link Set} which contains all {@link Node Nodes} usable directly in the provided <code>graphModel</code>
	 */
	def static Set<Node> getUsableNodes(GraphModel graphModel) {
		graphModel.getUsableNodes
	}
	
	/**
	 * Returns a {@link Set} of {@link Node Nodes} that can be contained any in the provided <code>graphModel</code>.
	 * This includes all edges usable through inherited properties and also edges usable in {@link ContainableElement ContainableElements}
	 * within the <code>graphModel</code>.
	 * 
	 * @param	graphModel	the {@link GraphModel} to return the usable nodes for
	 * @return	a {@link Set} which contains all {@link Edge Edges} usable directly in the provided <code>graphModel</code>
	 */
	def static Set<Edge> getUsableEdges(GraphModel graphModel) {
		graphModel.usableEdges
	}
	
	/**
	 * Returns the fully qualified name for the provided <code>modelElement</code>.
	 * It consists of the <code>modelElement</code>'s {@link MGLModel} package, the <code>modelElement</code>'s {@link MGLModel} file name, and
	 * the <code>modelElement</code>'s name.
	 * 
	 * @param	modelElement	the {@link ModelElement} for which the fully qualified name should be returned
	 * @return	a {@link String} that represents the fully qualified name of the provided <code>modelElement</code>
	 */
	def static String getFqn(ModelElement modelElement) {
		return modelElement.mglModel.package.toLowerCase + "." + modelElement.mglModel.fileName.toLowerCase + "." + modelElement.name
	}
}
