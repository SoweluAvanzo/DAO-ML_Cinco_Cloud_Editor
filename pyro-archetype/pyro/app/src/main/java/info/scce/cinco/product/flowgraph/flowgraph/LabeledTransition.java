package info.scce.cinco.product.flowgraph.flowgraph;

public interface LabeledTransition extends graphmodel.Edge {
	 
	public LabeledTransition getLabeledTransitionView();
	public graphmodel.GraphModel getRootElement();
	public graphmodel.ModelElementContainer getContainer();
	String getLabel();
	void setLabel(String label);
}
