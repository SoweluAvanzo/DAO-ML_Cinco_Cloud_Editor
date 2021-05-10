	package info.scce.cinco.product.empty.empty.util;
	
	import io.quarkus.hibernate.orm.panache.PanacheEntity;
	import info.scce.pyro.core.command.EmptyCommandExecuter;
	
	public class TypeRegistry {
		
		public static String getTypeOf(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.empty.empty.Empty) {
				return "empty.Empty";
			}
			
			return null;
		}
		
		public static String getTypeOf(PanacheEntity e) {
			if(e instanceof entity.empty.EmptyDB) {
				return "empty.Empty";
			}
			
			return null;
		}
		
		public static String getTypeOf(info.scce.pyro.core.graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.pyro.empty.rest.Empty) {
				return "empty.Empty";
			}
			
			return null;
		}

		public static info.scce.pyro.core.graphmodel.IdentifiableElement getApiToRest(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.empty.empty.Empty) {
				info.scce.cinco.product.empty.empty.Empty apiE = (info.scce.cinco.product.empty.empty.Empty) e;
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
			if(e instanceof entity.empty.EmptyDB) {
				entity.empty.EmptyDB en = (entity.empty.EmptyDB) e;
				if(onlyProperties) {
					return info.scce.pyro.empty.rest.Empty.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.empty.rest.Empty.fromEntity(en, cache);
				}
			}
			return getDBToRestPrime(e, cache, onlyProperties);
		}
		
		public static info.scce.pyro.core.graphmodel.IdentifiableElement getDBToRestPrime(PanacheEntity e, info.scce.pyro.rest.ObjectCache cache, boolean onlyProperties) {
			return null;
		}

		public static PanacheEntity getApiToDB(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.empty.empty.Empty) {
				info.scce.cinco.product.empty.empty.Empty apiE = (info.scce.cinco.product.empty.empty.Empty) e;
				return apiE.getDelegate();
			}
			
			return null;
		}
		
		public static graphmodel.IdentifiableElement getDBToApi(
			PanacheEntity e,
			info.scce.pyro.core.command.EmptyCommandExecuter executer
		) {
			return getDBToApi(e, executer, null, null);
		}
		
		public static graphmodel.IdentifiableElement getDBToApi(
			PanacheEntity e,
			EmptyCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			if(e instanceof entity.empty.EmptyDB) {
				entity.empty.EmptyDB en = (entity.empty.EmptyDB) e;
				return new info.scce.cinco.product.empty.empty.impl.EmptyImpl(en, executer);
			}
			return getDBToApiPrime(e, executer, parent, prev);
		}
		
		public static graphmodel.IdentifiableElement getDBToApiPrime(
			PanacheEntity e,
			EmptyCommandExecuter executer,
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
			if(info.scce.cinco.product.empty.empty.Empty.class.equals(entityClass)) {
				return entity.empty.EmptyDB.findById(id);
			}
			
			return null;
		}
		
		public static PanacheEntity findAbstractEntityByRest(long id, Class<?> entityClass) {
			if(info.scce.pyro.empty.rest.Empty.class.equals(entityClass)) {
				return entity.empty.EmptyDB.findById(id);
			}
			
			return null;
		}
		
		public static PanacheEntity findAbstractEntityByEntity(long id, Class<?> entityClass) {
			if(entity.empty.EmptyDB.class.equals(entityClass)) {
				return entity.empty.EmptyDB.findById(id);
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
				found = entity.empty.EmptyDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.empty.EmptyDB...");
				e.printStackTrace();
			}
			
			return null;
		}
		
		public static PanacheEntity findByType(String type, long id) {
			if(type.equals("empty.Empty") ){
				return entity.empty.EmptyDB.findById(id);
			}
			
			return null;
		}
		
		public static graphmodel.IdentifiableElement findApiByType(
			String type,
			long id,
			info.scce.pyro.core.command.EmptyCommandExecuter executer
		) {
			return findApiByType(type, id, executer, null, null);
		}
		
		public static graphmodel.IdentifiableElement findApiByType(
			String type,
			long id,
			info.scce.pyro.core.command.EmptyCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			if(type.equals("empty.Empty") ){
				entity.empty.EmptyDB e = entity.empty.EmptyDB.findById(id);
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
			info.scce.pyro.core.command.EmptyCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			
			return null;
		}
	}
