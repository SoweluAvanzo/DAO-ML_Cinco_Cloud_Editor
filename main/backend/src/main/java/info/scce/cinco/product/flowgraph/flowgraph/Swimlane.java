package info.scce.cinco.product.flowgraph.flowgraph;

public interface Swimlane extends graphmodel.Container {
	
	public FlowGraph getRootElement();
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
	public EActivityA newEActivityA(
		long primeId,
		int x,
		int y
	);
	public EActivityA newEActivityA(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<EActivityA> getEActivityAs();
	public EActivityB newEActivityB(
		long primeId,
		int x,
		int y
	);
	public EActivityB newEActivityB(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<EActivityB> getEActivityBs();
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
	public Swimlane newSwimlane(int x, int y, int width, int height);
	public Swimlane newSwimlane(int x, int y);
	public java.util.List<Swimlane> getSwimlanes();
	String getActor();
	void setActor(String actor);
}
