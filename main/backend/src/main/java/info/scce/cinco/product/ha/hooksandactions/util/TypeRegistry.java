	package info.scce.cinco.product.ha.hooksandactions.util;
	
	import io.quarkus.hibernate.orm.panache.PanacheEntity;
	import info.scce.pyro.core.command.HooksAndActionsCommandExecuter;
	
	public class TypeRegistry {
		
		public static String getTypeOf(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.ha.hooksandactions.HookAContainer) {
				return "hooksandactions.HookAContainer";
			} else if(e instanceof info.scce.cinco.product.ha.hooksandactions.HookAnEdge) {
				return "hooksandactions.HookAnEdge";
			} else if(e instanceof info.scce.cinco.product.ha.hooksandactions.HookANode) {
				return "hooksandactions.HookANode";
			} else if(e instanceof info.scce.cinco.product.ha.hooksandactions.HookAType) {
				return "hooksandactions.HookAType";
			} else if(e instanceof info.scce.cinco.product.ha.hooksandactions.HooksAndActions) {
				return "hooksandactions.HooksAndActions";
			}
			
			return null;
		}
		
		public static String getTypeOf(PanacheEntity e) {
			if(e instanceof entity.hooksandactions.HookAContainerDB) {
				return "hooksandactions.HookAContainer";
			} else if(e instanceof entity.hooksandactions.HookAnEdgeDB) {
				return "hooksandactions.HookAnEdge";
			} else if(e instanceof entity.hooksandactions.HookANodeDB) {
				return "hooksandactions.HookANode";
			} else if(e instanceof entity.hooksandactions.HookATypeDB) {
				return "hooksandactions.HookAType";
			} else if(e instanceof entity.hooksandactions.HooksAndActionsDB) {
				return "hooksandactions.HooksAndActions";
			}
			
			return null;
		}
		
		public static String getTypeOf(info.scce.pyro.core.graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.pyro.hooksandactions.rest.HookAContainer) {
				return "hooksandactions.HookAContainer";
			} else if(e instanceof info.scce.pyro.hooksandactions.rest.HookAnEdge) {
				return "hooksandactions.HookAnEdge";
			} else if(e instanceof info.scce.pyro.hooksandactions.rest.HookANode) {
				return "hooksandactions.HookANode";
			} else if(e instanceof info.scce.pyro.hooksandactions.rest.HookAType) {
				return "hooksandactions.HookAType";
			} else if(e instanceof info.scce.pyro.hooksandactions.rest.HooksAndActions) {
				return "hooksandactions.HooksAndActions";
			}
			
			return null;
		}

		public static info.scce.pyro.core.graphmodel.IdentifiableElement getApiToRest(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.ha.hooksandactions.HookAContainer) {
				info.scce.cinco.product.ha.hooksandactions.HookAContainer apiE = (info.scce.cinco.product.ha.hooksandactions.HookAContainer) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.ha.hooksandactions.HookAnEdge) {
				info.scce.cinco.product.ha.hooksandactions.HookAnEdge apiE = (info.scce.cinco.product.ha.hooksandactions.HookAnEdge) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.ha.hooksandactions.HookANode) {
				info.scce.cinco.product.ha.hooksandactions.HookANode apiE = (info.scce.cinco.product.ha.hooksandactions.HookANode) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.ha.hooksandactions.HookAType) {
				info.scce.cinco.product.ha.hooksandactions.HookAType apiE = (info.scce.cinco.product.ha.hooksandactions.HookAType) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.ha.hooksandactions.HooksAndActions) {
				info.scce.cinco.product.ha.hooksandactions.HooksAndActions apiE = (info.scce.cinco.product.ha.hooksandactions.HooksAndActions) e;
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
			if(e instanceof entity.hooksandactions.HookAContainerDB) {
				entity.hooksandactions.HookAContainerDB en = (entity.hooksandactions.HookAContainerDB) e;
				if(onlyProperties) {
					return info.scce.pyro.hooksandactions.rest.HookAContainer.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.hooksandactions.rest.HookAContainer.fromEntity(en, cache);
				}
			} else if(e instanceof entity.hooksandactions.HookAnEdgeDB) {
				entity.hooksandactions.HookAnEdgeDB en = (entity.hooksandactions.HookAnEdgeDB) e;
				if(onlyProperties) {
					return info.scce.pyro.hooksandactions.rest.HookAnEdge.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.hooksandactions.rest.HookAnEdge.fromEntity(en, cache);
				}
			} else if(e instanceof entity.hooksandactions.HookANodeDB) {
				entity.hooksandactions.HookANodeDB en = (entity.hooksandactions.HookANodeDB) e;
				if(onlyProperties) {
					return info.scce.pyro.hooksandactions.rest.HookANode.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.hooksandactions.rest.HookANode.fromEntity(en, cache);
				}
			} else if(e instanceof entity.hooksandactions.HookATypeDB) {
				entity.hooksandactions.HookATypeDB en = (entity.hooksandactions.HookATypeDB) e;
				if(onlyProperties) {
					return info.scce.pyro.hooksandactions.rest.HookAType.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.hooksandactions.rest.HookAType.fromEntity(en, cache);
				}
			} else if(e instanceof entity.hooksandactions.HooksAndActionsDB) {
				entity.hooksandactions.HooksAndActionsDB en = (entity.hooksandactions.HooksAndActionsDB) e;
				if(onlyProperties) {
					return info.scce.pyro.hooksandactions.rest.HooksAndActions.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.hooksandactions.rest.HooksAndActions.fromEntity(en, cache);
				}
			}
			return getDBToRestPrime(e, cache, onlyProperties);
		}
		
		public static info.scce.pyro.core.graphmodel.IdentifiableElement getDBToRestPrime(PanacheEntity e, info.scce.pyro.rest.ObjectCache cache, boolean onlyProperties) {
			return null;
		}

		public static PanacheEntity getApiToDB(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.ha.hooksandactions.HookAContainer) {
				info.scce.cinco.product.ha.hooksandactions.HookAContainer apiE = (info.scce.cinco.product.ha.hooksandactions.HookAContainer) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.ha.hooksandactions.HookAnEdge) {
				info.scce.cinco.product.ha.hooksandactions.HookAnEdge apiE = (info.scce.cinco.product.ha.hooksandactions.HookAnEdge) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.ha.hooksandactions.HookANode) {
				info.scce.cinco.product.ha.hooksandactions.HookANode apiE = (info.scce.cinco.product.ha.hooksandactions.HookANode) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.ha.hooksandactions.HookAType) {
				info.scce.cinco.product.ha.hooksandactions.HookAType apiE = (info.scce.cinco.product.ha.hooksandactions.HookAType) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.ha.hooksandactions.HooksAndActions) {
				info.scce.cinco.product.ha.hooksandactions.HooksAndActions apiE = (info.scce.cinco.product.ha.hooksandactions.HooksAndActions) e;
				return apiE.getDelegate();
			}
			
			return null;
		}
		
		public static graphmodel.IdentifiableElement getDBToApi(
			PanacheEntity e,
			info.scce.pyro.core.command.HooksAndActionsCommandExecuter executer
		) {
			return getDBToApi(e, executer, null, null);
		}
		
		public static graphmodel.IdentifiableElement getDBToApi(
			PanacheEntity e,
			HooksAndActionsCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			if(e instanceof entity.hooksandactions.HookAContainerDB) {
				entity.hooksandactions.HookAContainerDB en = (entity.hooksandactions.HookAContainerDB) e;
				return new info.scce.cinco.product.ha.hooksandactions.impl.HookAContainerImpl(en, executer);
			} else if(e instanceof entity.hooksandactions.HookAnEdgeDB) {
				entity.hooksandactions.HookAnEdgeDB en = (entity.hooksandactions.HookAnEdgeDB) e;
				return new info.scce.cinco.product.ha.hooksandactions.impl.HookAnEdgeImpl(en, executer);
			} else if(e instanceof entity.hooksandactions.HookANodeDB) {
				entity.hooksandactions.HookANodeDB en = (entity.hooksandactions.HookANodeDB) e;
				return new info.scce.cinco.product.ha.hooksandactions.impl.HookANodeImpl(en, executer);
			} else if(e instanceof entity.hooksandactions.HookATypeDB) {
				entity.hooksandactions.HookATypeDB en = (entity.hooksandactions.HookATypeDB) e;
				return new info.scce.cinco.product.ha.hooksandactions.impl.HookATypeImpl(en, executer, parent, prev);
			} else if(e instanceof entity.hooksandactions.HooksAndActionsDB) {
				entity.hooksandactions.HooksAndActionsDB en = (entity.hooksandactions.HooksAndActionsDB) e;
				return new info.scce.cinco.product.ha.hooksandactions.impl.HooksAndActionsImpl(en, executer);
			}
			return getDBToApiPrime(e, executer, parent, prev);
		}
		
		public static graphmodel.IdentifiableElement getDBToApiPrime(
			PanacheEntity e,
			HooksAndActionsCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
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
			if(info.scce.cinco.product.ha.hooksandactions.HookAContainer.class.equals(entityClass)) {
				return entity.hooksandactions.HookAContainerDB.findById(id);
			} else if(info.scce.cinco.product.ha.hooksandactions.HookAnEdge.class.equals(entityClass)) {
				return entity.hooksandactions.HookAnEdgeDB.findById(id);
			} else if(info.scce.cinco.product.ha.hooksandactions.HookANode.class.equals(entityClass)) {
				return entity.hooksandactions.HookANodeDB.findById(id);
			} else if(info.scce.cinco.product.ha.hooksandactions.HookAType.class.equals(entityClass)) {
				return entity.hooksandactions.HookATypeDB.findById(id);
			} else if(info.scce.cinco.product.ha.hooksandactions.HooksAndActions.class.equals(entityClass)) {
				return entity.hooksandactions.HooksAndActionsDB.findById(id);
			}
			
			return null;
		}
		
		public static PanacheEntity findAbstractEntityByRest(long id, Class<?> entityClass) {
			if(info.scce.pyro.hooksandactions.rest.HookAContainer.class.equals(entityClass)) {
				return entity.hooksandactions.HookAContainerDB.findById(id);
			} else if(info.scce.pyro.hooksandactions.rest.HookAnEdge.class.equals(entityClass)) {
				return entity.hooksandactions.HookAnEdgeDB.findById(id);
			} else if(info.scce.pyro.hooksandactions.rest.HookANode.class.equals(entityClass)) {
				return entity.hooksandactions.HookANodeDB.findById(id);
			} else if(info.scce.pyro.hooksandactions.rest.HookAType.class.equals(entityClass)) {
				return entity.hooksandactions.HookATypeDB.findById(id);
			} else if(info.scce.pyro.hooksandactions.rest.HooksAndActions.class.equals(entityClass)) {
				return entity.hooksandactions.HooksAndActionsDB.findById(id);
			}
			
			return null;
		}
		
		public static PanacheEntity findAbstractEntityByEntity(long id, Class<?> entityClass) {
			if(entity.hooksandactions.HookAContainerDB.class.equals(entityClass)) {
				return entity.hooksandactions.HookAContainerDB.findById(id);
			} else if(entity.hooksandactions.HookAnEdgeDB.class.equals(entityClass)) {
				return entity.hooksandactions.HookAnEdgeDB.findById(id);
			} else if(entity.hooksandactions.HookANodeDB.class.equals(entityClass)) {
				return entity.hooksandactions.HookANodeDB.findById(id);
			} else if(entity.hooksandactions.HookATypeDB.class.equals(entityClass)) {
				return entity.hooksandactions.HookATypeDB.findById(id);
			} else if(entity.hooksandactions.HooksAndActionsDB.class.equals(entityClass)) {
				return entity.hooksandactions.HooksAndActionsDB.findById(id);
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
				found = entity.hooksandactions.HookAContainerDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.hooksandactions.HookAContainerDB...");
				e.printStackTrace();
			}
			try {
				found = entity.hooksandactions.HookAnEdgeDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.hooksandactions.HookAnEdgeDB...");
				e.printStackTrace();
			}
			try {
				found = entity.hooksandactions.HookANodeDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.hooksandactions.HookANodeDB...");
				e.printStackTrace();
			}
			try {
				found = entity.hooksandactions.HookATypeDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.hooksandactions.HookATypeDB...");
				e.printStackTrace();
			}
			try {
				found = entity.hooksandactions.HooksAndActionsDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.hooksandactions.HooksAndActionsDB...");
				e.printStackTrace();
			}
			
			return null;
		}
		
		public static PanacheEntity findByType(String type, long id) {
			if(type.equals("hooksandactions.HookAContainer") ){
				return entity.hooksandactions.HookAContainerDB.findById(id);
			} else if(type.equals("hooksandactions.HookAnEdge") ){
				return entity.hooksandactions.HookAnEdgeDB.findById(id);
			} else if(type.equals("hooksandactions.HookANode") ){
				return entity.hooksandactions.HookANodeDB.findById(id);
			} else if(type.equals("hooksandactions.HookAType") ){
				return entity.hooksandactions.HookATypeDB.findById(id);
			} else if(type.equals("hooksandactions.HooksAndActions") ){
				return entity.hooksandactions.HooksAndActionsDB.findById(id);
			}
			
			return null;
		}
		
		public static graphmodel.IdentifiableElement findApiByType(
			String type,
			long id,
			info.scce.pyro.core.command.HooksAndActionsCommandExecuter executer
		) {
			return findApiByType(type, id, executer, null, null);
		}
		
		public static graphmodel.IdentifiableElement findApiByType(
			String type,
			long id,
			info.scce.pyro.core.command.HooksAndActionsCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			if(type.equals("hooksandactions.HookAContainer") ){
				entity.hooksandactions.HookAContainerDB e = entity.hooksandactions.HookAContainerDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("hooksandactions.HookAnEdge") ){
				entity.hooksandactions.HookAnEdgeDB e = entity.hooksandactions.HookAnEdgeDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("hooksandactions.HookANode") ){
				entity.hooksandactions.HookANodeDB e = entity.hooksandactions.HookANodeDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("hooksandactions.HookAType") ){
				entity.hooksandactions.HookATypeDB e = entity.hooksandactions.HookATypeDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("hooksandactions.HooksAndActions") ){
				entity.hooksandactions.HooksAndActionsDB e = entity.hooksandactions.HooksAndActionsDB.findById(id);
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
			info.scce.pyro.core.command.HooksAndActionsCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			
			return null;
		}
	}
