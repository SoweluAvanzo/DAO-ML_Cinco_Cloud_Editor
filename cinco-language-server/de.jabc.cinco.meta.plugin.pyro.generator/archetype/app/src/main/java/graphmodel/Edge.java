package graphmodel;

public interface Edge extends ModelElement {
	public void delete();
	public Node getSourceElement();
	public Node getTargetElement();
	public void reconnectSource(Node node);
	public void reconnectTarget(Node node);
	public void addBendingPoint(long x, long y);
	public void clearBendingPoints();
	public java.util.List<? extends graphmodel.BendingPoint> getBendingPoints();
}