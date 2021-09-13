package info.scce.cinco.product.flowgraph.flowgraph;

public interface FlowGraphDiagram extends graphmodel.GraphModel {
	 
	public FlowGraphDiagram getFlowGraphDiagramView();
	public String getRouter();
	public String getConnector();
	public long getWidth();
	public long getHeight();
	public double getScale();
	public String getFileName();
	public String getExtension();
	public java.util.List<Transition> getTransitions();
	public java.util.List<LabeledTransition> getLabeledTransitions();
	public Start newStart(int x, int y, int width, int height);
	public Start newStart(int x, int y);
	public java.util.List<Start> getStarts();
	public End newEnd(int x, int y, int width, int height);
	public End newEnd(int x, int y);
	public java.util.List<End> getEnds();
	public Activity newActivity(int x, int y, int width, int height);
	public Activity newActivity(int x, int y);
	public java.util.List<Activity> getActivitys();
	public Swimlane newSwimlane(int x, int y, int width, int height);
	public Swimlane newSwimlane(int x, int y);
	public java.util.List<Swimlane> getSwimlanes();
	public SubFlowGraph newSubFlowGraph(
		long primeId,
		int x,
		int y
	);
	public SubFlowGraph newSubFlowGraph(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<SubFlowGraph> getSubFlowGraphs();
	String getModelName();
	void setModelName(String modelname);
}
