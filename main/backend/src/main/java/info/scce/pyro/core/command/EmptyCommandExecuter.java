	package info.scce.pyro.core.command;
	
	import info.scce.cinco.product.empty.empty.util.TypeRegistry;
	import info.scce.pyro.core.graphmodel.BendingPoint;
	import graphmodel.*;
	import entity.core.PyroUserDB;
	import info.scce.pyro.sync.GraphModelWebSocket;
	import io.quarkus.hibernate.orm.panache.PanacheEntity;
	
	/**
	 * Author zweihoff
	 */
	public class EmptyCommandExecuter extends CommandExecuter {
		
		private info.scce.pyro.rest.ObjectCache objectCache;
		private GraphModelWebSocket graphModelWebSocket;
		
		public EmptyCommandExecuter(
			PyroUserDB user,
			info.scce.pyro.rest.ObjectCache objectCache,
			GraphModelWebSocket graphModelWebSocket,
			entity.empty.EmptyDB graph,
			java.util.List<info.scce.pyro.core.command.types.HighlightCommand> highlightings
		) {
			super(
				graphModelWebSocket,
				highlightings
			);
			this.objectCache = objectCache;
			super.batch = new BatchExecution(user,new info.scce.cinco.product.empty.empty.impl.EmptyImpl(graph,this));
		}
		
		/**
		 * NOTE: Use this if it is needed to utilize (/work on) the same batch of commands
		 * of the GraphModelCommandExecuter and on the one of a primeReferenced GraphModel
		 */
		public EmptyCommandExecuter(
			BatchExecution batch,
			GraphModelWebSocket graphModelWebSocket,
			java.util.List<info.scce.pyro.core.command.types.HighlightCommand> highlightings,
			info.scce.pyro.rest.ObjectCache objectCache
		) {
			super(
				graphModelWebSocket,
				highlightings
			);
			super.batch = batch;
			this.objectCache = objectCache;
		}
		
		public void removeEmpty(info.scce.cinco.product.empty.empty.Empty entity){
			//for complex props
			entity.delete();
			/*
			*/
		}
		
		

	    public void setEdgeDBComponents(PanacheEntity edge, Node source, Node target, java.util.List<BendingPoint> bendingPoints) {
	    	graphmodel.GraphModel graphModel = source.getRootElement();
	    	PanacheEntity e = TypeRegistry.getApiToDB(graphModel);
	    	
	    	// switch edge types
	    }
	    
	    public void updateIdentifiableElement(IdentifiableElement entity, info.scce.pyro.core.graphmodel.IdentifiableElement prev) {
		    if(entity instanceof info.scce.cinco.product.empty.empty.Empty) {
		    	updateEmptyProperties((info.scce.cinco.product.empty.empty.Empty) entity,(info.scce.pyro.empty.rest.Empty)prev);
		    	return;
		    }
	    }

		public void updateEmptyProperties(info.scce.cinco.product.empty.empty.Empty entity, info.scce.pyro.empty.rest.Empty prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.empty.rest.Empty.fromEntityProperties(
					(entity.empty.EmptyDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		//FOR NODE EDGE GRAPHMODEL TYPE
		public info.scce.cinco.product.empty.empty.Empty updateEmpty(info.scce.pyro.empty.rest.Empty update){
			entity.empty.EmptyDB dbEntity = entity.empty.EmptyDB.findById(update.getId());
			info.scce.cinco.product.empty.empty.Empty apiEntity = (info.scce.cinco.product.empty.empty.Empty) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateEmpty(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.empty.empty.Empty updateEmpty(info.scce.cinco.product.empty.empty.Empty apiEntity, info.scce.pyro.empty.rest.Empty update){
			// handle type
			return updateEmpty(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.empty.empty.Empty updateEmpty(info.scce.cinco.product.empty.empty.Empty apiEntity, info.scce.pyro.empty.rest.Empty update, boolean propagate){
			// handle type
			entity.empty.EmptyDB dbEntity = (entity.empty.EmptyDB) apiEntity.getDelegate();
			info.scce.pyro.empty.rest.Empty prev = info.scce.pyro.empty.rest.Empty.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			if(
				dbEntity.modelName != null &&
				!dbEntity.modelName.equals(update.getmodelName())
				||
				update.getmodelName() != null &&
				!update.getmodelName().equals(dbEntity.modelName)
				
			) { // value changed?
				dbEntity.modelName = update.getmodelName();
			}
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.empty.rest.Empty.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		@Override
		public void updateAppearance() {
			super.getAllModelElements().forEach((element)->{
			});
		}

	}
