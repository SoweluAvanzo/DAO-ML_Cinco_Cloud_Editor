package info.scce.cinco.product.flowgraph.flowgraph;

public interface EActivityB extends graphmodel.Node {
	
	public FlowGraph getRootElement();
	public graphmodel.ModelElementContainer getContainer();
	public externallibrary.ExternalActivityD getActivityD();
	java.util.List<Transition> getIncomingTransitions();
	java.util.List<LabeledTransition> getIncomingLabeledTransitions();
	java.util.List<Start> getStartPredecessors();
	java.util.List<Activity> getActivityPredecessors();
	java.util.List<EActivityA> getEActivityAPredecessors();
	java.util.List<EActivityB> getEActivityBPredecessors();
	java.util.List<ELibrary> getELibraryPredecessors();
	java.util.List<SubFlowGraph> getSubFlowGraphPredecessors();
	java.util.List<LabeledTransition> getOutgoingLabeledTransitions();
	public LabeledTransition newLabeledTransition(End target);
	public LabeledTransition newLabeledTransition(Activity target);
	public LabeledTransition newLabeledTransition(EActivityA target);
	public LabeledTransition newLabeledTransition(EActivityB target);
	public LabeledTransition newLabeledTransition(ELibrary target);
	public LabeledTransition newLabeledTransition(SubFlowGraph target);
	java.util.List<End> getEndSuccessors();
	java.util.List<Activity> getActivitySuccessors();
	java.util.List<EActivityA> getEActivityASuccessors();
	java.util.List<EActivityB> getEActivityBSuccessors();
	java.util.List<ELibrary> getELibrarySuccessors();
	java.util.List<SubFlowGraph> getSubFlowGraphSuccessors();
}
