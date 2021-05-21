package de.jabc.cinco.meta.core.utils.dependency;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import de.jabc.cinco.meta.core.utils.dependency.DependencyNode;
import de.jabc.cinco.meta.core.utils.generator.GeneratorUtils;
import mgl.MGLModel;

public class DependencyNode<T> {
	private T path;
	private Set<T> dependsOf;
	private GeneratorUtils generatorUtils;

	public Set<T> getDependsOf() {
		return dependsOf;
	}

	public DependencyNode(T path){
		this.path = path;
		this.dependsOf = new HashSet<T>();
		this.generatorUtils = GeneratorUtils.getInstance();
	}
	
	public boolean dependsOf(T path){
		return this.dependsOf.add(path);
	}
	
	public boolean removeDependency(T path){
		if(path instanceof MGLModel) {
			for(T mglModel : this.dependsOf) {
				if(mglModel instanceof MGLModel
						&& (generatorUtils.getFileName((MGLModel) mglModel)).contentEquals(generatorUtils.getFileName((MGLModel) path))
						&& ((MGLModel) mglModel).getPackage().contentEquals(((MGLModel) path).getPackage())) {
					return this.dependsOf.remove(mglModel);
				}
			}
			return false;
		} else {
			return this.dependsOf.remove(path);
		}
	}
	

	public T getPath() {
		return this.path;
	}

	public boolean addDependencies(Collection<T> strings) {
	  return dependsOf.addAll(strings);
	}
	
	@Override
	public String toString() {
		return "DependencyNode [path=" + path + "]";
	}
	
}
