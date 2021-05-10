package info.scce.cinco.product.primerefs.primerefs.impl;

import info.scce.cinco.product.primerefs.primerefs.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.PrimeRefsCommandExecuter;

public class PrimeCToContainerFlowImpl implements info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow {
	
	private final entity.primerefs.PrimeCToContainerFlowDB delegate;
	private final PrimeRefsCommandExecuter cmdExecuter;

	public PrimeCToContainerFlowImpl(
		entity.primerefs.PrimeCToContainerFlowDB delegate,
		PrimeRefsCommandExecuter cmdExecuter	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
	}
	
	public PrimeCToContainerFlowImpl(
		PrimeRefsCommandExecuter cmdExecuter	) {
		this.delegate = new entity.primerefs.PrimeCToContainerFlowDB();
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow
			&& ((info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow) obj).getId().equals(getId());
	}
	
	@Override
	public int hashCode() {
		return delegate.id.intValue();
	}
	
	@Override
	public String getId() {
		return Long.toString(this.delegate.id);
	}
	
	@Override
	public long getDelegateId() {
		return this.delegate.id;
	}
	
	@Override
	public entity.primerefs.PrimeCToContainerFlowDB getDelegate() {
		return this.delegate;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeRefs getRootElement() {
		return this.getContainer();
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeRefs getContainer() {
		return (info.scce.cinco.product.primerefs.primerefs.PrimeRefs) TypeRegistry.getDBToApi(this.delegate.getContainer(), cmdExecuter);
	}
	
	@Override
	public void delete() {
		removeEdges();
		removeNodes();
		cmdExecuter.removePrimeCToContainerFlow(this,this.getPr());
		this.delegate.delete();
	}
	
	private void removeEdges() {
		java.util.List<graphmodel.Edge> outgoing = this.getOutgoing();
		java.util.List<graphmodel.Edge> incoming = this.getIncoming();
		
		java.util.Iterator<graphmodel.Edge> iter_outgoing = outgoing.iterator();
		while(iter_outgoing.hasNext()) {
			graphmodel.Edge e = iter_outgoing.next();
			if(e != null) {
				e.delete();
				outgoing.remove(e);
			}
			iter_outgoing = outgoing.iterator();
		}
		
		java.util.Iterator<graphmodel.Edge> iter_incoming = incoming.iterator();
		while(iter_incoming.hasNext()) {
			graphmodel.Edge e = iter_incoming.next();
			if(e != null) {
				e.delete();
				incoming.remove(e);
			}
			iter_incoming = incoming.iterator();
		}
	}
	
	@Override
	public int getX() {
	    return (int)this.delegate.x;
	}
	
	@Override
	public int getY() {
	    return (int)this.delegate.y;
	}
	
	@Override
	public int getWidth() {
	    return (int)this.delegate.width;
	}
	
	@Override
	public int getHeight() {
	    return (int)this.delegate.height;
	}
	
	@Override
	public java.util.List<graphmodel.Edge> getIncoming() {
		java.util.List<graphmodel.Edge> edges = new java.util.LinkedList<>();
		java.util.Collection<PanacheEntity> incoming = this.delegate.getIncoming();
		for(PanacheEntity e : incoming) {
			graphmodel.Edge edge = (graphmodel.Edge) TypeRegistry.getDBToApi(e, cmdExecuter);
			edges.add(edge);
		}
		return edges;
	}
	
	@Override
	public <T extends graphmodel.Edge> java.util.List<T> getIncoming(Class<T> clazz) {
	    return getIncoming().stream().filter(n->clazz.isInstance(n)).map(n->clazz.cast(n)).collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public java.util.List<graphmodel.Node> getPredecessors() {
	    return getIncoming().stream().map(n->n.getSourceElement()).collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public <T extends graphmodel.Node> java.util.List<T> getPredecessors(Class<T> clazz) {
	   return getPredecessors().stream().filter(n->clazz.isInstance(n)).map(n->clazz.cast(n)).collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public java.util.List<graphmodel.Edge> getOutgoing() {
		java.util.List<graphmodel.Edge> edges = new java.util.LinkedList<>();
		java.util.Collection<PanacheEntity> outgoing = this.delegate.getOutgoing();
		for(PanacheEntity e : outgoing) {
			graphmodel.Edge edge = (graphmodel.Edge) TypeRegistry.getDBToApi(e, cmdExecuter);
			edges.add(edge);
		}
		return edges;
	}
	
	@Override
	public <T extends graphmodel.Edge> java.util.List<T> getOutgoing(Class<T> clazz) {
	   return getOutgoing().stream().filter(n->clazz.isInstance(n)).map(n->clazz.cast(n)).collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public java.util.List<graphmodel.Node> getSuccessors() {
	    return getOutgoing().stream().map(n->n.getTargetElement()).collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public <T extends graphmodel.Node> java.util.List<T> getSuccessors(Class<T> clazz) {
	    return getSuccessors().stream().filter(n->clazz.isInstance(n)).map(n->clazz.cast(n)).collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public void move(int x, int y) {
		this.moveTo(this.getContainer(),x,y);
	}
	
	private void changeContainer(PanacheEntity newContainer) {
		PanacheEntity c = this.delegate.getContainer();
		if(c instanceof entity.primerefs.PrimeRefsDB) {
			entity.primerefs.PrimeRefsDB container = (entity.primerefs.PrimeRefsDB) c;
			container.removeModelElements(this.delegate);
			this.setContainer(newContainer);
		}
	}
	
	private void setContainer(PanacheEntity c) {
		if(c instanceof entity.primerefs.PrimeRefsDB) {
			entity.primerefs.PrimeRefsDB newContainer = (entity.primerefs.PrimeRefsDB) c;
			newContainer.addModelElements(this.delegate);
			this.delegate.setContainer(newContainer);
		}
	}
	
	@Override
	public void moveTo(graphmodel.ModelElementContainer container,int x, int y) {
		// command executer
		this.cmdExecuter.moveNode(
			TypeRegistry.getTypeOf(this),
			this,
			container,
			TypeRegistry.getTypeOf(container),
			TypeRegistry.getTypeOf(this.getContainer()),
			x,
			y
		);
		
		// changes
		PanacheEntity oldContainer = this.delegate.getContainer();
		PanacheEntity newContainer = TypeRegistry.getApiToDB(container);
		this.changeContainer(newContainer);
		this.delegate.x = x;
		this.delegate.y = y;
		
		// postMove
		
		// persist
		oldContainer.persist();
		newContainer.persist();
		this.delegate.persist();
	}
	
	@Override
	public void resize(int width, int height) {
		String type = TypeRegistry.getTypeOf(this);
		this.cmdExecuter.resizeNode(type, this, width, height);
		this.delegate.width = width;
		this.delegate.height = height;
		this.delegate.persist();
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.Swimlane getPr()
	{
		PanacheEntity entity = delegate.getPr();
		return (info.scce.cinco.product.flowgraph.flowgraph.Swimlane) TypeRegistry.getDBToApi(entity, cmdExecuter);
	}
	
	private void removeNodes() {
		java.util.List<graphmodel.Node> nodes = this.getModelElements(graphmodel.Node.class);
		
		java.util.Iterator<graphmodel.Node> iter_nodes = nodes.iterator();
		while(iter_nodes.hasNext()) {
			graphmodel.Node e = iter_nodes.next();
			if(e != null) {
				e.delete();
				nodes.remove(e);
			}
			iter_nodes = nodes.iterator();
		}
	}
	
	@Override
	public java.util.List<graphmodel.ModelElement> getModelElements() {
		java.util.List<graphmodel.ModelElement> modelElements = new java.util.LinkedList<>();
		java.util.Collection<PanacheEntity> m = this.delegate.getModelElements();
		for(PanacheEntity e : m) {
			graphmodel.ModelElement apiE = (graphmodel.ModelElement) TypeRegistry.getDBToApi(e, cmdExecuter);
			modelElements.add(apiE);
		}
		return modelElements;
	}
		
	@Override
	public <T extends graphmodel.ModelElement> java.util.List<T> getModelElements(Class<T> clazz) {
		return this.getModelElements().stream().filter(n->clazz.isInstance(n)).map(n->clazz.cast(n)).collect(java.util.stream.Collectors.toList());
	}
	
	private java.util.List<graphmodel.ModelElement> getAllModelElements(graphmodel.ModelElementContainer cmc) {
		java.util.List<graphmodel.ModelElement> cm = new java.util.LinkedList<>(cmc.getModelElements());
		cm.addAll(cmc.getModelElements().stream()
			.filter(n -> n instanceof graphmodel.ModelElementContainer)
			.flatMap(n->
				getAllModelElements((graphmodel.ModelElementContainer)n).stream()
			).collect(java.util.stream.Collectors.toList()));
		return cm;
	}
	
	@Override
	public <T extends graphmodel.Edge> java.util.List<T> getEdges(Class<T> clazz) {
		return getModelElements(clazz);
	}
	
	@Override
	public <T extends graphmodel.Node> java.util.List<T> getNodes(Class<T> clazz) {
		return getModelElements(clazz);
	}
	
	@Override
	public java.util.List<graphmodel.Node> getNodes() {
		return getModelElements(graphmodel.Node.class);
	}
		
	@Override
	public java.util.List<graphmodel.Node> getAllNodes() {
		return getAllModelElements(this).stream()
		.filter(n->n instanceof graphmodel.Node)
		.map(n->(graphmodel.Node)n)
		.collect(java.util.stream.Collectors.toList());
	}
		
	@Override
	public java.util.List<graphmodel.Edge> getAllEdges() {
		return getAllModelElements(this).stream()
			.filter(n->n instanceof graphmodel.Edge)
			.map(n->(graphmodel.Edge)n)
			.collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public java.util.List<graphmodel.Container> getAllContainers() {
		return getAllModelElements(this).stream()
			.filter(n->n instanceof graphmodel.Container)
			.map(n->(graphmodel.Container)n)
			.collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.SourceNode newSourceNode(int x, int y, int width, int height) {
		info.scce.cinco.product.primerefs.primerefs.SourceNode cn = cmdExecuter.createSourceNode(x,y,new Long(width),new Long(height),this,null);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.SourceNode newSourceNode(int x, int y) {
			return this.newSourceNode(x,y,36,
			36
			);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.SourceNode> getSourceNodes() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.SourceNode.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.SourceContainer newSourceContainer(int x, int y, int width, int height) {
		info.scce.cinco.product.primerefs.primerefs.SourceContainer cn = cmdExecuter.createSourceContainer(x,y,new Long(width),new Long(height),this,null);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.SourceContainer newSourceContainer(int x, int y) {
			return this.newSourceContainer(x,y,36,
			36
			);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.SourceContainer> getSourceContainers() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.SourceContainer.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToNode newPrimeToNode(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToNode cn = cmdExecuter.createPrimeToNode(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToNode newPrimeToNode(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToNode cn = cmdExecuter.createPrimeToNode(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeToNode> getPrimeToNodes() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeToNode.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToEdge newPrimeToEdge(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToEdge cn = cmdExecuter.createPrimeToEdge(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToEdge newPrimeToEdge(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToEdge cn = cmdExecuter.createPrimeToEdge(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeToEdge> getPrimeToEdges() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeToEdge.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToContainer newPrimeToContainer(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToContainer cn = cmdExecuter.createPrimeToContainer(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToContainer newPrimeToContainer(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToContainer cn = cmdExecuter.createPrimeToContainer(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeToContainer> getPrimeToContainers() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeToContainer.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel newPrimeToGraphModel(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel cn = cmdExecuter.createPrimeToGraphModel(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel newPrimeToGraphModel(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel cn = cmdExecuter.createPrimeToGraphModel(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel> getPrimeToGraphModels() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModel.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeCToNode newPrimeCToNode(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeCToNode cn = cmdExecuter.createPrimeCToNode(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeCToNode newPrimeCToNode(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeCToNode cn = cmdExecuter.createPrimeCToNode(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeCToNode> getPrimeCToNodes() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeCToNode.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge newPrimeCToEdge(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge cn = cmdExecuter.createPrimeCToEdge(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge newPrimeCToEdge(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge cn = cmdExecuter.createPrimeCToEdge(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge> getPrimeCToEdges() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdge.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer newPrimeCToContainer(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer cn = cmdExecuter.createPrimeCToContainer(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer newPrimeCToContainer(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer cn = cmdExecuter.createPrimeCToContainer(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer> getPrimeCToContainers() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainer.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel newPrimeCToGraphModel(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel cn = cmdExecuter.createPrimeCToGraphModel(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel newPrimeCToGraphModel(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel cn = cmdExecuter.createPrimeCToGraphModel(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel> getPrimeCToGraphModels() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModel.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy newPrimeToNodeHierarchy(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy cn = cmdExecuter.createPrimeToNodeHierarchy(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy newPrimeToNodeHierarchy(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy cn = cmdExecuter.createPrimeToNodeHierarchy(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy> getPrimeToNodeHierarchys() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeHierarchy.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy newPrimeToAbstractNodeHierarchy(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy cn = cmdExecuter.createPrimeToAbstractNodeHierarchy(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy newPrimeToAbstractNodeHierarchy(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy cn = cmdExecuter.createPrimeToAbstractNodeHierarchy(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy> getPrimeToAbstractNodeHierarchys() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeToAbstractNodeHierarchy.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow newPrimeToNodeFlow(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow cn = cmdExecuter.createPrimeToNodeFlow(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow newPrimeToNodeFlow(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow cn = cmdExecuter.createPrimeToNodeFlow(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow> getPrimeToNodeFlows() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeToNodeFlow.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow newPrimeToEdgeFlow(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow cn = cmdExecuter.createPrimeToEdgeFlow(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow newPrimeToEdgeFlow(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow cn = cmdExecuter.createPrimeToEdgeFlow(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow> getPrimeToEdgeFlows() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeToEdgeFlow.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow newPrimeToContainerFlow(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow cn = cmdExecuter.createPrimeToContainerFlow(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow newPrimeToContainerFlow(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow cn = cmdExecuter.createPrimeToContainerFlow(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow> getPrimeToContainerFlows() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeToContainerFlow.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow newPrimeToGraphModelFlow(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow cn = cmdExecuter.createPrimeToGraphModelFlow(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow newPrimeToGraphModelFlow(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow cn = cmdExecuter.createPrimeToGraphModelFlow(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow> getPrimeToGraphModelFlows() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeToGraphModelFlow.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow newPrimeCToNodeFlow(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow cn = cmdExecuter.createPrimeCToNodeFlow(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow newPrimeCToNodeFlow(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow cn = cmdExecuter.createPrimeCToNodeFlow(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow> getPrimeCToNodeFlows() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeCToNodeFlow.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow newPrimeCToEdgeFlow(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow cn = cmdExecuter.createPrimeCToEdgeFlow(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow newPrimeCToEdgeFlow(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow cn = cmdExecuter.createPrimeCToEdgeFlow(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow> getPrimeCToEdgeFlows() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeCToEdgeFlow.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow newPrimeCToContainerFlow(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow cn = cmdExecuter.createPrimeCToContainerFlow(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow newPrimeCToContainerFlow(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow cn = cmdExecuter.createPrimeCToContainerFlow(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow> getPrimeCToContainerFlows() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeCToContainerFlow.class);
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow newPrimeCToGraphModelFlow(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow cn = cmdExecuter.createPrimeCToGraphModelFlow(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow newPrimeCToGraphModelFlow(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow cn = cmdExecuter.createPrimeCToGraphModelFlow(
			x,
			y,
			new Long(width),
			new Long(height),
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow> getPrimeCToGraphModelFlows() {
		return getModelElements(info.scce.cinco.product.primerefs.primerefs.PrimeCToGraphModelFlow.class);
	}
}
