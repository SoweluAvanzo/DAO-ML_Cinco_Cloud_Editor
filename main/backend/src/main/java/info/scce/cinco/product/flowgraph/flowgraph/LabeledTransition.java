package info.scce.cinco.product.flowgraph.flowgraph;

public interface LabeledTransition extends graphmodel.Edge {
	
	public FlowGraph getRootElement();
	public info.scce.cinco.product.flowgraph.flowgraph.FlowGraph getContainer();
	String getLabel();
	void setLabel(String label);
}
