package de.jabc.cinco.meta.runtime.xapi;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import graphmodel.Container;
import graphmodel.Edge;
import graphmodel.IdentifiableElement;
import graphmodel.ModelElement;
import graphmodel.ModelElementContainer;
import graphmodel.Node;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * GraphModel-specific extension methods.
 * 
 * @author Johannes Neubauer
 * @author Steve Bosselmann
 */
@SuppressWarnings("all")
public class GraphModelExtension {
  /**
   * Checks whether two identifiable elements are equal by comparing their IDs.
   * 
   * @param it the left hand side of the equals check.
   * @param that the right hand side of the equals check.
   * @return the result of the equals check.
   */
  public boolean operator_equals(final IdentifiableElement it, final IdentifiableElement that) {
    return ((it == that) || ((!(that == null)) && Objects.equal(it.getId(), that.getId())));
  }
  
  /**
   * Checks whether two identifiable elements are unequal by comparing their IDs.
   * 
   * @param it the left hand side of the unequal check.
   * @param that the right hand side of the unequal check.
   * @return the result of the unequal check.
   */
  public boolean operator_notEquals(final IdentifiableElement it, final IdentifiableElement that) {
    boolean _equals = this.operator_equals(it, that);
    return (!_equals);
  }
  
  /**
   * Gets the path to root for any model element.
   * 
   * @param el the model element.
   * @return the path to the root model element.
   */
  public Iterable<ModelElement> getPathToRoot(final ModelElement el) {
    Iterable<ModelElement> _xifexpression = null;
    ModelElementContainer _container = el.getContainer();
    if ((_container instanceof ModelElement)) {
      ModelElementContainer _container_1 = el.getContainer();
      Iterable<ModelElement> _pathToRoot = this.getPathToRoot(((ModelElement) _container_1));
      _xifexpression = Iterables.<ModelElement>concat(Collections.<ModelElement>unmodifiableList(CollectionLiterals.<ModelElement>newArrayList(el)), _pathToRoot);
    } else {
      _xifexpression = Collections.<ModelElement>unmodifiableList(CollectionLiterals.<ModelElement>newArrayList(el));
    }
    return _xifexpression;
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
  public <C extends IdentifiableElement> Iterable<C> find(final ModelElementContainer container, final Class<C> clazz) {
    Iterable<C> _xblockexpression = null;
    {
      final List<ModelElement> children = container.getModelElements();
      Iterable<C> _filter = Iterables.<C>filter(children, clazz);
      final Function1<ModelElementContainer, Iterable<C>> _function = new Function1<ModelElementContainer, Iterable<C>>() {
        @Override
        public Iterable<C> apply(final ModelElementContainer it) {
          return GraphModelExtension.this.<C>find(it, clazz);
        }
      };
      Iterable<C> _flatten = Iterables.<C>concat(IterableExtensions.<ModelElementContainer, Iterable<C>>map(Iterables.<ModelElementContainer>filter(children, ModelElementContainer.class), _function));
      _xblockexpression = Iterables.<C>concat(_filter, _flatten);
    }
    return _xblockexpression;
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
  public Iterable<? extends ModelElement> find(final ModelElementContainer container, final Class<? extends ModelElement>... classes) {
    Iterable<ModelElement> _xblockexpression = null;
    {
      final List<ModelElement> children = container.getModelElements();
      final Function1<ModelElement, Boolean> _function = new Function1<ModelElement, Boolean>() {
        @Override
        public Boolean apply(final ModelElement child) {
          final Function1<Class<? extends ModelElement>, Boolean> _function = new Function1<Class<? extends ModelElement>, Boolean>() {
            @Override
            public Boolean apply(final Class<? extends ModelElement> it) {
              return Boolean.valueOf(it.isInstance(child));
            }
          };
          return Boolean.valueOf(IterableExtensions.<Class<? extends ModelElement>>exists(((Iterable<Class<? extends ModelElement>>)Conversions.doWrapArray(classes)), _function));
        }
      };
      final Iterable<ModelElement> filtered = IterableExtensions.<ModelElement>filter(children, _function);
      final Function1<ModelElementContainer, Iterable<? extends ModelElement>> _function_1 = new Function1<ModelElementContainer, Iterable<? extends ModelElement>>() {
        @Override
        public Iterable<? extends ModelElement> apply(final ModelElementContainer it) {
          return GraphModelExtension.this.find(it, classes);
        }
      };
      Iterable<ModelElement> _flatten = Iterables.<ModelElement>concat(IterableExtensions.<ModelElementContainer, Iterable<? extends ModelElement>>map(Iterables.<ModelElementContainer>filter(children, ModelElementContainer.class), _function_1));
      _xblockexpression = Iterables.<ModelElement>concat(filtered, _flatten);
    }
    return _xblockexpression;
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
  public <C extends ModelElement> C findThe(final ModelElementContainer container, final Class<C> cls) {
    final Iterable<C> result = this.<C>find(container, cls);
    return IterableExtensions.<C>head(result);
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
  public <C extends ModelElement> C findThe(final ModelElementContainer container, final Class<C> cls, final Function1<? super C, ? extends Boolean> predicate) {
    final Iterable<C> result = IterableExtensions.<C>filter(this.<C>find(container, cls), ((Function1<? super C, Boolean>)predicate));
    return IterableExtensions.<C>head(result);
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
  public <T extends Object> Iterable<T> plus(final T a, final Iterable<? extends T> b) {
    return Iterables.<T>concat(Collections.<T>unmodifiableList(CollectionLiterals.<T>newArrayList(a)), b);
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
  public <T extends Object> Iterable<T> plus(final Iterable<? extends T> a, final T b) {
    return Iterables.<T>concat(a, Collections.<T>unmodifiableList(CollectionLiterals.<T>newArrayList(b)));
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
  public <C extends IdentifiableElement> Iterable<C> findDeeply(final ModelElementContainer container, final Class<C> clazz, final Function1<? super IdentifiableElement, ? extends ModelElementContainer> progression) {
    return this.<C>findDeeply_recurse(container, clazz, progression, CollectionLiterals.<ModelElementContainer>newHashSet());
  }
  
  /**
   * Cycle-aware recursion by applying the specified progression.
   */
  private <C extends IdentifiableElement> Iterable<C> findDeeply_recurse(final ModelElementContainer container, final Class<C> clazz, final Function1<? super IdentifiableElement, ? extends ModelElementContainer> progression, final Set<ModelElementContainer> visited) {
    Iterable<C> _xblockexpression = null;
    {
      boolean _add = visited.add(container);
      boolean _not = (!_add);
      if (_not) {
        return Collections.<C>unmodifiableList(CollectionLiterals.<C>newArrayList());
      }
      final Iterable<IdentifiableElement> candidates = this.<IdentifiableElement>plus(container, this.<IdentifiableElement>find(container, IdentifiableElement.class));
      Iterable<C> _filter = Iterables.<C>filter(candidates, clazz);
      final Function1<ModelElementContainer, Iterable<C>> _function = new Function1<ModelElementContainer, Iterable<C>>() {
        @Override
        public Iterable<C> apply(final ModelElementContainer it) {
          return GraphModelExtension.this.<C>findDeeply_recurse(it, clazz, progression, visited);
        }
      };
      Iterable<C> _flatten = Iterables.<C>concat(IterableExtensions.<ModelElementContainer, Iterable<C>>map(IterableExtensions.<ModelElementContainer>filterNull(IterableExtensions.<IdentifiableElement, ModelElementContainer>map(candidates, progression)), _function));
      _xblockexpression = Iterables.<C>concat(_filter, _flatten);
    }
    return _xblockexpression;
  }
  
  /**
   * Finds all parents of a model element of a specific type.
   * <br>The type of each element is matched to the specified type via {@code instanceof}.
   * 
   * @param element - The element for which to find the parent elements.
   * @param cls - The class of the parent elements to be found.
   * @return  All parents of the element of the specified type.
   */
  public <C extends ModelElement> Iterable<C> findParents(final ModelElement it, final Class<C> cls) {
    Iterable<C> _xifexpression = null;
    boolean _isInstance = cls.isInstance(it.getContainer());
    if (_isInstance) {
      ModelElementContainer _container = it.getContainer();
      ModelElementContainer _container_1 = it.getContainer();
      Iterable<C> _findParents = this.<C>findParents(((C) _container_1), cls);
      _xifexpression = Iterables.<C>concat(Collections.<C>unmodifiableList(CollectionLiterals.<C>newArrayList(((C) _container))), _findParents);
    } else {
      _xifexpression = Collections.<C>unmodifiableList(CollectionLiterals.<C>newArrayList());
    }
    return _xifexpression;
  }
  
  /**
   * Finds the first parent (bottom-up) of a model element of a specific type.
   * <br>The type of each element is matched to the specified type via {@code instanceof}.
   * 
   * @param element - The element for which to find the parent elements.
   * @param cls - The class of the parent elements to be found.
   * @return  The first parent of the specified type, or {@code null} if none exists.
   */
  public <C extends ModelElement> C findFirstParent(final ModelElement it, final Class<C> cls) {
    final Function1<C, Boolean> _function = new Function1<C, Boolean>() {
      @Override
      public Boolean apply(final C it) {
        return Boolean.valueOf(true);
      }
    };
    return this.<C>findFirstParent(it, cls, _function);
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
  public <C extends ModelElement> C findFirstParent(final ModelElement it, final Class<C> cls, final Function1<? super C, ? extends Boolean> predicate) {
    C _switchResult = null;
    ModelElementContainer _container = it.getContainer();
    boolean _matched = false;
    boolean _and = false;
    boolean _isInstance = cls.isInstance(it.getContainer());
    if (!_isInstance) {
      _and = false;
    } else {
      Boolean _apply = null;
      if (predicate!=null) {
        ModelElementContainer _container_1 = it.getContainer();
        _apply=predicate.apply(((C) _container_1));
      }
      _and = (_apply).booleanValue();
    }
    if (_and) {
      _matched=true;
      ModelElementContainer _container_2 = it.getContainer();
      _switchResult = ((C) _container_2);
    }
    if (!_matched) {
      if (_container instanceof ModelElement) {
        _matched=true;
        ModelElementContainer _container_3 = it.getContainer();
        _switchResult = this.<C>findFirstParent(((ModelElement) _container_3), cls, predicate);
      }
    }
    return _switchResult;
  }
  
  /**
   * Finds the last parent (bottom-up) of a model element of a specific type.
   * <br>The type of each element is matched to the specified type via {@code instanceof}.
   * 
   * @param element - The element for which to find the parent elements.
   * @param cls - The class of the parent elements to be found.
   * @return  The last parent of the specified type, or {@code null} if none exists.
   */
  public <C extends ModelElement> C findLastParent(final ModelElement it, final Class<C> cls) {
    final Function1<C, Boolean> _function = new Function1<C, Boolean>() {
      @Override
      public Boolean apply(final C it) {
        return Boolean.valueOf(true);
      }
    };
    return this.<C>findLastParent(it, cls, _function);
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
  public <C extends ModelElement> C findLastParent(final ModelElement it, final Class<C> cls, final Function1<? super C, ? extends Boolean> predicate) {
    return IterableExtensions.<C>last(IterableExtensions.<C>filter(this.<C>findParents(it, cls), ((Function1<? super C, Boolean>)predicate)));
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
  public <C extends ModelElement> boolean hasParent(final ModelElement it, final Class<C> cls) {
    C _findFirstParent = this.<C>findFirstParent(it, cls);
    return this.operator_notEquals(_findFirstParent, null);
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
  public <C extends Edge> Iterable<Node> findSourcesOf(final Node node, final Class<C> cls) {
    final Function1<C, Node> _function = new Function1<C, Node>() {
      @Override
      public Node apply(final C it) {
        return it.getSourceElement();
      }
    };
    return IterableExtensions.<Node>filterNull(ListExtensions.<C, Node>map(node.<C>getIncoming(cls), _function));
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
  public <C extends Edge> Node findSourceOf(final Node node, final Class<C> cls) {
    final List<C> result = node.<C>getIncoming(cls);
    final C edge = IterableExtensions.<C>head(result);
    Node _sourceElement = null;
    if (edge!=null) {
      _sourceElement=edge.getSourceElement();
    }
    return _sourceElement;
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
  public <C extends Edge> Node findSourceOf(final Node node, final Class<C> cls, final Function1<? super C, ? extends Boolean> predicate) {
    final Iterable<C> result = IterableExtensions.<C>filter(node.<C>getIncoming(cls), ((Function1<? super C, Boolean>)predicate));
    final C edge = IterableExtensions.<C>head(result);
    Node _sourceElement = null;
    if (edge!=null) {
      _sourceElement=edge.getSourceElement();
    }
    return _sourceElement;
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
  public <C extends Edge> Iterable<Node> findTargetsOf(final Node node, final Class<C> cls) {
    final Function1<C, Node> _function = new Function1<C, Node>() {
      @Override
      public Node apply(final C it) {
        return it.getTargetElement();
      }
    };
    return IterableExtensions.<Node>filterNull(ListExtensions.<C, Node>map(node.<C>getOutgoing(cls), _function));
  }
  
  /**
   * Retrieves all nodes that are reachable from the specified node by
   * following outgoing edges, recursively.
   * 
   * @param node - The node for which to retrieve successors.
   * @return An iterable of reachable nodes.
   */
  public Set<Node> findSuccessors(final Node node) {
    return this.findSuccessorsVia(node, Edge.class);
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
  public Set<Node> findSuccessorsVia(final Node node, final Class<? extends Edge>... classes) {
    return IterableExtensions.<Node>toSet(this.findSuccessorsVia_recurse(node, ((Iterable<Class<? extends Edge>>)Conversions.doWrapArray(classes)), CollectionLiterals.<Node>newHashSet()));
  }
  
  /**
   * Cycle-aware recursion by following outgoing edges matching the specified types.
   */
  private Iterable<Node> findSuccessorsVia_recurse(final Node node, final Iterable<Class<? extends Edge>> classes, final Set<Node> visited) {
    Iterable<Node> _xblockexpression = null;
    {
      boolean _add = visited.add(node);
      boolean _not = (!_add);
      if (_not) {
        return Collections.<Node>unmodifiableList(CollectionLiterals.<Node>newArrayList());
      }
      final Function1<Edge, Boolean> _function = new Function1<Edge, Boolean>() {
        @Override
        public Boolean apply(final Edge edge) {
          final Function1<Class<? extends Edge>, Boolean> _function = new Function1<Class<? extends Edge>, Boolean>() {
            @Override
            public Boolean apply(final Class<? extends Edge> cls) {
              boolean _isEmpty = IterableExtensions.isEmpty(Iterables.filter(Collections.<Edge>unmodifiableList(CollectionLiterals.<Edge>newArrayList(edge)), cls));
              return Boolean.valueOf((!_isEmpty));
            }
          };
          return Boolean.valueOf(IterableExtensions.<Class<? extends Edge>>exists(classes, _function));
        }
      };
      final Function1<Edge, Boolean> typecheck = _function;
      final Function1<Edge, Node> _function_1 = new Function1<Edge, Node>() {
        @Override
        public Node apply(final Edge it) {
          return it.getTargetElement();
        }
      };
      final Iterable<Node> successors = IterableExtensions.<Edge, Node>map(IterableExtensions.<Edge>filter(node.getOutgoing(), typecheck), _function_1);
      final Function1<Node, Iterable<Node>> _function_2 = new Function1<Node, Iterable<Node>>() {
        @Override
        public Iterable<Node> apply(final Node it) {
          return GraphModelExtension.this.findSuccessorsVia_recurse(it, classes, visited);
        }
      };
      Iterable<Node> _flatten = Iterables.<Node>concat(IterableExtensions.<Node, Iterable<Node>>map(successors, _function_2));
      _xblockexpression = Iterables.<Node>concat(successors, _flatten);
    }
    return _xblockexpression;
  }
  
  /**
   * Retrieves all nodes that are reachable from the specified node by
   * following incoming edges, recursively.
   * 
   * @param node - The node for which to retrieve predecessors.
   * @return An iterable of reachable nodes.
   */
  public Set<Node> findPredecessors(final Node node) {
    return this.findPredecessorsVia(node, Edge.class);
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
  public Set<Node> findPredecessorsVia(final Node node, final Class<? extends Edge>... classes) {
    return IterableExtensions.<Node>toSet(this.findPredecessorsVia_recurse(node, ((Iterable<Class<? extends Edge>>)Conversions.doWrapArray(classes)), CollectionLiterals.<Node>newHashSet()));
  }
  
  /**
   * Cycle-aware recursion by following incoming edges matching the specified types.
   */
  private Iterable<Node> findPredecessorsVia_recurse(final Node node, final Iterable<Class<? extends Edge>> classes, final Set<Node> visited) {
    Iterable<Node> _xblockexpression = null;
    {
      boolean _add = visited.add(node);
      boolean _not = (!_add);
      if (_not) {
        return Collections.<Node>unmodifiableList(CollectionLiterals.<Node>newArrayList());
      }
      final Function1<Edge, Boolean> _function = new Function1<Edge, Boolean>() {
        @Override
        public Boolean apply(final Edge edge) {
          final Function1<Class<? extends Edge>, Boolean> _function = new Function1<Class<? extends Edge>, Boolean>() {
            @Override
            public Boolean apply(final Class<? extends Edge> cls) {
              boolean _isEmpty = IterableExtensions.isEmpty(Iterables.filter(Collections.<Edge>unmodifiableList(CollectionLiterals.<Edge>newArrayList(edge)), cls));
              return Boolean.valueOf((!_isEmpty));
            }
          };
          return Boolean.valueOf(IterableExtensions.<Class<? extends Edge>>exists(classes, _function));
        }
      };
      final Function1<Edge, Boolean> typecheck = _function;
      final Function1<Edge, Node> _function_1 = new Function1<Edge, Node>() {
        @Override
        public Node apply(final Edge it) {
          return it.getSourceElement();
        }
      };
      final Iterable<Node> predecessors = IterableExtensions.<Edge, Node>map(IterableExtensions.<Edge>filter(node.getIncoming(), typecheck), _function_1);
      final Function1<Node, Iterable<Node>> _function_2 = new Function1<Node, Iterable<Node>>() {
        @Override
        public Iterable<Node> apply(final Node it) {
          return GraphModelExtension.this.findPredecessorsVia_recurse(it, classes, visited);
        }
      };
      Iterable<Node> _flatten = Iterables.<Node>concat(IterableExtensions.<Node, Iterable<Node>>map(predecessors, _function_2));
      _xblockexpression = Iterables.<Node>concat(predecessors, _flatten);
    }
    return _xblockexpression;
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
  public <C extends Edge> Node findTargetOf(final Node node, final Class<C> cls) {
    final List<C> result = node.<C>getOutgoing(cls);
    final C edge = IterableExtensions.<C>head(result);
    Node _targetElement = null;
    if (edge!=null) {
      _targetElement=edge.getTargetElement();
    }
    return _targetElement;
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
  public <C extends Edge> Node findTargetOf(final Node node, final Class<C> cls, final Function1<? super C, ? extends Boolean> predicate) {
    final Iterable<C> result = IterableExtensions.<C>filter(node.<C>getOutgoing(cls), ((Function1<? super C, Boolean>)predicate));
    final C edge = IterableExtensions.<C>head(result);
    Node _targetElement = null;
    if (edge!=null) {
      _targetElement=edge.getTargetElement();
    }
    return _targetElement;
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
  public boolean isInstanceOf(final Object obj, final Class<?> cls) {
    return ((obj != null) && 
      (Objects.equal(obj.getClass().getName(), cls.getSimpleName()) || cls.isInstance(obj)));
  }
  
  /**
   * Creates an {@link Iterable} containing the specified container as well as
   * its children.
   * 
   * @param container - A container.
   * @return  An {@link Iterable} containing the container as well as its children.
   */
  public Iterable<IdentifiableElement> withChildren(final ModelElementContainer container) {
    List<ModelElement> _modelElements = container.getModelElements();
    return Iterables.<IdentifiableElement>concat(Collections.<ModelElementContainer>unmodifiableList(CollectionLiterals.<ModelElementContainer>newArrayList(container)), _modelElements);
  }
  
  /**
   * Adds objects to an existing collection and returns it.
   * 
   * @param collection - A collection of objects.
   * @return  A collection containing the specified objects as well as
   *   the elements to be included.
   */
  public <C extends Collection<T>, T extends Object> C withAll(final C collection, final Iterable<T> toBeIncluded) {
    final Procedure1<C> _function = new Procedure1<C>() {
      @Override
      public void apply(final C it) {
        Iterables.<T>addAll(it, toBeIncluded);
      }
    };
    return ObjectExtensions.<C>operator_doubleArrow(collection, _function);
  }
  
  /**
   * Returns the Graphmodel of the given type
   * 
   * @param type - The type for which the graphmodel should be retrieved
   * @return The Graphmodel containing (transitive) the type
   */
  public ModelElementContainer getRootElement(final ModelElement type) {
    ModelElementContainer _xblockexpression = null;
    {
      ModelElementContainer container = type.getContainer();
      while ((this.operator_notEquals(container, null) && (container instanceof Container))) {
        container = ((Container) container).getContainer();
      }
      ModelElementContainer _xifexpression = null;
      boolean _equals = this.operator_equals(container, null);
      if (_equals) {
        _xifexpression = null;
      } else {
        _xifexpression = container;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
}
