	package info.scce.cinco.product.primerefs.primerefs.util;
	
	import io.quarkus.hibernate.orm.panache.PanacheEntity;
	import info.scce.pyro.core.command.PrimeRefsCommandExecuter;
	
	public class TypeRegistry {
		
		public static String getTypeOf(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.primerefs.primerefs.SourceNode) {
				return "primerefs.SourceNode";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.SourceContainer) {
				return "primerefs.SourceContainer";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNode) {
				return "primerefs.PrimeToNode";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToEdge) {
				return "primerefs.PrimeToEdge";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToContainer) {
				return "primerefs.PrimeToContainer";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel) {
				return "primerefs.PrimeToGraphModel";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToNode) {
				return "primerefs.PrimeCToNode";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge) {
				return "primerefs.PrimeCToEdge";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer) {
				return "primerefs.PrimeCToContainer";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel) {
				return "primerefs.PrimeCToGraphModel";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy) {
				return "primerefs.PrimeToNodeHierarchy";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy) {
				return "primerefs.PrimeToAbstractNodeHierarchy";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow) {
				return "primerefs.PrimeToNodeFlow";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow) {
				return "primerefs.PrimeToEdgeFlow";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow) {
				return "primerefs.PrimeToContainerFlow";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow) {
				return "primerefs.PrimeToGraphModelFlow";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow) {
				return "primerefs.PrimeCToNodeFlow";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow) {
				return "primerefs.PrimeCToEdgeFlow";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow) {
				return "primerefs.PrimeCToContainerFlow";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow) {
				return "primerefs.PrimeCToGraphModelFlow";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.SourceEdge) {
				return "primerefs.SourceEdge";
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeRefs) {
				return "primerefs.PrimeRefs";
			}
			// prime referenced graph-models
			else if(
				(e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContA)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeA)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.D)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.Cont)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContD)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeD)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.A)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.TA)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.TD)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.Hierarchy)
			) {
				return info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry.getTypeOf(e);
			} else if(
				(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Start)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.End)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityA)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityB)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.ELibrary)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraph)
			) {
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.getTypeOf(e);
			}
			
			return null;
		}
		
		public static String getTypeOf(PanacheEntity e) {
			if(e instanceof entity.primerefs.SourceNodeDB) {
				return "primerefs.SourceNode";
			} else if(e instanceof entity.primerefs.SourceContainerDB) {
				return "primerefs.SourceContainer";
			} else if(e instanceof entity.primerefs.PrimeToNodeDB) {
				return "primerefs.PrimeToNode";
			} else if(e instanceof entity.primerefs.PrimeToEdgeDB) {
				return "primerefs.PrimeToEdge";
			} else if(e instanceof entity.primerefs.PrimeToContainerDB) {
				return "primerefs.PrimeToContainer";
			} else if(e instanceof entity.primerefs.PrimeToGraphModelDB) {
				return "primerefs.PrimeToGraphModel";
			} else if(e instanceof entity.primerefs.PrimeCToNodeDB) {
				return "primerefs.PrimeCToNode";
			} else if(e instanceof entity.primerefs.PrimeCToEdgeDB) {
				return "primerefs.PrimeCToEdge";
			} else if(e instanceof entity.primerefs.PrimeCToContainerDB) {
				return "primerefs.PrimeCToContainer";
			} else if(e instanceof entity.primerefs.PrimeCToGraphModelDB) {
				return "primerefs.PrimeCToGraphModel";
			} else if(e instanceof entity.primerefs.PrimeToNodeHierarchyDB) {
				return "primerefs.PrimeToNodeHierarchy";
			} else if(e instanceof entity.primerefs.PrimeToAbstractNodeHierarchyDB) {
				return "primerefs.PrimeToAbstractNodeHierarchy";
			} else if(e instanceof entity.primerefs.PrimeToNodeFlowDB) {
				return "primerefs.PrimeToNodeFlow";
			} else if(e instanceof entity.primerefs.PrimeToEdgeFlowDB) {
				return "primerefs.PrimeToEdgeFlow";
			} else if(e instanceof entity.primerefs.PrimeToContainerFlowDB) {
				return "primerefs.PrimeToContainerFlow";
			} else if(e instanceof entity.primerefs.PrimeToGraphModelFlowDB) {
				return "primerefs.PrimeToGraphModelFlow";
			} else if(e instanceof entity.primerefs.PrimeCToNodeFlowDB) {
				return "primerefs.PrimeCToNodeFlow";
			} else if(e instanceof entity.primerefs.PrimeCToEdgeFlowDB) {
				return "primerefs.PrimeCToEdgeFlow";
			} else if(e instanceof entity.primerefs.PrimeCToContainerFlowDB) {
				return "primerefs.PrimeCToContainerFlow";
			} else if(e instanceof entity.primerefs.PrimeCToGraphModelFlowDB) {
				return "primerefs.PrimeCToGraphModelFlow";
			} else if(e instanceof entity.primerefs.SourceEdgeDB) {
				return "primerefs.SourceEdge";
			} else if(e instanceof entity.primerefs.PrimeRefsDB) {
				return "primerefs.PrimeRefs";
			}
			// prime referenced graph-models
			else if(
				(e instanceof entity.hierarchy.ContADB)
				|| (e instanceof entity.hierarchy.EdgeADB)
				|| (e instanceof entity.hierarchy.DDB)
				|| (e instanceof entity.hierarchy.ContDB)
				|| (e instanceof entity.hierarchy.ContDDB)
				|| (e instanceof entity.hierarchy.EdgeDDB)
				|| (e instanceof entity.hierarchy.ADB)
				|| (e instanceof entity.hierarchy.TADB)
				|| (e instanceof entity.hierarchy.TDDB)
				|| (e instanceof entity.hierarchy.HierarchyDB)
			) {
				return info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry.getTypeOf(e);
			} else if(
				(e instanceof entity.flowgraph.StartDB)
				|| (e instanceof entity.flowgraph.EndDB)
				|| (e instanceof entity.flowgraph.ActivityDB)
				|| (e instanceof entity.flowgraph.EActivityADB)
				|| (e instanceof entity.flowgraph.EActivityBDB)
				|| (e instanceof entity.flowgraph.ELibraryDB)
				|| (e instanceof entity.flowgraph.SubFlowGraphDB)
				|| (e instanceof entity.flowgraph.SwimlaneDB)
				|| (e instanceof entity.flowgraph.TransitionDB)
				|| (e instanceof entity.flowgraph.LabeledTransitionDB)
				|| (e instanceof entity.flowgraph.FlowGraphDB)
			) {
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.getTypeOf(e);
			}
			
			return null;
		}
		
		public static String getTypeOf(info.scce.pyro.core.graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.pyro.primerefs.rest.SourceNode) {
				return "primerefs.SourceNode";
			} else if(e instanceof info.scce.pyro.primerefs.rest.SourceContainer) {
				return "primerefs.SourceContainer";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeToNode) {
				return "primerefs.PrimeToNode";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeToEdge) {
				return "primerefs.PrimeToEdge";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeToContainer) {
				return "primerefs.PrimeToContainer";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeToGraphModel) {
				return "primerefs.PrimeToGraphModel";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeCToNode) {
				return "primerefs.PrimeCToNode";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeCToEdge) {
				return "primerefs.PrimeCToEdge";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeCToContainer) {
				return "primerefs.PrimeCToContainer";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeCToGraphModel) {
				return "primerefs.PrimeCToGraphModel";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy) {
				return "primerefs.PrimeToNodeHierarchy";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy) {
				return "primerefs.PrimeToAbstractNodeHierarchy";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeToNodeFlow) {
				return "primerefs.PrimeToNodeFlow";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeToEdgeFlow) {
				return "primerefs.PrimeToEdgeFlow";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeToContainerFlow) {
				return "primerefs.PrimeToContainerFlow";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow) {
				return "primerefs.PrimeToGraphModelFlow";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeCToNodeFlow) {
				return "primerefs.PrimeCToNodeFlow";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow) {
				return "primerefs.PrimeCToEdgeFlow";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeCToContainerFlow) {
				return "primerefs.PrimeCToContainerFlow";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow) {
				return "primerefs.PrimeCToGraphModelFlow";
			} else if(e instanceof info.scce.pyro.primerefs.rest.SourceEdge) {
				return "primerefs.SourceEdge";
			} else if(e instanceof info.scce.pyro.primerefs.rest.PrimeRefs) {
				return "primerefs.PrimeRefs";
			}
			// prime referenced graph-models
			else if(
				(e instanceof info.scce.pyro.hierarchy.rest.ContA)
				|| (e instanceof info.scce.pyro.hierarchy.rest.EdgeA)
				|| (e instanceof info.scce.pyro.hierarchy.rest.D)
				|| (e instanceof info.scce.pyro.hierarchy.rest.Cont)
				|| (e instanceof info.scce.pyro.hierarchy.rest.ContD)
				|| (e instanceof info.scce.pyro.hierarchy.rest.EdgeD)
				|| (e instanceof info.scce.pyro.hierarchy.rest.A)
				|| (e instanceof info.scce.pyro.hierarchy.rest.TA)
				|| (e instanceof info.scce.pyro.hierarchy.rest.TD)
				|| (e instanceof info.scce.pyro.hierarchy.rest.Hierarchy)
			) {
				return info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry.getTypeOf(e);
			} else if(
				(e instanceof info.scce.pyro.flowgraph.rest.Start)
				|| (e instanceof info.scce.pyro.flowgraph.rest.End)
				|| (e instanceof info.scce.pyro.flowgraph.rest.Activity)
				|| (e instanceof info.scce.pyro.flowgraph.rest.EActivityA)
				|| (e instanceof info.scce.pyro.flowgraph.rest.EActivityB)
				|| (e instanceof info.scce.pyro.flowgraph.rest.ELibrary)
				|| (e instanceof info.scce.pyro.flowgraph.rest.SubFlowGraph)
				|| (e instanceof info.scce.pyro.flowgraph.rest.Swimlane)
				|| (e instanceof info.scce.pyro.flowgraph.rest.Transition)
				|| (e instanceof info.scce.pyro.flowgraph.rest.LabeledTransition)
				|| (e instanceof info.scce.pyro.flowgraph.rest.FlowGraph)
			) {
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.getTypeOf(e);
			}
			
			return null;
		}

		public static info.scce.pyro.core.graphmodel.IdentifiableElement getApiToRest(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.primerefs.primerefs.SourceNode) {
				info.scce.cinco.product.primerefs.primerefs.SourceNode apiE = (info.scce.cinco.product.primerefs.primerefs.SourceNode) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.SourceContainer) {
				info.scce.cinco.product.primerefs.primerefs.SourceContainer apiE = (info.scce.cinco.product.primerefs.primerefs.SourceContainer) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNode) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToNode apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToNode) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToEdge) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToEdge apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToEdge) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToContainer) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToContainer apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToContainer) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToNode) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToNode apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeCToNode) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.SourceEdge) {
				info.scce.cinco.product.primerefs.primerefs.SourceEdge apiE = (info.scce.cinco.product.primerefs.primerefs.SourceEdge) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeRefs) {
				info.scce.cinco.product.primerefs.primerefs.PrimeRefs apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeRefs) e;
				return getDBToRest(apiE.getDelegate());
			}
			// prime referenced graph-models
			else if(
				(e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContA)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContC)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeA)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeC)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.B)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.D)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.Cont)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContB)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContD)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeB)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeD)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.A)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.C)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.TA)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.TB)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.TC)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.TD)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.Hierarchy)
			) {
				return info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry.getApiToRest(e);
			} else if(
				(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Start)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.End)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityA)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityB)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.ELibrary)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraph)
			) {
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.getApiToRest(e);
			}
			
			return null;
		}

		public static info.scce.pyro.core.graphmodel.IdentifiableElement getDBToRest(PanacheEntity e) {
			return getDBToRest(e, new info.scce.pyro.rest.ObjectCache(), false);
		}
		
		public static info.scce.pyro.core.graphmodel.IdentifiableElement getDBToRest(PanacheEntity e, info.scce.pyro.rest.ObjectCache cache) {
			return getDBToRest(e, cache, false);
		}

		public static info.scce.pyro.core.graphmodel.IdentifiableElement getDBToRest(PanacheEntity e, info.scce.pyro.rest.ObjectCache cache, boolean onlyProperties) {
			if(e instanceof entity.primerefs.SourceNodeDB) {
				entity.primerefs.SourceNodeDB en = (entity.primerefs.SourceNodeDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.SourceNode.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.SourceNode.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.SourceContainerDB) {
				entity.primerefs.SourceContainerDB en = (entity.primerefs.SourceContainerDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.SourceContainer.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.SourceContainer.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeToNodeDB) {
				entity.primerefs.PrimeToNodeDB en = (entity.primerefs.PrimeToNodeDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeToNode.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeToNode.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeToEdgeDB) {
				entity.primerefs.PrimeToEdgeDB en = (entity.primerefs.PrimeToEdgeDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeToEdge.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeToEdge.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeToContainerDB) {
				entity.primerefs.PrimeToContainerDB en = (entity.primerefs.PrimeToContainerDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeToContainer.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeToContainer.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeToGraphModelDB) {
				entity.primerefs.PrimeToGraphModelDB en = (entity.primerefs.PrimeToGraphModelDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeToGraphModel.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeToGraphModel.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeCToNodeDB) {
				entity.primerefs.PrimeCToNodeDB en = (entity.primerefs.PrimeCToNodeDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeCToNode.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeCToNode.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeCToEdgeDB) {
				entity.primerefs.PrimeCToEdgeDB en = (entity.primerefs.PrimeCToEdgeDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeCToEdge.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeCToEdge.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeCToContainerDB) {
				entity.primerefs.PrimeCToContainerDB en = (entity.primerefs.PrimeCToContainerDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeCToContainer.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeCToContainer.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeCToGraphModelDB) {
				entity.primerefs.PrimeCToGraphModelDB en = (entity.primerefs.PrimeCToGraphModelDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeCToGraphModel.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeCToGraphModel.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeToNodeHierarchyDB) {
				entity.primerefs.PrimeToNodeHierarchyDB en = (entity.primerefs.PrimeToNodeHierarchyDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeToAbstractNodeHierarchyDB) {
				entity.primerefs.PrimeToAbstractNodeHierarchyDB en = (entity.primerefs.PrimeToAbstractNodeHierarchyDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeToNodeFlowDB) {
				entity.primerefs.PrimeToNodeFlowDB en = (entity.primerefs.PrimeToNodeFlowDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeToNodeFlow.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeToNodeFlow.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeToEdgeFlowDB) {
				entity.primerefs.PrimeToEdgeFlowDB en = (entity.primerefs.PrimeToEdgeFlowDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeToEdgeFlow.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeToEdgeFlow.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeToContainerFlowDB) {
				entity.primerefs.PrimeToContainerFlowDB en = (entity.primerefs.PrimeToContainerFlowDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeToContainerFlow.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeToContainerFlow.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeToGraphModelFlowDB) {
				entity.primerefs.PrimeToGraphModelFlowDB en = (entity.primerefs.PrimeToGraphModelFlowDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeCToNodeFlowDB) {
				entity.primerefs.PrimeCToNodeFlowDB en = (entity.primerefs.PrimeCToNodeFlowDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeCToNodeFlow.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeCToNodeFlow.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeCToEdgeFlowDB) {
				entity.primerefs.PrimeCToEdgeFlowDB en = (entity.primerefs.PrimeCToEdgeFlowDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeCToContainerFlowDB) {
				entity.primerefs.PrimeCToContainerFlowDB en = (entity.primerefs.PrimeCToContainerFlowDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeCToContainerFlow.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeCToContainerFlow.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeCToGraphModelFlowDB) {
				entity.primerefs.PrimeCToGraphModelFlowDB en = (entity.primerefs.PrimeCToGraphModelFlowDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.SourceEdgeDB) {
				entity.primerefs.SourceEdgeDB en = (entity.primerefs.SourceEdgeDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.SourceEdge.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.SourceEdge.fromEntity(en, cache);
				}
			} else if(e instanceof entity.primerefs.PrimeRefsDB) {
				entity.primerefs.PrimeRefsDB en = (entity.primerefs.PrimeRefsDB) e;
				if(onlyProperties) {
					return info.scce.pyro.primerefs.rest.PrimeRefs.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.primerefs.rest.PrimeRefs.fromEntity(en, cache);
				}
			}
			return getDBToRestPrime(e, cache, onlyProperties);
		}
		
		public static info.scce.pyro.core.graphmodel.IdentifiableElement getDBToRestPrime(PanacheEntity e, info.scce.pyro.rest.ObjectCache cache, boolean onlyProperties) {
			// prime referenced graph-models
			if(
				(e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContA)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContC)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeA)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeC)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.B)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.D)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.Cont)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContB)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContD)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeB)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeD)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.A)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.C)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.TA)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.TB)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.TC)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.TD)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.Hierarchy)
			) {
				return info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry.getDBToRest(e);
			} else if(
				(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Start)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.End)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityA)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityB)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.ELibrary)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraph)
			) {
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.getDBToRest(e);
			}
			return null;
		}

		public static PanacheEntity getApiToDB(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.primerefs.primerefs.SourceNode) {
				info.scce.cinco.product.primerefs.primerefs.SourceNode apiE = (info.scce.cinco.product.primerefs.primerefs.SourceNode) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.SourceContainer) {
				info.scce.cinco.product.primerefs.primerefs.SourceContainer apiE = (info.scce.cinco.product.primerefs.primerefs.SourceContainer) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNode) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToNode apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToNode) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToEdge) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToEdge apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToEdge) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToContainer) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToContainer apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToContainer) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToNode) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToNode apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeCToNode) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow) {
				info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.SourceEdge) {
				info.scce.cinco.product.primerefs.primerefs.SourceEdge apiE = (info.scce.cinco.product.primerefs.primerefs.SourceEdge) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.primerefs.primerefs.PrimeRefs) {
				info.scce.cinco.product.primerefs.primerefs.PrimeRefs apiE = (info.scce.cinco.product.primerefs.primerefs.PrimeRefs) e;
				return apiE.getDelegate();
			}
			// prime referenced graph-models
			else if(
				(e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContA)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContC)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeA)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeC)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.B)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.D)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.Cont)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContB)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContD)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeB)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeD)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.A)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.C)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.TA)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.TB)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.TC)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.TD)
				|| (e instanceof info.scce.cinco.product.hierarchy.hierarchy.Hierarchy)
			) {
				return info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry.getApiToDB(e);
			} else if(
				(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Start)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.End)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityA)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityB)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.ELibrary)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraph)
			) {
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.getApiToDB(e);
			}
			
			return null;
		}
		
		public static graphmodel.IdentifiableElement getDBToApi(
			PanacheEntity e,
			info.scce.pyro.core.command.PrimeRefsCommandExecuter executer
		) {
			return getDBToApi(e, executer, null, null);
		}
		
		public static graphmodel.IdentifiableElement getDBToApi(
			PanacheEntity e,
			PrimeRefsCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			if(e instanceof entity.primerefs.SourceNodeDB) {
				entity.primerefs.SourceNodeDB en = (entity.primerefs.SourceNodeDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.SourceNodeImpl(en, executer);
			} else if(e instanceof entity.primerefs.SourceContainerDB) {
				entity.primerefs.SourceContainerDB en = (entity.primerefs.SourceContainerDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.SourceContainerImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeToNodeDB) {
				entity.primerefs.PrimeToNodeDB en = (entity.primerefs.PrimeToNodeDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToNodeImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeToEdgeDB) {
				entity.primerefs.PrimeToEdgeDB en = (entity.primerefs.PrimeToEdgeDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToEdgeImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeToContainerDB) {
				entity.primerefs.PrimeToContainerDB en = (entity.primerefs.PrimeToContainerDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToContainerImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeToGraphModelDB) {
				entity.primerefs.PrimeToGraphModelDB en = (entity.primerefs.PrimeToGraphModelDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToGraphModelImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeCToNodeDB) {
				entity.primerefs.PrimeCToNodeDB en = (entity.primerefs.PrimeCToNodeDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToNodeImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeCToEdgeDB) {
				entity.primerefs.PrimeCToEdgeDB en = (entity.primerefs.PrimeCToEdgeDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToEdgeImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeCToContainerDB) {
				entity.primerefs.PrimeCToContainerDB en = (entity.primerefs.PrimeCToContainerDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToContainerImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeCToGraphModelDB) {
				entity.primerefs.PrimeCToGraphModelDB en = (entity.primerefs.PrimeCToGraphModelDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToGraphModelImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeToNodeHierarchyDB) {
				entity.primerefs.PrimeToNodeHierarchyDB en = (entity.primerefs.PrimeToNodeHierarchyDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToNodeHierarchyImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeToAbstractNodeHierarchyDB) {
				entity.primerefs.PrimeToAbstractNodeHierarchyDB en = (entity.primerefs.PrimeToAbstractNodeHierarchyDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToAbstractNodeHierarchyImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeToNodeFlowDB) {
				entity.primerefs.PrimeToNodeFlowDB en = (entity.primerefs.PrimeToNodeFlowDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToNodeFlowImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeToEdgeFlowDB) {
				entity.primerefs.PrimeToEdgeFlowDB en = (entity.primerefs.PrimeToEdgeFlowDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToEdgeFlowImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeToContainerFlowDB) {
				entity.primerefs.PrimeToContainerFlowDB en = (entity.primerefs.PrimeToContainerFlowDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToContainerFlowImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeToGraphModelFlowDB) {
				entity.primerefs.PrimeToGraphModelFlowDB en = (entity.primerefs.PrimeToGraphModelFlowDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToGraphModelFlowImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeCToNodeFlowDB) {
				entity.primerefs.PrimeCToNodeFlowDB en = (entity.primerefs.PrimeCToNodeFlowDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToNodeFlowImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeCToEdgeFlowDB) {
				entity.primerefs.PrimeCToEdgeFlowDB en = (entity.primerefs.PrimeCToEdgeFlowDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToEdgeFlowImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeCToContainerFlowDB) {
				entity.primerefs.PrimeCToContainerFlowDB en = (entity.primerefs.PrimeCToContainerFlowDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToContainerFlowImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeCToGraphModelFlowDB) {
				entity.primerefs.PrimeCToGraphModelFlowDB en = (entity.primerefs.PrimeCToGraphModelFlowDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToGraphModelFlowImpl(en, executer);
			} else if(e instanceof entity.primerefs.SourceEdgeDB) {
				entity.primerefs.SourceEdgeDB en = (entity.primerefs.SourceEdgeDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.SourceEdgeImpl(en, executer);
			} else if(e instanceof entity.primerefs.PrimeRefsDB) {
				entity.primerefs.PrimeRefsDB en = (entity.primerefs.PrimeRefsDB) e;
				return new info.scce.cinco.product.primerefs.primerefs.impl.PrimeRefsImpl(en, executer);
			}
			return getDBToApiPrime(e, executer, parent, prev);
		}
		
		public static graphmodel.IdentifiableElement getDBToApiPrime(
			PanacheEntity e,
			PrimeRefsCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			// prime referenced graph-models
			if(
				(e instanceof entity.hierarchy.ContADB)
				|| (e instanceof entity.hierarchy.EdgeADB)
				|| (e instanceof entity.hierarchy.DDB)
				|| (e instanceof entity.hierarchy.ContDB)
				|| (e instanceof entity.hierarchy.ContDDB)
				|| (e instanceof entity.hierarchy.EdgeDDB)
				|| (e instanceof entity.hierarchy.ADB)
				|| (e instanceof entity.hierarchy.TADB)
				|| (e instanceof entity.hierarchy.TDDB)
				|| (e instanceof entity.hierarchy.HierarchyDB)
			) {
				info.scce.pyro.core.command.HierarchyCommandExecuter HierarchyCommandExecuter = executer.getHierarchyCommandExecuter();
				return info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry.getDBToApi(e, HierarchyCommandExecuter, parent, prev);
			} else if(
				(e instanceof entity.flowgraph.StartDB)
				|| (e instanceof entity.flowgraph.EndDB)
				|| (e instanceof entity.flowgraph.ActivityDB)
				|| (e instanceof entity.flowgraph.EActivityADB)
				|| (e instanceof entity.flowgraph.EActivityBDB)
				|| (e instanceof entity.flowgraph.ELibraryDB)
				|| (e instanceof entity.flowgraph.SubFlowGraphDB)
				|| (e instanceof entity.flowgraph.SwimlaneDB)
				|| (e instanceof entity.flowgraph.TransitionDB)
				|| (e instanceof entity.flowgraph.LabeledTransitionDB)
				|| (e instanceof entity.flowgraph.FlowGraphDB)
			) {
				info.scce.pyro.core.command.FlowGraphCommandExecuter FlowGraphCommandExecuter = executer.getFlowGraphCommandExecuter();
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.getDBToApi(e, FlowGraphCommandExecuter, parent, prev);
			}
			return null;
		}
		
		public static PanacheEntity findAbstract(long id, Class<?> c) {
			if(graphmodel.IdentifiableElement.class.isAssignableFrom(c)) {
				return findAbstractEntityByApi(id, c);
			} else if (info.scce.pyro.core.graphmodel.IdentifiableElement.class.isAssignableFrom(c)){
				return findAbstractEntityByRest(id, c);
			} else if (PanacheEntity.class.isAssignableFrom(c)){
				return findAbstractEntityByEntity(id, c);
			}
			return null;
		}
		
		public static PanacheEntity findAbstractEntityByApi(long id, Class<?> entityClass) {
			if(info.scce.cinco.product.primerefs.primerefs.SourceNode.class.equals(entityClass)) {
				return entity.primerefs.SourceNodeDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.SourceContainer.class.equals(entityClass)) {
				return entity.primerefs.SourceContainerDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeToNode.class.equals(entityClass)) {
				return entity.primerefs.PrimeToNodeDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeToEdge.class.equals(entityClass)) {
				return entity.primerefs.PrimeToEdgeDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeToContainer.class.equals(entityClass)) {
				return entity.primerefs.PrimeToContainerDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel.class.equals(entityClass)) {
				return entity.primerefs.PrimeToGraphModelDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeCToNode.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToNodeDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToEdgeDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToContainerDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToGraphModelDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy.class.equals(entityClass)) {
				return entity.primerefs.PrimeToNodeHierarchyDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy.class.equals(entityClass)) {
				return entity.primerefs.PrimeToAbstractNodeHierarchyDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow.class.equals(entityClass)) {
				return entity.primerefs.PrimeToNodeFlowDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow.class.equals(entityClass)) {
				return entity.primerefs.PrimeToEdgeFlowDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow.class.equals(entityClass)) {
				return entity.primerefs.PrimeToContainerFlowDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow.class.equals(entityClass)) {
				return entity.primerefs.PrimeToGraphModelFlowDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToNodeFlowDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToEdgeFlowDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToContainerFlowDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToGraphModelFlowDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.SourceEdge.class.equals(entityClass)) {
				return entity.primerefs.SourceEdgeDB.findById(id);
			} else if(info.scce.cinco.product.primerefs.primerefs.PrimeRefs.class.equals(entityClass)) {
				return entity.primerefs.PrimeRefsDB.findById(id);
			}
			// prime referenced graph-models
			else if(
				info.scce.cinco.product.hierarchy.hierarchy.ContA.class.equals(entityClass)
				|| info.scce.cinco.product.hierarchy.hierarchy.ContC.class.equals(entityClass)
				|| info.scce.cinco.product.hierarchy.hierarchy.EdgeA.class.equals(entityClass)
				|| info.scce.cinco.product.hierarchy.hierarchy.EdgeC.class.equals(entityClass)
				|| info.scce.cinco.product.hierarchy.hierarchy.B.class.equals(entityClass)
				|| info.scce.cinco.product.hierarchy.hierarchy.D.class.equals(entityClass)
				|| info.scce.cinco.product.hierarchy.hierarchy.Cont.class.equals(entityClass)
				|| info.scce.cinco.product.hierarchy.hierarchy.ContB.class.equals(entityClass)
				|| info.scce.cinco.product.hierarchy.hierarchy.ContD.class.equals(entityClass)
				|| info.scce.cinco.product.hierarchy.hierarchy.EdgeB.class.equals(entityClass)
				|| info.scce.cinco.product.hierarchy.hierarchy.EdgeD.class.equals(entityClass)
				|| info.scce.cinco.product.hierarchy.hierarchy.A.class.equals(entityClass)
				|| info.scce.cinco.product.hierarchy.hierarchy.C.class.equals(entityClass)
				|| info.scce.cinco.product.hierarchy.hierarchy.TA.class.equals(entityClass)
				|| info.scce.cinco.product.hierarchy.hierarchy.TB.class.equals(entityClass)
				|| info.scce.cinco.product.hierarchy.hierarchy.TC.class.equals(entityClass)
				|| info.scce.cinco.product.hierarchy.hierarchy.TD.class.equals(entityClass)
				|| info.scce.cinco.product.hierarchy.hierarchy.Hierarchy.class.equals(entityClass)
			) {
				return info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry.findAbstractEntityByApi(id, entityClass);
			} else if(
				info.scce.cinco.product.flowgraph.flowgraph.Start.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.End.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.Activity.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.EActivityA.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.EActivityB.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.ELibrary.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.Swimlane.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.Transition.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.FlowGraph.class.equals(entityClass)
			) {
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.findAbstractEntityByApi(id, entityClass);
			}
			
			return null;
		}
		
		public static PanacheEntity findAbstractEntityByRest(long id, Class<?> entityClass) {
			if(info.scce.pyro.primerefs.rest.SourceNode.class.equals(entityClass)) {
				return entity.primerefs.SourceNodeDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.SourceContainer.class.equals(entityClass)) {
				return entity.primerefs.SourceContainerDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeToNode.class.equals(entityClass)) {
				return entity.primerefs.PrimeToNodeDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeToEdge.class.equals(entityClass)) {
				return entity.primerefs.PrimeToEdgeDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeToContainer.class.equals(entityClass)) {
				return entity.primerefs.PrimeToContainerDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeToGraphModel.class.equals(entityClass)) {
				return entity.primerefs.PrimeToGraphModelDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeCToNode.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToNodeDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeCToEdge.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToEdgeDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeCToContainer.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToContainerDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeCToGraphModel.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToGraphModelDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy.class.equals(entityClass)) {
				return entity.primerefs.PrimeToNodeHierarchyDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy.class.equals(entityClass)) {
				return entity.primerefs.PrimeToAbstractNodeHierarchyDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeToNodeFlow.class.equals(entityClass)) {
				return entity.primerefs.PrimeToNodeFlowDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeToEdgeFlow.class.equals(entityClass)) {
				return entity.primerefs.PrimeToEdgeFlowDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeToContainerFlow.class.equals(entityClass)) {
				return entity.primerefs.PrimeToContainerFlowDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow.class.equals(entityClass)) {
				return entity.primerefs.PrimeToGraphModelFlowDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeCToNodeFlow.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToNodeFlowDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToEdgeFlowDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeCToContainerFlow.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToContainerFlowDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToGraphModelFlowDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.SourceEdge.class.equals(entityClass)) {
				return entity.primerefs.SourceEdgeDB.findById(id);
			} else if(info.scce.pyro.primerefs.rest.PrimeRefs.class.equals(entityClass)) {
				return entity.primerefs.PrimeRefsDB.findById(id);
			}
			// prime referenced graph-models
			else if(
				info.scce.pyro.hierarchy.rest.ContA.class.equals(entityClass)
				|| info.scce.pyro.hierarchy.rest.ContC.class.equals(entityClass)
				|| info.scce.pyro.hierarchy.rest.EdgeA.class.equals(entityClass)
				|| info.scce.pyro.hierarchy.rest.EdgeC.class.equals(entityClass)
				|| info.scce.pyro.hierarchy.rest.B.class.equals(entityClass)
				|| info.scce.pyro.hierarchy.rest.D.class.equals(entityClass)
				|| info.scce.pyro.hierarchy.rest.Cont.class.equals(entityClass)
				|| info.scce.pyro.hierarchy.rest.ContB.class.equals(entityClass)
				|| info.scce.pyro.hierarchy.rest.ContD.class.equals(entityClass)
				|| info.scce.pyro.hierarchy.rest.EdgeB.class.equals(entityClass)
				|| info.scce.pyro.hierarchy.rest.EdgeD.class.equals(entityClass)
				|| info.scce.pyro.hierarchy.rest.A.class.equals(entityClass)
				|| info.scce.pyro.hierarchy.rest.C.class.equals(entityClass)
				|| info.scce.pyro.hierarchy.rest.TA.class.equals(entityClass)
				|| info.scce.pyro.hierarchy.rest.TB.class.equals(entityClass)
				|| info.scce.pyro.hierarchy.rest.TC.class.equals(entityClass)
				|| info.scce.pyro.hierarchy.rest.TD.class.equals(entityClass)
				|| info.scce.pyro.hierarchy.rest.Hierarchy.class.equals(entityClass)
			) {
				return info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry.findAbstractEntityByRest(id, entityClass);
			} else if(
				info.scce.pyro.flowgraph.rest.Start.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.End.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.Activity.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.EActivityA.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.EActivityB.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.ELibrary.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.SubFlowGraph.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.Swimlane.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.Transition.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.LabeledTransition.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.FlowGraph.class.equals(entityClass)
			) {
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.findAbstractEntityByRest(id, entityClass);
			}
			
			return null;
		}
		
		public static PanacheEntity findAbstractEntityByEntity(long id, Class<?> entityClass) {
			if(entity.primerefs.SourceNodeDB.class.equals(entityClass)) {
				return entity.primerefs.SourceNodeDB.findById(id);
			} else if(entity.primerefs.SourceContainerDB.class.equals(entityClass)) {
				return entity.primerefs.SourceContainerDB.findById(id);
			} else if(entity.primerefs.PrimeToNodeDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeToNodeDB.findById(id);
			} else if(entity.primerefs.PrimeToEdgeDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeToEdgeDB.findById(id);
			} else if(entity.primerefs.PrimeToContainerDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeToContainerDB.findById(id);
			} else if(entity.primerefs.PrimeToGraphModelDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeToGraphModelDB.findById(id);
			} else if(entity.primerefs.PrimeCToNodeDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToNodeDB.findById(id);
			} else if(entity.primerefs.PrimeCToEdgeDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToEdgeDB.findById(id);
			} else if(entity.primerefs.PrimeCToContainerDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToContainerDB.findById(id);
			} else if(entity.primerefs.PrimeCToGraphModelDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToGraphModelDB.findById(id);
			} else if(entity.primerefs.PrimeToNodeHierarchyDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeToNodeHierarchyDB.findById(id);
			} else if(entity.primerefs.PrimeToAbstractNodeHierarchyDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeToAbstractNodeHierarchyDB.findById(id);
			} else if(entity.primerefs.PrimeToNodeFlowDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeToNodeFlowDB.findById(id);
			} else if(entity.primerefs.PrimeToEdgeFlowDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeToEdgeFlowDB.findById(id);
			} else if(entity.primerefs.PrimeToContainerFlowDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeToContainerFlowDB.findById(id);
			} else if(entity.primerefs.PrimeToGraphModelFlowDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeToGraphModelFlowDB.findById(id);
			} else if(entity.primerefs.PrimeCToNodeFlowDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToNodeFlowDB.findById(id);
			} else if(entity.primerefs.PrimeCToEdgeFlowDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToEdgeFlowDB.findById(id);
			} else if(entity.primerefs.PrimeCToContainerFlowDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToContainerFlowDB.findById(id);
			} else if(entity.primerefs.PrimeCToGraphModelFlowDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeCToGraphModelFlowDB.findById(id);
			} else if(entity.primerefs.SourceEdgeDB.class.equals(entityClass)) {
				return entity.primerefs.SourceEdgeDB.findById(id);
			} else if(entity.primerefs.PrimeRefsDB.class.equals(entityClass)) {
				return entity.primerefs.PrimeRefsDB.findById(id);
			}
			// prime referenced graph-models
			else if(
				entity.hierarchy.ContADB.class.equals(entityClass)
				|| entity.hierarchy.EdgeADB.class.equals(entityClass)
				|| entity.hierarchy.DDB.class.equals(entityClass)
				|| entity.hierarchy.ContDB.class.equals(entityClass)
				|| entity.hierarchy.ContDDB.class.equals(entityClass)
				|| entity.hierarchy.EdgeDDB.class.equals(entityClass)
				|| entity.hierarchy.ADB.class.equals(entityClass)
				|| entity.hierarchy.TADB.class.equals(entityClass)
				|| entity.hierarchy.TDDB.class.equals(entityClass)
				|| entity.hierarchy.HierarchyDB.class.equals(entityClass)
			) {
				return info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry.findAbstractEntityByEntity(id, entityClass);
			} else if(
				entity.flowgraph.StartDB.class.equals(entityClass)
				|| entity.flowgraph.EndDB.class.equals(entityClass)
				|| entity.flowgraph.ActivityDB.class.equals(entityClass)
				|| entity.flowgraph.EActivityADB.class.equals(entityClass)
				|| entity.flowgraph.EActivityBDB.class.equals(entityClass)
				|| entity.flowgraph.ELibraryDB.class.equals(entityClass)
				|| entity.flowgraph.SubFlowGraphDB.class.equals(entityClass)
				|| entity.flowgraph.SwimlaneDB.class.equals(entityClass)
				|| entity.flowgraph.TransitionDB.class.equals(entityClass)
				|| entity.flowgraph.LabeledTransitionDB.class.equals(entityClass)
				|| entity.flowgraph.FlowGraphDB.class.equals(entityClass)
			) {
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.findAbstractEntityByEntity(id, entityClass);
			}
			
			return null;
		}
		
		/**
		 * This should only be used if no other information can be derived or resolved!
		 * It iterates over all type-tables of the GraphModel with bruteforce and tries
		 * to find the associated entity by the given id.
		 *
		 * (utilizing this method is bad style!)
		 */
		public static PanacheEntity findById(long id) {
			PanacheEntity found = null;
			try {
				found = entity.primerefs.SourceNodeDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.SourceNodeDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.SourceContainerDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.SourceContainerDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeToNodeDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeToNodeDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeToEdgeDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeToEdgeDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeToContainerDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeToContainerDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeToGraphModelDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeToGraphModelDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeCToNodeDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeCToNodeDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeCToEdgeDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeCToEdgeDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeCToContainerDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeCToContainerDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeCToGraphModelDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeCToGraphModelDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeToNodeHierarchyDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeToNodeHierarchyDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeToAbstractNodeHierarchyDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeToAbstractNodeHierarchyDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeToNodeFlowDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeToNodeFlowDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeToEdgeFlowDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeToEdgeFlowDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeToContainerFlowDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeToContainerFlowDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeToGraphModelFlowDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeToGraphModelFlowDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeCToNodeFlowDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeCToNodeFlowDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeCToEdgeFlowDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeCToEdgeFlowDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeCToContainerFlowDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeCToContainerFlowDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeCToGraphModelFlowDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeCToGraphModelFlowDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.SourceEdgeDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.SourceEdgeDB...");
				e.printStackTrace();
			}
			try {
				found = entity.primerefs.PrimeRefsDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.primerefs.PrimeRefsDB...");
				e.printStackTrace();
			}
			
			return null;
		}
		
		public static PanacheEntity findByType(String type, long id) {
			if(type.equals("primerefs.SourceNode") ){
				return entity.primerefs.SourceNodeDB.findById(id);
			} else if(type.equals("primerefs.SourceContainer") ){
				return entity.primerefs.SourceContainerDB.findById(id);
			} else if(type.equals("primerefs.PrimeToNode") ){
				return entity.primerefs.PrimeToNodeDB.findById(id);
			} else if(type.equals("primerefs.PrimeToEdge") ){
				return entity.primerefs.PrimeToEdgeDB.findById(id);
			} else if(type.equals("primerefs.PrimeToContainer") ){
				return entity.primerefs.PrimeToContainerDB.findById(id);
			} else if(type.equals("primerefs.PrimeToGraphModel") ){
				return entity.primerefs.PrimeToGraphModelDB.findById(id);
			} else if(type.equals("primerefs.PrimeCToNode") ){
				return entity.primerefs.PrimeCToNodeDB.findById(id);
			} else if(type.equals("primerefs.PrimeCToEdge") ){
				return entity.primerefs.PrimeCToEdgeDB.findById(id);
			} else if(type.equals("primerefs.PrimeCToContainer") ){
				return entity.primerefs.PrimeCToContainerDB.findById(id);
			} else if(type.equals("primerefs.PrimeCToGraphModel") ){
				return entity.primerefs.PrimeCToGraphModelDB.findById(id);
			} else if(type.equals("primerefs.PrimeToNodeHierarchy") ){
				return entity.primerefs.PrimeToNodeHierarchyDB.findById(id);
			} else if(type.equals("primerefs.PrimeToAbstractNodeHierarchy") ){
				return entity.primerefs.PrimeToAbstractNodeHierarchyDB.findById(id);
			} else if(type.equals("primerefs.PrimeToNodeFlow") ){
				return entity.primerefs.PrimeToNodeFlowDB.findById(id);
			} else if(type.equals("primerefs.PrimeToEdgeFlow") ){
				return entity.primerefs.PrimeToEdgeFlowDB.findById(id);
			} else if(type.equals("primerefs.PrimeToContainerFlow") ){
				return entity.primerefs.PrimeToContainerFlowDB.findById(id);
			} else if(type.equals("primerefs.PrimeToGraphModelFlow") ){
				return entity.primerefs.PrimeToGraphModelFlowDB.findById(id);
			} else if(type.equals("primerefs.PrimeCToNodeFlow") ){
				return entity.primerefs.PrimeCToNodeFlowDB.findById(id);
			} else if(type.equals("primerefs.PrimeCToEdgeFlow") ){
				return entity.primerefs.PrimeCToEdgeFlowDB.findById(id);
			} else if(type.equals("primerefs.PrimeCToContainerFlow") ){
				return entity.primerefs.PrimeCToContainerFlowDB.findById(id);
			} else if(type.equals("primerefs.PrimeCToGraphModelFlow") ){
				return entity.primerefs.PrimeCToGraphModelFlowDB.findById(id);
			} else if(type.equals("primerefs.SourceEdge") ){
				return entity.primerefs.SourceEdgeDB.findById(id);
			} else if(type.equals("primerefs.PrimeRefs") ){
				return entity.primerefs.PrimeRefsDB.findById(id);
			}
			
			return null;
		}
		
		public static graphmodel.IdentifiableElement findApiByType(
			String type,
			long id,
			info.scce.pyro.core.command.PrimeRefsCommandExecuter executer
		) {
			return findApiByType(type, id, executer, null, null);
		}
		
		public static graphmodel.IdentifiableElement findApiByType(
			String type,
			long id,
			info.scce.pyro.core.command.PrimeRefsCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			if(type.equals("primerefs.SourceNode") ){
				entity.primerefs.SourceNodeDB e = entity.primerefs.SourceNodeDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.SourceContainer") ){
				entity.primerefs.SourceContainerDB e = entity.primerefs.SourceContainerDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeToNode") ){
				entity.primerefs.PrimeToNodeDB e = entity.primerefs.PrimeToNodeDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeToEdge") ){
				entity.primerefs.PrimeToEdgeDB e = entity.primerefs.PrimeToEdgeDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeToContainer") ){
				entity.primerefs.PrimeToContainerDB e = entity.primerefs.PrimeToContainerDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeToGraphModel") ){
				entity.primerefs.PrimeToGraphModelDB e = entity.primerefs.PrimeToGraphModelDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeCToNode") ){
				entity.primerefs.PrimeCToNodeDB e = entity.primerefs.PrimeCToNodeDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeCToEdge") ){
				entity.primerefs.PrimeCToEdgeDB e = entity.primerefs.PrimeCToEdgeDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeCToContainer") ){
				entity.primerefs.PrimeCToContainerDB e = entity.primerefs.PrimeCToContainerDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeCToGraphModel") ){
				entity.primerefs.PrimeCToGraphModelDB e = entity.primerefs.PrimeCToGraphModelDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeToNodeHierarchy") ){
				entity.primerefs.PrimeToNodeHierarchyDB e = entity.primerefs.PrimeToNodeHierarchyDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeToAbstractNodeHierarchy") ){
				entity.primerefs.PrimeToAbstractNodeHierarchyDB e = entity.primerefs.PrimeToAbstractNodeHierarchyDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeToNodeFlow") ){
				entity.primerefs.PrimeToNodeFlowDB e = entity.primerefs.PrimeToNodeFlowDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeToEdgeFlow") ){
				entity.primerefs.PrimeToEdgeFlowDB e = entity.primerefs.PrimeToEdgeFlowDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeToContainerFlow") ){
				entity.primerefs.PrimeToContainerFlowDB e = entity.primerefs.PrimeToContainerFlowDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeToGraphModelFlow") ){
				entity.primerefs.PrimeToGraphModelFlowDB e = entity.primerefs.PrimeToGraphModelFlowDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeCToNodeFlow") ){
				entity.primerefs.PrimeCToNodeFlowDB e = entity.primerefs.PrimeCToNodeFlowDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeCToEdgeFlow") ){
				entity.primerefs.PrimeCToEdgeFlowDB e = entity.primerefs.PrimeCToEdgeFlowDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeCToContainerFlow") ){
				entity.primerefs.PrimeCToContainerFlowDB e = entity.primerefs.PrimeCToContainerFlowDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeCToGraphModelFlow") ){
				entity.primerefs.PrimeCToGraphModelFlowDB e = entity.primerefs.PrimeCToGraphModelFlowDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.SourceEdge") ){
				entity.primerefs.SourceEdgeDB e = entity.primerefs.SourceEdgeDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("primerefs.PrimeRefs") ){
				entity.primerefs.PrimeRefsDB e = entity.primerefs.PrimeRefsDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			}
			return findApiByTypePrime(type, id, executer, parent, prev);
		}
		
		public static graphmodel.IdentifiableElement findApiByTypePrime(
			String type,
			long id,
			info.scce.pyro.core.command.PrimeRefsCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			// prime referenced graph-models
			if(
				"hierarchy.ContA".equals(type)
				|| "hierarchy.EdgeA".equals(type)
				|| "hierarchy.D".equals(type)
				|| "hierarchy.Cont".equals(type)
				|| "hierarchy.ContD".equals(type)
				|| "hierarchy.EdgeD".equals(type)
				|| "hierarchy.A".equals(type)
				|| "hierarchy.TA".equals(type)
				|| "hierarchy.TD".equals(type)
				|| "hierarchy.Hierarchy".equals(type)
			) {
				info.scce.pyro.core.command.HierarchyCommandExecuter HierarchyCommandExecuter = executer.getHierarchyCommandExecuter();
				return info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry.findApiByType(type, id, HierarchyCommandExecuter, parent, prev);
			} else if(
				"flowgraph.Start".equals(type)
				|| "flowgraph.End".equals(type)
				|| "flowgraph.Activity".equals(type)
				|| "flowgraph.EActivityA".equals(type)
				|| "flowgraph.EActivityB".equals(type)
				|| "flowgraph.ELibrary".equals(type)
				|| "flowgraph.SubFlowGraph".equals(type)
				|| "flowgraph.Swimlane".equals(type)
				|| "flowgraph.Transition".equals(type)
				|| "flowgraph.LabeledTransition".equals(type)
				|| "flowgraph.FlowGraph".equals(type)
			) {
				info.scce.pyro.core.command.FlowGraphCommandExecuter FlowGraphCommandExecuter = executer.getFlowGraphCommandExecuter();
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.findApiByType(type, id, FlowGraphCommandExecuter, parent, prev);
			}
			
			return null;
		}
	}
