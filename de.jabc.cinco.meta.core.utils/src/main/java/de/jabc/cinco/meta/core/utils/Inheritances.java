package de.jabc.cinco.meta.core.utils;

import java.util.ArrayList;
import java.util.List;

import mgl.Edge;
import mgl.GraphModel;
import mgl.ModelElement;
import mgl.Node;
import mgl.UserDefinedType;

/**
 * A class to manage inhertiance dependencies of {@link ModelElement ModelElements}.
 * <br/>
 * This class manages multiple {@link InheritanceTree InheritanceTrees} to analyze dependencies.
 * The very roots of each dependency chains are stored in this class.
 * <br/>
 * Several methods are provided to add new {@link ModelElement ModelElements} to 
 * {@link InheritanceTree InheritanceTrees} or to retrieve information of these trees.
 */
public class Inheritances {
	private List<InheritanceTree<ModelElement>> trees;
	
	/**
	 * Creates an empty {@link Inheritances} object that does not hold any trees yet.
	 */
	public Inheritances() {
		trees = new ArrayList<InheritanceTree<ModelElement>>();
	}
	
	/**
	 * Adds the provided <code>modelElement</code> to the inheritance trees.
	 * Creates a new entry for it and adds it as a child to a parent element if 
	 * code>modelElement</code> extends any.
	 * 
	 * @param modelElement	the {@link ModelElement} 
	 */
	public void addElement(ModelElement modelElement) {
		ModelElement parent = getParent(modelElement);
		if (parent == null) { 
			trees.add(new InheritanceTree<ModelElement>(modelElement));
			return;
		}

		InheritanceTree<ModelElement> containingTree = getContainingTree(parent);
		if (containingTree != null)
			containingTree.get(parent).add(modelElement);
		else trees.add(new InheritanceTree<ModelElement>(modelElement));
	}

	private InheritanceTree<ModelElement> getContainingTree(ModelElement parent) {
		for (InheritanceTree<ModelElement> t : trees) {
			if (t.contains(parent)) {
				return t;
			}
		}
		return null;
	}

	/**
	 * Prints the content of all trees.
	 * See {@link InheritanceTree#print()} for more information.
	 * 
	 * @see InheritanceTree#print()
	 */
	public void printTrees() {
		for (InheritanceTree<ModelElement> t : trees) {
			t.print();
		}
	}
	
	/**
	 * Returns a {@link List} that contains all {@link ModelElements} present in any
	 * {@link InheritanceTree} managed in this {@link Inheritance} object.
	 * The elements of the trees are added in postorder and each tree managed in this object
	 * is added after another.
	 * 
	 * @return	a {@link List} that contains all {@link ModelElement} of
	 * 			{@link InheritanceTree InheritanceTrees} managed in this {@link Inheritance} object
	 */
	public List<ModelElement> createList(){
		List<ModelElement> list = new ArrayList<ModelElement>();
		for (InheritanceTree<ModelElement> t : trees) {
			t.getPostorder(list);
		}
		return list;
	}
	
	/**
	 * Returns the parent {@link ModelElement} that is extended by <code>modelElement</code>.
	 * If <code>modelElement</code> does not extend any other element <code>null</code> is returned
	 * instead.
	 * 
	 * @param modelElement	the {@link ModelElement} which's parent should be returned if it exists
	 * @return	the {@link ModelElement} that is extended by <code>modelElement</code> or 
	 * 			<code>null</code> if <code>modelElement</code> does not extend anything
	 */
	private ModelElement getParent(ModelElement modelElement) {
		if (modelElement instanceof Node)
			return ((Node) modelElement).getExtends();
		if (modelElement instanceof Edge)
			return ((Edge) modelElement).getExtends();
		if (modelElement instanceof UserDefinedType)
			return ((UserDefinedType) modelElement).getExtends();
		if (modelElement instanceof GraphModel)
			return ((GraphModel) modelElement).getExtends();
		return null;
	}
	
	/**
	 * A tree representation to inspect inheritance dependecies.
	 * <br/>
	 * This class holds a single object of type <code>E</code> as its root and
	 * any number of children of the same type.
	 * <br/>
	 * 
	 * @param <E>	a type that extends {@link ModelElement} as only such types can extend
	 * 				other elements in the context of MGL
	 */
	class InheritanceTree<E extends ModelElement> {
		private E root;
		private List<InheritanceTree<E>> children;
		
		/**
		 * Creates an empty {@link InheritanceTree} with no root element and an empty list
		 * for children.
		 * 
		 * @see InheritanceTree#InheritanceTree(ModelElement)
		 */
		public InheritanceTree() {
			children = new ArrayList<InheritanceTree<E>>();
		}
		
		/**
		 * Creates an {@link InheritanceTree} that contains the provided <code>element</code>
		 * as its root and an empty list for children.
		 * 
		 * @param element	the object of type <code>E</code> to initially set as the newly created
		 * 					tree's root object
		 * @see InheritanceTree#InheritanceTree() 
		 */
		public InheritanceTree(E element) {
			root = element;
			children = new ArrayList<InheritanceTree<E>>();
		}
		
		/**
		 * Prints the content of this {@link InheritanceTree}.
		 * First the name of its root is printed, followed by the root name of all children
		 * separated by commas.
		 * Then a horizontal rule separates this listing from a recursive call on all children.
		 */
		public void print() {
			System.out.print(this.getRoot().getName() + ", ");
			children.forEach(c -> System.out.print(c.getRoot().getName() + ", "));
			System.err.println("\n------------------------------------------\n\n");
			children.forEach(c -> c.print());
		}

		/**
		 * Adds the contents of this {@link InheritanceTree} to the provided <code>list</code>.
		 * Contents are added in postorder which means that first this method is called recursively
		 * on all children and lastly the root of this {@link InheritanceTree} is added to the
		 * <code>list</code>.
		 * 
		 * @param list	the {@link List} to which the {@link ModelElement ModelElements} of this 
		 * 				{@link InheritanceTree} should be added
		 */
		public void getPostorder(List<ModelElement> list) {
			for (InheritanceTree<E> c : children) {
				c.getPostorder(list);
			}
			if (!isEmpty())
				list.add(root);
		}
		
		/**
		 * Create a new {@link InheritanceTree} for the provided <code>element</code>
		 * and adds it to this {@link InheritanceTree} as a child.
		 * 
		 * @param element	the element of type <code>E</code> to create a new
		 * 					{@link InheritanceTree} as a child for this {@link InheritanceTree} 
		 */
		public void add(E element) {
			InheritanceTree<E> r = new InheritanceTree<E>(element);
			children.add(r);
		}
		
		/**
		 * Returns whether the provided <code>element</code> is contained in this
		 * {@link InheritanceTree}.
		 * An element in contained in an {@link InheritanceTree} if it either is the root of
		 * the {@link InheritanceTree} or if it is contained in any of its children.
		 * 
		 * @param element	the object of type <code>E</code> that should be checked whether it is
		 * 					contained in this {@link InheritanceTree}
		 * @return	<code>true</code> if this {@link InheritanceTree} contains the provided
		 * 			<code>element</code>, <code>false</code> if not
		 */
		public boolean contains(E element) {
			return get(element) != null;
		}
		
		/**
		 * Returns the subtree that has <code>element</code> as its root.
		 * If no child of the {@link InheritanceTree} this method is called on possesses
		 * <code>element</code> as its root, <code>null</code> is returned instead.
		 * 
		 * @param element	the object of type <code>E</code> which should be the root of the
		 * 					returned {@link InheritanceTree}
		 * @return	the {@link InheritanceTree} that contains <code>element</code> as its root
		 * 			and is a subtree of the {@link InheritanceTree} the method has originally
		 * 			been called at. If no subtree satisfies this condition <code>null</code>
		 * 			is returned instead
		 */
		public InheritanceTree<E> get(E element) {
			if (isEmpty())
				return null;
			
			if (root.equals(element))
				return this;
		
			else {
				for (InheritanceTree<E> child : children) {
					InheritanceTree<E> childResult = child.get(element);
					if (childResult != null)
						return childResult;
				}
			}
			return null;
		}
		
		/**
		 * Returns whether this {@link InheritanceTree} possesses a root object or not
		 * 
		 * @return <code>true</code> if this {@link InheritanceTree} hold an object as its root,
		 * 			<code>false</code> if the root is <code>null</code>
		 */
		public boolean isEmpty() {
			return root == null;
		}
		
		/**
		 * Returns the root this {@link InheritanceTree} possesses.
		 * Maybe <code>null</code> if the {@link InheritanceTree} has been newly created.
		 * 
		 * @return	the object of type <code>E</code> that is the root of the
		 * 			{@link InheritanceTree} this method has been called at
		 * @see #isEmpty()
		 */
		public E getRoot() {
			return root;
		}
		
		/**
		 * Returns a {@link List} that contains all {@link InheritanceTree InheritanceTrees} that
		 * are a child of the tree this method has been called at
		 * 
		 * @return	a {@link List} that contains all {@link InheritanceTree InheritanceTrees} that
		 * are a child of the tree this method has been called at
		 */
		public List<InheritanceTree<E>> getChildren() {
			return children;
		}
	}
	
}
