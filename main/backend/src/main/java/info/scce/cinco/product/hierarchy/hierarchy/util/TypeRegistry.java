	package info.scce.cinco.product.hierarchy.hierarchy.util;
	
	import io.quarkus.hibernate.orm.panache.PanacheEntity;
	import info.scce.pyro.core.command.HierarchyCommandExecuter;
	
	public class TypeRegistry {
		
		public static String getTypeOf(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContA) {
				return "hierarchy.ContA";
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeA) {
				return "hierarchy.EdgeA";
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.D) {
				return "hierarchy.D";
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.Cont) {
				return "hierarchy.Cont";
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContD) {
				return "hierarchy.ContD";
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeD) {
				return "hierarchy.EdgeD";
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.A) {
				return "hierarchy.A";
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.TA) {
				return "hierarchy.TA";
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.TD) {
				return "hierarchy.TD";
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.Hierarchy) {
				return "hierarchy.Hierarchy";
			}
			
			return null;
		}
		
		public static String getTypeOf(PanacheEntity e) {
			if(e instanceof entity.hierarchy.ContADB) {
				return "hierarchy.ContA";
			} else if(e instanceof entity.hierarchy.EdgeADB) {
				return "hierarchy.EdgeA";
			} else if(e instanceof entity.hierarchy.DDB) {
				return "hierarchy.D";
			} else if(e instanceof entity.hierarchy.ContDB) {
				return "hierarchy.Cont";
			} else if(e instanceof entity.hierarchy.ContDDB) {
				return "hierarchy.ContD";
			} else if(e instanceof entity.hierarchy.EdgeDDB) {
				return "hierarchy.EdgeD";
			} else if(e instanceof entity.hierarchy.ADB) {
				return "hierarchy.A";
			} else if(e instanceof entity.hierarchy.TADB) {
				return "hierarchy.TA";
			} else if(e instanceof entity.hierarchy.TDDB) {
				return "hierarchy.TD";
			} else if(e instanceof entity.hierarchy.HierarchyDB) {
				return "hierarchy.Hierarchy";
			}
			
			return null;
		}
		
		public static String getTypeOf(info.scce.pyro.core.graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.pyro.hierarchy.rest.ContA) {
				return "hierarchy.ContA";
			} else if(e instanceof info.scce.pyro.hierarchy.rest.EdgeA) {
				return "hierarchy.EdgeA";
			} else if(e instanceof info.scce.pyro.hierarchy.rest.D) {
				return "hierarchy.D";
			} else if(e instanceof info.scce.pyro.hierarchy.rest.Cont) {
				return "hierarchy.Cont";
			} else if(e instanceof info.scce.pyro.hierarchy.rest.ContD) {
				return "hierarchy.ContD";
			} else if(e instanceof info.scce.pyro.hierarchy.rest.EdgeD) {
				return "hierarchy.EdgeD";
			} else if(e instanceof info.scce.pyro.hierarchy.rest.A) {
				return "hierarchy.A";
			} else if(e instanceof info.scce.pyro.hierarchy.rest.TA) {
				return "hierarchy.TA";
			} else if(e instanceof info.scce.pyro.hierarchy.rest.TD) {
				return "hierarchy.TD";
			} else if(e instanceof info.scce.pyro.hierarchy.rest.Hierarchy) {
				return "hierarchy.Hierarchy";
			}
			
			return null;
		}

		public static info.scce.pyro.core.graphmodel.IdentifiableElement getApiToRest(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContA) {
				info.scce.cinco.product.hierarchy.hierarchy.ContA apiE = (info.scce.cinco.product.hierarchy.hierarchy.ContA) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeA) {
				info.scce.cinco.product.hierarchy.hierarchy.EdgeA apiE = (info.scce.cinco.product.hierarchy.hierarchy.EdgeA) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.D) {
				info.scce.cinco.product.hierarchy.hierarchy.D apiE = (info.scce.cinco.product.hierarchy.hierarchy.D) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.Cont) {
				info.scce.cinco.product.hierarchy.hierarchy.Cont apiE = (info.scce.cinco.product.hierarchy.hierarchy.Cont) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContD) {
				info.scce.cinco.product.hierarchy.hierarchy.ContD apiE = (info.scce.cinco.product.hierarchy.hierarchy.ContD) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeD) {
				info.scce.cinco.product.hierarchy.hierarchy.EdgeD apiE = (info.scce.cinco.product.hierarchy.hierarchy.EdgeD) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.A) {
				info.scce.cinco.product.hierarchy.hierarchy.A apiE = (info.scce.cinco.product.hierarchy.hierarchy.A) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.TA) {
				info.scce.cinco.product.hierarchy.hierarchy.TA apiE = (info.scce.cinco.product.hierarchy.hierarchy.TA) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.TD) {
				info.scce.cinco.product.hierarchy.hierarchy.TD apiE = (info.scce.cinco.product.hierarchy.hierarchy.TD) e;
				return getDBToRest(apiE.getDelegate());
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.Hierarchy) {
				info.scce.cinco.product.hierarchy.hierarchy.Hierarchy apiE = (info.scce.cinco.product.hierarchy.hierarchy.Hierarchy) e;
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
			if(e instanceof entity.hierarchy.ContADB) {
				entity.hierarchy.ContADB en = (entity.hierarchy.ContADB) e;
				if(onlyProperties) {
					return info.scce.pyro.hierarchy.rest.ContA.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.hierarchy.rest.ContA.fromEntity(en, cache);
				}
			} else if(e instanceof entity.hierarchy.EdgeADB) {
				entity.hierarchy.EdgeADB en = (entity.hierarchy.EdgeADB) e;
				if(onlyProperties) {
					return info.scce.pyro.hierarchy.rest.EdgeA.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.hierarchy.rest.EdgeA.fromEntity(en, cache);
				}
			} else if(e instanceof entity.hierarchy.DDB) {
				entity.hierarchy.DDB en = (entity.hierarchy.DDB) e;
				if(onlyProperties) {
					return info.scce.pyro.hierarchy.rest.D.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.hierarchy.rest.D.fromEntity(en, cache);
				}
			} else if(e instanceof entity.hierarchy.ContDB) {
				entity.hierarchy.ContDB en = (entity.hierarchy.ContDB) e;
				if(onlyProperties) {
					return info.scce.pyro.hierarchy.rest.Cont.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.hierarchy.rest.Cont.fromEntity(en, cache);
				}
			} else if(e instanceof entity.hierarchy.ContDDB) {
				entity.hierarchy.ContDDB en = (entity.hierarchy.ContDDB) e;
				if(onlyProperties) {
					return info.scce.pyro.hierarchy.rest.ContD.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.hierarchy.rest.ContD.fromEntity(en, cache);
				}
			} else if(e instanceof entity.hierarchy.EdgeDDB) {
				entity.hierarchy.EdgeDDB en = (entity.hierarchy.EdgeDDB) e;
				if(onlyProperties) {
					return info.scce.pyro.hierarchy.rest.EdgeD.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.hierarchy.rest.EdgeD.fromEntity(en, cache);
				}
			} else if(e instanceof entity.hierarchy.ADB) {
				entity.hierarchy.ADB en = (entity.hierarchy.ADB) e;
				if(onlyProperties) {
					return info.scce.pyro.hierarchy.rest.A.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.hierarchy.rest.A.fromEntity(en, cache);
				}
			} else if(e instanceof entity.hierarchy.TADB) {
				entity.hierarchy.TADB en = (entity.hierarchy.TADB) e;
				if(onlyProperties) {
					return info.scce.pyro.hierarchy.rest.TA.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.hierarchy.rest.TA.fromEntity(en, cache);
				}
			} else if(e instanceof entity.hierarchy.TDDB) {
				entity.hierarchy.TDDB en = (entity.hierarchy.TDDB) e;
				if(onlyProperties) {
					return info.scce.pyro.hierarchy.rest.TD.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.hierarchy.rest.TD.fromEntity(en, cache);
				}
			} else if(e instanceof entity.hierarchy.HierarchyDB) {
				entity.hierarchy.HierarchyDB en = (entity.hierarchy.HierarchyDB) e;
				if(onlyProperties) {
					return info.scce.pyro.hierarchy.rest.Hierarchy.fromEntityProperties(en, cache);
				} else {
					return info.scce.pyro.hierarchy.rest.Hierarchy.fromEntity(en, cache);
				}
			}
			return getDBToRestPrime(e, cache, onlyProperties);
		}
		
		public static info.scce.pyro.core.graphmodel.IdentifiableElement getDBToRestPrime(PanacheEntity e, info.scce.pyro.rest.ObjectCache cache, boolean onlyProperties) {
			return null;
		}

		public static PanacheEntity getApiToDB(graphmodel.IdentifiableElement e) {
			if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContA) {
				info.scce.cinco.product.hierarchy.hierarchy.ContA apiE = (info.scce.cinco.product.hierarchy.hierarchy.ContA) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeA) {
				info.scce.cinco.product.hierarchy.hierarchy.EdgeA apiE = (info.scce.cinco.product.hierarchy.hierarchy.EdgeA) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.D) {
				info.scce.cinco.product.hierarchy.hierarchy.D apiE = (info.scce.cinco.product.hierarchy.hierarchy.D) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.Cont) {
				info.scce.cinco.product.hierarchy.hierarchy.Cont apiE = (info.scce.cinco.product.hierarchy.hierarchy.Cont) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.ContD) {
				info.scce.cinco.product.hierarchy.hierarchy.ContD apiE = (info.scce.cinco.product.hierarchy.hierarchy.ContD) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeD) {
				info.scce.cinco.product.hierarchy.hierarchy.EdgeD apiE = (info.scce.cinco.product.hierarchy.hierarchy.EdgeD) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.A) {
				info.scce.cinco.product.hierarchy.hierarchy.A apiE = (info.scce.cinco.product.hierarchy.hierarchy.A) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.TA) {
				info.scce.cinco.product.hierarchy.hierarchy.TA apiE = (info.scce.cinco.product.hierarchy.hierarchy.TA) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.TD) {
				info.scce.cinco.product.hierarchy.hierarchy.TD apiE = (info.scce.cinco.product.hierarchy.hierarchy.TD) e;
				return apiE.getDelegate();
			} else if(e instanceof info.scce.cinco.product.hierarchy.hierarchy.Hierarchy) {
				info.scce.cinco.product.hierarchy.hierarchy.Hierarchy apiE = (info.scce.cinco.product.hierarchy.hierarchy.Hierarchy) e;
				return apiE.getDelegate();
			}
			
			return null;
		}
		
		public static graphmodel.IdentifiableElement getDBToApi(
			PanacheEntity e,
			info.scce.pyro.core.command.HierarchyCommandExecuter executer
		) {
			return getDBToApi(e, executer, null, null);
		}
		
		public static graphmodel.IdentifiableElement getDBToApi(
			PanacheEntity e,
			HierarchyCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			if(e instanceof entity.hierarchy.ContADB) {
				entity.hierarchy.ContADB en = (entity.hierarchy.ContADB) e;
				return new info.scce.cinco.product.hierarchy.hierarchy.impl.ContAImpl(en, executer);
			} else if(e instanceof entity.hierarchy.EdgeADB) {
				entity.hierarchy.EdgeADB en = (entity.hierarchy.EdgeADB) e;
				return new info.scce.cinco.product.hierarchy.hierarchy.impl.EdgeAImpl(en, executer);
			} else if(e instanceof entity.hierarchy.DDB) {
				entity.hierarchy.DDB en = (entity.hierarchy.DDB) e;
				return new info.scce.cinco.product.hierarchy.hierarchy.impl.DImpl(en, executer);
			} else if(e instanceof entity.hierarchy.ContDB) {
				entity.hierarchy.ContDB en = (entity.hierarchy.ContDB) e;
				return new info.scce.cinco.product.hierarchy.hierarchy.impl.ContImpl(en, executer);
			} else if(e instanceof entity.hierarchy.ContDDB) {
				entity.hierarchy.ContDDB en = (entity.hierarchy.ContDDB) e;
				return new info.scce.cinco.product.hierarchy.hierarchy.impl.ContDImpl(en, executer);
			} else if(e instanceof entity.hierarchy.EdgeDDB) {
				entity.hierarchy.EdgeDDB en = (entity.hierarchy.EdgeDDB) e;
				return new info.scce.cinco.product.hierarchy.hierarchy.impl.EdgeDImpl(en, executer);
			} else if(e instanceof entity.hierarchy.ADB) {
				entity.hierarchy.ADB en = (entity.hierarchy.ADB) e;
				return new info.scce.cinco.product.hierarchy.hierarchy.impl.AImpl(en, executer);
			} else if(e instanceof entity.hierarchy.TADB) {
				entity.hierarchy.TADB en = (entity.hierarchy.TADB) e;
				return new info.scce.cinco.product.hierarchy.hierarchy.impl.TAImpl(en, executer, parent, prev);
			} else if(e instanceof entity.hierarchy.TDDB) {
				entity.hierarchy.TDDB en = (entity.hierarchy.TDDB) e;
				return new info.scce.cinco.product.hierarchy.hierarchy.impl.TDImpl(en, executer, parent, prev);
			} else if(e instanceof entity.hierarchy.HierarchyDB) {
				entity.hierarchy.HierarchyDB en = (entity.hierarchy.HierarchyDB) e;
				return new info.scce.cinco.product.hierarchy.hierarchy.impl.HierarchyImpl(en, executer);
			}
			return getDBToApiPrime(e, executer, parent, prev);
		}
		
		public static graphmodel.IdentifiableElement getDBToApiPrime(
			PanacheEntity e,
			HierarchyCommandExecuter executer,
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
			if(info.scce.cinco.product.hierarchy.hierarchy.ContA.class.equals(entityClass)) {
				return entity.hierarchy.ContADB.findById(id);
			} else if(info.scce.cinco.product.hierarchy.hierarchy.EdgeA.class.equals(entityClass)) {
				return entity.hierarchy.EdgeADB.findById(id);
			} else if(info.scce.cinco.product.hierarchy.hierarchy.D.class.equals(entityClass)) {
				return entity.hierarchy.DDB.findById(id);
			} else if(info.scce.cinco.product.hierarchy.hierarchy.Cont.class.equals(entityClass)) {
				return entity.hierarchy.ContDB.findById(id);
			} else if(info.scce.cinco.product.hierarchy.hierarchy.ContD.class.equals(entityClass)) {
				return entity.hierarchy.ContDDB.findById(id);
			} else if(info.scce.cinco.product.hierarchy.hierarchy.EdgeD.class.equals(entityClass)) {
				return entity.hierarchy.EdgeDDB.findById(id);
			} else if(info.scce.cinco.product.hierarchy.hierarchy.A.class.equals(entityClass)) {
				return entity.hierarchy.ADB.findById(id);
			} else if(info.scce.cinco.product.hierarchy.hierarchy.TA.class.equals(entityClass)) {
				return entity.hierarchy.TADB.findById(id);
			} else if(info.scce.cinco.product.hierarchy.hierarchy.TD.class.equals(entityClass)) {
				return entity.hierarchy.TDDB.findById(id);
			} else if(info.scce.cinco.product.hierarchy.hierarchy.Hierarchy.class.equals(entityClass)) {
				return entity.hierarchy.HierarchyDB.findById(id);
			}
			
			return null;
		}
		
		public static PanacheEntity findAbstractEntityByRest(long id, Class<?> entityClass) {
			if(info.scce.pyro.hierarchy.rest.ContA.class.equals(entityClass)) {
				return entity.hierarchy.ContADB.findById(id);
			} else if(info.scce.pyro.hierarchy.rest.EdgeA.class.equals(entityClass)) {
				return entity.hierarchy.EdgeADB.findById(id);
			} else if(info.scce.pyro.hierarchy.rest.D.class.equals(entityClass)) {
				return entity.hierarchy.DDB.findById(id);
			} else if(info.scce.pyro.hierarchy.rest.Cont.class.equals(entityClass)) {
				return entity.hierarchy.ContDB.findById(id);
			} else if(info.scce.pyro.hierarchy.rest.ContD.class.equals(entityClass)) {
				return entity.hierarchy.ContDDB.findById(id);
			} else if(info.scce.pyro.hierarchy.rest.EdgeD.class.equals(entityClass)) {
				return entity.hierarchy.EdgeDDB.findById(id);
			} else if(info.scce.pyro.hierarchy.rest.A.class.equals(entityClass)) {
				return entity.hierarchy.ADB.findById(id);
			} else if(info.scce.pyro.hierarchy.rest.TA.class.equals(entityClass)) {
				return entity.hierarchy.TADB.findById(id);
			} else if(info.scce.pyro.hierarchy.rest.TD.class.equals(entityClass)) {
				return entity.hierarchy.TDDB.findById(id);
			} else if(info.scce.pyro.hierarchy.rest.Hierarchy.class.equals(entityClass)) {
				return entity.hierarchy.HierarchyDB.findById(id);
			}
			
			return null;
		}
		
		public static PanacheEntity findAbstractEntityByEntity(long id, Class<?> entityClass) {
			if(entity.hierarchy.ContADB.class.equals(entityClass)) {
				return entity.hierarchy.ContADB.findById(id);
			} else if(entity.hierarchy.EdgeADB.class.equals(entityClass)) {
				return entity.hierarchy.EdgeADB.findById(id);
			} else if(entity.hierarchy.DDB.class.equals(entityClass)) {
				return entity.hierarchy.DDB.findById(id);
			} else if(entity.hierarchy.ContDB.class.equals(entityClass)) {
				return entity.hierarchy.ContDB.findById(id);
			} else if(entity.hierarchy.ContDDB.class.equals(entityClass)) {
				return entity.hierarchy.ContDDB.findById(id);
			} else if(entity.hierarchy.EdgeDDB.class.equals(entityClass)) {
				return entity.hierarchy.EdgeDDB.findById(id);
			} else if(entity.hierarchy.ADB.class.equals(entityClass)) {
				return entity.hierarchy.ADB.findById(id);
			} else if(entity.hierarchy.TADB.class.equals(entityClass)) {
				return entity.hierarchy.TADB.findById(id);
			} else if(entity.hierarchy.TDDB.class.equals(entityClass)) {
				return entity.hierarchy.TDDB.findById(id);
			} else if(entity.hierarchy.HierarchyDB.class.equals(entityClass)) {
				return entity.hierarchy.HierarchyDB.findById(id);
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
				found = entity.hierarchy.ContADB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.hierarchy.ContADB...");
				e.printStackTrace();
			}
			try {
				found = entity.hierarchy.EdgeADB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.hierarchy.EdgeADB...");
				e.printStackTrace();
			}
			try {
				found = entity.hierarchy.DDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.hierarchy.DDB...");
				e.printStackTrace();
			}
			try {
				found = entity.hierarchy.ContDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.hierarchy.ContDB...");
				e.printStackTrace();
			}
			try {
				found = entity.hierarchy.ContDDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.hierarchy.ContDDB...");
				e.printStackTrace();
			}
			try {
				found = entity.hierarchy.EdgeDDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.hierarchy.EdgeDDB...");
				e.printStackTrace();
			}
			try {
				found = entity.hierarchy.ADB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.hierarchy.ADB...");
				e.printStackTrace();
			}
			try {
				found = entity.hierarchy.TADB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.hierarchy.TADB...");
				e.printStackTrace();
			}
			try {
				found = entity.hierarchy.TDDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.hierarchy.TDDB...");
				e.printStackTrace();
			}
			try {
				found = entity.hierarchy.HierarchyDB.findById(id);
				if(found != null) {
					return found;
				}
			} catch(Exception e) {
				System.out.println("the id is not associated with entity.hierarchy.HierarchyDB...");
				e.printStackTrace();
			}
			
			return null;
		}
		
		public static PanacheEntity findByType(String type, long id) {
			if(type.equals("hierarchy.ContA") ){
				return entity.hierarchy.ContADB.findById(id);
			} else if(type.equals("hierarchy.EdgeA") ){
				return entity.hierarchy.EdgeADB.findById(id);
			} else if(type.equals("hierarchy.D") ){
				return entity.hierarchy.DDB.findById(id);
			} else if(type.equals("hierarchy.Cont") ){
				return entity.hierarchy.ContDB.findById(id);
			} else if(type.equals("hierarchy.ContD") ){
				return entity.hierarchy.ContDDB.findById(id);
			} else if(type.equals("hierarchy.EdgeD") ){
				return entity.hierarchy.EdgeDDB.findById(id);
			} else if(type.equals("hierarchy.A") ){
				return entity.hierarchy.ADB.findById(id);
			} else if(type.equals("hierarchy.TA") ){
				return entity.hierarchy.TADB.findById(id);
			} else if(type.equals("hierarchy.TD") ){
				return entity.hierarchy.TDDB.findById(id);
			} else if(type.equals("hierarchy.Hierarchy") ){
				return entity.hierarchy.HierarchyDB.findById(id);
			}
			
			return null;
		}
		
		public static graphmodel.IdentifiableElement findApiByType(
			String type,
			long id,
			info.scce.pyro.core.command.HierarchyCommandExecuter executer
		) {
			return findApiByType(type, id, executer, null, null);
		}
		
		public static graphmodel.IdentifiableElement findApiByType(
			String type,
			long id,
			info.scce.pyro.core.command.HierarchyCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			if(type.equals("hierarchy.ContA") ){
				entity.hierarchy.ContADB e = entity.hierarchy.ContADB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("hierarchy.EdgeA") ){
				entity.hierarchy.EdgeADB e = entity.hierarchy.EdgeADB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("hierarchy.D") ){
				entity.hierarchy.DDB e = entity.hierarchy.DDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("hierarchy.Cont") ){
				entity.hierarchy.ContDB e = entity.hierarchy.ContDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("hierarchy.ContD") ){
				entity.hierarchy.ContDDB e = entity.hierarchy.ContDDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("hierarchy.EdgeD") ){
				entity.hierarchy.EdgeDDB e = entity.hierarchy.EdgeDDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("hierarchy.A") ){
				entity.hierarchy.ADB e = entity.hierarchy.ADB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("hierarchy.TA") ){
				entity.hierarchy.TADB e = entity.hierarchy.TADB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("hierarchy.TD") ){
				entity.hierarchy.TDDB e = entity.hierarchy.TDDB.findById(id);
				return getDBToApi(
					e,
					executer,
					parent,
					prev
				);
			} else if(type.equals("hierarchy.Hierarchy") ){
				entity.hierarchy.HierarchyDB e = entity.hierarchy.HierarchyDB.findById(id);
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
			info.scce.pyro.core.command.HierarchyCommandExecuter executer,
			graphmodel.IdentifiableElement parent,
			info.scce.pyro.core.graphmodel.IdentifiableElement prev
		) {
			
			return null;
		}
	}
