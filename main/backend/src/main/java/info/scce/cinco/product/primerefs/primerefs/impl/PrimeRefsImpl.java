package info.scce.cinco.product.primerefs.primerefs.impl;

import info.scce.cinco.product.primerefs.primerefs.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.PrimeRefsCommandExecuter;

public class PrimeRefsImpl implements info.scce.cinco.product.primerefs.primerefs.PrimeRefs {
	
	private final entity.primerefs.PrimeRefsDB delegate;
	private final PrimeRefsCommandExecuter cmdExecuter;

	public PrimeRefsImpl(
		entity.primerefs.PrimeRefsDB delegate,
		PrimeRefsCommandExecuter cmdExecuter	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
	}
	
	public PrimeRefsImpl(
		PrimeRefsCommandExecuter cmdExecuter	) {
		this.delegate = new entity.primerefs.PrimeRefsDB();
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.primerefs.primerefs.PrimeRefs
			&& ((info.scce.cinco.product.primerefs.primerefs.PrimeRefs) obj).getId().equals(getId());
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
	public entity.primerefs.PrimeRefsDB getDelegate() {
		return this.delegate;
	}
	
	@Override
	public long getWidth() {
		return this.delegate.width;
	}
	
	@Override
	public long getHeight() {
		return this.delegate.height;
	}
	
	@Override
	public String getRouter() {
		return this.delegate.router;
	}
	
	@Override
	public String getConnector() {
		return this.delegate.connector;
	}
	
	@Override
	public double getScale() {
		return this.delegate.scale;
	}
	
	@Override
	public String getFileName() {
		return this.delegate.filename;
	}
	
	@Override
	public String getExtension() {
		return this.delegate.extension;
	}
	
	@Override
	public void deleteModelElement(graphmodel.ModelElement me) {
		PanacheEntity e = TypeRegistry.getApiToDB(me);
		if(e != null) {
			e.delete();
		}
	}
	
	@Override
	public void delete() {
		this.delegate.delete();
	}
	
	
	@Override
	public java.util.List<info.scce.cinco.product.primerefs.primerefs.SourceEdge> getSourceEdges() {
		return this.getModelElements(info.scce.cinco.product.primerefs.primerefs.SourceEdge.class);
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
