package info.scce.cinco.product.flowgraph.flowgraph;

public interface Start extends graphmodel.Node {
	
	public FlowGraph getRootElement();
	public graphmodel.ModelElementContainer getContainer();
	java.util.List<Transition> getOutgoingTransitions();
	public Transition newTransition(End target);
	public Transition newTransition(Activity target);
	public Transition newTransition(EActivityA target);
	public Transition newTransition(EActivityB target);
	public Transition newTransition(ELibrary target);
	public Transition newTransition(SubFlowGraph target);
	java.util.List<End> getEndSuccessors();
	java.util.List<Activity> getActivitySuccessors();
	java.util.List<EActivityA> getEActivityASuccessors();
	java.util.List<EActivityB> getEActivityBSuccessors();
	java.util.List<ELibrary> getELibrarySuccessors();
	java.util.List<SubFlowGraph> getSubFlowGraphSuccessors();
}
