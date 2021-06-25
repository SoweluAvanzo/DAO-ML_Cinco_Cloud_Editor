package graphmodel;

public interface Node extends ModelElement {
	
	public void delete();
	public int getX();
	public int getY();
	public int getWidth();
	public int getHeight();

	public java.util.List<Edge> getIncoming();
	public <T extends Edge> java.util.List<T> getIncoming(Class<T> clazz);
	public java.util.List<Node> getPredecessors();
	public <T extends Node> java.util.List<T> getPredecessors(Class<T> clazz);

	public java.util.List<Edge> getOutgoing();
	public <T extends Edge> java.util.List<T> getOutgoing(Class<T> clazz);
	public java.util.List<Node> getSuccessors();
	public <T extends Node> java.util.List<T> getSuccessors(Class<T> clazz);

	public void move(int x,int y);
	public void moveTo(ModelElementContainer container,int x,int y);
	public void resize(int width,int height);
}
