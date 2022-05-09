package graphmodel;


public interface IdentifiableElement extends PyroElement {
	public IdentifiableElement eClass();
	public String getName();
	boolean isTransient();
}
