package info.scce.cinco.product.flowgraph.flowgraph;

public interface Activity extends graphmodel.Node {
	 
	public Activity getActivityView();
	public graphmodel.GraphModel getRootElement();
	public graphmodel.ModelElementContainer getContainer();
	java.util.List<Transition> getIncomingTransitions();
	java.util.List<LabeledTransition> getIncomingLabeledTransitions();
	java.util.List<Start> getStartPredecessors();
	java.util.List<SubFlowGraph> getSubFlowGraphPredecessors();
	java.util.List<Activity> getActivityPredecessors();
	java.util.List<ExternalActivity> getExternalActivityPredecessors();
	java.util.List<LabeledTransition> getOutgoingLabeledTransitions();
	public LabeledTransition newLabeledTransition(End target);
	public LabeledTransition newLabeledTransition(SubFlowGraph target);
	public LabeledTransition newLabeledTransition(Activity target);
	public LabeledTransition newLabeledTransition(ExternalActivity target);
	java.util.List<End> getEndSuccessors();
	java.util.List<SubFlowGraph> getSubFlowGraphSuccessors();
	java.util.List<Activity> getActivitySuccessors();
	java.util.List<ExternalActivity> getExternalActivitySuccessors();
	String getName();
	void setName(String name);
	String getDescription();
	void setDescription(String description);
}
