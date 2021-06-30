package graphmodel;

public interface ModelElementContainer extends IdentifiableElement {
	public java.util.List<ModelElement> getModelElements();
	public <T extends ModelElement> java.util.List<T> getModelElements(Class<T> clazz);
	public java.util.List<Node> getAllNodes();
	public java.util.List<Edge> getAllEdges();
	public java.util.List<Container> getAllContainers();
	<T extends Edge> java.util.List<T> getEdges(Class<T> clazz);
	<T extends Node> java.util.List<T> getNodes(Class<T> clazz);
	<T extends Node> java.util.List<T> getNodes();
	public void delete();
}
