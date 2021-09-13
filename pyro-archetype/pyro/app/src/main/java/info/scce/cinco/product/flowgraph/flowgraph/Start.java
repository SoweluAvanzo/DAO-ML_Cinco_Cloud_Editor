package info.scce.cinco.product.flowgraph.flowgraph;

public interface Start extends graphmodel.Node {
	 
	public Start getStartView();
	public graphmodel.GraphModel getRootElement();
	public graphmodel.ModelElementContainer getContainer();
	java.util.List<Transition> getOutgoingTransitions();
	public Transition newTransition(End target);
	public Transition newTransition(SubFlowGraph target);
	public Transition newTransition(Activity target);
	public Transition newTransition(ExternalActivity target);
	java.util.List<End> getEndSuccessors();
	java.util.List<SubFlowGraph> getSubFlowGraphSuccessors();
	java.util.List<Activity> getActivitySuccessors();
	java.util.List<ExternalActivity> getExternalActivitySuccessors();
}
