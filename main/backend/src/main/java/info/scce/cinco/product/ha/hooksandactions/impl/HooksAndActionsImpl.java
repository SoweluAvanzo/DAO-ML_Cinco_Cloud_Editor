package info.scce.cinco.product.ha.hooksandactions.impl;

import info.scce.cinco.product.ha.hooksandactions.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.HooksAndActionsCommandExecuter;

public class HooksAndActionsImpl implements info.scce.cinco.product.ha.hooksandactions.HooksAndActions {
	
	private final entity.hooksandactions.HooksAndActionsDB delegate;
	private final HooksAndActionsCommandExecuter cmdExecuter;

	public HooksAndActionsImpl(
		entity.hooksandactions.HooksAndActionsDB delegate,
		HooksAndActionsCommandExecuter cmdExecuter	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
	}
	
	public HooksAndActionsImpl(
		HooksAndActionsCommandExecuter cmdExecuter	) {
		this.delegate = new entity.hooksandactions.HooksAndActionsDB();
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.ha.hooksandactions.HooksAndActions
			&& ((info.scce.cinco.product.ha.hooksandactions.HooksAndActions) obj).getId().equals(getId());
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
	public entity.hooksandactions.HooksAndActionsDB getDelegate() {
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
	public java.util.List<info.scce.cinco.product.ha.hooksandactions.HookAnEdge> getHookAnEdges() {
		return this.getModelElements(info.scce.cinco.product.ha.hooksandactions.HookAnEdge.class);
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
	public java.util.List<info.scce.cinco.product.ha.hooksandactions.AbstractHookANode> getAbstractHookANodes() {
		return getModelElements(info.scce.cinco.product.ha.hooksandactions.AbstractHookANode.class);
	}
	
	@Override
	public info.scce.cinco.product.ha.hooksandactions.HookAContainer newHookAContainer(int x, int y, int width, int height) {
		info.scce.cinco.product.ha.hooksandactions.HookAContainer cn = cmdExecuter.createHookAContainer(x,y,new Long(width),new Long(height),this,null);
		// postCreateHooks
		{
			info.scce.cinco.product.flowgraph.hooks.PostCreate hook = new info.scce.cinco.product.flowgraph.hooks.PostCreate();
			hook.init(cmdExecuter);
			hook.postCreate(cn);
		}
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.ha.hooksandactions.HookAContainer newHookAContainer(int x, int y) {
			return this.newHookAContainer(x,y,36,
			36
			);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.ha.hooksandactions.HookAContainer> getHookAContainers() {
		return getModelElements(info.scce.cinco.product.ha.hooksandactions.HookAContainer.class);
	}
	
	@Override
	public info.scce.cinco.product.ha.hooksandactions.HookANode newHookANode(int x, int y, int width, int height) {
		info.scce.cinco.product.ha.hooksandactions.HookANode cn = cmdExecuter.createHookANode(x,y,new Long(width),new Long(height),this,null);
		// postCreateHooks
		{
			info.scce.cinco.product.flowgraph.hooks.PostCreate2 hook = new info.scce.cinco.product.flowgraph.hooks.PostCreate2();
			hook.init(cmdExecuter);
			hook.postCreate(cn);
		}
		{
			info.scce.cinco.product.flowgraph.hooks.PostCreate hook = new info.scce.cinco.product.flowgraph.hooks.PostCreate();
			hook.init(cmdExecuter);
			hook.postCreate(cn);
		}
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.ha.hooksandactions.HookANode newHookANode(int x, int y) {
			return this.newHookANode(x,y,36,
			36
			);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.ha.hooksandactions.HookANode> getHookANodes() {
		return getModelElements(info.scce.cinco.product.ha.hooksandactions.HookANode.class);
	}
	
	@Override
	public info.scce.cinco.product.ha.hooksandactions.HookAType getAtype() {
		info.scce.pyro.hooksandactions.rest.HooksAndActions prev = info.scce.pyro.hooksandactions.rest.HooksAndActions.fromEntityProperties(this.delegate,null);
		PanacheEntity attribute = this.delegate.getAtype();
		return (info.scce.cinco.product.ha.hooksandactions.HookAType) TypeRegistry.getDBToApi(attribute, this.cmdExecuter, this, prev);
	}
	
	@Override
	public void setAtype(info.scce.cinco.product.ha.hooksandactions.HookAType attr) {
		info.scce.pyro.hooksandactions.rest.HooksAndActions prev = info.scce.pyro.hooksandactions.rest.HooksAndActions.fromEntityProperties(this.delegate,null);
		
		// cast value
		PanacheEntity newEntity = attr.getDelegate();
		// set new value/delete old value
		this.delegate.setAtype(newEntity, true);
		
		// commandExecuter
		this.cmdExecuter.updateHooksAndActionsProperties(this,prev);
		
		// persist
		this.delegate.persist();
		
		//property change hook
		org.eclipse.emf.ecore.EStructuralFeature esf = new org.eclipse.emf.ecore.EStructuralFeature();
		esf.setName("atype");
		info.scce.cinco.product.flowgraph.hooks.PostAttributeChange hook = new info.scce.cinco.product.flowgraph.hooks.PostAttributeChange();
		hook.init(cmdExecuter);
		if(hook.canHandleChange(this,esf)) {
			hook.handleChange(this,esf);
		}
	}
	
	@Override
	public String getAttribute() {
		return this.delegate.attribute;
	}
	
	@Override
	public void setAttribute(String attr) {
		info.scce.pyro.hooksandactions.rest.HooksAndActions prev = info.scce.pyro.hooksandactions.rest.HooksAndActions.fromEntityProperties(this.delegate,null);
		this.delegate.attribute = attr;
		
		// commandExecuter
		this.cmdExecuter.updateHooksAndActionsProperties(this,prev);
		
		// persist
		this.delegate.persist();
		
		//property change hook
		org.eclipse.emf.ecore.EStructuralFeature esf = new org.eclipse.emf.ecore.EStructuralFeature();
		esf.setName("attribute");
		info.scce.cinco.product.flowgraph.hooks.PostAttributeChange hook = new info.scce.cinco.product.flowgraph.hooks.PostAttributeChange();
		hook.init(cmdExecuter);
		if(hook.canHandleChange(this,esf)) {
			hook.handleChange(this,esf);
		}
	}
}
