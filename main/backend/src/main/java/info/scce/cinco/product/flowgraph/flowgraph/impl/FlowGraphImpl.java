package info.scce.cinco.product.flowgraph.flowgraph.impl;

import info.scce.cinco.product.flowgraph.flowgraph.util.TypeRegistry;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import info.scce.pyro.core.command.FlowGraphCommandExecuter;

public class FlowGraphImpl implements info.scce.cinco.product.flowgraph.flowgraph.FlowGraph {
	
	private final entity.flowgraph.FlowGraphDB delegate;
	private final FlowGraphCommandExecuter cmdExecuter;

	public FlowGraphImpl(
		entity.flowgraph.FlowGraphDB delegate,
		FlowGraphCommandExecuter cmdExecuter	) {
		this.delegate = delegate;
		this.cmdExecuter = cmdExecuter;
	}
	
	public FlowGraphImpl(
		FlowGraphCommandExecuter cmdExecuter	) {
		this.delegate = new entity.flowgraph.FlowGraphDB();
		this.delegate.persist();
		this.cmdExecuter = cmdExecuter;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof info.scce.cinco.product.flowgraph.flowgraph.FlowGraph
			&& ((info.scce.cinco.product.flowgraph.flowgraph.FlowGraph) obj).getId().equals(getId());
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
	public entity.flowgraph.FlowGraphDB getDelegate() {
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
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.Transition> getTransitions() {
		return this.getModelElements(info.scce.cinco.product.flowgraph.flowgraph.Transition.class);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition> getLabeledTransitions() {
		return this.getModelElements(info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition.class);
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
	public info.scce.cinco.product.flowgraph.flowgraph.Start newStart(int x, int y, int width, int height) {
		info.scce.cinco.product.flowgraph.flowgraph.Start cn = cmdExecuter.createStart(x,y,new Long(width),new Long(height),this,null);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.Start newStart(int x, int y) {
			return this.newStart(x,y,36,
			36
			);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.Start> getStarts() {
		return getModelElements(info.scce.cinco.product.flowgraph.flowgraph.Start.class);
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.End newEnd(int x, int y, int width, int height) {
		info.scce.cinco.product.flowgraph.flowgraph.End cn = cmdExecuter.createEnd(x,y,new Long(width),new Long(height),this,null);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.End newEnd(int x, int y) {
			return this.newEnd(x,y,36,
			36
			);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.End> getEnds() {
		return getModelElements(info.scce.cinco.product.flowgraph.flowgraph.End.class);
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.Activity newActivity(int x, int y, int width, int height) {
		info.scce.cinco.product.flowgraph.flowgraph.Activity cn = cmdExecuter.createActivity(x,y,new Long(width),new Long(height),this,null);
		// postCreateHooks
		{
			info.scce.cinco.product.flowgraph.hooks.RandomActivityName hook = new info.scce.cinco.product.flowgraph.hooks.RandomActivityName();
			hook.init(cmdExecuter);
			hook.postCreate(cn);
		}
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.Activity newActivity(int x, int y) {
			return this.newActivity(x,y,96,
			32
			);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.Activity> getActivitys() {
		return getModelElements(info.scce.cinco.product.flowgraph.flowgraph.Activity.class);
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.EActivityA newEActivityA(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.flowgraph.flowgraph.EActivityA cn = cmdExecuter.createEActivityA(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.EActivityA newEActivityA(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.flowgraph.flowgraph.EActivityA cn = cmdExecuter.createEActivityA(
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
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.EActivityA> getEActivityAs() {
		return getModelElements(info.scce.cinco.product.flowgraph.flowgraph.EActivityA.class);
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.EActivityB newEActivityB(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.flowgraph.flowgraph.EActivityB cn = cmdExecuter.createEActivityB(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.EActivityB newEActivityB(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.flowgraph.flowgraph.EActivityB cn = cmdExecuter.createEActivityB(
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
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.EActivityB> getEActivityBs() {
		return getModelElements(info.scce.cinco.product.flowgraph.flowgraph.EActivityB.class);
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.ELibrary newELibrary(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.flowgraph.flowgraph.ELibrary cn = cmdExecuter.createELibrary(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.ELibrary newELibrary(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.flowgraph.flowgraph.ELibrary cn = cmdExecuter.createELibrary(
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
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.ELibrary> getELibrarys() {
		return getModelElements(info.scce.cinco.product.flowgraph.flowgraph.ELibrary.class);
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph newSubFlowGraph(
		long primeId,
		int x,
		int y
	) {
		info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph cn = cmdExecuter.createSubFlowGraph(x,y,
			96,
			32,
			this,
			null,
			primeId
		);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph newSubFlowGraph(
		long primeId,
		int x,
		int y,
		int width,
		int height
	) {
		info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph cn = cmdExecuter.createSubFlowGraph(
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
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph> getSubFlowGraphs() {
		return getModelElements(info.scce.cinco.product.flowgraph.flowgraph.SubFlowGraph.class);
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.Swimlane newSwimlane(int x, int y, int width, int height) {
		info.scce.cinco.product.flowgraph.flowgraph.Swimlane cn = cmdExecuter.createSwimlane(x,y,new Long(width),new Long(height),this,null);
		return cn;
	}
	
	@Override
	public info.scce.cinco.product.flowgraph.flowgraph.Swimlane newSwimlane(int x, int y) {
			return this.newSwimlane(x,y,400,
			100
			);
	}
	
	@Override
	public java.util.List<info.scce.cinco.product.flowgraph.flowgraph.Swimlane> getSwimlanes() {
		return getModelElements(info.scce.cinco.product.flowgraph.flowgraph.Swimlane.class);
	}
	
	@Override
	public String getModelName() {
		return this.delegate.modelName;
	}
	
	@Override
	public void setModelName(String attr) {
		info.scce.pyro.flowgraph.rest.FlowGraph prev = info.scce.pyro.flowgraph.rest.FlowGraph.fromEntityProperties(this.delegate,null);
		this.delegate.modelName = attr;
		
		// commandExecuter
		this.cmdExecuter.updateFlowGraphProperties(this,prev);
		
		// persist
		this.delegate.persist();
	}
}
