package info.scce.cinco.product.hierarchy.hierarchy.impl;

import info.scce.cinco.product.hierarchy.hierarchy.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.HierarchyCommandExecuter;

public class DImpl implements info.scce.cinco.product.hierarchy.hierarchy.D {
	
	private final entity.hierarchy.DDB delegate;
	private final HierarchyCommandExecuter cmdExecuter;

	public DImpl(
		entity.hierarchy.DDB delegate,
		HierarchyCommandExecuter cmdExecuter	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
	}
	
	public DImpl(
		HierarchyCommandExecuter cmdExecuter	) {
		this.delegate = new entity.hierarchy.DDB();
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.hierarchy.hierarchy.D
			&& ((info.scce.cinco.product.hierarchy.hierarchy.D) obj).getId().equals(getId());
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
	public entity.hierarchy.DDB getDelegate() {
		return this.delegate;
	}
	
	@Override
	public info.scce.cinco.product.hierarchy.hierarchy.Hierarchy getRootElement() {
		info.scce.cinco.product.hierarchy.hierarchy.ContD container = this.getContainer();
		if(container instanceof info.scce.cinco.product.hierarchy.hierarchy.Hierarchy){
			return (info.scce.cinco.product.hierarchy.hierarchy.Hierarchy) container;
		} else if(container instanceof graphmodel.ModelElement) {
			graphmodel.ModelElement parent = (graphmodel.ModelElement) container;
			return (info.scce.cinco.product.hierarchy.hierarchy.Hierarchy) parent.getRootElement();
		} else {
			return null;
		}
	}
	
	@Override
	public info.scce.cinco.product.hierarchy.hierarchy.ContD getContainer() {
		return (info.scce.cinco.product.hierarchy.hierarchy.ContD) TypeRegistry.getDBToApi(this.delegate.getContainer(), cmdExecuter);
	}
	
	@Override
	public void delete() {
		removeEdges();
		cmdExecuter.removeD(this);
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
		if(c instanceof entity.hierarchy.ContDB) {
			entity.hierarchy.ContDB container = (entity.hierarchy.ContDB) c;
			container.removeModelElements(this.delegate);
			this.setContainer(newContainer);
		} else if(c instanceof entity.hierarchy.ContDDB) {
			entity.hierarchy.ContDDB container = (entity.hierarchy.ContDDB) c;
			container.removeModelElements(this.delegate);
			this.setContainer(newContainer);
		} else if(c instanceof entity.hierarchy.ContADB) {
			entity.hierarchy.ContADB container = (entity.hierarchy.ContADB) c;
			container.removeModelElements(this.delegate);
			this.setContainer(newContainer);
		}
	}
	
	private void setContainer(PanacheEntity c) {
		if(c instanceof entity.hierarchy.ContDB) {
			entity.hierarchy.ContDB newContainer = (entity.hierarchy.ContDB) c;
			newContainer.addModelElements(this.delegate);
			this.delegate.setContainer(newContainer);
		} else if(c instanceof entity.hierarchy.ContDDB) {
			entity.hierarchy.ContDDB newContainer = (entity.hierarchy.ContDDB) c;
			newContainer.addModelElements(this.delegate);
			this.delegate.setContainer(newContainer);
		} else if(c instanceof entity.hierarchy.ContADB) {
			entity.hierarchy.ContADB newContainer = (entity.hierarchy.ContADB) c;
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
	public String getOfD() {
		return this.delegate.ofD;
	}
	
	@Override
	public void setOfD(String attr) {
		info.scce.pyro.hierarchy.rest.D prev = info.scce.pyro.hierarchy.rest.D.fromEntityProperties(this.delegate,null);
		this.delegate.ofD = attr;
		
		// commandExecuter
		this.cmdExecuter.updateDProperties(this,prev);
		
		// persist
		this.delegate.persist();
	}
	
	@Override
	public info.scce.cinco.product.hierarchy.hierarchy.TD getTd() {
		info.scce.pyro.hierarchy.rest.D prev = info.scce.pyro.hierarchy.rest.D.fromEntityProperties(this.delegate,null);
		PanacheEntity attribute = this.delegate.getTd();
		return (info.scce.cinco.product.hierarchy.hierarchy.TD) TypeRegistry.getDBToApi(attribute, this.cmdExecuter, this, prev);
	}
	
	@Override
	public void setTd(info.scce.cinco.product.hierarchy.hierarchy.TD attr) {
		info.scce.pyro.hierarchy.rest.D prev = info.scce.pyro.hierarchy.rest.D.fromEntityProperties(this.delegate,null);
		
		// cast value
		PanacheEntity newEntity = attr.getDelegate();
		// set new value/delete old value
		this.delegate.setTd(newEntity, true);
		
		// commandExecuter
		this.cmdExecuter.updateDProperties(this,prev);
		
		// persist
		this.delegate.persist();
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TD> getTdList() {
		info.scce.pyro.hierarchy.rest.D prev = info.scce.pyro.hierarchy.rest.D.fromEntityProperties(this.delegate,null);
		java.util.Collection<PanacheEntity> entityList = this.delegate.getTdList();
		return (java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TD>) entityList.stream().map(n -> {
			return (info.scce.cinco.product.hierarchy.hierarchy.TD) TypeRegistry.getDBToApi(n, this.cmdExecuter, this, prev);
		}).collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public void setTdList(java.util.List<info.scce.cinco.product.hierarchy.hierarchy.TD> attr) {
		info.scce.pyro.hierarchy.rest.D prev = info.scce.pyro.hierarchy.rest.D.fromEntityProperties(this.delegate,null);
		
		// cast values
		java.util.Collection<PanacheEntity> newList = attr.stream().map(n -> 
				n.getDelegate()
			).collect(java.util.stream.Collectors.toList());
		// delete values that are not present in newList
		this.delegate.getTdList().stream().filter(
				(e) -> !newList.contains(e)
			).forEach(
				(e) -> this.delegate.removeTdList(e, true)
			);
		// set new values
		this.delegate.setTdList(newList);
		
		// commandExecuter
		this.cmdExecuter.updateDProperties(this,prev);
		
		// persist
		this.delegate.persist();
	}
}
