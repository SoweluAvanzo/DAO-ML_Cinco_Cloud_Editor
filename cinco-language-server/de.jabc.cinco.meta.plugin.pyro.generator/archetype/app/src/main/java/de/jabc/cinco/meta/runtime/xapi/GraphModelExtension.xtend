package de.jabc.cinco.meta.runtime.xapi

import graphmodel.Edge
import graphmodel.IdentifiableElement
import graphmodel.ModelElement
import graphmodel.Container
import graphmodel.ModelElementContainer
import graphmodel.Node
import java.util.Collection
import java.util.Set

/**
 * GraphModel-specific extension methods.
 *
 * @author Johannes Neubauer
 * @author Steve Bosselmann
 */
class GraphModelExtension {


    /**
     * Checks whether two identifiable elements are equal by comparing their IDs.
     *
     * @param it the left hand side of the equals check.
     * @param that the right hand side of the equals check.
     * @return the result of the equals check.
     */
    def ==(IdentifiableElement it, IdentifiableElement that) {
        (it === that) || (!(that === null) && id == that.id)
    }

    /**
     * Checks whether two identifiable elements are unequal by comparing their IDs.
     *
     * @param it the left hand side of the unequal check.
     * @param that the right hand side of the unequal check.
     * @return the result of the unequal check.
     */
    def !=(IdentifiableElement it, IdentifiableElement that) { !(it == that) }

    /**
     * Gets the path to root for any model element.
     *
     * @param el the model element.
     * @return the path to the root model element.
     */
    def Iterable<ModelElement> getPathToRoot(ModelElement el) {
        if (el.container instanceof ModelElement)
            #[el] + (el.container as ModelElement).pathToRoot
        else #[el]
    }



    /**
     * Finds all elements of specific type inside the specified container.
     * <br>Recurses into all sub-containers.
     * <p>
     * Convenient method for {@code findDeeply(container, cls, [])}</p>
     *
     * @param container - The container holding the elements to be searched through.
     * @param cls - The class of the elements to be found.
     * @return  A set of elements of the specified type. Might be empty but is never null.
     */
    def <C extends IdentifiableElement> Iterable<C> find(ModelElementContainer container, Class<C> clazz) {
        val children = container.modelElements
        children.filter(clazz)
                + children.filter(ModelElementContainer)
        .map[find(clazz)].flatten
    }

    /**
     * Finds all elements of any of the specified types inside the specified container.
     * <br>Recurses into all sub-containers.
     * <p>
     * Convenient method for {@code findDeeply(container, classes, [])}</p>
     *
     * @param container - The container holding the elements to be searched through.
     * @param cls - The class of the elements to be found.
     * @return  A set of elements of the specified type. Might be empty but is never null.
     */
    def Iterable<? extends ModelElement> find(ModelElementContainer container, Class<? extends ModelElement>... classes) {
        val children = container.modelElements
        val filtered = children.filter[child | classes.exists[isInstance(child)]]
        filtered + children.filter(ModelElementContainer).map[find(classes)].flatten
    }

    /**
     * Finds the element of specific type inside the specified container.
     * <br>Recurses into all sub-containers.
     * <p>
     * Convenient method for {@code find(container, cls).head}</p>
     *
     * @param container - The container holding the elements to be searched through.
     * @param cls - The class of the elements to be found.
     * @return The element of the specified type, or {@code null} if none is found. If more
     *   than one element of the specified type is found, a warning is printed to the console
     *   and the first instance is returned.
     */
    def <C extends ModelElement> C findThe(ModelElementContainer container, Class<C> cls) {
        val result = find(container, cls)
        return result.head
    }

    /**
     * Finds the element of specific type inside the specified container that fulfills the
     * specified predicate.
     * <br>Recurses into all sub-containers.
     * <br>The type of each element is matched to the specified type via {@code instanceof}.
     * <p>
     * Convenient method for {@code find(container, cls).filter(predicate).head}</p>
     *
     * @param container - The container holding the elements to be searched through.
     * @param cls - The class of the elements to be found.
     * @param predicate - The predicate to be fulfilled.
     * @return The element of the specified type, or {@code null} if none is found. If more
     *   than one element of the specified type is found, a warning is printed to the console
     *   and the first instance is returned.
     */
    def <C extends ModelElement> C findThe(ModelElementContainer container, Class<C> cls, (C) => boolean predicate) {
        val result = find(container, cls).filter(predicate)
        return result.head
    }

    /**
	 * <p>
	 * Concatenates an object and an iterable into a single iterable. The returned
	 * iterable has an iterator that traverses the element {@code a}, followed by
	 * the elements in {@code b}. The resulting iterable is effectivly a view on the
	 * source iterable. That is, the source iterator is not polled until necessary
	 * and the result will reflect changes in the sources.
	 * </p>
	 * <p>
	 * The returned iterable's iterator supports {@code remove()} when the
	 * corresponding input iterator supports it.
	 * </p>
	 *
	 * @param a
	 *            the first element. May not be <code>null</code>.
	 * @param b
	 *            the iterable to append. May not be <code>null</code>.
	 * @return a combined iterable. Never <code>null</code>.
	 */
    def <T> Iterable<T> plus(T a, Iterable<? extends T> b) {
        #[a] + b
    }

    /**
     * <p>
     * Concatenates an iterable and an object into a single iterable. The returned
     * iterable has an iterator that traverses the elements in {@code a}, followed by
     * the element {@code b}. The resulting iterable is effectivly a view on the
     * source iterable. That is, the source iterator is not polled until necessary
     * and the result will reflect changes in the sources.
     * </p>
     * <p>
     * The returned iterable's iterator supports {@code remove()} when the
     * corresponding input iterator supports it.
     * </p>
     *
     * @param a
     *            the first iterable. May not be <code>null</code>.
     * @param b
     *            the element to append. May not be <code>null</code>.
     * @return a combined iterable. Never <code>null</code>.
     */
    def <T> Iterable<T> plus(Iterable<? extends T> a, T b) {
        a + #[b]
    }

    /**
     * Finds all elements of specific type inside the specified container.
     * <br>Recurses into all sub-containers.
     * <br>Recurses into sub-models, as specified via the {@code progression} parameter.
     * <br>The type of each element is matched to the specified type via {@code instanceof}.
     *
     * <br>The progression may be defined using a simple switch statement:
     * <pre>
     *   findDeeply(container, cls, [switch it {
     *     GUISIB: gui
     *     ProcessSIB: proMod
     *   }])
     * </pre>
     *
     * <br>Containers that are handled by the progression function will additionally be
     * searched through the conventional way, i.e. by looking at its children.
     *
     * @param container - The container holding the elements to be searched through.
     * @param cls - The class of the elements to be found.
     * @param progression  A function that defines how to dig deeper.
     * @return  A set of elements of the specified type. Might be empty but never null.
     */
    def <C extends IdentifiableElement> Iterable<C> findDeeply(ModelElementContainer container, Class<C> clazz, (IdentifiableElement) => ModelElementContainer progression) {
        findDeeply_recurse(container, clazz, progression, newHashSet)
    }

    /**
     * Cycle-aware recursion by applying the specified progression.
     */
    private def <C extends IdentifiableElement> Iterable<C> findDeeply_recurse(ModelElementContainer container, Class<C> clazz, (IdentifiableElement) => ModelElementContainer progression, Set<ModelElementContainer> visited) {
        if (!visited.add(container))
            return #[]
        val candidates = container.plus(container.find(IdentifiableElement))
        candidates.filter(clazz)
                + candidates
        .map(progression).filterNull
        .map[findDeeply_recurse(clazz, progression, visited)].flatten
    }

    /**
     * Finds all parents of a model element of a specific type.
     * <br>The type of each element is matched to the specified type via {@code instanceof}.
     *
     * @param element - The element for which to find the parent elements.
     * @param cls - The class of the parent elements to be found.
     * @return  All parents of the element of the specified type.
     */
    def <C extends ModelElement> Iterable<C> findParents(ModelElement it, Class<C> cls) {
        if (cls.isInstance(container))
            #[container as C] + (container as C).findParents(cls)
        else #[]
    }

    /**
     * Finds the first parent (bottom-up) of a model element of a specific type.
     * <br>The type of each element is matched to the specified type via {@code instanceof}.
     *
     * @param element - The element for which to find the parent elements.
     * @param cls - The class of the parent elements to be found.
     * @return  The first parent of the specified type, or {@code null} if none exists.
     */
    def <C extends ModelElement> C findFirstParent(ModelElement it, Class<C> cls) {
        findFirstParent(cls, [true])
    }

    /**
     * Finds the first parent (bottom-up) of a model element of a specific type that
     * fulfills the specified predicate.
     * <br>The type of each element is matched to the specified type via {@code instanceof}.
     *
     * @param element - The element for which to find the parent elements.
     * @param cls - The class of the parent elements to be found.
     * @param predicate - The predicate to be fulfilled.
     * @return  The first parent of the specified type that fulfills the specified
     *   predicate, or {@code null} if none exists.
     */
    def <C extends ModelElement> C findFirstParent(ModelElement it, Class<C> cls, (C) => boolean predicate) {
        switch container {
            case cls.isInstance(container)
                    && predicate?.apply(container as C): container as C
            ModelElement: (container as ModelElement).findFirstParent(cls, predicate)
        }
    }

    /**
     * Finds the last parent (bottom-up) of a model element of a specific type.
     * <br>The type of each element is matched to the specified type via {@code instanceof}.
     *
     * @param element - The element for which to find the parent elements.
     * @param cls - The class of the parent elements to be found.
     * @return  The last parent of the specified type, or {@code null} if none exists.
     */
    def <C extends ModelElement> C findLastParent(ModelElement it, Class<C> cls) {
        findLastParent(cls, [true])
    }

    /**
     * Finds the last parent (bottom-up) of a model element of a specific type that
     * fulfills the specified predicate.
     * <br>The type of each element is matched to the specified type via {@code instanceof}.
     * <p>
     * Convenient method for
     * {@code findParents(cls).filter(predicate).last}</p>
     *
     * @param element - The element for which to find the parent elements.
     * @param cls - The class of the parent elements to be found.
     * @param predicate - The predicate to be fulfilled.
     * @return  The last parent of the specified type that fulfills the specified
     *   predicate, or {@code null} if none exists.
     */
    def <C extends ModelElement> C findLastParent(ModelElement it, Class<C> cls, (C) => boolean predicate) {
        findParents(cls).filter(predicate).last
    }

    /**
     * Determines whether a model element has a parent of a specific type.
     * <br>The type of each element is matched to the specified type via {@code instanceof}.
     * <p>
     * Convenient method for {@code findFirstParent(cls) != null}</p>
     *
     * @param element - The element for which to find the parent element.
     * @param cls - The class of the parent element to be found.
     */
    def <C extends ModelElement> hasParent(ModelElement it, Class<C> cls) {
        findFirstParent(cls) != null
    }

    /**
     * Finds the source elements of all incoming edges of specific type.
     * <p>
     * Convenient method for
     * {@code getIncoming(cls).map[sourceElement].filterNull}
     *
     * @param node - The node for which to examine incoming edges.
     * @param cls - The class of the edges to be examined.
     * @return The source elements of the incoming edges of the specified type.
     */
    def <C extends Edge> findSourcesOf(Node node, Class<C> cls) {
        node.getIncoming(cls).map[sourceElement].filterNull
    }

    /**
     * Finds the source element of an incoming edge of specific type.
     * <p>
     * If more than one incoming edge of the specified type exists the
     * source element of the first of these edges is returned.
     * <p>
     * A warning is printed to the console if
     * <ul>
     * <li>more than one incoming edge of the specified type exists or
     * <li>the source element of the edge is {@code null}.
     * </ul>
     * <p>
     * Convenient method for {@code getIncoming(cls).head?.sourceElement}
     *
     * @param node - The node for which to examine incoming edges.
     * @param cls - The class of the edges to be examined.
     * @return The source element of the incoming edge of the specified type,
     *   or {@code null} if none such edge exists or the source element is
     *   {@code null}.
     */
    def <C extends Edge> Node findSourceOf(Node node, Class<C> cls) {
        val result = node.getIncoming(cls)
        val edge = result.head
        return edge?.sourceElement
    }

    /**
     * Finds the source element of an incoming edge of specific type
     * that fulfills the specified predicate.
     * <p>
     * If more than one matching edge exists the source element of the
     * first of these edges is returned.
     * <p>
     * A warning is printed to the console if
     * <ul>
     * <li>more than one matching edge exists or
     * <li>the source element of the edge is {@code null}.
     * </ul>
     * <p>
     * Convenient method for
     * {@code getIncoming(cls).filter(predicate).head?.sourceElement}
     *
     * @param node - The node for which to examine incoming edges.
     * @param cls - The class of the edges to be examined.
     * @param predicate - The predicate to be fulfilled.
     * @return The source element of the incoming edge of the specified type,
     *   or {@code null} if none such edge exists or the source element is
     *   {@code null}.
     */
    def <C extends Edge> Node findSourceOf(Node node, Class<C> cls, (C) => boolean predicate) {
        val result = node.getIncoming(cls).filter(predicate)
        val edge = result.head
        return edge?.sourceElement
    }

    /**
     * Finds the target elements of all outgoing edges of specific type.
     * <p>
     * Convenient method for
     * {@code getOutgoing(cls).map[targetElement].filterNull}
     *
     * @param node - The node for which to examine outgoing edges.
     * @param cls - The class of the edges to be examined.
     * @return The target elements of the outgoing edges of the specified type.
     */
    def <C extends Edge> findTargetsOf(Node node, Class<C> cls) {
        node.getOutgoing(cls).map[targetElement].filterNull
    }
	/**
	 * Retrieves all nodes that are reachable from the specified node by
	 * following outgoing edges, recursively.
	 * 
	 * @param node - The node for which to retrieve successors.
	 * @return An iterable of reachable nodes.
	 */
	def findSuccessors(Node node) {
		findSuccessorsVia(node, Edge)
	}
	
	/**
	 * Retrieves all nodes that are reachable from the specified node by
	 * following outgoing edges, recursively. Only those edges are respected
	 * that match any of the specified types.
	 * 
	 * @param node - The node for which to retrieve successors.
	 * @param classes - The list of edge types that should be considered only.
	 * @return An iterable of reachable nodes.
	 */
	def findSuccessorsVia(Node node, Class<? extends Edge>... classes) {
		findSuccessorsVia_recurse(node, classes, newHashSet).toSet
	}
	
	/**
	 * Cycle-aware recursion by following outgoing edges matching the specified types.
	 */
	private def Iterable<Node> findSuccessorsVia_recurse(Node node, Iterable<Class<? extends Edge>> classes, Set<Node> visited) {
		if (!visited.add(node))
			return #[]
		val typecheck = [Edge edge | classes.exists[cls | !#[edge].filter(cls).isEmpty] ]
		val successors = node.outgoing.filter(typecheck).map[targetElement]
		successors + successors.map[findSuccessorsVia_recurse(classes, visited)].flatten
	}
	
	/**
	 * Retrieves all nodes that are reachable from the specified node by
	 * following incoming edges, recursively.
	 * 
	 * @param node - The node for which to retrieve predecessors.
	 * @return An iterable of reachable nodes.
	 */
	def findPredecessors(Node node) {
		findPredecessorsVia(node, Edge)
	}
	
	/**
	 * Retrieves all nodes that are reachable from the specified node by
	 * following incoming edges, recursively. Only those edges are respected
	 * that match any of the specified types.
	 * 
	 * @param node - The node for which to retrieve predecessors.
	 * @param classes - The list of edge types that should be considered only.
	 * @return An iterable of reachable nodes.
	 */
	def findPredecessorsVia(Node node, Class<? extends Edge>... classes) {
		findPredecessorsVia_recurse(node, classes, newHashSet).toSet
	}
	
	/**
	 * Cycle-aware recursion by following incoming edges matching the specified types.
	 */
	private def Iterable<Node> findPredecessorsVia_recurse(Node node, Iterable<Class<? extends Edge>> classes, Set<Node> visited) {
		if (!visited.add(node))
			return #[]
		val typecheck = [Edge edge | classes.exists[cls | !#[edge].filter(cls).isEmpty] ]
		val predecessors = node.incoming.filter(typecheck).map[sourceElement]
		predecessors + predecessors.map[findPredecessorsVia_recurse(classes, visited)].flatten
	}

    /**
     * Retrieves the target element of an outgoing edge of specific type.
     * <p>
     * If more than one outgoing edge of the specified type exists the
     * target element of the first of these edges is returned.
     * <p>
     * A warning is printed to the console if
     * <ul>
     * <li>more than one outgoing edge of the specified type exists or
     * <li>the target element of the edge is {@code null}.
     * </ul>
     * <p>
     * Convenient method for {@code getOutgoing(cls).head?.targetElement}
     *
     * @param node - The node for which to examine outgoing edges.
     * @param cls - The class of the edges to be examined.
     * @return The target element of the outgoing edge of the specified type,
     *   or {@code null} if none such edge exists or the target element is
     *   {@code null}.
     */
    def <C extends Edge> Node findTargetOf(Node node, Class<C> cls) {
        val result = node.getOutgoing(cls)

        val edge = result.head

        return edge?.targetElement
    }

    /**
     * Retrieves the target element of an outgoing edge of specific type
     * that fulfills the specified predicate.
     * <p>
     * If more than one matching edge exists the target element of the
     * first of these edges is returned.
     * <p>
     * A warning is printed to the console if
     * <ul>
     * <li>more than one matching edge exists or
     * <li>the target element of the edge is {@code null}.
     * </ul>
     * <p>
     * Convenient method for
     * {@code getOutgoing(cls).filter(predicate).head?.targetElement}
     *
     * @param node - The node for which to examine outgoing edges.
     * @param cls - The class of the edges to be examined.
     * @param predicate - The predicate to be fulfilled.
     * @return The target element of the outgoing edge of the specified type,
     *   or {@code null} if none such edge exists or the target element is
     *   {@code null}.
     */
    def <C extends Edge> Node findTargetOf(Node node, Class<C> cls, (C) => boolean predicate) {
        val result = node.getOutgoing(cls).filter(predicate)

        val edge = result.head

        return edge?.targetElement
    }

    /**
     * A type-check via EObject-based reflection that compares the name of the
     * element's EClass as well as the name of all ESuperTypes with the name of
     * the specified EClass.
     *
     * @param obj - The object whose type should be checked.
     * @param cls - The class to be compared to the object's EClass.
     * @return  {@code true} if the object is instance of the class, {@code false}
     *  otherwise.
     */
    def isInstanceOf(Object obj, Class<? extends Object> cls) {


        obj !== null && (
                obj.class.name == cls.simpleName
                        || cls.isInstance(obj)
        )
    }

    /**
     * Creates an {@link Iterable} containing the specified container as well as
     * its children.
     *
     * @param container - A container.
     * @return  An {@link Iterable} containing the container as well as its children.
     */
    def withChildren(ModelElementContainer container) {
        #[container] + container.modelElements
    }

    /**
     * Adds objects to an existing collection and returns it.
     *
     * @param collection - A collection of objects.
     * @return  A collection containing the specified objects as well as
     *   the elements to be included.
     */
    def <C extends Collection<T>,T> withAll(C collection, Iterable<T> toBeIncluded) {
        collection => [ addAll(toBeIncluded) ]
    }

    /**
     * Returns the Graphmodel of the given type
     *
     * @param type - The type for which the graphmodel should be retrieved
     * @return The Graphmodel containing (transitive) the type
     */
    def getRootElement(ModelElement type) {
        var container = type.container
        while (container != null && (container instanceof Container)) {
            container = (container as Container).container
        }

        if (container == null) null else container
    }



}
