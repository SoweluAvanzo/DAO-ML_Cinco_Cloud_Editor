package info.scce.cinco.product.hierarchy.hierarchy.impl;

import info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.HierarchyCommandExecuter;

public class HierarchyImpl implements info.scce.cinco.product.hierarchy.hierarchy.Hierarchy {
	
	private final entity.hierarchy.HierarchyDB delegate;
	private final HierarchyCommandExecuter cmdExecuter;

	public HierarchyImpl(
		entity.hierarchy.HierarchyDB delegate,
		HierarchyCommandExecuter cmdExecuter	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
	}
	
	public HierarchyImpl(
		HierarchyCommandExecuter cmdExecuter	) {
		this.delegate = new entity.hierarchy.HierarchyDB();
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.hierarchy.hierarchy.Hierarchy
			&& ((info.scce.cinco.product.hierarchy.hierarchy.Hierarchy) obj).getId().equals(getId());
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
	public entity.hierarchy.HierarchyDB getDelegate() {
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
	public java.util.List<info.scce.cinco.product.hierarchy.hierarchy.EdgeA> getEdgeAs() {
		return this.getModelElements(info.scce.cinco.product.hierarchy.hierarchy.EdgeA.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.hierarchy.hierarchy.EdgeC> getEdgeCs() {
		return this.getModelElements(info.scce.cinco.product.hierarchy.hierarchy.EdgeC.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.hierarchy.hierarchy.EdgeB> getEdgeBs() {
		return this.getModelElements(info.scce.cinco.product.hierarchy.hierarchy.EdgeB.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.hierarchy.hierarchy.EdgeD> getEdgeDs() {
		return this.getModelElements(info.scce.cinco.product.hierarchy.hierarchy.EdgeD.class);
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
	public java.util.List<info.scce.cinco.product.hierarchy.hierarchy.ContB> getContBs() {
		return getModelElements(info.scce.cinco.product.hierarchy.hierarchy.ContB.class);
	}
	
	@Override
	public info.scce.cinco.product.hierarchy.hierarchy.ContA newContA(int x, int y, int width, int height) {
		info.scce.cinco.product.hierarchy.hierarchy.ContA cn = cmdExecuter.createContA(x,y,new Long(width),new Long(height),this,null);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.hierarchy.hierarchy.ContA newContA(int x, int y) {
			return this.newContA(x,y,400,
			100
			);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.hierarchy.hierarchy.ContA> getContAs() {
		return getModelElements(info.scce.cinco.product.hierarchy.hierarchy.ContA.class);
	}
	
	@Override
	public info.scce.cinco.product.hierarchy.hierarchy.Cont newCont(int x, int y, int width, int height) {
		info.scce.cinco.product.hierarchy.hierarchy.Cont cn = cmdExecuter.createCont(x,y,new Long(width),new Long(height),this,null);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.hierarchy.hierarchy.Cont newCont(int x, int y) {
			return this.newCont(x,y,400,
			100
			);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.hierarchy.hierarchy.Cont> getConts() {
		return getModelElements(info.scce.cinco.product.hierarchy.hierarchy.Cont.class);
	}
	
	@Override
	public info.scce.cinco.product.hierarchy.hierarchy.TA getTa() {
		info.scce.pyro.hierarchy.rest.Hierarchy prev = info.scce.pyro.hierarchy.rest.Hierarchy.fromEntityProperties(this.delegate,null);
		PanacheEntity attribute = this.delegate.getTa();
		return (info.scce.cinco.product.hierarchy.hierarchy.TA) TypeRegistry.getDBToApi(attribute, this.cmdExecuter, this, prev);
	}
	
	@Override
	public void setTa(info.scce.cinco.product.hierarchy.hierarchy.TA attr) {
		info.scce.pyro.hierarchy.rest.Hierarchy prev = info.scce.pyro.hierarchy.rest.Hierarchy.fromEntityProperties(this.delegate,null);
		
		// cast value
		PanacheEntity newEntity = attr.getDelegate();
		// set new value/delete old value
		this.delegate.setTa(newEntity, true);
		
		// commandExecuter
		this.cmdExecuter.updateHierarchyProperties(this,prev);
		
		// persist
		this.delegate.persist();
	}
	
	@Override
	public String getModelName() {
		return this.delegate.modelName;
	}
	
	@Override
	public void setModelName(String attr) {
		info.scce.pyro.hierarchy.rest.Hierarchy prev = info.scce.pyro.hierarchy.rest.Hierarchy.fromEntityProperties(this.delegate,null);
		this.delegate.modelName = attr;
		
		// commandExecuter
		this.cmdExecuter.updateHierarchyProperties(this,prev);
		
		// persist
		this.delegate.persist();
	}
}
