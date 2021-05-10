package info.scce.cinco.product.flowgraph.flowgraph;

public interface Transition extends graphmodel.Edge {
	
	public FlowGraph getRootElement();
	public info.scce.cinco.product.flowgraph.flowgraph.FlowGraph getContainer();
}
