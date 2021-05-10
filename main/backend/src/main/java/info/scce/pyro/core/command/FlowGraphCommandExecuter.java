	package info.scce.pyro.core.command;
	
	import info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry;
	import info.scce.pyro.core.graphmodel.BendingPoint;
	import graphmodel.*;
	import entity.core.PyroUserDB;
	import info.scce.pyro.sync.GraphModelWebSocket;
	import io.quarkus.hibernate.orm.panache.PanacheEntity;
	
	/**
	 * Author zweihoff
	 */
	public class FlowGraphCommandExecuter extends CommandExecuter {
		
		private info.scce.pyro.rest.ObjectCache objectCache;
		private GraphModelWebSocket graphModelWebSocket;
		
		public FlowGraphCommandExecuter(
			PyroUserDB user,
			info.scce.pyro.rest.ObjectCache objectCache,
			GraphModelWebSocket graphModelWebSocket,
			entity.flowgraph.FlowGraphDB graph,
			java.util.List<info.scce.pyro.core.command.types.HighlightCommand> highlightings
		) {
			super(
				graphModelWebSocket,
				highlightings
			);
			this.objectCache = objectCache;
			super.batch = new BatchExecution(user,new info.scce.cinco.product.flowgraph.flowgraph.impl.FlowGraphImpl(graph,this));
		}
		
		/**
		 * NOTE: Use this if it is needed to utilize (/work on) the same batch of commands
		 * of the GraphModelCommandExecuter and on the one of a primeReferenced GraphModel
		 */
		public FlowGraphCommandExecuter(
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
		
		public void removeFlowGraph(info.scce.cinco.product.flowgraph.flowgraph.FlowGraph entity){
			//for complex props
			entity.delete();
			/*
			*/
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.Start createStart(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.flowgraph.rest.Start prev){
			entity.flowgraph.StartDB node = new entity.flowgraph.StartDB();
			node.width = width;
			node.height = height;
			node.x = x;
			node.y = y;
			
			//primitive init
			
			// setting container
			PanacheEntity dbMec = TypeRegistry.getApiToDB(mec);
			node.setContainer(dbMec);
			
		    node.persist();
		    
		    info.scce.cinco.product.flowgraph.flowgraph.Start apiNode = new info.scce.cinco.product.flowgraph.flowgraph.impl.StartImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
				info.scce.pyro.flowgraph.rest.Start.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updateStart(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.Start createStart(long x, long y, ModelElementContainer mec, info.scce.pyro.flowgraph.rest.Start prev) {
			return createStart(
				x,
				y,
				36,
				36,
				mec,
				prev);
		}
		
		public void removeStart(
			info.scce.cinco.product.flowgraph.flowgraph.Start entity
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				null,
				info.scce.pyro.flowgraph.rest.Start.fromEntityProperties((entity.flowgraph.StartDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.flowgraph.flowgraph.End createEnd(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.flowgraph.rest.End prev){
			entity.flowgraph.EndDB node = new entity.flowgraph.EndDB();
			node.width = width;
			node.height = height;
			node.x = x;
			node.y = y;
			
			//primitive init
			
			// setting container
			PanacheEntity dbMec = TypeRegistry.getApiToDB(mec);
			node.setContainer(dbMec);
			
		    node.persist();
		    
		    info.scce.cinco.product.flowgraph.flowgraph.End apiNode = new info.scce.cinco.product.flowgraph.flowgraph.impl.EndImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
				info.scce.pyro.flowgraph.rest.End.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updateEnd(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.End createEnd(long x, long y, ModelElementContainer mec, info.scce.pyro.flowgraph.rest.End prev) {
			return createEnd(
				x,
				y,
				36,
				36,
				mec,
				prev);
		}
		
		public void removeEnd(
			info.scce.cinco.product.flowgraph.flowgraph.End entity
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				null,
				info.scce.pyro.flowgraph.rest.End.fromEntityProperties((entity.flowgraph.EndDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.flowgraph.flowgraph.Activity createActivity(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.flowgraph.rest.Activity prev){
			entity.flowgraph.ActivityDB node = new entity.flowgraph.ActivityDB();
			node.width = width;
			node.height = height;
			node.x = x;
			node.y = y;
			
			//primitive init
			node.name = null;
			node.description = null;
			
			// setting container
			PanacheEntity dbMec = TypeRegistry.getApiToDB(mec);
			node.setContainer(dbMec);
			
		    node.persist();
		    
		    info.scce.cinco.product.flowgraph.flowgraph.Activity apiNode = new info.scce.cinco.product.flowgraph.flowgraph.impl.ActivityImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
				info.scce.pyro.flowgraph.rest.Activity.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updateActivity(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.Activity createActivity(long x, long y, ModelElementContainer mec, info.scce.pyro.flowgraph.rest.Activity prev) {
			return createActivity(
				x,
				y,
				96,
				32,
				mec,
				prev);
		}
		
		public void removeActivity(
			info.scce.cinco.product.flowgraph.flowgraph.Activity entity
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				null,
				info.scce.pyro.flowgraph.rest.Activity.fromEntityProperties((entity.flowgraph.ActivityDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.flowgraph.flowgraph.EActivityA createEActivityA(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.flowgraph.rest.EActivityA prev,long primeId){
			entity.flowgraph.EActivityADB node = new entity.flowgraph.EActivityADB();
			node.width = width;
			node.height = height;
			node.x = x;
			node.y = y;
			
			//primitive init
			
			// setting container
			PanacheEntity dbMec = TypeRegistry.getApiToDB(mec);
			node.setContainer(dbMec);
			
			PanacheEntity prime = null;
			if(prime == null) {
				prime = entity.externallibrary.ExternalActivityADB.findById(primeId);
			}
			node.setActivityC(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.flowgraph.flowgraph.EActivityA apiNode = new info.scce.cinco.product.flowgraph.flowgraph.impl.EActivityAImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.externallibrary.rest.ExternalAbstractActivityC.fromEntityProperties(prime,null),
				info.scce.pyro.flowgraph.rest.EActivityA.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updateEActivityA(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.EActivityA createEActivityA(long x, long y, ModelElementContainer mec, info.scce.pyro.flowgraph.rest.EActivityA prev,long primeId) {
			return createEActivityA(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removeEActivityA(
			info.scce.cinco.product.flowgraph.flowgraph.EActivityA entity,
			externallibrary.ExternalAbstractActivityC prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.flowgraph.rest.EActivityA.fromEntityProperties((entity.flowgraph.EActivityADB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.flowgraph.flowgraph.EActivityB createEActivityB(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.flowgraph.rest.EActivityB prev,long primeId){
			entity.flowgraph.EActivityBDB node = new entity.flowgraph.EActivityBDB();
			node.width = width;
			node.height = height;
			node.x = x;
			node.y = y;
			
			//primitive init
			
			// setting container
			PanacheEntity dbMec = TypeRegistry.getApiToDB(mec);
			node.setContainer(dbMec);
			
			PanacheEntity prime = null;
			if(prime == null) {
				prime = entity.externallibrary.ExternalActivityDDB.findById(primeId);
			}
			if(prime == null) {
				prime = entity.externallibrary.ExternalActivityADB.findById(primeId);
			}
			node.setActivityD(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.flowgraph.flowgraph.EActivityB apiNode = new info.scce.cinco.product.flowgraph.flowgraph.impl.EActivityBImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.externallibrary.rest.ExternalActivityD.fromEntityProperties(prime,null),
				info.scce.pyro.flowgraph.rest.EActivityB.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updateEActivityB(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.EActivityB createEActivityB(long x, long y, ModelElementContainer mec, info.scce.pyro.flowgraph.rest.EActivityB prev,long primeId) {
			return createEActivityB(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removeEActivityB(
			info.scce.cinco.product.flowgraph.flowgraph.EActivityB entity,
			externallibrary.ExternalActivityD prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.flowgraph.rest.EActivityB.fromEntityProperties((entity.flowgraph.EActivityBDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.flowgraph.flowgraph.ELibrary createELibrary(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.flowgraph.rest.ELibrary prev,long primeId){
			entity.flowgraph.ELibraryDB node = new entity.flowgraph.ELibraryDB();
			node.width = width;
			node.height = height;
			node.x = x;
			node.y = y;
			
			//primitive init
			
			// setting container
			PanacheEntity dbMec = TypeRegistry.getApiToDB(mec);
			node.setContainer(dbMec);
			
			PanacheEntity prime = null;
			if(prime == null) {
				prime = entity.externallibrary.ExternalActivityLibraryDB.findById(primeId);
			}
			node.setLibrary(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.flowgraph.flowgraph.ELibrary apiNode = new info.scce.cinco.product.flowgraph.flowgraph.impl.ELibraryImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.externallibrary.rest.ExternalActivityLibrary.fromEntityProperties(prime,null),
				info.scce.pyro.flowgraph.rest.ELibrary.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updateELibrary(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.ELibrary createELibrary(long x, long y, ModelElementContainer mec, info.scce.pyro.flowgraph.rest.ELibrary prev,long primeId) {
			return createELibrary(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removeELibrary(
			info.scce.cinco.product.flowgraph.flowgraph.ELibrary entity,
			externallibrary.ExternalActivityLibrary prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.flowgraph.rest.ELibrary.fromEntityProperties((entity.flowgraph.ELibraryDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph createSubFlowGraph(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.flowgraph.rest.SubFlowGraph prev,long primeId){
			entity.flowgraph.SubFlowGraphDB node = new entity.flowgraph.SubFlowGraphDB();
			node.width = width;
			node.height = height;
			node.x = x;
			node.y = y;
			
			//primitive init
			
			// setting container
			PanacheEntity dbMec = TypeRegistry.getApiToDB(mec);
			node.setContainer(dbMec);
			
			PanacheEntity prime = null;
			if(prime == null) {
				prime = entity.flowgraph.FlowGraphDB.findById(primeId);
			}
			node.setSubFlowGraph(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph apiNode = new info.scce.cinco.product.flowgraph.flowgraph.impl.SubFlowGraphImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.flowgraph.rest.FlowGraph.fromEntityProperties(prime,null),
				info.scce.pyro.flowgraph.rest.SubFlowGraph.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updateSubFlowGraph(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph createSubFlowGraph(long x, long y, ModelElementContainer mec, info.scce.pyro.flowgraph.rest.SubFlowGraph prev,long primeId) {
			return createSubFlowGraph(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removeSubFlowGraph(
			info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph entity,
			info.scce.cinco.product.flowgraph.flowgraph.FlowGraph prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.flowgraph.rest.SubFlowGraph.fromEntityProperties((entity.flowgraph.SubFlowGraphDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.flowgraph.flowgraph.Swimlane createSwimlane(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.flowgraph.rest.Swimlane prev){
			entity.flowgraph.SwimlaneDB node = new entity.flowgraph.SwimlaneDB();
			node.width = width;
			node.height = height;
			node.x = x;
			node.y = y;
			
			//primitive init
			node.actor = null;
			
			// setting container
			PanacheEntity dbMec = TypeRegistry.getApiToDB(mec);
			node.setContainer(dbMec);
			
		    node.persist();
		    
		    info.scce.cinco.product.flowgraph.flowgraph.Swimlane apiNode = new info.scce.cinco.product.flowgraph.flowgraph.impl.SwimlaneImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
				info.scce.pyro.flowgraph.rest.Swimlane.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updateSwimlane(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.Swimlane createSwimlane(long x, long y, ModelElementContainer mec, info.scce.pyro.flowgraph.rest.Swimlane prev) {
			return createSwimlane(
				x,
				y,
				400,
				100,
				mec,
				prev);
		}
		
		public void removeSwimlane(
			info.scce.cinco.product.flowgraph.flowgraph.Swimlane entity
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				null,
				info.scce.pyro.flowgraph.rest.Swimlane.fromEntityProperties((entity.flowgraph.SwimlaneDB) entity.getDelegate(),null)
			);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.Transition createTransition(Node source, Node target, java.util.List<BendingPoint> positions, info.scce.pyro.flowgraph.rest.Transition prev){
			entity.flowgraph.TransitionDB edge = new entity.flowgraph.TransitionDB();
			//primitive init
			
			setEdgeDBComponents(edge, source, target, positions);
			edge.persist();
		
			info.scce.cinco.product.flowgraph.flowgraph.Transition apiEdge = new info.scce.cinco.product.flowgraph.flowgraph.impl.TransitionImpl(edge,this);
			super.createEdge(
				TypeRegistry.getTypeOf(apiEdge),
				apiEdge,
				source,
				TypeRegistry.getTypeOf(source),
				target,
				TypeRegistry.getTypeOf(target),
				edge.bendingPoints,
				info.scce.pyro.flowgraph.rest.Transition.fromEntityProperties(edge,null)
			);
			if(prev != null) {
				//create from copy
				this.updateTransition(apiEdge,prev,true);
			}
			
			
			return apiEdge;
		}
		
		public void addBendpointTransition(info.scce.cinco.product.flowgraph.flowgraph.Transition edge, long x,long y){
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
		
		public void updateTransition(info.scce.cinco.product.flowgraph.flowgraph.Transition edge, java.util.List<BendingPoint> points){
			super.updateBendingPoints(TypeRegistry.getTypeOf(edge),edge,points);
		}
		
		public void removeTransition(info.scce.cinco.product.flowgraph.flowgraph.Transition entity){
			super.removeEdge(
				TypeRegistry.getTypeOf(entity),
				entity,
				info.scce.pyro.flowgraph.rest.Transition.fromEntityProperties((entity.flowgraph.TransitionDB) entity.getDelegate(),null),
				TypeRegistry.getTypeOf(entity.getSourceElement()),
				TypeRegistry.getTypeOf(entity.getTargetElement())
			);
		}
		
		public void setTransitionContainer(entity.flowgraph.TransitionDB edge, PanacheEntity container) {
			if(container instanceof entity.flowgraph.FlowGraphDB) {
				entity.flowgraph.FlowGraphDB containerDB = (entity.flowgraph.FlowGraphDB) container;
				containerDB.addModelElements(edge);
				edge.setContainer(container);
				container.persist();
			} else if(container instanceof entity.flowgraph.FlowGraphDB) {
				entity.flowgraph.FlowGraphDB containerDB = (entity.flowgraph.FlowGraphDB) container;
				containerDB.addModelElements(edge);
				edge.setContainer(container);
				container.persist();
			}
		}
		
		public void setTransitionDBSource(entity.flowgraph.TransitionDB edge, Node source) {
			if(source instanceof info.scce.cinco.product.flowgraph.flowgraph.Start) {
				entity.flowgraph.StartDB o = (entity.flowgraph.StartDB) ((info.scce.cinco.product.flowgraph.flowgraph.Start) source).getDelegate();
				edge.setSource(o);
				o.addOutgoing(edge);
				o.persist();
			}
		}
		
		public void setTransitionDBTarget(entity.flowgraph.TransitionDB edge, Node target) {
			if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.End) {
				entity.flowgraph.EndDB o = (entity.flowgraph.EndDB) ((info.scce.cinco.product.flowgraph.flowgraph.End) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			} else if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
				entity.flowgraph.ActivityDB o = (entity.flowgraph.ActivityDB) ((info.scce.cinco.product.flowgraph.flowgraph.Activity) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			} else if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityA) {
				entity.flowgraph.EActivityADB o = (entity.flowgraph.EActivityADB) ((info.scce.cinco.product.flowgraph.flowgraph.EActivityA) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			} else if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityB) {
				entity.flowgraph.EActivityBDB o = (entity.flowgraph.EActivityBDB) ((info.scce.cinco.product.flowgraph.flowgraph.EActivityB) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			} else if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.ELibrary) {
				entity.flowgraph.ELibraryDB o = (entity.flowgraph.ELibraryDB) ((info.scce.cinco.product.flowgraph.flowgraph.ELibrary) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			} else if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
				entity.flowgraph.SubFlowGraphDB o = (entity.flowgraph.SubFlowGraphDB) ((info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			}
		}
		public info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition createLabeledTransition(Node source, Node target, java.util.List<BendingPoint> positions, info.scce.pyro.flowgraph.rest.LabeledTransition prev){
			entity.flowgraph.LabeledTransitionDB edge = new entity.flowgraph.LabeledTransitionDB();
			//primitive init
			edge.label = null;
			
			setEdgeDBComponents(edge, source, target, positions);
			edge.persist();
		
			info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition apiEdge = new info.scce.cinco.product.flowgraph.flowgraph.impl.LabeledTransitionImpl(edge,this);
			super.createEdge(
				TypeRegistry.getTypeOf(apiEdge),
				apiEdge,
				source,
				TypeRegistry.getTypeOf(source),
				target,
				TypeRegistry.getTypeOf(target),
				edge.bendingPoints,
				info.scce.pyro.flowgraph.rest.LabeledTransition.fromEntityProperties(edge,null)
			);
			if(prev != null) {
				//create from copy
				this.updateLabeledTransition(apiEdge,prev,true);
			}
			
			
			return apiEdge;
		}
		
		public void addBendpointLabeledTransition(info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition edge, long x,long y){
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
		
		public void updateLabeledTransition(info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition edge, java.util.List<BendingPoint> points){
			super.updateBendingPoints(TypeRegistry.getTypeOf(edge),edge,points);
		}
		
		public void removeLabeledTransition(info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition entity){
			super.removeEdge(
				TypeRegistry.getTypeOf(entity),
				entity,
				info.scce.pyro.flowgraph.rest.LabeledTransition.fromEntityProperties((entity.flowgraph.LabeledTransitionDB) entity.getDelegate(),null),
				TypeRegistry.getTypeOf(entity.getSourceElement()),
				TypeRegistry.getTypeOf(entity.getTargetElement())
			);
		}
		
		public void setLabeledTransitionContainer(entity.flowgraph.LabeledTransitionDB edge, PanacheEntity container) {
			if(container instanceof entity.flowgraph.FlowGraphDB) {
				entity.flowgraph.FlowGraphDB containerDB = (entity.flowgraph.FlowGraphDB) container;
				containerDB.addModelElements(edge);
				edge.setContainer(container);
				container.persist();
			} else if(container instanceof entity.flowgraph.FlowGraphDB) {
				entity.flowgraph.FlowGraphDB containerDB = (entity.flowgraph.FlowGraphDB) container;
				containerDB.addModelElements(edge);
				edge.setContainer(container);
				container.persist();
			}
		}
		
		public void setLabeledTransitionDBSource(entity.flowgraph.LabeledTransitionDB edge, Node source) {
			if(source instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
				entity.flowgraph.ActivityDB o = (entity.flowgraph.ActivityDB) ((info.scce.cinco.product.flowgraph.flowgraph.Activity) source).getDelegate();
				edge.setSource(o);
				o.addOutgoing(edge);
				o.persist();
			} else if(source instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityA) {
				entity.flowgraph.EActivityADB o = (entity.flowgraph.EActivityADB) ((info.scce.cinco.product.flowgraph.flowgraph.EActivityA) source).getDelegate();
				edge.setSource(o);
				o.addOutgoing(edge);
				o.persist();
			} else if(source instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityB) {
				entity.flowgraph.EActivityBDB o = (entity.flowgraph.EActivityBDB) ((info.scce.cinco.product.flowgraph.flowgraph.EActivityB) source).getDelegate();
				edge.setSource(o);
				o.addOutgoing(edge);
				o.persist();
			} else if(source instanceof info.scce.cinco.product.flowgraph.flowgraph.ELibrary) {
				entity.flowgraph.ELibraryDB o = (entity.flowgraph.ELibraryDB) ((info.scce.cinco.product.flowgraph.flowgraph.ELibrary) source).getDelegate();
				edge.setSource(o);
				o.addOutgoing(edge);
				o.persist();
			} else if(source instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
				entity.flowgraph.SubFlowGraphDB o = (entity.flowgraph.SubFlowGraphDB) ((info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) source).getDelegate();
				edge.setSource(o);
				o.addOutgoing(edge);
				o.persist();
			}
		}
		
		public void setLabeledTransitionDBTarget(entity.flowgraph.LabeledTransitionDB edge, Node target) {
			if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.End) {
				entity.flowgraph.EndDB o = (entity.flowgraph.EndDB) ((info.scce.cinco.product.flowgraph.flowgraph.End) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			} else if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
				entity.flowgraph.ActivityDB o = (entity.flowgraph.ActivityDB) ((info.scce.cinco.product.flowgraph.flowgraph.Activity) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			} else if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityA) {
				entity.flowgraph.EActivityADB o = (entity.flowgraph.EActivityADB) ((info.scce.cinco.product.flowgraph.flowgraph.EActivityA) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			} else if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityB) {
				entity.flowgraph.EActivityBDB o = (entity.flowgraph.EActivityBDB) ((info.scce.cinco.product.flowgraph.flowgraph.EActivityB) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			} else if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.ELibrary) {
				entity.flowgraph.ELibraryDB o = (entity.flowgraph.ELibraryDB) ((info.scce.cinco.product.flowgraph.flowgraph.ELibrary) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			} else if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
				entity.flowgraph.SubFlowGraphDB o = (entity.flowgraph.SubFlowGraphDB) ((info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			}
		}

	    public void setEdgeDBComponents(PanacheEntity edge, Node source, Node target, java.util.List<BendingPoint> bendingPoints) {
	    	graphmodel.GraphModel graphModel = source.getRootElement();
	    	PanacheEntity e = TypeRegistry.getApiToDB(graphModel);
	    	
	    	// switch edge types
	    	if(edge instanceof entity.flowgraph.TransitionDB) {
	    		entity.flowgraph.TransitionDB edgeDB = (entity.flowgraph.TransitionDB) edge;
	    		setTransitionDBSource(edgeDB, source);
	    		setTransitionDBTarget(edgeDB, target);
	    		setTransitionContainer(edgeDB, e);
	    		bendingPoints.forEach( p -> {
	    			entity.core.BendingPointDB bp = new entity.core.BendingPointDB();
	    			bp.x = p.getx();
	    			bp.y = p.gety();
	    			bp.persist();
	    			edgeDB.bendingPoints.add(bp);
	    		});
	    	} else if(edge instanceof entity.flowgraph.LabeledTransitionDB) {
	    		entity.flowgraph.LabeledTransitionDB edgeDB = (entity.flowgraph.LabeledTransitionDB) edge;
	    		setLabeledTransitionDBSource(edgeDB, source);
	    		setLabeledTransitionDBTarget(edgeDB, target);
	    		setLabeledTransitionContainer(edgeDB, e);
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
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.Start) {
		    	updateStartProperties((info.scce.cinco.product.flowgraph.flowgraph.Start) entity,(info.scce.pyro.flowgraph.rest.Start)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.End) {
		    	updateEndProperties((info.scce.cinco.product.flowgraph.flowgraph.End) entity,(info.scce.pyro.flowgraph.rest.End)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
		    	updateActivityProperties((info.scce.cinco.product.flowgraph.flowgraph.Activity) entity,(info.scce.pyro.flowgraph.rest.Activity)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityA) {
		    	updateEActivityAProperties((info.scce.cinco.product.flowgraph.flowgraph.EActivityA) entity,(info.scce.pyro.flowgraph.rest.EActivityA)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.EActivityB) {
		    	updateEActivityBProperties((info.scce.cinco.product.flowgraph.flowgraph.EActivityB) entity,(info.scce.pyro.flowgraph.rest.EActivityB)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.ELibrary) {
		    	updateELibraryProperties((info.scce.cinco.product.flowgraph.flowgraph.ELibrary) entity,(info.scce.pyro.flowgraph.rest.ELibrary)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
		    	updateSubFlowGraphProperties((info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) entity,(info.scce.pyro.flowgraph.rest.SubFlowGraph)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane) {
		    	updateSwimlaneProperties((info.scce.cinco.product.flowgraph.flowgraph.Swimlane) entity,(info.scce.pyro.flowgraph.rest.Swimlane)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition) {
		    	updateTransitionProperties((info.scce.cinco.product.flowgraph.flowgraph.Transition) entity,(info.scce.pyro.flowgraph.rest.Transition)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) {
		    	updateLabeledTransitionProperties((info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) entity,(info.scce.pyro.flowgraph.rest.LabeledTransition)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraph) {
		    	updateFlowGraphProperties((info.scce.cinco.product.flowgraph.flowgraph.FlowGraph) entity,(info.scce.pyro.flowgraph.rest.FlowGraph)prev);
		    	return;
		    }
	    }

		public void updateStartProperties(info.scce.cinco.product.flowgraph.flowgraph.Start entity, info.scce.pyro.flowgraph.rest.Start prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.Start.fromEntityProperties(
					(entity.flowgraph.StartDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateEndProperties(info.scce.cinco.product.flowgraph.flowgraph.End entity, info.scce.pyro.flowgraph.rest.End prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.End.fromEntityProperties(
					(entity.flowgraph.EndDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateActivityProperties(info.scce.cinco.product.flowgraph.flowgraph.Activity entity, info.scce.pyro.flowgraph.rest.Activity prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.Activity.fromEntityProperties(
					(entity.flowgraph.ActivityDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateEActivityAProperties(info.scce.cinco.product.flowgraph.flowgraph.EActivityA entity, info.scce.pyro.flowgraph.rest.EActivityA prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.EActivityA.fromEntityProperties(
					(entity.flowgraph.EActivityADB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateEActivityBProperties(info.scce.cinco.product.flowgraph.flowgraph.EActivityB entity, info.scce.pyro.flowgraph.rest.EActivityB prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.EActivityB.fromEntityProperties(
					(entity.flowgraph.EActivityBDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateELibraryProperties(info.scce.cinco.product.flowgraph.flowgraph.ELibrary entity, info.scce.pyro.flowgraph.rest.ELibrary prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.ELibrary.fromEntityProperties(
					(entity.flowgraph.ELibraryDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateSubFlowGraphProperties(info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph entity, info.scce.pyro.flowgraph.rest.SubFlowGraph prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.SubFlowGraph.fromEntityProperties(
					(entity.flowgraph.SubFlowGraphDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateSwimlaneProperties(info.scce.cinco.product.flowgraph.flowgraph.Swimlane entity, info.scce.pyro.flowgraph.rest.Swimlane prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.Swimlane.fromEntityProperties(
					(entity.flowgraph.SwimlaneDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateTransitionProperties(info.scce.cinco.product.flowgraph.flowgraph.Transition entity, info.scce.pyro.flowgraph.rest.Transition prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.Transition.fromEntityProperties(
					(entity.flowgraph.TransitionDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateLabeledTransitionProperties(info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition entity, info.scce.pyro.flowgraph.rest.LabeledTransition prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.LabeledTransition.fromEntityProperties(
					(entity.flowgraph.LabeledTransitionDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateFlowGraphProperties(info.scce.cinco.product.flowgraph.flowgraph.FlowGraph entity, info.scce.pyro.flowgraph.rest.FlowGraph prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.FlowGraph.fromEntityProperties(
					(entity.flowgraph.FlowGraphDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		//FOR NODE EDGE GRAPHMODEL TYPE
		public info.scce.cinco.product.flowgraph.flowgraph.Start updateStart(info.scce.pyro.flowgraph.rest.Start update){
			entity.flowgraph.StartDB dbEntity = entity.flowgraph.StartDB.findById(update.getId());
			info.scce.cinco.product.flowgraph.flowgraph.Start apiEntity = (info.scce.cinco.product.flowgraph.flowgraph.Start) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateStart(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.Start updateStart(info.scce.cinco.product.flowgraph.flowgraph.Start apiEntity, info.scce.pyro.flowgraph.rest.Start update){
			// handle type
			return updateStart(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.Start updateStart(info.scce.cinco.product.flowgraph.flowgraph.Start apiEntity, info.scce.pyro.flowgraph.rest.Start update, boolean propagate){
			// handle type
			entity.flowgraph.StartDB dbEntity = (entity.flowgraph.StartDB) apiEntity.getDelegate();
			info.scce.pyro.flowgraph.rest.Start prev = info.scce.pyro.flowgraph.rest.Start.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.flowgraph.rest.Start.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.End updateEnd(info.scce.pyro.flowgraph.rest.End update){
			entity.flowgraph.EndDB dbEntity = entity.flowgraph.EndDB.findById(update.getId());
			info.scce.cinco.product.flowgraph.flowgraph.End apiEntity = (info.scce.cinco.product.flowgraph.flowgraph.End) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateEnd(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.End updateEnd(info.scce.cinco.product.flowgraph.flowgraph.End apiEntity, info.scce.pyro.flowgraph.rest.End update){
			// handle type
			return updateEnd(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.End updateEnd(info.scce.cinco.product.flowgraph.flowgraph.End apiEntity, info.scce.pyro.flowgraph.rest.End update, boolean propagate){
			// handle type
			entity.flowgraph.EndDB dbEntity = (entity.flowgraph.EndDB) apiEntity.getDelegate();
			info.scce.pyro.flowgraph.rest.End prev = info.scce.pyro.flowgraph.rest.End.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.flowgraph.rest.End.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.Activity updateActivity(info.scce.pyro.flowgraph.rest.Activity update){
			entity.flowgraph.ActivityDB dbEntity = entity.flowgraph.ActivityDB.findById(update.getId());
			info.scce.cinco.product.flowgraph.flowgraph.Activity apiEntity = (info.scce.cinco.product.flowgraph.flowgraph.Activity) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateActivity(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.Activity updateActivity(info.scce.cinco.product.flowgraph.flowgraph.Activity apiEntity, info.scce.pyro.flowgraph.rest.Activity update){
			// handle type
			return updateActivity(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.Activity updateActivity(info.scce.cinco.product.flowgraph.flowgraph.Activity apiEntity, info.scce.pyro.flowgraph.rest.Activity update, boolean propagate){
			// handle type
			entity.flowgraph.ActivityDB dbEntity = (entity.flowgraph.ActivityDB) apiEntity.getDelegate();
			info.scce.pyro.flowgraph.rest.Activity prev = info.scce.pyro.flowgraph.rest.Activity.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			if(
				dbEntity.name != null &&
				!dbEntity.name.equals(update.getname())
				||
				update.getname() != null &&
				!update.getname().equals(dbEntity.name)
				
			) { // value changed?
				dbEntity.name = update.getname();
			}
			if(
				dbEntity.description != null &&
				!dbEntity.description.equals(update.getdescription())
				||
				update.getdescription() != null &&
				!update.getdescription().equals(dbEntity.description)
				
			) { // value changed?
				dbEntity.description = update.getdescription();
			}
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.flowgraph.rest.Activity.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.EActivityA updateEActivityA(info.scce.pyro.flowgraph.rest.EActivityA update){
			entity.flowgraph.EActivityADB dbEntity = entity.flowgraph.EActivityADB.findById(update.getId());
			info.scce.cinco.product.flowgraph.flowgraph.EActivityA apiEntity = (info.scce.cinco.product.flowgraph.flowgraph.EActivityA) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateEActivityA(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.EActivityA updateEActivityA(info.scce.cinco.product.flowgraph.flowgraph.EActivityA apiEntity, info.scce.pyro.flowgraph.rest.EActivityA update){
			// handle type
			return updateEActivityA(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.EActivityA updateEActivityA(info.scce.cinco.product.flowgraph.flowgraph.EActivityA apiEntity, info.scce.pyro.flowgraph.rest.EActivityA update, boolean propagate){
			// handle type
			entity.flowgraph.EActivityADB dbEntity = (entity.flowgraph.EActivityADB) apiEntity.getDelegate();
			info.scce.pyro.flowgraph.rest.EActivityA prev = info.scce.pyro.flowgraph.rest.EActivityA.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.flowgraph.rest.EActivityA.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.EActivityB updateEActivityB(info.scce.pyro.flowgraph.rest.EActivityB update){
			entity.flowgraph.EActivityBDB dbEntity = entity.flowgraph.EActivityBDB.findById(update.getId());
			info.scce.cinco.product.flowgraph.flowgraph.EActivityB apiEntity = (info.scce.cinco.product.flowgraph.flowgraph.EActivityB) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateEActivityB(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.EActivityB updateEActivityB(info.scce.cinco.product.flowgraph.flowgraph.EActivityB apiEntity, info.scce.pyro.flowgraph.rest.EActivityB update){
			// handle type
			return updateEActivityB(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.EActivityB updateEActivityB(info.scce.cinco.product.flowgraph.flowgraph.EActivityB apiEntity, info.scce.pyro.flowgraph.rest.EActivityB update, boolean propagate){
			// handle type
			entity.flowgraph.EActivityBDB dbEntity = (entity.flowgraph.EActivityBDB) apiEntity.getDelegate();
			info.scce.pyro.flowgraph.rest.EActivityB prev = info.scce.pyro.flowgraph.rest.EActivityB.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.flowgraph.rest.EActivityB.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.ELibrary updateELibrary(info.scce.pyro.flowgraph.rest.ELibrary update){
			entity.flowgraph.ELibraryDB dbEntity = entity.flowgraph.ELibraryDB.findById(update.getId());
			info.scce.cinco.product.flowgraph.flowgraph.ELibrary apiEntity = (info.scce.cinco.product.flowgraph.flowgraph.ELibrary) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateELibrary(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.ELibrary updateELibrary(info.scce.cinco.product.flowgraph.flowgraph.ELibrary apiEntity, info.scce.pyro.flowgraph.rest.ELibrary update){
			// handle type
			return updateELibrary(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.ELibrary updateELibrary(info.scce.cinco.product.flowgraph.flowgraph.ELibrary apiEntity, info.scce.pyro.flowgraph.rest.ELibrary update, boolean propagate){
			// handle type
			entity.flowgraph.ELibraryDB dbEntity = (entity.flowgraph.ELibraryDB) apiEntity.getDelegate();
			info.scce.pyro.flowgraph.rest.ELibrary prev = info.scce.pyro.flowgraph.rest.ELibrary.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.flowgraph.rest.ELibrary.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph updateSubFlowGraph(info.scce.pyro.flowgraph.rest.SubFlowGraph update){
			entity.flowgraph.SubFlowGraphDB dbEntity = entity.flowgraph.SubFlowGraphDB.findById(update.getId());
			info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph apiEntity = (info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateSubFlowGraph(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph updateSubFlowGraph(info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph apiEntity, info.scce.pyro.flowgraph.rest.SubFlowGraph update){
			// handle type
			return updateSubFlowGraph(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph updateSubFlowGraph(info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph apiEntity, info.scce.pyro.flowgraph.rest.SubFlowGraph update, boolean propagate){
			// handle type
			entity.flowgraph.SubFlowGraphDB dbEntity = (entity.flowgraph.SubFlowGraphDB) apiEntity.getDelegate();
			info.scce.pyro.flowgraph.rest.SubFlowGraph prev = info.scce.pyro.flowgraph.rest.SubFlowGraph.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.flowgraph.rest.SubFlowGraph.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.Swimlane updateSwimlane(info.scce.pyro.flowgraph.rest.Swimlane update){
			entity.flowgraph.SwimlaneDB dbEntity = entity.flowgraph.SwimlaneDB.findById(update.getId());
			info.scce.cinco.product.flowgraph.flowgraph.Swimlane apiEntity = (info.scce.cinco.product.flowgraph.flowgraph.Swimlane) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateSwimlane(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.Swimlane updateSwimlane(info.scce.cinco.product.flowgraph.flowgraph.Swimlane apiEntity, info.scce.pyro.flowgraph.rest.Swimlane update){
			// handle type
			return updateSwimlane(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.Swimlane updateSwimlane(info.scce.cinco.product.flowgraph.flowgraph.Swimlane apiEntity, info.scce.pyro.flowgraph.rest.Swimlane update, boolean propagate){
			// handle type
			entity.flowgraph.SwimlaneDB dbEntity = (entity.flowgraph.SwimlaneDB) apiEntity.getDelegate();
			info.scce.pyro.flowgraph.rest.Swimlane prev = info.scce.pyro.flowgraph.rest.Swimlane.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			if(
				dbEntity.actor != null &&
				!dbEntity.actor.equals(update.getactor())
				||
				update.getactor() != null &&
				!update.getactor().equals(dbEntity.actor)
				
			) { // value changed?
				dbEntity.actor = update.getactor();
			}
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.flowgraph.rest.Swimlane.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.Transition updateTransition(info.scce.pyro.flowgraph.rest.Transition update){
			entity.flowgraph.TransitionDB dbEntity = entity.flowgraph.TransitionDB.findById(update.getId());
			info.scce.cinco.product.flowgraph.flowgraph.Transition apiEntity = (info.scce.cinco.product.flowgraph.flowgraph.Transition) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateTransition(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.Transition updateTransition(info.scce.cinco.product.flowgraph.flowgraph.Transition apiEntity, info.scce.pyro.flowgraph.rest.Transition update){
			// handle type
			return updateTransition(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.Transition updateTransition(info.scce.cinco.product.flowgraph.flowgraph.Transition apiEntity, info.scce.pyro.flowgraph.rest.Transition update, boolean propagate){
			// handle type
			entity.flowgraph.TransitionDB dbEntity = (entity.flowgraph.TransitionDB) apiEntity.getDelegate();
			info.scce.pyro.flowgraph.rest.Transition prev = info.scce.pyro.flowgraph.rest.Transition.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.flowgraph.rest.Transition.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		private void updateAppearanceProviderTransition(info.scce.cinco.product.flowgraph.flowgraph.Transition modelElement)
		{
			info.scce.cinco.product.flowgraph.appearance.SimpleArrowAppearance app = new info.scce.cinco.product.flowgraph.appearance.SimpleArrowAppearance();
			String elementName = TypeRegistry.getTypeOf(modelElement);
			style.Appearance root = style.StyleFactory.eINSTANCE.createAppearance();
			root.setId("root");
			root.setName("simpleArrow");
			root.setAngle(0.0F);
			root.setFilled(style.BooleanEnum.UNDEF);
			style.Color foregroundroot = style.StyleFactory.eINSTANCE.createColor();
			foregroundroot.setR(0);
			foregroundroot.setG(0);
			foregroundroot.setB(0);
			root.setForeground(foregroundroot);
			style.Color backgroundroot = style.StyleFactory.eINSTANCE.createColor();
			backgroundroot.setR(255);
			backgroundroot.setG(255);
			backgroundroot.setB(255);
			root.setBackground(backgroundroot);
			root.setLineInVisible(false);
			style.Font fontroot = style.StyleFactory.eINSTANCE.createFont();
			fontroot.setFontName("Helvetica");
			fontroot.setSize(12);
			fontroot.setIsBold(false);
			fontroot.setIsItalic(false);
			root.setFont(fontroot);
			root.setLineWidth(1);
			root.setLineStyle(style.LineStyle.SOLID);
			root.setTransparency(0.0);
			root.setParent(null);
			root.setImagePath(null);
			style.Appearance rootResult = app.getAppearance(modelElement,root.getName());
			if(rootResult != null) {
				style.Appearance rootMerged = super.mergeAppearance(root,rootResult);
				super.updateAppearance(elementName,modelElement,rootMerged);
			}
			style.Appearance pyrox0tag = style.StyleFactory.eINSTANCE.createAppearance();
			pyrox0tag.setId("pyrox0tag");
			pyrox0tag.setName("pyrox0tag");
			pyrox0tag.setAngle(0.0F);
			pyrox0tag.setFilled(style.BooleanEnum.UNDEF);
			style.Color foregroundpyrox0tag = style.StyleFactory.eINSTANCE.createColor();
			foregroundpyrox0tag.setR(0);
			foregroundpyrox0tag.setG(0);
			foregroundpyrox0tag.setB(0);
			pyrox0tag.setForeground(foregroundpyrox0tag);
			style.Color backgroundpyrox0tag = style.StyleFactory.eINSTANCE.createColor();
			backgroundpyrox0tag.setR(144);
			backgroundpyrox0tag.setG(207);
			backgroundpyrox0tag.setB(238);
			pyrox0tag.setBackground(backgroundpyrox0tag);
			pyrox0tag.setLineInVisible(false);
			style.Font fontpyrox0tag = style.StyleFactory.eINSTANCE.createFont();
			fontpyrox0tag.setFontName("Helvetica");
			fontpyrox0tag.setSize(12);
			fontpyrox0tag.setIsBold(false);
			fontpyrox0tag.setIsItalic(false);
			pyrox0tag.setFont(fontpyrox0tag);
			pyrox0tag.setLineWidth(2);
			pyrox0tag.setLineStyle(style.LineStyle.SOLID);
			pyrox0tag.setTransparency(0.0);
			pyrox0tag.setParent(null);
			pyrox0tag.setImagePath(null);
			style.Appearance pyrox0tagResult = app.getAppearance(modelElement,pyrox0tag.getName());
			if(pyrox0tagResult != null) {
				style.Appearance pyrox0tagMerged = super.mergeAppearance(pyrox0tag,pyrox0tagResult);
				super.updateAppearance(elementName,modelElement,pyrox0tagMerged);
			}
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition updateLabeledTransition(info.scce.pyro.flowgraph.rest.LabeledTransition update){
			entity.flowgraph.LabeledTransitionDB dbEntity = entity.flowgraph.LabeledTransitionDB.findById(update.getId());
			info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition apiEntity = (info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateLabeledTransition(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition updateLabeledTransition(info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition apiEntity, info.scce.pyro.flowgraph.rest.LabeledTransition update){
			// handle type
			return updateLabeledTransition(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition updateLabeledTransition(info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition apiEntity, info.scce.pyro.flowgraph.rest.LabeledTransition update, boolean propagate){
			// handle type
			entity.flowgraph.LabeledTransitionDB dbEntity = (entity.flowgraph.LabeledTransitionDB) apiEntity.getDelegate();
			info.scce.pyro.flowgraph.rest.LabeledTransition prev = info.scce.pyro.flowgraph.rest.LabeledTransition.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			if(
				dbEntity.label != null &&
				!dbEntity.label.equals(update.getlabel())
				||
				update.getlabel() != null &&
				!update.getlabel().equals(dbEntity.label)
				
			) { // value changed?
				dbEntity.label = update.getlabel();
			}
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.flowgraph.rest.LabeledTransition.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.FlowGraph updateFlowGraph(info.scce.pyro.flowgraph.rest.FlowGraph update){
			entity.flowgraph.FlowGraphDB dbEntity = entity.flowgraph.FlowGraphDB.findById(update.getId());
			info.scce.cinco.product.flowgraph.flowgraph.FlowGraph apiEntity = (info.scce.cinco.product.flowgraph.flowgraph.FlowGraph) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateFlowGraph(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.FlowGraph updateFlowGraph(info.scce.cinco.product.flowgraph.flowgraph.FlowGraph apiEntity, info.scce.pyro.flowgraph.rest.FlowGraph update){
			// handle type
			return updateFlowGraph(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.FlowGraph updateFlowGraph(info.scce.cinco.product.flowgraph.flowgraph.FlowGraph apiEntity, info.scce.pyro.flowgraph.rest.FlowGraph update, boolean propagate){
			// handle type
			entity.flowgraph.FlowGraphDB dbEntity = (entity.flowgraph.FlowGraphDB) apiEntity.getDelegate();
			info.scce.pyro.flowgraph.rest.FlowGraph prev = info.scce.pyro.flowgraph.rest.FlowGraph.fromEntityProperties(dbEntity,null);
			
		
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
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.flowgraph.rest.FlowGraph.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		@Override
		public void updateAppearance() {
			super.getAllModelElements().forEach((element)->{
				if(element instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition) {
					updateAppearanceProviderTransition((info.scce.cinco.product.flowgraph.flowgraph.Transition) element);
				}
			});
		}

	}
