package info.scce.cinco.product.flowgraph.flowgraph;

public interface End extends graphmodel.Node {
	 
	public End getEndView();
	public graphmodel.GraphModel getRootElement();
	public graphmodel.ModelElementContainer getContainer();
	java.util.List<Transition> getIncomingTransitions();
	java.util.List<LabeledTransition> getIncomingLabeledTransitions();
	java.util.List<Start> getStartPredecessors();
	java.util.List<SubFlowGraph> getSubFlowGraphPredecessors();
	java.util.List<Activity> getActivityPredecessors();
	java.util.List<ExternalActivity> getExternalActivityPredecessors();
}
