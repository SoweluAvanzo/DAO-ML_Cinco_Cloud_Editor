package graphmodel;

public interface PyroElement {
	public String getId();
	public long getDelegateId();
	public io.quarkus.hibernate.orm.panache.PanacheEntity getDelegate();
}
