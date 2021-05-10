	package info.scce.pyro.core.command;
	
	import info.scce.cinco.product.ha.hooksandactions.util.TypeRegistry;
	import info.scce.pyro.core.graphmodel.BendingPoint;
	import graphmodel.*;
	import entity.core.PyroUserDB;
	import info.scce.pyro.sync.GraphModelWebSocket;
	import io.quarkus.hibernate.orm.panache.PanacheEntity;
	
	/**
	 * Author zweihoff
	 */
	public class HooksAndActionsCommandExecuter extends CommandExecuter {
		
		private info.scce.pyro.rest.ObjectCache objectCache;
		private GraphModelWebSocket graphModelWebSocket;
		
		public HooksAndActionsCommandExecuter(
			PyroUserDB user,
			info.scce.pyro.rest.ObjectCache objectCache,
			GraphModelWebSocket graphModelWebSocket,
			entity.hooksandactions.HooksAndActionsDB graph,
			java.util.List<info.scce.pyro.core.command.types.HighlightCommand> highlightings
		) {
			super(
				graphModelWebSocket,
				highlightings
			);
			this.objectCache = objectCache;
			super.batch = new BatchExecution(user,new info.scce.cinco.product.ha.hooksandactions.impl.HooksAndActionsImpl(graph,this));
		}
		
		/**
		 * NOTE: Use this if it is needed to utilize (/work on) the same batch of commands
		 * of the GraphModelCommandExecuter and on the one of a primeReferenced GraphModel
		 */
		public HooksAndActionsCommandExecuter(
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
		
		public void removeHooksAndActions(info.scce.cinco.product.ha.hooksandactions.HooksAndActions entity){
			//for complex props
			entity.delete();
			/*
			if(entity.getAtype()!=null) {
				removeHookAType(entity.getAtype());
				entity.setAtype(null);
			}
			*/
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HookAContainer createHookAContainer(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.hooksandactions.rest.HookAContainer prev){
			entity.hooksandactions.HookAContainerDB node = new entity.hooksandactions.HookAContainerDB();
			node.width = width;
			node.height = height;
			node.x = x;
			node.y = y;
			
			//primitive init
			node.attribute = null;
			
			// setting container
			PanacheEntity dbMec = TypeRegistry.getApiToDB(mec);
			node.setContainer(dbMec);
			
		    node.persist();
		    
		    info.scce.cinco.product.ha.hooksandactions.HookAContainer apiNode = new info.scce.cinco.product.ha.hooksandactions.impl.HookAContainerImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
				info.scce.pyro.hooksandactions.rest.HookAContainer.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updateHookAContainer(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HookAContainer createHookAContainer(long x, long y, ModelElementContainer mec, info.scce.pyro.hooksandactions.rest.HookAContainer prev) {
			return createHookAContainer(
				x,
				y,
				36,
				36,
				mec,
				prev);
		}
		
		public void removeHookAContainer(
			info.scce.cinco.product.ha.hooksandactions.HookAContainer entity
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				null,
				info.scce.pyro.hooksandactions.rest.HookAContainer.fromEntityProperties((entity.hooksandactions.HookAContainerDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.ha.hooksandactions.HookANode createHookANode(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.hooksandactions.rest.HookANode prev){
			entity.hooksandactions.HookANodeDB node = new entity.hooksandactions.HookANodeDB();
			node.width = width;
			node.height = height;
			node.x = x;
			node.y = y;
			
			//primitive init
			node.attribute = null;
			
			// setting container
			PanacheEntity dbMec = TypeRegistry.getApiToDB(mec);
			node.setContainer(dbMec);
			
		    node.persist();
		    
		    info.scce.cinco.product.ha.hooksandactions.HookANode apiNode = new info.scce.cinco.product.ha.hooksandactions.impl.HookANodeImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
				info.scce.pyro.hooksandactions.rest.HookANode.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updateHookANode(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HookANode createHookANode(long x, long y, ModelElementContainer mec, info.scce.pyro.hooksandactions.rest.HookANode prev) {
			return createHookANode(
				x,
				y,
				36,
				36,
				mec,
				prev);
		}
		
		public void removeHookANode(
			info.scce.cinco.product.ha.hooksandactions.HookANode entity
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				null,
				info.scce.pyro.hooksandactions.rest.HookANode.fromEntityProperties((entity.hooksandactions.HookANodeDB) entity.getDelegate(),null)
			);
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HookAnEdge createHookAnEdge(Node source, Node target, java.util.List<BendingPoint> positions, info.scce.pyro.hooksandactions.rest.HookAnEdge prev){
			entity.hooksandactions.HookAnEdgeDB edge = new entity.hooksandactions.HookAnEdgeDB();
			//primitive init
			edge.attribute = null;
			
			setEdgeDBComponents(edge, source, target, positions);
			edge.persist();
		
			info.scce.cinco.product.ha.hooksandactions.HookAnEdge apiEdge = new info.scce.cinco.product.ha.hooksandactions.impl.HookAnEdgeImpl(edge,this);
			super.createEdge(
				TypeRegistry.getTypeOf(apiEdge),
				apiEdge,
				source,
				TypeRegistry.getTypeOf(source),
				target,
				TypeRegistry.getTypeOf(target),
				edge.bendingPoints,
				info.scce.pyro.hooksandactions.rest.HookAnEdge.fromEntityProperties(edge,null)
			);
			if(prev != null) {
				//create from copy
				this.updateHookAnEdge(apiEdge,prev,true);
			}
			
			// postCreateHooks
			{
				info.scce.cinco.product.flowgraph.hooks.PostCreate hook = new info.scce.cinco.product.flowgraph.hooks.PostCreate();
				hook.init(this);
				hook.postCreate(apiEdge);
			}
			
			return apiEdge;
		}
		
		public void addBendpointHookAnEdge(info.scce.cinco.product.ha.hooksandactions.HookAnEdge edge, long x,long y){
			entity.core.BendingPointDB bp = new entity.core.BendingPointDB();
			bp.x = x;
			bp.y = y;
			java.util.List<BendingPoint> bps = edge.getBendingPoints().stream()
				.map(n->BendingPoint.fromEntity((entity.core.BendingPointDB) n))
				.collect(java.util.stream.Collectors.toList());
			bps.add(BendingPoint.fromEntity(bp));
			bp.persist();
			super.updateBendingPoints(TypeRegistry.getTypeOf(edge),edge,bps);
		}
		
		public void updateHookAnEdge(info.scce.cinco.product.ha.hooksandactions.HookAnEdge edge, java.util.List<BendingPoint> points){
			super.updateBendingPoints(TypeRegistry.getTypeOf(edge),edge,points);
		}
		
		public void removeHookAnEdge(info.scce.cinco.product.ha.hooksandactions.HookAnEdge entity){
			super.removeEdge(
				TypeRegistry.getTypeOf(entity),
				entity,
				info.scce.pyro.hooksandactions.rest.HookAnEdge.fromEntityProperties((entity.hooksandactions.HookAnEdgeDB) entity.getDelegate(),null),
				TypeRegistry.getTypeOf(entity.getSourceElement()),
				TypeRegistry.getTypeOf(entity.getTargetElement())
			);
		}
		
		public void setHookAnEdgeContainer(entity.hooksandactions.HookAnEdgeDB edge, PanacheEntity container) {
			if(container instanceof entity.hooksandactions.HooksAndActionsDB) {
				entity.hooksandactions.HooksAndActionsDB containerDB = (entity.hooksandactions.HooksAndActionsDB) container;
				containerDB.addModelElements(edge);
				edge.setContainer(container);
				container.persist();
			} else if(container instanceof entity.hooksandactions.HooksAndActionsDB) {
				entity.hooksandactions.HooksAndActionsDB containerDB = (entity.hooksandactions.HooksAndActionsDB) container;
				containerDB.addModelElements(edge);
				edge.setContainer(container);
				container.persist();
			}
		}
		
		public void setHookAnEdgeDBSource(entity.hooksandactions.HookAnEdgeDB edge, Node source) {
			if(source instanceof info.scce.cinco.product.ha.hooksandactions.HookANode) {
				entity.hooksandactions.HookANodeDB o = (entity.hooksandactions.HookANodeDB) ((info.scce.cinco.product.ha.hooksandactions.HookANode) source).getDelegate();
				edge.setSource(o);
				o.addOutgoing(edge);
				o.persist();
			} else if(source instanceof info.scce.cinco.product.ha.hooksandactions.HookAContainer) {
				entity.hooksandactions.HookAContainerDB o = (entity.hooksandactions.HookAContainerDB) ((info.scce.cinco.product.ha.hooksandactions.HookAContainer) source).getDelegate();
				edge.setSource(o);
				o.addOutgoing(edge);
				o.persist();
			}
		}
		
		public void setHookAnEdgeDBTarget(entity.hooksandactions.HookAnEdgeDB edge, Node target) {
			if(target instanceof info.scce.cinco.product.ha.hooksandactions.HookANode) {
				entity.hooksandactions.HookANodeDB o = (entity.hooksandactions.HookANodeDB) ((info.scce.cinco.product.ha.hooksandactions.HookANode) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			} else if(target instanceof info.scce.cinco.product.ha.hooksandactions.HookAContainer) {
				entity.hooksandactions.HookAContainerDB o = (entity.hooksandactions.HookAContainerDB) ((info.scce.cinco.product.ha.hooksandactions.HookAContainer) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			}
		}

	    public void setEdgeDBComponents(PanacheEntity edge, Node source, Node target, java.util.List<BendingPoint> bendingPoints) {
	    	graphmodel.GraphModel graphModel = source.getRootElement();
	    	PanacheEntity e = TypeRegistry.getApiToDB(graphModel);
	    	
	    	// switch edge types
	    	if(edge instanceof entity.hooksandactions.HookAnEdgeDB) {
	    		entity.hooksandactions.HookAnEdgeDB edgeDB = (entity.hooksandactions.HookAnEdgeDB) edge;
	    		setHookAnEdgeDBSource(edgeDB, source);
	    		setHookAnEdgeDBTarget(edgeDB, target);
	    		setHookAnEdgeContainer(edgeDB, e);
	    		bendingPoints.forEach( p -> {
	    			entity.core.BendingPointDB bp = new entity.core.BendingPointDB();
	    			bp.x = p.getx();
	    			bp.y = p.gety();
	    			bp.persist();
	    			edgeDB.bendingPoints.add(bp);
	    		});
	    	}
	    }
	    
	    public void updateIdentifiableElement(IdentifiableElement entity, info.scce.pyro.core.graphmodel.IdentifiableElement prev) {
		    if(entity instanceof info.scce.cinco.product.ha.hooksandactions.HookAContainer) {
		    	updateHookAContainerProperties((info.scce.cinco.product.ha.hooksandactions.HookAContainer) entity,(info.scce.pyro.hooksandactions.rest.HookAContainer)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.ha.hooksandactions.HookAnEdge) {
		    	updateHookAnEdgeProperties((info.scce.cinco.product.ha.hooksandactions.HookAnEdge) entity,(info.scce.pyro.hooksandactions.rest.HookAnEdge)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.ha.hooksandactions.HookANode) {
		    	updateHookANodeProperties((info.scce.cinco.product.ha.hooksandactions.HookANode) entity,(info.scce.pyro.hooksandactions.rest.HookANode)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.ha.hooksandactions.HooksAndActions) {
		    	updateHooksAndActionsProperties((info.scce.cinco.product.ha.hooksandactions.HooksAndActions) entity,(info.scce.pyro.hooksandactions.rest.HooksAndActions)prev);
		    	return;
		    }
	    }

		public void updateHookAContainerProperties(info.scce.cinco.product.ha.hooksandactions.HookAContainer entity, info.scce.pyro.hooksandactions.rest.HookAContainer prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.hooksandactions.rest.HookAContainer.fromEntityProperties(
					(entity.hooksandactions.HookAContainerDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateHookAnEdgeProperties(info.scce.cinco.product.ha.hooksandactions.HookAnEdge entity, info.scce.pyro.hooksandactions.rest.HookAnEdge prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.hooksandactions.rest.HookAnEdge.fromEntityProperties(
					(entity.hooksandactions.HookAnEdgeDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateHookANodeProperties(info.scce.cinco.product.ha.hooksandactions.HookANode entity, info.scce.pyro.hooksandactions.rest.HookANode prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.hooksandactions.rest.HookANode.fromEntityProperties(
					(entity.hooksandactions.HookANodeDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateHooksAndActionsProperties(info.scce.cinco.product.ha.hooksandactions.HooksAndActions entity, info.scce.pyro.hooksandactions.rest.HooksAndActions prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.hooksandactions.rest.HooksAndActions.fromEntityProperties(
					(entity.hooksandactions.HooksAndActionsDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		//FOR NODE EDGE GRAPHMODEL TYPE
		public info.scce.cinco.product.ha.hooksandactions.AbstractHookANode updateAbstractHookANode(info.scce.pyro.hooksandactions.rest.AbstractHookANode update){
			// handle subTypes
			if(update.get__type().equals("hooksandactions.HookANode")) {
				return (info.scce.cinco.product.ha.hooksandactions.AbstractHookANode) updateHookANode((info.scce.pyro.hooksandactions.rest.HookANode) update);
			}
			
			return null;
		}
		
		public info.scce.cinco.product.ha.hooksandactions.AbstractHookANode updateAbstractHookANode(info.scce.cinco.product.ha.hooksandactions.AbstractHookANode apiEntity, info.scce.pyro.hooksandactions.rest.AbstractHookANode update){
			// handle subTypes
			if(update.get__type().equals("hooksandactions.HookANode")) {
				return (info.scce.cinco.product.ha.hooksandactions.AbstractHookANode) updateHookANode((info.scce.cinco.product.ha.hooksandactions.HookANode) apiEntity, (info.scce.pyro.hooksandactions.rest.HookANode) update, true);
			}
			
			return null;
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HookAContainer updateHookAContainer(info.scce.pyro.hooksandactions.rest.HookAContainer update){
			entity.hooksandactions.HookAContainerDB dbEntity = entity.hooksandactions.HookAContainerDB.findById(update.getId());
			info.scce.cinco.product.ha.hooksandactions.HookAContainer apiEntity = (info.scce.cinco.product.ha.hooksandactions.HookAContainer) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateHookAContainer(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HookAContainer updateHookAContainer(info.scce.cinco.product.ha.hooksandactions.HookAContainer apiEntity, info.scce.pyro.hooksandactions.rest.HookAContainer update){
			// handle type
			return updateHookAContainer(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HookAContainer updateHookAContainer(info.scce.cinco.product.ha.hooksandactions.HookAContainer apiEntity, info.scce.pyro.hooksandactions.rest.HookAContainer update, boolean propagate){
			// handle type
			entity.hooksandactions.HookAContainerDB dbEntity = (entity.hooksandactions.HookAContainerDB) apiEntity.getDelegate();
			info.scce.pyro.hooksandactions.rest.HookAContainer prev = info.scce.pyro.hooksandactions.rest.HookAContainer.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			if(
				dbEntity.attribute != null &&
				!dbEntity.attribute.equals(update.getattribute())
				||
				update.getattribute() != null &&
				!update.getattribute().equals(dbEntity.attribute)
				
			) { // value changed?
				dbEntity.attribute = update.getattribute();
				// trigger hooks
				triggerPostAttributeChange(apiEntity, "attribute", new info.scce.cinco.product.flowgraph.hooks.PostAttributeChange());
			}
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.hooksandactions.rest.HookAContainer.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HookAnEdge updateHookAnEdge(info.scce.pyro.hooksandactions.rest.HookAnEdge update){
			entity.hooksandactions.HookAnEdgeDB dbEntity = entity.hooksandactions.HookAnEdgeDB.findById(update.getId());
			info.scce.cinco.product.ha.hooksandactions.HookAnEdge apiEntity = (info.scce.cinco.product.ha.hooksandactions.HookAnEdge) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateHookAnEdge(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HookAnEdge updateHookAnEdge(info.scce.cinco.product.ha.hooksandactions.HookAnEdge apiEntity, info.scce.pyro.hooksandactions.rest.HookAnEdge update){
			// handle type
			return updateHookAnEdge(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HookAnEdge updateHookAnEdge(info.scce.cinco.product.ha.hooksandactions.HookAnEdge apiEntity, info.scce.pyro.hooksandactions.rest.HookAnEdge update, boolean propagate){
			// handle type
			entity.hooksandactions.HookAnEdgeDB dbEntity = (entity.hooksandactions.HookAnEdgeDB) apiEntity.getDelegate();
			info.scce.pyro.hooksandactions.rest.HookAnEdge prev = info.scce.pyro.hooksandactions.rest.HookAnEdge.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			if(
				dbEntity.attribute != null &&
				!dbEntity.attribute.equals(update.getattribute())
				||
				update.getattribute() != null &&
				!update.getattribute().equals(dbEntity.attribute)
				
			) { // value changed?
				dbEntity.attribute = update.getattribute();
				// trigger hooks
				triggerPostAttributeChange(apiEntity, "attribute", new info.scce.cinco.product.flowgraph.hooks.PostAttributeChange());
			}
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.hooksandactions.rest.HookAnEdge.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HookANode updateHookANode(info.scce.pyro.hooksandactions.rest.HookANode update){
			entity.hooksandactions.HookANodeDB dbEntity = entity.hooksandactions.HookANodeDB.findById(update.getId());
			info.scce.cinco.product.ha.hooksandactions.HookANode apiEntity = (info.scce.cinco.product.ha.hooksandactions.HookANode) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateHookANode(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HookANode updateHookANode(info.scce.cinco.product.ha.hooksandactions.HookANode apiEntity, info.scce.pyro.hooksandactions.rest.HookANode update){
			// handle type
			return updateHookANode(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HookANode updateHookANode(info.scce.cinco.product.ha.hooksandactions.HookANode apiEntity, info.scce.pyro.hooksandactions.rest.HookANode update, boolean propagate){
			// handle type
			entity.hooksandactions.HookANodeDB dbEntity = (entity.hooksandactions.HookANodeDB) apiEntity.getDelegate();
			info.scce.pyro.hooksandactions.rest.HookANode prev = info.scce.pyro.hooksandactions.rest.HookANode.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			if(
				dbEntity.attribute != null &&
				!dbEntity.attribute.equals(update.getattribute())
				||
				update.getattribute() != null &&
				!update.getattribute().equals(dbEntity.attribute)
				
			) { // value changed?
				dbEntity.attribute = update.getattribute();
				// trigger hooks
				triggerPostAttributeChange(apiEntity, "attribute", new info.scce.cinco.product.flowgraph.hooks.PostAttributeChange2());
				triggerPostAttributeChange(apiEntity, "attribute", new info.scce.cinco.product.flowgraph.hooks.PostAttributeChange());
			}
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.hooksandactions.rest.HookANode.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HookAType updateHookAType(info.scce.pyro.hooksandactions.rest.HookAType update){
			// handle type
			return updateHookAType(update, true);
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HookAType updateHookAType(info.scce.pyro.hooksandactions.rest.HookAType update, boolean propagate){
			// handle type
			info.scce.cinco.product.ha.hooksandactions.HookAType apiEntity = null;
			entity.hooksandactions.HookATypeDB dbEntity = entity.hooksandactions.HookATypeDB.findById(update.getId());
			if(dbEntity == null) {
				// create new entity, if not existent (only for UserDefinedTypes)
				dbEntity = new entity.hooksandactions.HookATypeDB();
				// persist to generate associated id
				dbEntity.persist();
				apiEntity = (info.scce.cinco.product.ha.hooksandactions.HookAType) TypeRegistry.getDBToApi(dbEntity, this);
				// postCreateHooks
				{
					info.scce.cinco.product.flowgraph.hooks.PostCreate hook = new info.scce.cinco.product.flowgraph.hooks.PostCreate();
					hook.init(this);
					hook.postCreate(apiEntity);
				}
			} else {
				apiEntity = (info.scce.cinco.product.ha.hooksandactions.HookAType) TypeRegistry.getDBToApi(dbEntity, this);
			}
			info.scce.pyro.hooksandactions.rest.HookAType prev = info.scce.pyro.hooksandactions.rest.HookAType.fromEntityProperties(dbEntity,null);
			
			//primitive init
			dbEntity.attribute = null;
		
			//for primitive prop
			if(
				dbEntity.attribute != null &&
				!dbEntity.attribute.equals(update.getattribute())
				||
				update.getattribute() != null &&
				!update.getattribute().equals(dbEntity.attribute)
				
			) { // value changed?
				dbEntity.attribute = update.getattribute();
				// trigger hooks
				triggerPostAttributeChange(apiEntity, "attribute", new info.scce.cinco.product.flowgraph.hooks.PostAttributeChange());
			}
			
			//for complex prop
			
			dbEntity.persist();
			
			return apiEntity;
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HooksAndActions updateHooksAndActions(info.scce.pyro.hooksandactions.rest.HooksAndActions update){
			entity.hooksandactions.HooksAndActionsDB dbEntity = entity.hooksandactions.HooksAndActionsDB.findById(update.getId());
			info.scce.cinco.product.ha.hooksandactions.HooksAndActions apiEntity = (info.scce.cinco.product.ha.hooksandactions.HooksAndActions) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateHooksAndActions(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HooksAndActions updateHooksAndActions(info.scce.cinco.product.ha.hooksandactions.HooksAndActions apiEntity, info.scce.pyro.hooksandactions.rest.HooksAndActions update){
			// handle type
			return updateHooksAndActions(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.ha.hooksandactions.HooksAndActions updateHooksAndActions(info.scce.cinco.product.ha.hooksandactions.HooksAndActions apiEntity, info.scce.pyro.hooksandactions.rest.HooksAndActions update, boolean propagate){
			// handle type
			entity.hooksandactions.HooksAndActionsDB dbEntity = (entity.hooksandactions.HooksAndActionsDB) apiEntity.getDelegate();
			info.scce.pyro.hooksandactions.rest.HooksAndActions prev = info.scce.pyro.hooksandactions.rest.HooksAndActions.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			if(
				dbEntity.attribute != null &&
				!dbEntity.attribute.equals(update.getattribute())
				||
				update.getattribute() != null &&
				!update.getattribute().equals(dbEntity.attribute)
				
			) { // value changed?
				dbEntity.attribute = update.getattribute();
				// trigger hooks
				triggerPostAttributeChange(apiEntity, "attribute", new info.scce.cinco.product.flowgraph.hooks.PostAttributeChange());
			}
			
			//for complex prop
			// type
			if(update.getatype() != null) {
				//update user defined type
				PanacheEntity newAtype = (PanacheEntity) updateHookAType(update.getatype()).getDelegate();
				
				if(!newAtype.equals(dbEntity.getAtype())) {
					// update new value
					dbEntity.setAtype(newAtype, true);
					// trigger hooks
					triggerPostAttributeChange(apiEntity, "atype", new info.scce.cinco.product.flowgraph.hooks.PostAttributeChange());
				}
			} else if(dbEntity.getAtype() != null) {
				// update new value
				dbEntity.setAtype(null, true);
				// trigger hooks
				triggerPostAttributeChange(apiEntity, "atype", new info.scce.cinco.product.flowgraph.hooks.PostAttributeChange());
			}
			
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.hooksandactions.rest.HooksAndActions.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public <T extends graphmodel.IdentifiableElement>  void triggerPostAttributeChange(T element, String name, de.jabc.cinco.meta.runtime.action.CincoPostAttributeChangeHook<T> hook) {
			//property change hook
			{
				org.eclipse.emf.ecore.EStructuralFeature esf = new org.eclipse.emf.ecore.EStructuralFeature();
				esf.setName(name);
				hook.init(this);
				if(hook.canHandleChange(element,esf)) {
					hook.handleChange(element,esf);
				}
			}
		}
		
		@Override
		public void updateAppearance() {
			super.getAllModelElements().forEach((element)->{
			});
		}

		public void removeHookAType(info.scce.cinco.product.ha.hooksandactions.HookAType apiEntity){
			PanacheEntity entity = apiEntity.getDelegate();
			removeHookAType((entity.hooksandactions.HookATypeDB) entity);
		}
		
		public void removeHookAType(entity.hooksandactions.HookATypeDB entity){
			//for enums
			//remove all complex fieds
			entity.delete();
		}
	}
