package info.scce.cinco.product.flowgraph.flowgraph;

public interface Transition extends graphmodel.Edge {
	 
	public Transition getTransitionView();
	public graphmodel.GraphModel getRootElement();
	public graphmodel.ModelElementContainer getContainer();
}
