package info.scce.cinco.product.flowgraph.flowgraph;

public interface Swimlane extends graphmodel.Container {
	 
	public Swimlane getSwimlaneView();
	public graphmodel.GraphModel getRootElement();
	public graphmodel.ModelElementContainer getContainer();
	public Start newStart(int x, int y, int width, int height);
	public Start newStart(int x, int y);
	public java.util.List<Start> getStarts();
	public Activity newActivity(int x, int y, int width, int height);
	public Activity newActivity(int x, int y);
	public java.util.List<Activity> getActivitys();
	public End newEnd(int x, int y, int width, int height);
	public End newEnd(int x, int y);
	public java.util.List<End> getEnds();
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
	String getActor();
	void setActor(String actor);
}
