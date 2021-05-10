	package info.scce.pyro.core.command;
	
	import info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry;
	import info.scce.pyro.core.graphmodel.BendingPoint;
	import graphmodel.*;
	import entity.core.PyroUserDB;
	import info.scce.pyro.sync.GraphModelWebSocket;
	import io.quarkus.hibernate.orm.panache.PanacheEntity;
	
	/**
	 * Author zweihoff
	 */
	public class HierarchyCommandExecuter extends CommandExecuter {
		
		private info.scce.pyro.rest.ObjectCache objectCache;
		private GraphModelWebSocket graphModelWebSocket;
		
		public HierarchyCommandExecuter(
			PyroUserDB user,
			info.scce.pyro.rest.ObjectCache objectCache,
			GraphModelWebSocket graphModelWebSocket,
			entity.hierarchy.HierarchyDB graph,
			java.util.List<info.scce.pyro.core.command.types.HighlightCommand> highlightings
		) {
			super(
				graphModelWebSocket,
				highlightings
			);
			this.objectCache = objectCache;
			super.batch = new BatchExecution(user,new info.scce.cinco.product.hierarchy.hierarchy.impl.HierarchyImpl(graph,this));
		}
		
		/**
		 * NOTE: Use this if it is needed to utilize (/work on) the same batch of commands
		 * of the GraphModelCommandExecuter and on the one of a primeReferenced GraphModel
		 */
		public HierarchyCommandExecuter(
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
		
		public void removeHierarchy(info.scce.cinco.product.hierarchy.hierarchy.Hierarchy entity){
			//for complex props
			entity.delete();
			/*
			if(entity.getTa()!=null) {
				removeTA(entity.getTa());
				entity.setTa(null);
			}
			*/
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.ContA createContA(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.hierarchy.rest.ContA prev){
			entity.hierarchy.ContADB node = new entity.hierarchy.ContADB();
			node.width = width;
			node.height = height;
			node.x = x;
			node.y = y;
			
			//primitive init
			node.ofContA = null;
			node.ofContB = null;
			node.ofContC = null;
			node.ofContD = null;
			
			// setting container
			PanacheEntity dbMec = TypeRegistry.getApiToDB(mec);
			node.setContainer(dbMec);
			
		    node.persist();
		    
		    info.scce.cinco.product.hierarchy.hierarchy.ContA apiNode = new info.scce.cinco.product.hierarchy.hierarchy.impl.ContAImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
				info.scce.pyro.hierarchy.rest.ContA.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updateContA(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.ContA createContA(long x, long y, ModelElementContainer mec, info.scce.pyro.hierarchy.rest.ContA prev) {
			return createContA(
				x,
				y,
				400,
				100,
				mec,
				prev);
		}
		
		public void removeContA(
			info.scce.cinco.product.hierarchy.hierarchy.ContA entity
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				null,
				info.scce.pyro.hierarchy.rest.ContA.fromEntityProperties((entity.hierarchy.ContADB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.hierarchy.hierarchy.D createD(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.hierarchy.rest.D prev){
			entity.hierarchy.DDB node = new entity.hierarchy.DDB();
			node.width = width;
			node.height = height;
			node.x = x;
			node.y = y;
			
			//primitive init
			node.ofD = null;
			
			// setting container
			PanacheEntity dbMec = TypeRegistry.getApiToDB(mec);
			node.setContainer(dbMec);
			
		    node.persist();
		    
		    info.scce.cinco.product.hierarchy.hierarchy.D apiNode = new info.scce.cinco.product.hierarchy.hierarchy.impl.DImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
				info.scce.pyro.hierarchy.rest.D.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updateD(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.D createD(long x, long y, ModelElementContainer mec, info.scce.pyro.hierarchy.rest.D prev) {
			return createD(
				x,
				y,
				96,
				32,
				mec,
				prev);
		}
		
		public void removeD(
			info.scce.cinco.product.hierarchy.hierarchy.D entity
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				null,
				info.scce.pyro.hierarchy.rest.D.fromEntityProperties((entity.hierarchy.DDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.hierarchy.hierarchy.Cont createCont(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.hierarchy.rest.Cont prev){
			entity.hierarchy.ContDB node = new entity.hierarchy.ContDB();
			node.width = width;
			node.height = height;
			node.x = x;
			node.y = y;
			
			//primitive init
			node.ofCont = null;
			node.ofContA = null;
			node.ofContB = null;
			node.ofContC = null;
			node.ofContD = null;
			
			// setting container
			PanacheEntity dbMec = TypeRegistry.getApiToDB(mec);
			node.setContainer(dbMec);
			
		    node.persist();
		    
		    info.scce.cinco.product.hierarchy.hierarchy.Cont apiNode = new info.scce.cinco.product.hierarchy.hierarchy.impl.ContImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
				info.scce.pyro.hierarchy.rest.Cont.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updateCont(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.Cont createCont(long x, long y, ModelElementContainer mec, info.scce.pyro.hierarchy.rest.Cont prev) {
			return createCont(
				x,
				y,
				400,
				100,
				mec,
				prev);
		}
		
		public void removeCont(
			info.scce.cinco.product.hierarchy.hierarchy.Cont entity
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				null,
				info.scce.pyro.hierarchy.rest.Cont.fromEntityProperties((entity.hierarchy.ContDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.hierarchy.hierarchy.ContD createContD(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.hierarchy.rest.ContD prev){
			entity.hierarchy.ContDDB node = new entity.hierarchy.ContDDB();
			node.width = width;
			node.height = height;
			node.x = x;
			node.y = y;
			
			//primitive init
			node.ofContD = null;
			
			// setting container
			PanacheEntity dbMec = TypeRegistry.getApiToDB(mec);
			node.setContainer(dbMec);
			
		    node.persist();
		    
		    info.scce.cinco.product.hierarchy.hierarchy.ContD apiNode = new info.scce.cinco.product.hierarchy.hierarchy.impl.ContDImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
				info.scce.pyro.hierarchy.rest.ContD.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updateContD(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.ContD createContD(long x, long y, ModelElementContainer mec, info.scce.pyro.hierarchy.rest.ContD prev) {
			return createContD(
				x,
				y,
				400,
				100,
				mec,
				prev);
		}
		
		public void removeContD(
			info.scce.cinco.product.hierarchy.hierarchy.ContD entity
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				null,
				info.scce.pyro.hierarchy.rest.ContD.fromEntityProperties((entity.hierarchy.ContDDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.hierarchy.hierarchy.A createA(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.hierarchy.rest.A prev){
			entity.hierarchy.ADB node = new entity.hierarchy.ADB();
			node.width = width;
			node.height = height;
			node.x = x;
			node.y = y;
			
			//primitive init
			node.ofA = null;
			node.ofB = null;
			node.ofC = null;
			node.ofD = null;
			
			// setting container
			PanacheEntity dbMec = TypeRegistry.getApiToDB(mec);
			node.setContainer(dbMec);
			
		    node.persist();
		    
		    info.scce.cinco.product.hierarchy.hierarchy.A apiNode = new info.scce.cinco.product.hierarchy.hierarchy.impl.AImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
				info.scce.pyro.hierarchy.rest.A.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updateA(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.A createA(long x, long y, ModelElementContainer mec, info.scce.pyro.hierarchy.rest.A prev) {
			return createA(
				x,
				y,
				96,
				32,
				mec,
				prev);
		}
		
		public void removeA(
			info.scce.cinco.product.hierarchy.hierarchy.A entity
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				null,
				info.scce.pyro.hierarchy.rest.A.fromEntityProperties((entity.hierarchy.ADB) entity.getDelegate(),null)
			);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.EdgeA createEdgeA(Node source, Node target, java.util.List<BendingPoint> positions, info.scce.pyro.hierarchy.rest.EdgeA prev){
			entity.hierarchy.EdgeADB edge = new entity.hierarchy.EdgeADB();
			//primitive init
			edge.ofA = null;
			edge.ofB = null;
			edge.ofC = null;
			edge.ofD = null;
			
			setEdgeDBComponents(edge, source, target, positions);
			edge.persist();
		
			info.scce.cinco.product.hierarchy.hierarchy.EdgeA apiEdge = new info.scce.cinco.product.hierarchy.hierarchy.impl.EdgeAImpl(edge,this);
			super.createEdge(
				TypeRegistry.getTypeOf(apiEdge),
				apiEdge,
				source,
				TypeRegistry.getTypeOf(source),
				target,
				TypeRegistry.getTypeOf(target),
				edge.bendingPoints,
				info.scce.pyro.hierarchy.rest.EdgeA.fromEntityProperties(edge,null)
			);
			if(prev != null) {
				//create from copy
				this.updateEdgeA(apiEdge,prev,true);
			}
			
			
			return apiEdge;
		}
		
		public void addBendpointEdgeA(info.scce.cinco.product.hierarchy.hierarchy.EdgeA edge, long x,long y){
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
		
		public void updateEdgeA(info.scce.cinco.product.hierarchy.hierarchy.EdgeA edge, java.util.List<BendingPoint> points){
			super.updateBendingPoints(TypeRegistry.getTypeOf(edge),edge,points);
		}
		
		public void removeEdgeA(info.scce.cinco.product.hierarchy.hierarchy.EdgeA entity){
			super.removeEdge(
				TypeRegistry.getTypeOf(entity),
				entity,
				info.scce.pyro.hierarchy.rest.EdgeA.fromEntityProperties((entity.hierarchy.EdgeADB) entity.getDelegate(),null),
				TypeRegistry.getTypeOf(entity.getSourceElement()),
				TypeRegistry.getTypeOf(entity.getTargetElement())
			);
		}
		
		public void setEdgeAContainer(entity.hierarchy.EdgeADB edge, PanacheEntity container) {
			if(container instanceof entity.hierarchy.HierarchyDB) {
				entity.hierarchy.HierarchyDB containerDB = (entity.hierarchy.HierarchyDB) container;
				containerDB.addModelElements(edge);
				edge.setContainer(container);
				container.persist();
			}
		}
		
		public void setEdgeADBSource(entity.hierarchy.EdgeADB edge, Node source) {
		}
		
		public void setEdgeADBTarget(entity.hierarchy.EdgeADB edge, Node target) {
		}
		public info.scce.cinco.product.hierarchy.hierarchy.EdgeD createEdgeD(Node source, Node target, java.util.List<BendingPoint> positions, info.scce.pyro.hierarchy.rest.EdgeD prev){
			entity.hierarchy.EdgeDDB edge = new entity.hierarchy.EdgeDDB();
			//primitive init
			edge.ofD = null;
			
			setEdgeDBComponents(edge, source, target, positions);
			edge.persist();
		
			info.scce.cinco.product.hierarchy.hierarchy.EdgeD apiEdge = new info.scce.cinco.product.hierarchy.hierarchy.impl.EdgeDImpl(edge,this);
			super.createEdge(
				TypeRegistry.getTypeOf(apiEdge),
				apiEdge,
				source,
				TypeRegistry.getTypeOf(source),
				target,
				TypeRegistry.getTypeOf(target),
				edge.bendingPoints,
				info.scce.pyro.hierarchy.rest.EdgeD.fromEntityProperties(edge,null)
			);
			if(prev != null) {
				//create from copy
				this.updateEdgeD(apiEdge,prev,true);
			}
			
			
			return apiEdge;
		}
		
		public void addBendpointEdgeD(info.scce.cinco.product.hierarchy.hierarchy.EdgeD edge, long x,long y){
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
		
		public void updateEdgeD(info.scce.cinco.product.hierarchy.hierarchy.EdgeD edge, java.util.List<BendingPoint> points){
			super.updateBendingPoints(TypeRegistry.getTypeOf(edge),edge,points);
		}
		
		public void removeEdgeD(info.scce.cinco.product.hierarchy.hierarchy.EdgeD entity){
			super.removeEdge(
				TypeRegistry.getTypeOf(entity),
				entity,
				info.scce.pyro.hierarchy.rest.EdgeD.fromEntityProperties((entity.hierarchy.EdgeDDB) entity.getDelegate(),null),
				TypeRegistry.getTypeOf(entity.getSourceElement()),
				TypeRegistry.getTypeOf(entity.getTargetElement())
			);
		}
		
		public void setEdgeDContainer(entity.hierarchy.EdgeDDB edge, PanacheEntity container) {
			if(container instanceof entity.hierarchy.HierarchyDB) {
				entity.hierarchy.HierarchyDB containerDB = (entity.hierarchy.HierarchyDB) container;
				containerDB.addModelElements(edge);
				edge.setContainer(container);
				container.persist();
			}
		}
		
		public void setEdgeDDBSource(entity.hierarchy.EdgeDDB edge, Node source) {
		}
		
		public void setEdgeDDBTarget(entity.hierarchy.EdgeDDB edge, Node target) {
		}

	    public void setEdgeDBComponents(PanacheEntity edge, Node source, Node target, java.util.List<BendingPoint> bendingPoints) {
	    	graphmodel.GraphModel graphModel = source.getRootElement();
	    	PanacheEntity e = TypeRegistry.getApiToDB(graphModel);
	    	
	    	// switch edge types
	    	if(edge instanceof entity.hierarchy.EdgeADB) {
	    		entity.hierarchy.EdgeADB edgeDB = (entity.hierarchy.EdgeADB) edge;
	    		setEdgeADBSource(edgeDB, source);
	    		setEdgeADBTarget(edgeDB, target);
	    		setEdgeAContainer(edgeDB, e);
	    		bendingPoints.forEach( p -> {
	    			entity.core.BendingPointDB bp = new entity.core.BendingPointDB();
	    			bp.x = p.getx();
	    			bp.y = p.gety();
	    			bp.persist();
	    			edgeDB.bendingPoints.add(bp);
	    		});
	    	} else if(edge instanceof entity.hierarchy.EdgeDDB) {
	    		entity.hierarchy.EdgeDDB edgeDB = (entity.hierarchy.EdgeDDB) edge;
	    		setEdgeDDBSource(edgeDB, source);
	    		setEdgeDDBTarget(edgeDB, target);
	    		setEdgeDContainer(edgeDB, e);
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
		    if(entity instanceof info.scce.cinco.product.hierarchy.hierarchy.ContA) {
		    	updateContAProperties((info.scce.cinco.product.hierarchy.hierarchy.ContA) entity,(info.scce.pyro.hierarchy.rest.ContA)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeA) {
		    	updateEdgeAProperties((info.scce.cinco.product.hierarchy.hierarchy.EdgeA) entity,(info.scce.pyro.hierarchy.rest.EdgeA)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.hierarchy.hierarchy.D) {
		    	updateDProperties((info.scce.cinco.product.hierarchy.hierarchy.D) entity,(info.scce.pyro.hierarchy.rest.D)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.hierarchy.hierarchy.Cont) {
		    	updateContProperties((info.scce.cinco.product.hierarchy.hierarchy.Cont) entity,(info.scce.pyro.hierarchy.rest.Cont)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.hierarchy.hierarchy.ContD) {
		    	updateContDProperties((info.scce.cinco.product.hierarchy.hierarchy.ContD) entity,(info.scce.pyro.hierarchy.rest.ContD)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.hierarchy.hierarchy.EdgeD) {
		    	updateEdgeDProperties((info.scce.cinco.product.hierarchy.hierarchy.EdgeD) entity,(info.scce.pyro.hierarchy.rest.EdgeD)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.hierarchy.hierarchy.A) {
		    	updateAProperties((info.scce.cinco.product.hierarchy.hierarchy.A) entity,(info.scce.pyro.hierarchy.rest.A)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.hierarchy.hierarchy.Hierarchy) {
		    	updateHierarchyProperties((info.scce.cinco.product.hierarchy.hierarchy.Hierarchy) entity,(info.scce.pyro.hierarchy.rest.Hierarchy)prev);
		    	return;
		    }
	    }

		public void updateContAProperties(info.scce.cinco.product.hierarchy.hierarchy.ContA entity, info.scce.pyro.hierarchy.rest.ContA prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.hierarchy.rest.ContA.fromEntityProperties(
					(entity.hierarchy.ContADB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateEdgeAProperties(info.scce.cinco.product.hierarchy.hierarchy.EdgeA entity, info.scce.pyro.hierarchy.rest.EdgeA prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.hierarchy.rest.EdgeA.fromEntityProperties(
					(entity.hierarchy.EdgeADB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateDProperties(info.scce.cinco.product.hierarchy.hierarchy.D entity, info.scce.pyro.hierarchy.rest.D prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.hierarchy.rest.D.fromEntityProperties(
					(entity.hierarchy.DDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateContProperties(info.scce.cinco.product.hierarchy.hierarchy.Cont entity, info.scce.pyro.hierarchy.rest.Cont prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.hierarchy.rest.Cont.fromEntityProperties(
					(entity.hierarchy.ContDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateContDProperties(info.scce.cinco.product.hierarchy.hierarchy.ContD entity, info.scce.pyro.hierarchy.rest.ContD prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.hierarchy.rest.ContD.fromEntityProperties(
					(entity.hierarchy.ContDDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateEdgeDProperties(info.scce.cinco.product.hierarchy.hierarchy.EdgeD entity, info.scce.pyro.hierarchy.rest.EdgeD prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.hierarchy.rest.EdgeD.fromEntityProperties(
					(entity.hierarchy.EdgeDDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateAProperties(info.scce.cinco.product.hierarchy.hierarchy.A entity, info.scce.pyro.hierarchy.rest.A prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.hierarchy.rest.A.fromEntityProperties(
					(entity.hierarchy.ADB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateHierarchyProperties(info.scce.cinco.product.hierarchy.hierarchy.Hierarchy entity, info.scce.pyro.hierarchy.rest.Hierarchy prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.hierarchy.rest.Hierarchy.fromEntityProperties(
					(entity.hierarchy.HierarchyDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		//FOR NODE EDGE GRAPHMODEL TYPE
		public info.scce.cinco.product.hierarchy.hierarchy.ContA updateContA(info.scce.pyro.hierarchy.rest.ContA update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.Cont")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.ContA) updateCont((info.scce.pyro.hierarchy.rest.Cont) update);
			}
			
			entity.hierarchy.ContADB dbEntity = entity.hierarchy.ContADB.findById(update.getId());
			info.scce.cinco.product.hierarchy.hierarchy.ContA apiEntity = (info.scce.cinco.product.hierarchy.hierarchy.ContA) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateContA(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.ContA updateContA(info.scce.cinco.product.hierarchy.hierarchy.ContA apiEntity, info.scce.pyro.hierarchy.rest.ContA update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.Cont")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.ContA) updateCont((info.scce.cinco.product.hierarchy.hierarchy.Cont) apiEntity, (info.scce.pyro.hierarchy.rest.Cont) update, true);
			}
			
			// handle type
			return updateContA(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.ContA updateContA(info.scce.cinco.product.hierarchy.hierarchy.ContA apiEntity, info.scce.pyro.hierarchy.rest.ContA update, boolean propagate){
			// handle subTypes
			if(update.get__type().equals("hierarchy.Cont")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.ContA) updateCont((info.scce.cinco.product.hierarchy.hierarchy.Cont) apiEntity, (info.scce.pyro.hierarchy.rest.Cont) update, propagate);
			}
			
			// handle type
			entity.hierarchy.ContADB dbEntity = (entity.hierarchy.ContADB) apiEntity.getDelegate();
			info.scce.pyro.hierarchy.rest.ContA prev = info.scce.pyro.hierarchy.rest.ContA.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			if(
				dbEntity.ofContA != null &&
				!dbEntity.ofContA.equals(update.getofContA())
				||
				update.getofContA() != null &&
				!update.getofContA().equals(dbEntity.ofContA)
				
			) { // value changed?
				dbEntity.ofContA = update.getofContA();
			}
			if(
				dbEntity.ofContB != null &&
				!dbEntity.ofContB.equals(update.getofContB())
				||
				update.getofContB() != null &&
				!update.getofContB().equals(dbEntity.ofContB)
				
			) { // value changed?
				dbEntity.ofContB = update.getofContB();
			}
			if(
				dbEntity.ofContC != null &&
				!dbEntity.ofContC.equals(update.getofContC())
				||
				update.getofContC() != null &&
				!update.getofContC().equals(dbEntity.ofContC)
				
			) { // value changed?
				dbEntity.ofContC = update.getofContC();
			}
			if(
				dbEntity.ofContD != null &&
				!dbEntity.ofContD.equals(update.getofContD())
				||
				update.getofContD() != null &&
				!update.getofContD().equals(dbEntity.ofContD)
				
			) { // value changed?
				dbEntity.ofContD = update.getofContD();
			}
			
			//for complex prop
			// type
			if(update.getta() != null) {
				//update user defined type
				PanacheEntity newTa = (PanacheEntity) updateTA(update.getta()).getDelegate();
				
				if(!newTa.equals(dbEntity.getTa())) {
					// update new value
					dbEntity.setTa(newTa, true);
				}
			} else if(dbEntity.getTa() != null) {
				// update new value
				dbEntity.setTa(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TA> newList = update.gettaList().stream()
					.map(this::updateTA)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTaList().equals(newList)) {
					apiEntity.setTaList(newList);
				}
			}
			
			// type
			if(update.gettb() != null) {
				//update user defined type
				PanacheEntity newTb = (PanacheEntity) updateTB(update.gettb()).getDelegate();
				
				if(!newTb.equals(dbEntity.getTb())) {
					// update new value
					dbEntity.setTb(newTb, true);
				}
			} else if(dbEntity.getTb() != null) {
				// update new value
				dbEntity.setTb(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TB> newList = update.gettbList().stream()
					.map(this::updateTB)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTbList().equals(newList)) {
					apiEntity.setTbList(newList);
				}
			}
			
			// type
			if(update.gettc() != null) {
				//update user defined type
				PanacheEntity newTc = (PanacheEntity) updateTC(update.gettc()).getDelegate();
				
				if(!newTc.equals(dbEntity.getTc())) {
					// update new value
					dbEntity.setTc(newTc, true);
				}
			} else if(dbEntity.getTc() != null) {
				// update new value
				dbEntity.setTc(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TC> newList = update.gettcList().stream()
					.map(this::updateTC)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTcList().equals(newList)) {
					apiEntity.setTcList(newList);
				}
			}
			
			// type
			if(update.gettd() != null) {
				//update user defined type
				PanacheEntity newTd = (PanacheEntity) updateTD(update.gettd()).getDelegate();
				
				if(!newTd.equals(dbEntity.getTd())) {
					// update new value
					dbEntity.setTd(newTd, true);
				}
			} else if(dbEntity.getTd() != null) {
				// update new value
				dbEntity.setTd(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TD> newList = update.gettdList().stream()
					.map(this::updateTD)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTdList().equals(newList)) {
					apiEntity.setTdList(newList);
				}
			}
			
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.hierarchy.rest.ContA.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.ContC updateContC(info.scce.pyro.hierarchy.rest.ContC update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.Cont")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.ContC) updateCont((info.scce.pyro.hierarchy.rest.Cont) update);
			} else if(update.get__type().equals("hierarchy.ContA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.ContC) updateContA((info.scce.pyro.hierarchy.rest.ContA) update);
			}
			
			return null;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.ContC updateContC(info.scce.cinco.product.hierarchy.hierarchy.ContC apiEntity, info.scce.pyro.hierarchy.rest.ContC update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.Cont")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.ContC) updateCont((info.scce.cinco.product.hierarchy.hierarchy.Cont) apiEntity, (info.scce.pyro.hierarchy.rest.Cont) update, true);
			} else if(update.get__type().equals("hierarchy.ContA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.ContC) updateContA((info.scce.cinco.product.hierarchy.hierarchy.ContA) apiEntity, (info.scce.pyro.hierarchy.rest.ContA) update, true);
			}
			
			return null;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.EdgeA updateEdgeA(info.scce.pyro.hierarchy.rest.EdgeA update){
			entity.hierarchy.EdgeADB dbEntity = entity.hierarchy.EdgeADB.findById(update.getId());
			info.scce.cinco.product.hierarchy.hierarchy.EdgeA apiEntity = (info.scce.cinco.product.hierarchy.hierarchy.EdgeA) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateEdgeA(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.EdgeA updateEdgeA(info.scce.cinco.product.hierarchy.hierarchy.EdgeA apiEntity, info.scce.pyro.hierarchy.rest.EdgeA update){
			// handle type
			return updateEdgeA(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.EdgeA updateEdgeA(info.scce.cinco.product.hierarchy.hierarchy.EdgeA apiEntity, info.scce.pyro.hierarchy.rest.EdgeA update, boolean propagate){
			// handle type
			entity.hierarchy.EdgeADB dbEntity = (entity.hierarchy.EdgeADB) apiEntity.getDelegate();
			info.scce.pyro.hierarchy.rest.EdgeA prev = info.scce.pyro.hierarchy.rest.EdgeA.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			if(
				dbEntity.ofA != null &&
				!dbEntity.ofA.equals(update.getofA())
				||
				update.getofA() != null &&
				!update.getofA().equals(dbEntity.ofA)
				
			) { // value changed?
				dbEntity.ofA = update.getofA();
			}
			if(
				dbEntity.ofB != null &&
				!dbEntity.ofB.equals(update.getofB())
				||
				update.getofB() != null &&
				!update.getofB().equals(dbEntity.ofB)
				
			) { // value changed?
				dbEntity.ofB = update.getofB();
			}
			if(
				dbEntity.ofC != null &&
				!dbEntity.ofC.equals(update.getofC())
				||
				update.getofC() != null &&
				!update.getofC().equals(dbEntity.ofC)
				
			) { // value changed?
				dbEntity.ofC = update.getofC();
			}
			if(
				dbEntity.ofD != null &&
				!dbEntity.ofD.equals(update.getofD())
				||
				update.getofD() != null &&
				!update.getofD().equals(dbEntity.ofD)
				
			) { // value changed?
				dbEntity.ofD = update.getofD();
			}
			
			//for complex prop
			// type
			if(update.getta() != null) {
				//update user defined type
				PanacheEntity newTa = (PanacheEntity) updateTA(update.getta()).getDelegate();
				
				if(!newTa.equals(dbEntity.getTa())) {
					// update new value
					dbEntity.setTa(newTa, true);
				}
			} else if(dbEntity.getTa() != null) {
				// update new value
				dbEntity.setTa(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TA> newList = update.gettaList().stream()
					.map(this::updateTA)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTaList().equals(newList)) {
					apiEntity.setTaList(newList);
				}
			}
			
			// type
			if(update.gettb() != null) {
				//update user defined type
				PanacheEntity newTb = (PanacheEntity) updateTB(update.gettb()).getDelegate();
				
				if(!newTb.equals(dbEntity.getTb())) {
					// update new value
					dbEntity.setTb(newTb, true);
				}
			} else if(dbEntity.getTb() != null) {
				// update new value
				dbEntity.setTb(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TB> newList = update.gettbList().stream()
					.map(this::updateTB)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTbList().equals(newList)) {
					apiEntity.setTbList(newList);
				}
			}
			
			// type
			if(update.gettc() != null) {
				//update user defined type
				PanacheEntity newTc = (PanacheEntity) updateTC(update.gettc()).getDelegate();
				
				if(!newTc.equals(dbEntity.getTc())) {
					// update new value
					dbEntity.setTc(newTc, true);
				}
			} else if(dbEntity.getTc() != null) {
				// update new value
				dbEntity.setTc(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TC> newList = update.gettcList().stream()
					.map(this::updateTC)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTcList().equals(newList)) {
					apiEntity.setTcList(newList);
				}
			}
			
			// type
			if(update.gettd() != null) {
				//update user defined type
				PanacheEntity newTd = (PanacheEntity) updateTD(update.gettd()).getDelegate();
				
				if(!newTd.equals(dbEntity.getTd())) {
					// update new value
					dbEntity.setTd(newTd, true);
				}
			} else if(dbEntity.getTd() != null) {
				// update new value
				dbEntity.setTd(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TD> newList = update.gettdList().stream()
					.map(this::updateTD)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTdList().equals(newList)) {
					apiEntity.setTdList(newList);
				}
			}
			
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.hierarchy.rest.EdgeA.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.EdgeC updateEdgeC(info.scce.pyro.hierarchy.rest.EdgeC update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.EdgeA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.EdgeC) updateEdgeA((info.scce.pyro.hierarchy.rest.EdgeA) update);
			}
			
			return null;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.EdgeC updateEdgeC(info.scce.cinco.product.hierarchy.hierarchy.EdgeC apiEntity, info.scce.pyro.hierarchy.rest.EdgeC update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.EdgeA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.EdgeC) updateEdgeA((info.scce.cinco.product.hierarchy.hierarchy.EdgeA) apiEntity, (info.scce.pyro.hierarchy.rest.EdgeA) update, true);
			}
			
			return null;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.B updateB(info.scce.pyro.hierarchy.rest.B update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.A")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.B) updateA((info.scce.pyro.hierarchy.rest.A) update);
			}
			
			return null;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.B updateB(info.scce.cinco.product.hierarchy.hierarchy.B apiEntity, info.scce.pyro.hierarchy.rest.B update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.A")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.B) updateA((info.scce.cinco.product.hierarchy.hierarchy.A) apiEntity, (info.scce.pyro.hierarchy.rest.A) update, true);
			}
			
			return null;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.D updateD(info.scce.pyro.hierarchy.rest.D update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.A")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.D) updateA((info.scce.pyro.hierarchy.rest.A) update);
			}
			
			entity.hierarchy.DDB dbEntity = entity.hierarchy.DDB.findById(update.getId());
			info.scce.cinco.product.hierarchy.hierarchy.D apiEntity = (info.scce.cinco.product.hierarchy.hierarchy.D) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateD(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.D updateD(info.scce.cinco.product.hierarchy.hierarchy.D apiEntity, info.scce.pyro.hierarchy.rest.D update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.A")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.D) updateA((info.scce.cinco.product.hierarchy.hierarchy.A) apiEntity, (info.scce.pyro.hierarchy.rest.A) update, true);
			}
			
			// handle type
			return updateD(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.D updateD(info.scce.cinco.product.hierarchy.hierarchy.D apiEntity, info.scce.pyro.hierarchy.rest.D update, boolean propagate){
			// handle subTypes
			if(update.get__type().equals("hierarchy.A")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.D) updateA((info.scce.cinco.product.hierarchy.hierarchy.A) apiEntity, (info.scce.pyro.hierarchy.rest.A) update, propagate);
			}
			
			// handle type
			entity.hierarchy.DDB dbEntity = (entity.hierarchy.DDB) apiEntity.getDelegate();
			info.scce.pyro.hierarchy.rest.D prev = info.scce.pyro.hierarchy.rest.D.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			if(
				dbEntity.ofD != null &&
				!dbEntity.ofD.equals(update.getofD())
				||
				update.getofD() != null &&
				!update.getofD().equals(dbEntity.ofD)
				
			) { // value changed?
				dbEntity.ofD = update.getofD();
			}
			
			//for complex prop
			// type
			if(update.gettd() != null) {
				//update user defined type
				PanacheEntity newTd = (PanacheEntity) updateTD(update.gettd()).getDelegate();
				
				if(!newTd.equals(dbEntity.getTd())) {
					// update new value
					dbEntity.setTd(newTd, true);
				}
			} else if(dbEntity.getTd() != null) {
				// update new value
				dbEntity.setTd(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TD> newList = update.gettdList().stream()
					.map(this::updateTD)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTdList().equals(newList)) {
					apiEntity.setTdList(newList);
				}
			}
			
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.hierarchy.rest.D.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.Cont updateCont(info.scce.pyro.hierarchy.rest.Cont update){
			entity.hierarchy.ContDB dbEntity = entity.hierarchy.ContDB.findById(update.getId());
			info.scce.cinco.product.hierarchy.hierarchy.Cont apiEntity = (info.scce.cinco.product.hierarchy.hierarchy.Cont) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateCont(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.Cont updateCont(info.scce.cinco.product.hierarchy.hierarchy.Cont apiEntity, info.scce.pyro.hierarchy.rest.Cont update){
			// handle type
			return updateCont(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.Cont updateCont(info.scce.cinco.product.hierarchy.hierarchy.Cont apiEntity, info.scce.pyro.hierarchy.rest.Cont update, boolean propagate){
			// handle type
			entity.hierarchy.ContDB dbEntity = (entity.hierarchy.ContDB) apiEntity.getDelegate();
			info.scce.pyro.hierarchy.rest.Cont prev = info.scce.pyro.hierarchy.rest.Cont.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			if(
				dbEntity.ofCont != null &&
				!dbEntity.ofCont.equals(update.getofCont())
				||
				update.getofCont() != null &&
				!update.getofCont().equals(dbEntity.ofCont)
				
			) { // value changed?
				dbEntity.ofCont = update.getofCont();
			}
			if(
				dbEntity.ofContA != null &&
				!dbEntity.ofContA.equals(update.getofContA())
				||
				update.getofContA() != null &&
				!update.getofContA().equals(dbEntity.ofContA)
				
			) { // value changed?
				dbEntity.ofContA = update.getofContA();
			}
			if(
				dbEntity.ofContB != null &&
				!dbEntity.ofContB.equals(update.getofContB())
				||
				update.getofContB() != null &&
				!update.getofContB().equals(dbEntity.ofContB)
				
			) { // value changed?
				dbEntity.ofContB = update.getofContB();
			}
			if(
				dbEntity.ofContC != null &&
				!dbEntity.ofContC.equals(update.getofContC())
				||
				update.getofContC() != null &&
				!update.getofContC().equals(dbEntity.ofContC)
				
			) { // value changed?
				dbEntity.ofContC = update.getofContC();
			}
			if(
				dbEntity.ofContD != null &&
				!dbEntity.ofContD.equals(update.getofContD())
				||
				update.getofContD() != null &&
				!update.getofContD().equals(dbEntity.ofContD)
				
			) { // value changed?
				dbEntity.ofContD = update.getofContD();
			}
			
			//for complex prop
			// type
			if(update.getta() != null) {
				//update user defined type
				PanacheEntity newTa = (PanacheEntity) updateTA(update.getta()).getDelegate();
				
				if(!newTa.equals(dbEntity.getTa())) {
					// update new value
					dbEntity.setTa(newTa, true);
				}
			} else if(dbEntity.getTa() != null) {
				// update new value
				dbEntity.setTa(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TA> newList = update.gettaList().stream()
					.map(this::updateTA)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTaList().equals(newList)) {
					apiEntity.setTaList(newList);
				}
			}
			
			// type
			if(update.gettb() != null) {
				//update user defined type
				PanacheEntity newTb = (PanacheEntity) updateTB(update.gettb()).getDelegate();
				
				if(!newTb.equals(dbEntity.getTb())) {
					// update new value
					dbEntity.setTb(newTb, true);
				}
			} else if(dbEntity.getTb() != null) {
				// update new value
				dbEntity.setTb(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TB> newList = update.gettbList().stream()
					.map(this::updateTB)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTbList().equals(newList)) {
					apiEntity.setTbList(newList);
				}
			}
			
			// type
			if(update.gettc() != null) {
				//update user defined type
				PanacheEntity newTc = (PanacheEntity) updateTC(update.gettc()).getDelegate();
				
				if(!newTc.equals(dbEntity.getTc())) {
					// update new value
					dbEntity.setTc(newTc, true);
				}
			} else if(dbEntity.getTc() != null) {
				// update new value
				dbEntity.setTc(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TC> newList = update.gettcList().stream()
					.map(this::updateTC)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTcList().equals(newList)) {
					apiEntity.setTcList(newList);
				}
			}
			
			// type
			if(update.gettd() != null) {
				//update user defined type
				PanacheEntity newTd = (PanacheEntity) updateTD(update.gettd()).getDelegate();
				
				if(!newTd.equals(dbEntity.getTd())) {
					// update new value
					dbEntity.setTd(newTd, true);
				}
			} else if(dbEntity.getTd() != null) {
				// update new value
				dbEntity.setTd(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TD> newList = update.gettdList().stream()
					.map(this::updateTD)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTdList().equals(newList)) {
					apiEntity.setTdList(newList);
				}
			}
			
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.hierarchy.rest.Cont.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.ContB updateContB(info.scce.pyro.hierarchy.rest.ContB update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.Cont")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.ContB) updateCont((info.scce.pyro.hierarchy.rest.Cont) update);
			} else if(update.get__type().equals("hierarchy.ContA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.ContB) updateContA((info.scce.pyro.hierarchy.rest.ContA) update);
			}
			
			return null;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.ContB updateContB(info.scce.cinco.product.hierarchy.hierarchy.ContB apiEntity, info.scce.pyro.hierarchy.rest.ContB update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.Cont")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.ContB) updateCont((info.scce.cinco.product.hierarchy.hierarchy.Cont) apiEntity, (info.scce.pyro.hierarchy.rest.Cont) update, true);
			} else if(update.get__type().equals("hierarchy.ContA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.ContB) updateContA((info.scce.cinco.product.hierarchy.hierarchy.ContA) apiEntity, (info.scce.pyro.hierarchy.rest.ContA) update, true);
			}
			
			return null;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.ContD updateContD(info.scce.pyro.hierarchy.rest.ContD update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.Cont")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.ContD) updateCont((info.scce.pyro.hierarchy.rest.Cont) update);
			} else if(update.get__type().equals("hierarchy.ContA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.ContD) updateContA((info.scce.pyro.hierarchy.rest.ContA) update);
			}
			
			entity.hierarchy.ContDDB dbEntity = entity.hierarchy.ContDDB.findById(update.getId());
			info.scce.cinco.product.hierarchy.hierarchy.ContD apiEntity = (info.scce.cinco.product.hierarchy.hierarchy.ContD) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateContD(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.ContD updateContD(info.scce.cinco.product.hierarchy.hierarchy.ContD apiEntity, info.scce.pyro.hierarchy.rest.ContD update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.Cont")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.ContD) updateCont((info.scce.cinco.product.hierarchy.hierarchy.Cont) apiEntity, (info.scce.pyro.hierarchy.rest.Cont) update, true);
			} else if(update.get__type().equals("hierarchy.ContA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.ContD) updateContA((info.scce.cinco.product.hierarchy.hierarchy.ContA) apiEntity, (info.scce.pyro.hierarchy.rest.ContA) update, true);
			}
			
			// handle type
			return updateContD(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.ContD updateContD(info.scce.cinco.product.hierarchy.hierarchy.ContD apiEntity, info.scce.pyro.hierarchy.rest.ContD update, boolean propagate){
			// handle subTypes
			if(update.get__type().equals("hierarchy.Cont")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.ContD) updateCont((info.scce.cinco.product.hierarchy.hierarchy.Cont) apiEntity, (info.scce.pyro.hierarchy.rest.Cont) update, propagate);
			} else if(update.get__type().equals("hierarchy.ContA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.ContD) updateContA((info.scce.cinco.product.hierarchy.hierarchy.ContA) apiEntity, (info.scce.pyro.hierarchy.rest.ContA) update, propagate);
			}
			
			// handle type
			entity.hierarchy.ContDDB dbEntity = (entity.hierarchy.ContDDB) apiEntity.getDelegate();
			info.scce.pyro.hierarchy.rest.ContD prev = info.scce.pyro.hierarchy.rest.ContD.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			if(
				dbEntity.ofContD != null &&
				!dbEntity.ofContD.equals(update.getofContD())
				||
				update.getofContD() != null &&
				!update.getofContD().equals(dbEntity.ofContD)
				
			) { // value changed?
				dbEntity.ofContD = update.getofContD();
			}
			
			//for complex prop
			// type
			if(update.gettd() != null) {
				//update user defined type
				PanacheEntity newTd = (PanacheEntity) updateTD(update.gettd()).getDelegate();
				
				if(!newTd.equals(dbEntity.getTd())) {
					// update new value
					dbEntity.setTd(newTd, true);
				}
			} else if(dbEntity.getTd() != null) {
				// update new value
				dbEntity.setTd(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TD> newList = update.gettdList().stream()
					.map(this::updateTD)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTdList().equals(newList)) {
					apiEntity.setTdList(newList);
				}
			}
			
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.hierarchy.rest.ContD.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.EdgeB updateEdgeB(info.scce.pyro.hierarchy.rest.EdgeB update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.EdgeA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.EdgeB) updateEdgeA((info.scce.pyro.hierarchy.rest.EdgeA) update);
			}
			
			return null;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.EdgeB updateEdgeB(info.scce.cinco.product.hierarchy.hierarchy.EdgeB apiEntity, info.scce.pyro.hierarchy.rest.EdgeB update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.EdgeA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.EdgeB) updateEdgeA((info.scce.cinco.product.hierarchy.hierarchy.EdgeA) apiEntity, (info.scce.pyro.hierarchy.rest.EdgeA) update, true);
			}
			
			return null;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.EdgeD updateEdgeD(info.scce.pyro.hierarchy.rest.EdgeD update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.EdgeA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.EdgeD) updateEdgeA((info.scce.pyro.hierarchy.rest.EdgeA) update);
			}
			
			entity.hierarchy.EdgeDDB dbEntity = entity.hierarchy.EdgeDDB.findById(update.getId());
			info.scce.cinco.product.hierarchy.hierarchy.EdgeD apiEntity = (info.scce.cinco.product.hierarchy.hierarchy.EdgeD) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateEdgeD(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.EdgeD updateEdgeD(info.scce.cinco.product.hierarchy.hierarchy.EdgeD apiEntity, info.scce.pyro.hierarchy.rest.EdgeD update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.EdgeA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.EdgeD) updateEdgeA((info.scce.cinco.product.hierarchy.hierarchy.EdgeA) apiEntity, (info.scce.pyro.hierarchy.rest.EdgeA) update, true);
			}
			
			// handle type
			return updateEdgeD(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.EdgeD updateEdgeD(info.scce.cinco.product.hierarchy.hierarchy.EdgeD apiEntity, info.scce.pyro.hierarchy.rest.EdgeD update, boolean propagate){
			// handle subTypes
			if(update.get__type().equals("hierarchy.EdgeA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.EdgeD) updateEdgeA((info.scce.cinco.product.hierarchy.hierarchy.EdgeA) apiEntity, (info.scce.pyro.hierarchy.rest.EdgeA) update, propagate);
			}
			
			// handle type
			entity.hierarchy.EdgeDDB dbEntity = (entity.hierarchy.EdgeDDB) apiEntity.getDelegate();
			info.scce.pyro.hierarchy.rest.EdgeD prev = info.scce.pyro.hierarchy.rest.EdgeD.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			if(
				dbEntity.ofD != null &&
				!dbEntity.ofD.equals(update.getofD())
				||
				update.getofD() != null &&
				!update.getofD().equals(dbEntity.ofD)
				
			) { // value changed?
				dbEntity.ofD = update.getofD();
			}
			
			//for complex prop
			// type
			if(update.gettd() != null) {
				//update user defined type
				PanacheEntity newTd = (PanacheEntity) updateTD(update.gettd()).getDelegate();
				
				if(!newTd.equals(dbEntity.getTd())) {
					// update new value
					dbEntity.setTd(newTd, true);
				}
			} else if(dbEntity.getTd() != null) {
				// update new value
				dbEntity.setTd(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TD> newList = update.gettdList().stream()
					.map(this::updateTD)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTdList().equals(newList)) {
					apiEntity.setTdList(newList);
				}
			}
			
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.hierarchy.rest.EdgeD.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.A updateA(info.scce.pyro.hierarchy.rest.A update){
			entity.hierarchy.ADB dbEntity = entity.hierarchy.ADB.findById(update.getId());
			info.scce.cinco.product.hierarchy.hierarchy.A apiEntity = (info.scce.cinco.product.hierarchy.hierarchy.A) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateA(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.A updateA(info.scce.cinco.product.hierarchy.hierarchy.A apiEntity, info.scce.pyro.hierarchy.rest.A update){
			// handle type
			return updateA(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.A updateA(info.scce.cinco.product.hierarchy.hierarchy.A apiEntity, info.scce.pyro.hierarchy.rest.A update, boolean propagate){
			// handle type
			entity.hierarchy.ADB dbEntity = (entity.hierarchy.ADB) apiEntity.getDelegate();
			info.scce.pyro.hierarchy.rest.A prev = info.scce.pyro.hierarchy.rest.A.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			if(
				dbEntity.ofA != null &&
				!dbEntity.ofA.equals(update.getofA())
				||
				update.getofA() != null &&
				!update.getofA().equals(dbEntity.ofA)
				
			) { // value changed?
				dbEntity.ofA = update.getofA();
			}
			if(
				dbEntity.ofB != null &&
				!dbEntity.ofB.equals(update.getofB())
				||
				update.getofB() != null &&
				!update.getofB().equals(dbEntity.ofB)
				
			) { // value changed?
				dbEntity.ofB = update.getofB();
			}
			if(
				dbEntity.ofC != null &&
				!dbEntity.ofC.equals(update.getofC())
				||
				update.getofC() != null &&
				!update.getofC().equals(dbEntity.ofC)
				
			) { // value changed?
				dbEntity.ofC = update.getofC();
			}
			if(
				dbEntity.ofD != null &&
				!dbEntity.ofD.equals(update.getofD())
				||
				update.getofD() != null &&
				!update.getofD().equals(dbEntity.ofD)
				
			) { // value changed?
				dbEntity.ofD = update.getofD();
			}
			
			//for complex prop
			// type
			if(update.getta() != null) {
				//update user defined type
				PanacheEntity newTa = (PanacheEntity) updateTA(update.getta()).getDelegate();
				
				if(!newTa.equals(dbEntity.getTa())) {
					// update new value
					dbEntity.setTa(newTa, true);
				}
			} else if(dbEntity.getTa() != null) {
				// update new value
				dbEntity.setTa(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TA> newList = update.gettaList().stream()
					.map(this::updateTA)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTaList().equals(newList)) {
					apiEntity.setTaList(newList);
				}
			}
			
			// type
			if(update.gettb() != null) {
				//update user defined type
				PanacheEntity newTb = (PanacheEntity) updateTB(update.gettb()).getDelegate();
				
				if(!newTb.equals(dbEntity.getTb())) {
					// update new value
					dbEntity.setTb(newTb, true);
				}
			} else if(dbEntity.getTb() != null) {
				// update new value
				dbEntity.setTb(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TB> newList = update.gettbList().stream()
					.map(this::updateTB)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTbList().equals(newList)) {
					apiEntity.setTbList(newList);
				}
			}
			
			// type
			if(update.gettc() != null) {
				//update user defined type
				PanacheEntity newTc = (PanacheEntity) updateTC(update.gettc()).getDelegate();
				
				if(!newTc.equals(dbEntity.getTc())) {
					// update new value
					dbEntity.setTc(newTc, true);
				}
			} else if(dbEntity.getTc() != null) {
				// update new value
				dbEntity.setTc(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TC> newList = update.gettcList().stream()
					.map(this::updateTC)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTcList().equals(newList)) {
					apiEntity.setTcList(newList);
				}
			}
			
			// type
			if(update.gettd() != null) {
				//update user defined type
				PanacheEntity newTd = (PanacheEntity) updateTD(update.gettd()).getDelegate();
				
				if(!newTd.equals(dbEntity.getTd())) {
					// update new value
					dbEntity.setTd(newTd, true);
				}
			} else if(dbEntity.getTd() != null) {
				// update new value
				dbEntity.setTd(null, true);
			}
			
			{
				// list
				java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TD> newList = update.gettdList().stream()
					.map(this::updateTD)
					.collect(java.util.stream.Collectors.toList());
				
				// check if list has changed
				if(!apiEntity.getTdList().equals(newList)) {
					apiEntity.setTdList(newList);
				}
			}
			
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.hierarchy.rest.A.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.C updateC(info.scce.pyro.hierarchy.rest.C update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.A")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.C) updateA((info.scce.pyro.hierarchy.rest.A) update);
			}
			
			return null;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.C updateC(info.scce.cinco.product.hierarchy.hierarchy.C apiEntity, info.scce.pyro.hierarchy.rest.C update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.A")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.C) updateA((info.scce.cinco.product.hierarchy.hierarchy.A) apiEntity, (info.scce.pyro.hierarchy.rest.A) update, true);
			}
			
			return null;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.TA updateTA(info.scce.pyro.hierarchy.rest.TA update){
			// handle type
			return updateTA(update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.TA updateTA(info.scce.pyro.hierarchy.rest.TA update, boolean propagate){
			// handle type
			info.scce.cinco.product.hierarchy.hierarchy.TA apiEntity = null;
			entity.hierarchy.TADB dbEntity = entity.hierarchy.TADB.findById(update.getId());
			if(dbEntity == null) {
				// create new entity, if not existent (only for UserDefinedTypes)
				dbEntity = new entity.hierarchy.TADB();
				// persist to generate associated id
				dbEntity.persist();
				apiEntity = (info.scce.cinco.product.hierarchy.hierarchy.TA) TypeRegistry.getDBToApi(dbEntity, this);
			} else {
				apiEntity = (info.scce.cinco.product.hierarchy.hierarchy.TA) TypeRegistry.getDBToApi(dbEntity, this);
			}
			info.scce.pyro.hierarchy.rest.TA prev = info.scce.pyro.hierarchy.rest.TA.fromEntityProperties(dbEntity,null);
			
			//primitive init
			dbEntity.ofTA = null;
			dbEntity.ofTB = null;
			dbEntity.ofTC = null;
			dbEntity.ofTD = null;
		
			//for primitive prop
			if(
				dbEntity.ofTA != null &&
				!dbEntity.ofTA.equals(update.getofTA())
				||
				update.getofTA() != null &&
				!update.getofTA().equals(dbEntity.ofTA)
				
			) { // value changed?
				dbEntity.ofTA = update.getofTA();
			}
			if(
				dbEntity.ofTB != null &&
				!dbEntity.ofTB.equals(update.getofTB())
				||
				update.getofTB() != null &&
				!update.getofTB().equals(dbEntity.ofTB)
				
			) { // value changed?
				dbEntity.ofTB = update.getofTB();
			}
			if(
				dbEntity.ofTC != null &&
				!dbEntity.ofTC.equals(update.getofTC())
				||
				update.getofTC() != null &&
				!update.getofTC().equals(dbEntity.ofTC)
				
			) { // value changed?
				dbEntity.ofTC = update.getofTC();
			}
			if(
				dbEntity.ofTD != null &&
				!dbEntity.ofTD.equals(update.getofTD())
				||
				update.getofTD() != null &&
				!update.getofTD().equals(dbEntity.ofTD)
				
			) { // value changed?
				dbEntity.ofTD = update.getofTD();
			}
			
			//for complex prop
			
			dbEntity.persist();
			
			return apiEntity;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.TB updateTB(info.scce.pyro.hierarchy.rest.TB update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.TA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.TB) updateTA((info.scce.pyro.hierarchy.rest.TA) update, true);
			}
			
			return null;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.TC updateTC(info.scce.pyro.hierarchy.rest.TC update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.TA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.TC) updateTA((info.scce.pyro.hierarchy.rest.TA) update, true);
			}
			
			return null;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.TD updateTD(info.scce.pyro.hierarchy.rest.TD update){
			// handle subTypes
			if(update.get__type().equals("hierarchy.TA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.TD) updateTA((info.scce.pyro.hierarchy.rest.TA) update, true);
			}
			
			// handle type
			return updateTD(update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.TD updateTD(info.scce.pyro.hierarchy.rest.TD update, boolean propagate){
			// handle subTypes
			if(update.get__type().equals("hierarchy.TA")) {
				return (info.scce.cinco.product.hierarchy.hierarchy.TD) updateTA((info.scce.pyro.hierarchy.rest.TA) update, propagate);
			}
			
			// handle type
			info.scce.cinco.product.hierarchy.hierarchy.TD apiEntity = null;
			entity.hierarchy.TDDB dbEntity = entity.hierarchy.TDDB.findById(update.getId());
			if(dbEntity == null) {
				// create new entity, if not existent (only for UserDefinedTypes)
				dbEntity = new entity.hierarchy.TDDB();
				// persist to generate associated id
				dbEntity.persist();
				apiEntity = (info.scce.cinco.product.hierarchy.hierarchy.TD) TypeRegistry.getDBToApi(dbEntity, this);
			} else {
				apiEntity = (info.scce.cinco.product.hierarchy.hierarchy.TD) TypeRegistry.getDBToApi(dbEntity, this);
			}
			info.scce.pyro.hierarchy.rest.TD prev = info.scce.pyro.hierarchy.rest.TD.fromEntityProperties(dbEntity,null);
			
			//primitive init
			dbEntity.ofTD = null;
		
			//for primitive prop
			if(
				dbEntity.ofTD != null &&
				!dbEntity.ofTD.equals(update.getofTD())
				||
				update.getofTD() != null &&
				!update.getofTD().equals(dbEntity.ofTD)
				
			) { // value changed?
				dbEntity.ofTD = update.getofTD();
			}
			
			//for complex prop
			
			dbEntity.persist();
			
			return apiEntity;
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.Hierarchy updateHierarchy(info.scce.pyro.hierarchy.rest.Hierarchy update){
			entity.hierarchy.HierarchyDB dbEntity = entity.hierarchy.HierarchyDB.findById(update.getId());
			info.scce.cinco.product.hierarchy.hierarchy.Hierarchy apiEntity = (info.scce.cinco.product.hierarchy.hierarchy.Hierarchy) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateHierarchy(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.Hierarchy updateHierarchy(info.scce.cinco.product.hierarchy.hierarchy.Hierarchy apiEntity, info.scce.pyro.hierarchy.rest.Hierarchy update){
			// handle type
			return updateHierarchy(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.hierarchy.hierarchy.Hierarchy updateHierarchy(info.scce.cinco.product.hierarchy.hierarchy.Hierarchy apiEntity, info.scce.pyro.hierarchy.rest.Hierarchy update, boolean propagate){
			// handle type
			entity.hierarchy.HierarchyDB dbEntity = (entity.hierarchy.HierarchyDB) apiEntity.getDelegate();
			info.scce.pyro.hierarchy.rest.Hierarchy prev = info.scce.pyro.hierarchy.rest.Hierarchy.fromEntityProperties(dbEntity,null);
			
		
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
			// type
			if(update.getta() != null) {
				//update user defined type
				PanacheEntity newTa = (PanacheEntity) updateTA(update.getta()).getDelegate();
				
				if(!newTa.equals(dbEntity.getTa())) {
					// update new value
					dbEntity.setTa(newTa, true);
				}
			} else if(dbEntity.getTa() != null) {
				// update new value
				dbEntity.setTa(null, true);
			}
			
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.hierarchy.rest.Hierarchy.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		@Override
		public void updateAppearance() {
			super.getAllModelElements().forEach((element)->{
			});
		}

		public void removeTA(info.scce.cinco.product.hierarchy.hierarchy.TA apiEntity){
			PanacheEntity entity = apiEntity.getDelegate();
			removeTA((entity.hierarchy.TADB) entity);
		}
		
		public void removeTA(entity.hierarchy.TADB entity){
			//for enums
			//remove all complex fieds
			entity.delete();
		}
		public void removeTB(info.scce.cinco.product.hierarchy.hierarchy.TB apiEntity){
			PanacheEntity entity = apiEntity.getDelegate();
			removeTB(entity);
		}
		
		public void removeTB(PanacheEntity entity) {
			if(entity instanceof entity.hierarchy.TADB) {
				removeTA((entity.hierarchy.TADB) entity);
			} else if(entity instanceof entity.hierarchy.TDDB) {
				removeTD((entity.hierarchy.TDDB) entity);
			}
		}
		public void removeTC(info.scce.cinco.product.hierarchy.hierarchy.TC apiEntity){
			PanacheEntity entity = apiEntity.getDelegate();
			removeTC(entity);
		}
		
		public void removeTC(PanacheEntity entity) {
			if(entity instanceof entity.hierarchy.TADB) {
				removeTA((entity.hierarchy.TADB) entity);
			} else if(entity instanceof entity.hierarchy.TDDB) {
				removeTD((entity.hierarchy.TDDB) entity);
			}
		}
		public void removeTD(info.scce.cinco.product.hierarchy.hierarchy.TD apiEntity){
			PanacheEntity entity = apiEntity.getDelegate();
			removeTD((entity.hierarchy.TDDB) entity);
		}
		
		public void removeTD(entity.hierarchy.TDDB entity){
			//for enums
			//remove all complex fieds
			entity.delete();
		}
	}
