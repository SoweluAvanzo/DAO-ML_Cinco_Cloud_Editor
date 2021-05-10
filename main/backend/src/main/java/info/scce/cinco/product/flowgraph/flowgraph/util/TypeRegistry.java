	package info.scce.cinco.product.flowgraph.flowgraph.util;
	
	import io.quarkus.hibernate.orm.panache.PanacheEntity;
	import info.scce.pyro.core.command.FlowGraphCommandExecuter;
	
	public class TypeRegistry {
		
		public static String getTypeOf(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Start) {
				return "flowgraph.Start";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.End) {
				return "flowgraph.End";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
				return "flowgraph.Activity";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityA) {
				return "flowgraph.EActivityA";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityB) {
				return "flowgraph.EActivityB";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.ELibrary) {
				return "flowgraph.ELibrary";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
				return "flowgraph.SubFlowGraph";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane) {
				return "flowgraph.Swimlane";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition) {
				return "flowgraph.Transition";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) {
				return "flowgraph.LabeledTransition";
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraph) {
				return "flowgraph.FlowGraph";
			}
			
			return null;
		}
		
		public static String getTypeOf(PanacheEntity e) {
			if(e instanceof entity.flowgraph.StartDB) {
				return "flowgraph.Start";
			} else if(e instanceof entity.flowgraph.EndDB) {
				return "flowgraph.End";
			} else if(e instanceof entity.flowgraph.ActivityDB) {
				return "flowgraph.Activity";
			} else if(e instanceof entity.flowgraph.EActivityADB) {
				return "flowgraph.EActivityA";
			} else if(e instanceof entity.flowgraph.EActivityBDB) {
				return "flowgraph.EActivityB";
			} else if(e instanceof entity.flowgraph.ELibraryDB) {
				return "flowgraph.ELibrary";
			} else if(e instanceof entity.flowgraph.SubFlowGraphDB) {
				return "flowgraph.SubFlowGraph";
			} else if(e instanceof entity.flowgraph.SwimlaneDB) {
				return "flowgraph.Swimlane";
			} else if(e instanceof entity.flowgraph.TransitionDB) {
				return "flowgraph.Transition";
			} else if(e instanceof entity.flowgraph.LabeledTransitionDB) {
				return "flowgraph.LabeledTransition";
			} else if(e instanceof entity.flowgraph.FlowGraphDB) {
				return "flowgraph.FlowGraph";
			}
			
			return null;
		}
		
		public static String getTypeOf(info.scce.pyro.core.graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.pyro.flowgraph.rest.Start) {
				return "flowgraph.Start";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.End) {
				return "flowgraph.End";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.Activity) {
				return "flowgraph.Activity";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.EActivityA) {
				return "flowgraph.EActivityA";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.EActivityB) {
				return "flowgraph.EActivityB";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.ELibrary) {
				return "flowgraph.ELibrary";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.SubFlowGraph) {
				return "flowgraph.SubFlowGraph";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.Swimlane) {
				return "flowgraph.Swimlane";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.Transition) {
				return "flowgraph.Transition";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.LabeledTransition) {
				return "flowgraph.LabeledTransition";
			} else if(e instanceof info.scce.pyro.flowgraph.rest.FlowGraph) {
				return "flowgraph.FlowGraph";
			}
			
			return null;
		}

		public static info.scce.pyro.core.graphmodel.IdentifiableElement getApiToRest(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Start) {
				info.scce.cinco.product.flowgraph.flowgraph.Start apiE = (info.scce.cinco.product.flowgraph.flowgraph.Start) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.End) {
				info.scce.cinco.product.flowgraph.flowgraph.End apiE = (info.scce.cinco.product.flowgraph.flowgraph.End) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
				info.scce.cinco.product.flowgraph.flowgraph.Activity apiE = (info.scce.cinco.product.flowgraph.flowgraph.Activity) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityA) {
				info.scce.cinco.product.flowgraph.flowgraph.EActivityA apiE = (info.scce.cinco.product.flowgraph.flowgraph.EActivityA) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityB) {
				info.scce.cinco.product.flowgraph.flowgraph.EActivityB apiE = (info.scce.cinco.product.flowgraph.flowgraph.EActivityB) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.ELibrary) {
				info.scce.cinco.product.flowgraph.flowgraph.ELibrary apiE = (info.scce.cinco.product.flowgraph.flowgraph.ELibrary) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
				info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph apiE = (info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane) {
				info.scce.cinco.product.flowgraph.flowgraph.Swimlane apiE = (info.scce.cinco.product.flowgraph.flowgraph.Swimlane) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition) {
				info.scce.cinco.product.flowgraph.flowgraph.Transition apiE = (info.scce.cinco.product.flowgraph.flowgraph.Transition) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) {
				info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition apiE = (info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraph) {
				info.scce.cinco.product.flowgraph.flowgraph.FlowGraph apiE = (info.scce.cinco.product.flowgraph.flowgraph.FlowGraph) e;
				return getDBToRest(apiE.getDelegate());
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
			if(e instanceof entity.flowgraph.StartDB) {
				entity.flowgraph.StartDB en = (entity.flowgraph.StartDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.Start.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.Start.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.EndDB) {
				entity.flowgraph.EndDB en = (entity.flowgraph.EndDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.End.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.End.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.ActivityDB) {
				entity.flowgraph.ActivityDB en = (entity.flowgraph.ActivityDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.Activity.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.Activity.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.EActivityADB) {
				entity.flowgraph.EActivityADB en = (entity.flowgraph.EActivityADB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.EActivityA.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.EActivityA.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.EActivityBDB) {
				entity.flowgraph.EActivityBDB en = (entity.flowgraph.EActivityBDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.EActivityB.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.EActivityB.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.ELibraryDB) {
				entity.flowgraph.ELibraryDB en = (entity.flowgraph.ELibraryDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.ELibrary.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.ELibrary.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.SubFlowGraphDB) {
				entity.flowgraph.SubFlowGraphDB en = (entity.flowgraph.SubFlowGraphDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.SubFlowGraph.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.SubFlowGraph.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.SwimlaneDB) {
				entity.flowgraph.SwimlaneDB en = (entity.flowgraph.SwimlaneDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.Swimlane.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.Swimlane.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.TransitionDB) {
				entity.flowgraph.TransitionDB en = (entity.flowgraph.TransitionDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.Transition.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.Transition.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.LabeledTransitionDB) {
				entity.flowgraph.LabeledTransitionDB en = (entity.flowgraph.LabeledTransitionDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.LabeledTransition.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.LabeledTransition.fromEntity(en, cache);
				}
			} else if(e instanceof entity.flowgraph.FlowGraphDB) {
				entity.flowgraph.FlowGraphDB en = (entity.flowgraph.FlowGraphDB) e;
				if(onlyProperties) {
					return info.scce.pyro.flowgraph.rest.FlowGraph.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.flowgraph.rest.FlowGraph.fromEntity(en, cache);
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
			else if(e instanceof entity.externallibrary.ExternalActivityADB) {
				entity.externallibrary.ExternalActivityADB en = (entity.externallibrary.ExternalActivityADB) e;
				if(onlyProperties) {
					return info.scce.pyro.externallibrary.rest.ExternalActivityA.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.externallibrary.rest.ExternalActivityA.fromEntity(en, cache);
				}
			}
			else if(e instanceof entity.externallibrary.ExternalActivityDDB) {
				entity.externallibrary.ExternalActivityDDB en = (entity.externallibrary.ExternalActivityDDB) e;
				if(onlyProperties) {
					return info.scce.pyro.externallibrary.rest.ExternalActivityD.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.externallibrary.rest.ExternalActivityD.fromEntity(en, cache);
				}
			}
			return null;
		}

		public static PanacheEntity getApiToDB(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Start) {
				info.scce.cinco.product.flowgraph.flowgraph.Start apiE = (info.scce.cinco.product.flowgraph.flowgraph.Start) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.End) {
				info.scce.cinco.product.flowgraph.flowgraph.End apiE = (info.scce.cinco.product.flowgraph.flowgraph.End) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
				info.scce.cinco.product.flowgraph.flowgraph.Activity apiE = (info.scce.cinco.product.flowgraph.flowgraph.Activity) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityA) {
				info.scce.cinco.product.flowgraph.flowgraph.EActivityA apiE = (info.scce.cinco.product.flowgraph.flowgraph.EActivityA) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityB) {
				info.scce.cinco.product.flowgraph.flowgraph.EActivityB apiE = (info.scce.cinco.product.flowgraph.flowgraph.EActivityB) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.ELibrary) {
				info.scce.cinco.product.flowgraph.flowgraph.ELibrary apiE = (info.scce.cinco.product.flowgraph.flowgraph.ELibrary) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
				info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph apiE = (info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane) {
				info.scce.cinco.product.flowgraph.flowgraph.Swimlane apiE = (info.scce.cinco.product.flowgraph.flowgraph.Swimlane) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition) {
				info.scce.cinco.product.flowgraph.flowgraph.Transition apiE = (info.scce.cinco.product.flowgraph.flowgraph.Transition) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) {
				info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition apiE = (info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraph) {
				info.scce.cinco.product.flowgraph.flowgraph.FlowGraph apiE = (info.scce.cinco.product.flowgraph.flowgraph.FlowGraph) e;
				return apiE.getDelegate();
			}
			
			return null;
		}
		
		public static graphmodel.IdentifiableElement getDBToApi(
			PanacheEntity e,
			info.scce.pyro.core.command.FlowGraphCommandExecuter executer
		) {
			return getDBToApi(e, executer, null, null);
		}
		
		public static graphmodel.IdentifiableElement getDBToApi(
			PanacheEntity e,
			FlowGraphCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			if(e instanceof entity.flowgraph.StartDB) {
				entity.flowgraph.StartDB en = (entity.flowgraph.StartDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.StartImpl(en, executer);
			} else if(e instanceof entity.flowgraph.EndDB) {
				entity.flowgraph.EndDB en = (entity.flowgraph.EndDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.EndImpl(en, executer);
			} else if(e instanceof entity.flowgraph.ActivityDB) {
				entity.flowgraph.ActivityDB en = (entity.flowgraph.ActivityDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.ActivityImpl(en, executer);
			} else if(e instanceof entity.flowgraph.EActivityADB) {
				entity.flowgraph.EActivityADB en = (entity.flowgraph.EActivityADB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.EActivityAImpl(en, executer);
			} else if(e instanceof entity.flowgraph.EActivityBDB) {
				entity.flowgraph.EActivityBDB en = (entity.flowgraph.EActivityBDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.EActivityBImpl(en, executer);
			} else if(e instanceof entity.flowgraph.ELibraryDB) {
				entity.flowgraph.ELibraryDB en = (entity.flowgraph.ELibraryDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.ELibraryImpl(en, executer);
			} else if(e instanceof entity.flowgraph.SubFlowGraphDB) {
				entity.flowgraph.SubFlowGraphDB en = (entity.flowgraph.SubFlowGraphDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.SubFlowGraphImpl(en, executer);
			} else if(e instanceof entity.flowgraph.SwimlaneDB) {
				entity.flowgraph.SwimlaneDB en = (entity.flowgraph.SwimlaneDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.SwimlaneImpl(en, executer);
			} else if(e instanceof entity.flowgraph.TransitionDB) {
				entity.flowgraph.TransitionDB en = (entity.flowgraph.TransitionDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.TransitionImpl(en, executer);
			} else if(e instanceof entity.flowgraph.LabeledTransitionDB) {
				entity.flowgraph.LabeledTransitionDB en = (entity.flowgraph.LabeledTransitionDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.LabeledTransitionImpl(en, executer);
			} else if(e instanceof entity.flowgraph.FlowGraphDB) {
				entity.flowgraph.FlowGraphDB en = (entity.flowgraph.FlowGraphDB) e;
				return new info.scce.cinco.product.flowgraph.flowgraph.impl.FlowGraphImpl(en, executer);
			}
			return getDBToApiPrime(e, executer, parent, prev);
		}
		
		public static graphmodel.IdentifiableElement getDBToApiPrime(
			PanacheEntity e,
			FlowGraphCommandExecuter executer,
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
			else if(e instanceof entity.externallibrary.ExternalActivityADB) {
				entity.externallibrary.ExternalActivityADB en = (entity.externallibrary.ExternalActivityADB) e;
				return new externallibrary.impl.ExternalActivityAImpl(en);
			}
			else if(e instanceof entity.externallibrary.ExternalActivityDDB) {
				entity.externallibrary.ExternalActivityDDB en = (entity.externallibrary.ExternalActivityDDB) e;
				return new externallibrary.impl.ExternalActivityDImpl(en);
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
			if(info.scce.cinco.product.flowgraph.flowgraph.Start.class.equals(entityClass)) {
				return entity.flowgraph.StartDB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.End.class.equals(entityClass)) {
				return entity.flowgraph.EndDB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.Activity.class.equals(entityClass)) {
				return entity.flowgraph.ActivityDB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.EActivityA.class.equals(entityClass)) {
				return entity.flowgraph.EActivityADB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.EActivityB.class.equals(entityClass)) {
				return entity.flowgraph.EActivityBDB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.ELibrary.class.equals(entityClass)) {
				return entity.flowgraph.ELibraryDB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph.class.equals(entityClass)) {
				return entity.flowgraph.SubFlowGraphDB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.Swimlane.class.equals(entityClass)) {
				return entity.flowgraph.SwimlaneDB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.Transition.class.equals(entityClass)) {
				return entity.flowgraph.TransitionDB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition.class.equals(entityClass)) {
				return entity.flowgraph.LabeledTransitionDB.findById(id);
			} else if(info.scce.cinco.product.flowgraph.flowgraph.FlowGraph.class.equals(entityClass)) {
				return entity.flowgraph.FlowGraphDB.findById(id);
			}
			
			return null;
		}
		
		public static PanacheEntity findAbstractEntityByRest(long id, Class<?> entityClass) {
			if(info.scce.pyro.flowgraph.rest.Start.class.equals(entityClass)) {
				return entity.flowgraph.StartDB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.End.class.equals(entityClass)) {
				return entity.flowgraph.EndDB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.Activity.class.equals(entityClass)) {
				return entity.flowgraph.ActivityDB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.EActivityA.class.equals(entityClass)) {
				return entity.flowgraph.EActivityADB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.EActivityB.class.equals(entityClass)) {
				return entity.flowgraph.EActivityBDB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.ELibrary.class.equals(entityClass)) {
				return entity.flowgraph.ELibraryDB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.SubFlowGraph.class.equals(entityClass)) {
				return entity.flowgraph.SubFlowGraphDB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.Swimlane.class.equals(entityClass)) {
				return entity.flowgraph.SwimlaneDB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.Transition.class.equals(entityClass)) {
				return entity.flowgraph.TransitionDB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.LabeledTransition.class.equals(entityClass)) {
				return entity.flowgraph.LabeledTransitionDB.findById(id);
			} else if(info.scce.pyro.flowgraph.rest.FlowGraph.class.equals(entityClass)) {
				return entity.flowgraph.FlowGraphDB.findById(id);
			}
			
			return null;
		}
		
		public static PanacheEntity findAbstractEntityByEntity(long id, Class<?> entityClass) {
			if(entity.flowgraph.StartDB.class.equals(entityClass)) {
				return entity.flowgraph.StartDB.findById(id);
			} else if(entity.flowgraph.EndDB.class.equals(entityClass)) {
				return entity.flowgraph.EndDB.findById(id);
			} else if(entity.flowgraph.ActivityDB.class.equals(entityClass)) {
				return entity.flowgraph.ActivityDB.findById(id);
			} else if(entity.flowgraph.EActivityADB.class.equals(entityClass)) {
				return entity.flowgraph.EActivityADB.findById(id);
			} else if(entity.flowgraph.EActivityBDB.class.equals(entityClass)) {
				return entity.flowgraph.EActivityBDB.findById(id);
			} else if(entity.flowgraph.ELibraryDB.class.equals(entityClass)) {
				return entity.flowgraph.ELibraryDB.findById(id);
			} else if(entity.flowgraph.SubFlowGraphDB.class.equals(entityClass)) {
				return entity.flowgraph.SubFlowGraphDB.findById(id);
			} else if(entity.flowgraph.SwimlaneDB.class.equals(entityClass)) {
				return entity.flowgraph.SwimlaneDB.findById(id);
			} else if(entity.flowgraph.TransitionDB.class.equals(entityClass)) {
				return entity.flowgraph.TransitionDB.findById(id);
			} else if(entity.flowgraph.LabeledTransitionDB.class.equals(entityClass)) {
				return entity.flowgraph.LabeledTransitionDB.findById(id);
			} else if(entity.flowgraph.FlowGraphDB.class.equals(entityClass)) {
				return entity.flowgraph.FlowGraphDB.findById(id);
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
				found = entity.flowgraph.StartDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.flowgraph.StartDB...");
				e.printStackTrace();
			}
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
				found = entity.flowgraph.ActivityDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.flowgraph.ActivityDB...");
				e.printStackTrace();
			}
			try {
				found = entity.flowgraph.EActivityADB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.flowgraph.EActivityADB...");
				e.printStackTrace();
			}
			try {
				found = entity.flowgraph.EActivityBDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.flowgraph.EActivityBDB...");
				e.printStackTrace();
			}
			try {
				found = entity.flowgraph.ELibraryDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.flowgraph.ELibraryDB...");
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
				found = entity.flowgraph.SwimlaneDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.flowgraph.SwimlaneDB...");
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
				found = entity.flowgraph.LabeledTransitionDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.flowgraph.LabeledTransitionDB...");
				e.printStackTrace();
			}
			try {
				found = entity.flowgraph.FlowGraphDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.flowgraph.FlowGraphDB...");
				e.printStackTrace();
			}
			
			return null;
		}
		
		public static PanacheEntity findByType(String type, long id) {
			if(type.equals("flowgraph.Start") ){
				return entity.flowgraph.StartDB.findById(id);
			} else if(type.equals("flowgraph.End") ){
				return entity.flowgraph.EndDB.findById(id);
			} else if(type.equals("flowgraph.Activity") ){
				return entity.flowgraph.ActivityDB.findById(id);
			} else if(type.equals("flowgraph.EActivityA") ){
				return entity.flowgraph.EActivityADB.findById(id);
			} else if(type.equals("flowgraph.EActivityB") ){
				return entity.flowgraph.EActivityBDB.findById(id);
			} else if(type.equals("flowgraph.ELibrary") ){
				return entity.flowgraph.ELibraryDB.findById(id);
			} else if(type.equals("flowgraph.SubFlowGraph") ){
				return entity.flowgraph.SubFlowGraphDB.findById(id);
			} else if(type.equals("flowgraph.Swimlane") ){
				return entity.flowgraph.SwimlaneDB.findById(id);
			} else if(type.equals("flowgraph.Transition") ){
				return entity.flowgraph.TransitionDB.findById(id);
			} else if(type.equals("flowgraph.LabeledTransition") ){
				return entity.flowgraph.LabeledTransitionDB.findById(id);
			} else if(type.equals("flowgraph.FlowGraph") ){
				return entity.flowgraph.FlowGraphDB.findById(id);
			}
			
			return null;
		}
		
		public static graphmodel.IdentifiableElement findApiByType(
			String type,
			long id,
			info.scce.pyro.core.command.FlowGraphCommandExecuter executer
		) {
			return findApiByType(type, id, executer, null, null);
		}
		
		public static graphmodel.IdentifiableElement findApiByType(
			String type,
			long id,
			info.scce.pyro.core.command.FlowGraphCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			if(type.equals("flowgraph.Start") ){
				entity.flowgraph.StartDB e = entity.flowgraph.StartDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("flowgraph.End") ){
				entity.flowgraph.EndDB e = entity.flowgraph.EndDB.findById(id);
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
			} else if(type.equals("flowgraph.EActivityA") ){
				entity.flowgraph.EActivityADB e = entity.flowgraph.EActivityADB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("flowgraph.EActivityB") ){
				entity.flowgraph.EActivityBDB e = entity.flowgraph.EActivityBDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("flowgraph.ELibrary") ){
				entity.flowgraph.ELibraryDB e = entity.flowgraph.ELibraryDB.findById(id);
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
			} else if(type.equals("flowgraph.Swimlane") ){
				entity.flowgraph.SwimlaneDB e = entity.flowgraph.SwimlaneDB.findById(id);
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
			} else if(type.equals("flowgraph.LabeledTransition") ){
				entity.flowgraph.LabeledTransitionDB e = entity.flowgraph.LabeledTransitionDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("flowgraph.FlowGraph") ){
				entity.flowgraph.FlowGraphDB e = entity.flowgraph.FlowGraphDB.findById(id);
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
			info.scce.pyro.core.command.FlowGraphCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			
			return null;
		}
	}
