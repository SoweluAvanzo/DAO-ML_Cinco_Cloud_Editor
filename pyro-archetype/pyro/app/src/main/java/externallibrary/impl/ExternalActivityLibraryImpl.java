package externallibrary.impl;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

public class ExternalActivityLibraryImpl implements externallibrary.ExternalActivityLibrary {
	
	private final entity.externallibrary.ExternalActivityLibraryDB delegate;
	
	public ExternalActivityLibraryImpl(
		entity.externallibrary.ExternalActivityLibraryDB delegate
	) {
		this.delegate = delegate;
	}
	
	public ExternalActivityLibraryImpl(
	) {
		this.delegate = new entity.externallibrary.ExternalActivityLibraryDB();
	}
	
	public externallibrary.ExternalActivityLibrary eClass() {
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof externallibrary.ExternalActivityLibrary
			&& ((externallibrary.ExternalActivityLibrary) obj).getId().equals(getId());
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
	public entity.externallibrary.ExternalActivityLibraryDB getDelegate() {
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
	public String getName() {
		return "ExternalActivityLibrary";
	}
	
	// EReferences
	@Override
	public java.util.List<externallibrary.ExternalActivity> getActivities() {
		java.util.Collection<PanacheEntity> entityList = this.delegate.getActivities();
		java.util.List<externallibrary.ExternalActivity> apiList = entityList.stream().map(n->
			new externallibrary.impl.ExternalActivityImpl(
				(entity.externallibrary.ExternalActivityDB) n
			)
		).collect(java.util.stream.Collectors.toList());
		return apiList;
	}
	
	@Override
	public void setActivities(java.util.Collection<externallibrary.ExternalActivity> e) {
		java.util.Collection<PanacheEntity> entityList = e.stream().map(n->
			n.getDelegate()
		).collect(java.util.stream.Collectors.toList());
		this.delegate.setActivities(entityList);
		this.delegate.persist();
	}
	
	@Override
	public boolean removeActivities(externallibrary.ExternalActivity apiElement, boolean delete) {
		PanacheEntity dbElement = apiElement.getDelegate();
		return ((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).removeActivities(dbElement, delete);
	}
	
	@Override
	public boolean removeActivities(externallibrary.ExternalActivity apiElement) {
		return removeActivities(apiElement, true);
	}
	
	@Override
	public void clearActivities() {
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).clearActivities();
	}
	
	@Override
	public void clearActivities(boolean delete) {
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).clearActivities(delete);
	}
	
	@Override
	public void addAllActivities(java.util.Collection<externallibrary.ExternalActivity> apiElements) {
		for(externallibrary.ExternalActivity apiElement : apiElements) {
			addActivities(apiElement);
		}
	}
	
	@Override
	public void addActivities(externallibrary.ExternalActivity apiElement) {
		PanacheEntity dbElement = apiElement.getDelegate();
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).addActivities(dbElement);
	}
}
