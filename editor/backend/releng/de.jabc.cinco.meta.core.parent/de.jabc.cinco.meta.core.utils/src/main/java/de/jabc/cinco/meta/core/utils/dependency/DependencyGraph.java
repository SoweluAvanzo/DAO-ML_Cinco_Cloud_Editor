package de.jabc.cinco.meta.core.utils.dependency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import de.jabc.cinco.meta.core.utils.MGLUtil;
import de.jabc.cinco.meta.core.utils.dependency.DependencyNode;
import mgl.Edge;
import mgl.MGLModel;
import mgl.Node;

public class DependencyGraph<T> {
	HashMap<T,DependencyNode<T>> nodes;
	
	public DependencyGraph(){
		this.nodes = new HashMap<T, DependencyNode<T>>();
	}
	
	public void addNode(DependencyNode<T> node){
		nodes.put(node.getPath(),node);
	}
	
	public DependencyGraph<T> createGraph(Iterable<DependencyNode<T>> nodes){
		DependencyGraph<T> dpg = new DependencyGraph<T>();
		nodes.forEach(node -> dpg.addNode(node));
		
		return dpg;
	}
	
	public Stack<T> topSort(){
		Stack<T> stck = new Stack<>();
		List<T> toVisit = new ArrayList<>();
		
		for(T key: this.nodes.keySet()){
			if(nodes.get(key).getDependsOf().size()==0){
				stck.push(key);
			}else{
				toVisit.add(key);
			}
		}
	
		while(!toVisit.isEmpty()){
			List<T> toRemove = new ArrayList<T>();
			T lastCurrent =null;
 			for(T current: toVisit){
 				lastCurrent = current;
				DependencyNode<T> dn = findDependencyNodeOfObject(current);
				for(T stacked : stck){
					dn.removeDependency(stacked);
					
				}
				if(dn.getDependsOf().size()==0){
					stck.push(current);
					toRemove.add(current);
				}
			}
 			if(!toRemove.isEmpty())
 				toVisit.removeAll(toRemove);
 			else
 				throw new RuntimeException(String.format("Could not resolve Dependencies, Dependency Graph contains cycles, including '%s'.",lastCurrent));
		}
		return stck;
	}
	
	private DependencyNode<T> findDependencyNodeOfObject(T obj) {
		if(obj instanceof MGLModel) {
			for(T mglModel : nodes.keySet()) {
				if(mglModel instanceof MGLModel && MGLUtil.equalMGLModels((MGLModel) mglModel, (MGLModel) obj)) {
					return nodes.get(mglModel);
				}
			}
			return null;
		} else if(obj instanceof Node) {
			for(T node : nodes.keySet()) {
				if(node instanceof Node && MGLUtil.equalModelElement((Node) node, (Node) obj)) {
					return nodes.get(node);
				}
			}
			return null;
		} else if(obj instanceof Edge) {
			for(T edge : nodes.keySet()) {
				if(edge instanceof Edge && MGLUtil.equalModelElement((Edge) edge, (Edge) obj)) {
					return nodes.get(edge);
				}
			}
			return null;
		} else {
			return nodes.get(obj);
		}
	}

	public void addNodes(List<DependencyNode<T>> nodes) {
	  nodes.forEach(n->this.addNode(n));
	}
}
