package de.jabc.cinco.meta.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.collect.Lists;

import mgl.Attribute;
import mgl.Edge;
import mgl.GraphModel;
import mgl.ModelElement;
import mgl.Node;
import mgl.NodeContainer;
import mgl.UserDefinedType;
import static de.jabc.cinco.meta.core.utils.MGLUtil.getFqn;

public class InheritanceUtil {

	/**
	 * Checks whether the provided <code>modelElement</code> contains any circular inheritances.
	 * If it does a {@link List} that contains all objects that are part of this circular inheritance relationship.
	 * If not it returns <code>null</code>.
	 * 
	 * @param modelElement	the {@link ModelElement} that should be checked for circular inheritances
	 * @return	a {@link List} containing the names of the {@link ModelElement ModelElements} that are part of the circular inheritance
	 * 			relationship which has its origin in <code>modelElement</code> or <code>null</code> otherwise
	 */
	public List<String> checkMGLInheritance(ModelElement modelElement) {
		if (modelElement instanceof GraphModel) {
			return checkGraphModelInheritance((GraphModel) modelElement);
		}
		if (modelElement instanceof Node) {
			return checkNodeInheritance((Node) modelElement);
		}
		if (modelElement instanceof Edge) {
			return checkEdgeInheritance((Edge) modelElement);
		}
		if (modelElement instanceof UserDefinedType){
			return checkUserDefinedTypeInheritance((UserDefinedType) modelElement);
		}
		return null;
	}
	
	private List<String> checkUserDefinedTypeInheritance(
			UserDefinedType type) {
		UserDefinedType curr = type;
		List<String> ancestors = new ArrayList<>();
		while (curr != null) {
			if (ancestors.contains(getFqn(curr))) {
				return ancestors;
			}
			ancestors.add(getFqn(curr));
			curr = curr.getExtends();
		}
		
		return null;
	}

	private List<String> checkGraphModelInheritance(GraphModel g) {
		GraphModel curr = g;
		List<String> ancestors = new ArrayList<>();
		while (curr != null) {
			if (ancestors.contains(getFqn(curr))) {
				return ancestors;
			}
			ancestors.add(getFqn(curr));
			curr = curr.getExtends();
			if(curr.eIsProxy()) {
				URI proxyURI = ((InternalEObject)curr).eProxyURI();
				curr = (GraphModel) g.eResource().getResourceSet().getEObject(proxyURI, true);
			}
		}
		
		return null;
	}
	
	private List<String> checkNodeInheritance(Node node) {
		Node curr = node;
		List<String> ancestors = new ArrayList<>();
		while (curr != null) {
			if (ancestors.contains(getFqn(curr))) {
				return ancestors;
			}
			ancestors.add(getFqn(curr));
			curr = curr.getExtends();
		}
		
		return null;
	}
	
	private static List<String> checkEdgeInheritance(Edge edge) {
		Edge curr = edge;
		List<String> ancestors = new ArrayList<>();
		while (curr != null) {
			if (ancestors.contains(getFqn(curr))) {
				return ancestors;
			}
			ancestors.add(getFqn(curr));
			curr = curr.getExtends();
		}
		
		return null;
	}
	
	/**
	 * Returns all inherited attributes of any depth of the provided <code>modelElement</code>.
	 * <br/>
	 * Attributes of the <code>modelElement</code> itself are <strong>not</strong> included.
	 * 
	 * @param modelElement	the {@link ModelElement} for which all inherited {@link Attribute Attributes} should be returned
	 * @return	a {@link List} that contains all {@link Attribute Attributes} that the provided <code>modelElement</code> possesses
	 * 			via inheritances of any depth
	 */
	public List<Attribute> getInheritedAttributes(ModelElement modelElement) {
		ArrayList<Attribute> attributes = new ArrayList<>();
		List<String> checked = checkMGLInheritance(modelElement);
		if (modelElement instanceof Node)
			modelElement = ((Node) modelElement).getExtends();
		if (modelElement instanceof Edge)
			modelElement = ((Edge) modelElement).getExtends();
		if (modelElement instanceof NodeContainer)
			modelElement = ((NodeContainer) modelElement).getExtends();
		while (modelElement != null && (checked == null || checked.isEmpty()) ) {
			attributes.addAll(modelElement.getAttributes());
			if (modelElement instanceof Node)
				modelElement = ((Node) modelElement).getExtends();
			if (modelElement instanceof Edge)
				modelElement = ((Edge) modelElement).getExtends();
			if (modelElement instanceof NodeContainer)
				modelElement = ((NodeContainer) modelElement).getExtends();
		}
		
		return attributes;
	}
	
	/**
	 * Returns a {@link GraphModel} that is mutual to all of the provided <code>graphModels</code>.
	 * The resulting {@link GraphModel} tries to be as close to the provides <code>graphModels</code> as possible.
	 * This means that the graph model with least parent graph models will be selected as the lowest mutual super graph model.
	 * <br/>
	 * If no mutual super graph model exists <code>null</code> is returned. In this case the class of {@link graphmodel.GraphModel}
	 * can be assumed to be the lowest mutual super graph model.
	 * 
	 * @param graphModels	an {@link Iterable} of {@link GraphModel GraphModels} for which the lowest mutual super graph model should be returned
	 * @return	the {@link GraphModel} that is the lowest mutual super graph model for the provided <code>graphModels</code> under respect of
	 * 			the above described characteristics. If no mutual super graph model exists <code>null</code> is returned instead
	 * @see #getLowestMutualSuperNode(Iterable)
	 * @see #getLowestMutualSuperEdge(Iterable)
	 */
	public GraphModel getLowestMutualSuperGraphModel(Iterable<GraphModel> graphModels){
		if(graphModels != null){
			HashSet<GraphModel> superGraphModels = new HashSet<GraphModel>();
			boolean first = true;
			for(GraphModel graphModel : graphModels){
				if(first) {
					superGraphModels.addAll(getAllSuperGraphModels(graphModel));
					first = false;
				} else {
					superGraphModels.retainAll(getAllSuperGraphModels(graphModel));
				}
				
			}
			if(superGraphModels.size() == 1) {
				return superGraphModels.toArray(new GraphModel[1])[0];
			} else if(superGraphModels.size()>1) {
				return sortGraphModelsByInheritance(Lists.newArrayList(superGraphModels)).get(superGraphModels.size()-1);
			} else {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Returns a {@link Node} that is mutual to all of the provided <code>nodes</code>.
	 * The resulting {@link Node} tries to be as close to the provides <code>nodes</code> as possible.
	 * This means that the node with least parent nodes will be selected as the lowest mutual super node.
	 * <br/>
	 * If no mutual super node exists <code>null</code> is returned. In this case the class of {@link graphmodel.Node}
	 * can be assumed to be the lowest mutual super node.
	 * 
	 * @param nodes	an {@link Iterable} of {@link Node Nodes} for which the lowest mutual super node should be returned
	 * @return	the {@link Node} that is the lowest mutual super node for the provided <code>nodes</code> under respect of
	 * 			the above described characteristics. If no mutual super node exists <code>null</code> is returned instead
	 * @see #getLowestMutualSuperGraphModel(Iterable)
	 * @see #getLowestMutualSuperEdge(Iterable)
	 */
	public Node getLowestMutualSuperNode(Iterable<Node> nodes){
		if(nodes != null){
			HashSet<Node> superNodes = new HashSet<Node>();
			boolean first = true;
			for(Node node : nodes) {
				if(first) {
					superNodes.addAll(getAllSuperNodes(node));
					first = false;
				}else{
					superNodes.retainAll(getAllSuperNodes(node));
				}
				
			}
			if(superNodes.size() == 1) {
				return superNodes.toArray(new Node[1])[0];
			} else if(superNodes.size() > 1) {
				return sortByInheritance(Lists.newArrayList(superNodes)).get(superNodes.size()-1);
			} else {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Returns an {@link Edge} that is mutual to all of the provided <code>edges</code>.
	 * The resulting {@link Edge} tries to be as close to the provides <code>edges</code> as possible.
	 * This means that the node with least parent edges will be selected as the lowest mutual super node.
	 * <br/>
	 * If no mutual super node exists <code>null</code> is returned. In this case the class of {@link graphmodel.Edge}
	 * can be assumed to be the lowest mutual super node.
	 * 
	 * @param edges	an {@link Iterable} of {@link Edge Edges} for which the lowest mutual super edge should be returned
	 * @return	the {@link Edge} that is the lowest mutual super edge for the provided <code>edges</code> under respect of
	 * 			the above described characteristics. If no mutual super edge exists <code>null</code> is returned instead
	 * @see #getLowestMutualSuperGraphModel(Iterable)
	 * @see #getLowestMutualSuperNode(Iterable)
	 */
	public Edge getLowestMutualSuperEdge(Iterable<Edge> edges){
		if(edges!=null){
			HashSet<Edge> superEdges = new HashSet<Edge>();
			boolean first = true;
			for(Edge edge: edges){
				if(first){
					superEdges.addAll(getAllSuperEdges(edge));
					first = false;
				}else{
					superEdges.retainAll(getAllSuperEdges(edge));
				}
			}
			if(superEdges.size()==1){
				return superEdges.toArray(new Edge[1])[0];
			}else if(superEdges.size()>1){
				return sortEdgesByInheritance(Lists.newArrayList(superEdges)).get(superEdges.size()-1);
			}else{
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Returns all {@link Edge Edges} that are parent to <code>edge</code> in any depth.
	 * <strong>Does also include <code>edge</code> itself.</strong>
	 * 
	 * @param edge	the {@link Edge} to retrieve all super edges for
	 * @return	a {@link Set} of <code>edge</code> itself and all {@link Edge Edges} that are super edges of <code>edge</code>
	 */
	public Collection<? extends Edge> getAllSuperEdges(Edge edge) {
		HashSet<Edge> superEdges = new HashSet<Edge>();
		superEdges.add(edge);
		Edge superEdge = edge.getExtends();
		List<String> checked = checkMGLInheritance(superEdge);
		while(superEdge!=null && (checked == null || checked.isEmpty())){
			superEdges.add(superEdge);
			superEdge = superEdge.getExtends();
		}
		return superEdges;
	}
	
	private List<GraphModel> sortGraphModelsByInheritance(List<GraphModel> graphModels) {
		graphModels.sort(new Comparator<GraphModel>() {

			@Override
			public int compare(GraphModel o1, GraphModel o2) {
				int j = 0, i = 0;
				GraphModel sn = o1.getExtends();
				while(sn != null){
					sn = sn.getExtends();
					i++;
				}
				sn = o2.getExtends();
				while(sn != null){
					sn = sn.getExtends();
					j++;
				}
				return Integer.compare(i, j);
			}
			
		});
		return graphModels;
	}

	private List<Node> sortByInheritance(List<Node> nodes) {
		nodes.sort(new Comparator<Node>() {

			@Override
			public int compare(Node o1, Node o2) {
				int j = 0, i = 0;
				Node sn = o1.getExtends();
				while(sn != null){
					sn = sn.getExtends();
					i++;
				}
				sn = o2.getExtends();
				while(sn != null){
					sn = sn.getExtends();
					j++;
				}
				return Integer.compare(i, j);
			}

		});
		return nodes;
	}
	
	private List<Edge> sortEdgesByInheritance(List<Edge> edges) {
		edges.sort(new Comparator<Edge>() {

			@Override
			public int compare(Edge o1, Edge o2) {
				int j = 0, i = 0;
				Edge sn = o1.getExtends();
				while(sn != null){
					sn = sn.getExtends();
					i++;
				}
				sn = o2.getExtends();
				while(sn != null){
					sn = sn.getExtends();
					j++;
				}
				return Integer.compare(i, j);
			}
			
		});
		return edges;
	}
	
	/**
	 * Returns all {@link GraphModel GraphModels} that are parent to <code>graphModel</code> in any depth.
	 * <strong>Does also include <code>graphModel</code> itself.</strong>
	 * 
	 * @param graphmodel	the {@link GraphModel} to retrieve all super graph models for
	 * @return	a {@link Set} of <code>graphModel</code> itself and all {@link GraphModel GraphModels} that are super graph models of <code>graphModel</code>
	 * @see #getAllSuperNodes(Node)
	 */
	public Set<GraphModel> getAllSuperGraphModels(GraphModel graphModel){
		HashSet<GraphModel> superGraphModels = new HashSet<GraphModel>();
		superGraphModels.add(graphModel);
		GraphModel superGraphModel = graphModel.getExtends();
		List<String> checked = checkMGLInheritance(superGraphModel);
		while(superGraphModel != null && (checked == null || checked.isEmpty())){
			superGraphModels.add(superGraphModel);
			superGraphModel = superGraphModel.getExtends();
		}
		return superGraphModels;
	}

	/**
	 * Returns all {@link Node Nodes} that are parent to <code>node</code> in any depth.
	 * <strong>Does also include <code>node</code> itself.</strong>
	 * 
	 * @param node	the {@link Node} to retrieve all super nodes for
	 * @return	a {@link Set} of <code>node</code> itself and all {@link Node Nodes} that are super nodes of <code>node</code>
	 * @see #getAllSuperGraphModels(GraphModel)
	 */
	public Set<Node> getAllSuperNodes(Node node){
		HashSet<Node> superNodes = new HashSet<Node>();
		superNodes.add(node);
		Node superNode = node.getExtends();
		List<String> checked = checkMGLInheritance(superNode);
		while(superNode != null && (checked == null || checked.isEmpty())){
			superNodes.add(superNode);
			superNode = superNode.getExtends();
		}
		return superNodes;
	}
	
	/**
	 * Returns whether the <code>superCandidate</code> is a parent {@link Node} of the <code>childCandidate</code>.
	 * The relationship is checked up to any depth.
	 * 
	 * @param superCandidate	the {@link Node} that should be checked whether it is a parent of <code>childCandidate</code>
	 * @param childCandidate	the {@link Node} that should be checked whether it is a child of <code>superCandidate</code>
	 * @return	<code>true</code> if <code>superCandidate</code> is parent of <code>childCandidate</code> in any depth,
	 * 			<code>false</code> otherwise
	 * @see	#isSuperEdgeOf(Edge, Edge)
	 * @see #isSuperGraphModelOf(GraphModel, GraphModel)
	 */
	public boolean isSuperNodeOf(Node superCandidate, Node childCandidate) {
		Node current = childCandidate.getExtends();
		
		while(current != null && current != childCandidate) {
			if(current == superCandidate)
				return true;
			
			current = current.getExtends();
		}
		return false;
	}
	
	/**
	 * Returns whether the <code>superCandidate</code> is a parent {@link Edge} of the <code>childCandidate</code>.
	 * The relationship is checked up to any depth.
	 * 
	 * @param superCandidate	the {@link Edge} that should be checked whether it is a parent of <code>childCandidate</code>
	 * @param childCandidate	the {@link Edge} that should be checked whether it is a child of <code>superCandidate</code>
	 * @return	<code>true</code> if <code>superCandidate</code> is parent of <code>childCandidate</code> in any depth,
	 * 			<code>false</code> otherwise
	 * @see #isSuperGraphModelOf(GraphModel, GraphModel)
	 * @see	#isSuperNodeOf(Node, Node)
	 */
	public boolean isSuperEdgeOf(Edge superCandidate, Edge childCandidate) {
		Edge current = childCandidate.getExtends();
		
		while(current != null && current != childCandidate) {
			if(current == superCandidate)
				return false;
			
			current = current.getExtends();
		}
		return false;
	}
	
	/**
	 * Returns whether the <code>superCandidate</code> is a parent {@link GraphModel} of the <code>childCandidate</code>.
	 * The relationship is checked up to any depth.
	 * 
	 * @param superCandidate	the {@link GraphModel} that should be checked whether it is a parent of <code>childCandidate</code>
	 * @param childCandidate	the {@link GraphModel} that should be checked whether it is a child of <code>superCandidate</code>
	 * @return	<code>true</code> if <code>superCandidate</code> is parent of <code>childCandidate</code> in any depth,
	 * 			<code>false</code> otherwise
	 * @see #isSuperEdgeOf(Edge, Edge)
	 * @see	#isSuperNodeOf(Node, Node)
	 */
	public boolean isSuperGraphModelOf(GraphModel superCandidate, GraphModel childCandidate) {
		GraphModel current = childCandidate.getExtends();
		
		while(current != null && current != childCandidate) {
			if(current == superCandidate)
				return false;
			
			current = current.getExtends();
		}
		return false;
	}
}
