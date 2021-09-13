package externallibrary.impl;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

public class ExternalActivityImpl implements externallibrary.ExternalActivity {
	
	private final entity.externallibrary.ExternalActivityDB delegate;
	
	public ExternalActivityImpl(
		entity.externallibrary.ExternalActivityDB delegate
	) {
		this.delegate = delegate;
	}
	
	public ExternalActivityImpl(
	) {
		this.delegate = new entity.externallibrary.ExternalActivityDB();
	}
	
	public externallibrary.ExternalActivity eClass() {
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof externallibrary.ExternalActivity
			&& ((externallibrary.ExternalActivity) obj).getId().equals(getId());
	}
	
	@Override
	public int hashCode() {
		return delegate.id.intValue();
	}
	
	@Override
	public String getId() {
		return Long.toString(this.delegate.id);
	}
	
	@Override
	public long getDelegateId() {
		return this.delegate.id;
	}
	
	@Override
	public entity.externallibrary.ExternalActivityDB getDelegate() {
		return this.delegate;
	}
	
	@Override
    public org.eclipse.emf.ecore.EObject eContainer() {
        return null;
    }
	
	@Override
	public void delete() {
		this.delegate.delete();
	}
	
	// EAttributes
	@Override
	public String getName() {
		return this.delegate.name;
	}
	
	@Override
	public void setName(String e) {
		this.delegate.name = e;
		this.delegate.persist();
	}
	
	@Override
	public String getDescription() {
		return this.delegate.description;
	}
	
	@Override
	public void setDescription(String e) {
		this.delegate.description = e;
		this.delegate.persist();
	}
	
	// EReferences
}
