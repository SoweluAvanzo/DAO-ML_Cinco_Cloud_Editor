package info.scce.cinco.product.flowgraph.flowgraph;

public interface End extends graphmodel.Node {
	
	public FlowGraph getRootElement();
	public graphmodel.ModelElementContainer getContainer();
	java.util.List<Transition> getIncomingTransitions();
	java.util.List<LabeledTransition> getIncomingLabeledTransitions();
	java.util.List<Start> getStartPredecessors();
	java.util.List<Activity> getActivityPredecessors();
	java.util.List<EActivityA> getEActivityAPredecessors();
	java.util.List<EActivityB> getEActivityBPredecessors();
	java.util.List<ELibrary> getELibraryPredecessors();
	java.util.List<SubFlowGraph> getSubFlowGraphPredecessors();
}
