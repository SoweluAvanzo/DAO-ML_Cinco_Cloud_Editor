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
	public class FlowGraphDiagramCommandExecuter extends CommandExecuter {
		
		private info.scce.pyro.rest.ObjectCache objectCache;
		private GraphModelWebSocket graphModelWebSocket;
		
		public FlowGraphDiagramCommandExecuter(
			PyroUserDB user,
			info.scce.pyro.rest.ObjectCache objectCache,
			GraphModelWebSocket graphModelWebSocket,
			entity.flowgraph.FlowGraphDiagramDB graph,
			java.util.List<info.scce.pyro.core.command.types.HighlightCommand> highlightings
		) {
			super(
				graphModelWebSocket,
				highlightings
			);
			this.objectCache = objectCache;
			super.batch = new BatchExecution(user,new info.scce.cinco.product.flowgraph.flowgraph.impl.FlowGraphDiagramImpl(graph,this));
		}
		
		/**
		 * NOTE: Use this if it is needed to utilize (/work on) the same batch of commands
		 * of the GraphModelCommandExecuter and on the one of a primeReferenced GraphModel
		 */
		public FlowGraphDiagramCommandExecuter(
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
		
		public void removeFlowGraphDiagram(info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram entity){
			//for complex props
			entity.delete();
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
				info.scce.pyro.flowgraph.rest.End.fromEntityProperties(
					node,
					new info.scce.pyro.rest.ObjectCache()
				)
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
				info.scce.pyro.flowgraph.rest.End.fromEntityProperties(
					(entity.flowgraph.EndDB) entity.getDelegate(),
					new info.scce.pyro.rest.ObjectCache()
				)
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
				info.scce.pyro.flowgraph.rest.Swimlane.fromEntityProperties(
					node,
					new info.scce.pyro.rest.ObjectCache()
				)
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
				info.scce.pyro.flowgraph.rest.Swimlane.fromEntityProperties(
					(entity.flowgraph.SwimlaneDB) entity.getDelegate(),
					new info.scce.pyro.rest.ObjectCache()
				)
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
				prime = entity.flowgraph.FlowGraphDiagramDB.findById(primeId);
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
		    	info.scce.pyro.flowgraph.rest.FlowGraphDiagram.fromEntityProperties(
		    		prime,
		    		new info.scce.pyro.rest.ObjectCache()
		    	),
				info.scce.pyro.flowgraph.rest.SubFlowGraph.fromEntityProperties(
					node,
					new info.scce.pyro.rest.ObjectCache()
				)
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
			info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.flowgraph.rest.SubFlowGraph.fromEntityProperties(
					(entity.flowgraph.SubFlowGraphDB) entity.getDelegate(),
					new info.scce.pyro.rest.ObjectCache()
				)
			);
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
				info.scce.pyro.flowgraph.rest.Start.fromEntityProperties(
					node,
					new info.scce.pyro.rest.ObjectCache()
				)
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
				info.scce.pyro.flowgraph.rest.Start.fromEntityProperties(
					(entity.flowgraph.StartDB) entity.getDelegate(),
					new info.scce.pyro.rest.ObjectCache()
				)
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
				info.scce.pyro.flowgraph.rest.Activity.fromEntityProperties(
					node,
					new info.scce.pyro.rest.ObjectCache()
				)
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
				info.scce.pyro.flowgraph.rest.Activity.fromEntityProperties(
					(entity.flowgraph.ActivityDB) entity.getDelegate(),
					new info.scce.pyro.rest.ObjectCache()
				)
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
				info.scce.pyro.flowgraph.rest.Transition.fromEntityProperties(
					edge,
					new info.scce.pyro.rest.ObjectCache()
				)
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
				info.scce.pyro.flowgraph.rest.Transition.fromEntityProperties(
					(entity.flowgraph.TransitionDB) entity.getDelegate(),
					new info.scce.pyro.rest.ObjectCache()
				),
				TypeRegistry.getTypeOf(entity.getSourceElement()),
				TypeRegistry.getTypeOf(entity.getTargetElement())
			);
		}
		
		public void setTransitionContainer(entity.flowgraph.TransitionDB edge, PanacheEntity container) {
			if(container instanceof entity.flowgraph.SwimlaneDB) {
				entity.flowgraph.SwimlaneDB containerDB = (entity.flowgraph.SwimlaneDB) container;
				containerDB.addModelElements(edge);
				edge.setContainer(container);
				container.persist();
			} else if(container instanceof entity.flowgraph.FlowGraphDiagramDB) {
				entity.flowgraph.FlowGraphDiagramDB containerDB = (entity.flowgraph.FlowGraphDiagramDB) container;
				containerDB.addModelElements(edge);
				edge.setContainer(container);
				container.persist();
			} else if(container instanceof entity.flowgraph.FlowGraphDiagramDB) {
				entity.flowgraph.FlowGraphDiagramDB containerDB = (entity.flowgraph.FlowGraphDiagramDB) container;
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
			} else if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
				entity.flowgraph.SubFlowGraphDB o = (entity.flowgraph.SubFlowGraphDB) ((info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			} else if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
				entity.flowgraph.ActivityDB o = (entity.flowgraph.ActivityDB) ((info.scce.cinco.product.flowgraph.flowgraph.Activity) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			} else if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity) {
				entity.flowgraph.ExternalActivityDB o = (entity.flowgraph.ExternalActivityDB) ((info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity) target).getDelegate();
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
				info.scce.pyro.flowgraph.rest.LabeledTransition.fromEntityProperties(
					edge,
					new info.scce.pyro.rest.ObjectCache()
				)
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
				info.scce.pyro.flowgraph.rest.LabeledTransition.fromEntityProperties(
					(entity.flowgraph.LabeledTransitionDB) entity.getDelegate(),
					new info.scce.pyro.rest.ObjectCache()
				),
				TypeRegistry.getTypeOf(entity.getSourceElement()),
				TypeRegistry.getTypeOf(entity.getTargetElement())
			);
		}
		
		public void setLabeledTransitionContainer(entity.flowgraph.LabeledTransitionDB edge, PanacheEntity container) {
			if(container instanceof entity.flowgraph.SwimlaneDB) {
				entity.flowgraph.SwimlaneDB containerDB = (entity.flowgraph.SwimlaneDB) container;
				containerDB.addModelElements(edge);
				edge.setContainer(container);
				container.persist();
			} else if(container instanceof entity.flowgraph.FlowGraphDiagramDB) {
				entity.flowgraph.FlowGraphDiagramDB containerDB = (entity.flowgraph.FlowGraphDiagramDB) container;
				containerDB.addModelElements(edge);
				edge.setContainer(container);
				container.persist();
			} else if(container instanceof entity.flowgraph.FlowGraphDiagramDB) {
				entity.flowgraph.FlowGraphDiagramDB containerDB = (entity.flowgraph.FlowGraphDiagramDB) container;
				containerDB.addModelElements(edge);
				edge.setContainer(container);
				container.persist();
			}
		}
		
		public void setLabeledTransitionDBSource(entity.flowgraph.LabeledTransitionDB edge, Node source) {
			if(source instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
				entity.flowgraph.SubFlowGraphDB o = (entity.flowgraph.SubFlowGraphDB) ((info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) source).getDelegate();
				edge.setSource(o);
				o.addOutgoing(edge);
				o.persist();
			} else if(source instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
				entity.flowgraph.ActivityDB o = (entity.flowgraph.ActivityDB) ((info.scce.cinco.product.flowgraph.flowgraph.Activity) source).getDelegate();
				edge.setSource(o);
				o.addOutgoing(edge);
				o.persist();
			} else if(source instanceof info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity) {
				entity.flowgraph.ExternalActivityDB o = (entity.flowgraph.ExternalActivityDB) ((info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity) source).getDelegate();
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
			} else if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
				entity.flowgraph.SubFlowGraphDB o = (entity.flowgraph.SubFlowGraphDB) ((info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			} else if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
				entity.flowgraph.ActivityDB o = (entity.flowgraph.ActivityDB) ((info.scce.cinco.product.flowgraph.flowgraph.Activity) target).getDelegate();
				edge.setTarget(o);
				o.addIncoming(edge);
				o.persist();
			} else if(target instanceof info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity) {
				entity.flowgraph.ExternalActivityDB o = (entity.flowgraph.ExternalActivityDB) ((info.scce.cinco.product.flowgraph.flowgraph.ExternalActivity) target).getDelegate();
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
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.End) {
		    	updateEndProperties((info.scce.cinco.product.flowgraph.flowgraph.End) entity,(info.scce.pyro.flowgraph.rest.End)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.Swimlane) {
		    	updateSwimlaneProperties((info.scce.cinco.product.flowgraph.flowgraph.Swimlane) entity,(info.scce.pyro.flowgraph.rest.Swimlane)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) {
		    	updateSubFlowGraphProperties((info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph) entity,(info.scce.pyro.flowgraph.rest.SubFlowGraph)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.Transition) {
		    	updateTransitionProperties((info.scce.cinco.product.flowgraph.flowgraph.Transition) entity,(info.scce.pyro.flowgraph.rest.Transition)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.Start) {
		    	updateStartProperties((info.scce.cinco.product.flowgraph.flowgraph.Start) entity,(info.scce.pyro.flowgraph.rest.Start)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.Activity) {
		    	updateActivityProperties((info.scce.cinco.product.flowgraph.flowgraph.Activity) entity,(info.scce.pyro.flowgraph.rest.Activity)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) {
		    	updateLabeledTransitionProperties((info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition) entity,(info.scce.pyro.flowgraph.rest.LabeledTransition)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram) {
		    	updateFlowGraphDiagramProperties((info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram) entity,(info.scce.pyro.flowgraph.rest.FlowGraphDiagram)prev);
		    	return;
		    }
	    }

		public void updateEndProperties(info.scce.cinco.product.flowgraph.flowgraph.End entity, info.scce.pyro.flowgraph.rest.End prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.End.fromEntityProperties(
					(entity.flowgraph.EndDB) entity.getDelegate(),
					new info.scce.pyro.rest.ObjectCache()
				),
				prev
			);
		}
		
		public void updateSwimlaneProperties(info.scce.cinco.product.flowgraph.flowgraph.Swimlane entity, info.scce.pyro.flowgraph.rest.Swimlane prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.Swimlane.fromEntityProperties(
					(entity.flowgraph.SwimlaneDB) entity.getDelegate(),
					new info.scce.pyro.rest.ObjectCache()
				),
				prev
			);
		}
		
		public void updateSubFlowGraphProperties(info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph entity, info.scce.pyro.flowgraph.rest.SubFlowGraph prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.SubFlowGraph.fromEntityProperties(
					(entity.flowgraph.SubFlowGraphDB) entity.getDelegate(),
					new info.scce.pyro.rest.ObjectCache()
				),
				prev
			);
		}
		
		public void updateTransitionProperties(info.scce.cinco.product.flowgraph.flowgraph.Transition entity, info.scce.pyro.flowgraph.rest.Transition prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.Transition.fromEntityProperties(
					(entity.flowgraph.TransitionDB) entity.getDelegate(),
					new info.scce.pyro.rest.ObjectCache()
				),
				prev
			);
		}
		
		public void updateStartProperties(info.scce.cinco.product.flowgraph.flowgraph.Start entity, info.scce.pyro.flowgraph.rest.Start prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.Start.fromEntityProperties(
					(entity.flowgraph.StartDB) entity.getDelegate(),
					new info.scce.pyro.rest.ObjectCache()
				),
				prev
			);
		}
		
		public void updateActivityProperties(info.scce.cinco.product.flowgraph.flowgraph.Activity entity, info.scce.pyro.flowgraph.rest.Activity prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.Activity.fromEntityProperties(
					(entity.flowgraph.ActivityDB) entity.getDelegate(),
					new info.scce.pyro.rest.ObjectCache()
				),
				prev
			);
		}
		
		public void updateLabeledTransitionProperties(info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition entity, info.scce.pyro.flowgraph.rest.LabeledTransition prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.LabeledTransition.fromEntityProperties(
					(entity.flowgraph.LabeledTransitionDB) entity.getDelegate(),
					new info.scce.pyro.rest.ObjectCache()
				),
				prev
			);
		}
		
		public void updateFlowGraphDiagramProperties(info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram entity, info.scce.pyro.flowgraph.rest.FlowGraphDiagram prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.flowgraph.rest.FlowGraphDiagram.fromEntityProperties(
					(entity.flowgraph.FlowGraphDiagramDB) entity.getDelegate(),
					new info.scce.pyro.rest.ObjectCache()
				),
				prev
			);
		}
		
		//FOR NODE EDGE GRAPHMODEL TYPE
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
			info.scce.pyro.flowgraph.rest.End prev = info.scce.pyro.flowgraph.rest.End.fromEntityProperties(
				dbEntity,
				new info.scce.pyro.rest.ObjectCache()
			);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(
					TypeRegistry.getTypeOf(dbEntity),
					info.scce.pyro.flowgraph.rest.End.fromEntityProperties(
						dbEntity,
						new info.scce.pyro.rest.ObjectCache()
					),
					prev
				);
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
			info.scce.pyro.flowgraph.rest.Swimlane prev = info.scce.pyro.flowgraph.rest.Swimlane.fromEntityProperties(
				dbEntity,
				new info.scce.pyro.rest.ObjectCache()
			);
			
		
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
				super.updateProperties(
					TypeRegistry.getTypeOf(dbEntity),
					info.scce.pyro.flowgraph.rest.Swimlane.fromEntityProperties(
						dbEntity,
						new info.scce.pyro.rest.ObjectCache()
					),
					prev
				);
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
			info.scce.pyro.flowgraph.rest.SubFlowGraph prev = info.scce.pyro.flowgraph.rest.SubFlowGraph.fromEntityProperties(
				dbEntity,
				new info.scce.pyro.rest.ObjectCache()
			);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(
					TypeRegistry.getTypeOf(dbEntity),
					info.scce.pyro.flowgraph.rest.SubFlowGraph.fromEntityProperties(
						dbEntity,
						new info.scce.pyro.rest.ObjectCache()
					),
					prev
				);
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
			info.scce.pyro.flowgraph.rest.Transition prev = info.scce.pyro.flowgraph.rest.Transition.fromEntityProperties(
				dbEntity,
				new info.scce.pyro.rest.ObjectCache()
			);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(
					TypeRegistry.getTypeOf(dbEntity),
					info.scce.pyro.flowgraph.rest.Transition.fromEntityProperties(
						dbEntity,
						new info.scce.pyro.rest.ObjectCache()
					),
					prev
				);
			}
			return apiEntity;
		}
		
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
			info.scce.pyro.flowgraph.rest.Start prev = info.scce.pyro.flowgraph.rest.Start.fromEntityProperties(
				dbEntity,
				new info.scce.pyro.rest.ObjectCache()
			);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(
					TypeRegistry.getTypeOf(dbEntity),
					info.scce.pyro.flowgraph.rest.Start.fromEntityProperties(
						dbEntity,
						new info.scce.pyro.rest.ObjectCache()
					),
					prev
				);
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
			info.scce.pyro.flowgraph.rest.Activity prev = info.scce.pyro.flowgraph.rest.Activity.fromEntityProperties(
				dbEntity,
				new info.scce.pyro.rest.ObjectCache()
			);
			
		
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
				super.updateProperties(
					TypeRegistry.getTypeOf(dbEntity),
					info.scce.pyro.flowgraph.rest.Activity.fromEntityProperties(
						dbEntity,
						new info.scce.pyro.rest.ObjectCache()
					),
					prev
				);
			}
			return apiEntity;
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
			info.scce.pyro.flowgraph.rest.LabeledTransition prev = info.scce.pyro.flowgraph.rest.LabeledTransition.fromEntityProperties(
				dbEntity,
				new info.scce.pyro.rest.ObjectCache()
			);
			
		
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
				super.updateProperties(
					TypeRegistry.getTypeOf(dbEntity),
					info.scce.pyro.flowgraph.rest.LabeledTransition.fromEntityProperties(
						dbEntity,
						new info.scce.pyro.rest.ObjectCache()
					),
					prev
				);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram updateFlowGraphDiagram(info.scce.pyro.flowgraph.rest.FlowGraphDiagram update){
			entity.flowgraph.FlowGraphDiagramDB dbEntity = entity.flowgraph.FlowGraphDiagramDB.findById(update.getId());
			info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram apiEntity = (info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateFlowGraphDiagram(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram updateFlowGraphDiagram(info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram apiEntity, info.scce.pyro.flowgraph.rest.FlowGraphDiagram update){
			// handle type
			return updateFlowGraphDiagram(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram updateFlowGraphDiagram(info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram apiEntity, info.scce.pyro.flowgraph.rest.FlowGraphDiagram update, boolean propagate){
			// handle type
			entity.flowgraph.FlowGraphDiagramDB dbEntity = (entity.flowgraph.FlowGraphDiagramDB) apiEntity.getDelegate();
			info.scce.pyro.flowgraph.rest.FlowGraphDiagram prev = info.scce.pyro.flowgraph.rest.FlowGraphDiagram.fromEntityProperties(
				dbEntity,
				new info.scce.pyro.rest.ObjectCache()
			);
			
		
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
				super.updateProperties(
					TypeRegistry.getTypeOf(dbEntity),
					info.scce.pyro.flowgraph.rest.FlowGraphDiagram.fromEntityProperties(
						dbEntity,
						new info.scce.pyro.rest.ObjectCache()
					),
					prev
				);
			}
			return apiEntity;
		}
		
		@Override
		public void updateAppearance() {
			super.getAllModelElements().forEach((element)->{
			});
		}

		public FlowGraphDiagramCommandExecuter getFlowGraphDiagramCommandExecuter() {
			return this;
		}
		
		public <T> boolean isDifferent(java.util.Collection<T> a, java.util.Collection<T> b) {
			java.util.Set<?> aH = a.stream().collect(java.util.stream.Collectors.toSet());
			java.util.Set<?> bH = b.stream().collect(java.util.stream.Collectors.toSet());
			return !aH.equals(bH) || a.size() != b.size();
		}
	}
