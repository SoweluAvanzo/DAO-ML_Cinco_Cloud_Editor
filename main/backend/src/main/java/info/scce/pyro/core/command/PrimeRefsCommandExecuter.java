	package info.scce.pyro.core.command;
	
	import info.scce.cinco.product.primerefs.primerefs.util.TypeRegistry;
	import info.scce.pyro.core.graphmodel.BendingPoint;
	import graphmodel.*;
	import entity.core.PyroUserDB;
	import info.scce.pyro.sync.GraphModelWebSocket;
	import io.quarkus.hibernate.orm.panache.PanacheEntity;
	
	/**
	 * Author zweihoff
	 */
	public class PrimeRefsCommandExecuter extends CommandExecuter {
		
		private info.scce.pyro.rest.ObjectCache objectCache;
		private GraphModelWebSocket graphModelWebSocket;
		
		public PrimeRefsCommandExecuter(
			PyroUserDB user,
			info.scce.pyro.rest.ObjectCache objectCache,
			GraphModelWebSocket graphModelWebSocket,
			entity.primerefs.PrimeRefsDB graph,
			java.util.List<info.scce.pyro.core.command.types.HighlightCommand> highlightings
		) {
			super(
				graphModelWebSocket,
				highlightings
			);
			this.objectCache = objectCache;
			super.batch = new BatchExecution(user,new info.scce.cinco.product.primerefs.primerefs.impl.PrimeRefsImpl(graph,this));
		}
		
		/**
		 * NOTE: Use this if it is needed to utilize (/work on) the same batch of commands
		 * of the GraphModelCommandExecuter and on the one of a primeReferenced GraphModel
		 */
		public PrimeRefsCommandExecuter(
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
		
		public void removePrimeRefs(info.scce.cinco.product.primerefs.primerefs.PrimeRefs entity){
			//for complex props
			entity.delete();
			/*
			*/
		}
		
		public info.scce.cinco.product.primerefs.primerefs.SourceNode createSourceNode(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.SourceNode prev){
			entity.primerefs.SourceNodeDB node = new entity.primerefs.SourceNodeDB();
			node.width = width;
			node.height = height;
			node.x = x;
			node.y = y;
			
			//primitive init
			
			// setting container
			PanacheEntity dbMec = TypeRegistry.getApiToDB(mec);
			node.setContainer(dbMec);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.SourceNode apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.SourceNodeImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
				info.scce.pyro.primerefs.rest.SourceNode.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updateSourceNode(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.SourceNode createSourceNode(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.SourceNode prev) {
			return createSourceNode(
				x,
				y,
				36,
				36,
				mec,
				prev);
		}
		
		public void removeSourceNode(
			info.scce.cinco.product.primerefs.primerefs.SourceNode entity
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				null,
				info.scce.pyro.primerefs.rest.SourceNode.fromEntityProperties((entity.primerefs.SourceNodeDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.SourceContainer createSourceContainer(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.SourceContainer prev){
			entity.primerefs.SourceContainerDB node = new entity.primerefs.SourceContainerDB();
			node.width = width;
			node.height = height;
			node.x = x;
			node.y = y;
			
			//primitive init
			
			// setting container
			PanacheEntity dbMec = TypeRegistry.getApiToDB(mec);
			node.setContainer(dbMec);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.SourceContainer apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.SourceContainerImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
				info.scce.pyro.primerefs.rest.SourceContainer.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updateSourceContainer(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.SourceContainer createSourceContainer(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.SourceContainer prev) {
			return createSourceContainer(
				x,
				y,
				36,
				36,
				mec,
				prev);
		}
		
		public void removeSourceContainer(
			info.scce.cinco.product.primerefs.primerefs.SourceContainer entity
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				null,
				info.scce.pyro.primerefs.rest.SourceContainer.fromEntityProperties((entity.primerefs.SourceContainerDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeToNode createPrimeToNode(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToNode prev,long primeId){
			entity.primerefs.PrimeToNodeDB node = new entity.primerefs.PrimeToNodeDB();
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
				prime = entity.primerefs.SourceNodeDB.findById(primeId);
			}
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeToNode apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToNodeImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.primerefs.rest.SourceNode.fromEntityProperties(prime,null),
				info.scce.pyro.primerefs.rest.PrimeToNode.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeToNode(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToNode createPrimeToNode(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToNode prev,long primeId) {
			return createPrimeToNode(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeToNode(
			info.scce.cinco.product.primerefs.primerefs.PrimeToNode entity,
			info.scce.cinco.product.primerefs.primerefs.SourceNode prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeToNode.fromEntityProperties((entity.primerefs.PrimeToNodeDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeToEdge createPrimeToEdge(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToEdge prev,long primeId){
			entity.primerefs.PrimeToEdgeDB node = new entity.primerefs.PrimeToEdgeDB();
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
				prime = entity.primerefs.SourceEdgeDB.findById(primeId);
			}
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeToEdge apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToEdgeImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.primerefs.rest.SourceEdge.fromEntityProperties(prime,null),
				info.scce.pyro.primerefs.rest.PrimeToEdge.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeToEdge(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToEdge createPrimeToEdge(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToEdge prev,long primeId) {
			return createPrimeToEdge(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeToEdge(
			info.scce.cinco.product.primerefs.primerefs.PrimeToEdge entity,
			info.scce.cinco.product.primerefs.primerefs.SourceEdge prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeToEdge.fromEntityProperties((entity.primerefs.PrimeToEdgeDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeToContainer createPrimeToContainer(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToContainer prev,long primeId){
			entity.primerefs.PrimeToContainerDB node = new entity.primerefs.PrimeToContainerDB();
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
				prime = entity.primerefs.SourceContainerDB.findById(primeId);
			}
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeToContainer apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToContainerImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.primerefs.rest.SourceContainer.fromEntityProperties(prime,null),
				info.scce.pyro.primerefs.rest.PrimeToContainer.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeToContainer(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToContainer createPrimeToContainer(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToContainer prev,long primeId) {
			return createPrimeToContainer(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeToContainer(
			info.scce.cinco.product.primerefs.primerefs.PrimeToContainer entity,
			info.scce.cinco.product.primerefs.primerefs.SourceContainer prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeToContainer.fromEntityProperties((entity.primerefs.PrimeToContainerDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel createPrimeToGraphModel(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToGraphModel prev,long primeId){
			entity.primerefs.PrimeToGraphModelDB node = new entity.primerefs.PrimeToGraphModelDB();
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
				prime = entity.primerefs.PrimeRefsDB.findById(primeId);
			}
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToGraphModelImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.primerefs.rest.PrimeRefs.fromEntityProperties(prime,null),
				info.scce.pyro.primerefs.rest.PrimeToGraphModel.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeToGraphModel(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel createPrimeToGraphModel(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToGraphModel prev,long primeId) {
			return createPrimeToGraphModel(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeToGraphModel(
			info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel entity,
			info.scce.cinco.product.primerefs.primerefs.PrimeRefs prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeToGraphModel.fromEntityProperties((entity.primerefs.PrimeToGraphModelDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToNode createPrimeCToNode(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeCToNode prev,long primeId){
			entity.primerefs.PrimeCToNodeDB node = new entity.primerefs.PrimeCToNodeDB();
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
				prime = entity.primerefs.SourceNodeDB.findById(primeId);
			}
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeCToNode apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToNodeImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.primerefs.rest.SourceNode.fromEntityProperties(prime,null),
				info.scce.pyro.primerefs.rest.PrimeCToNode.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeCToNode(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToNode createPrimeCToNode(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeCToNode prev,long primeId) {
			return createPrimeCToNode(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeCToNode(
			info.scce.cinco.product.primerefs.primerefs.PrimeCToNode entity,
			info.scce.cinco.product.primerefs.primerefs.SourceNode prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeCToNode.fromEntityProperties((entity.primerefs.PrimeCToNodeDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge createPrimeCToEdge(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeCToEdge prev,long primeId){
			entity.primerefs.PrimeCToEdgeDB node = new entity.primerefs.PrimeCToEdgeDB();
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
				prime = entity.primerefs.SourceEdgeDB.findById(primeId);
			}
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToEdgeImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.primerefs.rest.SourceEdge.fromEntityProperties(prime,null),
				info.scce.pyro.primerefs.rest.PrimeCToEdge.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeCToEdge(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge createPrimeCToEdge(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeCToEdge prev,long primeId) {
			return createPrimeCToEdge(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeCToEdge(
			info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge entity,
			info.scce.cinco.product.primerefs.primerefs.SourceEdge prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeCToEdge.fromEntityProperties((entity.primerefs.PrimeCToEdgeDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer createPrimeCToContainer(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeCToContainer prev,long primeId){
			entity.primerefs.PrimeCToContainerDB node = new entity.primerefs.PrimeCToContainerDB();
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
				prime = entity.primerefs.SourceContainerDB.findById(primeId);
			}
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToContainerImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.primerefs.rest.SourceContainer.fromEntityProperties(prime,null),
				info.scce.pyro.primerefs.rest.PrimeCToContainer.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeCToContainer(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer createPrimeCToContainer(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeCToContainer prev,long primeId) {
			return createPrimeCToContainer(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeCToContainer(
			info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer entity,
			info.scce.cinco.product.primerefs.primerefs.SourceContainer prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeCToContainer.fromEntityProperties((entity.primerefs.PrimeCToContainerDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel createPrimeCToGraphModel(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeCToGraphModel prev,long primeId){
			entity.primerefs.PrimeCToGraphModelDB node = new entity.primerefs.PrimeCToGraphModelDB();
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
				prime = entity.primerefs.PrimeRefsDB.findById(primeId);
			}
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToGraphModelImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.primerefs.rest.PrimeRefs.fromEntityProperties(prime,null),
				info.scce.pyro.primerefs.rest.PrimeCToGraphModel.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeCToGraphModel(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel createPrimeCToGraphModel(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeCToGraphModel prev,long primeId) {
			return createPrimeCToGraphModel(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeCToGraphModel(
			info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel entity,
			info.scce.cinco.product.primerefs.primerefs.PrimeRefs prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeCToGraphModel.fromEntityProperties((entity.primerefs.PrimeCToGraphModelDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy createPrimeToNodeHierarchy(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy prev,long primeId){
			entity.primerefs.PrimeToNodeHierarchyDB node = new entity.primerefs.PrimeToNodeHierarchyDB();
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
				prime = entity.hierarchy.DDB.findById(primeId);
			}
			if(prime == null) {
				prime = entity.hierarchy.ADB.findById(primeId);
			}
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToNodeHierarchyImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.hierarchy.rest.D.fromEntityProperties(prime,null),
				info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeToNodeHierarchy(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy createPrimeToNodeHierarchy(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy prev,long primeId) {
			return createPrimeToNodeHierarchy(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeToNodeHierarchy(
			info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy entity,
			info.scce.cinco.product.hierarchy.hierarchy.D prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy.fromEntityProperties((entity.primerefs.PrimeToNodeHierarchyDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy createPrimeToAbstractNodeHierarchy(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy prev,long primeId){
			entity.primerefs.PrimeToAbstractNodeHierarchyDB node = new entity.primerefs.PrimeToAbstractNodeHierarchyDB();
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
				prime = entity.hierarchy.ADB.findById(primeId);
			}
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToAbstractNodeHierarchyImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.hierarchy.rest.C.fromEntityProperties(prime,null),
				info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeToAbstractNodeHierarchy(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy createPrimeToAbstractNodeHierarchy(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy prev,long primeId) {
			return createPrimeToAbstractNodeHierarchy(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeToAbstractNodeHierarchy(
			info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy entity,
			info.scce.cinco.product.hierarchy.hierarchy.C prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy.fromEntityProperties((entity.primerefs.PrimeToAbstractNodeHierarchyDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow createPrimeToNodeFlow(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToNodeFlow prev,long primeId){
			entity.primerefs.PrimeToNodeFlowDB node = new entity.primerefs.PrimeToNodeFlowDB();
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
				prime = entity.flowgraph.ActivityDB.findById(primeId);
			}
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToNodeFlowImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.flowgraph.rest.Activity.fromEntityProperties(prime,null),
				info.scce.pyro.primerefs.rest.PrimeToNodeFlow.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeToNodeFlow(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow createPrimeToNodeFlow(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToNodeFlow prev,long primeId) {
			return createPrimeToNodeFlow(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeToNodeFlow(
			info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow entity,
			info.scce.cinco.product.flowgraph.flowgraph.Activity prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeToNodeFlow.fromEntityProperties((entity.primerefs.PrimeToNodeFlowDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow createPrimeToEdgeFlow(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToEdgeFlow prev,long primeId){
			entity.primerefs.PrimeToEdgeFlowDB node = new entity.primerefs.PrimeToEdgeFlowDB();
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
				prime = entity.flowgraph.LabeledTransitionDB.findById(primeId);
			}
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToEdgeFlowImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.flowgraph.rest.LabeledTransition.fromEntityProperties(prime,null),
				info.scce.pyro.primerefs.rest.PrimeToEdgeFlow.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeToEdgeFlow(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow createPrimeToEdgeFlow(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToEdgeFlow prev,long primeId) {
			return createPrimeToEdgeFlow(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeToEdgeFlow(
			info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow entity,
			info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeToEdgeFlow.fromEntityProperties((entity.primerefs.PrimeToEdgeFlowDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow createPrimeToContainerFlow(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToContainerFlow prev,long primeId){
			entity.primerefs.PrimeToContainerFlowDB node = new entity.primerefs.PrimeToContainerFlowDB();
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
				prime = entity.flowgraph.SwimlaneDB.findById(primeId);
			}
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToContainerFlowImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.flowgraph.rest.Swimlane.fromEntityProperties(prime,null),
				info.scce.pyro.primerefs.rest.PrimeToContainerFlow.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeToContainerFlow(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow createPrimeToContainerFlow(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToContainerFlow prev,long primeId) {
			return createPrimeToContainerFlow(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeToContainerFlow(
			info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow entity,
			info.scce.cinco.product.flowgraph.flowgraph.Swimlane prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeToContainerFlow.fromEntityProperties((entity.primerefs.PrimeToContainerFlowDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow createPrimeToGraphModelFlow(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow prev,long primeId){
			entity.primerefs.PrimeToGraphModelFlowDB node = new entity.primerefs.PrimeToGraphModelFlowDB();
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
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeToGraphModelFlowImpl(node,this);
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
				info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeToGraphModelFlow(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow createPrimeToGraphModelFlow(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow prev,long primeId) {
			return createPrimeToGraphModelFlow(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeToGraphModelFlow(
			info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow entity,
			info.scce.cinco.product.flowgraph.flowgraph.FlowGraph prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow.fromEntityProperties((entity.primerefs.PrimeToGraphModelFlowDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow createPrimeCToNodeFlow(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeCToNodeFlow prev,long primeId){
			entity.primerefs.PrimeCToNodeFlowDB node = new entity.primerefs.PrimeCToNodeFlowDB();
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
				prime = entity.flowgraph.ActivityDB.findById(primeId);
			}
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToNodeFlowImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.flowgraph.rest.Activity.fromEntityProperties(prime,null),
				info.scce.pyro.primerefs.rest.PrimeCToNodeFlow.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeCToNodeFlow(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow createPrimeCToNodeFlow(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeCToNodeFlow prev,long primeId) {
			return createPrimeCToNodeFlow(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeCToNodeFlow(
			info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow entity,
			info.scce.cinco.product.flowgraph.flowgraph.Activity prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeCToNodeFlow.fromEntityProperties((entity.primerefs.PrimeCToNodeFlowDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow createPrimeCToEdgeFlow(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow prev,long primeId){
			entity.primerefs.PrimeCToEdgeFlowDB node = new entity.primerefs.PrimeCToEdgeFlowDB();
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
				prime = entity.flowgraph.LabeledTransitionDB.findById(primeId);
			}
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToEdgeFlowImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.flowgraph.rest.LabeledTransition.fromEntityProperties(prime,null),
				info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeCToEdgeFlow(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow createPrimeCToEdgeFlow(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow prev,long primeId) {
			return createPrimeCToEdgeFlow(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeCToEdgeFlow(
			info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow entity,
			info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow.fromEntityProperties((entity.primerefs.PrimeCToEdgeFlowDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow createPrimeCToContainerFlow(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeCToContainerFlow prev,long primeId){
			entity.primerefs.PrimeCToContainerFlowDB node = new entity.primerefs.PrimeCToContainerFlowDB();
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
				prime = entity.flowgraph.SwimlaneDB.findById(primeId);
			}
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToContainerFlowImpl(node,this);
		    super.createNode(
		    	TypeRegistry.getTypeOf(apiNode),
		    	apiNode,mec,
		    	TypeRegistry.
		    	getTypeOf(mec),
		    	x,
		    	y,
		    	width,
		    	height,
		    	info.scce.pyro.flowgraph.rest.Swimlane.fromEntityProperties(prime,null),
				info.scce.pyro.primerefs.rest.PrimeCToContainerFlow.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeCToContainerFlow(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow createPrimeCToContainerFlow(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeCToContainerFlow prev,long primeId) {
			return createPrimeCToContainerFlow(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeCToContainerFlow(
			info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow entity,
			info.scce.cinco.product.flowgraph.flowgraph.Swimlane prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeCToContainerFlow.fromEntityProperties((entity.primerefs.PrimeCToContainerFlowDB) entity.getDelegate(),null)
			);
		}
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow createPrimeCToGraphModelFlow(long x, long y, long width, long height, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow prev,long primeId){
			entity.primerefs.PrimeCToGraphModelFlowDB node = new entity.primerefs.PrimeCToGraphModelFlowDB();
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
			node.setPr(prime);
			
		    node.persist();
		    
		    info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow apiNode = new info.scce.cinco.product.primerefs.primerefs.impl.PrimeCToGraphModelFlowImpl(node,this);
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
				info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow.fromEntityProperties(node,null)
			);
			if(prev != null) {
		    	//create from copy
		    	this.updatePrimeCToGraphModelFlow(apiNode,prev,true);
		    }
		    
			return apiNode;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow createPrimeCToGraphModelFlow(long x, long y, ModelElementContainer mec, info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow prev,long primeId) {
			return createPrimeCToGraphModelFlow(
				x,
				y,
				96,
				32,
				mec,
				prev,
				primeId
			);
		}
		
		public void removePrimeCToGraphModelFlow(
			info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow entity,
			info.scce.cinco.product.flowgraph.flowgraph.FlowGraph prime
		){
			super.removeNode(
				TypeRegistry.getTypeOf(entity),
				entity,
				TypeRegistry.getTypeOf(entity.getContainer()),
				TypeRegistry.getApiToRest(prime),
				info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow.fromEntityProperties((entity.primerefs.PrimeCToGraphModelFlowDB) entity.getDelegate(),null)
			);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.SourceEdge createSourceEdge(Node source, Node target, java.util.List<BendingPoint> positions, info.scce.pyro.primerefs.rest.SourceEdge prev){
			entity.primerefs.SourceEdgeDB edge = new entity.primerefs.SourceEdgeDB();
			//primitive init
			
			setEdgeDBComponents(edge, source, target, positions);
			edge.persist();
		
			info.scce.cinco.product.primerefs.primerefs.SourceEdge apiEdge = new info.scce.cinco.product.primerefs.primerefs.impl.SourceEdgeImpl(edge,this);
			super.createEdge(
				TypeRegistry.getTypeOf(apiEdge),
				apiEdge,
				source,
				TypeRegistry.getTypeOf(source),
				target,
				TypeRegistry.getTypeOf(target),
				edge.bendingPoints,
				info.scce.pyro.primerefs.rest.SourceEdge.fromEntityProperties(edge,null)
			);
			if(prev != null) {
				//create from copy
				this.updateSourceEdge(apiEdge,prev,true);
			}
			
			
			return apiEdge;
		}
		
		public void addBendpointSourceEdge(info.scce.cinco.product.primerefs.primerefs.SourceEdge edge, long x,long y){
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
		
		public void updateSourceEdge(info.scce.cinco.product.primerefs.primerefs.SourceEdge edge, java.util.List<BendingPoint> points){
			super.updateBendingPoints(TypeRegistry.getTypeOf(edge),edge,points);
		}
		
		public void removeSourceEdge(info.scce.cinco.product.primerefs.primerefs.SourceEdge entity){
			super.removeEdge(
				TypeRegistry.getTypeOf(entity),
				entity,
				info.scce.pyro.primerefs.rest.SourceEdge.fromEntityProperties((entity.primerefs.SourceEdgeDB) entity.getDelegate(),null),
				TypeRegistry.getTypeOf(entity.getSourceElement()),
				TypeRegistry.getTypeOf(entity.getTargetElement())
			);
		}
		
		public void setSourceEdgeContainer(entity.primerefs.SourceEdgeDB edge, PanacheEntity container) {
			if(container instanceof entity.primerefs.PrimeRefsDB) {
				entity.primerefs.PrimeRefsDB containerDB = (entity.primerefs.PrimeRefsDB) container;
				containerDB.addModelElements(edge);
				edge.setContainer(container);
				container.persist();
			} else if(container instanceof entity.primerefs.PrimeRefsDB) {
				entity.primerefs.PrimeRefsDB containerDB = (entity.primerefs.PrimeRefsDB) container;
				containerDB.addModelElements(edge);
				edge.setContainer(container);
				container.persist();
			}
		}
		
		public void setSourceEdgeDBSource(entity.primerefs.SourceEdgeDB edge, Node source) {
		}
		
		public void setSourceEdgeDBTarget(entity.primerefs.SourceEdgeDB edge, Node target) {
		}

	    public void setEdgeDBComponents(PanacheEntity edge, Node source, Node target, java.util.List<BendingPoint> bendingPoints) {
	    	graphmodel.GraphModel graphModel = source.getRootElement();
	    	PanacheEntity e = TypeRegistry.getApiToDB(graphModel);
	    	
	    	// switch edge types
	    	if(edge instanceof entity.primerefs.SourceEdgeDB) {
	    		entity.primerefs.SourceEdgeDB edgeDB = (entity.primerefs.SourceEdgeDB) edge;
	    		setSourceEdgeDBSource(edgeDB, source);
	    		setSourceEdgeDBTarget(edgeDB, target);
	    		setSourceEdgeContainer(edgeDB, e);
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
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.SourceNode) {
		    	updateSourceNodeProperties((info.scce.cinco.product.primerefs.primerefs.SourceNode) entity,(info.scce.pyro.primerefs.rest.SourceNode)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.SourceContainer) {
		    	updateSourceContainerProperties((info.scce.cinco.product.primerefs.primerefs.SourceContainer) entity,(info.scce.pyro.primerefs.rest.SourceContainer)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNode) {
		    	updatePrimeToNodeProperties((info.scce.cinco.product.primerefs.primerefs.PrimeToNode) entity,(info.scce.pyro.primerefs.rest.PrimeToNode)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToEdge) {
		    	updatePrimeToEdgeProperties((info.scce.cinco.product.primerefs.primerefs.PrimeToEdge) entity,(info.scce.pyro.primerefs.rest.PrimeToEdge)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToContainer) {
		    	updatePrimeToContainerProperties((info.scce.cinco.product.primerefs.primerefs.PrimeToContainer) entity,(info.scce.pyro.primerefs.rest.PrimeToContainer)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel) {
		    	updatePrimeToGraphModelProperties((info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel) entity,(info.scce.pyro.primerefs.rest.PrimeToGraphModel)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToNode) {
		    	updatePrimeCToNodeProperties((info.scce.cinco.product.primerefs.primerefs.PrimeCToNode) entity,(info.scce.pyro.primerefs.rest.PrimeCToNode)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge) {
		    	updatePrimeCToEdgeProperties((info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge) entity,(info.scce.pyro.primerefs.rest.PrimeCToEdge)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer) {
		    	updatePrimeCToContainerProperties((info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer) entity,(info.scce.pyro.primerefs.rest.PrimeCToContainer)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel) {
		    	updatePrimeCToGraphModelProperties((info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel) entity,(info.scce.pyro.primerefs.rest.PrimeCToGraphModel)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy) {
		    	updatePrimeToNodeHierarchyProperties((info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy) entity,(info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy) {
		    	updatePrimeToAbstractNodeHierarchyProperties((info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy) entity,(info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow) {
		    	updatePrimeToNodeFlowProperties((info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow) entity,(info.scce.pyro.primerefs.rest.PrimeToNodeFlow)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow) {
		    	updatePrimeToEdgeFlowProperties((info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow) entity,(info.scce.pyro.primerefs.rest.PrimeToEdgeFlow)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow) {
		    	updatePrimeToContainerFlowProperties((info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow) entity,(info.scce.pyro.primerefs.rest.PrimeToContainerFlow)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow) {
		    	updatePrimeToGraphModelFlowProperties((info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow) entity,(info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow) {
		    	updatePrimeCToNodeFlowProperties((info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow) entity,(info.scce.pyro.primerefs.rest.PrimeCToNodeFlow)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow) {
		    	updatePrimeCToEdgeFlowProperties((info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow) entity,(info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow) {
		    	updatePrimeCToContainerFlowProperties((info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow) entity,(info.scce.pyro.primerefs.rest.PrimeCToContainerFlow)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow) {
		    	updatePrimeCToGraphModelFlowProperties((info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow) entity,(info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.SourceEdge) {
		    	updateSourceEdgeProperties((info.scce.cinco.product.primerefs.primerefs.SourceEdge) entity,(info.scce.pyro.primerefs.rest.SourceEdge)prev);
		    	return;
		    }
		    if(entity instanceof info.scce.cinco.product.primerefs.primerefs.PrimeRefs) {
		    	updatePrimeRefsProperties((info.scce.cinco.product.primerefs.primerefs.PrimeRefs) entity,(info.scce.pyro.primerefs.rest.PrimeRefs)prev);
		    	return;
		    }
	    }

		public void updateSourceNodeProperties(info.scce.cinco.product.primerefs.primerefs.SourceNode entity, info.scce.pyro.primerefs.rest.SourceNode prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.SourceNode.fromEntityProperties(
					(entity.primerefs.SourceNodeDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateSourceContainerProperties(info.scce.cinco.product.primerefs.primerefs.SourceContainer entity, info.scce.pyro.primerefs.rest.SourceContainer prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.SourceContainer.fromEntityProperties(
					(entity.primerefs.SourceContainerDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeToNodeProperties(info.scce.cinco.product.primerefs.primerefs.PrimeToNode entity, info.scce.pyro.primerefs.rest.PrimeToNode prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeToNode.fromEntityProperties(
					(entity.primerefs.PrimeToNodeDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeToEdgeProperties(info.scce.cinco.product.primerefs.primerefs.PrimeToEdge entity, info.scce.pyro.primerefs.rest.PrimeToEdge prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeToEdge.fromEntityProperties(
					(entity.primerefs.PrimeToEdgeDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeToContainerProperties(info.scce.cinco.product.primerefs.primerefs.PrimeToContainer entity, info.scce.pyro.primerefs.rest.PrimeToContainer prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeToContainer.fromEntityProperties(
					(entity.primerefs.PrimeToContainerDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeToGraphModelProperties(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel entity, info.scce.pyro.primerefs.rest.PrimeToGraphModel prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeToGraphModel.fromEntityProperties(
					(entity.primerefs.PrimeToGraphModelDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeCToNodeProperties(info.scce.cinco.product.primerefs.primerefs.PrimeCToNode entity, info.scce.pyro.primerefs.rest.PrimeCToNode prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeCToNode.fromEntityProperties(
					(entity.primerefs.PrimeCToNodeDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeCToEdgeProperties(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge entity, info.scce.pyro.primerefs.rest.PrimeCToEdge prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeCToEdge.fromEntityProperties(
					(entity.primerefs.PrimeCToEdgeDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeCToContainerProperties(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer entity, info.scce.pyro.primerefs.rest.PrimeCToContainer prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeCToContainer.fromEntityProperties(
					(entity.primerefs.PrimeCToContainerDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeCToGraphModelProperties(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel entity, info.scce.pyro.primerefs.rest.PrimeCToGraphModel prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeCToGraphModel.fromEntityProperties(
					(entity.primerefs.PrimeCToGraphModelDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeToNodeHierarchyProperties(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy entity, info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy.fromEntityProperties(
					(entity.primerefs.PrimeToNodeHierarchyDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeToAbstractNodeHierarchyProperties(info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy entity, info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy.fromEntityProperties(
					(entity.primerefs.PrimeToAbstractNodeHierarchyDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeToNodeFlowProperties(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow entity, info.scce.pyro.primerefs.rest.PrimeToNodeFlow prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeToNodeFlow.fromEntityProperties(
					(entity.primerefs.PrimeToNodeFlowDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeToEdgeFlowProperties(info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow entity, info.scce.pyro.primerefs.rest.PrimeToEdgeFlow prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeToEdgeFlow.fromEntityProperties(
					(entity.primerefs.PrimeToEdgeFlowDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeToContainerFlowProperties(info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow entity, info.scce.pyro.primerefs.rest.PrimeToContainerFlow prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeToContainerFlow.fromEntityProperties(
					(entity.primerefs.PrimeToContainerFlowDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeToGraphModelFlowProperties(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow entity, info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow.fromEntityProperties(
					(entity.primerefs.PrimeToGraphModelFlowDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeCToNodeFlowProperties(info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow entity, info.scce.pyro.primerefs.rest.PrimeCToNodeFlow prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeCToNodeFlow.fromEntityProperties(
					(entity.primerefs.PrimeCToNodeFlowDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeCToEdgeFlowProperties(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow entity, info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow.fromEntityProperties(
					(entity.primerefs.PrimeCToEdgeFlowDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeCToContainerFlowProperties(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow entity, info.scce.pyro.primerefs.rest.PrimeCToContainerFlow prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeCToContainerFlow.fromEntityProperties(
					(entity.primerefs.PrimeCToContainerFlowDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeCToGraphModelFlowProperties(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow entity, info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow.fromEntityProperties(
					(entity.primerefs.PrimeCToGraphModelFlowDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updateSourceEdgeProperties(info.scce.cinco.product.primerefs.primerefs.SourceEdge entity, info.scce.pyro.primerefs.rest.SourceEdge prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.SourceEdge.fromEntityProperties(
					(entity.primerefs.SourceEdgeDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		public void updatePrimeRefsProperties(info.scce.cinco.product.primerefs.primerefs.PrimeRefs entity, info.scce.pyro.primerefs.rest.PrimeRefs prev) {
			super.updatePropertiesReNew(
				TypeRegistry.getTypeOf(entity),
				info.scce.pyro.primerefs.rest.PrimeRefs.fromEntityProperties(
					(entity.primerefs.PrimeRefsDB) entity.getDelegate(),
					null
				),
				prev
			);
		}
		
		//FOR NODE EDGE GRAPHMODEL TYPE
		public info.scce.cinco.product.primerefs.primerefs.SourceNode updateSourceNode(info.scce.pyro.primerefs.rest.SourceNode update){
			entity.primerefs.SourceNodeDB dbEntity = entity.primerefs.SourceNodeDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.SourceNode apiEntity = (info.scce.cinco.product.primerefs.primerefs.SourceNode) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateSourceNode(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.SourceNode updateSourceNode(info.scce.cinco.product.primerefs.primerefs.SourceNode apiEntity, info.scce.pyro.primerefs.rest.SourceNode update){
			// handle type
			return updateSourceNode(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.SourceNode updateSourceNode(info.scce.cinco.product.primerefs.primerefs.SourceNode apiEntity, info.scce.pyro.primerefs.rest.SourceNode update, boolean propagate){
			// handle type
			entity.primerefs.SourceNodeDB dbEntity = (entity.primerefs.SourceNodeDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.SourceNode prev = info.scce.pyro.primerefs.rest.SourceNode.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.SourceNode.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.SourceContainer updateSourceContainer(info.scce.pyro.primerefs.rest.SourceContainer update){
			entity.primerefs.SourceContainerDB dbEntity = entity.primerefs.SourceContainerDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.SourceContainer apiEntity = (info.scce.cinco.product.primerefs.primerefs.SourceContainer) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateSourceContainer(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.SourceContainer updateSourceContainer(info.scce.cinco.product.primerefs.primerefs.SourceContainer apiEntity, info.scce.pyro.primerefs.rest.SourceContainer update){
			// handle type
			return updateSourceContainer(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.SourceContainer updateSourceContainer(info.scce.cinco.product.primerefs.primerefs.SourceContainer apiEntity, info.scce.pyro.primerefs.rest.SourceContainer update, boolean propagate){
			// handle type
			entity.primerefs.SourceContainerDB dbEntity = (entity.primerefs.SourceContainerDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.SourceContainer prev = info.scce.pyro.primerefs.rest.SourceContainer.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.SourceContainer.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToNode updatePrimeToNode(info.scce.pyro.primerefs.rest.PrimeToNode update){
			entity.primerefs.PrimeToNodeDB dbEntity = entity.primerefs.PrimeToNodeDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToNode apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeToNode) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeToNode(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToNode updatePrimeToNode(info.scce.cinco.product.primerefs.primerefs.PrimeToNode apiEntity, info.scce.pyro.primerefs.rest.PrimeToNode update){
			// handle type
			return updatePrimeToNode(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToNode updatePrimeToNode(info.scce.cinco.product.primerefs.primerefs.PrimeToNode apiEntity, info.scce.pyro.primerefs.rest.PrimeToNode update, boolean propagate){
			// handle type
			entity.primerefs.PrimeToNodeDB dbEntity = (entity.primerefs.PrimeToNodeDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeToNode prev = info.scce.pyro.primerefs.rest.PrimeToNode.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeToNode.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToEdge updatePrimeToEdge(info.scce.pyro.primerefs.rest.PrimeToEdge update){
			entity.primerefs.PrimeToEdgeDB dbEntity = entity.primerefs.PrimeToEdgeDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToEdge apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeToEdge) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeToEdge(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToEdge updatePrimeToEdge(info.scce.cinco.product.primerefs.primerefs.PrimeToEdge apiEntity, info.scce.pyro.primerefs.rest.PrimeToEdge update){
			// handle type
			return updatePrimeToEdge(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToEdge updatePrimeToEdge(info.scce.cinco.product.primerefs.primerefs.PrimeToEdge apiEntity, info.scce.pyro.primerefs.rest.PrimeToEdge update, boolean propagate){
			// handle type
			entity.primerefs.PrimeToEdgeDB dbEntity = (entity.primerefs.PrimeToEdgeDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeToEdge prev = info.scce.pyro.primerefs.rest.PrimeToEdge.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeToEdge.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToContainer updatePrimeToContainer(info.scce.pyro.primerefs.rest.PrimeToContainer update){
			entity.primerefs.PrimeToContainerDB dbEntity = entity.primerefs.PrimeToContainerDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToContainer apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeToContainer) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeToContainer(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToContainer updatePrimeToContainer(info.scce.cinco.product.primerefs.primerefs.PrimeToContainer apiEntity, info.scce.pyro.primerefs.rest.PrimeToContainer update){
			// handle type
			return updatePrimeToContainer(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToContainer updatePrimeToContainer(info.scce.cinco.product.primerefs.primerefs.PrimeToContainer apiEntity, info.scce.pyro.primerefs.rest.PrimeToContainer update, boolean propagate){
			// handle type
			entity.primerefs.PrimeToContainerDB dbEntity = (entity.primerefs.PrimeToContainerDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeToContainer prev = info.scce.pyro.primerefs.rest.PrimeToContainer.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeToContainer.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel updatePrimeToGraphModel(info.scce.pyro.primerefs.rest.PrimeToGraphModel update){
			entity.primerefs.PrimeToGraphModelDB dbEntity = entity.primerefs.PrimeToGraphModelDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeToGraphModel(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel updatePrimeToGraphModel(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel apiEntity, info.scce.pyro.primerefs.rest.PrimeToGraphModel update){
			// handle type
			return updatePrimeToGraphModel(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel updatePrimeToGraphModel(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel apiEntity, info.scce.pyro.primerefs.rest.PrimeToGraphModel update, boolean propagate){
			// handle type
			entity.primerefs.PrimeToGraphModelDB dbEntity = (entity.primerefs.PrimeToGraphModelDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeToGraphModel prev = info.scce.pyro.primerefs.rest.PrimeToGraphModel.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeToGraphModel.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToNode updatePrimeCToNode(info.scce.pyro.primerefs.rest.PrimeCToNode update){
			entity.primerefs.PrimeCToNodeDB dbEntity = entity.primerefs.PrimeCToNodeDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeCToNode apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeCToNode) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeCToNode(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToNode updatePrimeCToNode(info.scce.cinco.product.primerefs.primerefs.PrimeCToNode apiEntity, info.scce.pyro.primerefs.rest.PrimeCToNode update){
			// handle type
			return updatePrimeCToNode(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToNode updatePrimeCToNode(info.scce.cinco.product.primerefs.primerefs.PrimeCToNode apiEntity, info.scce.pyro.primerefs.rest.PrimeCToNode update, boolean propagate){
			// handle type
			entity.primerefs.PrimeCToNodeDB dbEntity = (entity.primerefs.PrimeCToNodeDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeCToNode prev = info.scce.pyro.primerefs.rest.PrimeCToNode.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeCToNode.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge updatePrimeCToEdge(info.scce.pyro.primerefs.rest.PrimeCToEdge update){
			entity.primerefs.PrimeCToEdgeDB dbEntity = entity.primerefs.PrimeCToEdgeDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeCToEdge(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge updatePrimeCToEdge(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge apiEntity, info.scce.pyro.primerefs.rest.PrimeCToEdge update){
			// handle type
			return updatePrimeCToEdge(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge updatePrimeCToEdge(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge apiEntity, info.scce.pyro.primerefs.rest.PrimeCToEdge update, boolean propagate){
			// handle type
			entity.primerefs.PrimeCToEdgeDB dbEntity = (entity.primerefs.PrimeCToEdgeDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeCToEdge prev = info.scce.pyro.primerefs.rest.PrimeCToEdge.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeCToEdge.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer updatePrimeCToContainer(info.scce.pyro.primerefs.rest.PrimeCToContainer update){
			entity.primerefs.PrimeCToContainerDB dbEntity = entity.primerefs.PrimeCToContainerDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeCToContainer(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer updatePrimeCToContainer(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer apiEntity, info.scce.pyro.primerefs.rest.PrimeCToContainer update){
			// handle type
			return updatePrimeCToContainer(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer updatePrimeCToContainer(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer apiEntity, info.scce.pyro.primerefs.rest.PrimeCToContainer update, boolean propagate){
			// handle type
			entity.primerefs.PrimeCToContainerDB dbEntity = (entity.primerefs.PrimeCToContainerDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeCToContainer prev = info.scce.pyro.primerefs.rest.PrimeCToContainer.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeCToContainer.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel updatePrimeCToGraphModel(info.scce.pyro.primerefs.rest.PrimeCToGraphModel update){
			entity.primerefs.PrimeCToGraphModelDB dbEntity = entity.primerefs.PrimeCToGraphModelDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeCToGraphModel(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel updatePrimeCToGraphModel(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel apiEntity, info.scce.pyro.primerefs.rest.PrimeCToGraphModel update){
			// handle type
			return updatePrimeCToGraphModel(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel updatePrimeCToGraphModel(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel apiEntity, info.scce.pyro.primerefs.rest.PrimeCToGraphModel update, boolean propagate){
			// handle type
			entity.primerefs.PrimeCToGraphModelDB dbEntity = (entity.primerefs.PrimeCToGraphModelDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeCToGraphModel prev = info.scce.pyro.primerefs.rest.PrimeCToGraphModel.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeCToGraphModel.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy updatePrimeToNodeHierarchy(info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy update){
			entity.primerefs.PrimeToNodeHierarchyDB dbEntity = entity.primerefs.PrimeToNodeHierarchyDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeToNodeHierarchy(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy updatePrimeToNodeHierarchy(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy apiEntity, info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy update){
			// handle type
			return updatePrimeToNodeHierarchy(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy updatePrimeToNodeHierarchy(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy apiEntity, info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy update, boolean propagate){
			// handle type
			entity.primerefs.PrimeToNodeHierarchyDB dbEntity = (entity.primerefs.PrimeToNodeHierarchyDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy prev = info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeToNodeHierarchy.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy updatePrimeToAbstractNodeHierarchy(info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy update){
			entity.primerefs.PrimeToAbstractNodeHierarchyDB dbEntity = entity.primerefs.PrimeToAbstractNodeHierarchyDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeToAbstractNodeHierarchy(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy updatePrimeToAbstractNodeHierarchy(info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy apiEntity, info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy update){
			// handle type
			return updatePrimeToAbstractNodeHierarchy(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy updatePrimeToAbstractNodeHierarchy(info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy apiEntity, info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy update, boolean propagate){
			// handle type
			entity.primerefs.PrimeToAbstractNodeHierarchyDB dbEntity = (entity.primerefs.PrimeToAbstractNodeHierarchyDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy prev = info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeToAbstractNodeHierarchy.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow updatePrimeToNodeFlow(info.scce.pyro.primerefs.rest.PrimeToNodeFlow update){
			entity.primerefs.PrimeToNodeFlowDB dbEntity = entity.primerefs.PrimeToNodeFlowDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeToNodeFlow(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow updatePrimeToNodeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow apiEntity, info.scce.pyro.primerefs.rest.PrimeToNodeFlow update){
			// handle type
			return updatePrimeToNodeFlow(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow updatePrimeToNodeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow apiEntity, info.scce.pyro.primerefs.rest.PrimeToNodeFlow update, boolean propagate){
			// handle type
			entity.primerefs.PrimeToNodeFlowDB dbEntity = (entity.primerefs.PrimeToNodeFlowDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeToNodeFlow prev = info.scce.pyro.primerefs.rest.PrimeToNodeFlow.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeToNodeFlow.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow updatePrimeToEdgeFlow(info.scce.pyro.primerefs.rest.PrimeToEdgeFlow update){
			entity.primerefs.PrimeToEdgeFlowDB dbEntity = entity.primerefs.PrimeToEdgeFlowDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeToEdgeFlow(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow updatePrimeToEdgeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow apiEntity, info.scce.pyro.primerefs.rest.PrimeToEdgeFlow update){
			// handle type
			return updatePrimeToEdgeFlow(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow updatePrimeToEdgeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow apiEntity, info.scce.pyro.primerefs.rest.PrimeToEdgeFlow update, boolean propagate){
			// handle type
			entity.primerefs.PrimeToEdgeFlowDB dbEntity = (entity.primerefs.PrimeToEdgeFlowDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeToEdgeFlow prev = info.scce.pyro.primerefs.rest.PrimeToEdgeFlow.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeToEdgeFlow.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow updatePrimeToContainerFlow(info.scce.pyro.primerefs.rest.PrimeToContainerFlow update){
			entity.primerefs.PrimeToContainerFlowDB dbEntity = entity.primerefs.PrimeToContainerFlowDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeToContainerFlow(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow updatePrimeToContainerFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow apiEntity, info.scce.pyro.primerefs.rest.PrimeToContainerFlow update){
			// handle type
			return updatePrimeToContainerFlow(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow updatePrimeToContainerFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow apiEntity, info.scce.pyro.primerefs.rest.PrimeToContainerFlow update, boolean propagate){
			// handle type
			entity.primerefs.PrimeToContainerFlowDB dbEntity = (entity.primerefs.PrimeToContainerFlowDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeToContainerFlow prev = info.scce.pyro.primerefs.rest.PrimeToContainerFlow.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeToContainerFlow.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow updatePrimeToGraphModelFlow(info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow update){
			entity.primerefs.PrimeToGraphModelFlowDB dbEntity = entity.primerefs.PrimeToGraphModelFlowDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeToGraphModelFlow(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow updatePrimeToGraphModelFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow apiEntity, info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow update){
			// handle type
			return updatePrimeToGraphModelFlow(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow updatePrimeToGraphModelFlow(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow apiEntity, info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow update, boolean propagate){
			// handle type
			entity.primerefs.PrimeToGraphModelFlowDB dbEntity = (entity.primerefs.PrimeToGraphModelFlowDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow prev = info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeToGraphModelFlow.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow updatePrimeCToNodeFlow(info.scce.pyro.primerefs.rest.PrimeCToNodeFlow update){
			entity.primerefs.PrimeCToNodeFlowDB dbEntity = entity.primerefs.PrimeCToNodeFlowDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeCToNodeFlow(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow updatePrimeCToNodeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow apiEntity, info.scce.pyro.primerefs.rest.PrimeCToNodeFlow update){
			// handle type
			return updatePrimeCToNodeFlow(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow updatePrimeCToNodeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow apiEntity, info.scce.pyro.primerefs.rest.PrimeCToNodeFlow update, boolean propagate){
			// handle type
			entity.primerefs.PrimeCToNodeFlowDB dbEntity = (entity.primerefs.PrimeCToNodeFlowDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeCToNodeFlow prev = info.scce.pyro.primerefs.rest.PrimeCToNodeFlow.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeCToNodeFlow.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow updatePrimeCToEdgeFlow(info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow update){
			entity.primerefs.PrimeCToEdgeFlowDB dbEntity = entity.primerefs.PrimeCToEdgeFlowDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeCToEdgeFlow(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow updatePrimeCToEdgeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow apiEntity, info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow update){
			// handle type
			return updatePrimeCToEdgeFlow(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow updatePrimeCToEdgeFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow apiEntity, info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow update, boolean propagate){
			// handle type
			entity.primerefs.PrimeCToEdgeFlowDB dbEntity = (entity.primerefs.PrimeCToEdgeFlowDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow prev = info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeCToEdgeFlow.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow updatePrimeCToContainerFlow(info.scce.pyro.primerefs.rest.PrimeCToContainerFlow update){
			entity.primerefs.PrimeCToContainerFlowDB dbEntity = entity.primerefs.PrimeCToContainerFlowDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeCToContainerFlow(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow updatePrimeCToContainerFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow apiEntity, info.scce.pyro.primerefs.rest.PrimeCToContainerFlow update){
			// handle type
			return updatePrimeCToContainerFlow(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow updatePrimeCToContainerFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow apiEntity, info.scce.pyro.primerefs.rest.PrimeCToContainerFlow update, boolean propagate){
			// handle type
			entity.primerefs.PrimeCToContainerFlowDB dbEntity = (entity.primerefs.PrimeCToContainerFlowDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeCToContainerFlow prev = info.scce.pyro.primerefs.rest.PrimeCToContainerFlow.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeCToContainerFlow.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow updatePrimeCToGraphModelFlow(info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow update){
			entity.primerefs.PrimeCToGraphModelFlowDB dbEntity = entity.primerefs.PrimeCToGraphModelFlowDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeCToGraphModelFlow(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow updatePrimeCToGraphModelFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow apiEntity, info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow update){
			// handle type
			return updatePrimeCToGraphModelFlow(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow updatePrimeCToGraphModelFlow(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow apiEntity, info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow update, boolean propagate){
			// handle type
			entity.primerefs.PrimeCToGraphModelFlowDB dbEntity = (entity.primerefs.PrimeCToGraphModelFlowDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow prev = info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeCToGraphModelFlow.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.SourceEdge updateSourceEdge(info.scce.pyro.primerefs.rest.SourceEdge update){
			entity.primerefs.SourceEdgeDB dbEntity = entity.primerefs.SourceEdgeDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.SourceEdge apiEntity = (info.scce.cinco.product.primerefs.primerefs.SourceEdge) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updateSourceEdge(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.SourceEdge updateSourceEdge(info.scce.cinco.product.primerefs.primerefs.SourceEdge apiEntity, info.scce.pyro.primerefs.rest.SourceEdge update){
			// handle type
			return updateSourceEdge(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.SourceEdge updateSourceEdge(info.scce.cinco.product.primerefs.primerefs.SourceEdge apiEntity, info.scce.pyro.primerefs.rest.SourceEdge update, boolean propagate){
			// handle type
			entity.primerefs.SourceEdgeDB dbEntity = (entity.primerefs.SourceEdgeDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.SourceEdge prev = info.scce.pyro.primerefs.rest.SourceEdge.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.SourceEdge.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeRefs updatePrimeRefs(info.scce.pyro.primerefs.rest.PrimeRefs update){
			entity.primerefs.PrimeRefsDB dbEntity = entity.primerefs.PrimeRefsDB.findById(update.getId());
			info.scce.cinco.product.primerefs.primerefs.PrimeRefs apiEntity = (info.scce.cinco.product.primerefs.primerefs.PrimeRefs) TypeRegistry.getDBToApi(dbEntity, this);
			// handle type
			return updatePrimeRefs(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeRefs updatePrimeRefs(info.scce.cinco.product.primerefs.primerefs.PrimeRefs apiEntity, info.scce.pyro.primerefs.rest.PrimeRefs update){
			// handle type
			return updatePrimeRefs(apiEntity, update, true);
		}
		
		public info.scce.cinco.product.primerefs.primerefs.PrimeRefs updatePrimeRefs(info.scce.cinco.product.primerefs.primerefs.PrimeRefs apiEntity, info.scce.pyro.primerefs.rest.PrimeRefs update, boolean propagate){
			// handle type
			entity.primerefs.PrimeRefsDB dbEntity = (entity.primerefs.PrimeRefsDB) apiEntity.getDelegate();
			info.scce.pyro.primerefs.rest.PrimeRefs prev = info.scce.pyro.primerefs.rest.PrimeRefs.fromEntityProperties(dbEntity,null);
			
		
			//for primitive prop
			
			//for complex prop
			
			dbEntity.persist();
			
			if(propagate) {
				super.updateProperties(TypeRegistry.getTypeOf(dbEntity),info.scce.pyro.primerefs.rest.PrimeRefs.fromEntityProperties(dbEntity,null),prev);
			}
			return apiEntity;
		}
		
		@Override
		public void updateAppearance() {
			super.getAllModelElements().forEach((element)->{
			});
		}

		
		public HierarchyCommandExecuter getHierarchyCommandExecuter() {
			return new HierarchyCommandExecuter(
				this.batch,
				this.graphModelWebSocket,
				this.highlightings,
				this.objectCache
			);
		}
		
		public FlowGraphCommandExecuter getFlowGraphCommandExecuter() {
			return new FlowGraphCommandExecuter(
				this.batch,
				this.graphModelWebSocket,
				this.highlightings,
				this.objectCache
			);
		}
	}
