package info.scce.cinco.product.empty.empty.impl;

import info.scce.cinco.product.empty.empty.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.EmptyCommandExecuter;

public class EmptyImpl implements info.scce.cinco.product.empty.empty.Empty {
	
	private final entity.empty.EmptyDB delegate;
	private final EmptyCommandExecuter cmdExecuter;

	public EmptyImpl(
		entity.empty.EmptyDB delegate,
		EmptyCommandExecuter cmdExecuter	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
	}
	
	public EmptyImpl(
		EmptyCommandExecuter cmdExecuter	) {
		this.delegate = new entity.empty.EmptyDB();
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.empty.empty.Empty
			&& ((info.scce.cinco.product.empty.empty.Empty) obj).getId().equals(getId());
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
	public entity.empty.EmptyDB getDelegate() {
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
	public String getModelName() {
		return this.delegate.modelName;
	}
	
	@Override
	public void setModelName(String attr) {
		info.scce.pyro.empty.rest.Empty prev = info.scce.pyro.empty.rest.Empty.fromEntityProperties(this.delegate,null);
		this.delegate.modelName = attr;
		
		// commandExecuter
		this.cmdExecuter.updateEmptyProperties(this,prev);
		
		// persist
		this.delegate.persist();
	}
}
