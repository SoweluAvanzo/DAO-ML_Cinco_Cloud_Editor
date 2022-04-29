package graphmodel;

public interface Node extends ModelElement {
	
	public void delete();
	public int getX();
	public void setX(int x);
	public int getY();
	public void setY(int y);
	public int getWidth();
	public void setWidth(int width);
	public int getHeight();
	public void setHeight(int height);


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
