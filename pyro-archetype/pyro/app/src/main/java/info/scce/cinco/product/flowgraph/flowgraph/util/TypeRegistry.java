	package info.scce.cinco.product.flowgraph.flowgraph.util;
	
	import io.quarkus.hibernate.orm.panache.PanacheEntity;
	import info.scce.pyro.core.command.CommandExecuter;
	import info.scce.pyro.core.command.FlowGraphDiagramCommandExecuter;
	
	public class TypeRegistry {
		
		/**
		 * PACKAGE-SPECIFIC FUNCTIONS
		 */
		
		public static String getTypeOf(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.End) {
				return "flowgraph.End";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane) {
				return "flowgraph.Swimlane";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
				return "flowgraph.SubFlowGraph";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition) {
				return "flowgraph.Transition";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Start) {
				return "flowgraph.Start";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
				return "flowgraph.Activity";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity) {
				return "flowgraph.ExternalActivity";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) {
				return "flowgraph.LabeledTransition";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram) {
				return "flowgraph.FlowGraphDiagram";
			}
			// prime referenced graph-models
			else if(
				(e instanceof info.scce.cinco.product.flowgraph.flowgraph.End)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Start)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram)
			) {
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.getTypeOf(e);
			}
			
			return null;
		}
		
		public static String getTypeOf(PanacheEntity e) {
			if(e instanceof entity.flowgraph.EndDB) {
				return "flowgraph.End";
			} else if(e instanceof entity.flowgraph.SwimlaneDB) {
				return "flowgraph.Swimlane";
			} else if(e instanceof entity.flowgraph.SubFlowGraphDB) {
				return "flowgraph.SubFlowGraph";
			} else if(e instanceof entity.flowgraph.TransitionDB) {
				return "flowgraph.Transition";
			} else if(e instanceof entity.flowgraph.StartDB) {
				return "flowgraph.Start";
			} else if(e instanceof entity.flowgraph.ActivityDB) {
				return "flowgraph.Activity";
			} else if(e instanceof entity.flowgraph.ExternalActivityDB) {
				return "flowgraph.ExternalActivity";
			} else if(e instanceof entity.flowgraph.LabeledTransitionDB) {
				return "flowgraph.LabeledTransition";
			} else if(e instanceof entity.flowgraph.FlowGraphDiagramDB) {
				return "flowgraph.FlowGraphDiagram";
			}
			// prime referenced graph-models
			else if(
				(e instanceof entity.flowgraph.EndDB)
				|| (e instanceof entity.flowgraph.SwimlaneDB)
				|| (e instanceof entity.flowgraph.SubFlowGraphDB)
				|| (e instanceof entity.flowgraph.TransitionDB)
				|| (e instanceof entity.flowgraph.StartDB)
				|| (e instanceof entity.flowgraph.ActivityDB)
				|| (e instanceof entity.flowgraph.ExternalActivityDB)
				|| (e instanceof entity.flowgraph.LabeledTransitionDB)
				|| (e instanceof entity.flowgraph.FlowGraphDiagramDB)
			) {
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.getTypeOf(e);
			}
			
			return null;
		}
		
		public static String getTypeOf(info.scce.pyro.core.graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.pyro.flowgraph.rest.End) {
				return "flowgraph.End";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.Swimlane) {
				return "flowgraph.Swimlane";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.SubFlowGraph) {
				return "flowgraph.SubFlowGraph";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.Transition) {
				return "flowgraph.Transition";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.Start) {
				return "flowgraph.Start";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.Activity) {
				return "flowgraph.Activity";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.ExternalActivity) {
				return "flowgraph.ExternalActivity";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.LabeledTransition) {
				return "flowgraph.LabeledTransition";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.FlowGraphDiagram) {
				return "flowgraph.FlowGraphDiagram";
			}
			// prime referenced graph-models
			else if(
				(e instanceof info.scce.pyro.flowgraph.rest.End)
				|| (e instanceof info.scce.pyro.flowgraph.rest.Swimlane)
				|| (e instanceof info.scce.pyro.flowgraph.rest.SubFlowGraph)
				|| (e instanceof info.scce.pyro.flowgraph.rest.Transition)
				|| (e instanceof info.scce.pyro.flowgraph.rest.Start)
				|| (e instanceof info.scce.pyro.flowgraph.rest.Activity)
				|| (e instanceof info.scce.pyro.flowgraph.rest.ExternalActivity)
				|| (e instanceof info.scce.pyro.flowgraph.rest.LabeledTransition)
				|| (e instanceof info.scce.pyro.flowgraph.rest.FlowGraphDiagram)
			) {
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.getTypeOf(e);
			}
			
			return null;
		}

		public static info.scce.pyro.core.graphmodel.IdentifiableElement getApiToRest(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.End) {
				info.scce.cinco.product.flowgraph.flowgraph.End apiE = (info.scce.cinco.product.flowgraph.flowgraph.End) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane) {
				info.scce.cinco.product.flowgraph.flowgraph.Swimlane apiE = (info.scce.cinco.product.flowgraph.flowgraph.Swimlane) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
				info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph apiE = (info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition) {
				info.scce.cinco.product.flowgraph.flowgraph.Transition apiE = (info.scce.cinco.product.flowgraph.flowgraph.Transition) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Start) {
				info.scce.cinco.product.flowgraph.flowgraph.Start apiE = (info.scce.cinco.product.flowgraph.flowgraph.Start) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
				info.scce.cinco.product.flowgraph.flowgraph.Activity apiE = (info.scce.cinco.product.flowgraph.flowgraph.Activity) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity) {
				info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity apiE = (info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) {
				info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition apiE = (info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram) {
				info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram apiE = (info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram) e;
				return getDBToRest(apiE.getDelegate());
			}
			// prime referenced graph-models
			else if(
				(e instanceof info.scce.cinco.product.flowgraph.flowgraph.End)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Start)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram)
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
			if(e instanceof entity.flowgraph.EndDB) {
				entity.flowgraph.EndDB en = (entity.flowgraph.EndDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.End.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.End.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.SwimlaneDB) {
				entity.flowgraph.SwimlaneDB en = (entity.flowgraph.SwimlaneDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.Swimlane.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.Swimlane.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.SubFlowGraphDB) {
				entity.flowgraph.SubFlowGraphDB en = (entity.flowgraph.SubFlowGraphDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.SubFlowGraph.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.SubFlowGraph.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.TransitionDB) {
				entity.flowgraph.TransitionDB en = (entity.flowgraph.TransitionDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.Transition.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.Transition.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.StartDB) {
				entity.flowgraph.StartDB en = (entity.flowgraph.StartDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.Start.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.Start.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.ActivityDB) {
				entity.flowgraph.ActivityDB en = (entity.flowgraph.ActivityDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.Activity.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.Activity.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.ExternalActivityDB) {
				entity.flowgraph.ExternalActivityDB en = (entity.flowgraph.ExternalActivityDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.ExternalActivity.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.ExternalActivity.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.LabeledTransitionDB) {
				entity.flowgraph.LabeledTransitionDB en = (entity.flowgraph.LabeledTransitionDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.LabeledTransition.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.LabeledTransition.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.FlowGraphDiagramDB) {
				entity.flowgraph.FlowGraphDiagramDB en = (entity.flowgraph.FlowGraphDiagramDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.FlowGraphDiagram.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.FlowGraphDiagram.fromEntity(en, cache);
				}
			}
			return getDBToRestPrime(e, cache, onlyProperties);
		}
		
		public static info.scce.pyro.core.graphmodel.IdentifiableElement getDBToRestPrime(PanacheEntity e, info.scce.pyro.rest.ObjectCache cache, boolean onlyProperties) {
			// prime referenced ecore-models
			if(e instanceof entity.externallibrary.ExternalLibraryDB) {
				entity.externallibrary.ExternalLibraryDB en = (entity.externallibrary.ExternalLibraryDB) e;
				return info.scce.pyro.externallibrary.rest.ExternalLibrary.fromEntity(en, cache);
			}
			else if(e instanceof entity.externallibrary.ExternalActivityLibraryDB) {
				entity.externallibrary.ExternalActivityLibraryDB en = (entity.externallibrary.ExternalActivityLibraryDB) e;
				if(onlyProperties) {
					return info.scce.pyro.externallibrary.rest.ExternalActivityLibrary.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.externallibrary.rest.ExternalActivityLibrary.fromEntity(en, cache);
				}
			}
			else if(e instanceof entity.externallibrary.ExternalActivityDB) {
				entity.externallibrary.ExternalActivityDB en = (entity.externallibrary.ExternalActivityDB) e;
				if(onlyProperties) {
					return info.scce.pyro.externallibrary.rest.ExternalActivity.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.externallibrary.rest.ExternalActivity.fromEntity(en, cache);
				}
			}
			// prime referenced graph-models
			else if(
				(e instanceof info.scce.cinco.product.flowgraph.flowgraph.End)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Start)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram)
			) {
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.getDBToRest(e);
			}
			return null;
		}

		public static PanacheEntity getApiToDB(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.End) {
				info.scce.cinco.product.flowgraph.flowgraph.End apiE = (info.scce.cinco.product.flowgraph.flowgraph.End) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane) {
				info.scce.cinco.product.flowgraph.flowgraph.Swimlane apiE = (info.scce.cinco.product.flowgraph.flowgraph.Swimlane) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
				info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph apiE = (info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition) {
				info.scce.cinco.product.flowgraph.flowgraph.Transition apiE = (info.scce.cinco.product.flowgraph.flowgraph.Transition) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Start) {
				info.scce.cinco.product.flowgraph.flowgraph.Start apiE = (info.scce.cinco.product.flowgraph.flowgraph.Start) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
				info.scce.cinco.product.flowgraph.flowgraph.Activity apiE = (info.scce.cinco.product.flowgraph.flowgraph.Activity) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity) {
				info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity apiE = (info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) {
				info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition apiE = (info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram) {
				info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram apiE = (info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram) e;
				return apiE.getDelegate();
			}
			// prime referenced graph-models
			else if(
				(e instanceof info.scce.cinco.product.flowgraph.flowgraph.End)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Start)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition)
				|| (e instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram)
			) {
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.getApiToDB(e);
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
			if(info.scce.cinco.product.flowgraph.flowgraph.End.class.equals(entityClass)) {
				return entity.flowgraph.EndDB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.Swimlane.class.equals(entityClass)) {
				return entity.flowgraph.SwimlaneDB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph.class.equals(entityClass)) {
				return entity.flowgraph.SubFlowGraphDB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.Transition.class.equals(entityClass)) {
				return entity.flowgraph.TransitionDB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.Start.class.equals(entityClass)) {
				return entity.flowgraph.StartDB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.Activity.class.equals(entityClass)) {
				return entity.flowgraph.ActivityDB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity.class.equals(entityClass)) {
				return entity.flowgraph.ExternalActivityDB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition.class.equals(entityClass)) {
				return entity.flowgraph.LabeledTransitionDB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram.class.equals(entityClass)) {
				return entity.flowgraph.FlowGraphDiagramDB.findById(id);
			}
			// prime referenced graph-models
			else if(
				info.scce.cinco.product.flowgraph.flowgraph.End.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.Swimlane.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.Transition.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.Start.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.Activity.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition.class.equals(entityClass)
				|| info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram.class.equals(entityClass)
			) {
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.findAbstractEntityByApi(id, entityClass);
			}
			
			return null;
		}
		
		public static PanacheEntity findAbstractEntityByRest(long id, Class<?> entityClass) {
			if(info.scce.pyro.flowgraph.rest.End.class.equals(entityClass)) {
				return entity.flowgraph.EndDB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.Swimlane.class.equals(entityClass)) {
				return entity.flowgraph.SwimlaneDB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.SubFlowGraph.class.equals(entityClass)) {
				return entity.flowgraph.SubFlowGraphDB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.Transition.class.equals(entityClass)) {
				return entity.flowgraph.TransitionDB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.Start.class.equals(entityClass)) {
				return entity.flowgraph.StartDB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.Activity.class.equals(entityClass)) {
				return entity.flowgraph.ActivityDB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.ExternalActivity.class.equals(entityClass)) {
				return entity.flowgraph.ExternalActivityDB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.LabeledTransition.class.equals(entityClass)) {
				return entity.flowgraph.LabeledTransitionDB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.FlowGraphDiagram.class.equals(entityClass)) {
				return entity.flowgraph.FlowGraphDiagramDB.findById(id);
			}
			// prime referenced graph-models
			else if(
				info.scce.pyro.flowgraph.rest.End.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.Swimlane.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.SubFlowGraph.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.Transition.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.Start.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.Activity.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.ExternalActivity.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.LabeledTransition.class.equals(entityClass)
				|| info.scce.pyro.flowgraph.rest.FlowGraphDiagram.class.equals(entityClass)
			) {
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.findAbstractEntityByRest(id, entityClass);
			}
			
			return null;
		}
		
		public static PanacheEntity findAbstractEntityByEntity(long id, Class<?> entityClass) {
			if(entity.flowgraph.EndDB.class.equals(entityClass)) {
				return entity.flowgraph.EndDB.findById(id);
			} else if(entity.flowgraph.SwimlaneDB.class.equals(entityClass)) {
				return entity.flowgraph.SwimlaneDB.findById(id);
			} else if(entity.flowgraph.SubFlowGraphDB.class.equals(entityClass)) {
				return entity.flowgraph.SubFlowGraphDB.findById(id);
			} else if(entity.flowgraph.TransitionDB.class.equals(entityClass)) {
				return entity.flowgraph.TransitionDB.findById(id);
			} else if(entity.flowgraph.StartDB.class.equals(entityClass)) {
				return entity.flowgraph.StartDB.findById(id);
			} else if(entity.flowgraph.ActivityDB.class.equals(entityClass)) {
				return entity.flowgraph.ActivityDB.findById(id);
			} else if(entity.flowgraph.ExternalActivityDB.class.equals(entityClass)) {
				return entity.flowgraph.ExternalActivityDB.findById(id);
			} else if(entity.flowgraph.LabeledTransitionDB.class.equals(entityClass)) {
				return entity.flowgraph.LabeledTransitionDB.findById(id);
			} else if(entity.flowgraph.FlowGraphDiagramDB.class.equals(entityClass)) {
				return entity.flowgraph.FlowGraphDiagramDB.findById(id);
			}
			// prime referenced graph-models
			else if(
				entity.flowgraph.EndDB.class.equals(entityClass)
				|| entity.flowgraph.SwimlaneDB.class.equals(entityClass)
				|| entity.flowgraph.SubFlowGraphDB.class.equals(entityClass)
				|| entity.flowgraph.TransitionDB.class.equals(entityClass)
				|| entity.flowgraph.StartDB.class.equals(entityClass)
				|| entity.flowgraph.ActivityDB.class.equals(entityClass)
				|| entity.flowgraph.ExternalActivityDB.class.equals(entityClass)
				|| entity.flowgraph.LabeledTransitionDB.class.equals(entityClass)
				|| entity.flowgraph.FlowGraphDiagramDB.class.equals(entityClass)
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
				found = entity.flowgraph.EndDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.flowgraph.EndDB...");
				e.printStackTrace();
			}
			try {
				found = entity.flowgraph.SwimlaneDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.flowgraph.SwimlaneDB...");
				e.printStackTrace();
			}
			try {
				found = entity.flowgraph.SubFlowGraphDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.flowgraph.SubFlowGraphDB...");
				e.printStackTrace();
			}
			try {
				found = entity.flowgraph.TransitionDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.flowgraph.TransitionDB...");
				e.printStackTrace();
			}
			try {
				found = entity.flowgraph.StartDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.flowgraph.StartDB...");
				e.printStackTrace();
			}
			try {
				found = entity.flowgraph.ActivityDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.flowgraph.ActivityDB...");
				e.printStackTrace();
			}
			try {
				found = entity.flowgraph.ExternalActivityDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.flowgraph.ExternalActivityDB...");
				e.printStackTrace();
			}
			try {
				found = entity.flowgraph.LabeledTransitionDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.flowgraph.LabeledTransitionDB...");
				e.printStackTrace();
			}
			try {
				found = entity.flowgraph.FlowGraphDiagramDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.flowgraph.FlowGraphDiagramDB...");
				e.printStackTrace();
			}
			
			return null;
		}
		
		public static PanacheEntity findByType(String type, long id) {
			if(type.equals("flowgraph.End") ){
				return entity.flowgraph.EndDB.findById(id);
			} else if(type.equals("flowgraph.Swimlane") ){
				return entity.flowgraph.SwimlaneDB.findById(id);
			} else if(type.equals("flowgraph.SubFlowGraph") ){
				return entity.flowgraph.SubFlowGraphDB.findById(id);
			} else if(type.equals("flowgraph.Transition") ){
				return entity.flowgraph.TransitionDB.findById(id);
			} else if(type.equals("flowgraph.Start") ){
				return entity.flowgraph.StartDB.findById(id);
			} else if(type.equals("flowgraph.Activity") ){
				return entity.flowgraph.ActivityDB.findById(id);
			} else if(type.equals("flowgraph.ExternalActivity") ){
				return entity.flowgraph.ExternalActivityDB.findById(id);
			} else if(type.equals("flowgraph.LabeledTransition") ){
				return entity.flowgraph.LabeledTransitionDB.findById(id);
			} else if(type.equals("flowgraph.FlowGraphDiagram") ){
				return entity.flowgraph.FlowGraphDiagramDB.findById(id);
			}
			
			return null;
		}
		
		/**
		 * GRAPHMODEL FUNCTIONS
		 */
		
		public static graphmodel.IdentifiableElement getDBToApi(
						PanacheEntity e,
						CommandExecuter executer
		) {
			return getDBToApi(e, executer, null, null);
		}
		
		public static graphmodel.IdentifiableElement getDBToApi(
			PanacheEntity e,
			CommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			if(e instanceof entity.flowgraph.EndDB) {
				entity.flowgraph.EndDB en = (entity.flowgraph.EndDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.EndImpl(en, executer);
			} else if(e instanceof entity.flowgraph.SwimlaneDB) {
				entity.flowgraph.SwimlaneDB en = (entity.flowgraph.SwimlaneDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.SwimlaneImpl(en, executer);
			} else if(e instanceof entity.flowgraph.SubFlowGraphDB) {
				entity.flowgraph.SubFlowGraphDB en = (entity.flowgraph.SubFlowGraphDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.SubFlowGraphImpl(en, executer);
			} else if(e instanceof entity.flowgraph.TransitionDB) {
				entity.flowgraph.TransitionDB en = (entity.flowgraph.TransitionDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.TransitionImpl(en, executer);
			} else if(e instanceof entity.flowgraph.StartDB) {
				entity.flowgraph.StartDB en = (entity.flowgraph.StartDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.StartImpl(en, executer);
			} else if(e instanceof entity.flowgraph.ActivityDB) {
				entity.flowgraph.ActivityDB en = (entity.flowgraph.ActivityDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.ActivityImpl(en, executer);
			} else if(e instanceof entity.flowgraph.ExternalActivityDB) {
				entity.flowgraph.ExternalActivityDB en = (entity.flowgraph.ExternalActivityDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.ExternalActivityImpl(en, executer);
			} else if(e instanceof entity.flowgraph.LabeledTransitionDB) {
				entity.flowgraph.LabeledTransitionDB en = (entity.flowgraph.LabeledTransitionDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.LabeledTransitionImpl(en, executer);
			} else if(e instanceof entity.flowgraph.FlowGraphDiagramDB) {
				entity.flowgraph.FlowGraphDiagramDB en = (entity.flowgraph.FlowGraphDiagramDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.FlowGraphDiagramImpl(en, executer);
			}
			return getDBToApiPrime(e, executer, parent, prev);
		}
		
		public static graphmodel.IdentifiableElement findApiByType(
			String type,
			long id,
			CommandExecuter cmdExecuter
		) {
			if(cmdExecuter instanceof FlowGraphDiagramCommandExecuter) {
				FlowGraphDiagramCommandExecuter flowgraphdiagramCommandExecuter = (FlowGraphDiagramCommandExecuter) cmdExecuter;
				return findApiByType(type, id, flowgraphdiagramCommandExecuter, null, null);
			}
			else
				if(cmdExecuter != null) throw new RuntimeException("GraphModelCommandExecuter can not handle this type!");
			return null;
		}
		
		public static graphmodel.IdentifiableElement findApiByType(
			String type,
			long id,
			CommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			if(type.equals("flowgraph.End") ){
				entity.flowgraph.EndDB e = entity.flowgraph.EndDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("flowgraph.Swimlane") ){
				entity.flowgraph.SwimlaneDB e = entity.flowgraph.SwimlaneDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("flowgraph.SubFlowGraph") ){
				entity.flowgraph.SubFlowGraphDB e = entity.flowgraph.SubFlowGraphDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("flowgraph.Transition") ){
				entity.flowgraph.TransitionDB e = entity.flowgraph.TransitionDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("flowgraph.Start") ){
				entity.flowgraph.StartDB e = entity.flowgraph.StartDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("flowgraph.Activity") ){
				entity.flowgraph.ActivityDB e = entity.flowgraph.ActivityDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("flowgraph.ExternalActivity") ){
				entity.flowgraph.ExternalActivityDB e = entity.flowgraph.ExternalActivityDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("flowgraph.LabeledTransition") ){
				entity.flowgraph.LabeledTransitionDB e = entity.flowgraph.LabeledTransitionDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("flowgraph.FlowGraphDiagram") ){
				entity.flowgraph.FlowGraphDiagramDB e = entity.flowgraph.FlowGraphDiagramDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			}
			return findApiByTypePrime(type, id, executer, parent, prev);
		}
		
		public static graphmodel.IdentifiableElement getDBToApiPrime(
			PanacheEntity e,
			CommandExecuter cmdExecuter,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			if(cmdExecuter instanceof FlowGraphDiagramCommandExecuter) {
				FlowGraphDiagramCommandExecuter flowgraphdiagramCommandExecuter = (FlowGraphDiagramCommandExecuter) cmdExecuter;
				return getDBToApiPrimeflowgraphdiagramCommandExecuter(e, flowgraphdiagramCommandExecuter, parent, prev);
			}
			else
				if(cmdExecuter != null) throw new RuntimeException("GraphModelCommandExecuter can not handle this type!");
			return null;
		}
		
		public static graphmodel.IdentifiableElement findApiByTypePrime(
			String type,
			long id,
			CommandExecuter cmdExecuter,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			if(cmdExecuter instanceof FlowGraphDiagramCommandExecuter) {
				FlowGraphDiagramCommandExecuter flowgraphdiagramCommandExecuter = (FlowGraphDiagramCommandExecuter) cmdExecuter;
				return findApiByTypePrimeflowgraphdiagramCommandExecuter(type, id, flowgraphdiagramCommandExecuter, parent, prev);
			}
			else
				if(cmdExecuter != null) throw new RuntimeException("GraphModelCommandExecuter can not handle this type!");
			return null;
		}
		
		/**
		 * GRAPHMODEL-SPECIFIC FUNCTIONS
		 */
		
		public static graphmodel.IdentifiableElement getDBToApiPrimeflowgraphdiagramCommandExecuter(
			PanacheEntity e,
			FlowGraphDiagramCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			// prime referenced ecore-models
			if(e instanceof entity.externallibrary.ExternalLibraryDB) {
				entity.externallibrary.ExternalLibraryDB en = (entity.externallibrary.ExternalLibraryDB) e;
				return new externallibrary.impl.ExternalLibraryImpl(en);
			}
			else if(e instanceof entity.externallibrary.ExternalActivityLibraryDB) {
				entity.externallibrary.ExternalActivityLibraryDB en = (entity.externallibrary.ExternalActivityLibraryDB) e;
				return new externallibrary.impl.ExternalActivityLibraryImpl(en);
			}
			else if(e instanceof entity.externallibrary.ExternalActivityDB) {
				entity.externallibrary.ExternalActivityDB en = (entity.externallibrary.ExternalActivityDB) e;
				return new externallibrary.impl.ExternalActivityImpl(en);
			}
			// prime referenced graph-models
			else if(
				(e instanceof entity.flowgraph.EndDB)
				|| (e instanceof entity.flowgraph.SwimlaneDB)
				|| (e instanceof entity.flowgraph.SubFlowGraphDB)
				|| (e instanceof entity.flowgraph.TransitionDB)
				|| (e instanceof entity.flowgraph.StartDB)
				|| (e instanceof entity.flowgraph.ActivityDB)
				|| (e instanceof entity.flowgraph.ExternalActivityDB)
				|| (e instanceof entity.flowgraph.LabeledTransitionDB)
				|| (e instanceof entity.flowgraph.FlowGraphDiagramDB)
			) {
				FlowGraphDiagramCommandExecuter FlowGraphDiagramCommandExecuter = executer.getFlowGraphDiagramCommandExecuter();
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.getDBToApi(e, FlowGraphDiagramCommandExecuter, parent, prev);
			}
			return null;
		}
		
		public static graphmodel.IdentifiableElement findApiByTypePrimeflowgraphdiagramCommandExecuter(
			String type,
			long id,
			FlowGraphDiagramCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			// prime referenced graph-models
			if(
				"flowgraph.End".equals(type)
				|| "flowgraph.Swimlane".equals(type)
				|| "flowgraph.SubFlowGraph".equals(type)
				|| "flowgraph.Transition".equals(type)
				|| "flowgraph.Start".equals(type)
				|| "flowgraph.Activity".equals(type)
				|| "flowgraph.ExternalActivity".equals(type)
				|| "flowgraph.LabeledTransition".equals(type)
				|| "flowgraph.FlowGraphDiagram".equals(type)
			) {
				FlowGraphDiagramCommandExecuter FlowGraphDiagramCommandExecuter = executer.getFlowGraphDiagramCommandExecuter();
				return info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry.findApiByType(type, id, FlowGraphDiagramCommandExecuter, parent, prev);
			}
			
			return null;
		}
		
	}
