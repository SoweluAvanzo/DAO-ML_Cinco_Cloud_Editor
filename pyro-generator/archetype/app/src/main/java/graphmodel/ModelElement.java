package graphmodel;

public interface ModelElement extends IdentifiableElement {
	ModelElementContainer getContainer();
	GraphModel getRootElement();
}
